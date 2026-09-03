package doggytalents.common.network;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import doggytalents.common.entity.serializers.Dim2BlockPosSerializer;
import doggytalents.common.entity.serializers.DoggyArtifactsSerializer;
import doggytalents.common.network.packet.CanineTrackerPackets;
import doggytalents.common.network.packet.ConductingBonePackets;
import doggytalents.common.network.packet.DogGroupPackets;
import doggytalents.common.network.packet.DogSyncDataPacket;
import doggytalents.common.network.packet.HeelByGroupPackets;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;

class PacketCollectionBoundsTest {

    @Test
    void rejectsOversizedDogListsBeforeAllocating() {
        assertThrows(DecoderException.class,
            () -> new CanineTrackerPackets.ResponseDogsPackets().decode(sizeOnly(Integer.MAX_VALUE)));
        assertThrows(DecoderException.class,
            () -> new ConductingBonePackets.ResponseDogsPackets().decode(sizeOnly(Integer.MAX_VALUE)));
    }

    @Test
    void rejectsOversizedGroupListsBeforeAllocating() {
        var dogGroups = new FriendlyByteBuf(Unpooled.buffer());
        dogGroups.writeInt(42);
        dogGroups.writeInt(Integer.MAX_VALUE);

        assertThrows(DecoderException.class,
            () -> new DogGroupPackets.UPDATE().decode(dogGroups));
        assertThrows(DecoderException.class,
            () -> new HeelByGroupPackets.RESPONSE_GROUP_LIST().decode(sizeOnly(Integer.MAX_VALUE)));
    }

    @Test
    void rejectsInvalidDogSyncStateAndCollectionSize() {
        var invalidState = new FriendlyByteBuf(Unpooled.buffer());
        invalidState.writeInt(42);
        invalidState.writeInt(Integer.MAX_VALUE);

        var negativeTalents = new FriendlyByteBuf(Unpooled.buffer());
        negativeTalents.writeInt(42);
        negativeTalents.writeInt(DogSyncDataPacket.ReadState.TALENTS.getId());
        negativeTalents.writeInt(-1);

        var packet = new DogSyncDataPacket();
        assertThrows(DecoderException.class, () -> packet.decode(invalidState));
        assertThrows(DecoderException.class, () -> packet.decode(negativeTalents));
    }

    @Test
    void rejectsOversizedEntityDataCollectionsBeforeAllocating() {
        assertThrows(DecoderException.class,
            () -> new DoggyArtifactsSerializer().read(sizeOnly(Integer.MAX_VALUE)));
        assertThrows(DecoderException.class,
            () -> new Dim2BlockPosSerializer().read(sizeOnly(Integer.MAX_VALUE)));
    }

    private static FriendlyByteBuf sizeOnly(int size) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(size);
        return buf;
    }
}
