package com.firmas.util.proceso;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FechaConverter {
	private FechaConverter() {
	}

	/**
	 * 
	 * @param fechaString String --> La Cadena de la fecha
	 * @param pattern     String --> El formato Ej. "dd-MM-yyyy"
	 * @return Devuelve un java.util.Date.
	 * @throws ParseException
	 */
	public static Date convetirFecha(String fechaString, String pattern) throws ParseException {
		DateFormat formato = new SimpleDateFormat(pattern);
		return formato.parse(fechaString);
	}

	/**
	 * 
	 * @param fechaDate Date --> El obeto Date a convertir
	 * @param pattern   String --> El patron de conversion
	 * @return Devuelve un java.util.Date en formato String.
	 * @throws ParseException
	 */
	public static String convertirFecha(Date fechaDate, String pattern)
			throws NullPointerException, IllegalArgumentException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(fechaDate);
	}

	/**
	 * 
	 * @param strDate String --> La cadena a convertir Timestamp
	 * @param pattern Date --> El patron que se usara el conversion
	 * @return Devuelve un Java.sql.timestamp
	 * @throws ParseException
	 */
	public static Timestamp convertStringToTimestamp(String strDate, String pattern) throws ParseException {

		DateFormat formatter = new SimpleDateFormat(pattern);
		Date date = formatter.parse(strDate);

		return new Timestamp(date.getTime());
	}

	/**
	 * 
	 * @param date Date --> El objeto Date a convertir
	 * @return Un objeto Java.sql.Timestamp
	 * @throws ParseException
	 */
	public static Timestamp convertirDateToTiemstamp(Date date) {
		return new Timestamp(date.getTime());
	}

	public static String fechaStringCuf(String fechaHora) {
		String aux = fechaHora;
		aux = aux.replace("-", "");
		aux = aux.replace("T", "");
		aux = aux.replace(":", "");
		aux = aux.replace(".", "");

		return aux;
	}
}
