package com.placideh.rateLimiter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



public class SendEmailRequest {

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String to;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Email body is required")
    private String body;

    public SendEmailRequest(String to,String subject,String body){
        this.to =to;
        this.subject=subject;
        this.body=body;
    }


    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }
}