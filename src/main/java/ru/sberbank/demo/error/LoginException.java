package ru.sberbank.demo.error;

public class LoginException extends Exception {
    public LoginException() {
        super("Login failed!");
    }
}
