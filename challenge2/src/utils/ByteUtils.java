package utils;

public final class ByteUtils {
    public static final int INT_SIZE = 4;
    private static final byte[] BUFFER = new byte[INT_SIZE];

    public static byte[] getIntBytes(int value) {
        getIntBytes(value, BUFFER, 0);

        return BUFFER;
    }

    public static void getIntBytes(int value, byte[] dest, int offset) {
        if (dest == null) {
            throw new ArgumentException("dest", "cannot be null");
        } else if (offset < 0) {
            throw new ArgumentException("offset", "cannot be negative");
        } else if (offset + INT_SIZE > dest.length) {
            throw new ArgumentException("offset", "cannot exceed array bounds");
        }

        dest[offset + 0] = (byte) (value >> 24);
        dest[offset + 1] = (byte) ((value >> 16) & 0xff);
        dest[offset + 2] = (byte) ((value >> 8) & 0xff);
        dest[offset + 3] = (byte) ((value >> 0) & 0xff);
    }

    public static int getIntFromBytes(byte[] buffer, int offset) {
        if (buffer == null) {
            throw new ArgumentException("buffer", "cannot be null");
        } else if (offset < 0) {
            throw new ArgumentException("offset", "cannot be negative");
        } else if (offset + INT_SIZE > buffer.length) {
            throw new ArgumentException("offset", "cannot exceed array bounds");
        }

        int value = 0;

        // These & 0xff will 'convert' the signed bytes to their unsigned value in an integer type.
        value |= (buffer[offset + 0] & 0xff) << 24;
        value |= (buffer[offset + 1] & 0xff) << 16;
        value |= (buffer[offset + 2] & 0xff) << 8;
        value |= buffer[offset + 3] & 0xff;

        return value;
    }

    public static Integer[] byteArrayToIntArray(byte[] value) {
        Integer[] result = new Integer[value.length];

        for (int i = 0; i < value.length; i++) {
            result[i] = new Integer(value[i]);
        }

        return result;
    }

    public static byte[] intArrayToByteArray(Integer[] value) {
        byte[] result = new byte[value.length];

        for (int i = 0; i < value.length; i++) {
            result[i] = value[i].byteValue();
        }

        return result;
    }
}
