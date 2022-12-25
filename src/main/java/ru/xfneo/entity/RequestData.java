package ru.xfneo.entity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public record RequestData(String uri, String method, String contentType, String requestBody) {
    public String getDigest() {
        MessageDigest digester;
        try {
            digester = MessageDigest.getInstance("sha1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        digester.update(uri.getBytes());
        digester.update(method.getBytes());
        return HexFormat.of().formatHex(digester.digest());
    }
}
