package protocol;

import utils.ArgumentException;

public abstract class Packet {
    public static final int ID_BEGIN_TRANSFER = 0;
    public static final int ID_DATA_FRAGMENT = 1;
    public static final int ID_ACK = 2;
    public static final int ID_BEGIN_ACK = 3;

    protected final int id;

    public Packet(int id) {
        this.id = id;
    }

    public abstract byte[] serialize();

    public abstract void deserialize(byte[] buffer);

    public static Packet fromId(int id) {
        switch (id) {
            case ID_BEGIN_TRANSFER:
                return new PacketBeginTransfer();
            case ID_DATA_FRAGMENT:
                return new PacketDataFragment();
            case ID_ACK:
                return new PacketAck();
            case ID_BEGIN_ACK:
                return new PacketBeginAck();
        }

        throw new ArgumentException("id", "unknown id");
    }
}
