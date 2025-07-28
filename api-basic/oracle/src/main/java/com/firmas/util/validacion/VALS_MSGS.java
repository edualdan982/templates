package com.firmas.util.validacion;

public class VALS_MSGS {
	public static final String REG_EXP_COMPLEMENTO = "^$|^([1-9]{1}[a-zA-z]{1})|([a-zA-z]{1}[1-9]{1})$|^\0$";
	public static final String MSG_COMPLEMENTO = "Debe ser una letra y un numero o viceversa Ej. A1";

	public static final String REG_EXP_PERIODO = "^([0-9]{1,4})$|^([0-9]{1,2}-[0-9]{1,4}$)";
	public static final String MSG_PERIODO = "Puede tener un mes y/o gestion, Ej. 1, 20, 303 o 12-1852";

	public static final String MSG_NULO = "No puede ser nulo";
	public static final String MSG_VACIO = "No puede ser vacio";
	public static final String MSG_EMPTY = "No puede ser vacio o nulo";

	public static final String REG_EXP_CORREO = "^(.+)@(\\S+)$";
	public static final String REG_EXP_CORREO_NULO = "^(.+)@(\\S+)$|^$";
	public static final String MSG_CORREO = "No es un correo valido Ej. usuario@dominio.com";

	public static final String MSG_POSITIVO = "No debe ser negativo o cero";

	private VALS_MSGS() {

	}
}
