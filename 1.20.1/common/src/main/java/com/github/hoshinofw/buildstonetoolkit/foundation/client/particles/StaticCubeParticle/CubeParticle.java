package com.github.hoshinofw.buildstonetoolkit.foundation.client.particles.StaticCubeParticle;

import com.github.hoshinofw.buildstonetoolkit.content.common.items.ModWand;
import com.github.hoshinofw.buildstonetoolkit.foundation.client.Sprites;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.Vec3Supplier;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.BlockParticleTexture;
import com.github.hoshinofw.buildstonetoolkit.foundation.common.util.render.RenderTransformSupplier;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.platform.Platform;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

//TODO Add face filtering option
//TODO Add different textures for different faces option.
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
    private boolean doCull = false;
    public ParticleRenderType render_type = (Platform.isFabric()) ? ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT : CubeParticleParticleRenderTypeHolder.NO_DEPTH;

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

    protected CubeParticle(ClientLevel clientLevel, Vec3 pos, SpriteSet spriteSet) {
        super(clientLevel, pos.x(), pos.y(), pos.y());
        this.sprites = spriteSet;
        this.setTextureSprite(spriteSet);
        this.updateUVValues();
    }

    public float r, g, b, a = 1;
    public float alpha;
    public Vec3 size = new Vec3(1, 1, 1);
    public float scale = 1;
    public Vec3 renderOffset = new Vec3(0, 0, 0);

    private BooleanSupplier shouldPersist = () -> false;
    private Vec3Supplier posSupplier = null;
    private RenderTransformSupplier renderTransformContextSupplier = null;
    private Vec3Supplier sizeSupplier = null;
    private DoubleSupplier scaleSupplier = null;
    private Vec3Supplier renderOffsetSupplier = null;

    final double[] cornerOffsets = new double[72];
    final Vector3d rotScratch = new Vector3d();
    boolean cornersDirty = true;
    private CornerSource cornerSource;
    private boolean built = false;

    private boolean fading = false;
    private int fadeTicks = 0;
    private int fadeDuration = 5;

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

    public static CubeParticle create(ClientLevel clientLevel, Vec3 pos) {
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

    public CubeParticle setScale(Vec3 size) {
        this.size = size;
        this.cornersDirty = true;
        return this;
    }

    public CubeParticle setSize(float scale) {
        this.scale = scale;
        this.cornersDirty = true;
        return this;
    }

    public CubeParticle setRenderOffset(Vec3 offset) {
        this.renderOffset = offset;
        this.cornersDirty = true;
        return this;
    }

    public CubeParticle setPersistSupplier(BooleanSupplier supplier) {
        this.shouldPersist = supplier;
        return this;
    }

    public CubeParticle setPosSupplier(Vec3Supplier supplier) {
        this.posSupplier = supplier;
        return this;
    }

    public CubeParticle setCull(boolean cull) {
        this.doCull = cull;

        if (doCull) {
            this.render_type = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
        }

        return this;
    }

    public CubeParticle setRenderTransformContextSupplier(RenderTransformSupplier supplier) {
        this.renderTransformContextSupplier = supplier;
        return this;
    }

    public CubeParticle setTextureSprite(SpriteSet spriteSet) {
        this.sprites = spriteSet;
        this.pickSprite(spriteSet);
        this.updateUVValues();
        return this;
    }

    public CubeParticle setTextureIndex(BlockParticleTexture texture) {
        this.setSprite(sprites.get(texture.index(), BlockParticleTexture.values().length - 1));
        this.updateUVValues();
        return this;
    }

    public CubeParticle setFadeDuration(int fadeDuration) {
        this.fadeDuration = fadeDuration;
        return this;
    }

    public CubeParticle build() {
        cornerSource = (sizeSupplier != null || scaleSupplier != null || renderOffsetSupplier != null)
                ? CornerSource.Dynamic.INSTANCE
                : CornerSource.Static.INSTANCE;
        recomputeCorners();
        cornersDirty = false;
        built = true;
        return this;
    }

    private void updateUVValues() {
        this.minU = sprite.getU0();
        this.maxU = sprite.getU1();
        this.minV = sprite.getV0();
        this.maxV = sprite.getV1();
    }

    void recomputeCorners() {
        Vec3 effSize = size;
        if (sizeSupplier != null) {
            Vec3 v = sizeSupplier.asVec3();
            if (v != null) effSize = v;
        }
        double effScale = scaleSupplier != null ? scaleSupplier.getAsDouble() : scale;
        Vec3 effOffset = renderOffset;
        if (renderOffsetSupplier != null) {
            Vec3 v = renderOffsetSupplier.asVec3();
            if (v != null) effOffset = v;
        }

        double hx = effSize.x * effScale * 0.5 + 0.001;
        double hy = effSize.y * effScale * 0.5 + 0.001;
        double hz = effSize.z * effScale * 0.5 + 0.001;

        for (int idx = 0; idx < 24; idx++) {
            Vec3 corner = CUBE[idx];
            int base = idx * 3;
            cornerOffsets[base]     = -corner.x * hx + effOffset.x;
            cornerOffsets[base + 1] = -corner.y * hy + effOffset.y;
            cornerOffsets[base + 2] = -corner.z * hz + effOffset.z;
        }
    }

    @Override
    public void render(@NotNull VertexConsumer consumer, @NotNull Camera camera, float partialTicks) {
        assert built;
        renderer.render(consumer, camera, partialTicks, this, renderTransformContextSupplier);
    }

    @Override
    public void tick() {
        assert built;

        if (posSupplier != null) {
            Vec3 vector = posSupplier.asVec3();
            if (vector != null) {
                this.x = vector.x();
                this.y = vector.y();
                this.z = vector.z();
            }
        }

        if (renderTransformContextSupplier != null) {
            renderTransformContextSupplier.tick();
        }

        cornerSource.prepareCorners(this);

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
                //TODO replace with an on-death lambda
                ModWand.setClientMode(ModWand.Mode.OFF);
            }
        }
    }


    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return render_type;
    }

    public static class Provider implements ParticleProvider<CubeParticleOptions> {

        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull CubeParticleOptions particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {

            return CubeParticle.create(clientLevel, new BlockPos((int)x, (int)y, (int)z), this.sprite)
                    .setRGBATint(1F, 1F, 1F, 0.8F)
                    .setSize(1F)
                    .setTextureSprite(this.sprite)
                    .setTextureIndex(BlockParticleTexture.SELECTION_BLOCK)
                    .build();
        }
    }

    public static class NFRenderer implements CubeParticleRenderProvider {

        @Override
        public void preRender(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, CubeParticle particle) {
        }
    }

    public static class FabricRenderer implements CubeParticleRenderProvider {

        @Override
        public void preRender(VertexConsumer consumer, @NotNull Camera camera, float partialTicks, CubeParticle particle) {
            if (!particle.doCull) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableCull();
                RenderSystem.depthMask(false);
            }
        }
    }

}
