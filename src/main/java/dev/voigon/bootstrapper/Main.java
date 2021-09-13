package dev.voigon.bootstrapper;

import lombok.extern.java.Log;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.*;

@Log
public class Main {

    public static void main(String[] args) {
        setupLogger();

        log.info(String.format(
                "Starting boostrap version %s \n" +
                        "           Made by Voigon", VersionInformation.VERSION));

        log.info("Loading configuration...");
        BootstrapperConfiguration configuration;
        try {
             configuration = BootstrapperConfiguration.load(new File("bootstrapper.properties"));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Cannot access current working directory. Please ensure file permissions are correctly configured", e);
            Runtime.getRuntime().exit(1);
            return;
        }

        log.info("Looking for main jar file...");
        File mainJarFile = new File(configuration.getMainJarFileName());
        if (!mainJarFile.exists()) {
            log.log(Level.SEVERE, String.format("Main jar file %s is not present in the working directory", configuration.getMainJarFileName()));
            Runtime.getRuntime().exit(1);
            return;
        }

        JarFile mainJarFileInstance;
        try {
            mainJarFileInstance = new JarFile(mainJarFile);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error accessing main jar file");
            Runtime.getRuntime().exit(1);
            return;
        }

        log.info("Looking for additional libraries...");

        File librariesDirectory = new File("libraries");
        URL[] classLoaderUrls;

        try {
            Collection<URL> collection = collectUrlsFromDirectory(librariesDirectory);
            collection.add(mainJarFile.toURI().toURL());
            classLoaderUrls = collection.toArray(new URL[0]);

        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Error while attaining URL of main jar file", e);
            Runtime.getRuntime().exit(1);
            return;
        }

        log.info("Loading classes...");

        URLClassLoader childClassLoader = new URLClassLoader(
                classLoaderUrls,
                Main.class.getClassLoader()// allow for a more dynamic use of this software
        );

        log.info("Entering application");

        Manifest manifest;
        try {
             manifest = mainJarFileInstance.getManifest();
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error reading manifest file of main jar", e);
            Runtime.getRuntime().exit(1);
            return;
        }

        String mainClassName = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        Class<?> mainClass;

        try {
            mainClass = childClassLoader.loadClass(mainClassName);
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, String.format("Cannot find Main-Class: %s", mainClassName), e);
            Runtime.getRuntime().exit(1);
            return;
        }

        Method mainMethod = null;
        try {
            mainMethod = mainClass.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            log.log(Level.SEVERE, String.format("Cannot find main() method in main class %s", mainClassName), e);
            Runtime.getRuntime().exit(1);
            return;
        }

        try {
            // main methods does not have to be public, make sure the method is accessible before continuing
            boolean accessible = mainMethod.isAccessible();
            if (!accessible)
                mainMethod.setAccessible(true);

            log.info("Come again soon!");
            mainMethod.invoke(null, new Object[] { args });

            if (!accessible)
                mainMethod.setAccessible(true);

        } catch (IllegalAccessException e) {
            log.log(Level.SEVERE, "Cannot invoke main method", e);
            Runtime.getRuntime().exit(1);
        } catch (InvocationTargetException e) {
            // Not our concern anymore
            e.getCause().printStackTrace();

        }

    }

    private static Collection<URL> collectUrlsFromDirectory(File directory) {
        if (!directory.exists())
            return Collections.emptyList();

        Set<URL> list = new HashSet<>();

        File[] files = directory.listFiles();
        if (files == null)
            return Collections.emptyList();

        for (File file : files) {
            if (file.isDirectory())
                list.addAll(
                        collectUrlsFromDirectory(file));
            else if (file.getName().endsWith(".jar") || file.getName().endsWith(".war")) {
                try {
                    list.add(file.toURI().toURL());
                    log.info(String.format("Found library jar file %s", file.getPath()));
                } catch (MalformedURLException e) {
                    log.info(String.format("Found library jar file %s, but couldn't load it due to invalid path", file.getPath()));
                }
            }
        }

        return list;
    }

    private static void setupLogger() {
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        });
        log.addHandler(handler);
    }
}
