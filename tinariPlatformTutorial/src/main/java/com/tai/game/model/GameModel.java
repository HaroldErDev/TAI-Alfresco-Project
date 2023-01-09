package com.tai.game.model;

import org.alfresco.service.namespace.QName;

public interface GameModel {
	
	//Namespaces
	public static final String NAMESPACE_URI_GAME_MODEL = "http://www.game.com/model/content/1.0";
	
	//Model
	public static final QName MODEL_G_GAME_MODEL = QName.createQName(NAMESPACE_URI_GAME_MODEL, "gameModel");
	
	//Types
	public static final QName TYPE_G_WEAPON = QName.createQName(NAMESPACE_URI_GAME_MODEL, "weapon");
	public static final QName TYPE_G_OPERATOR = QName.createQName(NAMESPACE_URI_GAME_MODEL, "operator");
	public static final QName TYPE_G_OPERATOR_CLASS = QName.createQName(NAMESPACE_URI_GAME_MODEL, "operatorClass");
	
	//Aspects
	public static final QName ASPECT_G_BLOCKED = QName.createQName(NAMESPACE_URI_GAME_MODEL, "blocked");
	public static final QName ASPECT_G_SKIN = QName.createQName(NAMESPACE_URI_GAME_MODEL, "skin");
	public static final QName ASPECT_G_ERROR = QName.createQName(NAMESPACE_URI_GAME_MODEL, "error");
	
	//Properties
	public static final QName PROP_G_WEAPON_NAME = QName.createQName(NAMESPACE_URI_GAME_MODEL, "weaponName");
	public static final QName PROP_G_FIRE_MODE = QName.createQName(NAMESPACE_URI_GAME_MODEL, "fireMode");
	public static final QName PROP_G_TOTAL_AMMO = QName.createQName(NAMESPACE_URI_GAME_MODEL, "totalAmmo");
	public static final QName PROP_G_WEAPON_TYPE = QName.createQName(NAMESPACE_URI_GAME_MODEL, "weaponType");
	public static final QName PROP_G_OPERATOR_NAME = QName.createQName(NAMESPACE_URI_GAME_MODEL, "operatorName");
	public static final QName PROP_G_NATIONALITY = QName.createQName(NAMESPACE_URI_GAME_MODEL, "nationality");
	public static final QName PROP_G_SPECIAL_ABILITY = QName.createQName(NAMESPACE_URI_GAME_MODEL, "specialAbility");
	public static final QName PROP_G_CLASS_TYPE = QName.createQName(NAMESPACE_URI_GAME_MODEL, "classType");
	public static final QName PROP_G_IS_BLOCKED = QName.createQName(NAMESPACE_URI_GAME_MODEL, "isBlocked");
	public static final QName PROP_G_SKIN_NAME = QName.createQName(NAMESPACE_URI_GAME_MODEL, "skinName");
	public static final QName PROP_G_ERROR_MESSAGE = QName.createQName(NAMESPACE_URI_GAME_MODEL, "errorMessage");
	
	//Associations
	public static final QName ASSOC_G_RELATED_OPERATORS = QName.createQName(NAMESPACE_URI_GAME_MODEL, "relatedOperators");
	
	//Constraints
	public static final QName CONS_G_FIRE_MODE_LIST = QName.createQName(NAMESPACE_URI_GAME_MODEL, "fireModeList");
	public static final QName CONS_G_OPERATOR_CLASS_LIST = QName.createQName(NAMESPACE_URI_GAME_MODEL, "operatorClassList");
	
}
