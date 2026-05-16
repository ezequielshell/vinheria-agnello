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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService auth = new AuthService();
    private final FavoritoDAO favoritoDAO = new FavoritoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("logout".equals(request.getParameter("acao"))) {
            request.getSession().invalidate();
            response.sendRedirect("home");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try {
            Usuario u = auth.autenticar(email, senha);
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", u);
            session.setAttribute("logado", true);
            session.setAttribute("favoritosIds", favoritoDAO.idsDoUsuario(u.getId()));

            String redirect = request.getParameter("redirect");
            response.sendRedirect(redirect == null || redirect.isBlank() ? "home" : redirect);
        } catch (AuthService.AuthException e) {
            request.setAttribute("erro", e.getMessage());
            request.setAttribute("emailPrefill", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
