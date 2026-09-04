package doggytalents.common.item;

import java.util.function.Supplier;

import doggytalents.api.registry.Accessory;

public class SnorkelAccessoryItem extends AccessoryItem{

    public SnorkelAccessoryItem(Supplier<? extends Accessory> type, Properties properties) {
        super(type, properties);
    }
}
