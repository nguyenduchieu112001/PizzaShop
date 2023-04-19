package com.workshop.pizza.service;

import java.io.File;
import java.time.LocalTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.workshop.pizza.controller.form.CodeAndExpiration;
import com.workshop.pizza.controller.form.EmailDetails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired(required = true)
	private CacheManager cacheManager;

	@Value("${spring.mail.username}")
	private String sender;

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

	public boolean isValidCode(String customerCode) {
		// Get the cached code and expiration time
		Cache cache = cacheManager.getCache("customer_code");
		String cachedCode = cache.get("code", String.class);
		LocalTime expiredTime = cache.get("expiredTime", LocalTime.class);

		// Check if the cached code is valid and not expired
		if (cachedCode != null && cachedCode.equals(customerCode) && LocalTime.now().isBefore(expiredTime)) {
			// Remove the code from the cache
			cache.evict("code");
			cache.evict("expiredTime");

			return true;
		} else {
			return false;
		}
	}

	@Cacheable(value = "customer_code", key = "#recipient + '_' + T(java.time.Instant).now().toEpochMilli()")
	public CodeAndExpiration sendMail(String recipient) {
		try {
			// Generate code and expiration time
			Random random = new Random();
			String code = String.format("%06d", random.nextInt(1000000));

			// Cache the code and expiration time
			LocalTime expirationTime = LocalTime.now().plusMinutes(3);

			// Creating a simple mail message
			SimpleMailMessage mailMessage = new SimpleMailMessage();

			// Setting up necessary details
			mailMessage.setFrom(sender);
			mailMessage.setTo(recipient);

			// Adding code and expiration time to message body
			String messageBody = String.format("Code: %s\n\nExpiration Time at: %s", code, expirationTime);
			mailMessage.setText(messageBody);
			mailMessage.setSubject("PizzaShop");

			// Sending the mail
			javaMailSender.send(mailMessage);

			return new CodeAndExpiration(code, expirationTime);
		} catch (MailException ex) {
			// Handle any exceptions that occur when sending the email
			// You could log the error, send an error response, or retry sending the email
			ex.printStackTrace();
			throw new RuntimeException("Failed to send email: " + ex.getMessage());
		}

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
