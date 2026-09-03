package doggytalents.common.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;

class NetworkDecodeUtilTest {

    @Test
    void acceptsBoundaryCollectionSizes() {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(0);
        buf.writeInt(3);

        assertEquals(0, NetworkDecodeUtil.readCollectionSize(buf, 3, "test"));
        assertEquals(3, NetworkDecodeUtil.readCollectionSize(buf, 3, "test"));
    }

    @Test
    void rejectsNegativeCollectionSize() {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(-1);

        assertThrows(DecoderException.class,
            () -> NetworkDecodeUtil.readCollectionSize(buf, 3, "test"));
    }

    @Test
    void rejectsOversizedCollection() {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(4);

        assertThrows(DecoderException.class,
            () -> NetworkDecodeUtil.readCollectionSize(buf, 3, "test"));
    }

    @Test
    void refusesToEncodeOversizedCollection() {
        var buf = new FriendlyByteBuf(Unpooled.buffer());

        assertThrows(EncoderException.class,
            () -> NetworkDecodeUtil.writeCollectionSize(buf, 4, 3, "test"));
        assertEquals(0, buf.writerIndex());
    }
}
