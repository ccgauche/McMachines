package com.ccgauche.mcmachines.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.json.conditions.ICondition;
import com.ccgauche.mcmachines.json.recipe.IRecipe;
import com.ccgauche.mcmachines.json.recipe.MCCraft;
import com.ccgauche.mcmachines.json.recipe.TransformerCraft;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.*;

public class DParser {

    private static final HashMap<String, FunctionThrow<AdvancedParser.JSONContext, @NotNull Object>> funcMap = new HashMap<>();

    static {
        funcMap.put(Integer.class.getTypeName(), AdvancedParser::parseInt);
        funcMap.put(String.class.getTypeName(), AdvancedParser::parseString);
        funcMap.put(DataCompound.class.getTypeName(), AdvancedParser::parseDataCompound);
        funcMap.put(CItem.class.getTypeName(), AdvancedParser::parseItem);
        funcMap.put(ItemStack.class.getTypeName(), AdvancedParser::parseItemStack);
        funcMap.put(ItemFile.class.getTypeName(), createBuilder(ItemFile.class));
        funcMap.put(Machine.class.getTypeName(), createBuilder(Machine.class));
        funcMap.put(MachineMode.class.getTypeName(), createEnum(MachineMode.class));
        funcMap.put(Block.class.getTypeName(), AdvancedParser::parseBlock);
        funcMap.put(ICondition.class.getTypeName(), DParser::parseICondition);
        funcMap.put(IRecipe.IDProvider.class.getTypeName(), createBuilder(IRecipe.IDProvider.class));
        funcMap.put(MCCraft.class.getTypeName(), createBuilder(MCCraft.class));
        funcMap.put(TransformerCraft.class.getTypeName(), createBuilder(TransformerCraft.class));
        funcMap.put(IRecipe.class.getTypeName(), IRecipe::parse);
    }

    public static AdvancedParser.JSONContext readFile(File file) throws FileNotFoundException {
        return new AdvancedParser.JSONContext(new Gson().fromJson(new FileReader(file), JsonElement.class), file.toString());
    }

    public static <T> FunctionThrow<AdvancedParser.JSONContext, @NotNull Object> createBuilder(Class<T> clas) {

        return e -> {
            if (e.object() == null || !e.object().isJsonObject()) {
                throw new FileException("Unexpected in while parsing " + clas.getSimpleName() + " in " + e.jsonFile());
            }
            JsonObject object = e.object().getAsJsonObject();

            T t = clas.newInstance();
            for (Field field : clas.getDeclaredFields()) {
                var l = parse(field.getGenericType().getTypeName(), e.swap(object.get(field.getName())));
                field.set(t, l);
            }
            return t;
        };
    }

    public static ICondition parseICondition(AdvancedParser.JSONContext context) throws FileException {
        var k2 = AdvancedParser.parseString(context);
        return ICondition.parse(k2);
    }

    public static List<String> create(String k) {
        List<String> str = new ArrayList<>();
        str.add(k);
        str.add(k.replace("_", ""));
        return str;
    }

    public static <T extends Enum<T>> FunctionThrow<AdvancedParser.JSONContext, @NotNull Object> createEnum(Class<T> clas) {

        return e -> {
            if (e.object() == null || e.object().getAsString() == null) {
                throw new FileException("Unexpected in while parsing " + clas.getDeclaringClass().getSimpleName() + " in " + e.jsonFile());
            }
            String name = e.object().getAsString();
            for (T field : getEnumValues(clas)) {
                for (String k : create(field.name())) {
                    if (k.equalsIgnoreCase(name)) {
                        return field;
                    }
                }
            }
            throw new FileException(name + " not found in " + e.jsonFile());
        };
    }

    private static <E extends Enum<E>> E[] getEnumValues(Class<E> enumClass)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = enumClass.getDeclaredField("$VALUES");
        f.setAccessible(true);
        return (E[]) f.get(null);
    }

    public static int posOfComma(String s) {
        int angle = 0;
        int w = 0;
        for (char c : s.toCharArray()) {
            if (c == '<') {
                angle += 1;
            }
            if (c == '>') {
                angle -= 1;
            }
            if (angle == 0 && c == ',') {
                return w;
            }
            w++;
        }
        return -1;
    }

    @NotNull
    public static Object parse(String clas, AdvancedParser.JSONContext object) throws FileException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        var t = funcMap.get(clas);
        if (t != null) {
            return t.apply(object);
        }
        if (clas.startsWith(Dual.class.getTypeName()+"<")) {
            String type = clas.substring(Dual.class.getTypeName().length()+1, clas.length() - 1);
            int vpos = posOfComma(type);
            String ftype = type.substring(0, vpos).trim();
            String stype = type.substring(vpos + 1).trim();
            return AdvancedParser.fromDual(object, e -> parse(ftype, e), e -> parse(stype, e));
        }
        if (clas.startsWith(List.class.getTypeName()+"<")) {
            String type = clas.substring((List.class.getTypeName()+"<").length(), clas.length() - 1);
            return AdvancedParser.fromListOrPlain(object, e -> parse(type, e));
        }
        if (clas.startsWith(Optional.class.getTypeName()+"<")) {
            String type = clas.substring((Optional.class.getTypeName()+"<").length(), clas.length() - 1);
            if (object.object() == null || object.object().isJsonNull()) {
                return Optional.empty();
            } else {
                System.out.println(clas + " -> " + object);
                return Optional.of(parse(type, object));
            }
        }
        throw new FileException("No type support for " + clas + " in " + object.jsonFile());
    }


}
