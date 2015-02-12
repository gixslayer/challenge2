package protocol;

import utils.ByteUtils;

public final class PacketBeginTransfer extends Packet {
    private static final byte[] BUFFER = new byte[16];

    private int chunks;
    private int chunkSize;
    private int fileSize;

    public PacketBeginTransfer() {
        super(ID_BEGIN_TRANSFER);
    }

    public PacketBeginTransfer(int chunks, int chunkSize, int fileSize) {
        super(ID_BEGIN_TRANSFER);

        this.chunks = chunks;
        this.chunkSize = chunkSize;
        this.fileSize = fileSize;
    }

    @Override
    public byte[] serialize() {
        ByteUtils.getIntBytes(id, BUFFER, 0);
        ByteUtils.getIntBytes(chunks, BUFFER, 4);
        ByteUtils.getIntBytes(chunkSize, BUFFER, 8);
        ByteUtils.getIntBytes(fileSize, BUFFER, 12);

        return BUFFER;
    }

    @Override
    public void deserialize(byte[] buffer) {
        chunks = ByteUtils.getIntFromBytes(buffer, 4);
        chunkSize = ByteUtils.getIntFromBytes(buffer, 8);
        fileSize = ByteUtils.getIntFromBytes(buffer, 12);
    }

    public int getChunks() {
        return chunks;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public int getFileSize() {
        return fileSize;
    }

}
