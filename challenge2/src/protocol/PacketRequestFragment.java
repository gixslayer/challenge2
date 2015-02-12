package protocol;

import utils.ByteUtils;

public final class PacketRequestFragment extends Packet {
    private static final byte[] BUFFER = new byte[8];

    private int index;

    public PacketRequestFragment() {
        super(ID_REQUEST_FRAGMENT);
    }

    public PacketRequestFragment(int index) {
        super(ID_REQUEST_FRAGMENT);

        this.index = index;
    }

    @Override
    public byte[] serialize() {
        ByteUtils.getIntBytes(id, BUFFER, 0);
        ByteUtils.getIntBytes(index, BUFFER, 4);

        return BUFFER;
    }

    @Override
    public void deserialize(byte[] buffer) {
        index = ByteUtils.getIntFromBytes(buffer, 4);
    }

    public int getIndex() {
        return index;
    }
}
