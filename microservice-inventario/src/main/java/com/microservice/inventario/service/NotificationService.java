package com.microservice.inventario.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    @Value("${fcm-messaging.secret-key}")
    private String secretKey;

    @Value("${fcm-messaging.firebase-url}")
    private String firebaseUrl;

    public void sendNotificationToTopic(String topic, String title, String messageBody) {
        RestTemplate restTemplate = new RestTemplate();

        String notificationPayload = "{"
                + "\"to\":\"/topics/" + topic + "\","
                + "\"notification\":{"
                + "\"title\":\"" + title + "\","
                + "\"body\":\"" + messageBody + "\""
                + "}"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "key=" + secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(notificationPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(firebaseUrl, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Notificación enviada exitosamente.");
        } else {
            System.out.println("Error al enviar la notificación.");
        }
    }
}
