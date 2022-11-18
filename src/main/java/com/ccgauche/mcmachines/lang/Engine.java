package com.ccgauche.mcmachines.lang;

import java.util.Arrays;
import java.util.Objects;

import com.ccgauche.mcmachines.lang.bindings.*;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Engine {

	/**
	 * Add global bindings to the engine.
	 *
	 * @param context The context to add the bindings to.
	 */
	public static void loadDefaults(Context context) {

		context.registerBinding(PropertyBinding.class);
		context.registerBinding(TransformerBinding.class);
		context.registerBinding(TransformerCraftBinding.class);
		context.registerBinding(DualBinding.class);
		context.registerBinding(BlockBinding.class);
		context.registerBinding(BlockPosBinding.class);
		context.registerBinding(DataCompoundBinding.class);
		context.registerBinding(ShapedRecipeBinding.class);
		context.registerBinding(WorldBinding.class);
		context.registerBinding(ConstantGeneratorBinding.class);
		context.registerBinding(ItemStackBinding.class);
		context.registerBinding(GeneratorCraftBinding.class);
		context.registerBinding(BurningGeneratorBinding.class);
		context.registerBinding(ChargerBinding.class);

		context.register("concat", (ctx, args) -> args[0] + ((String) args[1]));
		context.addGlobal("true", true);
		context.addGlobal("false", false);
		context.addGlobal("null", null);
		context.register("MachineExecutionEvent", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return null;
		});
		context.register("not", (ctx, args) -> {
			if (args == null || args.length != 1)
				return null;
			return !(boolean) args[0];
		});
		context.register("e", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return Objects.equals(args[0], args[1]) || Objects.equals(args[1], args[0]);
		});
		context.register("ne", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return !Objects.equals(args[0], args[1]) && !Objects.equals(args[1], args[0]);
		});
		context.register("l", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] < (int) args[1];
		});
		context.register("le", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] <= (int) args[1];
		});
		context.register("g", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] > (int) args[1];
		});
		context.register("ge", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] >= (int) args[1];
		});
		context.register("add", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] + (int) args[1];
		});
		context.register("sub", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] - (int) args[1];
		});
		context.register("mul", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] * (int) args[1];
		});
		context.register("div", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] / (int) args[1];
		});
		context.register("mod", (ctx, args) -> {
			if (args == null || args.length != 2)
				return null;
			return (int) args[0] % (int) args[1];
		});
		context.register("BlockPos::rel", (ctx, args) -> {
			if (args != null && args.length == 4)
				return ((BlockPos) args[0]).add((int) args[1], (int) args[2], (int) args[3]);
			if (args != null && args.length == 2)
				return ((BlockPos) args[0]).offset((Direction) args[1]);
			return null;
		});
		context.register("List::of", (ctx, args) -> Arrays.asList(args));
		context.register("and", (ctx, args) -> {
			if (args == null || args.length < 2)
				return null;
			for (Object arg : args)
				if (!(boolean) arg)
					return false;
			return true;
		});
		context.register("or", (ctx, args) -> {
			if (args == null || args.length < 2)
				return null;
			for (Object arg : args)
				if ((boolean) arg)
					return true;
			return false;
		});
		context.register("inc", (ctx, args) -> {
			if (args == null || args.length != 1)
				return null;
			return (int) args[0] + 1;
		});
		context.register("dec", (ctx, args) -> {
			if (args == null || args.length != 1)
				return null;
			return (int) args[0] - 1;
		});
		context.register("if", (ctx, args) -> {
			if (args == null || args.length == 0)
				return null;
			if ((boolean) args[0]) {
				if (args.length <= 2)
					return null;
				if (args[1]instanceof CodeParser.ClosureFunction closure) {
					return closure.run(context, new Object[0]);
				} else {
					throw new RuntimeException("Not a function " + args[1]);
				}
			} else {
				if (args.length <= 3)
					return null;
				if (args[2]instanceof CodeParser.ClosureFunction closure) {
					return closure.run(context, new Object[0]);
				} else {
					throw new RuntimeException("Not a function " + args[2]);
				}
			}
		});

		context.register("set", (ctx, args) -> {
			ctx.setVariable((String) args[0], args[1]);
			return null;
		});
		context.register("get", (ctx, args) -> ctx.getVariable((String) args[0]));
		context.register("print", (ctx, args) -> {
			StringBuilder b = new StringBuilder();
			for (Object arg : args) {
				b.append(arg);
			}
			System.out.println(b);
			return null;
		});

		System.out.println(context.getGlobal().keySet());

	}
}
