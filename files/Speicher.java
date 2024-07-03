package files;

import data.Rezept;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Speicher {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final File folder = new File(USER_HOME, "AppData\\Local\\Rezepte");

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
            File afile = new File(folder, rezept.getName() + ".rezept");
            afile.createNewFile();
            Files.writeString(afile.toPath(), rezept.toJSON().toString());
        } catch (IOException e) {
            System.err.println("Da war 'n Error beim Speichern von so 'nem Rezept: \n" + e.getMessage());
        }

    }

    /**
     * Versucht das Rezept zu laden, dass in dieser File gespeichert ist
     *
     * @return null, wenn das Rezept nicht geladen werden kann, ansonsten das Rezept
     */
    private static Rezept lade(File file) {
        return null;
    }

    /**
     * Gibt alle Rezepte zurück, die geladen werden konnten
     **/
    public static Rezept[] ladeAlle() {
        return new Rezept[0];
    }
}