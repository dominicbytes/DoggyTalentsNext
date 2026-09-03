package doggytalents.common.network;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;

public final class NetworkDecodeUtil {

    public static final int MAX_TRACKED_DOGS = 4_096;
    public static final int MAX_GROUPS = 1_024;
    public static final int MAX_DOG_SYNC_ENTRIES = 1_024;
    public static final int MAX_ARTIFACTS = 64;
    public static final int MAX_DIMENSIONS = 256;
    public static final int MAX_DOG_NAME_LENGTH = 256;

    private NetworkDecodeUtil() {}

    public static int readCollectionSize(FriendlyByteBuf buf, int maximum, String fieldName) {
        int size = buf.readInt();
        if (size < 0 || size > maximum) {
            throw new DecoderException(fieldName + " size " + size + " is outside 0.." + maximum);
        }
        return size;
    }

    public static void writeCollectionSize(FriendlyByteBuf buf, int size, int maximum, String fieldName) {
        if (size < 0 || size > maximum) {
            throw new EncoderException(fieldName + " size " + size + " is outside 0.." + maximum);
        }
        buf.writeInt(size);
    }
}
