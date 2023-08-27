package com.heyticket.backend.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.heyticket.backend.exception.FcmFailureException;
import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.NotFoundException;
import com.heyticket.backend.service.dto.PushInfo;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

@Slf4j
public class FcmService implements IFcmService {

    private static final String KEY_FILE_PATH = "src/main/resources/firebase/key/hey-ticket-firebase-adminsdk-4bp7r-194d242388.json";

    @PostConstruct
    public void init() {
        try (final FileInputStream fis = new FileInputStream(KEY_FILE_PATH)){
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(fis))
                .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to init Firebase");
        }
    }

    @Override
    public void sendTopicMessage(String topic, PushInfo pushInfo) {
        Notification notification = Notification.builder()
            .setTitle("'" + topic + "' " + pushInfo.getTitle())
            .setBody(pushInfo.getBody())
            .build();

        Message message = Message.builder()
            .setTopic(topic)
            .setNotification(notification)
            .putData("type", pushInfo.getType())
            .putData("id", pushInfo.getId())
            .build();

        String response;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send topic message. topic : {}", topic);
            throw new FcmFailureException("Failed to send topic message");
        }
        log.info("Successfully sent message: " + response);
    }

    @Override
    public void subscribeTopic(String fcmToken, String topic) {
        if (ObjectUtils.isEmpty(fcmToken)) {
            throw new NotFoundException("Fcm token is not found.");
        }
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(fcmToken), topic);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to subscribe topic. topic : {}", topic);
            throw new FcmFailureException("Failed to subscribe topic", InternalCode.SERVER_ERROR);
        }
    }

    @Override
    public void unsubscribeTopic(String registrationToken, String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(registrationToken), topic);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to unsubscribe topic. topic : {}", topic);
            throw new FcmFailureException("Failed to unsubscribe topic", InternalCode.SERVER_ERROR);
        }
    }
}
