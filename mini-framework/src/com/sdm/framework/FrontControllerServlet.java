package com.sdm.framework;

import com.sdm.framework.outils.MapFile;
import com.sdm.framework.outils.Mapping;
import com.sdm.framework.outils.ModelAndView;
import com.sdm.framework.outils.MySession;
import com.sdm.framework.outils.UrlMethod;
import com.sdm.framework.annotation.ApiRest;
import com.sdm.framework.annotation.Auth;
import com.sdm.framework.constant.HttpMethod;
import com.google.gson.Gson;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import java.lang.reflect.Parameter;
import java.lang.reflect.Modifier;

@MultipartConfig
public class FrontControllerServlet extends HttpServlet {

    private String variableSesion;
    private String variableRole;
    private Map<UrlMethod, Mapping> mappingUrls = new HashMap<>();
    private Map<Class<?>, Object> controllers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        super.init();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("framework.properties")) {
            if (in == null) {
                throw new ServletException("framework.properties introuvable dans le classpath");
            }
            Properties properties = new Properties();
            properties.load(in);
            this.variableSesion = properties.getProperty("auth.session.variable");
            this.variableRole = properties.getProperty("auth.role.variable");
            if (this.variableSesion == null) {
                throw new ServletException("la clé auth.session.variable est absente de framework.properties");
            }
        } catch (IOException e) {
            throw new ServletException("impossible d'acceder a la variable de session", e);
        }
        Map<UrlMethod, Mapping> m = (Map<UrlMethod, Mapping>) getServletContext().getAttribute("mappingUrls");
        if (m != null) {
            this.mappingUrls = m;
        }
        Map<Class<?>, Object> c = (Map<Class<?>, Object>) getServletContext().getAttribute("controllers");
        if (c != null) {
            this.controllers = c;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        processRequest(req, res);
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String relativeUrl = req.getRequestURI().substring(req.getContextPath().length());
        HttpMethod httpMethod = HttpMethod.valueOf(req.getMethod());

        Mapping matchedMapping = mappingUrls.get(new UrlMethod(relativeUrl, httpMethod));

        if (matchedMapping == null) {
            afficherNotFound(res, relativeUrl);
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, MySession> session = (Map<String, MySession>) getServletContext().getAttribute("mySessions");
        HttpSession http = req.getSession();

        MySession mySession = session.computeIfAbsent(http.getId(), id -> new MySession(http));

        Method method = matchedMapping.getMethod();
        if (method.isAnnotationPresent(Auth.class)) {
            Auth auth = method.getAnnotation(Auth.class);
            if (!mySession.ifContains(this.variableSesion)) {
                res.sendError(401, "Vous devez être connecté");
                return;
            }
            String[] rolesAutorises = auth.roles();
            if (rolesAutorises.length > 0) {
                if (this.variableRole == null) {
                    res.sendError(500, "auth.role.variable n'est pas défini dans framework.properties");
                    return;
                }
                Object roleUtilisateur = mySession.get(this.variableRole);
                if (!Arrays.asList(rolesAutorises).contains(roleUtilisateur)) {
                    res.sendError(403, "Accès interdit pour votre rôle");
                    return;
                }
            }
        }

        try {
            Object controllerInstance = getController(matchedMapping.getControllerClass());
            Object result = matchedMapping.getMethod().invoke(controllerInstance,
                    buildArgs(matchedMapping.getMethod(), req, mySession));

            if (matchedMapping.getControllerClass().isAnnotationPresent(ApiRest.class)) {
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                PrintWriter out = res.getWriter();
                if (result instanceof String) {
                    out.print((String) result);
                } else {
                    out.print(new Gson().toJson(result));
                }
                out.flush();
                return;
            }

            if (!(result instanceof ModelAndView)) {
                throw new ServletException("La méthode " + matchedMapping.getMethod().getName()
                        + " doit retourner un ModelAndView");
            }

            ModelAndView mv = (ModelAndView) result;
            addArgToRequest(req, mv.getData());
            req.getRequestDispatcher(mv.getView()).forward(req, res);

        } catch (InvocationTargetException e) {
            afficherErreur(res, e.getCause() != null ? e.getCause() : e);
        } catch (Exception e) {
            afficherErreur(res, e);
        }

    }

    // Sprint 11 : retourne l'instance déjà créée (la crée une seule fois si absente)
    private Object getController(Class<?> type) {
        return controllers.computeIfAbsent(type, t -> {
            try {
                return t.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Impossible d'instancier " + t.getName(), e);
            }
        });
    }

    private Object[] buildArgs(Method method, HttpServletRequest req, MySession mySession) throws Exception {
        Parameter[] parametres = method.getParameters();
        Object[] args = new Object[parametres.length];

        for (int i = 0; i < parametres.length; i++) {
            Class<?> type = parametres[i].getType();
            if (type == MySession.class) {
                args[i] = mySession;
            } else if (type == MapFile.class) {
                args[i] = lireFichier(parametres[i].getName(), req);
            } else if (estSimple(type)) {
                String valeur = req.getParameter(parametres[i].getName());
                args[i] = convertir(valeur, type);
            } else {
                args[i] = construireObjet(type, parametres[i].getName(), req);
            }
        }
        return args;
    }

    private Object convertir(String valeur, Class<?> type) {
        if (type == String.class) {
            return valeur;
        }
        if (valeur == null || valeur.isEmpty()) {
            if (type == int.class)
                return 0;
            if (type == long.class)
                return 0L;
            if (type == double.class)
                return 0.0;
            if (type == float.class)
                return 0f;
            if (type == boolean.class)
                return false;
            return null;
        }

        if (type == int.class || type == Integer.class)
            return Integer.parseInt(valeur);
        if (type == long.class || type == Long.class)
            return Long.parseLong(valeur);
        if (type == double.class || type == Double.class)
            return Double.parseDouble(valeur);
        if (type == float.class || type == Float.class)
            return Float.parseFloat(valeur);
        if (type == boolean.class || type == Boolean.class)
            return Boolean.parseBoolean(valeur);
        return null;
    }

    private boolean estSimple(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class;
    }

    private Object construireObjet(Class<?> type, String prefixe, HttpServletRequest req) throws Exception {
        Object objet = type.getDeclaredConstructor().newInstance();

        for (Field champ : type.getDeclaredFields()) {
            if (Modifier.isStatic(champ.getModifiers()))
                continue;

            String nomComplet = prefixe + "." + champ.getName();
            Object valeur;

            if (estSimple(champ.getType())) {
                valeur = convertir(req.getParameter(nomComplet), champ.getType());
            } else if (champ.getType() == MapFile.class) {
                valeur = lireFichier(nomComplet, req);
            } else {
                valeur = construireObjet(champ.getType(), nomComplet, req);
            }

            String nomSetter = "set" + Character.toUpperCase(champ.getName().charAt(0))
                    + champ.getName().substring(1);
            Method setter = type.getMethod(nomSetter, champ.getType());
            setter.invoke(objet, valeur);
        }
        return objet;
    }

    // Lit le fichier envoye, l'enregistre dans upload.dir et renvoie un MapFile (ou
    // null si aucun fichier)
    private MapFile lireFichier(String nomChamp, HttpServletRequest req) throws Exception {
        String ct = req.getContentType();
        if (ct == null || !ct.toLowerCase().startsWith("multipart/")) {
            return null;
        }
        Part part = req.getPart(nomChamp);
        if (part == null || part.getSize() == 0) {
            return null;
        }

        String nomOriginal = Paths.get(part.getSubmittedFileName()).getFileName().toString();

        MapFile mf = new MapFile();
        mf.setNomFichier(nomOriginal);
        mf.setType(part.getContentType());
        try (InputStream in = part.getInputStream()) {
            mf.setContenu(in.readAllBytes());
        }

        String dir = (String) getServletContext().getAttribute("uploadDir");
        if (dir == null) {
            throw new ServletException("upload.dir n'est pas defini dans framework.properties");
        }
        Path dossier = Paths.get(dir);
        Files.createDirectories(dossier);
        Path cible = dossier.resolve(UUID.randomUUID() + "_" + nomOriginal);
        Files.write(cible, mf.getContenu());
        mf.setCheminSauvegarde(cible.toString());

        return mf;
    }

    private void addArgToRequest(HttpServletRequest req, Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    private void afficherErreur(HttpServletResponse res, Throwable e) throws IOException {
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();
        out.println("<h2 style='color: red;'>Erreur lors de l'exécution :</h2>");
        out.println("<pre>" + escape(String.valueOf(e.getMessage())) + "</pre>");
        out.close();
    }

    private void afficherNotFound(HttpServletResponse res, String relativeUrl) throws IOException {
        res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        PrintWriter out = res.getWriter();

        out.println("<!DOCTYPE html><html><head><title>404</title></head><body>");
        out.println("<h1 style='color: red;'>Erreur 404 - Route introuvable</h1>");
        out.println("<p>L'URL <strong>" + escape(relativeUrl) + "</strong> ne correspond à aucun mapping.</p>");
        out.println("<hr/>");
        out.println("<h3>Liste de toutes les routes disponibles :</h3>");

        if (mappingUrls.isEmpty()) {
            out.println("<p>Aucune route n'a été configurée dans l'application.</p>");
        } else {
            out.println("<table border='1' cellpadding='10' style='border-collapse: collapse; text-align: left;'>");
            out.println("<tr style='background-color: #f2f2f2;'><th>URL</th><th>Contrôleur</th><th>Méthode</th></tr>");
            for (Map.Entry<UrlMethod, Mapping> entry : mappingUrls.entrySet()) {
                UrlMethod um = entry.getKey();
                Mapping mapping = entry.getValue();
                out.println("<tr>");
                out.println("  <td><code>" + um.getMethod() + " " + escape(um.getUrl()) + "</code></td>");
                out.println("  <td>" + mapping.getControllerClass().getName() + "</td>");
                out.println("  <td><code>" + mapping.getMethod().getName() + "()</code></td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
        out.println("</body></html>");
        out.close();
    }

    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}