package doggytalents.common.talent;

import java.util.ArrayList;

import doggytalents.DoggyTalents;
import doggytalents.api.registry.Talent;
import doggytalents.api.registry.TalentInstance;
import doggytalents.common.entity.Dog;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

public class HunterDogTalent extends TalentInstance {

    public HunterDogTalent(Talent talentIn, int levelIn) {
        super(talentIn, levelIn);
    }

    public static void onLootDrop(final LivingDropsEvent event) {
        var source = event.getSource();
        if (source == null) return;
        var attacker = source.getEntity();
        if (!(attacker instanceof Dog dog)) return;

        int level = dog.getDogLevel(DoggyTalents.HUNTER_DOG);
        if (level <= 0) return;

        // 10% chance per level; level 5 gets an extra 10% (60% total)
        int chance = level + (level >= 5 ? 1 : 0);
        if (dog.getRandom().nextInt(10) >= chance) return;

        var originalDrops = new ArrayList<>(event.getDrops());
        for (ItemEntity itemEntity : originalDrops) {
            var copy = new ItemEntity(
                itemEntity.level(),
                itemEntity.getX(),
                itemEntity.getY(),
                itemEntity.getZ(),
                itemEntity.getItem().copy()
            );
            event.getDrops().add(copy);
        }
    }
}
