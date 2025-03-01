package com.bookwheelapp.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to)
	{
		//rest of the code..
		
		boolean f = false;
		
		String from = "manujadhav8805@gmail.com";
		
		//variable for gmail
		String host = "smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES: "+properties);
		
		//Setting important information to properties object
		
		//host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.port", 587);
		properties.put("mail.smtp.ssl.enabled", true);
		properties.put("mail.smtp.auth", true);
		
		//Step 1: to get the session object..
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication("javamanisha9689@gmail.com", "mhqt wakl esye njmt ");
			}
		});
		
		session.setDebug(true);
		
		//Step 2 : compose the message [text,multi media]
		MimeMessage m = new MimeMessage(session);
		
		try {
			//from email
			m.setFrom(from);
			
			//adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject to message
			m.setSubject(subject);
			
			//adding text to message
//			m.setText(message);
			m.setContent(message,"text/html");

			//send
			
			//Step 3 : send the message using Transport class
			Transport.send(m);
			
			System.out.println("sent success...................");
			
			f = true;
		}catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return f;
	}
	

	
}
