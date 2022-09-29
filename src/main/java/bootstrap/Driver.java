package bootstrap;

import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import owlery.Owl;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Driver {
    public static Properties projectProperties = new Properties();
    static String logFilePath;

    public static void main(String[] args) throws InterruptedException {
        try {
            Thread.currentThread().setName("hogwarts-startOf-Execution");
            configureProperties(args);
        } catch (Exception e) {
            System.out.println("Could not confiure application" + e.getMessage());
            System.exit(1);
        }
        configureLogging(false);
        Logger logger = LoggerFactory.getLogger(Driver.class);

        logger.info("Starting the owl thread.");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Owl(projectProperties));
        executorService.awaitTermination(1l, TimeUnit.SECONDS);
        executorService.shutdown();
        logger.info("Ending the owl thread.");
    }

    public static String configureLogging(boolean debug) {
        logFilePath = projectProperties.getProperty("log.file.path");
        FileAppender fa = new FileAppender();

        if (!debug) {
            fa.setThreshold(Level.toLevel(Priority.INFO_INT));
            fa.setFile(logFilePath + "secret-owl.log");
        } else {
            fa.setThreshold(Level.toLevel(Priority.DEBUG_INT));
            fa.setFile(logFilePath + "secret-owl-debug.log");
        }

        fa.setLayout(new EnhancedPatternLayout("%-6d [%25.35t] %-5p %40.80c - %m%n"));

        fa.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(fa);
        return fa.getFile();
    }

    public static void configureProperties(String[] args) {
        projectProperties = new Properties();
        for (int i = 0; i < args.length; i++) {
            if (args[i].contains("--")) {
                String key = args[i].replaceAll("--", "");
                projectProperties.put(key, args[i + 1]);
            }
        }
    }
}
