package doggytalents.client.entity.model.dog;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import doggytalents.client.entity.model.SyncedAccessoryModel;
import doggytalents.client.entity.render.DogRenderState;
import net.minecraft.client.model.geom.ModelPart;

class DogModelBabyStateTest {

    @Test
    void scalesDefaultModelHeadOnlyForBabies() {
        var model = new TestDogModel(dogRoot());
        var state = new DogRenderState();

        model.setupAnim(state);
        assertFalse(model.scalesBabyHead());

        state.isBaby = true;
        model.setupAnim(state);
        assertTrue(model.scalesBabyHead());
    }

    @Test
    void respectsCustomModelBabyScalingOptOut() {
        var model = new TestDogModel(dogRoot()) {
            @Override
            public boolean scaleBabyDog() {
                return false;
            }
        };
        var state = new DogRenderState();
        state.isBaby = true;

        model.setupAnim(state);

        assertFalse(model.scalesBabyHead());
    }

    @Test
    void copiesBabyStateToAccessoryModels() {
        var dogModel = new TestDogModel(dogRoot());
        var state = new DogRenderState();
        state.isBaby = true;
        dogModel.setupAnim(state);
        var accessory = new TestAccessoryModel(accessoryRoot());

        dogModel.copyPropertiesTo(accessory);

        assertTrue(accessory.hasBabyHead());
    }

    private static ModelPart dogRoot() {
        var realHead = part();
        var head = part(Map.of("real_head", realHead));
        var realTail = part();
        var tail = part(Map.of("real_tail", realTail));
        return part(Map.of(
            "head", head,
            "body", part(),
            "upper_body", part(),
            "right_hind_leg", part(),
            "left_hind_leg", part(),
            "right_front_leg", part(),
            "left_front_leg", part(),
            "tail", tail));
    }

    private static ModelPart accessoryRoot() {
        return part(Map.of("head", part()));
    }

    private static ModelPart part() {
        return part(Map.of());
    }

    private static ModelPart part(Map<String, ModelPart> children) {
        return new ModelPart(List.of(), children);
    }

    private static class TestDogModel extends DogModel {
        TestDogModel(ModelPart root) {
            super(root);
        }

        boolean scalesBabyHead() {
            return doScaleBabyHead();
        }
    }

    private static final class TestAccessoryModel extends SyncedAccessoryModel {
        TestAccessoryModel(ModelPart root) {
            super(root);
        }

        @Override
        protected void populatePart(ModelPart root) {
            this.head = java.util.Optional.of(root.getChild("head"));
        }

        boolean hasBabyHead() {
            return getDogModelBabyHead(this.head, isBaby()).isPresent();
        }
    }
}
