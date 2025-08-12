package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.common.data.ClientNestData;
import by.sakeplays.cycle_of_life.util.Util;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;


public class NestListScreen extends Screen {


    public NestListScreen(Component title) {
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


    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

    }

}
