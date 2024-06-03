package com.lksnext.parking.data;

public enum RegisterErrorType{
    EMAIL_ALREADY_REGISTERED("El email ya está registrado"),
    UNKNOWN_ERROR("Error al registrar");

    private String message;

    RegisterErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
