package io.github.fugginbeenus.createtweaks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class CreateTweaks implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("createtweaks");

    @Override
    public void onInitialize() {
        if (Config.get().deployerCannotAttack) blockDeployerAttacks();
        if (Config.get().hideUncraftableItems) hideItems();
    }

    private void blockDeployerAttacks() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
                !isDeployer(source.getAttacker()));
    }

    private static boolean isDeployer(Entity entity) {
        return entity != null && entity.getClass().getName().equals(Versions.DEPLOYER_FAKE_PLAYER);
    }

    private void hideItems() {
        Set<Item> hidden = Versions.HIDDEN_ITEMS.stream()
                .map(Identifier::new)
                .filter(Registries.ITEM::containsId)
                .map(Registries.ITEM::get)
                .collect(Collectors.toSet());

        if (hidden.isEmpty()) return;

        ItemGroupEvents.MODIFY_ENTRIES_ALL.register((group, entries) -> {
            entries.getDisplayStacks().removeIf(stack -> hidden.contains(stack.getItem()));
            entries.getSearchTabStacks().removeIf(stack -> hidden.contains(stack.getItem()));
        });
    }
}
