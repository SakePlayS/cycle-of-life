package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DietStat;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.StatusData;
import by.sakeplays.cycle_of_life.util.Util;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class DietScreen extends Screen {


    private static ResourceLocation BAR = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "bar");

    private static ResourceLocation POINTER = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "pointer");

    public DietScreen(Component title) {
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

        if (player == null) return;

        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

        renderBar(guiGraphics, guiGraphics.guiWidth()/2,guiGraphics.guiHeight()/2 - 125, (int) Math.ceil(dinoData.getCarbs() * 110f),
                "Carbs: " + (int) Math.ceil(dinoData.getCarbs() * 100f) + "%");

        renderBar(guiGraphics, guiGraphics.guiWidth()/2,guiGraphics.guiHeight()/2 - 95, (int) Math.ceil(dinoData.getLipids() * 110f),
                "Lipids: " + (int) Math.ceil(dinoData.getLipids() * 100f) + "%");

        renderBar(guiGraphics, guiGraphics.guiWidth()/2,guiGraphics.guiHeight()/2 - 65, (int) Math.ceil(dinoData.getProteins() * 110f),
                "Proteins: " + (int) Math.ceil(dinoData.getProteins() * 100f) + "%");

        renderBar(guiGraphics, guiGraphics.guiWidth()/2,guiGraphics.guiHeight()/2 - 35, (int) Math.ceil(dinoData.getVitamins() * 110f),
                "Vitamins: " + (int) Math.ceil(dinoData.getVitamins() * 100f) + "%");

        renderStats(guiGraphics);
    }

    private void renderStats(GuiGraphics guiGraphics) {
        List<Pair<String, Integer>> stats = new ArrayList<>();
        Player player = Minecraft.getInstance().player;
        DecimalFormat format = new DecimalFormat("##.#");

        stats.add(Pair.of("Diet quality: " + format.format(DietStat.dietQuality(player)), Util.rgbaToInt(255, 255 ,255, 0)));

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.STAMINA_POOL),
                "Stamina pool is increased by ",
                "Stamina pool is reduced by "
                );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.STAMINA_REGEN),
                "Stamina regeneration is increased by ",
                "Stamina regeneration is reduced by "
        );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.SPEED),
                "Speed is increased by ",
                "Speed is reduced by "
        );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.DAMAGE),
                "Damage is increased by ",
                "Damage is reduced by "
        );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.HEALTH_REGEN),
                "Health regeneration is increased by ",
                "Health regeneration is reduced by "
        );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.BLOOD_REGEN),
                "Blood regeneration is increased by ",
                "Blood regeneration is reduced by "
        );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.BLEED_RESISTANCE),
                "Bleed resistance is increased by ",
                "Bleed resistance is reduced by "
        );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.GROWTH_SPEED),
                "Growth speed is increased by ",
                "Growth speed is reduced by "
        );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.TEMPERATURE_RESISTANCE),
                "Temperature resistance is increased by ",
                "Temperature resistance is reduced by "
        );

        createStatsEntry(
                stats,
                DietStat.calculate(player, DietStat.IMMUNE_SYS_STRENGTH),
                "Immune system is empowered by ",
                "Immune system is weakened by "
        );

        if (stats.isEmpty()) {
            guiGraphics.drawString(font, "Your stats are unaffected by your diet", guiGraphics.guiWidth()/2 - 100, guiGraphics.guiHeight()/2 + 13, Util.rgbaToInt(255, 255, 255, 1));
        }

        int i = 0;
        for (Pair<String, Integer> pair : stats) {
            guiGraphics.drawString(font, pair.left(), guiGraphics.guiWidth()/2 - 100, guiGraphics.guiHeight()/2 + 13 + i, pair.right());
            i += minecraft.font.lineHeight;
        }
    }

    private void createStatsEntry(List<Pair<String, Integer>> list, float value, String baseMsgPositive, String baseMsgNegative) {
        DecimalFormat format = new DecimalFormat("##.##");

        if (value > 1f)
        {
            list.add(Pair.of("▲ " +  baseMsgPositive + format.format(100f * (value - 1f)) + "%", Util.rgbaToInt(100, 255, 100, 1)));
        }
        else if (value < 1f)
        {
            list.add(Pair.of("▼ " + baseMsgNegative + format.format(100f * (1f - value)) + "%", Util.rgbaToInt(255, 100, 150, 1)));
        }
    }

    private void renderBar(GuiGraphics guiGraphics, int x, int y, int pointerOffset, String text) {
        guiGraphics.drawString(font, text, x - 100,y + 13,
                Util.rgbaToInt(255, 255 ,255 ,1));
        guiGraphics.blitSprite(BAR, x, y, 128, 32);
        guiGraphics.blitSprite(POINTER, x - 54 + pointerOffset, y, 128, 32);
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
