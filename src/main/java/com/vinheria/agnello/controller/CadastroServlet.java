package com.vinheria.agnello.controller;

import com.vinheria.agnello.dao.FavoritoDAO;
import com.vinheria.agnello.model.Usuario;
import com.vinheria.agnello.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final AuthService auth = new AuthService();
    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String confirma = request.getParameter("confirmarSenha");
        String telefone = request.getParameter("telefone");
        String cpf = request.getParameter("cpf");

        if (senha == null || !senha.equals(confirma)) {
            request.setAttribute("erro", "As senhas não coincidem.");
            preserveInputs(request, nome, email, telefone, cpf);
            request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
            return;
        }

        try {
            Usuario u = auth.cadastrar(nome, email, senha, telefone, cpf);
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", u);
            session.setAttribute("logado", true);
            session.setAttribute("favoritosIds", favoritoDAO.idsDoUsuario(u.getId()));
            session.setAttribute("flash", "Bem-vindo à Vinheria, " + u.getPrimeiroNome() + "!");
            response.sendRedirect("home");
        } catch (AuthService.AuthException e) {
            request.setAttribute("erro", e.getMessage());
            preserveInputs(request, nome, email, telefone, cpf);
            request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
        }
    }

    private void preserveInputs(HttpServletRequest req, String nome, String email,
                                String telefone, String cpf) {
        req.setAttribute("nomePrefill", nome);
        req.setAttribute("emailPrefill", email);
        req.setAttribute("telefonePrefill", telefone);
        req.setAttribute("cpfPrefill", cpf);
    }
}
