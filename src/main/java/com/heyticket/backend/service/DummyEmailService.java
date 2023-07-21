package com.heyticket.backend.service;

import com.heyticket.backend.service.dto.request.EmailSendRequest;

public class DummyEmailService implements IEmailService {

    public String sendSimpleMessage(EmailSendRequest request) {
        return request.getEmail();
    }
}
