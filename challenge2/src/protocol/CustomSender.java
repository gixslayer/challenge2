package protocol;

import utils.ByteUtils;
import client.NetworkLayer;
import client.Utils;

public final class CustomSender implements IRDTProtocol {
    private final int chunkSize = 128;
    private NetworkLayer networkLayer;
    private PacketCache packetCache;

    @Override
    public void TimeoutElapsed(Object tag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void run() {
        Integer[] intFileContents = Utils.getFileContents();
        byte[] fileContents = ByteUtils.intArrayToByteArray(intFileContents);
        int fileSize = fileContents.length;
        int chunks = fileSize % chunkSize == 0 ? fileSize / chunkSize : fileSize / chunkSize + 1;
        PacketDataFragment[] dataFragments = new PacketDataFragment[chunks];
        int fileOffset = 0;

        for (int i = 0; i < chunks; i++) {
            int index = i;
            int size = fileOffset + chunkSize > fileSize ? fileSize - fileOffset : chunkSize;
            byte[] data = new byte[size];

            System.arraycopy(fileContents, fileOffset, data, 0, size);

            fileOffset += size;

            PacketDataFragment dataFragment = new PacketDataFragment(index, data);

            dataFragments[i] = dataFragment;
        }

        PacketBeginTransfer packetBeginTransfer =
                new PacketBeginTransfer(chunks, chunkSize, fileSize);

        packetCache = new PacketCache(packetBeginTransfer, dataFragments);

        networkLayer.sendPacket(packetCache.getBeginTransfer());

        // TODO: Wait for beginAck from receiver.

        for (Integer[] dataFragment : packetCache.getDataFragments()) {
            networkLayer.sendPacket(dataFragment);
        }
    }

    @Override
    public void setNetworkLayer(NetworkLayer networkLayer) {
        this.networkLayer = networkLayer;
    }

}
