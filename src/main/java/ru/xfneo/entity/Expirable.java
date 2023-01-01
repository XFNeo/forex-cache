package ru.xfneo.entity;

import java.time.LocalDateTime;

public interface Expirable {

    LocalDateTime expirationDate();
}
