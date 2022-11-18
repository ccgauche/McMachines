package com.ccgauche.mcmachines.utils;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public class TextUtils {

	/**
	 * @param s The string to be formatted
	 * @return The formatted string
	 */
	@NotNull
	public static Text from(@NotNull String s) {
		List<Text> texts = new ArrayList<>();
		List<Formatting> formattingList = new ArrayList<>();
		boolean formatting = false;
		StringBuilder current = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c == '&' || c == '§') {
				formatting = true;
				continue;
			}
			if (formatting) {
				formatting = false;
				for (Formatting f : Formatting.values()) {
					if (f.getCode() == c) {
						if (!f.isModifier()) {
							texts.add(new LiteralText(current.toString())
									.formatted(formattingList.toArray(new Formatting[0])));
							formattingList.clear();
							current = new StringBuilder();
						}
						formattingList.add(f);
						break;
					}
				}
			} else {
				current.append(c);
			}
		}
		texts.add(new LiteralText(current.toString()).formatted(formattingList.toArray(new Formatting[0])));

		return Texts.join(texts, LiteralText.EMPTY, e -> e);
	}
}
