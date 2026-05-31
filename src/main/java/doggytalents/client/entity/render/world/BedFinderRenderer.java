package doggytalents.client.entity.render.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import doggytalents.DoggyTalents;
import doggytalents.common.entity.Dog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Optional;

public class BedFinderRenderer {

    private static final int BED_OUTLINE_COLOR = ARGB.color(255, 255, 255, 0);

    public static void onWorldRenderLast(RenderLevelStageEvent.AfterTranslucentParticles event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        for (Entity passenger : player.getPassengers()) {
            if (passenger instanceof Dog dog) {
                Optional<BlockPos> bedPosOpt = dog.getBedPos();
                if (bedPosOpt.isPresent()) {
                    BlockPos bedPos = bedPosOpt.get();
                    int level = dog.getDogLevel(DoggyTalents.BED_FINDER);
                    double distance = (level * 200D) - Math.sqrt(bedPos.distSqr(dog.blockPosition()));
                    if (level == 5 || distance >= 0.0D) {
                        drawBedOutline(event.getPoseStack(), bedPos);
                    }
                }
            }
        }
    }

    private static void drawBedOutline(PoseStack poseStack, BlockPos bedPos) {
        var mc = Minecraft.getInstance();
        Vec3 camPos = mc.gameRenderer.getMainCamera().position();
        var bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderTypes.lines());
        ShapeRenderer.renderShape(
            poseStack,
            buffer,
            Shapes.block(),
            bedPos.getX() - camPos.x,
            bedPos.getY() - camPos.y,
            bedPos.getZ() - camPos.z,
            BED_OUTLINE_COLOR,
            2.0F
        );
        bufferSource.endBatch(RenderTypes.lines());
    }
}
