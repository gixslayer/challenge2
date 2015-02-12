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

        dest[offset + 0] = (byte) (value >> 32);
        dest[offset + 1] = (byte) ((value >> 24) & 0xff);
        dest[offset + 2] = (byte) ((value >> 16) & 0xff);
        dest[offset + 3] = (byte) ((value >> 8) & 0xff);
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

        value |= buffer[0] << 32;
        value |= buffer[1] << 24;
        value |= buffer[2] << 16;
        value |= buffer[3];

        return value;
    }

    public static Integer[] byteArrayToIntArray(byte[] value) {
        Integer[] result = new Integer[value.length];

        for (int i = 0; i < value.length; i++) {
            result[i] = new Integer(value[i]);
        }

        return result;
    }
}
