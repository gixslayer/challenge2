package protocol;

import utils.ByteUtils;

public final class PacketCache {
    private final Integer[] beginTransfer;
    private final Integer[][] dataFragments;

    public PacketCache(PacketBeginTransfer pBeginTransfer, PacketDataFragment[] pDataFragments) {
        beginTransfer = ByteUtils.byteArrayToIntArray(pBeginTransfer.serialize());
        dataFragments = new Integer[pDataFragments.length][];

        for (int i = 0; i < pDataFragments.length; i++) {
            dataFragments[i] = ByteUtils.byteArrayToIntArray(pDataFragments[i].serialize());
        }
    }

    public Integer[] getBeginTransfer() {
        return beginTransfer;
    }

    public Integer[] getDataFragment(int index) {
        return dataFragments[index];
    }

    public Integer[][] getDataFragments() {
        return dataFragments;
    }
}
