package by.sakeplays.cycle_of_life.client.screen;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.client.screen.util.BrightnessSlider;
import by.sakeplays.cycle_of_life.client.screen.util.ColorOption;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.common.data.SelectedColors;
import by.sakeplays.cycle_of_life.entity.*;
import by.sakeplays.cycle_of_life.entity.util.ColorOptionArray;
import by.sakeplays.cycle_of_life.entity.util.ColorableBodyParts;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.network.bidirectional.SyncSkinData;
import by.sakeplays.cycle_of_life.network.to_server.RequestSelectDinosaur;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class SkinCreatorScreen extends Screen {


    private static final ResourceLocation COLOR_PAD = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "color_pad");

    private Button CONFIRM_BUTTON;

    SelectedColors selectedColors = new SelectedColors();
    ColorOptionArray colorOptions;
    List<Button> bodyPartButtons = new ArrayList<>();

    private int desiredDinosaurID;
    private final DinosaurEntity dummyDino;

    private ColorableBodyParts selectedBodyPart = ColorableBodyParts.EYES;
    private float modelYRot = 0;
    private float modelXRot = 0;

    public SkinCreatorScreen(Component title, int desiredDinoID) {
        super(title);
        desiredDinosaurID = desiredDinoID;
        dummyDino = getDinoToRender(desiredDinosaurID);

        colorOptions = Dinosaurs.getById(desiredDinosaurID).getColorOptions();

        for (Map.Entry<ColorableBodyParts, List<Pair<ColorOption, ColorOption>>> entry : colorOptions.getColorOptions().entrySet()) {
            selectedColors.setColor(entry.getKey(), entry.getValue().getFirst().first().toInt(), entry.getValue().getFirst().second().toInt());
        }

    }

    @Override
    public void tick() {
        super.tick();

        for (ColorableBodyParts part : ColorableBodyParts.values()) {
            copyColorToDummyDino(part);
        }

    }

    private void copyColorToDummyDino(ColorableBodyParts part) {


        Pair<Integer, Integer> partColor =
                (colorOptions.getColorOptions().containsKey(part)) ?
                selectedColors.getColor(part) :
                Pair.of(new ColorOption(0,0,0).toInt(), new ColorOption(0,0,0).toInt());

        if (part == ColorableBodyParts.MALE_DISPLAY && !Minecraft.getInstance().player.getData(DataAttachments.DINO_DATA).isMale()) {
            partColor = selectedColors.getColor(ColorableBodyParts.MARKINGS);
        }


        dummyDino.colors.setColor(part, partColor.first(), partColor.second());

    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);


        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int startX = 50;
        int startY = 50;
        int size = 20;
        int padding = 8;

        guiGraphics.blitSprite(COLOR_PAD, 20, 20, 192, 192);

        for (int i = 0; i < getColorPaletteForSelectedPart().size(); i++) {
            Pair<ColorOption, ColorOption> color = getColorPaletteForSelectedPart().get(i);
            int x = startX + (i % 5) * (size + padding);
            int y = startY + (i / 5) * (size + padding);


            for (int j = 0; j < size; j++) {

                int r = (int) Mth.lerp((float) j /size, color.first().r(), color.second().r());
                int g = (int) Mth.lerp((float) j /size, color.first().g(), color.second().g());
                int b = (int) Mth.lerp((float) j /size, color.first().b(), color.second().b());

                guiGraphics.fill(x, y + j, x + size, y + 1 + j, new ColorOption(r, g, b).toInt());

            }

            Pair<Integer, Integer> partColor = selectedColors.getColor(selectedBodyPart);

            if (partColor.first() == color.first().toInt() && partColor.second() == color.second().toInt()) {
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
        int padding = 8;

        for (int i = 0; i < getColorPaletteForSelectedPart().size(); i++) {
            int x = startX + (i % 5) * (size + padding);
            int y = startY + (i / 5) * (size + padding);

            if (mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size) {
                Pair<ColorOption, ColorOption> color = getColorPaletteForSelectedPart().get(i);

                selectedColors.setColor(selectedBodyPart, color.first().toInt(), color.second().toInt());

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
                    selectedColors));
            Minecraft.getInstance().setScreen(null);
        }).size(120, 18).pos(width/2 - 60, height - 40).build();

        int buttonY = 20;
        for (Map.Entry<ColorableBodyParts, List<Pair<ColorOption, ColorOption>>> entry : colorOptions.getColorOptions().entrySet()) {
            if (!Minecraft.getInstance().player.getData(DataAttachments.DINO_DATA).isMale() && entry.getKey() == ColorableBodyParts.MALE_DISPLAY) continue;
            bodyPartButtons.add(createSkinButton(entry.getKey().translationKey, entry.getKey(), width - 200, buttonY));
            buttonY += 20;
        }



        addRenderableWidget(CONFIRM_BUTTON);
        for (Button button : bodyPartButtons) {
            addRenderableWidget(button);
        }
    }

    private Button createSkinButton(String translationKey, ColorableBodyParts bodyPart, int x, int y) {
        return new Button.Builder(Component.translatable(translationKey), button -> {

            selectedBodyPart = bodyPart;
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


    private List<Pair<ColorOption, ColorOption>> getColorPaletteForSelectedPart() {
        return colorOptions.getColorOptions().get(selectedBodyPart);
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
}
