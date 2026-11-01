package com.sdm.framework.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import com.sdm.framework.outils.Mapping;
import com.sdm.framework.outils.MySession;
import com.sdm.framework.outils.Utils;
import com.sdm.framework.outils.UrlMethod;
import com.sdm.framework.annotation.UrlMapping; // au lieu de mg.itu.test.annotation.UrlMapping
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        Map<UrlMethod, Mapping> mappingUrls = new HashMap<>();
        context.setAttribute("mySessions", new ConcurrentHashMap<String, MySession>());
        Map<Class<?>, Object> controllers = new ConcurrentHashMap<>();

        String basePackage = context.getInitParameter("base-package");

        if (basePackage != null && !basePackage.trim().isEmpty()) {
            try {
                List<Class<?>> controllerList = Utils.getControllerClasses(basePackage.trim());

                for (Class<?> controllerClass : controllerList) {
                    // Sprint 11 : une seule instance par contrôleur
                    controllers.put(controllerClass, controllerClass.getDeclaredConstructor().newInstance());

                    Method[] methods = controllerClass.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(UrlMapping.class)) {
                            UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);

                            Mapping mapping = new Mapping(controllerClass, method);
                            mappingUrls.put(new UrlMethod(urlMapping), mapping);
                        }
                    }
                }
                System.out.println("Scan des contrôleurs terminé avec succès.");
            } catch (Exception e) {
                context.log("Erreur lors du scan des contrôleurs", e);
            }
        } else {
            System.out.println("Attention : Aucun 'base-package' n'a été spécifié.");
        }

        context.setAttribute("mappingUrls", mappingUrls);
        context.setAttribute("controllers", controllers);
        chargerConfigUpload(context);
    }

    private void chargerConfigUpload(ServletContext context) {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("framework.properties")) {
            if (in == null) {
                System.out.println("Attention : framework.properties introuvable dans le classpath.");
                return;
            }
            Properties props = new Properties();
            props.load(in);
            String dir = props.getProperty("upload.dir");
            if (dir != null && !dir.trim().isEmpty()) {
                context.setAttribute("uploadDir", dir.trim());
            }
        } catch (Exception e) {
            context.log("Erreur lors de la lecture de framework.properties", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("mappingUrls");
        sce.getServletContext().removeAttribute("controllers");
        sce.getServletContext().removeAttribute("uploadDir");
    }

}