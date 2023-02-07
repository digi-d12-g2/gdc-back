package com.exception;

public class ResourceNotFoundException extends RuntimeException {

    private Integer status = 400;

    public ResourceNotFoundException() {
        super("Ressource not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public Integer getStatus() {
        return status;
    }
}
