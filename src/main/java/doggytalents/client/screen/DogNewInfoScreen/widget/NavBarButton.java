package doggytalents.client.screen.DogNewInfoScreen.widget;

import com.mojang.blaze3d.vertex.PoseStack;

import doggytalents.client.screen.DogNewInfoScreen.store.slice.ActiveTabSlice;
import doggytalents.client.screen.DogNewInfoScreen.store.slice.ActiveTabSlice.Tab;
import doggytalents.client.screen.framework.CommonUIActionTypes;
import doggytalents.client.screen.framework.Store;
import doggytalents.client.screen.framework.widget.TextOnlyButton;
import doggytalents.common.entity.Dog;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NavBarButton extends TextOnlyButton {
    private static final Logger LOGGER = LogManager.getLogger("DTN/NavBarButton");
    protected final Tab tab;
    private Screen screen;
    private Dog dog;

    public NavBarButton(int x, int y, Component text, Tab tab,
            Font font, Screen screen, Dog dog) {
        super(x, y, font.width(text), font.lineHeight, text, b -> {}, font);
        this.tab = tab;
        this.screen = screen;
        this.dog = dog;
    }

    @Override
    public void onPress(net.minecraft.client.input.InputWithModifiers input) {
        LOGGER.info("[DTN tab] NavBarButton pressed: tab={} isActive={}", tab,
            this.active);
        var store = Store.get(screen);
        LOGGER.info("[DTN tab] Store={} dispatching CHANGE_TAB to tab={}", store, tab);
        store.dispatchAll(
            ActiveTabSlice.UIActionCreator(dog, tab, CommonUIActionTypes.CHANGE_TAB)
        );
        LOGGER.info("[DTN tab] dispatch complete");
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {
    }
}
