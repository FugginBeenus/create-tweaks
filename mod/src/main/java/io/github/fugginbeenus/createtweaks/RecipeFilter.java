package io.github.fugginbeenus.createtweaks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class RecipeFilter {
    private RecipeFilter() {}

    public static boolean shouldRemove(String id, JsonElement element) {
        if (!element.isJsonObject()) return false;
        JsonObject json = element.getAsJsonObject();

        if (Config.get().removeUncraftableRecipes && Versions.UNCRAFTABLE.contains(id)) return true;
        if (!Config.get().removeOreDoubling) return false;
        if (!Versions.CRUSHING_RECIPE_TYPE.equals(string(json, "type"))) return false;

        return isOre(json) && primaryYield(json) > 1;
    }

    private static boolean isOre(JsonObject recipe) {
        JsonArray ingredients = recipe.getAsJsonArray("ingredients");
        if (ingredients == null || ingredients.size() != 1) return false;
        JsonElement first = ingredients.get(0);
        if (!first.isJsonObject()) return false;
        JsonObject ingredient = first.getAsJsonObject();

        String item = string(ingredient, "item");
        if (item != null) {
            return Versions.ORE_ITEMS.contains(item)
                    || Versions.EXTRA_ORES.contains(item)
                    || Config.get().extraOres.contains(item);
        }

        String tag = string(ingredient, "tag");
        if (tag == null) return false;
        String path = tag.substring(tag.indexOf(':') + 1);
        return path.endsWith("_ores") && !path.startsWith("raw_");
    }

    private static double primaryYield(JsonObject recipe) {
        JsonArray results = recipe.getAsJsonArray("results");
        if (results == null || results.isEmpty()) return 0;

        JsonObject primary = results.get(0).getAsJsonObject();
        String item = string(primary, "item");
        if (item == null) return 0;

        double total = count(primary);
        for (int i = 1; i < results.size(); i++) {
            JsonObject extra = results.get(i).getAsJsonObject();
            if (item.equals(string(extra, "item"))) total += count(extra) * chance(extra);
        }
        return total;
    }

    private static double count(JsonObject result) {
        return result.has("count") ? result.get("count").getAsDouble() : 1;
    }

    private static double chance(JsonObject result) {
        return result.has("chance") ? result.get("chance").getAsDouble() : 1;
    }

    private static String string(JsonObject object, String key) {
        JsonElement value = object.get(key);
        return value != null && value.isJsonPrimitive() ? value.getAsString() : null;
    }
}
