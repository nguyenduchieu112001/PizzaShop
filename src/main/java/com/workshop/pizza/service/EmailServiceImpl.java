package com.workshop.pizza.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileSystemResource;
//import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.workshop.pizza.controller.form.CodeAndExpiration;
import com.workshop.pizza.controller.form.EmailDetails;
import com.workshop.pizza.exception.BadRequestException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

//	@Autowired(required = true)
//	private CacheManager cacheManager;

	@Value("${spring.mail.username}")
	private String sender;
	
	@Autowired
	private Configuration config;
	
	Cache<String, CodeAndExpiration> cache = Caffeine.newBuilder()
	        .maximumSize(1000)
	        .build();

	@Override
	public String sendSimpleMail(EmailDetails details) {
		// Try block to check for exceptions
		try {

			// Creating a simple mail message
			SimpleMailMessage mailMessage = new SimpleMailMessage();

			// Setting up necessary details
			mailMessage.setFrom(sender);
			mailMessage.setTo(details.getRecipient());
			mailMessage.setText(details.getMessageBody());
			mailMessage.setSubject(details.getSubject());

			// Sending the mail
			javaMailSender.send(mailMessage);
			return "Mail Sent Successfully...";
		}

		// Catch block to handle the exceptions
		catch (Exception e) {
			return "Error while Sending Mail";
		}
	}
	
	public CodeAndExpiration sendMailByFreeMarker(String recipient) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			Map<String, Object> model = new HashMap<>();
			LocalTime expirationTime = LocalTime.now().plusMinutes(3);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	        String formattedTime = expirationTime.format(formatter);
			SecureRandom random = new SecureRandom();
			String code = String.format("%06d", random.nextInt(900000) + 100000) ;
			
			model.put("Name", "Expired time:");
			model.put("time", formattedTime);
			model.put("code", code);
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			Template t = config.getTemplate("email-template.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
			
			helper.setTo(recipient);
			helper.setText(html, true);
			helper.setSubject("Forgot Password Code: " + code);
			helper.setFrom(sender);
			javaMailSender.send(message);
			
			CodeAndExpiration codeAndExpiration = new CodeAndExpiration(code, expirationTime);
	        cache.put("customer_code", codeAndExpiration);

	        return codeAndExpiration;
		} catch (Exception e) {
			throw new RuntimeException("Failed to send email: " + e.getMessage());
		}
	}
	
	public boolean isValidCode(String code) {
		CodeAndExpiration codeAndExpiration = cache.getIfPresent("customer_code");
		if(codeAndExpiration == null) {
			throw new BadRequestException("Code is expired");
		}
		return !(codeAndExpiration == null || !codeAndExpiration.getCode().equals(code)
				|| LocalTime.now().isAfter(codeAndExpiration.getExpirationTime()));
	}

	@Override
	public String sendMailWithAttachment(EmailDetails details) {
		// Creating a mime message
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper;

		try {

			// Setting multipart as true for attachments to
			// be send
			mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(sender);
			mimeMessageHelper.setTo(details.getRecipient());
			mimeMessageHelper.setText(details.getMessageBody());
			mimeMessageHelper.setSubject(details.getSubject());

			// Adding the attachment
			FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

			mimeMessageHelper.addAttachment(file.getFilename(), file);

			// Sending the mail
			javaMailSender.send(mimeMessage);
			return "Mail sent Successfully";
		}

		// Catch block to handle MessagingException
		catch (MessagingException e) {

			// Display message when exception occurred
			return "Error while sending mail!!!";
		}
	}

}
