package files;

import data.Rezept;
import files.json.JSON;
import files.json.JSONFormatException;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.function.Predicate;

public class Speicher {
    private static final Path USER_HOME = Paths.get(System.getProperty("user.home"));
    private static final Path folder = USER_HOME.resolve(Paths.get("AppData", "Local", "Rezepte"));
    private static final Predicate<Path> rezeptFileFilter = pathname -> {
        String name = pathname.getFileName().toString();
        if (name.indexOf('.') == -1) return false;
        return name.substring(name.lastIndexOf('.')).equals(".rezept");
    };

    public static Path getPath(String filename) {
        return folder.resolve(filename);
    }

    private static String getFileName(Rezept rezept) {
        return rezept.getName() + ".rezept";
    }

    /**
     * Kreiert den Ordner, in dem die Rezepte gespeichert werden, sollte dieser nicht existieren
     */
    private static void ensureExist() throws IOException {
        Files.createDirectories(folder);
    }


    /**
     * Speichert das gegebene Rezept in den files
     */
    public static void speicher(Rezept rezept) {
        try {
            ensureExist();
            Path afile = getPath(getFileName(rezept));
            Files.writeString(afile, rezept.toJSON().toString());
        } catch (IOException e) {
            System.err.println("Da war 'n Error beim Speichern von so 'nem Rezept: \n" + e.getMessage());
        }

    }

    public static void loschen(Rezept rezept) {
        try {
            Files.deleteIfExists(getPath(getFileName(rezept)));
        } catch (IOException e) {
            System.err.println("Datei konnte nicht gelöscht werden");
        }
    }

    /**
     * Versucht das Rezept zu laden, dass in dieser File gespeichert ist
     *
     * @return null, wenn das Rezept nicht geladen werden kann, ansonsten das Rezept
     */
    private static Rezept lade(String name, Path file) {
        Rezept ziel = null;

        for (Rezept r : Rezept.getAlleRezepte()) {
            if (r.getName().equals(name)) {
                ziel = r;
            }
        }

        if (ziel == null) {
            ziel = new Rezept();
        }

        try {
            ziel.loadJSON(JSON.parse(Files.readString(file)));
        } catch (JSONFormatException | IOException e) {
            System.err.println("Das Rezept " + name + " konnte nicht geladen werden:\n" + e.getMessage());
        }

        return ziel;
    }

    /**
     * Gibt alle Rezepte zurück, die geladen werden konnten
     **/
    public static Stream<Rezept> ladeAlle() {
        try {
            return Files.list(folder).filter(rezeptFileFilter)
            .map(file -> {
                String name = file.getFileName().toString();
                return lade(name.substring(0, name.lastIndexOf('.')), file);
            });
        } catch (IOException e) {
            return Stream.empty();
        }
    }
}
