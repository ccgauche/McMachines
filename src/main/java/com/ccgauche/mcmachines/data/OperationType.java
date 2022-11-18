package com.ccgauche.mcmachines.data;

/**
 * From minecraft source code
 */
public enum OperationType {
	Add(0), AddMultiply(1), MultiplyMultiply(2);

	private final int operation;

	OperationType(int operation) {
		this.operation = operation;
	}

	public int getOperation() {
		return operation;
	}
}
