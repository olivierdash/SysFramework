package main.java.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import main.java.annotation.UrlMapping;
import main.java.constant.HttpMethod;
import main.java.outils.Mapping;
import main.java.outils.UrlMethod;

public class FrontController extends HttpServlet {
    private List<String> listClass;
    private Map<UrlMethod, Mapping> mappings = new HashMap<>();

    @Override
    public void init() throws ServletException {
        List<String> listFromContext = (List<String>) getServletContext().getAttribute("listClass");
        this.setListClass(listFromContext);
        chargerMappings();
    }

    // Parcourt les classes trouvées par le Listener et construit la table des
    // routes
    private void chargerMappings() {
        if (listClass == null) {
            return;
        }
        for (String className : listClass) {
            try {
                Class<?> classe = Class.forName(className);
                for (Method methode : classe.getDeclaredMethods()) {
                    if (methode.isAnnotationPresent(UrlMapping.class)) {
                        UrlMapping urlMapping = methode.getAnnotation(UrlMapping.class);
                        UrlMethod cle = new UrlMethod(urlMapping);
                        Mapping mapping = new Mapping(classe, methode);
                        mappings.put(cle, mapping);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        HttpMethod methodeHttp = HttpMethod.valueOf(req.getMethod().toUpperCase());

        UrlMethod cle = new UrlMethod();
        cle.setUrl(path);
        cle.setMethod(methodeHttp);

        Mapping mapping = mappings.get(cle);

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

    public List<String> getListClass() {
        return listClass;
    }

    public void setListClass(List<String> listClass) {
        this.listClass = listClass;
    }
}