package com.firmas.service.correo;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.firmas.entity.dt.CorreoData;

@Service
public class CorreoService implements ICorreoService {
	private static final Logger log = LoggerFactory.getLogger(CorreoService.class);
	private static final String HOST = "smtp.gmail.com";
	private static final String PORT = "465";
	private static final boolean SSL = true;
	private static final boolean AUTH = true;

	@Value("${spring.profiles.active}")
	private String ENTORNO;

	@Override
	public String enviarTexto(CorreoData dataLogin, String[] datosEnviar, String[] correos) {
		log.info("Iniciando el envio de Correo de Texto...");
		Properties properties = System.getProperties();

		properties.put("mail.smtp.host", HOST);
		properties.put("mail.smtp.port", PORT);
		properties.put("mail.smtp.ssl.enable", SSL);
		properties.put("mail.smtp.auth", AUTH);

		// INICIOO: Seteando el Correo que se usara para mandar.
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(dataLogin.getCorreo(), dataLogin.getKeyAcceso());
			}
		});
		// FIN
		if (ENTORNO.equals("dev"))
			session.setDebug(true);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(dataLogin.getCorreo()));
			if (correos != null) {
				for (int i = 0; i < correos.length; i++) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(correos[i]));
				}
			} else
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(datosEnviar[0]));
			log.info("Asunto: " + datosEnviar[1]);
			message.setSubject(datosEnviar[1]);
			message.setText(datosEnviar[2], "ISO-8859-1", "html");

			message.setSentDate(new Date());
			log.info("Enviando...");
			Transport.send(message);
			log.info("Mensaje enviado con éxito ...");
			return "OK";
		} catch (MessagingException mex) {
			mex.printStackTrace();
			return "ERROR";
		}
	}

	@Override
	public String enviarTextoAndObjeto(CorreoData dataLogin, String[] datosEnviar, Map<String, File> files) {
		log.info("Iniciando el envio de Correo de Texto...");
		Properties properties = System.getProperties();

		properties.put("mail.smtp.host", HOST);
		properties.put("mail.smtp.port", PORT);
		properties.put("mail.smtp.ssl.enable", SSL);
		properties.put("mail.smtp.auth", AUTH);

		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.socketFactory.fallback", "false");
		properties.setProperty("mail.smtp.socketFactory.port", PORT);

		// INICIOO: Seteando el Correo que se usara para mandar.
		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(dataLogin.getCorreo(), dataLogin.getKeyAcceso());
			}
		});
		// FIN
		if (ENTORNO.equals("dev"))
			session.setDebug(true);
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(dataLogin.getCorreo()));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(datosEnviar[0]));

			log.info("Asunto: " + datosEnviar[1]);
			message.setSubject(datosEnviar[1]);

			Multipart multipart = new MimeMultipart();

			BodyPart mensajeBodyPart = new MimeBodyPart();
			mensajeBodyPart.setContent(datosEnviar[2], "text/html;charset=UTF-8");
			multipart.addBodyPart(mensajeBodyPart);

			if (files != null) {
				for (String clave : files.keySet()) {
					BodyPart archivoAdjuntoBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(files.get(clave));

					archivoAdjuntoBodyPart.setDataHandler(new DataHandler(source));
					archivoAdjuntoBodyPart.setFileName(clave);
					multipart.addBodyPart(archivoAdjuntoBodyPart);
				}
			}

			message.setContent(multipart);

			log.info("Enviando...");
			Transport.send(message);
			log.info("Mensaje enviado con éxito a: " + datosEnviar[0]);
			return "OK";
		} catch (MessagingException mex) {
			mex.printStackTrace();
			return "ERROR";
		}
	}

}
