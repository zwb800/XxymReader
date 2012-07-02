package com.reader.xxym;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {
	static void sendMail(String subject,String content)
	{
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", "smtp.tom.com");
		props.setProperty("mail.smtp.port", "25");
		props.setProperty("mail.smtp.auth", "true");
		Session session = Session.getInstance(props, new Authenticator(){
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("zwb.88","12345");
			}
		});
		
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress("zwb.88@tom.com"));
			message.setText(content);
			message.setSubject(subject);
			Address address = new InternetAddress("zwb800@gmail.com");
			message.setRecipient(Message.RecipientType.TO, address);
			message.setSentDate(new Date());
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
