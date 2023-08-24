package com.heyticket.backend.service.dto;

public interface PushInfo {

    String getTitle();

    String getBody();

    String getId();

    String getType();

    enum PushInfoType {
        PERFORMANCE
    }
}
