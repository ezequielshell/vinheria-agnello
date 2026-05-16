package com.vinheria.agnello.controller;

import com.vinheria.agnello.dao.VinhoDAO;
import com.vinheria.agnello.model.Vinho;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/adega")
public class AdegaServlet extends HttpServlet {

    private final VinhoDAO vinhoDAO = new VinhoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String busca = request.getParameter("busca");
        String tipo = request.getParameter("tipo");

        List<Vinho> vinhos;
        if (busca != null && !busca.trim().isEmpty()) {
            vinhos = vinhoDAO.buscar(busca);
            request.setAttribute("termoBusca", busca);
        } else if (tipo != null && !tipo.trim().isEmpty()) {
            vinhos = vinhoDAO.filtrarPorTipo(tipo);
            request.setAttribute("filtroTipo", tipo);
        } else {
            vinhos = vinhoDAO.listarTodos();
        }

        request.setAttribute("vinhos", vinhos);
        request.setAttribute("totalResultados", vinhos.size());
        request.getRequestDispatcher("/adega.jsp").forward(request, response);
    }
}
