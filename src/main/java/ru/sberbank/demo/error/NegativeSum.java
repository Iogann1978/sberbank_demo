package ru.sberbank.demo.error;

public class NegativeSum extends Exception {
    public NegativeSum() {
        super("Sum is negative or zero!");
    }
}
