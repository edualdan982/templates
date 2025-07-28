package com.firmas.service.correo;

/**
 * Interfaz para el servicio de mensajería para el whatsapp
 */
public interface IMensajeriaService {
  boolean mandarMensaje(int codigo, int numero, String mensaje);

  boolean mandarPdf(int codigo, int numero, String mensaje, String titulo, byte[] pdf);

}
