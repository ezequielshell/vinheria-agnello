package com.vinheria.agnello.controller;

import com.vinheria.agnello.dao.VinhoDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/home", ""})
public class HomeServlet extends HttpServlet {

    private final VinhoDAO vinhoDAO = new VinhoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("vinhoDestaque", vinhoDAO.getDestaque());
        request.setAttribute("vinhos", vinhoDAO.listarTodos());
        request.setAttribute("totalVinhos", vinhoDAO.contarTotal());
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
