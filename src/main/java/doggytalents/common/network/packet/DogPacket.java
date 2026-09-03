package doggytalents.common.network.packet;

import doggytalents.common.entity.Dog;
import doggytalents.common.network.IPacket;
import doggytalents.common.network.packet.data.DogData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import doggytalents.common.network.DTNNetworkHandler.NetworkEvent.Context;

import java.util.function.Supplier;

public abstract class DogPacket<T extends DogData> implements IPacket<T> {

    @Override
    public void encode(T data, FriendlyByteBuf buf) {
        buf.writeInt(data.entityId);
    }

    @Override
    public abstract T decode(FriendlyByteBuf buf);

    @Override
    public final void handle(T data, Supplier<Context> ctx) {
        var context = ctx.get();
        if (!context.isServerRecipent()) {
            context.setPacketHandled(true);
            return;
        }

        context.enqueueWork(() -> {
            Entity target = context.getSender().level().getEntity(data.entityId);

            if (!(target instanceof Dog)) {
                return;
            }

            this.handleDog((Dog) target, data, ctx);
        });

        context.setPacketHandled(true);
    }

    public abstract void handleDog(Dog dogIn, T data, Supplier<Context> ctx);

}
