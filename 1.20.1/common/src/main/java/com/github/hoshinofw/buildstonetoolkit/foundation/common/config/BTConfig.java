package com.github.hoshinofw.buildstonetoolkit.foundation.common.config;

import com.github.hoshinofw.buildstonetoolkit.foundation.common.core.BuildstoneToolkit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BTConfig {
    //TODO Switch to a cleaner TOML
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String CONFIG_FILE_NAME = BuildstoneToolkit.MOD_ID + "-config.json";

    private static final int DEFAULT_INTERACTION_PROXY_RAY_REACH = 64;
    private static final int DEFAULT_PROXY_TUNER_RAY_REACH = 64;
    private static final List<TagKey<Block>> DEFAULT_INTERACTION_PROXY_RAY_SKIP = List.of();

    private static int INTERACTION_PROXY_RAY_REACH = DEFAULT_INTERACTION_PROXY_RAY_REACH;
    private static int PROXY_TUNER_RAY_REACH = DEFAULT_PROXY_TUNER_RAY_REACH;
    private static List<TagKey<Block>> INTERACTION_PROXY_RAY_SKIP = DEFAULT_INTERACTION_PROXY_RAY_SKIP;
    private static boolean INTERACTION_PROXY_RAY_SKIP_IS_EMPTY = false;
    
    private static Set<Block> INTERACTION_PROXY_RAY_SKIP_BLOCKS = Set.of();

    public static int getInteractionProxyRayReach() {
        return INTERACTION_PROXY_RAY_REACH;
    }

    public static void setInteractionProxyRayReach(int reach) {
        BTConfig.INTERACTION_PROXY_RAY_REACH = reach;
    }

    public static int getProxyTunerRayReach() {
        return PROXY_TUNER_RAY_REACH;
    }

    public static void setProxyTunerRayReach(int reach) {
        BTConfig.PROXY_TUNER_RAY_REACH = reach;
    }

    public static List<TagKey<Block>> getInteractionProxyRaySkip() {
        return INTERACTION_PROXY_RAY_SKIP;

    }

    public static boolean getInteractionProxyRaySkipIsEmpty() {
        return INTERACTION_PROXY_RAY_SKIP_IS_EMPTY;
    }

    public static Set<Block> getInteractionProxyRaySkipBlocks() {
        return INTERACTION_PROXY_RAY_SKIP_BLOCKS;
    }

    public static void setInteractionProxyRaySkip(List<TagKey<Block>> skipList) {
        BTConfig.INTERACTION_PROXY_RAY_SKIP = skipList;
        BTConfig.INTERACTION_PROXY_RAY_SKIP_IS_EMPTY = skipList.isEmpty();
        BTConfig.INTERACTION_PROXY_RAY_SKIP_BLOCKS = resolveSkipBlocks(skipList);
    }

   
    private static Set<Block> resolveSkipBlocks(List<TagKey<Block>> tags) {
        if (tags.isEmpty()) {
            return Set.of();
        }
        Set<Block> blocks = new ObjectOpenHashSet<>();
        for (TagKey<Block> tag : tags) {
            BuiltInRegistries.BLOCK.getTag(tag).ifPresent(holders -> {
                for (Holder<Block> holder : holders) {
                    blocks.add(holder.value());
                }
            });
        }
        return blocks;
    }

    /**
     * Server-side config init.
     */
    public static void init() {
        Path configPath = Platform.getConfigFolder().resolve(CONFIG_FILE_NAME);
        ConfigData data = new ConfigData();
        boolean needsWrite = false;

        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                ConfigData parsed = GSON.fromJson(reader, ConfigData.class);
                if (parsed != null) {
                    data = parsed;
                } else {
                    needsWrite = true;
                }
            } catch (IOException | JsonSyntaxException e) {
                BuildstoneToolkit.LOGGER.warn("Failed to parse config at {}, regenerating with defaults: {}",
                        configPath, e.getMessage());
                data = new ConfigData();
                needsWrite = true;
            }
        } else {
            BuildstoneToolkit.LOGGER.info("Config file not found, writing default at {}", configPath);
            needsWrite = true;
        }

        if (data.interaction_proxy_ray_reach <= 0) {
            data.interaction_proxy_ray_reach = DEFAULT_INTERACTION_PROXY_RAY_REACH;
            needsWrite = true;
        }
        if (data.proxy_tuner_ray_reach <= 0) {
            data.proxy_tuner_ray_reach = DEFAULT_PROXY_TUNER_RAY_REACH;
            needsWrite = true;
        }

        List<TagKey<Block>> skip;
        if (data.interaction_proxy_ray_skip == null) {
            skip = DEFAULT_INTERACTION_PROXY_RAY_SKIP;
            data.interaction_proxy_ray_skip = tagsToStrings(DEFAULT_INTERACTION_PROXY_RAY_SKIP);
            needsWrite = true;
        } else {
            skip = new ArrayList<>(data.interaction_proxy_ray_skip.size());
            boolean dropped = false;
            for (String raw : data.interaction_proxy_ray_skip) {
                TagKey<Block> tag = parseBlockTag(raw);
                if (tag != null) {
                    skip.add(tag);
                } else {
                    BuildstoneToolkit.LOGGER.warn("Invalid block tag '{}' in config, dropping it", raw);
                    dropped = true;
                }
            }
            if (dropped) {
                data.interaction_proxy_ray_skip = tagsToStrings(skip);
                needsWrite = true;
            }
        }

        setInteractionProxyRayReach(data.interaction_proxy_ray_reach);
        setProxyTunerRayReach(data.proxy_tuner_ray_reach);
        setInteractionProxyRaySkip(skip);

        if (needsWrite) {
            write(configPath, data);
        }
    }

    /**
     * Parses a block-tag id (with or without a leading {@code #}). Returns {@code null} if the string is not a valid {@link ResourceLocation}.
     */
    private static TagKey<Block> parseBlockTag(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.startsWith("#") ? raw.substring(1) : raw;
        ResourceLocation id = ResourceLocation.tryParse(s);
        if (id == null) {
            return null;
        }
        return TagKey.create(Registries.BLOCK, id);
    }

    private static List<String> tagsToStrings(List<TagKey<Block>> tags) {
        List<String> out = new ArrayList<>(tags.size());
        for (TagKey<Block> tag : tags) {
            out.add(tag.location().toString());
        }
        return out;
    }

    private static void write(Path path, ConfigData data) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            BuildstoneToolkit.LOGGER.error("Failed to write config at {}: {}", path, e.getMessage());
        }
    }

    private static class ConfigData {
        int interaction_proxy_ray_reach = DEFAULT_INTERACTION_PROXY_RAY_REACH;
        int proxy_tuner_ray_reach = DEFAULT_PROXY_TUNER_RAY_REACH;
        List<String> interaction_proxy_ray_skip = new ArrayList<>(tagsToStrings(DEFAULT_INTERACTION_PROXY_RAY_SKIP));
    }
}
