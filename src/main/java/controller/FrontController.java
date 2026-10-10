package main.java.controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import main.java.constant.HttpMethod;
import main.java.outils.Mapping;
import main.java.outils.UrlMethod;

public class FrontController extends HttpServlet {
    private Map<UrlMethod, Mapping> mappingUrls;

    @Override
    public void init() throws ServletException {
        this.mappingUrls = (Map<UrlMethod, Mapping>) getServletContext().getAttribute("mappingUrls");
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        HttpMethod methodeHttp = HttpMethod.valueOf(req.getMethod().toUpperCase());

        UrlMethod cle = new UrlMethod();
        cle.setUrl(path);
        cle.setMethod(methodeHttp);

        Mapping mapping = (mappingUrls != null) ? mappingUrls.get(cle) : null;

        if (mapping == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            Object controllerInstance = mapping.getControllerClass().getDeclaredConstructor().newInstance();
            Object resultat = mapping.getMethod().invoke(controllerInstance);

            String vue = (resultat != null) ? resultat.toString() : "/index.jsp";
            req.setAttribute("url", req.getRequestURL().toString());
            req.getRequestDispatcher(vue).forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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