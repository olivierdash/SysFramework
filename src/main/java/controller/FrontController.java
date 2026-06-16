package main.java.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String url = req.getRequestURL().toString();
        String path = req.getServletPath();

        // Aiguillage des routes personnalisées
        if (path.equals("/") || path.contains("page1")) {
            req.setAttribute("url", url);
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        } else if (path.contains("page2")) {
            req.setAttribute("url", url);
            req.getRequestDispatcher("/Accueil.jsp").forward(req, resp);
        } else {
            // Si l'utilisateur demande directement une JSP (ex: /index.jsp ou /Accueil.jsp)
            // on laisse Tomcat l'afficher normalement
            if (path.endsWith(".jsp")) {
                req.getRequestDispatcher(path).forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}