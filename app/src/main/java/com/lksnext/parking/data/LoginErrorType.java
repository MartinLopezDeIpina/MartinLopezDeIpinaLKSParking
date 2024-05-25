package com.lksnext.parking.data;

public enum LoginErrorType {
    EMPTY_EMAIL("Introduce un email"),
    EMPTY_PASSWORD("Introduce una contraseña"),
    INVALID_EMAIL("Introduce un email válido"),
    INVALID_PASSWORD("Introduce una contraseña válida"),
    USER_NOT_FOUND("Usuario no encontrado"),
    WRONG_PASSWORD("Contraseña incorrecta"),
    UNKNOWN_ERROR("Error al iniciar sesión");

    private String message;

    LoginErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
