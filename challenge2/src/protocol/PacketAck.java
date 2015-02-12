package protocol;

import utils.ByteUtils;

public final class PacketAck extends Packet {
    private static final byte[] BUFFER = new byte[8];

    private int ackId;

    public PacketAck() {
        super(ID_ACK);
    }

    public PacketAck(int ackId) {
        super(ID_ACK);

        this.ackId = ackId;
    }

    @Override
    public byte[] serialize() {
        ByteUtils.getIntBytes(id, BUFFER, 0);
        ByteUtils.getIntBytes(ackId, BUFFER, 4);

        return BUFFER;
    }

    @Override
    public void deserialize(byte[] buffer) {
        ackId = ByteUtils.getIntFromBytes(buffer, 4);
    }

    public int getAckId() {
        return ackId;
    }
}
