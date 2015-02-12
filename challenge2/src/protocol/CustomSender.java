package protocol;

import utils.ByteUtils;
import client.NetworkLayer;
import client.Utils;

public final class CustomSender implements IRDTProtocol {
    private final int chunkSize = 512;
    private NetworkLayer networkLayer;
    private PacketCache packetCache;

    @Override
    public void TimeoutElapsed(Object tag) {

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

        beginTransfer();
        beginDataTransfer();
    }

    @Override
    public void setNetworkLayer(NetworkLayer networkLayer) {
        this.networkLayer = networkLayer;
    }

    private void beginTransfer() {
        boolean acked = false;
        long timeout = 1000;

        while (!acked) {
            System.out.println("SENDING BEGIN");

            networkLayer.sendPacket(packetCache.getBeginTransfer());

            if (receiveBeginAck(timeout)) {
                acked = true;

                System.out.println("RECEIVED BEGIN ACK");
            }
        }
    }

    private void beginDataTransfer() {
        //SendWindow window = new SendWindow(0, networkLayer, packetCache);

        //window.send();

        ReqResponder responder = new ReqResponder(networkLayer, packetCache.getDataFragments());

        responder.run();
    }

    private boolean receiveBeginAck(long timeout) {
        long start = System.currentTimeMillis();

        while (true) {
            Packet packet = receivePacket();

            if (packet != null && packet.getId() == Packet.ID_BEGIN_ACK) {
                return true;
            }

            long elapsed = System.currentTimeMillis() - start;

            if (elapsed > timeout) {
                return false;
            }
        }
    }

    private Packet receivePacket() {
        Integer[] data = networkLayer.receivePacket();

        if (data == null) {
            return null;
        }

        return Packet.fromData(data);
    }

}
