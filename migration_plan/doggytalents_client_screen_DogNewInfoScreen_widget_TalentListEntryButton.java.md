# Migration Plan for `/home/aptd/DoggyTalentsNext/src/main/java/doggytalents/client/screen/DogNewInfoScreen/widget/TalentListEntryButton.java`

Total Errors: 3

## Error: TalentListEntryButton is not abstract and does not override abstract method extractContents(GuiGraphicsExtractor,int,int,float) in AbstractButton
- **Lines:** 23
- **Suggested Fix:** The `net.minecraft.client.gui.components.AbstractButton` class now has a new abstract method `extractContents` that must be implemented. The rendering logic from the old `renderWidget` method should be moved into this new method.

    **Concrete Change:**
    Implement the `extractContents` method and move the rendering logic from `renderWidget` into it.

    **Example:**
    ```java
    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float pTicks) {
        int cl = this.isHovered ? DEFAULT_HLCOLOR : DEFAULT_COLOR;
        int lvlcl = this.isHovered ? DEFAULT_LEVEL_HLCOLOR : DEFAULT_LEVEL_COLOR;
        
        //Ex : disable talent (or mutual talent)
        int talentMaxLvl = 0;
        float talentLvlPercent = 0;
        if (!DogUtil.playerCanTrainTalent(Minecraft.getInstance().player, talent)) {
            lvlcl = 0;
            cl = this.isHovered ? DEFAULT_INVALID_HLCOLOR : DEFAULT_INVALID_COLOR;
        } else {
            talentMaxLvl = this.talent.getMaxLevel();
            talentLvlPercent = ((float)this.dog.getDogLevel(talent))/((float)talentMaxLvl);
            if (talentLvlPercent >= 1) {
                lvlcl = this.isHovered ? DEFAULT_MAXLEVEL_HLCOLOR : DEFAULT_MAXLEVEL_COLOR;
            }
        
        }
        
        graphics.fill( this.getX(), this.getY(), this.getX()+this.width, this.getY()+this.height, cl);
        graphics.fill( this.getX(), this.getY(), this.getX()+Mth.ceil(this.width*talentLvlPercent), this.getY()+this.height, lvlcl);
        
        //draw text
        int mX = this.getX() + this.width/2;
        int mY = this.getY() + this.height/2;
        var msg = this.getMessage();
        if (!talent.isDogEligible(dog)) {
            msg = msg.copy().withStyle(
                msg.getStyle().withColor(0xff828282)
            );
        }
        if (this.selected) {
            msg = msg.copy().withStyle(
                msg.getStyle()
                .withUnderlined(true)
            );
        }
        int tX = mX - font.width(msg)/2;
        int tY = mY - font.lineHeight/2;
        //TODO if the name is too long, draw it cut off with a ..
        graphics.text(font, msg, tX, tY, 0xffffffff);
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 51
- **Suggested Fix:** The `onPress` method in `net.minecraft.client.gui.components.Button` (which `TalentListEntryButton` extends via `FlatButton` and `AbstractButton`) has changed its signature. It now takes an `InputWithModifiers` object.

    **Concrete Change:**
    Update the `onPress` method signature in `TalentListEntryButton`.

    **Example:**
    ```java
    // Add import:
    import net.minecraft.client.gui.InputWithModifiers;

    // Original:
    // @Override
    // public void onPress() {
    //     Store.get(screen).dispatch(ActiveTalentDescSlice.class,
    //         new UIAction(UIActionTypes.Talents.OPEN_DESC, new ActiveTalentDescSlice(this.talent))
    //     );
    // }

    // Proposed:
    @Override
    public void onPress(InputWithModifiers input) { // Changed signature
        Store.get(screen).dispatch(ActiveTalentDescSlice.class,
            new UIAction(UIActionTypes.Talents.OPEN_DESC, new ActiveTalentDescSlice(this.talent))
        );
    }
    ```

## Error: method does not override or implement a method from a supertype
- **Lines:** 58
- **Suggested Fix:** The `renderWidget` method is no longer the correct method to override for custom rendering in widgets that extend `AbstractWidget`. Its logic should be moved to `extractContents`.

    **Concrete Change:**
    Remove the `renderWidget` method entirely.