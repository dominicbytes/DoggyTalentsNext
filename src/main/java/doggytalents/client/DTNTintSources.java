package doggytalents.client;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import doggytalents.DoggyBlocks;
import doggytalents.common.item.DoubleDyableAccessoryItem;
import doggytalents.common.util.Util;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public final class DTNTintSources {

    private DTNTintSources() {}

    record DoubleDyableForeground(int defaultColor) implements ItemTintSource {

        static final MapCodec<DoubleDyableForeground> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default")
                    .forGetter(DoubleDyableForeground::defaultColor)
            ).apply(builder, DoubleDyableForeground::new)
        );

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            return stack.getItem() instanceof DoubleDyableAccessoryItem item
                ? item.getFgColor(stack) : defaultColor;
        }

        @Override
        public MapCodec<DoubleDyableForeground> type() {
            return CODEC;
        }
    }

    record DoubleDyableBackground(int defaultColor) implements ItemTintSource {

        static final MapCodec<DoubleDyableBackground> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default")
                    .forGetter(DoubleDyableBackground::defaultColor)
            ).apply(builder, DoubleDyableBackground::new)
        );

        @Override
        public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
            return stack.getItem() instanceof DoubleDyableAccessoryItem item
                ? item.getBgColor(stack) : defaultColor;
        }

        @Override
        public MapCodec<DoubleDyableBackground> type() {
            return CODEC;
        }
    }

    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Util.getResource("double_dyable_fg"), DoubleDyableForeground.CODEC);
        event.register(Util.getResource("double_dyable_bg"), DoubleDyableBackground.CODEC);
    }

    public static void registerBlockTintSources(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(BlockTintSources.water()), DoggyBlocks.DOG_BATH.get());
    }
}
