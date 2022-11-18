package com.ccgauche.mcmachines.lang;

/**
 * A cursor is a pointer to a specific position in a string.
 */
public class Cursor {

	private final String string;
	private int index;

	public Cursor(String string) {
		this.string = string;
		index = 0;
	}

	public char next() {
		return string.charAt(index++);
	}

	public char peek() {
		return string.charAt(index);
	}

	public boolean hasNext() {
		return index < string.length();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getString() {
		return string;
	}

	public String substring(int start, int end) {
		return string.substring(start, end);
	}

	public String substring(int start) {
		return string.substring(start);
	}

	public boolean startsWith(String string) {
		return this.string.startsWith(string, index);
	}

	public void skip(int i) {
		index += i;
	}

	public String substring() {
		return string.substring(index);
	}

	public Cursor copy() {
		Cursor cursor = new Cursor(string);
		cursor.setIndex(index);
		return cursor;
	}

}
