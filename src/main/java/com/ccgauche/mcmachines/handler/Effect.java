package com.ccgauche.mcmachines.handler;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.handler.events.Listener;
import com.ccgauche.mcmachines.handler.events.PlayerTickListener;
import com.ccgauche.mcmachines.registry.DataRegistry;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * The effect handler enables plugins to add effects to items, players, etc.
 */
public class Effect implements PlayerTickListener {

	public int duration = 0;
	public int power = 0;
	public int energy_use = 0;

	public StatusEffect effect = null;

	@Override
	public String handlerId() {
		return "effect";
	}

	@Override
	public Listener derive(String argument) {
		Effect effect = new Effect();
		String[] arguments = argument.split(",");
		effect.duration = Integer.parseInt(arguments[0].trim());
		effect.power = Integer.parseInt(arguments[1].trim());
		effect.effect = Objects.requireNonNull(Registry.STATUS_EFFECT.get(new Identifier(arguments[2].trim())));
		effect.energy_use = Integer.parseInt(arguments[3].trim());
		return effect;
	}

	@Override
	public void onTick(@NotNull ServerPlayerEntity player, @NotNull ServerWorld world, @NotNull ItemStack stack) {
		if (effect == null)
			return;
		DataCompound compound = new DataCompound(stack);
		int energy = DataRegistry.ENERGY_CONTENT.getOrDefault(compound, 0);
		if (energy < energy_use)
			return;
		player.addStatusEffect(new StatusEffectInstance(effect, duration, power, false, false, true));
		DataRegistry.ENERGY_CONTENT.set(compound, energy - energy_use);
		compound.updateStack(stack);
	}
}
