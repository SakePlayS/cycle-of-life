package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DietStat;
import by.sakeplays.cycle_of_life.common.data.DinoData;
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

    private static final ResourceLocation HUD = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "hud");

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

        x = graphics.guiWidth()/2 - 120;
        y = graphics.guiHeight() - 100;

        graphics.blitSprite(HUD, x, y, 240, 96);

        float weight = dinoData.getWeight();
        float healthRatio = dinoData.getHealth()/weight;

        float staminaRatio = dinoData.getStamina() / Util.getStaminaPool(player);

        float bloodRatio = dinoData.getBloodLevel() / weight;

        float foodRatio = dinoData.getFoodLevel();
        float dietQuality = DietStat.dietQuality(player);
        float waterQuality = dinoData.getWaterLevel();

        drawString(graphics, healthRatio, x + 41, y + 17);
        drawString(graphics, staminaRatio, x + 26, y + 44);
        drawString(graphics, bloodRatio, x + 41, y + 71);

        drawString(graphics, foodRatio, x + 199, y + 17);
        drawString(graphics, dietQuality, x + 215, y + 44);
        drawString(graphics, waterQuality, x + 199, y + 71);
    }

    private static void drawString(GuiGraphics graphics, float value, int x, int y) {
        DecimalFormat format = new DecimalFormat("##.#");
        int color = Util.rgbaToInt(157, 255, 0 ,1);

        if (value < 0.75) color = Util.rgbaToInt(255, 199, 0 ,1);
        if (value < 0.5) color = Util.rgbaToInt(255, 104, 0 ,1);
        if (value < 0.25) color = Util.rgbaToInt(255, 0, 25 ,1);

        graphics.drawCenteredString(Minecraft.getInstance().font, format.format(value * 100f) + "%", x, y, color);
    }


}
