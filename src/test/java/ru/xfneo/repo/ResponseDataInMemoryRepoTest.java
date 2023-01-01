package ru.xfneo.repo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.xfneo.entity.ResponseData;

import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseDataInMemoryRepoTest {

    ResponseDataInMemoryRepo responseDataInMemoryRepo;
    String key = "key1";

    @BeforeEach
    void beforeEach() {
        responseDataInMemoryRepo = new ResponseDataInMemoryRepo();
    }

    @Test
    void save() {
        var expectedResponseData = new ResponseData(null, now(), "responseBody", "Content-Type");
        var actualResponseData = responseDataInMemoryRepo.save(key, expectedResponseData);
        assertEquals(expectedResponseData, actualResponseData);
    }

    @Test
    void get() {
        var expectedResponseData = new ResponseData(null, now(), "responseBody", "Content-Type");
        responseDataInMemoryRepo.save(key, expectedResponseData);
        var expectedOptionalResponseData = Optional.of(expectedResponseData);
        final Optional<ResponseData> actualOptionalResponseData = responseDataInMemoryRepo.get(key);
        assertEquals(expectedOptionalResponseData, actualOptionalResponseData);
    }

    @Test
    void getEmpty() {
        final Optional<ResponseData> actualOptionalResponseData = responseDataInMemoryRepo.get(key);
        assertTrue(actualOptionalResponseData.isEmpty());
    }
}