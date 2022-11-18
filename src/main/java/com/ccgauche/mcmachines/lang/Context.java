package com.ccgauche.mcmachines.lang;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The context of a call. It contains the current scope data as well as global
 * data.
 */
public class Context {

	@NotNull
	private final HashMap<@NotNull String, @Nullable Object> global;
	@NotNull
	private final HashMap<@NotNull String, @Nullable Object> variables = new HashMap<>();

	public Context(@NotNull HashMap<@NotNull String, @Nullable Object> global) {
		this.global = global;
	}

	public HashMap<String, Object> getGlobal() {
		return global;
	}

	interface NativeClosure extends CodeParser.ClosureFunction {

	}

	public void execute(File file) {
		try {
			execute("{" + Files.readString(file.toPath()) + "}");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void execute(String file) {
		CodeParser.Expression expr = CodeParser.parseExpression(new Cursor(file));
		expr.execute(this.copy());
	}

	public void register(@NotNull String name, @Nullable NativeClosure value) {
		global.put(name, value);
	}

	public void addGlobal(@NotNull String name, @Nullable Object value) {
		global.put(name, value);
	}

	/**
	 * Registers a class as a native class. It will be accessible from the script.
	 * Warning it uses reflection, so it is not recommended to use it with classes
	 * that have a lot of methods.
	 *
	 * @param clazz
	 */
	public void registerBinding(Class<?> clazz) {
		TypeAlias alias = clazz.getAnnotation(TypeAlias.class);
		if (alias == null) {
			throw new RuntimeException("Class " + clazz.getName() + " is not annotated with @TypeAlias");
		}
		for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
			// Don't touch to java autogenerated methods
			if (method.isSynthetic()) {
				continue;
			}
			// Only apply on static methods
			if ((method.getModifiers() & java.lang.reflect.Modifier.STATIC) != 0) {
				Parameter[] params = method.getParameters();
				if (params.length > 0 && params[0].getType() == Context.class) {
					if (method.isAnnotationPresent(Method.class)) {
						register(params[params.length - 1].getType().getSimpleName() + "::"
								+ method.getName().replace("$", ""), (context, arguments) -> {
									try {
										Object[] args = new Object[arguments.length + 1];
										args[0] = context;
										System.arraycopy(arguments, 0, args, 1, arguments.length);
										return method.invoke(null, args);
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								});
					}
					register(alias.name() + "::" + method.getName().replace("$", ""), (context, arguments) -> {
						try {
							Object[] args = new Object[arguments.length + 1];
							args[0] = context;
							System.arraycopy(arguments, 0, args, 1, arguments.length);
							return method.invoke(null, args);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					});
				} else {
					if (method.isAnnotationPresent(Method.class) && params.length > 0) {
						register(params[params.length - 1].getType().getSimpleName() + "::"
								+ method.getName().replace("$", ""), (context, arguments) -> {
									try {
										return method.invoke(null, arguments);
									} catch (Exception e) {
										throw new RuntimeException(e);
									}
								});
					}
					register(alias.name() + "::" + method.getName().replace("$", ""), (context, arguments) -> {
						try {
							return method.invoke(null, arguments);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					});
				}

			}
		}
	}

	public Context copy() {
		Context context = new Context(global);
		context.variables.putAll(variables);
		return context;
	}

	public void setVariable(@NotNull String name, @Nullable Object value) {
		variables.put(name, value);
	}

	public @Nullable Object getVariable(@NotNull String name) {
		Object value = variables.get(name);
		if (value == null)
			return global.get(name);
		return value;
	}

}
