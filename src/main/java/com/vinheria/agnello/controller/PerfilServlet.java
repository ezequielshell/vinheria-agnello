package com.vinheria.agnello.controller;

import com.vinheria.agnello.dao.EnderecoDAO;
import com.vinheria.agnello.dao.UsuarioDAO;
import com.vinheria.agnello.model.Endereco;
import com.vinheria.agnello.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/perfil")
public class PerfilServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EnderecoDAO enderecoDAO = new EnderecoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario u = (Usuario) request.getSession().getAttribute("usuario");
        if (u == null) {
            response.sendRedirect("login?redirect=perfil");
            return;
        }
        request.setAttribute("enderecos", enderecoDAO.listarDoUsuario(u.getId()));
        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario u = (Usuario) session.getAttribute("usuario");
        if (u == null) {
            response.sendRedirect("login?redirect=perfil");
            return;
        }

        String acao = request.getParameter("acao");
        try {
            switch (String.valueOf(acao)) {
                case "atualizarPerfil" -> {
                    u.setNome(request.getParameter("nome"));
                    u.setTelefone(request.getParameter("telefone"));
                    u.setCpf(request.getParameter("cpf"));
                    usuarioDAO.atualizar(u);
                    session.setAttribute("usuario", u);
                    session.setAttribute("flash", "Dados atualizados.");
                }
                case "novoEndereco" -> {
                    Endereco e = new Endereco();
                    e.setUsuarioId(u.getId());
                    e.setCep(request.getParameter("cep"));
                    e.setLogradouro(request.getParameter("logradouro"));
                    e.setNumero(request.getParameter("numero"));
                    e.setComplemento(request.getParameter("complemento"));
                    e.setBairro(request.getParameter("bairro"));
                    e.setCidade(request.getParameter("cidade"));
                    e.setUf(request.getParameter("uf"));
                    e.setPrincipal("on".equals(request.getParameter("principal")));
                    enderecoDAO.inserir(e);
                    session.setAttribute("flash", "Endereço adicionado.");
                }
                case "removerEndereco" -> {
                    long id = Long.parseLong(request.getParameter("enderecoId"));
                    enderecoDAO.remover(id, u.getId());
                    session.setAttribute("flash", "Endereço removido.");
                }
                default -> { /* ignore */ }
            }
        } catch (RuntimeException e) {
            session.setAttribute("flash", "Erro: " + e.getMessage());
        }
        response.sendRedirect("perfil");
    }
}
