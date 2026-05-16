package com.vinheria.agnello.controller;

import com.google.gson.Gson;
import com.vinheria.agnello.dao.FavoritoDAO;
import com.vinheria.agnello.model.Usuario;
import com.vinheria.agnello.model.Vinho;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/favoritos")
public class FavoritoServlet extends HttpServlet {

    private final FavoritoDAO favoritoDAO = new FavoritoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        List<Vinho> favoritos = usuario != null
            ? favoritoDAO.listarVinhosFavoritos(usuario.getId())
            : List.of();
        request.setAttribute("favoritos", favoritos);
        request.getRequestDispatcher("/favoritos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        boolean ajax = isAjax(request);

        if (usuario == null) {
            if (ajax) {
                writeJson(response, 401, Map.of(
                    "ok", false,
                    "loginRequired", true,
                    "redirect", "login?redirect=favoritos"
                ));
            } else {
                response.sendRedirect("login?redirect=favoritos");
            }
            return;
        }

        String idParam = request.getParameter("vinhoId");
        String acao = request.getParameter("acao");
        boolean favoritoAgora = false;

        if (idParam != null) {
            int vinhoId = Integer.parseInt(idParam);
            if ("remover".equals(acao)) {
                favoritoDAO.remover(usuario.getId(), vinhoId);
                favoritoAgora = false;
            } else {
                favoritoDAO.adicionar(usuario.getId(), vinhoId);
                favoritoAgora = true;
            }

            Set<Integer> ids = new HashSet<>(favoritoDAO.idsDoUsuario(usuario.getId()));
            session.setAttribute("favoritosIds", ids);
        }

        if (ajax) {
            Map<String, Object> body = new HashMap<>();
            body.put("ok", true);
            body.put("favorito", favoritoAgora);
            body.put("vinhoId", idParam == null ? null : Integer.valueOf(idParam));
            writeJson(response, 200, body);
            return;
        }

        String origem = request.getParameter("origem");
        response.sendRedirect((origem == null || origem.isBlank()) ? "favoritos" : origem);
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
