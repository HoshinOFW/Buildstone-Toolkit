package com.github.hoshinofw.buildstonetoolkit.common.particles.TargetBlockParticle;

import com.github.hoshinofw.buildstonetoolkit.client.Sprites;
import com.github.hoshinofw.buildstonetoolkit.common.level.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.core.BuildstoneToolkit;
import com.github.hoshinofw.buildstonetoolkit.util.client.BlockParticleTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

public class BlockParticle extends TextureSheetParticle {

    private SpriteSet sprites;
    private static final BlockParticleRenderProvider renderer = (Platform.isFabric() ? new FabricRenderer() : new NFRenderer());

    float minU;
    float maxU;
    float minV;
    float maxV;

    protected BlockParticle(ClientLevel clientLevel, BlockPos pos, SpriteSet spriteSet) {
        super(clientLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        this.sprites = spriteSet;
        this.setTextureSprite(spriteSet);
        this.updateUVValues();
    }

    private float r, g, b, a = 1;
    private float alpha;
    private float size = 1;

    private BooleanSupplier shouldPersist = () -> false;

    private boolean fading = false;
    private int fadeTicks = 0;
    private static final int fadeDuration = 5;

    public static final Vec3[] CUBE = {
            // TOP
            new Vec3(1, 1, -1), new Vec3(1, 1, 1), new Vec3(-1, 1, 1), new Vec3(-1, 1, -1),

            // BOTTOM
            new Vec3(-1, -1, -1), new Vec3(-1, -1, 1), new Vec3(1, -1, 1), new Vec3(1, -1, -1),

            // FRONT
            new Vec3(-1, -1, 1), new Vec3(-1, 1, 1), new Vec3(1, 1, 1), new Vec3(1, -1, 1),

            // BACK
            new Vec3(1, -1, -1), new Vec3(1, 1, -1), new Vec3(-1, 1, -1), new Vec3(-1, -1, -1),

            // LEFT
            new Vec3(-1, -1, -1), new Vec3(-1, 1, -1), new Vec3(-1, 1, 1), new Vec3(-1, -1, 1),

            // RIGHT
            new Vec3(1, -1, 1), new Vec3(1, 1, 1), new Vec3(1, 1, -1), new Vec3(1, -1, -1)
    };

    public static BlockParticle create(ClientLevel clientLevel, BlockPos pos, SpriteSet spriteSet) {
        ModWand.setClientMode(ModWand.Mode.ON);
        return new BlockParticle(clientLevel, pos, spriteSet);
    }

    public static BlockParticle create(ClientLevel clientLevel, BlockPos pos) {
        ModWand.setClientMode(ModWand.Mode.ON);
        return new BlockParticle(clientLevel, pos, Sprites.BLOCK_PARTICLE_SPRITES);
    }

    public BlockParticle setRGBATint(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.alpha = a;
        return this;
    }

    public BlockParticle setSize(float size) {
        this.size = size;
        return this;
    }

    public BlockParticle setPersistSupplier(BooleanSupplier supplier) {
        this.shouldPersist = supplier;
        return this;
    }

    public BlockParticle setTextureSprite(SpriteSet spriteSet) {
        this.sprites = spriteSet;
        this.pickSprite(spriteSet);
        this.updateUVValues();
        return this;
    }

    public BlockParticle setTextureIndex(BlockParticleTexture texture) {
        this.setSprite(sprites.get(texture.index(), 2));
        BuildstoneToolkit.LOGGER.info("Sprite indexed: {}", sprites.get(texture.index(), 2));
        this.updateUVValues();
        return this;
    }

    private void updateUVValues() {
        this.minU = sprite.getU0();
        this.maxU = sprite.getU1();
        this.minV = sprite.getV0();
        this.maxV = sprite.getV1();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(VertexConsumer consumer, @NotNull Camera camera, float partialTicks) {
        renderer.render(consumer, camera, partialTicks, this);
    }

    @Override
    public void tick() {
        if (!fading) {
            //Not good practice...
            ModWand.setClientMode(ModWand.Mode.ON);
            if (!shouldPersist.getAsBoolean()) {
                fading = true;
                fadeTicks = 0;
            }
        } else {
            fadeTicks++;
            float fadeProgress = fadeTicks / (float) fadeDuration;
            this.alpha = Mth.clamp(1.0f - fadeProgress, 0.0f, 1.0f) * this.a;

            if (fadeTicks >= fadeDuration) {
                this.remove();
                //Not good practice... sorry.
                ModWand.setClientMode(ModWand.Mode.OFF);
            }
        }
    }

    public static final ParticleRenderType NO_DEPTH = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);

            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);

            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        @Override
        public String toString() {
            return "buildstonetoolkit:no_depth_particle";
        }
    };

    public static final ParticleRenderType RENDER_TYPE;

    static {
        RENDER_TYPE = (Platform.isFabric()) ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT : NO_DEPTH;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static class Provider implements ParticleProvider<BlockParticleOptions> {

        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(BlockParticleOptions particleOptions, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {

            return BlockParticle.create(clientLevel, new BlockPos((int)x, (int)y, (int)z), this.sprite)
                    .setRGBATint(1F, 1F, 1F, 0.8F)
                    .setSize(1F)
                    .setTextureSprite(this.sprite)
                    .setTextureIndex(BlockParticleTexture.SELECTION_BLOCK);
        }
    }
    @Environment(EnvType.CLIENT)
    public interface BlockParticleRenderProvider {
        default void render(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, BlockParticle particle) {
            Vec3 projectedView = camera.getPosition();

            float x = (float) (particle.x - projectedView.x());
            float y = (float) (particle.y - projectedView.y());
            float z = (float) (particle.z - projectedView.z());

            int light = LightTexture.FULL_BRIGHT;

            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 4; j++) {
                    Vec3 vec = CUBE[i * 4 + j].scale(-1).scale((particle.size / 2) + 0.001F).add(x, y, z);

                    float u; float v;
                    switch (j) {
                        case 0 -> { u = particle.maxU; v = particle.maxV; }
                        case 1 -> { u = particle.maxU; v = particle.minV; }
                        case 2 -> { u = particle.minU; v = particle.minV; }
                        default -> { u = particle.minU; v = particle.maxV; } //Case 3
                    }

                    consumer.vertex((float) vec.x, (float) vec.y, (float) vec.z)
                            .uv(u, v)
                            .color(particle.r, particle.g, particle.b, particle.alpha)
                            .uv2(light)
                            .endVertex();
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class NFRenderer implements BlockParticleRenderProvider {

        public void render(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, BlockParticle particle) {
            BlockParticleRenderProvider.super.render(consumer, camera, partialTicks, particle);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class FabricRenderer implements BlockParticleRenderProvider {

        public void render(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, BlockParticle particle) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);

            BlockParticleRenderProvider.super.render(consumer, camera, partialTicks, particle);
        }
    }

}
