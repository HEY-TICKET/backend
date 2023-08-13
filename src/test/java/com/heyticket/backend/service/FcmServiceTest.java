package com.heyticket.backend.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.TopicManagementResponse;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

@Disabled // Fcm 학습 테스트
public class FcmServiceTest {

    private static final String KEY_FILE_PATH = "firebase/key/heyticket-test-firebase-adminsdk-tbp6w-2eb362abb1.json";

    private static final String TOPIC = "topic";

    @Test
    @DisplayName("FCM sdk client 초기화")
    void initFcmSdkClient() {
        ClassPathResource serviceAccount = new ClassPathResource(KEY_FILE_PATH);

        FirebaseOptions options;
        try {
            options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FirebaseApp.initializeApp(options);
    }

    @Test
    @DisplayName("FCM topic 등록")
    void subscribeTopic() throws FirebaseMessagingException {
        // Client FCM SDK에서 등록 후 받은 token 정보
        List<String> registrationTokens = List.of("token1, token2");

        // 등록 token들에 해당하는 기기들을 topic에 등록한다.
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
            registrationTokens, TOPIC);

        System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");
    }

    @Test
    @DisplayName("FCM topic 등록 취소")
    void unsubscribeTopic() throws FirebaseMessagingException {
        // Client FCM SDK에서 등록 후 받은 token 정보
        List<String> registrationTokens = List.of("token1, token2");

        // 등록 token들에 해당하는 기기들을 topic에서 등록 해제한다.
        TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
            registrationTokens, TOPIC);

        System.out.println(response.getSuccessCount() + " tokens were unsubscribed successfully");
    }

    @Test
    @DisplayName("FCM topic 메시지 전송")
    void sendMessageForTopic() throws FirebaseMessagingException {
        Message message = Message.builder()
            .putData("key", "value")
            .setTopic(TOPIC)
            .build();

        // 제공된 topic을 구독중인 기기들에게 메시지 전송
        String response = FirebaseMessaging.getInstance().send(message);

        // 응답 결과는 메시지 ID
        System.out.println("Successfully sent message: " + response);
     }
}
