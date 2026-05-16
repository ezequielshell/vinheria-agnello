package com.vinheria.agnello.controller;

import com.vinheria.agnello.dao.VinhoDAO;
import com.vinheria.agnello.model.Vinho;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/detalhe")
public class DetalheServlet extends HttpServlet {

    private final VinhoDAO vinhoDAO = new VinhoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            response.sendRedirect("adega");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Optional<Vinho> opt = vinhoDAO.buscarPorId(id);
            if (opt.isEmpty()) {
                request.setAttribute("erro", "Vinho não encontrado no catálogo.");
                request.getRequestDispatcher("/adega.jsp").forward(request, response);
                return;
            }
            request.setAttribute("vinho", opt.get());
            request.getRequestDispatcher("/detalhe.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("adega");
        }
    }
}
