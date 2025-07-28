package com.firmas.service.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

public interface IFechaServiceUtil {
	/**
	 * 
	 * @return Se obtiene la gestión actual en java.lang.Integer
	 */
	Integer obtenerGestionActual();

	/**
	 * 
	 * @param fechaString String --> La Cadena de la fecha
	 * @param pattern     String --> El formato Ej. "dd-MM-yyyy"
	 * @return Devuelve un java.util.Date.
	 * @throws ParseException
	 */
	Date convetirFecha(String fechaString, String pattern) throws ParseException;

	/**
	 * 
	 * @param fechaDate Date --> El obeto Date a convertir
	 * @param pattern   String --> El patron de conversion
	 * @return Devuelve un java.util.Date en formato String.
	 * @throws ParseException
	 */
	String convertirFecha(Date fechaDate, String pattern) throws ParseException;

	/**
	 * 
	 * @param strDate String --> La cadena a convertir Timestamp
	 * @param pattern Date --> El patron que se usara el conversion
	 * @return Devuelve un Java.sql.timestamp
	 * @throws ParseException
	 */
	Timestamp convertStringToTimestamp(String strDate, String pattern) throws ParseException;

	/**
	 * 
	 * @param date Date --> El objeto Date a convertir
	 * @return Un objeto Java.sql.Timestamp
	 * @throws ParseException
	 */
	Timestamp convertirDateToTiemstamp(Date date) throws ParseException;

	/**
	 * 
	 * @param fechaInicio
	 * @param fechaFin
	 * @return
	 */
	Boolean beetweenFechas(Date fechaInicio, Date fechaFin);
	
	
	Boolean compararFechas(Date arg1, Date arg2);
}
