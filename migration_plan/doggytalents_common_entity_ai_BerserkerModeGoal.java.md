# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/entity/ai/BerserkerModeGoal.java`

Total Errors: 1

## Error: no suitable constructor found for NearestAttackableTargetGoal(Dog,Class<Mob>,boolean,(e)->{ if [...]ue; })
- **Lines:** 22
- **Suggested Fix:** The constructor for `net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal` has changed. The `Predicate<LivingEntity>` parameter is now expected to be a `TargetingConditions.Selector`.

    **Concrete Change:**
    Cast the lambda expression to `TargetingConditions.Selector`.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.world.entity.ai.targeting.TargetingConditions;

    // Original:
    // super(dog, Mob.class, false , (e) -> {
    //     if (targetingOwnerCheck(dog, e))
    //         return true;
    //     if (!(e instanceof Enemy)) return false;
    //
    //     return true;
    // });
    // Proposed:
    super(dog, Mob.class, false , (TargetingConditions.Selector) (e) -> { // Cast to TargetingConditions.Selector
        if (targetingOwnerCheck(dog, e))
            return true;
        if (!(e instanceof Enemy)) return false;
        
        return true;
    });
    ```