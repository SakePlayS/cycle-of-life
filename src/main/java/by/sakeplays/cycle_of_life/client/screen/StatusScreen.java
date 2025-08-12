package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.common.data.ClientNestData;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.DinoData;
import by.sakeplays.cycle_of_life.common.data.StatusData;
import by.sakeplays.cycle_of_life.util.Util;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.codehaus.plexus.util.dag.DAG;

import java.util.ArrayList;
import java.util.List;


public class StatusScreen extends Screen {


    public StatusScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        List<Pair<String, Integer>> stats = new ArrayList<>();

        Player player = Minecraft.getInstance().player;
        StatusData statusData = player.getData(DataAttachments.STATUS_DATA);
        DinoData dinoData = player.getData(DataAttachments.DINO_DATA);

        float healthRatio = dinoData.getHealth() / dinoData.getWeight();
        float bloodRatio = dinoData.getBloodLevel() / dinoData.getWeight();

        if (statusData.isGestatingEggs()) stats.add(Pair.of("- Gestating eggs", Util.rgbaToInt(255, 230, 0, 1)));
        if (healthRatio < 0.25f) {
            stats.add(Pair.of("- Severely wounded", Util.rgbaToInt(255, 75, 0, 1)));
        } else if (healthRatio < 0.5f) {
            stats.add(Pair.of("- Wounded", Util.rgbaToInt(255, 150, 0, 1)));
        }

        if (bloodRatio < 0.25f) {
            stats.add(Pair.of("- Severe blood loss", Util.rgbaToInt(255, 75, 0, 1)));
        } else if (bloodRatio < 0.5f) {
            stats.add(Pair.of("- Blood loss", Util.rgbaToInt(255, 150, 0, 1)));
        }


        if (stats.isEmpty()) stats.add(Pair.of("- All good!", Util.rgbaToInt(150, 255, 200, 1)));

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

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void renderStats(GuiGraphics guiGraphics, List<Pair<String, Integer>> stats) {
        int y = height/2 - 120;

        for (Pair<String, Integer> s : stats) {
            guiGraphics.drawString(Minecraft.getInstance().font, s.first(), width/2 - 60, y, s.second());
            y += Minecraft.getInstance().font.lineHeight;
        }

    }
}
