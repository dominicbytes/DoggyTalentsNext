# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/common/entity/ai/DogNearestToOwnerAttackableTargetGoal.java`

Total Errors: 2

## Error: cannot find symbol
- **Lines:** 34
- **Suggested Fix:** The constructor for `net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal` has changed. It now expects a `TargetingConditions.Selector` for the predicate parameter.

    **Concrete Change:**
    Update the `super` constructor call to match the new `NearestAttackableTargetGoal` API.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.world.entity.ai.targeting.TargetingConditions;

    // Original:
    // public DogNearestToOwnerAttackableTargetGoal(Dog dog, Class<T> type, boolean p_26062_) {
    //     super(dog, type ,p_26062_);
    //     this.dog = dog;
    // }

    // Proposed:
    public DogNearestToOwnerAttackableTargetGoal(Dog dog, Class<T> type, boolean p_26062_) {
        super(dog, type ,p_26062_, (TargetingConditions.Selector) (e) -> { return true; }); // Added TargetingConditions.Selector
        this.dog = dog;
    }
    ```
    Note: The `(e) -> { return true; }` is a placeholder predicate. The actual predicate logic might need to be more specific based on the original intent.

## Error: no suitable method found for getNearestPlayer(TargetingConditions,Dog,double,double,double)
- **Lines:** 38
- **Suggested Fix:** The `getNearestPlayer` method in `net.minecraft.world.level.Level` (which `owner.level()` returns) no longer takes `TargetingConditions` as its first parameter. It now expects `double x, double y, double z, double maxDist, Predicate<Entity> predicate`.

    **Concrete Change:**
    Update the `getNearestPlayer` method call to match the new `Level` API.

    **Example:**
    ```java
    // Original:
    // this.target = owner.level().getNearestPlayer(this.targetConditions, this.dog, owner.getX(), owner.getEyeY(), owner.getZ());
    // Proposed:
    this.target = owner.level().getNearestPlayer(owner.getX(), owner.getEyeY(), owner.getZ(), this.getFollowDistance(), (e) -> this.targetConditions.test(owner.level(), this.dog, e));
    ```
    Note: This uses the `getNearestPlayer` overload that takes a `Predicate<Entity>`. The `targetConditions` object needs to be adapted to this predicate.