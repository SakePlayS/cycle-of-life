package by.sakeplays.cycle_of_life.client.screen;


import by.sakeplays.cycle_of_life.common.data.ClientNestData;
import by.sakeplays.cycle_of_life.common.data.Nest;
import by.sakeplays.cycle_of_life.entity.util.Dinosaurs;
import by.sakeplays.cycle_of_life.network.to_server.RequestNestData;
import by.sakeplays.cycle_of_life.network.to_server.RequestNestJoinByType;
import by.sakeplays.cycle_of_life.network.to_server.RequestNestJoinByUUID;
import by.sakeplays.cycle_of_life.util.DataArrivalState;
import by.sakeplays.cycle_of_life.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NestListScreen extends Screen {

    private List<Integer> types = new ArrayList<>();
    private Map<Button, Integer> speciesButtons = new HashMap<>();
    private Button refreshButton;
    private EditBox nestUUIDEditBox;
    private Button joinButton;
    private Button backButton;

    public NestListScreen(Component title) {
        super(title);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (ClientNestData.dataArrivalState.equals(DataArrivalState.ARRIVED)) {
            ClientNestData.dataArrivalState = DataArrivalState.IDLE;
            createNestButtons();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (ClientNestData.getAvailableNests().isEmpty()) {

            if (ClientNestData.dataArrivalState == DataArrivalState.REQUESTED) {
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, "Loading...", width / 2, 100,
                        Util.rgbaToInt(255, 200, 0, 1));
            } else {
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, "No nests were found", width / 2, 100,
                        Util.rgbaToInt(255, 200, 0, 1));
            }
        }

        guiGraphics.drawString(Minecraft.getInstance().font, ClientNestData.nestFeedback, width/2 - 60, height - 72,
                Util.rgbaToInt(255, 255, 255, 1));
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void init() {
        super.init();

        ClientNestData.nestFeedback = "";

        backButton = new Button.Builder(Component.literal("Back"), button -> {
            Minecraft.getInstance().setScreen(new DinoSelectionScreen(Component.literal("Select your dinosaur")));
        }).size(120, 18).pos(width/2 - 60, height - 20).build();

        refreshButton = new Button.Builder(Component.literal("Refresh"), button -> {
            PacketDistributor.sendToServer(new RequestNestData());
            ClientNestData.dataArrivalState = DataArrivalState.REQUESTED;
        }).size(120, 18).pos(width/2 - 60, height - 40).build();


        joinButton = new Button.Builder(Component.literal("Send"), button -> {
            if (!nestUUIDEditBox.getValue().isEmpty()) {
                PacketDistributor.sendToServer(new RequestNestJoinByUUID(nestUUIDEditBox.getValue()));
            }
        }).size(40, 18).pos(width/2 + 20, height - 60).build();

        nestUUIDEditBox = new EditBox(Minecraft.getInstance().font, width/2 - 60, height - 60, 80, 18,
                Component.literal("Nest UUID"));
        nestUUIDEditBox.setMaxLength(48);
        nestUUIDEditBox.setHint(Component.literal("Nest UUID"));


        this.addRenderableWidget(refreshButton);
        this.addRenderableWidget(nestUUIDEditBox);
        this.addRenderableWidget(joinButton);
        this.addRenderableWidget(backButton);

    }

    private void createNestButtons() {
        this.clearWidgets();
        speciesButtons.clear();
        types.clear();

        int startY = 100;
        int spacing = 20;
        int i = 0;
        for (Nest nest : ClientNestData.nests) {

            if (types.contains(nest.getType())) continue;
            i++;

            int y = startY + i * spacing;

            String dinoNameRaw = Dinosaurs.getById(nest.getType()).toString();
            String dinoName = dinoNameRaw.charAt(0) + dinoNameRaw.substring(1).toLowerCase();

            Button button = Button.builder(Component.literal(dinoName), btn -> {

                if (speciesButtons.containsKey(btn)) PacketDistributor.sendToServer(new RequestNestJoinByType(speciesButtons.get(btn)));

            }).pos(this.width / 2 - 75, y)
                    .size(150, 18)
                    .build();

            speciesButtons.put(button, nest.getType());
            addRenderableWidget(button);
            types.add(nest.getType());
        }

        addRenderableWidget(refreshButton);
        addRenderableWidget(nestUUIDEditBox);
        addRenderableWidget(joinButton);
        addRenderableWidget(backButton);

    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

}
