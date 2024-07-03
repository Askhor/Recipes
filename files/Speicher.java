package files;

import data.Rezept;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Speicher {
    private static final String USER_HOME = System.getProperty("user.home");
    private static final File folder = new File(USER_HOME, "AppData\\Local\\Rezepte");

    private static void ensureExist() throws IOException {
        Files.createDirectories(folder.toPath());
    }


    //Rezept speichern
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
    //Rezept laden
}