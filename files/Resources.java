package files;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Manages all resources with type T
 */
public class Resources<T> {
    private final Map<String, T> cache = new HashMap<>();
    private final Function<InputStream, T> loader;

    /**
     * Kreiert ein neues Resources-Objekt.
     *
     * @param loader Wird verwendet, um zwischen dem InputStream der Resource zu T zu konvertieren
     */
    public Resources(Function<InputStream, T> loader) {
        this.loader = loader;
    }

    /**
     * Versucht die Resource zurückzugeben
     *
     * @return die Resource oder null, falls diese nicht geladen werden konnte
     */
    public T get(String name) {
        if (!name.startsWith("/"))
            throw new IllegalArgumentException("Ressourcenname müssen mit '/' anfangen");
        return cache.computeIfAbsent(name, this::loadResource);
    }

    private T loadResource(String name) {
        try (InputStream stream = Resources.class.getResourceAsStream(name)) {
            if (stream == null) {
                System.err.println("Die Resource " + name + " existiert nicht");
                return null;
            }
            return loader.apply(stream);
        } catch (IOException e) {
            System.err.println("Resource " + name + " konnte nicht geladen werden");
            return null;
        }
    }
}