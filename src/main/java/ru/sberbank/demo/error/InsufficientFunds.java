package ru.sberbank.demo.error;

public class InsufficientFunds extends Exception {
    public InsufficientFunds() {
        super("Insufficient funds in the account!");
    }
}
