package utils;

@SuppressWarnings("serial")
public final class ArgumentException extends RuntimeException {

    public ArgumentException(String argName, String message) {
        super(String.format("%s: %s", argName, message));
    }
}
