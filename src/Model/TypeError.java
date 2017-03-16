package Model;

/**
 * Simpel custom error class
 */
public class TypeError extends Throwable {
    public TypeError() {
    }

    public TypeError(String message) {
        super(message);
    }
}
