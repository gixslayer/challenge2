package protocol;

import utils.ByteUtils;
import client.NetworkLayer;
import client.Utils;

public final class CustomSender implements IRDTProtocol {
    private final int chunkSize = 256;
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

        beginTransfer();
        //beginDataTransfer(chunks);
        beginDataTransfer2();
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

    private void beginDataTransfer2() {
        SendWindow window = new SendWindow(0, networkLayer, packetCache);

        window.send();
    }

    private void beginDataTransfer(int chunks) {
        int windowSize = 16;
        int offset = 0;
        int remaining = chunks;

        while (offset < chunks) {
            int count = remaining < windowSize ? remaining : windowSize;

            sendDataFragments(offset, count);

            offset += count;
            remaining -= count;
        }

    }

    private void sendDataFragments(int offset, int count) {
        long timeout = 1500;

        for (int i = 0; i < count; i++) {
            while (true) {
                System.out.println("SENDING FRAGMENT " + (offset + i));

                networkLayer.sendPacket(packetCache.getDataFragment(offset + i));

                if (receiveAck(offset + i, timeout)) {
                    System.out.println("RECEIVED ACK FOR FRAGMENT " + (offset + i));

                    break;
                }
            }
        }

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

    private boolean receiveAck(int index, long timeout) {
        long start = System.currentTimeMillis();

        while (true) {
            Packet packet = receivePacket();

            if (packet != null && packet.getId() == Packet.ID_ACK) {
                PacketAck packetAck = (PacketAck) packet;

                return packetAck.getAckId() == index;
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
