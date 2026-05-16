package com.vinheria.agnello.controller;

import com.vinheria.agnello.dao.PedidoDAO;
import com.vinheria.agnello.model.Pedido;
import com.vinheria.agnello.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet({"/pedido", "/pedidos"})
public class PedidoServlet extends HttpServlet {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario u = (Usuario) request.getSession().getAttribute("usuario");
        if (u == null) {
            response.sendRedirect("login?redirect=pedidos");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            request.setAttribute("pedidos", pedidoDAO.listarDoUsuario(u.getId()));
            request.getRequestDispatcher("/pedidos.jsp").forward(request, response);
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            Optional<Pedido> opt = pedidoDAO.buscarComItens(id, u.getId());
            if (opt.isEmpty()) {
                response.sendRedirect("pedidos");
                return;
            }
            request.setAttribute("pedido", opt.get());
            request.getRequestDispatcher("/pedido-detalhe.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("pedidos");
        }
    }
}
