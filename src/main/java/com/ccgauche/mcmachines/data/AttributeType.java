package com.ccgauche.mcmachines.data;

public enum AttributeType {

	MAX_HEALTH, ARMOR, ARMOR_TOUGHNESS, ATTACK_DAMAGE, ATTACK_KNOCKBACK, ATTACK_SPEED, FLYING_SPEED,
	KNOCKBACK_RESISTANCE, LUCK, FOLLOW_RANGE, MOVEMENT_SPEED;

	@Override
	public String toString() {
		return "generic." + this.name().toLowerCase();
	}
}
