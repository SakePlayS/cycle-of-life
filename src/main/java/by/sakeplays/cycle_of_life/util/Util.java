package by.sakeplays.cycle_of_life.util;

import by.sakeplays.cycle_of_life.ModSounds;
import by.sakeplays.cycle_of_life.client.ModRenderTypes;
import by.sakeplays.cycle_of_life.client.entity.CrossfadeTickTracker;
import by.sakeplays.cycle_of_life.client.screen.util.ColorOption;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DietStat;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.Position;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import by.sakeplays.cycle_of_life.common.data.adaptations.AdaptationType;
import by.sakeplays.cycle_of_life.entity.*;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.entity.util.GrowthCurveStat;
import by.sakeplays.cycle_of_life.entity.util.HitboxType;
import by.sakeplays.cycle_of_life.mixins.AnimationControllerAccessor;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncBleed;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncHealth;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncStamina;
import by.sakeplays.cycle_of_life.network.to_server.RequestPlayHurtSound;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static float getTurnSpeed(Player player) {
        return getDino(player).getTurnSpeed();

    }

    public static float getStamRegen(Player player) {
        return getDino(player).getStaminaRegen() * DietStat.calculate(player, DietStat.STAMINA_REGEN);

    }

    public static float getWalkSpeed(Player player) {
        return getDino(player).getWalkSpeed();

    }

    public static float getSprintSpeed(Player player) {
        return getDino(player).getSprintSpeed();
    }

    public static float getAcceleration(Player player) {
        return getDino(player).getAcceleration();

    }

    public static float getSwimSpeed(Player player) {
        return getDino(player).getSwimSpeed();
    }

    public static float getTurnPenalty(Player player) {
        if (!player.isInWater() && !player.onGround()) {
            return 0.1F;
        }

        return 1f;
    }

    public static Dinosaurs getDino(Player player) {
        int ID = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        return Dinosaurs.getById(ID);
    }


    public static void recordYHistory(Player player, float y) {
        player.getData(DataAttachments.Y_HISTORY).add(y);

        if (player.getData(DataAttachments.Y_HISTORY).size() > 6) {
            player.getData(DataAttachments.Y_HISTORY).removeFirst();
        }
    }

    public static void recordTurnHistory(Player player, float y) {
        player.getData(DataAttachments.TURN_HISTORY).add(y);

        if (player.getData(DataAttachments.TURN_HISTORY).size() > 7) {
            player.getData(DataAttachments.TURN_HISTORY).removeFirst();
        }
    }


    public static float calculateTailXRot(ArrayList<Float> arrayList) {
        float sum = 0;
        int iterations = 0;

        for (int i = 0; i < arrayList.size() - 1; i++) {
            sum = sum + (arrayList.get(i + 1) - arrayList.get(i)) * Mth.DEG_TO_RAD;
            iterations++;
        }

        return sum/iterations;
    }

    public static float calculateTailYRot(ArrayList<Float> arrayList, float currentTurnDegree, int lowerBound, int upperBound) {
        float sum = 0;
        int iterations = 0;

        for (int i = lowerBound; i < upperBound; i++) {
            sum = sum + (currentTurnDegree - arrayList.get(i)) * Mth.DEG_TO_RAD;
            iterations++;
        }

        return sum/iterations;
    }

    public static void dealDamage(Player target, float dmg, float bleed, boolean playHurtSound) {
        if (target.level().isClientSide) {
            DinoData data = target.getData(DataAttachments.DINO_DATA);
            float newBleed = data.getBleed() + bleed;
            float newHealth = data.getHealth() - dmg;

            data.setBleed(newBleed);
            PacketDistributor.sendToServer(new SyncBleed(target.getId(),newBleed));

            data.setHealth(newHealth);
            PacketDistributor.sendToServer(new SyncHealth(target.getId(), newHealth));

            if (playHurtSound) PacketDistributor.sendToServer(new RequestPlayHurtSound(target.getId()));

        } else {
            DinoData data = target.getData(DataAttachments.DINO_DATA);
            float newBleed = data.getBleed() + bleed;
            float newHealth = data.getHealth() - dmg;

            data.setBleed(newBleed);
            PacketDistributor.sendToAllPlayers(new SyncBleed(target.getId(),newBleed));

            data.setHealth(newHealth);
            PacketDistributor.sendToAllPlayers(new SyncHealth(target.getId(), newHealth));

            if (playHurtSound)  {
                Dinosaurs selectedDino = Util.getDino(target);

                switch (selectedDino) {
                    case DEINONYCHUS -> target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            ModSounds.DEINONYCHUS_HURT.get(), SoundSource.PLAYERS, 1f ,1f +
                                    (float) ((Math.random() - 0.5) / 4));
                    default -> target.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1f ,1f +
                                    (float) ((Math.random() - 0.5) / 4));
                }
            }
        }
    }

    public static void addStamina(Player target, float stamina) {
        if (target.level().isClientSide) {
            DinoData data = target.getData(DataAttachments.DINO_DATA);
            float newStam = data.getStamina() + stamina;

            if (newStam >  Util.getStaminaPool(target)) newStam = Util.getStaminaPool(target);

            target.getData(DataAttachments.DINO_DATA).setStamina(newStam);
            PacketDistributor.sendToServer(new SyncStamina(target.getId(), newStam));

        }
    }

    public static int rgbaToInt(float r, float g, float b, float a) {
        int alpha = (int)(a * 255.0f) << 24;
        int red   = (int)(r * 255.0f) << 16;
        int green = (int)(g * 255.0f) << 8;
        int blue  = (int)(b * 255.0f);
        return alpha | red | green | blue;
    }

    public static int rgbaToInt(int r, int g, int b, int a) {
        int alpha = a << 24;
        int red   = r << 16;
        int green = g << 8;
        return alpha | red | green | b;
    }

    public static float getStaminaPool(Player player) {
        Adaptation data = player.getData(DataAttachments.ADAPTATION_DATA).getAdaptation(AdaptationType.ENHANCED_STAMINA);


        return getDino(player).getStaminaPool() * DietStat.calculate(player, DietStat.STAMINA_POOL) * (1 + data.getType().getValue(data.getLevel()));
    }

    public static DinosaurEntity getBody(Player player) {
        int dinoId = player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur();

        if (dinoId == Dinosaurs.PACHYCEPHALOSAURUS.getID()) return new Pachycephalosaurus(ModEntities.PACHYCEPHALOSAURUS.get(), player.level());
        if (dinoId == Dinosaurs.DEINONYCHUS.getID()) return new Deinonychus(ModEntities.DEINONYCHUS.get(), player.level());

        // fallback
        return new Deinonychus(ModEntities.DEINONYCHUS.get(), player.level());
    }

    public static float calculateScale(DinosaurEntity entity) {

        if (entity.isCorpse()) return entity.getDinosaurSpecies().getGrowthCurve().calculate(entity.getBodyGrowth(), GrowthCurveStat.SCALE);
        if (entity.isForScreenRendering) return 1;
        return Util.getDino(entity.getPlayer()).getGrowthCurve().calculate(entity.getPlayer().getData(DataAttachments.DINO_DATA).getGrowth(), GrowthCurveStat.SCALE);

    }


    public static float calculateMaxSpeed(Player player) { // used for display
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        float growth = data.getGrowth();
        float maxSpeed = Util.getDino(player).getSprintSpeed() * Util.getDino(player).getGrowthCurve().calculate(growth, GrowthCurveStat.SPEED);
        maxSpeed *= DietStat.calculate(player, DietStat.SPEED);

        return maxSpeed;
    }


    public static float calculateSpeed(Player player) {  // used for actual movement
        DinoData data = player.getData(DataAttachments.DINO_DATA);

        float growth = data.getGrowth();
        float speed = data.isSprinting() ? Util.getDino(player).getSprintSpeed() : Util.getDino(player).getWalkSpeed();

        speed *= Util.getDino(player).getGrowthCurve().calculate(growth, GrowthCurveStat.SPEED);
        speed *= DietStat.calculate(player, DietStat.SPEED);
        if (player.isInWater()) {
            speed *= Util.getSwimSpeed(player);
        }

        return speed;
    }

    public static boolean isAttackValid(Player source, Player target) {

        /// the hitbox may vary in size. if the dino player is big they have higher attack distance.
        if (source.distanceTo(target) > 7 * (source.getBoundingBox().getXsize() + 0.75)) return false;

        if (source.getData(DataAttachments.KNOCKDOWN_TIME) > 0) return false;

        return true;
    }


    public static void attemptToHitPlayer(Player target, float damage, float bleed, boolean makeNoise, HitboxType type) {

        float damageModifier = type.getDamageModifier();

        dealDamage(target, damage * damageModifier, bleed * damageModifier, makeNoise);
    }

    public static float getDinoBaseWidth(Player player) {
        return switch (getDino(player)) {
            case PACHYCEPHALOSAURUS -> 0.725F;
            case DEINONYCHUS -> 0.65F;
            case PTERANODON -> 0.5F;

            default -> 0.6F;
        };
    }

    public static float getDinoBaseHeight(Player player) {
        return switch (getDino(player)) {
            case PACHYCEPHALOSAURUS -> 1.7F;
            case DEINONYCHUS -> 1.35F;
            case PTERANODON -> 1.1F;

            default -> 1.8F;
        };
    }

    public static <E extends GeoAnimatable> void setAnimationSpeed(double speed, double currentAnimTick, AnimationController<E> controller) {
        if(speed == controller.getAnimationSpeed() || speed <= 0) {
            return;
        }

        if (controller.getCurrentAnimation() != null) {

            double distance = currentAnimTick - ((AnimationControllerAccessor)controller).getTickOffset();
            ((AnimationControllerAccessor) controller).setTickOffset(currentAnimTick - distance * (controller.getAnimationSpeed() / speed));
            controller.setAnimationSpeed(speed);
        }
    }



    public static <E extends GeoAnimatable> PlayState setAndContinue(AnimationState<E> state, double currentTick, double tickAdvance, RawAnimation transitioningFrom, RawAnimation transitioningTo, double nextAnimLength) {
        AnimationController<E> controller = state.getController();

        controller.transitionLength((int) tickAdvance);
        crossfade(controller, currentTick, tickAdvance, transitioningFrom, nextAnimLength);
        return state.setAndContinue(transitioningTo);
    }

    public static <E extends GeoAnimatable> void crossfade(AnimationController<E> controller, double currentTick, double tickAdvance, RawAnimation transitioningFrom, double nextAnimLength) {
        AnimationControllerAccessor accessor = (AnimationControllerAccessor)controller;

        if (controller.getCurrentRawAnimation() == transitioningFrom) {
            if (accessor.getAnimatable() instanceof DinosaurEntity entity && entity.getPlayer() != null) {
                double length = controller.getCurrentAnimation().animation().length();
                double animCrossfadeTick = (currentTick % length) + tickAdvance;

                if (animCrossfadeTick > nextAnimLength * 20d) animCrossfadeTick -= nextAnimLength * 20d;

                CrossfadeTickTracker.addOrReplace(entity.getPlayer().getId(), animCrossfadeTick);

                accessor.setTickOffset(accessor.getTickOffset() + animCrossfadeTick);

            }
        }
    }

    public static void renderItemStack(
            PoseStack poseStack, Level level, int packedLight, MultiBufferSource bufferSource,
            String itemId,
            double xTranslation, double yTranslation, double zTranslation,
            float xScale, float yScale, float zScale,
            float xRot, float yRot, float zRot
            ) {
        poseStack.pushPose();

        poseStack.translate(xTranslation, yTranslation, zTranslation);

        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(zRot));

        poseStack.scale(xScale, yScale, zScale);

        if (BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemId))) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));

            ItemStack stack = new ItemStack(item);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    level,
                    0
            );
        }

        poseStack.popPose();
    }

    public static <T> T getFromEnd(List<T> list, int index) {
        return list.get(list.size() - 1 - index);
    }

    public static boolean hasClearLineOfSight(Position from, Position to, Level level) {
        ClipContext context = new ClipContext(from.toVec3(), to.toVec3(), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty());
        return level.clip(context).getType() == HitResult.Type.MISS;
    }

    public static void applyBodyPartColors(int primaryColor, int secondaryColor) {
        ShaderInstance shader = ModRenderTypes.getGrayscaleTintedShader();
        ColorOption primary = ColorOption.fromInt(primaryColor);
        ColorOption secondary = ColorOption.fromInt(secondaryColor);

        if (shader != null) {
            shader.safeGetUniform("PrimaryColor").set(primary.r() / 255f, primary.g() / 255f, primary.b() / 255f);
            shader.safeGetUniform("SecondaryColor").set(secondary.r() / 255f, secondary.g() / 255f, secondary.b() / 255f);
        }
    }
}
