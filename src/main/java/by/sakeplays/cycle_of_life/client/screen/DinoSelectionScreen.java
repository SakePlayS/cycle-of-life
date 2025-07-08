package by.sakeplays.cycle_of_life.client.screen;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.network.to_server.RequestSelectDinosaur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class DinoSelectionScreen extends Screen {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "textures/gui/dino_selection_screen/dino_selection_screen_bg.png");
    private int desiredDinosaurID = 0;
    private Button CONFIRM_BUTTON;

    public DinoSelectionScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();

        if (CONFIRM_BUTTON != null) {
            CONFIRM_BUTTON.active = (desiredDinosaurID != 0);
        }
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

        CONFIRM_BUTTON = new Button.Builder(Component.literal("Confirm"), button -> {
            if (desiredDinosaurID != 0) {
                Minecraft.getInstance().player.getData(DataAttachments.DINO_DATA).setSelectedDinosaur(desiredDinosaurID);
                PacketDistributor.sendToServer(new RequestSelectDinosaur(desiredDinosaurID));
                Minecraft.getInstance().setScreen(null);
            }
        }).size(120, 18).pos(width/2 - 60, height - 40).build();

        addRenderableWidget(new Button.Builder(Component.literal("Pachycephalosaurus"),button -> {
            desiredDinosaurID = Dinosaurs.PACHYCEPHALOSAURUS.getID();
        }).size(150, 18).pos(width/2 - 75, height/2).build());

        addRenderableWidget(new Button.Builder(Component.literal("Deinonychus"),button -> {
            desiredDinosaurID = Dinosaurs.DEINONYCHUS.getID();
        }).size(150, 18).pos(width/2 - 75, height/2 + 25).build());


        addRenderableWidget(CONFIRM_BUTTON);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
