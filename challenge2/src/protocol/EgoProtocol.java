package protocol;

import java.util.ArrayList;
import java.util.List;

import utils.ByteUtils;
import client.NetworkLayer;
import client.Utils;

/**
 * Created by Kevin on 12-02-15.
 */
public class EgoProtocol implements IRDTProtocol {

    private NetworkLayer networkLayer;

    private static final int MAX_CHUNKS = 8;

    byte[] fileContents = new byte[0];
    private int chunks;
    private int chunkSize;
    private List<Integer> receivedChunks;

    @Override
    public void run() {
        System.out.println("Receiving...");

        receivedChunks = new ArrayList<Integer>();

        boolean stop = false;
        while (!stop) {
            Integer[] p = networkLayer.receivePacket();

            //===== Packet Received =====
            if (p != null) {
                Packet packet = Packet.fromData(p);
                int id = packet.getId();

                System.out.print(System.lineSeparator() + "Received: "
                        + (receivedChunks.size() - 1));

                //===== Header =====
                if (id == Packet.ID_BEGIN_TRANSFER) {
                    PacketBeginTransfer beginPacket = (PacketBeginTransfer) packet;
                    fileContents = new byte[beginPacket.getFileSize()];

                    chunks = beginPacket.getChunks();
                    chunkSize = beginPacket.getChunkSize();

                    System.out.print(" | Chunks: " + chunks + " | Chunksize: " + chunkSize);

                    PacketBeginAck ack = new PacketBeginAck();
                    networkLayer.sendPacket(ByteUtils.byteArrayToIntArray(ack.serialize()));

                    sendRequests();
                    stop = true;
                }

                /*===== Data =====
                if(id == Packet.ID_DATA_FRAGMENT) {
                    PacketDataFragment pack = (PacketDataFragment)packet;
                    int index = pack.getIndex();

                    //===== ACK =====
                    PacketAck ack = new PacketAck(index);
                    networkLayer.sendPacket(ByteUtils.byteArrayToIntArray(ack.serialize()));

                    if(!receivedChunks.contains(index)) {
                        receivedChunks.add(index);

                        System.out.print(" | Index: " + index);

                        //===== Adding to the File =====
                        System.arraycopy(pack.getData(), 0, fileContents, index * chunkSize, pack.getData().length);
                        //===== End Of File =====
                        if (receivedChunks.size() == chunks) {
                            System.out.println(System.lineSeparator() + "Reached end-of-file. Done receiving.");
                            stop = true;
                        }
                    }
                }*/
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    stop = true;
                }
            }
        }

        Utils.setFileContents(ByteUtils.byteArrayToIntArray(fileContents));
    }

    @Override
    public void setNetworkLayer(NetworkLayer networkLayer) {
        this.networkLayer = networkLayer;
    }

    @Override
    public void TimeoutElapsed(Object tag) {
    }

    public void sendRequests() {
        int remaining = chunks;
        long[] lastRequested = new long[chunks];
        long threshold = 750;

        while (remaining != 0) {
            int count = 0;
            for (int i = 0; i < chunks && count < MAX_CHUNKS; i++) {
                if (!receivedChunks.contains(i)) {
                    long time = System.currentTimeMillis();
                    if (time - lastRequested[i] > threshold) {
                        PacketRequestFragment packet = new PacketRequestFragment(i);
                        networkLayer.sendPacket(ByteUtils.byteArrayToIntArray(packet.serialize()));
                        lastRequested[i] = time;
                        System.out.print("Request: " + i + " | ");
                        count++;
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            System.out.print(System.lineSeparator());
            remaining -= receiveChunks(1500);
        }
        PacketBeginAck endAck = new PacketBeginAck();
        networkLayer.sendPacket(ByteUtils.byteArrayToIntArray(endAck.serialize()));
    }

    public int receiveChunks(long timeout) {
        long start = System.currentTimeMillis();

        while (true) {
            Packet packet = receivePacket();

            if (packet != null && packet.getId() == Packet.ID_DATA_FRAGMENT) {
                PacketDataFragment packetData = (PacketDataFragment) packet;
                int index = packetData.getIndex();
                if (!receivedChunks.contains(index)) {
                    receivedChunks.add(index);
                    System.out.println("Received: " + receivedChunks.size() + " | Index: " + index);

                    //===== Adding to the File =====
                    System.arraycopy(packetData.getData(), 0, fileContents, index * chunkSize,
                            packetData.getData().length);
                    return 1;
                } else {
                    System.out.println("========> Same Packet Again <========");
                }
            }

            long elapsed = System.currentTimeMillis() - start;

            if (elapsed > timeout) {
                return 0;
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