package protocol;

import client.NetworkLayer;

public final class ReqResponder {
    private NetworkLayer networkLayer;
    private Integer[][] packetCache;
    private long[] lastRequested;

    public ReqResponder(NetworkLayer networkLayer, Integer[][] packetCache) {
        this.networkLayer = networkLayer;
        this.packetCache = packetCache;
        this.lastRequested = new long[packetCache.length];
    }

    public void run() {
        // Threshold in ms which must be reached before a specific fragment is transmitted again.
        // This prevents spamming the receiver with the same fragment too fast.
        long threshold = 100;
        // The sleep time is the time this thread will sleep before attempting to send another packet.
        // This provides flow control (bytes per second ~ chunkSize * 1000/sleepTime).
        int sleepTime = 200;

        while (true) {
            int requestedIndex = receiveRequest();

            if (requestedIndex == -1) {
                // No packet received.
                // Short sleep before attempting to receive the next packet.
                sleep(10);
                continue;
            } else if (requestedIndex == -2) {
                // Receiver has send a packet indicating all fragments have been received.
                // Break from the infinite while loop.
                System.out.println("COMPLETED");

                break;
            }

            long now = System.currentTimeMillis();
            if (now - lastRequested[requestedIndex] >= threshold) {
                networkLayer.sendPacket(packetCache[requestedIndex]);

                System.out.println("SEND " + requestedIndex);

                lastRequested[requestedIndex] = now;

                // Flow control to prevent congestion.
                sleep(sleepTime);
            } else {
                // Time passed between now and the last send was lower than the threshold.
                System.out.println("IGNORED " + requestedIndex);
            }
        }
    }

    private int receiveRequest() {
        Integer[] data = networkLayer.receivePacket();

        if (data == null) {
            return -1;
        }

        Packet packet = Packet.fromData(data);

        if (packet.getId() == Packet.ID_REQUEST_FRAGMENT) {
            return ((PacketRequestFragment) packet).getIndex();
        } else if (packet.getId() == Packet.ID_BEGIN_ACK) {
            return -2;
        }

        return -1;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
