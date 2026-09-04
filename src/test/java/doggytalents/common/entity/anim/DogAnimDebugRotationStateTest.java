package doggytalents.common.entity.anim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import doggytalents.api.anim.DogAnimation;
import doggytalents.common.entity.anim.DogAnimationManager.DogAnimDebugState;
import doggytalents.common.entity.anim.DogAnimationManager.DogAnimDebugState.DogAnimDebugFreezeRot;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

class DogAnimDebugRotationStateTest {

    @Test
    void animDebugRot01PreservesEveryFrozenRotationChannel() {
        var rotations = DogAnimDebugFreezeRot.DEFAULT
            .withYRot(10)
            .withYHeadRot(20)
            .withXHeadRot(30)
            .withBanking(0.5f)
            .withTailXRot(40);

        assertEquals(new DogAnimDebugFreezeRot(10, 20, 30, 0.5f, 40), rotations);
        assertEquals(rotations, DogAnimDebugFreezeRot.decode(rotations.encode()));
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        rotations.encodeNetwork(buffer);
        assertEquals(rotations, DogAnimDebugFreezeRot.decodeNetwork(buffer));
        assertEquals(rotations, DogAnimDebugState.of(DogAnimation.BACKFLIP, 7, rotations).rotState());
    }

    @Test
    void animDebugRot01LoadsLegacyYRotationValue() {
        var legacy = new CompoundTag();
        legacy.putFloat("yrot", 35);

        assertEquals(35, DogAnimDebugFreezeRot.decodeLegacy(legacy).yRot());
        assertEquals(0, DogAnimDebugFreezeRot.decodeLegacy(legacy).headYRot());
    }
}
