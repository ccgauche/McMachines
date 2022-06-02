package com.ccgauche.mcmachines.json.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.ccgauche.mcmachines.data.DataCompound;
import com.ccgauche.mcmachines.machine.IMachine;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public interface ICondition {

	boolean isTrue(@NotNull IMachine machine, @NotNull ServerWorld world, @NotNull BlockPos pos,
			@NotNull DataCompound data);

	//
	@NotNull
	static ICondition parseFunc(String func) {
		String funcname = null;
		List<String> arguments = new ArrayList<>();
		StringBuilder cuarg = new StringBuilder();
		int par = 0;
		for (char g : func.toCharArray()) {
			if (g == '(') {
				par++;
				if (par == 1) {
					funcname = cuarg.toString();
					cuarg = new StringBuilder();
					continue;
				}
			} else if (g == ')') {
				par--;
				if (par == 0) {
					arguments.add(cuarg.toString().trim());
					cuarg = new StringBuilder();
					if (funcname == null) {
						System.out.println("Empty func name");
						System.exit(0);
					}
					if (funcname.equalsIgnoreCase("and") || funcname.equalsIgnoreCase("&")) {
						return new And(arguments.stream().map(ICondition::parseFunc).collect(Collectors.toList()));
					} else if (funcname.equalsIgnoreCase("isDay")) {
						return new IsDayOrNight(true);
					} else if (funcname.equalsIgnoreCase("isNight")) {
						return new IsDayOrNight(false);
					} else if (funcname.equalsIgnoreCase("type")) {
						return new Type(Registry.BLOCK.get(new Identifier(arguments.get(0))));
					} else if (funcname.equalsIgnoreCase("relative") || funcname.equalsIgnoreCase("rel")
							|| funcname.equalsIgnoreCase("~")) {
						System.out.println(arguments);
						return new Relative(Integer.parseInt(arguments.get(0)), Integer.parseInt(arguments.get(1)),
								Integer.parseInt(arguments.get(2)),
								Objects.requireNonNull(ICondition.parse(arguments.get(3))));
					} else {
						System.out.println("Invalid FuncName " + funcname);
						System.exit(0);
					}
				}
			}
			if (par == 1) {
				if (g == ',') {
					arguments.add(cuarg.toString().trim());
					cuarg = new StringBuilder();
				} else {
					cuarg.append(g);
				}
			} else {
				cuarg.append(g);
			}
		}
		System.out.println("Function not terminated " + func);
		System.exit(0);
		return null;
	}

	@NotNull
	static ICondition parse(String string) {
		return parseFunc(string);
	}
}
