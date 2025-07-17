package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.adaptations.EnhancedStamina;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HexFormat;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HUDEvent {

    private static final ResourceLocation HEART = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "heart");
    private static final ResourceLocation STAMINA = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "stamina");
    private static final ResourceLocation BLOOD_LEVEL = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "blood_level");
    private static final ResourceLocation FOOD_LEVEL = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "food_level");
    private static final ResourceLocation WATER_LEVEL = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "water_level");
    private static final ResourceLocation NODE_0 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_0");
    private static final ResourceLocation NODE_24 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_24");


    private static int x = 0;
    private static int y = 0;


    @SubscribeEvent
    public static void renderHudElements(RenderGuiLayerEvent.Post event) {

        Player player = Minecraft.getInstance().player;

        if (Minecraft.getInstance().options.hideGui || player == null || player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0) return;

        GuiGraphics graphics = event.getGuiGraphics();

        x = graphics.guiWidth()/2;
        y = graphics.guiHeight()/2;


        handleHealthRendering(graphics);
        handleStaminaRendering(graphics);
        handleBloodLevelRendering(graphics);
        handleFoodRendering(graphics);
        handleWaterRendering(graphics);
    }

    private static void handleStaminaRendering(GuiGraphics graphics) {

        Player player = Minecraft.getInstance().player;

        int xPos = x - 32;
        int yPos = (int) (y * 2 - 80);

        EnhancedStamina enhancedStamina = player.getData(DataAttachments.ADAPTATION_DATA).ENHANCED_STAMINA;

        float stam = player.getData(DataAttachments.DINO_DATA).getStamina();
        float stamPool = Util.getStaminaUpgraded(player);
        float stamPoolFraction = stamPool / 24;

        if (stam >= stamPoolFraction * 24) graphics.blitSprite(NODE_0, xPos, yPos, 64 ,64);

        for (int i = 24; i > 1; i--) {

            if ((stam < stamPoolFraction * i) && (stam >= stamPoolFraction * (i - 1)))
                graphics.blitSprite(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
                        "node_" + (25 - i)), xPos, yPos, 64, 64);

        }

        if (stam < stamPoolFraction)  graphics.blitSprite(NODE_24, xPos, yPos, 64 ,64);

        graphics.blitSprite(STAMINA, xPos, yPos, 64 ,64);
    }

    private static void handleHealthRendering(GuiGraphics graphics) {

        Player player = Minecraft.getInstance().player;

        if (player == null) return;

        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        float health = player.getData(DataAttachments.DINO_DATA).getHealth();
        float weightFraction = player.getData(DataAttachments.DINO_DATA).getWeight() / 24;
        float weight = player.getData(DataAttachments.DINO_DATA).getWeight();
        float healthPercentage = health/weight * 100f;

        if (healthPercentage < 0) healthPercentage = 0;

        if (healthPercentage < 100) {
            graphics.drawString(Minecraft.getInstance().font, decimalFormat.format(healthPercentage) + "%",
                    x - 100, (y * 2 - 64), HexFormat.fromHexDigits("FF5733"));
        }

        int xPos = x - 64;
        int yPos = (y * 2 - 82);



        if (health >= weightFraction * 23) graphics.blitSprite(NODE_0, xPos, yPos, 32 ,32);

        for (int i = 23; i > 1; i--) {

            if ((health < weightFraction * i) && (health >= weightFraction * (i - 1)))
                graphics.blitSprite(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
                        "node_" + (24 - i)), xPos, yPos, 32, 32);

        }

        if (health < weightFraction) graphics.blitSprite(NODE_24, xPos, yPos, 32 ,32);

        graphics.blitSprite(HEART, xPos, yPos, 32 ,32);
    }

    private static void handleBloodLevelRendering(GuiGraphics graphics) {

        Player player = Minecraft.getInstance().player;
        DecimalFormat decimalFormat = new DecimalFormat("##.##");

        if (player == null) return;

        float blood = player.getData(DataAttachments.DINO_DATA).getBloodLevel();
        float weight = player.getData(DataAttachments.DINO_DATA).getWeight();
        float bloodPercentage = blood/weight * 100f;
        float weightFraction = player.getData(DataAttachments.DINO_DATA).getWeight() / 24;

        if (bloodPercentage < 0) bloodPercentage = 0;

        int xPos = x - 64;
        int yPos = y * 2 - 48;


        if (bloodPercentage < 100) {
            graphics.drawString(Minecraft.getInstance().font, decimalFormat.format(bloodPercentage) + "%",
                    x - 100, (y * 2 - 36), HexFormat.fromHexDigits("FF5733"));
        }

        if (blood >= weightFraction * 23) graphics.blitSprite(NODE_0, xPos, yPos, 32 ,32);

        for (int i = 23; i > 1; i--) {

            if ((blood < weightFraction * i) && (blood >= weightFraction * (i - 1)))
                graphics.blitSprite(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
                        "node_" + (24 - i)), xPos, yPos, 32, 32);

        }

        if (blood < weightFraction) graphics.blitSprite(NODE_24, xPos, yPos, 32 ,32);

        graphics.blitSprite(BLOOD_LEVEL, xPos, yPos, 32 ,32);
    }

    private static void handleFoodRendering(GuiGraphics graphics) {

        Player player = Minecraft.getInstance().player;

        if (player == null) return;

        float food = player.getData(DataAttachments.DINO_DATA).getFoodLevel();
        float foodFraction = 1f / 24;


        int xPos = x + 32;
        int yPos = (int) (y * 2 - 82);



        if (food >= foodFraction * 23) graphics.blitSprite(NODE_0, xPos, yPos, 32 ,32);

        for (int i = 23; i > 1; i--) {

            if ((food < foodFraction * i) && (food >= foodFraction * (i - 1)))
                graphics.blitSprite(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
                        "node_" + (24 - i)), xPos, yPos, 32, 32);

        }

        if (food < foodFraction) graphics.blitSprite(NODE_24, xPos, yPos, 32 ,32);

        graphics.blitSprite(FOOD_LEVEL, xPos, yPos, 32 ,32);
    }

    private static void handleWaterRendering(GuiGraphics graphics) {

        Player player = Minecraft.getInstance().player;

        if (player == null) return;

        float water = player.getData(DataAttachments.DINO_DATA).getWaterLevel();
        float waterFraction = 1f / 24;


        int xPos = x + 32;
        int yPos = (int) (y * 2 - 48);


        if (water >= waterFraction * 23) graphics.blitSprite(NODE_0, xPos, yPos, 32 ,32);

        for (int i = 23; i > 1; i--) {

            if ((water < waterFraction * i) && (water >= waterFraction * (i - 1)))
                graphics.blitSprite(ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
                        "node_" + (24 - i)), xPos, yPos, 32, 32);

        }

        if (water < waterFraction) graphics.blitSprite(NODE_24, xPos, yPos, 32 ,32);

        graphics.blitSprite(WATER_LEVEL, xPos, yPos, 32 ,32);
    }


}
