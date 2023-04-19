package com.workshop.pizza.service;

import com.workshop.pizza.controller.form.EmailDetails;

public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(EmailDetails details);
 
    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
}
