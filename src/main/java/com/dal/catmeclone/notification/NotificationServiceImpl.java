package com.dal.catmeclone.notification;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.dal.catmeclone.SystemConfig;
import com.dal.catmeclone.model.Course;
import com.dal.catmeclone.model.User;

@Service
public class NotificationServiceImpl implements NotificationService {

	final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

	private String fromgmail;

	private String fromPassword;

	private String subject;

	private String appurl;

	@Override
	public void sendNotificationToNewuser(User user, String password, Course course) {
		

		Properties property = SystemConfig.instance().getProperties();
		subject = property.getProperty("account.subject");
		appurl = property.getProperty("app.url");

		String body = property.getProperty("accountcreation.email.body");
		body =  body.replace("FIRSTNAME", user.getFirstName());
		body =  body.replace("BANNERID", user.getBannerId());
		body =  body.replace("PASSWORD", user.getPassword());
		body =  body.replace("URL", appurl+"/login");
		body =  body.replace("COURSE", String.valueOf(course.getCourseID()));
		

		send(user.getEmail(), subject, body);

	}

	public void sendNotificationForPassword(String bannerId, String password, String sendto) {

		Properties property = SystemConfig.instance().getProperties();

		fromgmail = property.getProperty("from.email");
		fromPassword = property.getProperty("from.password");

		appurl = System.getenv("loginurl");
		
		String body = property.getProperty("forgotpassword.email.body");
		body=body.replaceAll("BANNERID", bannerId);
		body=body.replace("URL", password);
		
		String subject = "Forgot password";
		send(sendto, subject, body);
	}

	private void send(String to, String sub, String msg) {

		Properties property = SystemConfig.instance().getProperties();

		fromgmail = property.getProperty("from.email");
		fromPassword = property.getProperty("from.password");

		// Get properties object
		Properties props = new Properties();
		props.put("mail.smtp.host", property.getProperty("mail.smtp.host"));
		props.put("mail.smtp.socketFactory.port", property.getProperty("mail.smtp.socketFactory.port"));
		props.put("mail.smtp.socketFactory.class", property.getProperty("mail.smtp.socketFactory.class"));
		props.put("mail.smtp.auth", property.getProperty("mail.smtp.auth"));
		props.put("mail.smtp.port", property.getProperty("mail.smtp.port"));
		props.put("mail.smtp.starttls.enable", property.getProperty("mail.smtp.starttls.enable"));

		// get Session
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromgmail, fromPassword);
			}
		});

		// compose message

		MimeMessage message = new MimeMessage(session);
		try {
			
			message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(sub);
			message.setText(msg, "UTF-8", "html");

			// send message
			Transport.send(message);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			logger.error("Error in sending email");
		}
	}
}
