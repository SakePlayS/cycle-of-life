package by.sakeplays.cycle_of_life.event.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, modid = CycleOfLife.MODID, value = Dist.CLIENT)
public class HUDEvent {

    private static final ResourceLocation HEART = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "heart");
    private static final ResourceLocation NODE_0 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_0");
    private static final ResourceLocation NODE_1 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_1");
    private static final ResourceLocation NODE_2 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_2");
    private static final ResourceLocation NODE_3 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_3");
    private static final ResourceLocation NODE_4 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_4");
    private static final ResourceLocation NODE_5 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_5");
    private static final ResourceLocation NODE_6 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_6");
    private static final ResourceLocation NODE_7 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_7");
    private static final ResourceLocation NODE_8 = ResourceLocation.fromNamespaceAndPath(CycleOfLife.MODID,
            "node_8");


    @SubscribeEvent
    public static void renderHudElements(RenderGuiLayerEvent.Post event) {

        GuiGraphics graphics = event.getGuiGraphics();

        int x = graphics.guiWidth()/2;
        int y = graphics.guiHeight()/2;


        // WATER
        graphics.blitSprite(NODE_0, (int) (x * 0.9), (int) (y * 1.77),48 ,48);

        // FOOD
        graphics.blitSprite(NODE_0, (int) (x * 1.065), (int) (y * 1.77),48 ,48);

        // HEALTH
        graphics.blitSprite(NODE_0, x, (int) (y * 1.87),32 ,32);
        graphics.blitSprite(HEART, x, (int) (y * 1.87),32 ,32);

        // BLOOD
        graphics.blitSprite(NODE_0, x, (int) (y * 1.73),32 ,32);



    }
}
