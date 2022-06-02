package com.ccgauche.mcmachines.json;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedParser {

    @NotNull
    public static <T, E> Dual<T, E> fromDual(@NotNull JSONContext object, @NotNull FunctionThrow<JSONContext, T> func
            , @NotNull FunctionThrow<JSONContext, E> func1) throws FileException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        if (object.object != null && object.object.isJsonArray()) {
            var j = object.object.getAsJsonArray();
            if (j.size() != 2) {
                throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", expected array of size 2 (" + object + ")");
            }
            var k1 = func.apply(object.swap(j.get(0)));
            var w1 = func1.apply(object.swap(j.get(1)));
            return new Dual<>(k1, w1);
        } else {
            throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", expected array (" + object + ")");
        }
    }

    @NotNull
    public static <T> List<T> fromList(@NotNull JSONContext object, @NotNull FunctionThrow<JSONContext, T> func) throws FileException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        if (object.object != null && object.object.isJsonArray()) {
            List<T> list = new ArrayList<>();
            for (JsonElement e : object.object.getAsJsonArray()) {
                var k = func.apply(object.swap(e));
                list.add(k);
            }
            return list;
        } else {
            throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", expected array (" + object + ")");
        }
    }

    @NotNull
    public static <T> List<T> fromListOrPlain(@NotNull JSONContext object, @NotNull FunctionThrow<JSONContext, T> func) throws FileException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        if (object.object != null && object.object.isJsonArray()) {
            return fromList(object, func);
        } else {
            var k = func.apply(object);
            return List.of(k);
        }
    }

    @NotNull
    public static String parseString(@NotNull JSONContext object) throws FileException {
        if (object.object == null || object.object.getAsString() == null) {
            throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", expected string (" + object + ")");
        } else {
            return object.object.getAsString();
        }
    }

    @NotNull
    public static Integer parseInt(@NotNull JSONContext object) throws FileException {
        if (object.object == null || !object.object.isJsonPrimitive()) {
            throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", expected int (" + object + ")");
        } else {
            return object.object.getAsInt();
        }
    }

    @NotNull
    public static Block parseBlock(@NotNull JSONContext object) throws FileException {
        var k1 = parseString(object);
        if (!k1.contains(":"))
            throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", expected identifier (" + object + ")");
        if (k1.startsWith("minecraft:")) {
            var j = Registry.BLOCK.get(new Identifier(k1));

            if (j == Blocks.AIR && !k1.equals("minecraft:air")) {
                throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", invalid identifier " + k1 + " (" + object + ")");
            }
            return j;
        }
        throw new FileException("Block identifier should start with minecraft:" + object.jsonFile);
    }

    @NotNull
    public static CItem parseItem(@NotNull JSONContext object) throws FileException {
        var k2 = parseString(object);
        if (!k2.contains(":"))
            throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", expected identifier (" + object + ")");
        if (k2.startsWith("minecraft:")) {
            var j = Registry.ITEM.get(new Identifier(k2));
            if (j == Items.AIR && !k2.equals("minecraft:air")) {
                throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", invalid identifier " + k2 + " (" + object + ")");
            }
            return new CItem(j);
        }
        return new CItem(k2);
    }


    @NotNull
    public static DataCompound parseDataCompound(@NotNull JSONContext object) throws FileException {
        if (object.object == null || !object.object.isJsonObject()) {
            throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", invalid DataCompound (" + object + ")");
        }
        var compound = new DataCompound();
        var obj1 = object.object.getAsJsonObject();
        for (Map.Entry<String, JsonElement> el : obj1.entrySet()) {
            try {
                compound.setInt(el.getKey(), Integer.parseInt(el.getValue().getAsString()));
            } catch (NumberFormatException e) {
                compound.setString(el.getKey(), el.getValue().getAsString());
            }

        }
        return compound;
    }

    @NotNull
    public static ItemStack parseItemStack(@NotNull JSONContext object) throws FileException {
        if (object.object == null) {
            throw new FileException("Invalid ItemStack found null " + object.jsonFile());
        }
        if (object.object.isJsonObject()) {
            var data = object.object.getAsJsonObject();

            var k1 = data.get("type");
            if (k1 == null)
                throw new FileException("Invalid JSON file \"" + object.jsonFile + "\", expected type (" + object + ")");
            var k2 = parseItem(object.swap(k1));
            var k3 = data.get("properties");
            DataCompound compound;
            if (k3 == null) {
                compound = new DataCompound();
            } else {
                compound = parseDataCompound(object.swap(k3));

            }
            var k5 = data.get("amount");
            int amount;
            if (k5 == null) {
                amount = 1;
            } else {
                amount = parseInt(object.swap(k5));
            }

            return k2.asStack(amount, compound);
        } else {
            return parseItem(object).asStack(1);
        }
    }

    public record JSONContext(@Nullable JsonElement object, @NotNull String jsonFile) {

        public JSONContext swap(@NotNull JsonElement object) {
            return new JSONContext(object, jsonFile);
        }

        @Override
        public String toString() {
            return "JSONContext{" +
                    "object=" + object +
                    ", jsonFile='" + jsonFile + '\'' +
                    '}';
        }
    }

}
