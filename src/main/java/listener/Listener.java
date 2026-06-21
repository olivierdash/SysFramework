package main.java.listener;

import java.net.URL;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import main.java.controller.AnnotationController;
import main.java.utility.Utility;

@WebListener
public class Listener implements ServletContextListener {
    // Dans Listener.java — remplacer tout le contenu de contextInitialized par :
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String packageName = context.getInitParameter("PackageWithAnnotation");

        List<String> listClass = new ArrayList<>();

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

                            if (Utility.aAnnotation(classe, AnnotationController.class)) {
                                listClass.add(className);
                            }
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        context.setAttribute("listClass", listClass);
    }
}
