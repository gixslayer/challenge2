package protocol;

import client.NetworkLayer;

public final class SendWindow {
    private int lastAcked;
    private int windowSize;
    private NetworkLayer networkLayer;
    private Integer[][] packetCache;
    private boolean[] ackedList;
    private int chunks;

    public SendWindow(int windowSize, NetworkLayer networkLayer, PacketCache packetCache) {
        this.lastAcked = 0;
        this.windowSize = windowSize;
        this.networkLayer = networkLayer;
        this.packetCache = packetCache.getDataFragments();
        this.chunks = this.packetCache.length;
        this.ackedList = new boolean[chunks];
    }

    public void send() {
        int remaining = chunks;
        int maxSend = 8;
        long delay = 1500;

        while (remaining != 0) {
            int numberSend = 0;

            for (int i = 0; i < chunks && numberSend < maxSend; i++) {
                if (ackedList[i]) {
                    continue;
                }

                System.out.println("SENDING " + i);

                networkLayer.sendPacket(packetCache[i]);
                numberSend++;

                try {
                    Thread.sleep(125);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            remaining -= receiveAcks(delay);

            System.out.println("REMAINING " + remaining);

            /* int ack = receiveAck(500);

             if (ack != -1 && !ackedList[ack]) {
                 System.out.println("ACKED " + ack);

                 ackedList[ack] = true;
                 remaining--;
             }*/
        }
    }

    private int receiveAcks(long timeout) {
        long start = System.currentTimeMillis();
        int acked = 0;

        while (true) {
            Packet packet = receivePacket();

            if (packet != null && packet.getId() == Packet.ID_ACK) {
                PacketAck packetAck = (PacketAck) packet;
                int ack = packetAck.getAckId();

                if (ack != -1 && !ackedList[ack]) {
                    System.out.println("ACKED " + ack);

                    ackedList[ack] = true;
                    acked++;
                }
            }

            long elapsed = System.currentTimeMillis() - start;

            if (elapsed > timeout) {
                break;
            }
        }

        return acked;
    }

    private int receiveAck(long timeout) {
        long start = System.currentTimeMillis();

        while (true) {
            Packet packet = receivePacket();

            if (packet != null && packet.getId() == Packet.ID_ACK) {
                PacketAck packetAck = (PacketAck) packet;

                return packetAck.getAckId();
            }

            long elapsed = System.currentTimeMillis() - start;

            if (elapsed > timeout) {
                return -1;
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
