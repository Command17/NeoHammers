package com.github.command17.neohammers.client;

import com.github.command17.neohammers.NeoHammers;
import com.github.command17.neohammers.common.enchantment.ModEnchantmentEffectComponents;
import com.github.command17.neohammers.common.item.HammerItem;
import com.github.command17.neohammers.mixin.client.LevelRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.*;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = NeoHammers.MOD_ID, value = Dist.CLIENT)
public final class ModClientEvents {
    private static final Quaternionf sableOrientationStorage = new Quaternionf();

    @SubscribeEvent
    public static void renderExtraOutlinesAndDecals(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return;
        }

        LevelRenderer renderer = event.getLevelRenderer();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        RenderBuffers renderBuffers = ((LevelRendererAccessor) renderer).getRenderBuffers();
        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        double camX = camPos.x();
        double camY = camPos.y();
        double camZ = camPos.z();
        Player player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (player != null
                && HammerItem.canPlayerUseExtendedAreaMine(player)
                && level != null
                && Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult) {
            ItemStack stack = player.getMainHandItem();
            BlockPos hitPos = hitResult.getBlockPos();
            BlockState hitState = level.getBlockState(hitPos);
            if (!EnchantmentHelper.has(stack, ModEnchantmentEffectComponents.EXTENDED_AREA_MINING.get())) {
                return;
            }

            BlockDestructionProgress destructionProgress;
            var destructionProgressSet = ((LevelRendererAccessor) renderer).getDestructionProgress().get(hitPos.asLong());
            if (destructionProgressSet != null) {
                destructionProgress = destructionProgressSet.last();
            } else {
                destructionProgress = null;
            }

            HammerItem.getBlocksInRadiusBasedOnEnchantment(player, hitPos, level).forEach((pos) -> {
                if (pos.equals(hitPos)) {
                    return;
                }

                BlockState state = level.getBlockState(pos);
                if (!HammerItem.canMineOther(stack, hitState, state)) {
                    return;
                }

                // Sable
                Vec3 plotPos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
                ClientSubLevelAccess subLevelAccess = SableCompanion.INSTANCE.getContainingClient(plotPos);

                Pose3dc subLevelPose;
                if (subLevelAccess != null) {
                    subLevelPose = subLevelAccess.renderPose();
                } else {
                    subLevelPose = new Pose3d();
                }

                Vec3 projectedPos = subLevelPose.transformPosition(plotPos);
                sableOrientationStorage.set(subLevelPose.orientation());

                // Outline
                poseStack.pushPose();

                // Sable
                poseStack.translate(projectedPos.x() - camX, projectedPos.y() - camY, projectedPos.z() - camZ);
                poseStack.mulPose(sableOrientationStorage);

                VoxelShape shape = state.getShape(level, pos);
                VertexConsumer outlineVertexConsumer = renderBuffers.bufferSource().getBuffer(RenderType.lines());
                LevelRendererAccessor.renderShape(poseStack, outlineVertexConsumer, shape, 0, 0, 0, 0, 0, 0, 0.4f);
                poseStack.popPose();

                // Breaking Texture
                if (destructionProgress != null) {
                    poseStack.pushPose();

                    // Sable
                    poseStack.translate(projectedPos.x() - camX, projectedPos.y() - camY, projectedPos.z() - camZ);
                    poseStack.mulPose(sableOrientationStorage);

                    PoseStack.Pose pose = poseStack.last();
                    VertexConsumer decal = new SheetedDecalTextureGenerator(((LevelRendererAccessor) renderer).getRenderBuffers()
                            .crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(destructionProgress.getProgress())), pose, 1);
                    blockRenderer.renderBreakingTexture(state, pos, level, poseStack, decal);
                    poseStack.popPose();
                }
            });
        }
    }
}
