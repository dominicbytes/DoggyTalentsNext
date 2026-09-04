package doggytalents.common.entity.anim;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import doggytalents.api.anim.DogAnimation;
import doggytalents.common.config.ConfigHandler;
import doggytalents.common.entity.Dog;
import doggytalents.common.entity.anim.DogAnimationManager.DogAnimDebugState.DogAnimDebugFreezeRot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public class DogAnimationManager {

    private static final int SYNC_INTERVAL_TICK = 7;
    private static final int MIN_VAL_MAX_LATENCY = 7;

    //Client
    public final DogAnimationState animationState
        = new DogAnimationState();

    //Common
    private boolean started = false;
    private int animationTime;
    private final Dog dog;
    private boolean looping = false;
    private boolean holdOnLastTick = false;
    private boolean isHolding = false;
    private int tickTillSync = 0;
    private int blendTick = 0;
    private int blendDuration = 0;
    private BlendState blendState = BlendState.NONE;
    public DogCapturedProceduralState capturedProcedural = DogCapturedProceduralState.NONE;
    public DogInterruptedAnimState capturedKeyframeAnim = DogInterruptedAnimState.NONE;

    private DogAnimation lastAnim = DogAnimation.NONE;
    private DogAnimation currentAnim = DogAnimation.NONE;

    //Common - Debug
    private boolean isDebug = false;

    public DogAnimationManager(Dog dog) { this.dog = dog; }

    public void onAnimationChange(DogAnimation anim) {
        this.lastAnim = this.currentAnim;
        this.currentAnim = anim;

        animationTime = 0;
        this.isHolding = false;
        this.blendTick = 0;
        this.blendDuration = 0;
        this.capturedProcedural = DogCapturedProceduralState.NONE;

        this.blendState = computeBlendState(this.lastAnim, this.currentAnim);
        if (!this.blendState.isNone()) {
            this.blendTick = 0;
            this.blendDuration = pickBlendDuration(
                this.lastAnim, this.currentAnim, this.blendState);
        }
        if (this.blendState.hasProceduralCapture()) {
            this.capturedProcedural = DogCapturedProceduralState.capture(dog);
        }
        if (this.blendState.hasAnimPoseCapture()) {
            this.capturedKeyframeAnim =
                new DogInterruptedAnimState(this.lastAnim, this.animationState.getAccumulatedTimeMillis());
        }

        if (anim != DogAnimation.NONE) {
            started = true;
            looping = anim.looping();
            holdOnLastTick = anim.holdOnLastTick();
            this.animationTime = anim.getLengthTicks();
            animationState.start(dog.tickCount);
            tickTillSync = SYNC_INTERVAL_TICK;
        } else {
            started = false;
            looping = false;
            animationState.stop();
        }
    }

    static BlendState computeBlendState(DogAnimation fromAnim, DogAnimation toAnim) {
        if (fromAnim.isNone() && toAnim.hasBlendIn())
            return BlendState.BLEND_IN;
        if (fromAnim.hasBlendOut() && toAnim.isNone())
            return BlendState.BLEND_OUT;
        return fromAnim.hasBlendOut() && toAnim.hasBlendIn() ?
            BlendState.ANIM_TO_ANIM : BlendState.NONE;
    }

    static int pickBlendDuration(
        DogAnimation fromAnim, DogAnimation toAnim, BlendState blendState) {
        return blendState == BlendState.BLEND_OUT ?
            fromAnim.blendOut().blendTick()
            : toAnim.blendIn().blendTick();
    }

    public void tick() {
        if (started && !this.dog.getAnim().freeHead()) {
            this.dog.yBodyRot = this.dog.yHeadRot;
            this.dog.resetBeggingRotation();
            if (!this.dog.getAnim().freeHeadXRotOnly()) {
                this.dog.setXRot(0);
                this.dog.xRotO = 0;
            }
        }
        if (started && (!this.dog.level().isClientSide()) && !looping) {
            if (this.animationTime <= 0) {
                this.animationTime = 0;
                if (this.holdOnLastTick)
                    this.isHolding = true;
                else
                    this.dog.setAnim(DogAnimation.NONE);
            } else {
                if (--tickTillSync <= 0) {
                    tickTillSync = SYNC_INTERVAL_TICK;
                    this.dog.setAnimSyncTime(this.animationTime);
                }
                --this.animationTime;
            }
        }
        if (started && (this.dog.level().isClientSide()) && !looping) {
            if (this.animationTime <= 0) {
                this.animationTime = 0;
                if (this.holdOnLastTick)
                    this.isHolding = true;
            } else {
                --this.animationTime;
            }
        }

        if (this.blendTick < this.blendDuration) {
            this.blendTick++;
        }

        if (isDebug)
            tickDebug();
    }

    public void onSyncTimeUpdated() {
        if (this.dog.level().isClientSide()) {
            int sync_time = this.dog.getAnimSyncTime();
            resolveLatencyIfNeeded(sync_time);
        }

    }

    private void resolveLatencyIfNeeded(int syncTime) {
        if (syncTime < 0)
            return;
        if (!started || looping)
            return;
        
        int configMaxLatency = ConfigHandler.CLIENT.MAX_ANIMATION_LATENCY_ALLOWED.get();
        if (configMaxLatency < 0)
            return;
        int maxLatencyAllowed = MIN_VAL_MAX_LATENCY;
        if (configMaxLatency > MIN_VAL_MAX_LATENCY)
            maxLatencyAllowed = configMaxLatency;

        int correctTime = syncTime;
        int currentTime = this.animationTime;
        int latencyAbs = Mth.abs(correctTime - currentTime);
        if (latencyAbs <= maxLatencyAllowed)
            return;
            
        var anim = dog.getAnim();
        this.animationTime = Mth.clamp(correctTime, 0, anim.getLengthTicks());
        int correctPassedTime = 
            anim.getLengthTicks() - this.animationTime;
        
        this.blendDuration = 0; this.blendTick = 0;
        animationState.resolveLatency(dog.tickCount, 
            correctPassedTime, 
            anim.getSpeedModifier());
    }

    public float getBlendInProgress(float partialTicks) {
        if (this.blendDuration <= 0 || this.blendTick >= this.blendDuration) return 1.0f;
        float ret = Mth.clamp((this.blendTick + partialTicks) / (float) this.blendDuration, 0.0f, 1.0f);
        return Mth.equal(ret, 1) ? 1 : ret;
    }

    public float getBlendOutProgress(float pticks) {
        float ret = 1 - getBlendInProgress(pticks);
        ret = Mth.clamp(ret, 0, 1);
        return Mth.equal(ret, 0) ? 0 : ret;
    }

    public boolean playingFullAnim(float pticks) {
        final var anim = this.dog.getAnim();
        return !anim.isNone() && (
            anim.blendIn().isNone()
            || getBlendInProgress(pticks) >= 1
        );
    }

    public BlendState getBlendState(float pticks) {
        if (this.blendState.isNone())
            return this.blendState;
        if (this.blendState == BlendState.BLEND_OUT)
            return getBlendOutProgress(pticks) > 0 ?
                this.blendState : BlendState.NONE;
        return getBlendInProgress(pticks) < 1 ?
            this.blendState : BlendState.NONE;
    }

    public boolean started() {
        return this.started;
    }

    public boolean isHolding() {
        return this.isHolding;
    }

    public void save(CompoundTag tag) {
        var debug_state = dog.getDogAnimDebugState();
        if (debug_state.isNone())
            return;
        var debug_tag = new CompoundTag();
        debug_tag.putInt("anim_id", debug_state.anim().getId());
        debug_tag.putInt("timestamp", debug_state.timestamp());
        debug_tag.put("rot_state", debug_state.rotState().encode());
        tag.put("dtnDogAnimDebug", debug_tag);
    }

    public void load(CompoundTag tag) {
        if (!tag.contains("dtnDogAnimDebug"))
            return;
        var debug_tag = tag.getCompoundOrEmpty("dtnDogAnimDebug");
        int anim_id = debug_tag.getIntOr("anim_id", 0);
        int timestamp = debug_tag.getIntOr("timestamp", 0);
        DogAnimDebugFreezeRot rotState;
        if (debug_tag.contains("yrot")) {
            rotState = DogAnimDebugFreezeRot.decodeLegacy(debug_tag);
        } else {
            rotState = DogAnimDebugFreezeRot.decode(debug_tag.getCompoundOrEmpty("rot_state"));
        }
        var debug_state = DogAnimDebugState.of(anim_id, timestamp, rotState);
        setDogAnimDebugState(debug_state);
    }

    private void tickDebug() {
        final var rotState = dog.getDogAnimDebugState().rotState();
        dog.yBodyRot = rotState.yRot();
        dog.yHeadRot = Mth.rotateIfNecessary(rotState.headYRot(), dog.yBodyRot, dog.getMaxHeadYRot());
        dog.setYRot(dog.yBodyRot);
        dog.setXRot(rotState.headXRot());
        dog.yBodyRotO = dog.yBodyRot;
        dog.yHeadRotO = dog.yHeadRot;
        dog.yRotO = dog.getYRot();
    }

    public void onDebugUpdate(DogAnimDebugState state) {
        this.isDebug = !state.isNone();
    }

    public void setDogAnimDebugState(DogAnimDebugState state) {
        var old_state = dog.getDogAnimDebugState();
        dog.setDogAnimDebugState(state);
        if (state.isNone() && !old_state.isNone())
            dog.setAnim(DogAnimation.NONE);
        if (!state.isNone() && old_state.isNone())
            dog.dogAi.forceStopAllGoal();
    }

    public void setDebugFreezeRot(DogAnimDebugFreezeRot rotation) {
        var current_state = dog.getDogAnimDebugState();
        setDogAnimDebugState(DogAnimDebugState.of(current_state.anim(), 
            current_state.timestamp(), rotation));
    }

    public DogAnimDebugState getFreezeDebugState(DogAnimation anim) {
        int timestamp = anim.getLengthTicks() - this.animationTime;
        timestamp = Mth.clamp(timestamp, 0, anim.getLengthTicks());
        var current_state = dog.getDogAnimDebugState();
        return DogAnimDebugState.of(anim, timestamp, current_state.rotState());
    }

    public static record DogCapturedProceduralState(
        float headXRot, DogPose pose,
        float shakeAnim, float begAnim
    ) {
        public static final DogCapturedProceduralState NONE =
            new DogCapturedProceduralState(0, DogPose.STAND, 0, 0);

        public static DogCapturedProceduralState capture(Dog dog) {
            final float xrot = Mth.wrapDegrees(dog.getXRot());
            return new DogCapturedProceduralState(
                xrot, dog.getDogPose(),
                dog.getDogClassicalShakeAnim(1), dog.getDogClassicalBegAnim(1)
            );
        }

        public boolean isNone() {
            return this == NONE;
        }
    }

    public static record DogInterruptedAnimState(DogAnimation anim, long timestampMillis) {
        public static final DogInterruptedAnimState NONE =
            new DogInterruptedAnimState(DogAnimation.NONE, 0);
        public boolean isNone() {
            return this == NONE;
        }
    }

    public static enum BlendState {
        NONE, ANIM_TO_ANIM, BLEND_IN, BLEND_OUT;

        public boolean isNone() {
            return this == NONE;
        }

        public boolean hasProceduralCapture() {
            return this == BLEND_IN || this == ANIM_TO_ANIM;
        }

        public boolean hasAnimPoseCapture() {
            return this == ANIM_TO_ANIM || this == BLEND_OUT;
        }
    }

    public static class DogAnimDebugState {
        
        public static final DogAnimDebugState NONE = new DogAnimDebugState();

        private DogAnimation anim = DogAnimation.NONE;
        private int timestamp = 0;
        private DogAnimDebugFreezeRot rotState = DogAnimDebugFreezeRot.DEFAULT;
    
        private DogAnimDebugState() {
            this.anim = DogAnimation.NONE;
            this.timestamp = 0;
            this.rotState = DogAnimDebugFreezeRot.DEFAULT;
        }

        private DogAnimDebugState(DogAnimation anim, int timestamp, DogAnimDebugFreezeRot rotation) {
            this.anim = anim;
            this.timestamp = timestamp;
            this.rotState = rotation;
        }

        public static DogAnimDebugState of(int animId, int timestamp, DogAnimDebugFreezeRot rotation) {
            var anim = DogAnimation.byId(animId);
            return of(anim, timestamp, rotation);
        }

        public static DogAnimDebugState of(DogAnimation anim, int timestamp, DogAnimDebugFreezeRot rotation) {
            if (anim == null || anim.isNone())
                return NONE;
            var ret = new DogAnimDebugState(anim, timestamp, rotation);
            if (ret.isNone())
                return NONE;
            return ret;
        }

        public boolean isNone() {
            if (this == NONE)
                return true;
            return this.anim.isNone();
        }

        public DogAnimation anim() {
            return this.anim;
        }

        public int timestamp() {
            return this.timestamp;
        }

        public DogAnimDebugFreezeRot rotState() {
            return this.rotState;
        }

        @Override
        public boolean equals(Object obj) {
            if (this.isNone() && obj == NONE)
                return true;
            if (!(obj instanceof DogAnimDebugState other))
                return false;
            if (this.isNone() && other.isNone())
                return true;
            return
                this.anim == other.anim
                && this.timestamp == other.timestamp
                && this.rotState.equals(other.rotState);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.anim.getId(), this.timestamp, this.rotState);
        }

        public static record DogAnimDebugFreezeRot(
            float yRot, float headYRot, float headXRot, float banking, float tailXRot
        ) {
            public static final DogAnimDebugFreezeRot DEFAULT =
                new DogAnimDebugFreezeRot(0, 0, 0, 0, 0);

            public static final Codec<DogAnimDebugFreezeRot> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                    Codec.FLOAT.optionalFieldOf("yRot", DEFAULT.yRot()).forGetter(DogAnimDebugFreezeRot::yRot),
                    Codec.FLOAT.optionalFieldOf("headYRot", DEFAULT.headYRot()).forGetter(DogAnimDebugFreezeRot::headYRot),
                    Codec.FLOAT.optionalFieldOf("headXRot", DEFAULT.headXRot()).forGetter(DogAnimDebugFreezeRot::headXRot),
                    Codec.FLOAT.optionalFieldOf("banking", DEFAULT.banking()).forGetter(DogAnimDebugFreezeRot::banking),
                    Codec.FLOAT.optionalFieldOf("tailXRot", DEFAULT.tailXRot()).forGetter(DogAnimDebugFreezeRot::tailXRot)
                ).apply(builder, DogAnimDebugFreezeRot::new));

            public DogAnimDebugFreezeRot withYRot(float value) {
                return new DogAnimDebugFreezeRot(value, headYRot, headXRot, banking, tailXRot);
            }

            public DogAnimDebugFreezeRot withYHeadRot(float value) {
                return new DogAnimDebugFreezeRot(yRot, value, headXRot, banking, tailXRot);
            }

            public DogAnimDebugFreezeRot withXHeadRot(float value) {
                return new DogAnimDebugFreezeRot(yRot, headYRot, value, banking, tailXRot);
            }

            public DogAnimDebugFreezeRot withBanking(float value) {
                return new DogAnimDebugFreezeRot(yRot, headYRot, headXRot, value, tailXRot);
            }

            public DogAnimDebugFreezeRot withTailXRot(float value) {
                return new DogAnimDebugFreezeRot(yRot, headYRot, headXRot, banking, value);
            }

            public Tag encode() {
                return CODEC.encodeStart(NbtOps.INSTANCE, this).result().orElseGet(CompoundTag::new);
            }

            public static DogAnimDebugFreezeRot decode(Tag tag) {
                return CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, tag)).result().orElse(DEFAULT);
            }

            public static DogAnimDebugFreezeRot decodeLegacy(CompoundTag tag) {
                return DEFAULT.withYRot(tag.getFloatOr("yrot", 0));
            }

            public void encodeNetwork(FriendlyByteBuf buf) {
                buf.writeFloat(yRot);
                buf.writeFloat(headYRot);
                buf.writeFloat(headXRot);
                buf.writeFloat(banking);
                buf.writeFloat(tailXRot);
            }

            public static DogAnimDebugFreezeRot decodeNetwork(FriendlyByteBuf buf) {
                return new DogAnimDebugFreezeRot(
                    buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
            }
        }

    }

}
//emgniypocpots_redrehtac
