package me.iamkhs.friendzone.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String message){
        super(message);
    }
}
