package main.java.listener;

import java.net.URL;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import main.java.annotation.AnnotationController;
import main.java.annotation.UrlMapping;
import main.java.outils.Mapping;
import main.java.outils.UrlMethod;
import main.java.utility.Utility;

@WebListener
public class Listener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String packageName = context.getInitParameter("PackageWithAnnotation");

        Map<UrlMethod, Mapping> mappingUrls = new HashMap<>();

        try {
            String path = packageName.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(path);

            if (resource != null) {
                File directory = new File(resource.getFile());
                if (directory.exists()) {
                    for (File file : directory.listFiles()) {
                        if (file.getName().endsWith(".class")) {
                            String className = packageName + "." + file.getName().replace(".class", "");
                            Class<?> classe = Class.forName(className);

                            // On ne garde que les classes annotées @AnnotationController
                            if (Utility.aAnnotation(classe, AnnotationController.class)) {
                                for (Method methode : classe.getDeclaredMethods()) {
                                    if (methode.isAnnotationPresent(UrlMapping.class)) {
                                        UrlMapping urlMapping = methode.getAnnotation(UrlMapping.class);
                                        Mapping mapping = new Mapping(classe, methode);
                                        mappingUrls.put(new UrlMethod(urlMapping), mapping);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("Scan des contrôleurs terminé avec succès.");
        } catch (ClassNotFoundException e) {
            context.log("Erreur lors du scan des contrôleurs", e);
        }

        context.setAttribute("mappingUrls", mappingUrls);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("mappingUrls");
    }
}