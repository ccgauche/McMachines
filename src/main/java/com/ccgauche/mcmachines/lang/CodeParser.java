package com.ccgauche.mcmachines.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.ccgauche.mcmachines.data.CItem;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

// TODO: Comment code
public class CodeParser {

	public interface ClosureFunction {
		Object run(@NotNull Context context, @NotNull Object[] expression);
	}

	record Closure(String[] arguments, Expression expression) implements Expression, ClosureFunction {
		@Override
		@Nullable
		public Object execute(@NotNull Context context) {
			return this;
		}

		@Override
		public String toString() {
			return "|" + Arrays.stream(arguments).map(String::toString).collect(Collectors.joining(", ")) + "| "
					+ expression;
		}

		@Override
		public Object run(@NotNull Context context, @NotNull Object[] expressions) {

			Context newContext = context.copy();
			for (int i = 0; i < this.arguments.length; i++) {
				newContext.setVariable(this.arguments[i], expressions[i]);
			}
			return expression.execute(newContext);
		}
	}

	record Assignment(String name, Expression expression) implements Expression {
		@Override
		@Nullable
		public Object execute(@NotNull Context context) {
			context.setVariable(name, expression.execute(context));
			return this;
		}

		@Override
		public String toString() {
			return name + " = " + expression;
		}
	}

	interface Expression {

		@Nullable
		Object execute(@NotNull Context context);
	}

	// Expressions separated by a semi-colon
	record CodeBlock(List<Expression> sections) implements Expression {
		@Override
		public @Nullable Object execute(@NotNull Context context) {
			Object last = null;
			for (Expression expression : sections) {
				last = expression.execute(context);
			}
			return last;
		}

		@Override
		public String toString() {
			return "{" + sections.stream().map(Expression::toString).collect(Collectors.joining("; ")) + "}";
		}
	}

	// Any piece of text
	record Item(CItem value) implements Expression {
		@Override
		public @NotNull Object execute(@NotNull Context context) {
			return value;
		}

		@Override
		public String toString() {
			return value + "";
		}

	}

	// Any piece of text
	record Number(int value) implements Expression {
		@Override
		public @NotNull Object execute(@NotNull Context context) {
			return value;
		}

		@Override
		public String toString() {
			return value + "";
		}

	}

	// Any piece of text
	record Literal(String value) implements Expression {
		@Override
		public @Nullable Object execute(@NotNull Context context) {
			return context.getVariable(value);
		}

		@Override
		public String toString() {
			return value;
		}

	}

	record StringLiteral(String value) implements Expression {
		@Override
		public @Nullable Object execute(@NotNull Context context) {
			return value;
		}

		@Override
		public String toString() {
			return "\"" + value + "\"";
		}
	}

	// A function call
	record FunctionCall(Expression functionGet, Expression[] arguments) implements Expression {
		@Override
		public @Nullable Object execute(@NotNull Context context) {
			Object function = functionGet.execute(context);
			if (function instanceof ClosureFunction closure) {
				return closure.run(context,
						Arrays.stream(arguments).map(expression -> expression.execute(context)).toArray(Object[]::new));
			} else {
				throw new RuntimeException("Not a function " + function + " in " + functionGet);
			}
		}

		@Override
		public String toString() {
			return functionGet + "("
					+ Arrays.stream(arguments).map(Expression::toString).collect(Collectors.joining(", ")) + ")";
		}
	}

	record MethodCall(Expression before, String name, Expression[] arguments) implements Expression {
		@Override
		public @Nullable Object execute(@NotNull Context context) {
			Object before = this.before.execute(context);
			assert before != null;
			Object function = context.getVariable(before.getClass().getSimpleName() + "::" + name);
			if (function instanceof ClosureFunction closure) {
				return closure.run(context,
						Stream.concat(Arrays.stream(arguments).map(expression -> expression.execute(context)),
								Stream.of(before)).toArray(Object[]::new));
			} else {
				throw new RuntimeException("Not a function " + before.getClass().getSimpleName() + "::" + name + " -> "
						+ function + " in " + before + "." + name);
			}
		}

		@Override
		public String toString() {
			return before + "." + name + "("
					+ Arrays.stream(arguments).map(Expression::toString).collect(Collectors.joining(", ")) + ")";
		}

	}

	public static void consumeSpaces(Cursor cursor) {
		while (cursor.hasNext()
				&& (cursor.peek() == ' ' || cursor.peek() == '\t' || cursor.peek() == '\n' || cursor.peek() == '\r')) {
			cursor.next();
		}
	}

	public static Optional<String> getLiteral(@NotNull Cursor cursor) {
		consumeSpaces(cursor);
		StringBuilder builder = new StringBuilder();
		while (cursor.hasNext() && (Character.isDigit(cursor.peek()) || Character.isAlphabetic(cursor.peek())
				|| cursor.peek() == '_' || cursor.peek() == '-' || cursor.peek() == '$' || cursor.peek() == ':')) {
			builder.append(cursor.next());
		}
		if (builder.isEmpty()) {
			return Optional.empty();
		}
		consumeSpaces(cursor);
		return Optional.of(builder.toString());
	}

	@NotNull
	public static Expression parseExpressionPart1(@NotNull Cursor code) throws RuntimeException {
		Expression small = parseSmallExpression(code);
		consumeSpaces(code);
		if (code.hasNext() && code.peek() == '(') {
			code.next();
			List<Expression> arguments = new ArrayList<>();
			while (code.hasNext() && code.peek() != ')') {
				arguments.add(parseExpression(code));
				consumeSpaces(code);
				if (code.hasNext() && code.peek() == ',') {
					code.next();
				}
			}
			if (code.hasNext() && code.peek() == ')') {
				code.next();
			}
			return new FunctionCall(small, arguments.toArray(Expression[]::new));
		}
		return small;
	}

	@NotNull
	public static Expression parseExpression(@NotNull Cursor code) throws RuntimeException {
		Expression small = parseExpressionPart0(code);
		consumeSpaces(code);
		while (code.hasNext() && code.peek() == '>') {
			code.next();
			Expression expression = parseExpressionPart0(code);
			if (expression instanceof FunctionCall call) {
				var k = new Expression[call.arguments.length + 1];
				System.arraycopy(call.arguments, 0, k, 0, call.arguments.length);
				k[call.arguments.length] = small;
				small = new FunctionCall(call.functionGet, k);
			} else {
				small = new FunctionCall(expression, new Expression[] { small });
			}
			consumeSpaces(code);
		}
		return small;
	}

	@NotNull
	public static Expression parseExpressionPart0(@NotNull Cursor code) throws RuntimeException {
		Expression small = parseExpressionPart1(code);
		consumeSpaces(code);
		while (code.hasNext() && code.peek() == '.') {
			code.next();
			String fnname = getLiteral(code).orElseThrow(() -> new RuntimeException("Expected function name"));
			consumeSpaces(code);

			if (code.hasNext() && code.peek() == '(') {
				code.next();
				consumeSpaces(code);
				List<Expression> arguments = new ArrayList<>();
				while (code.hasNext() && code.peek() != ')') {
					arguments.add(parseExpression(code));
					consumeSpaces(code);
					if (code.hasNext() && code.peek() == ',') {
						code.next();
					}
				}
				if (code.hasNext() && code.peek() == ')') {
					code.next();
				}
				small = new MethodCall(small, fnname, arguments.toArray(Expression[]::new));
			} else {
				small = new MethodCall(small, fnname, new Expression[0]);
			}
			consumeSpaces(code);
		}
		return small;
	}

	/*
	
	
	 */

	/**
	 * This function parses a string of code into a list of expressions Ex:
	 *
	 * @param code
	 * @return
	 */
	@NotNull
	public static Expression parseSmallExpression(@NotNull Cursor code) throws RuntimeException {
		consumeSpaces(code);
		if (code.peek() == '[') {
			code.next();
			List<Expression> expressions = new ArrayList<>(10);
			while (code.hasNext() && code.peek() != ']') {
				expressions.add(parseExpression(code));
				consumeSpaces(code);
				if (code.peek() == ',') {
					code.next();
				}
				consumeSpaces(code);
			}
			if (code.peek() != ']') {
				throw new RuntimeException("Expected ]");
			}
			code.next();
			return new FunctionCall(new Literal("List::of"), expressions.toArray(Expression[]::new));
		} else if (code.peek() == '{') {
			code.next();
			List<Expression> expressions = new ArrayList<>(10);
			while (code.hasNext() && code.peek() != '}') {
				expressions.add(parseExpression(code));
				consumeSpaces(code);
				if (code.peek() == ';') {
					code.next();
				}
				consumeSpaces(code);
			}
			if (code.peek() != '}') {
				throw new RuntimeException("Expected }");
			}
			code.next();
			return new CodeBlock(expressions);
		} else if (code.peek() == '"') {
			code.next();
			StringBuilder builder = new StringBuilder();
			while (code.hasNext() && code.peek() != '"') {
				builder.append(code.next());
			}
			if (code.peek() != '"') {
				throw new RuntimeException("Expected \"");
			}
			code.next();
			return new StringLiteral(builder.toString());
		} else if (code.peek() == '\'') {
			code.next();
			StringBuilder builder = new StringBuilder();
			while (code.hasNext() && code.peek() != '\'') {
				builder.append(code.next());
			}
			if (code.peek() != '\'') {
				throw new RuntimeException("Expected '");
			}
			code.next();
			return new StringLiteral(builder.toString());
		} else if (code.peek() == '|') {
			code.next();
			List<String> arguments = new ArrayList<>(10);
			while (code.hasNext() && code.peek() != '|') {
				Optional<String> argument = getLiteral(code);
				if (argument.isEmpty()) {
					throw new RuntimeException("Expected argument");
				}
				arguments.add(argument.get());
				consumeSpaces(code);
				if (code.peek() == ',') {
					code.next();
					consumeSpaces(code);
				}
			}
			if (code.peek() != '|') {
				throw new RuntimeException("Expected |");
			}
			code.next();
			Expression expression = parseExpression(code);
			return new Closure(arguments.toArray(String[]::new), expression);
		} else if (code.peek() == '(') {
			code.next();
			Expression expression = parseExpression(code);
			if (code.peek() != ')') {
				throw new RuntimeException("Expected )");
			}
			code.next();
			return expression;
		} else {
			consumeSpaces(code);
			Optional<String> literal = getLiteral(code);
			if (literal.isEmpty()) {
				throw new RuntimeException("Expected literal " + code.substring());
			}
			consumeSpaces(code);
			if (code.startsWith("=")) {
				code.skip(1);
				return new Assignment(literal.get(), parseExpression(code));
			}
			String lit = literal.get();
			try {
				return new Number(Integer.parseInt(lit));
			} catch (NumberFormatException ignored) {
			}
			if (lit.chars().filter(e -> e == ':').count() == 1) {
				if (lit.startsWith("minecraft:") || lit.startsWith(":")) {
					return new Item(new CItem(Registry.ITEM.get(new Identifier(lit.split(":")[1]))));
				} else {
					return new Item(new CItem(lit));
				}
			}
			return new Literal(lit);
		}
	}

}
