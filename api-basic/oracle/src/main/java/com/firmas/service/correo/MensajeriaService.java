package com.firmas.service.correo;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.firmas.service.util.IConsumirRestService;

@Service
public class MensajeriaService implements IMensajeriaService {
  private String urlMen = "https://mensajeria.umsa.bo/api/whatsapp";
  // private String urlMen = "http://localhost:4000/api/whatsapp/";
  private static final String MSG_SUCCESS = "Mensaje enviado correctamente";
  private static final String MSG_ERROR = "No se puedo enviar el mensaje";
  private static final Logger log = LoggerFactory.getLogger(MensajeriaService.class);

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private IConsumirRestService consumirRestService;

  @Override
  public boolean mandarMensaje(int codigo, int numero, String mensaje) {

    log.debug("Mandando mensaje al +{}-{}: {}", codigo, numero, mensaje);

    ObjectNode jsonBody = objectMapper.createObjectNode();
    jsonBody.put("telefono", numero);
    jsonBody.put("mensaje", mensaje);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    try {
      ResponseEntity<ObjectNode> response = consumirRestService.consumirRest(
          String.format("%s/enviarMensaje", urlMen), HttpMethod.POST, headers, jsonBody);
      if (response == null)
        return false;
      if (response.toString().equals(MSG_SUCCESS)) {
        log.debug(MSG_SUCCESS);
        return true;
      } else {
        log.debug(MSG_ERROR);
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.error("Error al enviar el mensaje. Detalle: {}",
          (e.getLocalizedMessage() == null ? "Sin detalle" : e.getLocalizedMessage()));
    }
    return true;
  }

  @Override
  public boolean mandarPdf(int codigo, int numero, String mensaje, String titulo, byte[] pdf) {
    try {
      // Convierte el PDF a una cadena Base64
      String pdfBase64 = Base64.getEncoder().encodeToString(pdf);

      ObjectNode jsonBody = objectMapper.createObjectNode();
      jsonBody.put("telefono", numero);
      jsonBody.put("mensaje", mensaje);
      jsonBody.put("titulo", titulo);
      jsonBody.put("pdf", pdfBase64);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      ResponseEntity<ObjectNode> response = consumirRestService.consumirRest(
          String.format("%s/enviarMensajePDF", urlMen), HttpMethod.POST, headers, jsonBody);

      log.info("Estado mensaje al %d %d: %s", codigo, numero, response.toString());
      return "Mensaje enviado correctamente".equals(response.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

}
