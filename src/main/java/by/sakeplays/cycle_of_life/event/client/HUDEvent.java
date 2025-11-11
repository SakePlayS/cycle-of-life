package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DietStat;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.text.DecimalFormat;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HUDEvent {

    private static final ResourceLocation HUD = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "hud");

    private static final ResourceLocation HUD_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "hud_background");

    private static int x = 0;
    private static int y = 0;


    @SubscribeEvent
    public static void renderHudElements(RenderGuiLayerEvent.Post event) {

        Player player = Minecraft.getInstance().player;

        if (Minecraft.getInstance().options.hideGui ||
            player == null ||
            player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0 ||
            player.getData(DataAttachments.DINO_DATA).isInHumanMode()
        ) return;

        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
        GuiGraphics graphics = event.getGuiGraphics();

        x = graphics.guiWidth()/2;
        y = graphics.guiHeight();


        float weight = dinoData.getWeight();
        float healthRatio = dinoData.getHealth()/weight;
        float staminaRatio = dinoData.getStamina() / Util.getStaminaPool(player);
        float bloodRatio = dinoData.getBloodLevel() / weight;
        float foodRatio = dinoData.getFoodLevel();
        float dietQuality = DietStat.dietQuality(player);
        float waterLevel = dinoData.getWaterLevel();

        graphics.blitSprite(HUD_BACKGROUND, x - 96, y - 80, 192, 72);


        // health
        graphics.fill(x - (int) Mth.lerp(healthRatio, 22, 93), y - 24, x - 22, y - 18, Util.rgbaToInt(255, 88, 75, 255));
        graphics.fill(x - (int) Mth.lerp(healthRatio, 22, 93), y - 18, x - 22, y - 14, Util.rgbaToInt(164, 10, 37, 255));
        graphics.fill(x - (int) Mth.lerp(healthRatio, 22, 93), y - 22, x - 22, y - 20, Util.rgbaToInt(255, 255, 255, 255));

        // food
        graphics.fill(x - (int) Mth.lerp(foodRatio, 24, 89), y - 46, x - 24, y - 40, Util.rgbaToInt(255, 159, 75, 255));
        graphics.fill(x - (int) Mth.lerp(foodRatio, 24, 89), y - 40, x - 24, y - 36, Util.rgbaToInt(210, 86, 20, 255));
        graphics.fill(x - (int) Mth.lerp(foodRatio, 24, 89), y - 44, x - 24, y - 42, Util.rgbaToInt(255, 255, 255, 255));

        // stamina
        graphics.fill(x - (int) Mth.lerp(staminaRatio, 25, 85), y - 68, x - 22, y - 62, Util.rgbaToInt(255, 208, 75, 255));
        graphics.fill(x - (int) Mth.lerp(staminaRatio, 25, 85), y - 62, x - 22, y - 58, Util.rgbaToInt(227, 141, 44, 255));
        graphics.fill(x - (int) Mth.lerp(staminaRatio, 25, 85), y - 67, x - 22, y - 65, Util.rgbaToInt(255, 255, 255, 255));

        // blood level
        graphics.fill(x + 22, y - 24, x + (int) Mth.lerp(bloodRatio, 22, 93), y - 18, Util.rgbaToInt(255, 88, 75, 255));
        graphics.fill(x + 22, y - 18, x + (int) Mth.lerp(bloodRatio, 22, 93), y - 14, Util.rgbaToInt(164, 10, 37, 255));
        graphics.fill(x + 22, y - 22, x + (int) Mth.lerp(bloodRatio, 22, 93), y - 20, Util.rgbaToInt(255, 255, 255, 255));

        // water
        graphics.fill(x + (int) Mth.lerp(waterLevel, 24, 89), y - 46, x + 24, y - 40, Util.rgbaToInt(110, 202, 255, 255));
        graphics.fill(x + (int) Mth.lerp(waterLevel, 24, 89), y - 40, x + 24, y - 36, Util.rgbaToInt(69, 94, 208, 255));
        graphics.fill(x + (int) Mth.lerp(waterLevel, 24, 89), y - 44, x + 24, y - 42, Util.rgbaToInt(255, 255, 255, 255));

        // diet quality
        graphics.fill(x + (int) Mth.lerp(dietQuality, 25, 85), y - 68, x + 22, y - 62, Util.rgbaToInt(191, 255, 88, 255));
        graphics.fill(x + (int) Mth.lerp(dietQuality, 25, 85), y - 62, x + 22, y - 58, Util.rgbaToInt(79, 202, 63, 255));
        graphics.fill(x + (int) Mth.lerp(dietQuality, 25, 85), y - 67, x + 22, y - 65, Util.rgbaToInt(255, 255, 255, 255));

        graphics.blitSprite(HUD, x - 96, y - 80, 192, 72);

    }

}
