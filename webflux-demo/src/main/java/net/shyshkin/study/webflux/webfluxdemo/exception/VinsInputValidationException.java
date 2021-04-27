package net.shyshkin.study.webflux.webfluxdemo.exception;

public class VinsInputValidationException extends RuntimeException {

    private static final String MSG = "allowed range from 10 to 20";
    private static final int errorCode = 100;

    private final int input;

    public VinsInputValidationException(int input) {
        super(MSG);
        this.input = input;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getInput() {
        return input;
    }
}
