package main.java.utility;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Utility {

    // Vérifie si l'annotation est présente sur la classe elle-même
    public static boolean aAnnotationSurClasse(Class<?> classe, Class<? extends Annotation> annotation) {
        return classe.isAnnotationPresent(annotation);
    }

    // Vérifie si l'annotation est présente sur au moins un attribut de la classe
    public static boolean aAnnotationSurAttribut(Class<?> classe, Class<? extends Annotation> annotation) {
        for (Field champ : classe.getDeclaredFields()) {
            if (champ.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;
    }

    // Vérifie si l'annotation est présente sur au moins une méthode de la classe
    public static boolean aAnnotationSurMethode(Class<?> classe, Class<? extends Annotation> annotation) {
        for (Method methode : classe.getDeclaredMethods()) {
            if (methode.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;
    }

    // Vérifie si l'annotation est présente n'importe où (classe, attribut ou
    // méthode)
    public static boolean aAnnotation(Class<?> classe, Class<? extends Annotation> annotation) {
        return aAnnotationSurClasse(classe, annotation)
                || aAnnotationSurAttribut(classe, annotation)
                || aAnnotationSurMethode(classe, annotation);
    }

    // Version qui prend le nom du package + le nom de la classe, comme demandé dans
    // la todo
    public static boolean aAnnotation(String packageName, String nomClasse, Class<? extends Annotation> annotation)
            throws ClassNotFoundException {
        Class<?> classe = Class.forName(packageName + "." + nomClasse);
        return aAnnotation(classe, annotation);
    }
}