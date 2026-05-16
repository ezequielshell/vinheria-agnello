package com.vinheria.agnello.controller;

import com.vinheria.agnello.dao.CarrinhoDAO;
import com.vinheria.agnello.dao.EnderecoDAO;
import com.vinheria.agnello.dao.PedidoDAO;
import com.vinheria.agnello.model.Endereco;
import com.vinheria.agnello.model.ItemCarrinho;
import com.vinheria.agnello.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final EnderecoDAO enderecoDAO = new EnderecoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario u = (Usuario) request.getSession().getAttribute("usuario");
        if (u == null) {
            response.sendRedirect("login?redirect=checkout");
            return;
        }
        List<ItemCarrinho> itens = carrinhoDAO.listar(u.getId());
        if (itens.isEmpty()) {
            response.sendRedirect("carrinho");
            return;
        }
        BigDecimal subtotal = itens.stream()
            .map(ItemCarrinho::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal frete = subtotal.compareTo(BigDecimal.valueOf(500)) >= 0
            ? BigDecimal.ZERO : BigDecimal.valueOf(29.90);

        request.setAttribute("itens", itens);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("frete", frete);
        request.setAttribute("total", subtotal.add(frete));
        request.setAttribute("enderecos", enderecoDAO.listarDoUsuario(u.getId()));
        request.getRequestDispatcher("/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario u = (Usuario) session.getAttribute("usuario");
        if (u == null) {
            response.sendRedirect("login?redirect=checkout");
            return;
        }

        List<ItemCarrinho> itens = carrinhoDAO.listar(u.getId());
        if (itens.isEmpty()) {
            response.sendRedirect("carrinho");
            return;
        }

        String formaPagamento = request.getParameter("formaPagamento");
        String enderecoIdStr = request.getParameter("enderecoId");
        String novoEnderecoFlag = request.getParameter("novoEndereco");

        String enderecoFormatado;
        if ("on".equals(novoEnderecoFlag) || enderecoIdStr == null || enderecoIdStr.isBlank()) {
            Endereco novo = new Endereco();
            novo.setUsuarioId(u.getId());
            novo.setCep(request.getParameter("cep"));
            novo.setLogradouro(request.getParameter("logradouro"));
            novo.setNumero(request.getParameter("numero"));
            novo.setComplemento(request.getParameter("complemento"));
            novo.setBairro(request.getParameter("bairro"));
            novo.setCidade(request.getParameter("cidade"));
            novo.setUf(request.getParameter("uf"));
            novo.setPrincipal("on".equals(request.getParameter("principal")));
            enderecoDAO.inserir(novo);
            enderecoFormatado = novo.formatado();
        } else {
            long id = Long.parseLong(enderecoIdStr);
            Optional<Endereco> opt = enderecoDAO.listarDoUsuario(u.getId()).stream()
                .filter(e -> e.getId() == id).findFirst();
            if (opt.isEmpty()) {
                response.sendRedirect("checkout");
                return;
            }
            enderecoFormatado = opt.get().formatado();
        }

        long pedidoId = pedidoDAO.criar(u.getId(), itens,
            formaPagamento == null ? "PIX" : formaPagamento,
            enderecoFormatado);

        session.setAttribute("totalCarrinho", 0);
        session.setAttribute("flash", "Pedido #" + pedidoId + " confirmado!");
        response.sendRedirect("pedido?id=" + pedidoId);
    }
}
