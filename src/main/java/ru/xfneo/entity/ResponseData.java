package ru.xfneo.entity;

import java.time.LocalDateTime;

public record ResponseData(RequestData requestData, LocalDateTime lastUpdate, String responseBody, String contentType) implements Updatable{

}
