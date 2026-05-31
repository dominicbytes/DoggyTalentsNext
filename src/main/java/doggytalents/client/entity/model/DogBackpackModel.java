package doggytalents.client.entity.model;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import doggytalents.common.entity.Dog;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DogBackpackModel extends SyncedAccessoryModel {

    public DogBackpackModel(ModelPart box) {
        super(box);
    }

    @Override
    protected void populatePart(ModelPart box) {
        this.body = Optional.of(box.getChild("body"));
    }

    public static LayerDefinition createChestLayer() {
        MeshDefinition var0 = new MeshDefinition();
        PartDefinition var1 = var0.getRoot();

        var var2 = var1.addOrReplaceChild("body", CubeListBuilder.create()
                , PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5707964F, 0.0F, 0.0F));

        // Bags positioned outside the dog body (which extends to x=±3); +0.01 avoids Z-fighting
        var2.addOrReplaceChild("right_chest", CubeListBuilder.create().texOffs(48, 0).addBox(3.01F, -1F, 0F, 4, 7, 4), PartPose.ZERO);
        var2.addOrReplaceChild("left_chest", CubeListBuilder.create().texOffs(48, 0).addBox(-7.01F, -1F, 0F, 4F, 7F, 4F), PartPose.ZERO);

        return LayerDefinition.create(var0, 64, 32);
    }
}
