package dev.fix85.particleculling;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ParticleCullingModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("title.particleculling.config"));

            ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.particleculling.general"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            // Master Switch to block all particles
            general.addEntry(entryBuilder.startBooleanToggle(
                    Component.translatable("option.particleculling.block_all"), 
                    Config.get().blockAll
            )
                    .setDefaultValue(false)
                    .setTooltip(Component.translatable("option.particleculling.block_all.tooltip"))
                    .setSaveConsumer(newValue -> Config.get().blockAll = newValue)
                    .build());

            general.addEntry(entryBuilder.startTextDescription(Component.literal("§7--------------------------------------§r")).build());

            List<Map.Entry<ResourceKey<ParticleType<?>>, ParticleType<?>>> particles = new ArrayList<>(
                    BuiltInRegistries.PARTICLE_TYPE.entrySet()
            );
            particles.sort(Comparator.comparing(entry -> entry.getKey().location().getPath()));

            // Individual toggles (these are visually evaluated at save time and disabled logically if blockAll is active)
            for (Map.Entry<ResourceKey<ParticleType<?>>, ParticleType<?>> entry : particles) {
                String idStr = entry.getKey().location().toString();
                String path = entry.getKey().location().getPath();
                
                String name = path.replace('_', ' ');
                if (!name.isEmpty()) {
                    name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                }

                general.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal(name), 
                        Config.get().particleStates.getOrDefault(idStr, false)
                )
                        .setDefaultValue(false)
                        .setTooltip(Component.literal("Block particle: " + idStr))
                        .setSaveConsumer(newValue -> Config.get().setParticleBlocked(idStr, newValue))
                        .build());
            }

            builder.setSavingRunnable(Config::save);
            return builder.build();
        };
    }
}
