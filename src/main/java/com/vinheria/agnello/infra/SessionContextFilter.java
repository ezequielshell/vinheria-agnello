package com.vinheria.agnello.infra;

import com.vinheria.agnello.dao.CarrinhoDAO;
import com.vinheria.agnello.model.Usuario;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(urlPatterns = "/*")
public class SessionContextFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(SessionContextFilter.class.getName());
    private final CarrinhoDAO carrinhoDAO = new CarrinhoDAO();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) req;
        HttpSession session = http.getSession(false);

        if (session != null) {
            Usuario u = (Usuario) session.getAttribute("usuario");
            if (u != null && session.getAttribute("totalCarrinho") == null) {
                try {
                    session.setAttribute("totalCarrinho", carrinhoDAO.contar(u.getId()));
                } catch (RuntimeException e) {
                    LOG.log(Level.FINE, "Não foi possível atualizar totalCarrinho", e);
                }
            }
        }
        chain.doFilter(req, res);
    }
}
