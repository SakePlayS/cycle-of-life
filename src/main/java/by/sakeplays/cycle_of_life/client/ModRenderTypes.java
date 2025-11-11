package by.sakeplays.cycle_of_life.client;

import by.sakeplays.cycle_of_life.CycleOfLife;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = CycleOfLife.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class ModRenderTypes {


    private static ShaderInstance GRAYSCALE_TINTED_SHADER;

    public static final ResourceLocation SHADER_LOCATION = ResourceLocation.fromNamespaceAndPath("cycle_of_life", "grayscale_tinted");

    public static ShaderInstance getGrayscaleTintedShader() {
        if (GRAYSCALE_TINTED_SHADER == null)
            return null;
        return GRAYSCALE_TINTED_SHADER;
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(), SHADER_LOCATION, DefaultVertexFormat.NEW_ENTITY),
                shader -> GRAYSCALE_TINTED_SHADER = shader
        );
    }

    public static RenderType grayscaleTinted(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(new RenderStateShard.ShaderStateShard(ModRenderTypes::getGrayscaleTintedShader))
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);

        return RenderType.create(
                "grayscale_tinted",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                true,
                true,
                state
        );
    }

}
