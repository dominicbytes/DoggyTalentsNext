package doggytalents.common.entity.serializers;

import doggytalents.common.network.NetworkDecodeUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;

public class Dim2BlockPosSerializer extends DogSerializer<Dimension2BlockPosMap> {

    @Override
    public void write(FriendlyByteBuf buf, Dimension2BlockPosMap value) {
        NetworkDecodeUtil.writeCollectionSize(buf, value.size(), NetworkDecodeUtil.MAX_DIMENSIONS, "dog dimension positions");
        for (var entry : value.entrySet()) {
            buf.writeIdentifier(entry.getKey().identifier());
            EntityDataSerializers.BLOCK_POS.codec().encode((RegistryFriendlyByteBuf) buf, entry.getValue());
        }
    }

    @Override
    public Dimension2BlockPosMap read(FriendlyByteBuf buf) {
        int size = NetworkDecodeUtil.readCollectionSize(buf, NetworkDecodeUtil.MAX_DIMENSIONS, "dog dimension positions");
        var value = new Dimension2BlockPosMap();
        for (int i = 0; i < size; i++) {
            var loc = buf.readIdentifier();
            var pos = EntityDataSerializers.BLOCK_POS.codec().decode((RegistryFriendlyByteBuf)buf);
            
            var res_key = ResourceKey.create(Registries.DIMENSION, loc);
            value.put(res_key, pos);
        }

        return value;
    }

    @Override
    public Dimension2BlockPosMap copy(Dimension2BlockPosMap value) {
        return value.copy();
    }
}
