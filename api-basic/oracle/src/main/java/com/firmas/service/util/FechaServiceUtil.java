package com.firmas.service.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FechaServiceUtil implements IFechaServiceUtil {

  private static final Logger log = LoggerFactory.getLogger(FechaServiceUtil.class);

  @Override
  public Integer obtenerGestionActual() {
    log.info("Obteniendo fecha actual");
    SimpleDateFormat getFormatoAnio = new SimpleDateFormat("yyyy");
    return Integer.parseInt(getFormatoAnio.format(new Date()));
  }

  @Override
  public Date convetirFecha(String fechaString, String pattern) throws ParseException {
    DateFormat formato = new SimpleDateFormat(pattern);
    return formato.parse(fechaString);
  }

  @Override
  public String convertirFecha(Date fechaDate, String pattern) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    return sdf.format(fechaDate);
  }

  @Override
  public Timestamp convertStringToTimestamp(String strDate, String pattern) throws ParseException {

    DateFormat formatter = new SimpleDateFormat(pattern);
    Date date = formatter.parse(strDate);

    return new Timestamp(date.getTime());
  }

  @Override
  public Timestamp convertirDateToTiemstamp(Date date) throws ParseException {
    return new Timestamp(date.getTime());
  }

  @Override
  public Boolean beetweenFechas(Date fechaInicio, Date fechaFin) {

    if (fechaInicio == null || fechaFin == null) {
      return false;
    }
    Calendar calendarInicio = Calendar.getInstance();
    // Setear hora de inicio a 00:00
    calendarInicio.setTime(fechaInicio);
    calendarInicio.set(Calendar.HOUR_OF_DAY, 0);
    calendarInicio.set(Calendar.MINUTE, 0);
    calendarInicio.set(Calendar.SECOND, 0);
    calendarInicio.set(Calendar.MILLISECOND, 0);

    Calendar calendarFin = Calendar.getInstance();
    // Setear hora de fin a 23:50
    calendarFin.setTime(fechaFin);
    calendarFin.set(Calendar.HOUR_OF_DAY, 23);
    calendarFin.set(Calendar.MINUTE, 50);
    calendarFin.set(Calendar.SECOND, 0);
    calendarFin.set(Calendar.MILLISECOND, 0);

    Date ahora = new Date();

    return !(ahora.compareTo(calendarInicio.getTime()) >= 0 && ahora.compareTo(calendarFin.getTime()) <= 0);

  }

  @Override
  public Boolean compararFechas(Date arg1, Date arg2) {
     if (arg1 == null || arg2 == null) {
      return false;
    }
    Calendar calendarInicio = Calendar.getInstance();
    // Setear hora de inicio a 00:00
    calendarInicio.setTime(arg1);
    calendarInicio.set(Calendar.HOUR_OF_DAY, 0);
    calendarInicio.set(Calendar.MINUTE, 0);
    calendarInicio.set(Calendar.SECOND, 0);
    calendarInicio.set(Calendar.MILLISECOND, 0);

    Calendar calendarFin = Calendar.getInstance();
    // Setear hora de fin a 23:50
    calendarFin.setTime(arg2);
    calendarFin.set(Calendar.HOUR_OF_DAY, 0);
    calendarFin.set(Calendar.MINUTE, 0);
    calendarFin.set(Calendar.SECOND, 0);
    calendarFin.set(Calendar.MILLISECOND, 0);

    return calendarInicio.getTime().equals(calendarFin.getTime());
  }
}
