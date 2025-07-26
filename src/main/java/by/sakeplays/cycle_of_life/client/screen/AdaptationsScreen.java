package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.util.Util;
import by.sakeplays.cycle_of_life.common.data.AdaptationData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.adaptations.Adaptation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;


public class AdaptationsScreen extends Screen {


    private Button SALTWATER_TOLERANCE_BUTTON;
    private Button ENHANCED_STAMINA;
    private Button BLEED_RESISTANCE;
    private Button HEAT_RESISTANCE;
    private Button COLD_RESISTANCE;

    public AdaptationsScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        Player player = Minecraft.getInstance().player;
        AdaptationData data = player.getData(DataAttachments.ADAPTATION_DATA);

        if (SALTWATER_TOLERANCE_BUTTON.isFocused()) {
            renderAdaptationDescription(guiGraphics, data.SALTWATER_TOLERANCE, "Saltwater Tolerance", "Decreases your likelihood to get",
                    "saltwater sickness.", "", "");
        }

        if (ENHANCED_STAMINA.isFocused()) {
            renderAdaptationDescription(guiGraphics, data.ENHANCED_STAMINA, "Enhanced Stamina", "Increases your stamina pool.",
                    "", "", "");
        }

        if (BLEED_RESISTANCE.isFocused()) {
            renderAdaptationDescription(guiGraphics, data.BLEED_RESISTANCE, "Bleed Resistance", "Increases your V resistance.",
                    "", "", "");
        }

        if (HEAT_RESISTANCE.isFocused()) {
            renderAdaptationDescription(guiGraphics, data.HEAT_RESISTANCE, "Heat Resistance", "Increases your heat resistance.",
                    "", "", "");
        }

        if (COLD_RESISTANCE.isFocused()) {
            renderAdaptationDescription(guiGraphics, data.COLD_RESISTANCE, "Cold Resistance", "Increases your cold resistance.",
                    "", "", "");
        }

    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        COLD_RESISTANCE = new Button.Builder(Component.literal("Cold Resistance"), button -> {
        }).size(120, 18).pos(width/2 - 120, height/2 - 40).build();

        HEAT_RESISTANCE = new Button.Builder(Component.literal("Heat Resistance"), button -> {
        }).size(120, 18).pos(width/2 - 120, height/2 - 20).build();

        SALTWATER_TOLERANCE_BUTTON = new Button.Builder(Component.literal("Saltwater Tolerance"), button -> {
        }).size(120, 18).pos(width/2 - 120, height/2).build();

        ENHANCED_STAMINA = new Button.Builder(Component.literal("Enhanced Stamina"), button -> {
        }).size(120, 18).pos(width/2 - 120, height/2 + 20).build();

        BLEED_RESISTANCE = new Button.Builder(Component.literal("Bleed Resistance"), button -> {
        }).size(120, 18).pos(width/2 - 120, height/2 + 40).build();

        addRenderableWidget(COLD_RESISTANCE);
        addRenderableWidget(HEAT_RESISTANCE);
        addRenderableWidget(SALTWATER_TOLERANCE_BUTTON);
        addRenderableWidget(ENHANCED_STAMINA);
        addRenderableWidget(BLEED_RESISTANCE);

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }


    public void renderAdaptationDescription(GuiGraphics guiGraphics, Adaptation adaptation, String name, String desc1,
                                            String desc2, String desc3, String desc4) {

        guiGraphics.drawString(Minecraft.getInstance().font, name, width / 2 + 60, height / 2 - 55,
                Util.rgbaToInt(255, 255, 255, 1));
        guiGraphics.drawString(Minecraft.getInstance().font, desc1, width / 2 + 60, height / 2 - 15,
                Util.rgbaToInt(255, 255, 255, 1));
        guiGraphics.drawString(Minecraft.getInstance().font, desc2, width / 2 + 60, height / 2 - 5,
                Util.rgbaToInt(255, 255, 255, 1));
        guiGraphics.drawString(Minecraft.getInstance().font, desc3, width / 2 + 60, height / 2 + 5,
                Util.rgbaToInt(255, 255, 255, 1));
        guiGraphics.drawString(Minecraft.getInstance().font, desc4, width / 2 + 60, height / 2 + 15,
                Util.rgbaToInt(255, 255, 255, 1));

        if (!adaptation.isUpgraded()) {
            guiGraphics.drawString(Minecraft.getInstance().font,
                    "Progress: " + (int) (adaptation.getProgress() * 100) + "%", width / 2 + 60, height / 2 + 35,
                    Util.rgbaToInt(255, 255, 255, 1));
        }

        guiGraphics.drawString(Minecraft.getInstance().font,
                "Level: " + adaptation.getLevel(), width / 2 + 60, height / 2 + 45,
                Util.rgbaToInt(255, 255, 255, 1));

        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        int level = adaptation.getLevel();
        String value = !adaptation.isUpgraded() ?
                decimalFormat.format(adaptation.getValue(level) * 100) + "% -> " +
                decimalFormat.format(adaptation.getValue(level + 1) * 100) + "%"
                : decimalFormat.format(adaptation.getValue(level) * 100) + "%";

        if (level == 5) value = " " + adaptation.getValue(level) * 100 + "%";

        guiGraphics.drawString(Minecraft.getInstance().font,
                "Value: " + value, width / 2 + 60, height / 2 + 55,
                Util.rgbaToInt(255, 255, 255, 1));
    }
}
