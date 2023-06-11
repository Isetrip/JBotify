package com.isetrip.jbotify.modules;

import com.isetrip.jbotify.annotations.JBotifyModule;
import com.isetrip.jbotify.exceptions.ModuleLoadException;
import lombok.Getter;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class ModulesLoader {

    private final String modulesPath;
    @Getter
    private final Set<String> modulesPackages;

    public ModulesLoader() {
        this.modulesPath = "modules";
        this.modulesPackages = new HashSet<>();
    }

    public void load() {
        File modulesDir = new File(this.modulesPath);
        if (!modulesDir.exists() || !modulesDir.isDirectory()) {
            System.out.println("Modular folder not found, nothing to download!");
            return;
        }

        File[] jarFiles = modulesDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            System.out.println("JAR files not found in module folder, nothing to load!");
            return;
        }

        for (File jarFile : jarFiles) {
            try {
                URL jarUrl = jarFile.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl});

                String jarFilePath = jarFile.getAbsolutePath();
                if (jarFilePath.contains(".jar")) {
                    jarFilePath = jarFilePath.substring(0, jarFilePath.indexOf(".jar"));
                }
                jarFilePath = jarFilePath.replace('/', '.');
                jarFilePath = jarFilePath.replace('\\', '.');
                Class<?>[] classes = classLoader.loadClass(jarFilePath).getDeclaredClasses();

                for (Class<?> clazz : classes) {
                    Class.forName(clazz.getName(), true, classLoader);
                    if (clazz.isAnnotationPresent(JBotifyModule.class)) this.modulesPackages.add(clazz.getPackageName());
                }
            } catch (Exception e) {
                throw new ModuleLoadException("Error when loading the module: " + e.getMessage());
            }
        }
    }

}
