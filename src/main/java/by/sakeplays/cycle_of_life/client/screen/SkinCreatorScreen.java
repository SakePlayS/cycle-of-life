package by.sakeplays.cycle_of_life.client.screen;

import by.sakeplays.cycle_of_life.client.screen.util.BodyPartColors;
import by.sakeplays.cycle_of_life.client.screen.util.BrightnessSlider;
import by.sakeplays.cycle_of_life.client.screen.util.ColorOption;
import by.sakeplays.cycle_of_life.client.screen.util.Colors;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.*;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncSkinData;
import by.sakeplays.cycle_of_life.network.to_server.RequestSelectDinosaur;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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

    private BrightnessSlider BRIGHTNESS;
    private boolean changingBrightness;


    public final List<ColorOption> MALE_DISPLAY;
    public final List<ColorOption> EYES = Colors.EYE_COLORS;
    public final List<ColorOption> BODY;
    public final List<ColorOption> FLANK;
    public final List<ColorOption> BELLY;
    public final List<ColorOption> MARKINGS;


    private Double maleDisplayBrightness = 1d;
    private Double markingsBrightness = 1d;
    private Double bodyBrightness = 1d;
    private Double flankBrightness = 1d;
    private Double bellyBrightness = 1d;
    private Double eyesBrightness = 1d;

    private BodyPartColors bodyPartColors;
    private int desiredDinosaurID;
    private final DinosaurEntity dummyDino;

    private int selectedBodyPart = 2;
    private float modelYRot = 0;
    private float modelXRot = 0;

    public SkinCreatorScreen(Component title, int desiredDinoID) {
        super(title);
        desiredDinosaurID = desiredDinoID;
        dummyDino = getDinoToRender(desiredDinosaurID);

        MALE_DISPLAY = Colors.LOAD_MALE_DISPLAY_COLORS(desiredDinosaurID);
        BODY =  Colors.LOAD_BODY_COLORS(desiredDinosaurID);
        FLANK = Colors.LOAD_FLANK_COLORS(desiredDinosaurID);
        BELLY = Colors.LOAD_BELLY_COLORS(desiredDinosaurID);
        MARKINGS = Colors.LOAD_MARKINGS_COLORS(desiredDinosaurID);

        bodyPartColors = new BodyPartColors(MALE_DISPLAY.getFirst(), MARKINGS.getFirst(), BODY.getFirst(),
                FLANK.getFirst(), BELLY.getFirst(), EYES.getFirst());
    }

    @Override
    public void tick() {
        super.tick();

        Player player = Minecraft.getInstance().player;

        if (!player.getData(DataAttachments.DINO_DATA).isMale()) {
            dummyDino.maleDisplayColor = dummyDino.markingsColor;
            MALE_DISPLAY_BUTTON.active = false;
        }

        switch (selectedBodyPart) {
            case 1 -> {
                BRIGHTNESS.setSliderValue(maleDisplayBrightness);
            }
            case 2 -> {
                BRIGHTNESS.setSliderValue(markingsBrightness);
            }
            case 3 -> {
                BRIGHTNESS.setSliderValue(bodyBrightness);
            }
            case 4 -> {
                BRIGHTNESS.setSliderValue(flankBrightness);
            }
            case 5 -> {
                BRIGHTNESS.setSliderValue(bellyBrightness);
            }
            case 6 -> {
                BRIGHTNESS.setSliderValue(eyesBrightness);
            }
        }

        dummyDino.maleDisplayColor = player.getData(DataAttachments.DINO_DATA).isMale() ?
                (processColor(bodyPartColors.getMaleDisplay(), maleDisplayBrightness)) :
                (processColor(bodyPartColors.getMarkings(), markingsBrightness));
        dummyDino.markingsColor = (processColor(bodyPartColors.getMarkings(), markingsBrightness));
        dummyDino.bodyColor = (processColor(bodyPartColors.getBody(), bodyBrightness));
        dummyDino.flankColor = (processColor(bodyPartColors.getFlank(), flankBrightness));
        dummyDino.bellyColor = (processColor(bodyPartColors.getBelly(), bellyBrightness));
        dummyDino.eyesColor = (processColor(bodyPartColors.getEyes(), eyesBrightness));
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

            ColorOption partColor = switch (selectedBodyPart) {
                case 1 -> bodyPartColors.getMaleDisplay();
                case 2 -> bodyPartColors.getMarkings();
                case 3 -> bodyPartColors.getBody();
                case 4 -> bodyPartColors.getFlank();
                case 5 -> bodyPartColors.getBelly();
                case 6 -> bodyPartColors.getEyes();
                default -> new ColorOption(0,0,0, 1, 1);
            };

            if (color.equals(partColor)) {
                guiGraphics.drawCenteredString(this.font, "✔", x + size / 2, y + size / 2 - 4, 0xFFFFFF);
            }
        }


        renderDinoInScreen(centerX, (int) (centerY * 1.2f), 60);
        guiGraphics.drawString(this.font, "Body", width/2, height/2,  + dummyDino.bodyColor);
        guiGraphics.drawString(this.font, "Body", width/2, height/2 + 20,  + bodyPartColors.getBody().toInt());

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
                ColorOption color = getColorPaletteForSelectedPart().get(i);

                switch (selectedBodyPart) {
                    case 1:
                        bodyPartColors.setMaleDisplay(color);
                        break;
                    case 2:
                        bodyPartColors.setMarkings(color);
                        break;
                    case 3:
                        bodyPartColors.setBody(color);
                        break;
                    case 4:
                        bodyPartColors.setFlank(color);
                        break;
                    case 5:
                        bodyPartColors.setBelly(color);
                        break;
                    case 6:
                        bodyPartColors.setEyes(color);
                        break;
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
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

        MALE_DISPLAY_BUTTON = createSkinButton("Male Display", 1, width - 200, 20);
        MARKINGS_BUTTON = createSkinButton("Markings", 2, width - 200, 40);
        BODY_BUTTON = createSkinButton("Body", 3, width - 200, 60);
        FLANK_BUTTON = createSkinButton("Flank", 4, width - 200, 80);
        BELLY_BUTTON = createSkinButton("Belly", 5, width - 200, 100);
        EYES_BUTTON = createSkinButton("Eyes", 6, width - 200, 120);

        BRIGHTNESS = new BrightnessSlider(50, height - 40, 120, 20, Component.literal("Brightness"), 1d, val -> {

            switch (selectedBodyPart) {
                case 1 -> maleDisplayBrightness = val;
                case 2 -> markingsBrightness = val;
                case 3 -> bodyBrightness = val;
                case 4 -> flankBrightness = val;
                case 5 -> bellyBrightness = val;
                case 6 -> eyesBrightness = val;
            }
        }) {
            @Override
            public void onRelease(double mouseX, double mouseY) {
                super.onRelease(mouseX, mouseY);

                changingBrightness = false;
            }

            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                super.onClick(mouseX, mouseY, button);

                changingBrightness = true;
            }
        };

        addRenderableWidget(CONFIRM_BUTTON);
        addRenderableWidget(MALE_DISPLAY_BUTTON);
        addRenderableWidget(MARKINGS_BUTTON);
        addRenderableWidget(BODY_BUTTON);
        addRenderableWidget(FLANK_BUTTON);
        addRenderableWidget(BELLY_BUTTON);
        addRenderableWidget(EYES_BUTTON);
        addRenderableWidget(BRIGHTNESS);

        MARKINGS_BUTTON.setFocused(true);
    }

    private Button createSkinButton(String name, int bodyPartId, int x, int y) {
        return new Button.Builder(Component.literal(name), button -> {
            selectedBodyPart = bodyPartId;
            MARKINGS_BUTTON.setFocused(false);
        }).size(120, 18).pos(x, y).build();
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
        if (!changingBrightness) {
            modelYRot += (float) dragX;
            modelXRot -= (float) dragY;
        }

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


    private DinosaurEntity getDinoToRender(int desiredDinosaurID) {
        if (desiredDinosaurID == Dinosaurs.DEINONYCHUS.getID())
            return new Deinonychus(ModEntities.DEINONYCHUS.get(), Minecraft.getInstance().level);

        if (desiredDinosaurID == Dinosaurs.PACHYCEPHALOSAURUS.getID())
            return new Pachycephalosaurus(ModEntities.PACHYCEPHALOSAURUS.get(), Minecraft.getInstance().level);

        if (desiredDinosaurID == Dinosaurs.PTERANODON.getID())
            return new Pteranodon(ModEntities.PTERANODON.get(), Minecraft.getInstance().level);

        // fallback
        return new Deinonychus(ModEntities.DEINONYCHUS.get(), Minecraft.getInstance().level);
    }

    private int processColor(ColorOption colorOption, double brightness) {

        double modifier = Mth.lerp(brightness, 0.5, 1f);

        return new ColorOption((int)(colorOption.r() * modifier), (int)(colorOption.g() * modifier), (int)(colorOption.b() * modifier), 1f, 1f).toInt();
    }


}
