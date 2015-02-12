package protocol;

import utils.ByteUtils;

public final class PacketDataFragment extends Packet {
    private int index;
    private byte[] data;

    public PacketDataFragment() {
        super(ID_DATA_FRAGMENT);
    }

    public PacketDataFragment(int index, byte[] data) {
        super(ID_DATA_FRAGMENT);

        this.index = index;
        this.data = data;
    }

    @Override
    public byte[] serialize() {
        byte[] buffer = new byte[12 + data.length];

        ByteUtils.getIntBytes(id, buffer, 0);
        ByteUtils.getIntBytes(index, buffer, 4);
        ByteUtils.getIntBytes(data.length, buffer, 8);
        System.arraycopy(data, 0, buffer, 12, data.length);

        return buffer;
    }

    @Override
    public void deserialize(byte[] buffer) {
        index = ByteUtils.getIntFromBytes(buffer, 4);
        int dataLength = ByteUtils.getIntFromBytes(buffer, 8);

        data = new byte[dataLength];

        System.arraycopy(buffer, 12, data, 0, data.length);
    }

    public int getIndex() {
        return index;
    }

    public byte[] getData() {
        return data;
    }
}
