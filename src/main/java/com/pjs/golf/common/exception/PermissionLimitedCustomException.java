package com.pjs.golf.common.exception;

public class PermissionLimitedCustomException extends RuntimeException{
    public PermissionLimitedCustomException(String message) {
        super(message);
    }
}
