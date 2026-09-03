package doggytalents.common.network;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import doggytalents.common.entity.Dog;
import doggytalents.common.network.DTNNetworkHandler.NetworkEvent.Context;
import doggytalents.common.network.packet.DogPacket;
import doggytalents.common.network.packet.data.DogData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.neoforge.network.handling.IPayloadContext;

class DogPacketDirectionTest {

    @Test
    void clientboundDogMutationIsRejectedBeforeWorkIsQueued() {
        var workQueued = new AtomicBoolean();
        var payloadContext = (IPayloadContext) Proxy.newProxyInstance(
            IPayloadContext.class.getClassLoader(),
            new Class<?>[] { IPayloadContext.class },
            (proxy, method, args) -> {
                if (method.getName().equals("flow")) {
                    return PacketFlow.CLIENTBOUND;
                }
                if (method.getName().equals("enqueueWork")) {
                    workQueued.set(true);
                }
                return null;
            });

        var packet = new TestDogPacket();
        packet.handle(new DogData(42), () -> new Context(payloadContext));

        assertFalse(workQueued.get());
    }

    private static final class TestDogPacket extends DogPacket<DogData> {
        @Override
        public DogData decode(FriendlyByteBuf buf) {
            return new DogData(buf.readInt());
        }

        @Override
        public void handleDog(Dog dog, DogData data, Supplier<Context> ctx) {
            throw new AssertionError("Wrong-direction packet reached the dog handler");
        }
    }
}
