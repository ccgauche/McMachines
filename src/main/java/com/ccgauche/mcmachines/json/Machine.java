package com.ccgauche.mcmachines.json;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.json.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Machine {

    @NotNull
    public String name;

    @NotNull
    public MachineMode mode;
    public Optional<DataCompound> properties;
    public Optional<ICondition> conditions;

    @Override
    public String toString() {
        return "Machine{" +
                "name='" + name + '\'' +
                ", mode='" + mode + '\'' +
                ", properties=" + properties +
                ", conditions=" + conditions +
                '}';
    }
}
