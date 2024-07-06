package files;

import data.Rezept;
import files.json.JSON;
import files.json.JSONFormatException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class Speicher {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final File folder = new File(USER_HOME, "AppData\\Local\\Rezepte");
    private static final FileFilter rezeptFileFilter = pathname -> {
        String name = pathname.getName();
        if (name.indexOf('.') == -1) return false;
        return name.substring(name.lastIndexOf('.')).equals(".rezept");
    };

    private static String getFileName(Rezept rezept) {
        return rezept.getName() + ".rezept";
    }

    /**
     * Kreiert den Ordner, in dem die Rezepte gespeichert werden, sollte dieser nicht existieren
     */
    private static void ensureExist() throws IOException {
        Files.createDirectories(folder.toPath());
    }


    /**
     * Speichert das gegebene Rezept in den files
     */
    public static void speicher(Rezept rezept) {
        try {
            ensureExist();
            File afile = new File(folder, getFileName(rezept));
            afile.createNewFile();
            Files.writeString(afile.toPath(), rezept.toJSON().toString());
        } catch (IOException e) {
            System.err.println("Da war 'n Error beim Speichern von so 'nem Rezept: \n" + e.getMessage());
        }

    }

    public static void loschen(Rezept rezept) {
        File file = new File(folder, getFileName(rezept));
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Versucht das Rezept zu laden, dass in dieser File gespeichert ist
     *
     * @return null, wenn das Rezept nicht geladen werden kann, ansonsten das Rezept
     */
    private static Rezept lade(String name, File file) {
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
            ziel.loadJSON(JSON.parse(Files.readString(file.toPath())));
        } catch (JSONFormatException | IOException e) {
            System.err.println("Das Rezept " + name + " konnte nicht geladen werden:\n" + e.getMessage());
        }

        return ziel;
    }

    /**
     * Gibt alle Rezepte zurück, die geladen werden konnten
     **/
    public static Stream<Rezept> ladeAlle() {
        File[] files = folder.listFiles(rezeptFileFilter);
        Objects.requireNonNull(files);

        return Arrays.stream(files)
                .map(file -> {
                    String name = file.getName();
                    return lade(name.substring(0, name.lastIndexOf('.')), file);
                });
    }
}