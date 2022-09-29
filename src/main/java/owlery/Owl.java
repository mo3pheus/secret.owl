package owlery;

import hogwarts.engineering.secret.owl.HelpLetterOuterClass;
import hogwarts.engineering.secret.owl.HelpLetterOuterClass.HelpLetter;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Owl implements Runnable {
    private String destinationAddress;
    private String addressee;
    private final Logger logger = LoggerFactory.getLogger(Owl.class);
    private IMqttClient wizardOwl;
    private Properties applicationProperties;

    public Owl(Properties applicationProperties) {
        this.applicationProperties = applicationProperties;
        destinationAddress = applicationProperties.getProperty("destination.address");
        addressee = applicationProperties.getProperty("addressee");
        taskOwl(applicationProperties.getProperty("secret.owl.sender.name"),
                applicationProperties.getProperty("secret.owl.sender.password"));
    }

    private void taskOwl(String senderName, String senderPassword) {
        try {
            wizardOwl = new MqttClient(destinationAddress,
                    "hogwarts.owlery" + ThreadLocalRandom.current().nextLong());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setMaxInflight(3);
            options.setKeepAliveInterval(300);
            options.setUserName(senderName);
            options.setPassword(senderPassword.toCharArray());

            wizardOwl.connect(options);
        } catch (MqttException e) {
            logger.error("Could not instantiate wizard.owl -> ", e);
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("wizards.owl");
        byte[] messageBytes = composeLetter(applicationProperties).toByteArray();
        try {
            Thread.sleep(1000000);
            logger.info("Start of owl thread.");
            wizardOwl.publish(addressee, new MqttMessage(messageBytes));
            wizardOwl.disconnect();
            wizardOwl.close();
            logger.info("Published message -> ", new String(messageBytes));
        } catch (MqttException e) {
            logger.error("Bad spell! Could not send owl.", e);
        } catch (InterruptedException e) {
            logger.error("Code was interrupted.", e);
        }
    }

    private HelpLetter composeLetter(Properties applicationProperties) {
        HelpLetterOuterClass.HelpLetter.Builder helpLetterBuilder = HelpLetter.newBuilder();
        helpLetterBuilder.setMessage(extractMessage(applicationProperties.getProperty("secret.owl.message")));
        helpLetterBuilder.setSenderName(applicationProperties.getProperty("secret.owl.sender.name"));
        helpLetterBuilder.setSenderPassword(applicationProperties.getProperty("secret.owl.sender.password"));
        return helpLetterBuilder.build();
    }

    private String extractMessage(String messageFilePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(messageFilePath));
            StringBuilder stringBuilder = new StringBuilder();
            for (String line : lines) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            logger.error("Could not read messageFile." + messageFilePath, e);
            return null;
        }
    }
}
