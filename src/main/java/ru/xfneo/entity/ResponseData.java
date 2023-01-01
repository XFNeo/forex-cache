package ru.xfneo.entity;

import java.time.LocalDateTime;

public record ResponseData(RequestData requestData, LocalDateTime expirationDate, String responseBody, String contentType) implements Expirable {

}
