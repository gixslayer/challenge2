package protocol;

import utils.ByteUtils;

public final class PacketBeginAck extends Packet {
    private static final byte[] BUFFER = new byte[4];

    public PacketBeginAck() {
        super(ID_BEGIN_ACK);
    }

    @Override
    public byte[] serialize() {
        ByteUtils.getIntBytes(id, BUFFER, 0);

        return BUFFER;
    }

    @Override
    public void deserialize(byte[] buffer) {

    }
}
