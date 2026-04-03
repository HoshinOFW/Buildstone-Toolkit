package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle;

import com.github.hoshinofw.buildstonetoolkit.content.common.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.Sprites;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.BlockPosSupplier;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.BlockParticleTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class CubeParticle extends TextureSheetParticle {

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

    private SpriteSet sprites;
    private static final CubeParticleRenderProvider renderer = (Platform.isFabric() ? new FabricRenderer() : new NFRenderer());

    public float minU;
    public float maxU;
    public float minV;
    public float maxV;

    protected CubeParticle(ClientLevel clientLevel, BlockPos pos, SpriteSet spriteSet) {
        super(clientLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        this.sprites = spriteSet;
        this.setTextureSprite(spriteSet);
        this.updateUVValues();
    }

    public float r, g, b, a = 1;
    public float alpha;
    public float size = 1;

    private BooleanSupplier shouldPersist = () -> false;
    private BlockPosSupplier posSupplier = null;

    private boolean fading = false;
    private int fadeTicks = 0;
    private static final int fadeDuration = 5;

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public static CubeParticle create(ClientLevel clientLevel, BlockPos pos, SpriteSet spriteSet) {
        ModWand.setClientMode(ModWand.Mode.ON);
        return new CubeParticle(clientLevel, pos, spriteSet);
    }

    public static CubeParticle create(ClientLevel clientLevel, BlockPos pos) {
        ModWand.setClientMode(ModWand.Mode.ON);
        return new CubeParticle(clientLevel, pos, Sprites.BLOCK_PARTICLE_SPRITES);
    }

    public CubeParticle setRGBATint(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.alpha = a;
        return this;
    }

    public CubeParticle setSize(float size) {
        this.size = size;
        return this;
    }

    public CubeParticle setPersistSupplier(BooleanSupplier supplier) {
        this.shouldPersist = supplier;
        return this;
    }

    public CubeParticle setPosSupplier(BlockPosSupplier supplier) {
        this.posSupplier = supplier;
        return this;
    }

    public CubeParticle setTextureSprite(SpriteSet spriteSet) {
        this.sprites = spriteSet;
        this.pickSprite(spriteSet);
        this.updateUVValues();
        return this;
    }

    public CubeParticle setTextureIndex(BlockParticleTexture texture) {
        this.setSprite(sprites.get(texture.index(), 2));
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
        if (posSupplier != null) {
            BlockPos pos = posSupplier.getAsBlockPos();
            if (pos != null) {
                Vec3 vector = pos.getCenter();
                this.x = vector.x();
                this.y = vector.y();
                this.z = vector.z();
            }
        }

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

    public static final ParticleRenderType RENDER_TYPE;

    static {
        RENDER_TYPE = (Platform.isFabric()) ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT : CubeParticleParticleRenderTypeHolder.NO_DEPTH;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    public static class Provider implements ParticleProvider<CubeParticleOptions> {

        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(CubeParticleOptions particleOptions, ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {

            return CubeParticle.create(clientLevel, new BlockPos((int)x, (int)y, (int)z), this.sprite)
                    .setRGBATint(1F, 1F, 1F, 0.8F)
                    .setSize(1F)
                    .setTextureSprite(this.sprite)
                    .setTextureIndex(BlockParticleTexture.SELECTION_BLOCK);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class NFRenderer implements CubeParticleRenderProvider {

        public void render(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, CubeParticle particle) {
            CubeParticleRenderProvider.super.render(consumer, camera, partialTicks, particle);
        }
    }

    @Environment(EnvType.CLIENT)
    public static class FabricRenderer implements CubeParticleRenderProvider {

        public void render(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, CubeParticle particle) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);

            CubeParticleRenderProvider.super.render(consumer, camera, partialTicks, particle);
        }
    }

}
