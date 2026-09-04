package doggytalents.common.entity;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

/** Vanilla's entity-specific attribute rename does not visit the custom DTN entity ID. */
final class LegacyDogAttributes {
    private LegacyDogAttributes() {
    }

    @SuppressWarnings("deprecation")
    static ValueInput upgrade(ValueInput input, HolderLookup.Provider registries, ProblemReporter reporter) {
        var attributes = input.read("attributes", CompoundTag.CODEC.listOf());
        if (attributes.isEmpty() || attributes.get().stream().noneMatch(LegacyDogAttributes::hasOldId)) {
            return input;
        }
        var tag = input.read(MapCodec.assumeMapUnsafe(CompoundTag.CODEC)).orElseThrow().copy();
        for (var entry : tag.getListOrEmpty("attributes")) {
            if (!(entry instanceof CompoundTag attribute)) continue;
            String oldId = attribute.getStringOr("id", "");
            String newId = oldId.replaceFirst("^minecraft:(generic|horse|player|zombie)\\.", "minecraft:");
            var id = Identifier.tryParse(newId);
            if (!newId.equals(oldId) && id != null && BuiltInRegistries.ATTRIBUTE.containsKey(id)) {
                attribute.putString("id", newId);
            }
        }
        return TagValueInput.create(reporter, registries, tag);
    }

    private static boolean hasOldId(CompoundTag attribute) {
        return attribute.getStringOr("id", "").matches("minecraft:(generic|horse|player|zombie)\\..+");
    }
}
