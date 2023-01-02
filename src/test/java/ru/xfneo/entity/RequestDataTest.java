package ru.xfneo.entity;

import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.*;

class RequestDataTest {

    @Test
    void getDigest() {
        var requestData1 = new RequestData("/test/123", "", MediaType.TEXT_PLAIN, "body");
        var requestData2 = new RequestData("/test/123", "", MediaType.TEXT_PLAIN, "body");
        assertEquals(requestData1.getDigest(), requestData2.getDigest());
    }
}