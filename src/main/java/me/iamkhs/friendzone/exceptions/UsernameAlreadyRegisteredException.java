package me.iamkhs.friendzone.exceptions;

public class UsernameAlreadyRegisteredException extends RuntimeException{
    public UsernameAlreadyRegisteredException(String message){
        super(message);
    }
}
