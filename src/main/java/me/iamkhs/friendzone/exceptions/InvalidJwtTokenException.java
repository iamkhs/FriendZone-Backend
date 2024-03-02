package me.iamkhs.friendzone.exceptions;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException(String s) {
        super(s);
    }
}
