package io.github.fugginbeenus.createtweaks.mixin;

import com.google.gson.JsonElement;
import io.github.fugginbeenus.createtweaks.CreateTweaks;
import io.github.fugginbeenus.createtweaks.RecipeFilter;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @ModifyVariable(method = "apply", at = @At("HEAD"), argsOnly = true, index = 1)
    private Map<Identifier, JsonElement> createtweaks$filter(Map<Identifier, JsonElement> recipes) {
        Map<Identifier, JsonElement> kept = new LinkedHashMap<>(recipes.size());
        int removed = 0;

        for (Map.Entry<Identifier, JsonElement> entry : recipes.entrySet()) {
            if (RecipeFilter.shouldRemove(entry.getKey().toString(), entry.getValue())) {
                removed++;
            } else {
                kept.put(entry.getKey(), entry.getValue());
            }
        }

        if (removed > 0) CreateTweaks.LOGGER.info("Removed {} recipes", removed);
        return kept;
    }
}
