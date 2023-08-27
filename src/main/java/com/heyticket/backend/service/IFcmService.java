package com.heyticket.backend.service;

import com.heyticket.backend.service.dto.PushInfo;

public interface IFcmService {

    void sendTopicMessage(String topic, PushInfo pushInfo);

    void subscribeTopic(String fcmToken, String topic);

    void unsubscribeTopic(String registrationToken, String topic);
}
