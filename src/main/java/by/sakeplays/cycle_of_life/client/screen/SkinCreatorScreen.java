package by.sakeplays.cycle_of_life.client.screen;

import by.sakeplays.cycle_of_life.client.screen.util.ColorOption;
import by.sakeplays.cycle_of_life.client.screen.util.Colors;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.COLEntities;
import by.sakeplays.cycle_of_life.entity.Deinonychus;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncSkinData;
import by.sakeplays.cycle_of_life.network.to_server.RequestSelectDinosaur;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class SkinCreatorScreen extends Screen {

    private Button CONFIRM_BUTTON;

    private Button MALE_DISPLAY_BUTTON; // 1
    private Button MARKINGS_BUTTON;     // 2
    private Button BODY_BUTTON;         // 3
    private Button FLANK_BUTTON;        // 4
    private Button BELLY_BUTTON;        // 5
    private Button EYES_BUTTON;         // 6


    private ColorOption selectedColor;
    private static int desiredDinosaurID;
    private final Deinonychus dummyDino = new Deinonychus(COLEntities.DEINONYCHUS.get(), Minecraft.getInstance().level);

    private int selectedBodyPart = 2;
    private float modelYRot = 0;
    private float modelXRot = 0;

    public SkinCreatorScreen(Component title, int desiredDinoID) {
        super(title);
        desiredDinosaurID = desiredDinoID;
    }

    @Override
    public void tick() {
        super.tick();

        Player player = Minecraft.getInstance().player;

        if (!player.getData(DataAttachments.DINO_DATA).isMale()) {
            dummyDino.maleDisplayColor = dummyDino.markingsColor;
            MALE_DISPLAY_BUTTON.active = false;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);


        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int startX = 50;
        int startY = 50;
        int size = 20;
        int padding = 4;

        for (int i = 0; i < getColorPaletteForSelectedPart().size(); i++) {
            ColorOption color = getColorPaletteForSelectedPart().get(i);
            int x = startX + (i % 5) * (size + padding);
            int y = startY + (i / 5) * (size + padding);

            guiGraphics.fill(x, y, x + size, y + size, color.toInt());

            if (color.equals(selectedColor)) {
                guiGraphics.drawCenteredString(this.font, "✔", x + size / 2, y + size / 2 - 4, 0xFFFFFF);
            }
        }

        renderDinoInScreen(centerX, (int) (centerY * 1.2f), 60);

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 50;
        int startY = 50;
        int size = 20;
        int padding = 4;

        for (int i = 0; i < getColorPaletteForSelectedPart().size(); i++) {
            int x = startX + (i % 5) * (size + padding);
            int y = startY + (i / 5) * (size + padding);

            if (mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size) {

                selectedColor = getColorPaletteForSelectedPart().get(i);
                int color = selectedColor.toInt();

                switch (selectedBodyPart) {
                    case 1:
                        dummyDino.maleDisplayColor = color;
                        break;
                    case 2:
                        dummyDino.markingsColor = color;
                        break;
                    case 3:
                        dummyDino.bodyColor = color;
                        break;
                    case 4:
                        dummyDino.flankColor = color;
                        break;
                    case 5:
                        dummyDino.bellyColor = color;
                        break;
                    case 6:
                        dummyDino.eyesColor = color;
                        break;
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        CONFIRM_BUTTON = new Button.Builder(Component.literal("Confirm"), button -> {
            Minecraft.getInstance().player.getData(DataAttachments.DINO_DATA).setSelectedDinosaur(desiredDinosaurID);
            PacketDistributor.sendToServer(new RequestSelectDinosaur(desiredDinosaurID));

            PacketDistributor.sendToServer(new SyncSkinData(Minecraft.getInstance().player.getId(),
                    dummyDino.eyesColor, dummyDino.markingsColor, dummyDino.bodyColor,
                    dummyDino.flankColor, dummyDino.bellyColor, dummyDino.maleDisplayColor));
            Minecraft.getInstance().setScreen(null);
        }).size(120, 18).pos(width/2 - 60, height - 40).build();

        MALE_DISPLAY_BUTTON = new Button.Builder(Component.literal("Male Display"), button -> {
            selectedBodyPart = 1;
            MARKINGS_BUTTON.setFocused(false);

        }).size(120, 18).pos(width - 200, 20).tooltip(Tooltip.create(Component.literal("Only for males!"))).build();

        MARKINGS_BUTTON = new Button.Builder(Component.literal("Markings"), button -> {
            selectedBodyPart = 2;

        }).size(120, 18).pos(width - 200, 40).build();

        BODY_BUTTON = new Button.Builder(Component.literal("Body"), button -> {
            selectedBodyPart = 3;
            MARKINGS_BUTTON.setFocused(false);

        }).size(120, 18).pos(width - 200, 60).build();

        FLANK_BUTTON = new Button.Builder(Component.literal("Flank"), button -> {
            selectedBodyPart = 4;
            MARKINGS_BUTTON.setFocused(false);


        }).size(120, 18).pos(width - 200, 80).build();

        BELLY_BUTTON = new Button.Builder(Component.literal("Belly"), button -> {
            selectedBodyPart = 5;
            MARKINGS_BUTTON.setFocused(false);

        }).size(120, 18).pos(width - 200, 100).build();

        EYES_BUTTON = new Button.Builder(Component.literal("Eyes"), button -> {
            selectedBodyPart = 6;
            MARKINGS_BUTTON.setFocused(false);

        }).size(120, 18).pos(width - 200, 120).build();


        addRenderableWidget(CONFIRM_BUTTON);
        addRenderableWidget(MALE_DISPLAY_BUTTON);
        addRenderableWidget(MARKINGS_BUTTON);
        addRenderableWidget(BODY_BUTTON);
        addRenderableWidget(FLANK_BUTTON);
        addRenderableWidget(BELLY_BUTTON);
        addRenderableWidget(EYES_BUTTON);

        MARKINGS_BUTTON.setFocused(true);

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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {

        modelYRot += (float) dragX;
        modelXRot -= (float) dragY;

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public void renderDinoInScreen(int x, int y, int scale) {

        RenderSystem.enableDepthTest();
        Lighting.setupForEntityInInventory();

        PoseStack modelPose = new PoseStack();
        modelPose.translate(x, y, 100.0D);
        modelPose.scale((float)scale, (float)scale, (float)scale);
        modelPose.mulPose(Axis.YP.rotationDegrees(modelYRot));
        modelPose.mulPose(Axis.XP.rotationDegrees(180));
        modelPose.mulPose(Axis.XP.rotationDegrees(modelXRot));


        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        dummyDino.playerId = mc.player.getId();
        dummyDino.isForScreenRendering = true;
        try {
            dispatcher.render(dummyDino, 0, 0, 0, 0.0F, 1.0F, modelPose, buffer, 15728880);
            buffer.endBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dispatcher.setRenderShadow(false);
        modelPose.popPose();
    }


    public static final List<ColorOption> MALE_DISPLAY = Colors.LOAD_MALE_DISPLAY_COLORS(desiredDinosaurID);
    public static final List<ColorOption> EYES = Colors.EYE_COLORS;
    public static final List<ColorOption> BODY = Colors.LOAD_BODY_COLORS(desiredDinosaurID);
    public static final List<ColorOption> FLANK = Colors.LOAD_FLANK_COLORS(desiredDinosaurID);
    public static final List<ColorOption> BELLY = Colors.LOAD_BELLY_COLORS(desiredDinosaurID);
    public static final List<ColorOption> MARKINGS = Colors.LOAD_MARKINGS_COLORS(desiredDinosaurID);

    private List<ColorOption> getColorPaletteForSelectedPart() {
        return switch (selectedBodyPart) {
            case 1 -> MALE_DISPLAY;
            case 2 -> MARKINGS;
            case 3 -> BODY;
            case 4 -> FLANK;
            case 5 -> BELLY;
            case 6 -> EYES;
            default -> BODY;
        };
    }

}
