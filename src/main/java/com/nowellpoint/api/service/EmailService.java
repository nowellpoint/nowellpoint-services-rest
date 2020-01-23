package com.nowellpoint.api.service;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;

import com.nowellpoint.services.rest.model.EmailEvent;
import com.nowellpoint.services.rest.model.User;
import com.nowellpoint.services.rest.util.ConfigProperties;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

@ApplicationScoped
public class EmailService {
	
	private static final String SENDER_EMAIL = "no-reply@nowellpoint.com";
	private static final String SENDER_NAME = "Nowellpoint Support";
	
	private SendGrid sendgrid = null;
	
	@Inject
	Logger logger;
	
	@Inject
	Config config;
	
	@Inject
	Event<EmailEvent> emailEvent;
	
	@PostConstruct
	public void init() {
		sendgrid = new SendGrid(config.getValue(ConfigProperties.SENDGRID_API_KEY, String.class));
	}
	
	public void sendEmailVerificationMessage(User user, String emailVerificationToken) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				Email from = new Email();
				from.setEmail(SENDER_EMAIL);
				from.setName(SENDER_NAME);
			    
			    Email to = new Email();
			    to.setEmail(user.getEmail());
			    to.setName(user.getName());
			    
			    Content content = new Content();
			    content.setType("text/html");
			    content.setValue("<html><body>some text here</body></html>");
			    
			    Personalization personalization = new Personalization();
			    personalization.addTo(to);
			    personalization.addSubstitution("%name%", user.getName());
			    personalization.addSubstitution("%emailVerificationToken%", UriBuilder.fromUri("http://localhost:8080/services/registration")
			    		.queryParam("emailVerificationToken", "{emailVerificationToken}")
						.build(emailVerificationToken)
						.toString());
			    
			    Mail mail = new Mail();
			    mail.setFrom(from);
			    mail.addContent(content);
			    mail.setSubject("Verify your email address");
			    mail.setTemplateId("3e2b0449-2ff8-40cb-86eb-32cad32886de");
			    mail.addPersonalization(personalization);
			    
			    try {
			    
			    	Request request = new Request();
				    request.setMethod(Method.POST);
			    	request.setEndpoint("mail/send");
			    	request.setBody(mail.build());
			    	
			    	Response response = sendgrid.api(request);
			    	
			    	EmailEvent event = EmailEvent.builder()
			    			.body(mail.build())
			    			.subject(mail.getSubject())
			    			.sentDate(Instant.now())
			    			.toId(user.getId())
			    			.status(response.getStatusCode() == 202 ? "Success" : "Failure")
			    			.build();
			    	
			    	emailEvent.fireAsync(event);
			    	
			    } catch (IOException e) {
			    	logger.error(e);
			    }
			}
		});
	}
}