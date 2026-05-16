package com.vinheria.agnello.controller;

import com.google.gson.Gson;
import com.vinheria.agnello.dao.CarrinhoDAO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/carrinho")
public class CarrinhoServlet extends HttpServlet {

    private final CarrinhoDAO carrinhoDAO = new CarrinhoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario u = (Usuario) request.getSession().getAttribute("usuario");
        if (u == null) {
            response.sendRedirect("login?redirect=carrinho");
            return;
        }

        List<ItemCarrinho> itens = carrinhoDAO.listar(u.getId());
        BigDecimal subtotal = itens.stream()
            .map(ItemCarrinho::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal frete = subtotal.compareTo(BigDecimal.valueOf(500)) >= 0
            ? BigDecimal.ZERO : BigDecimal.valueOf(29.90);

        request.setAttribute("itens", itens);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("frete", frete);
        request.setAttribute("total", subtotal.add(frete));
        request.getRequestDispatcher("/carrinho.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario u = (Usuario) session.getAttribute("usuario");
        boolean ajax = isAjax(request);

        if (u == null) {
            if (ajax) {
                writeJson(response, 401, Map.of(
                    "ok", false,
                    "loginRequired", true,
                    "redirect", "login?redirect=carrinho"
                ));
            } else {
                String back = request.getParameter("origem");
                response.sendRedirect("login?redirect=" +
                    (back == null || back.isBlank() ? "carrinho" : back));
            }
            return;
        }

        String acao = request.getParameter("acao");
        String idStr = request.getParameter("vinhoId");

        try {
            int vinhoId = idStr == null ? 0 : Integer.parseInt(idStr);
            switch (String.valueOf(acao)) {
                case "adicionar" -> {
                    int qtd = parseQtd(request.getParameter("quantidade"), 1);
                    carrinhoDAO.adicionar(u.getId(), vinhoId, qtd);
                    if (!ajax) session.setAttribute("flash", "Vinho adicionado ao carrinho.");
                }
                case "atualizar" -> {
                    int qtd = parseQtd(request.getParameter("quantidade"), 1);
                    carrinhoDAO.definirQuantidade(u.getId(), vinhoId, qtd);
                }
                case "remover" -> carrinhoDAO.remover(u.getId(), vinhoId);
                case "limpar"  -> carrinhoDAO.limpar(u.getId());
                default -> { /* no-op */ }
            }

            session.setAttribute("totalCarrinho", carrinhoDAO.contar(u.getId()));
        } catch (NumberFormatException ignored) {}

        if (ajax) {
            Map<String, Object> body = new HashMap<>();
            body.put("ok", true);
            body.put("acao", acao);
            body.put("totalCarrinho", session.getAttribute("totalCarrinho"));
            writeJson(response, 200, body);
            return;
        }

        String origem = request.getParameter("origem");
        response.sendRedirect((origem == null || origem.isBlank()) ? "carrinho" : origem);
    }

    private int parseQtd(String s, int def) {
        if (s == null) return def;
        try { return Math.max(1, Math.min(99, Integer.parseInt(s.trim()))); }
        catch (NumberFormatException e) { return def; }
    }

    private boolean isAjax(HttpServletRequest req) {
        String xrw = req.getHeader("X-Requested-With");
        String accept = req.getHeader("Accept");
        return "fetch".equalsIgnoreCase(xrw)
            || "XMLHttpRequest".equalsIgnoreCase(xrw)
            || (accept != null && accept.contains("application/json"));
    }

    private void writeJson(HttpServletResponse response, int status, Map<String, Object> body)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(gson.toJson(body));
    }
}
