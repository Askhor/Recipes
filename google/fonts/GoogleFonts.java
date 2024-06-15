package google.fonts;

import files.json.JSON;
import files.json.JSONFormatException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class GoogleFonts {
    private static final String VERY_SECRET_API_KEY = "AIzaSyBuAuZmcuSeWJO5odJKPZsL10MY3bxqaww";
    private static final HashMap<String, Font> loadedFonts = new HashMap<>();

    public static Font get(String name) {
        if (loadedFonts.containsKey(name))
            return loadedFonts.get(name);
        downloadFont(name);
        return loadedFonts.get(name);
    }

    private static void downloadFont(String name) {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(createURI(
                            "https", "www.googleapis.com", "/webfonts/v1/webfonts",
                            "capability=VF", "family=" + name, "key=" + VERY_SECRET_API_KEY
                    ))
                    .GET()
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                printDownloadErrorMessage(name, response);
                return;
            }
            var downloadURL = URI.create(
                    JSON.parse(response.body())
                            .object().get("items")
                            .list().getFirst()
                            .object().get("files")
                            .object().get("regular")
                            .string()
            );

            createFont(name, downloadURL, client);
        } catch (IOException | InterruptedException | JSONFormatException e) {
            System.err.println("Font download failed");
            e.printStackTrace();
        }
    }

    private static void createFont(String name, URI uri, HttpClient client) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(uri).GET().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            System.err.println("The downloading of font \"" + name + "\" failed with status code " + response.statusCode());
        }

        Font[] fonts;

        try {
            fonts = Font.createFonts(response.body());
        } catch (FontFormatException e) {
            System.err.println("The font " + name + " could not be downloaded, because the returned font file was not correctly formatted");
            return;
        }

        if (fonts.length != 1) {
            throw new IOException("Wtf");
        }

        loadedFonts.put(name, fonts[0]);
    }

    private static void printDownloadErrorMessage(String name, HttpResponse<String> response) {
        System.err.println("The downloading of font \"" + name + "\" failed with status code " + response.statusCode());
        try {
            var errorMessage = JSON.parse(response.body())
                    .object().get("error").object().get("message")
                    .string();
            System.err.println("The error message is:\n" + errorMessage);
        } catch (JSONFormatException e) {
            System.err.println("The complete response is\n" + response.body());
        }
    }

    private static URI createURI(String scheme, String authority, String path, String... arguments) {
        var out = new StringBuilder(scheme + "://" + authority + "/" + path);

        if (arguments.length >= 1) {
            out.append('?');
            out.append(arguments[0]);

            for (int i = 1; i < arguments.length; i++) {
                out.append('&');
                out.append(arguments[i]);
            }
        }
        return URI.create(out.toString().replace(" ", "%20"));
    }
}