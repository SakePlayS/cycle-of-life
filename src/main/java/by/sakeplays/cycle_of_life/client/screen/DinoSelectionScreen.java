package by.sakeplays.cycle_of_life.client.screen;

import by.sakeplays.cycle_of_life.CycleOfLife;
import by.sakeplays.cycle_of_life.entity.util.Diet;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DinoSelectionScreen extends Screen {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "textures/gui/dino_selection_screen/dino_selection_screen_bg.png");
    private int desiredDinosaurID = 0;
    private Diet selectedDiet = Diet.HERBIVORE;
    private Button CONFIRM_BUTTON;

    // herbivores
    private Button PACHYCEPHALOSAURUS_BUTTON;
    private Button DRYOSAURUS_BUTTON;
    private Button GALLIMIMUS_BUTTON;
    private Button PARKSOSAURUS_BUTTON;

    // omnivores
    private Button LATENIVENATRIX_BUTTON;

    // carnivores
    private Button DEINONYCHUS_BUTTON;
    private Button AUSTRORAPTOR_BUTTON;
    private Button UTAHRAPTOR_BUTTON;
    private Button ORNITHOLESTES_BUTTON;
    private Button QUETZALCOATLUS_BUTTON;

    private Button HERBIVORES_BUTTON;
    private Button CARNIVORES_BUTTON;
    private Button OMNIVORES_BUTTON;

    public DinoSelectionScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();

        if (CONFIRM_BUTTON != null) {
            CONFIRM_BUTTON.active = (desiredDinosaurID != 0);
        }

        LATENIVENATRIX_BUTTON.active = false;
        DRYOSAURUS_BUTTON.active = false;
        UTAHRAPTOR_BUTTON.active = false;
        AUSTRORAPTOR_BUTTON.active = false;
        GALLIMIMUS_BUTTON.active = false;
        PARKSOSAURUS_BUTTON.active = false;
        ORNITHOLESTES_BUTTON.active = false;
        QUETZALCOATLUS_BUTTON.active = false;

        tickButtons();
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

        CONFIRM_BUTTON = new Button.Builder(Component.literal("Next"), button -> {
            if (desiredDinosaurID != 0 && desiredDinosaurID <= 2) {
                Minecraft.getInstance().setScreen(new SkinCreatorScreen(Component.literal("Skin Creation"), desiredDinosaurID));
            }
        }).size(120, 18).pos(width/2 - 60, height - 40).build();

        OMNIVORES_BUTTON = new Button.Builder(Component.literal("Omnivores"), button -> {
            selectedDiet = Diet.OMNIVORE;
            OMNIVORES_BUTTON.active = false;
            HERBIVORES_BUTTON.active = true;
            CARNIVORES_BUTTON.active = true;
        }).size(70, 18).pos(width/2 - 35,  40).build();

        HERBIVORES_BUTTON = new Button.Builder(Component.literal("Herbivores"), button -> {
            selectedDiet = Diet.HERBIVORE;
            HERBIVORES_BUTTON.active = false;
            CARNIVORES_BUTTON.active = true;
            OMNIVORES_BUTTON.active = true;
        }).size(70, 18).pos(width/2 - 105,  40).build();

        CARNIVORES_BUTTON = new Button.Builder(Component.literal("Carnivores"), button -> {
            selectedDiet = Diet.CARNIVORE;
            CARNIVORES_BUTTON.active = false;
            OMNIVORES_BUTTON.active = true;
            HERBIVORES_BUTTON.active = true;
        }).size(70, 18).pos(width/2 + 35,  40).build();



        PACHYCEPHALOSAURUS_BUTTON =  new Button.Builder(Component.literal("Pachycephalosaurus"), button -> {
            desiredDinosaurID = Dinosaurs.PACHYCEPHALOSAURUS.getID();
        }).size(150, 18).pos(width/2 - 75, height/2 - 40)
                .tooltip(Tooltip.create(Component.literal("Pachy desc"))).build();

        GALLIMIMUS_BUTTON = new Button.Builder(Component.literal("Gallimimus"),button -> {
            desiredDinosaurID = 0;
        }).size(150, 18).pos(width/2 - 75, height/2 - 20)
                .tooltip(Tooltip.create(Component.literal("galli desc"))).build();

        DRYOSAURUS_BUTTON = new Button.Builder(Component.literal("Dryosaurus"), button -> {
            desiredDinosaurID = 0;
        }).size(150, 18).pos(width/2 - 75, height/2)
                .tooltip(Tooltip.create(Component.literal("dryo desc"))).build();

        PARKSOSAURUS_BUTTON = new Button.Builder(Component.literal("Parksosaurus"), button -> {
            desiredDinosaurID = 0;
        }).size(150, 18).pos(width/2 - 75, height/2 + 20)
                .tooltip(Tooltip.create(Component.literal("parkso desc"))).build();




        DEINONYCHUS_BUTTON = new Button.Builder(Component.literal("Deinonychus"), button -> {
            desiredDinosaurID = Dinosaurs.DEINONYCHUS.getID();
        }).size(150, 18).pos(width/2 - 75, height/2 - 40)
                .tooltip(Tooltip.create(Component.literal("This small dromaeosaurid is incredibly fragile, but its outstanding stamina and agility more than make up for it. A very effective pack hunter."))).build();

        UTAHRAPTOR_BUTTON = new Button.Builder(Component.literal("Utahraptor"), button -> {
            desiredDinosaurID = 0;
        }).size(150, 18).pos(width/2 - 75, height/2 - 20)
                .tooltip(Tooltip.create(Component.literal("Utah desc"))).build();

        AUSTRORAPTOR_BUTTON = new Button.Builder(Component.literal("Austroraptor"), button -> {
            desiredDinosaurID = 0;
        }).size(150, 18).pos(width/2 - 75, height/2)
                .tooltip(Tooltip.create(Component.literal("Austro desc"))).build();

        ORNITHOLESTES_BUTTON = new Button.Builder(Component.literal("Ornitholestes"), button -> {
            desiredDinosaurID = 0;
        }).size(150, 18).pos(width/2 - 75, height/2 + 20)
                .tooltip(Tooltip.create(Component.literal("Orni desc"))).build();

        QUETZALCOATLUS_BUTTON = new Button.Builder(Component.literal("Quetzalcoatlus"), button -> {
            desiredDinosaurID = 0;
        }).size(150, 18).pos(width/2 - 75, height/2 + 40)
                .tooltip(Tooltip.create(Component.literal("Quetz desc"))).build();



        LATENIVENATRIX_BUTTON = new Button.Builder(Component.literal("Latenivenatrix"), button -> {
            desiredDinosaurID = 0;
        }).size(150, 18).pos(width/2 - 75, height/2 - 40)
                .tooltip(Tooltip.create(Component.literal("lateni desc"))).build();



        addRenderableWidget(CONFIRM_BUTTON);

        addRenderableWidget(HERBIVORES_BUTTON);
        addRenderableWidget(CARNIVORES_BUTTON);
        addRenderableWidget(OMNIVORES_BUTTON);


        addRenderableWidget(PACHYCEPHALOSAURUS_BUTTON);
        addRenderableWidget(DRYOSAURUS_BUTTON);
        addRenderableWidget(GALLIMIMUS_BUTTON);
        addRenderableWidget(PARKSOSAURUS_BUTTON);

        addRenderableWidget(DEINONYCHUS_BUTTON);
        addRenderableWidget(UTAHRAPTOR_BUTTON);
        addRenderableWidget(AUSTRORAPTOR_BUTTON);
        addRenderableWidget(ORNITHOLESTES_BUTTON);
        addRenderableWidget(QUETZALCOATLUS_BUTTON);

        addRenderableWidget(LATENIVENATRIX_BUTTON);

        HERBIVORES_BUTTON.active = false;

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

    private void tickButtons() {
        DEINONYCHUS_BUTTON.visible = selectedDiet == Diet.CARNIVORE;
        UTAHRAPTOR_BUTTON.visible = selectedDiet == Diet.CARNIVORE;
        AUSTRORAPTOR_BUTTON.visible = selectedDiet == Diet.CARNIVORE;
        ORNITHOLESTES_BUTTON.visible = selectedDiet == Diet.CARNIVORE;
        QUETZALCOATLUS_BUTTON.visible = selectedDiet == Diet.CARNIVORE;

        PACHYCEPHALOSAURUS_BUTTON.visible = selectedDiet == Diet.HERBIVORE;
        DRYOSAURUS_BUTTON.visible = selectedDiet == Diet.HERBIVORE;
        GALLIMIMUS_BUTTON.visible = selectedDiet == Diet.HERBIVORE;
        PARKSOSAURUS_BUTTON.visible = selectedDiet == Diet.HERBIVORE;

        LATENIVENATRIX_BUTTON.visible = selectedDiet == Diet.OMNIVORE;

    }

}
