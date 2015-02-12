package protocol;

import client.NetworkLayer;
import client.Utils;

public final class CustomSender implements IRDTProtocol {
    private final int chunkSize = 128;
    private NetworkLayer networkLayer;

    @Override
    public void TimeoutElapsed(Object tag) {
        // TODO Auto-generated method stub

    }

    @Override
    public void run() {
        Integer[] intFileContents = Utils.getFileContents();

    }

    @Override
    public void setNetworkLayer(NetworkLayer networkLayer) {
        this.networkLayer = networkLayer;
    }

}
