package com.heyticket.backend.service;

import com.heyticket.backend.service.dto.request.EmailSendRequest;

public interface IEmailService {

    String sendSimpleMessage(EmailSendRequest request);
}
