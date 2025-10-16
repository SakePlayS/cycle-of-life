package by.sakeplays.cycle_of_life;

import by.sakeplays.cycle_of_life.block.ModBlockEntities;
import by.sakeplays.cycle_of_life.block.ModBlocks;
import by.sakeplays.cycle_of_life.client.block.crimsonweed.CrimsonweedRenderer;
import by.sakeplays.cycle_of_life.client.block.deinonychus_nest.DeinonychusNestRenderer;
import by.sakeplays.cycle_of_life.client.entity.FoodEntityRenderer;
import by.sakeplays.cycle_of_life.client.entity.deinonychus.DeinonychusRenderer;
import by.sakeplays.cycle_of_life.client.entity.HitboxRenderer;
import by.sakeplays.cycle_of_life.client.entity.pachycephalosaurus.PachycephalosaurusRenderer;
import by.sakeplays.cycle_of_life.client.entity.pteranodon.PteranodonRenderer;
import by.sakeplays.cycle_of_life.common.data.DataAttachments;
import by.sakeplays.cycle_of_life.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CycleOfLife.MODID)
public class CycleOfLife
{
    public static final String MODID = "cycle_of_life";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    public static final DeferredItem<BlockItem> CRIMSONWEED_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("crimsonweed", ModBlocks.CRIMSONWEED);


    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cycle_of_life")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> CRIMSONWEED_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CRIMSONWEED_BLOCK_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    public CycleOfLife(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModEntities.register(modEventBus);
        DataAttachments.register(modEventBus);
        ModSounds.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModBlocks.register(modEventBus);


        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(ModEntities.PACHYCEPHALOSAURUS.get(), PachycephalosaurusRenderer::new);
            EntityRenderers.register(ModEntities.DEINONYCHUS.get(), DeinonychusRenderer::new);
            EntityRenderers.register(ModEntities.HITBOX.get(), HitboxRenderer::new);
            EntityRenderers.register(ModEntities.FOOD_ENTITY.get(), FoodEntityRenderer::new);
            EntityRenderers.register(ModEntities.PTERANODON.get(), PteranodonRenderer::new);

        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.DEINONYCHUS_NEST_BLOCK_ENTITY.get(), DeinonychusNestRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.CRIMSONWEED_BLOCK_ENTITY.get(), CrimsonweedRenderer::new);

        }
    }
}
