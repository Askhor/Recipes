package secrets;

import files.json.JSON;
import files.json.JSONFormatException;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Diese Klasse ist dazu da, API-Keys zu speichern, ohne dass diese auf GitHub landen oder so
 */
public class Secrets {
    private static final Path USER_HOME = Paths.get(System.getProperty("user.home"));
    private static final Path file = USER_HOME.resolve(Paths.get("AppData", "Local", "Rezepte", "secrets.json"));
    private static final boolean PROMPT_ON_MISSING_KEY = false;

    private static final Map<String, String> SECRETS = new HashMap<>();

    private static void reload() {
        if (Files.notExists(file)) return;
        try {
            var json = JSON.parse(Files.readString(file));
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
            Files.createDirectories(file.getParent());
            if (Files.notExists(file))
                Files.createFile(file);
            Files.writeString(file, JSON.object(SECRETS, JSON::string).toString());
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

    /**
     * Speichert die geheime Info
     * */
    public static void putSecret(String name, String value) {
        SECRETS.put(name, value);
        save();
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
