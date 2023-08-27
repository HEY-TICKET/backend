package com.heyticket.backend.service;

import com.heyticket.backend.service.dto.PushInfo;

public class DummyFcmService implements IFcmService {

    @Override
    public void sendTopicMessage(String topic, PushInfo pushInfo) {

    }

    @Override
    public void subscribeTopic(String fcmToken, String topic) {

    }

    @Override
    public void unsubscribeTopic(String registrationToken, String topic) {

    }
}
