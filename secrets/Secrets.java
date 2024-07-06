package secrets;

import files.json.JSON;
import files.json.JSONFormatException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Diese Klasse ist dazu da, API-Keys zu speichern, ohne dass diese auf GitHub landen oder so
 */
public class Secrets {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final File folder = new File(USER_HOME, "AppData\\Local\\Rezepte\\Secrets");
    private static final boolean PROMPT_ON_MISSING_KEY = true;

    private static final Map<String, String> SECRETS = new HashMap<>();

    private static void reload() {
        File file = new File(folder, "secrets.json");
        if (!file.exists()) return;
        try {
            var json = JSON.parse(Files.readString(file.toPath()));
            var object = json.object();

            for (var key : object.keySet()) {
                SECRETS.put(key, object.get(key).string());
            }
        } catch (JSONFormatException | IOException e) {
            System.err.println("Could not reload secrets because: " + e.getMessage());
        }
    }

    private static void save() {
        try {
            Files.createDirectories(folder.toPath());
            File file = new File(folder, "secrets.json");
            file.createNewFile();
            Files.writeString(file.toPath(), JSON.object(SECRETS, JSON::string).toString());
        } catch (IOException e) {
            System.err.println("Could not save secrets because: " + e.getMessage());
        }
    }


    /**
     * Gibt die geheime Info zurück, die den Namen trägt
     */
    public static String getSecret(String name) {
        String value = SECRETS.get(name);
        if (value != null) return value;

        return onNotFound(name);
    }

    private static String onNotFound(String name) {
        reload();
        String value = SECRETS.get(name);
        if (value != null) return value;

        if (PROMPT_ON_MISSING_KEY) {
            return promptForSecret(name);
        }

        return null;
    }

    private static String promptForSecret(String name) {
        String input = JOptionPane.showInputDialog(null, "Enter the secret for " + name, "");

        if (input == null) {
            return null;
        }

        if (input.isBlank()) {
            return promptForSecret(name);
        }

        SECRETS.put(name, input);
        save();
        return input;
    }
}