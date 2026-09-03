package doggytalents.client.entity.render.layer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

public class DogArmorHelmetAltModel {

    private final HumanoidModel<HumanoidRenderState> helmetModel;
    private final HumanoidModel<HumanoidRenderState> dummyModel;

    public DogArmorHelmetAltModel(EntityRendererProvider.Context ctx) {
        this.helmetModel = createHelmetModel(ctx);
        this.dummyModel = createHelmetModel(ctx);
    }

    private static HumanoidModel<HumanoidRenderState> createHelmetModel(EntityRendererProvider.Context ctx) {
        var model = new HumanoidModel<HumanoidRenderState>(ctx.bakeLayer(ModelLayers.PLAYER_ARMOR.head()));
        configureForHelmet(model);
        return model;
    }

    static void configureForHelmet(HumanoidModel<?> model) {
        model.allParts().forEach(part -> part.visible = false);
        model.root().visible = true;
        model.head.visible = true;
    }

    public Model getModel() {
        return helmetModel;
    }

    public HumanoidModel<?> getDummy() {
        return dummyModel;
    }

}
