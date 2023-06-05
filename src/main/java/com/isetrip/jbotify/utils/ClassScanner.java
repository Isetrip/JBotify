package com.isetrip.jbotify.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassScanner {

    public static List<Class<?>> findAnnotatedClasses(String packageName, Class<? extends Annotation> annotation) {
        List<Class<?>> annotatedClasses = new ArrayList<>();

        String packagePath = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.getFile());
                if (file.isDirectory()) {
                    scanDirectory(packageName, file, annotation, annotatedClasses);
                } else if (file.getName().endsWith(".class")) {
                    scanClassFile(packageName, file, annotation, annotatedClasses);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return annotatedClasses;
    }

    private static void scanDirectory(String packageName, File directory, Class<? extends Annotation> annotation, List<Class<?>> annotatedClasses) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(packageName + "." + file.getName(), file, annotation, annotatedClasses);
                } else if (file.getName().endsWith(".class")) {
                    scanClassFile(packageName, file, annotation, annotatedClasses);
                }
            }
        }
    }

    private static void scanClassFile(String packageName, File file, Class<? extends Annotation> annotation, List<Class<?>> annotatedClasses) {
        String className = file.getName().substring(0, file.getName().length() - 6);
        try {
            Class<?> clazz = Class.forName(packageName + '.' + className);
            if (clazz.isAnnotationPresent(annotation)) {
                annotatedClasses.add(clazz);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

