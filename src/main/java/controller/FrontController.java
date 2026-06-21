package main.java.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private List<String> listClass;

    // Dans FrontController.java — remplacer le init() vide par :
    @Override
    public void init() throws ServletException {
        List<String> listFromContext = (List<String>) getServletContext().getAttribute("listClass");
        this.setListClass(listFromContext);
    }

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

    public List<String> getListClass() {
        return listClass;
    }

    public void setListClass(List<String> listClass) {
        this.listClass = listClass;
    }
}