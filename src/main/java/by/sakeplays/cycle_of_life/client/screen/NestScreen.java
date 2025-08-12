package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.common.data.ClientNestData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.PairData;
import by.sakeplays.cycle_of_life.util.Util;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;


public class NestScreen extends Screen {


    public NestScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        Player player = Minecraft.getInstance().player;
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);
        PairData pairData = player.getData(DataAttachments.PAIRING_DATA);

        List<Pair<String, Integer>> stats = new ArrayList<>();

        if (ClientNestData.ownNest == null) return;

        if (!dinoData.isMale()) {
            if (pairData.getStoredEggs() <= 0) {
                stats.add(Pair.of("Eggs: Gestating", Util.rgbaToInt(255, 255, 60, 1)));
            } else {
                stats.add(Pair.of("Eggs: Ready " + "(" + pairData.getStoredEggs() + "/" + Util.getDino(player).getMaxEggs() + ")"
                        , Util.rgbaToInt(60, 255, 60, 1)));
            }
        }

        stats.add(Pair.of("Coordinates: " + ClientNestData.ownNest.getPos().toString(), Util.rgbaToInt(150, 255, 200, 1)));
        stats.add(Pair.of("Eggs: " + ClientNestData.ownNest.getEggsCount(), Util.rgbaToInt(150, 255, 200, 1)));


        renderStats(guiGraphics, stats);
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

    }


    public void renderStats(GuiGraphics guiGraphics, List<Pair<String, Integer>> stats) {
        int y = height/2 - 120;

        for (Pair<String, Integer> s : stats) {
            guiGraphics.drawString(Minecraft.getInstance().font, s.first(), width/2 - 60, y, s.second());
            y += Minecraft.getInstance().font.lineHeight;
        }
    }
}
