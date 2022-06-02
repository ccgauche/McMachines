package com.ccgauche.mcmachines.json;

import com.ccgauche.mcmachines.data.CItem;
import com.ccgauche.mcmachines.data.DataCompound;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ItemFile {

    @NotNull
    public String name;
    @NotNull
    public CItem base;

    @NotNull
    public Optional<List<String>> handler;

    @NotNull
    public Optional<DataCompound> properties;

    @Override
    public String toString() {
        return "ItemFile{" +
                "name='" + name + '\'' +
                ", base=" + base +
                '}';
    }
}
