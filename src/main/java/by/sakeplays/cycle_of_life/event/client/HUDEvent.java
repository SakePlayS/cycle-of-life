package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.text.DecimalFormat;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HUDEvent {


    private static final ResourceLocation INDICATOR = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "indicator");

    private static final ResourceLocation INDICATOR_HEALTH = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "indicator_health");

    private static final ResourceLocation INDICATOR_FOOD = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "indicator_food");

    private static final ResourceLocation INDICATOR_WATER = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "indicator_water");

    private static final ResourceLocation INDICATOR_BLOOD = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "indicator_blood");

    private static final ResourceLocation INDICATOR_STAMINA = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "indicator_stamina");

    private static int x = 0;
    private static int y = 0;


    @SubscribeEvent
    public static void renderHudElements(RenderGuiLayerEvent.Post event) {

        Player player = Minecraft.getInstance().player;

        if (Minecraft.getInstance().options.hideGui ||
            player == null ||
            player.getData(DataAttachments.DINO_DATA).getSelectedDinosaur() == 0 ||
            player.getData(DataAttachments.DINO_DATA).isInBuildMode()
        ) return;

        GuiGraphics graphics = event.getGuiGraphics();

        x = graphics.guiWidth();
        y = graphics.guiHeight();

        float weight = player.getData(DataAttachments.DINO_DATA).getWeight();
        float stamina = player.getData(DataAttachments.DINO_DATA).getStamina();
        float staminaPercentage = (stamina / Util.getStaminaPool(player)) * 100;

        float health = player.getData(DataAttachments.DINO_DATA).getHealth();
        float healthPercentage = Math.max(0f, health/weight * 100f);

        float blood = player.getData(DataAttachments.DINO_DATA).getBloodLevel();
        float bloodPercentage = blood/weight * 100f;

        float food = player.getData(DataAttachments.DINO_DATA).getFoodLevel();

        float water = player.getData(DataAttachments.DINO_DATA).getWaterLevel();


        renderIndicator(INDICATOR_STAMINA, 0, graphics, staminaPercentage);
        renderIndicator(INDICATOR_HEALTH, 35, graphics, healthPercentage);
        renderIndicator(INDICATOR_BLOOD, 70, graphics, bloodPercentage);
        renderIndicator(INDICATOR_FOOD, 105, graphics, food * 100);
        renderIndicator(INDICATOR_WATER, 140, graphics, water * 100);

    }

    private static void renderIndicator(ResourceLocation icon, int yOffset, GuiGraphics graphics, float value) {
        DecimalFormat decimalFormat = new DecimalFormat("##.##");

        graphics.blitSprite(INDICATOR, x - 96, y - (52 + yOffset), 96, 48);
        graphics.blitSprite(icon, x - 96, y - (52 + yOffset), 96, 48);
        graphics.drawString(Minecraft.getInstance().font, decimalFormat.format(value) + "%",
                x - 73, y - (32 + yOffset), getTextColor(value));
    }

    private static int getTextColor(float v) {

        if (v > 75) return Util.rgbaToInt(150, 255, 55, 1);
        if (v > 50) return Util.rgbaToInt(240, 200, 55, 1);
        if (v > 25) return Util.rgbaToInt(255, 100, 25, 1);
        return Util.rgbaToInt(255, 50, 5, 1);
    }

}
