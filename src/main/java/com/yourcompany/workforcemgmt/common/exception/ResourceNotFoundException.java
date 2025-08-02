package com.yourcompany.workforcemgmt.common.exception;

public class ResourceNotFoundException extends RuntimeException {
   public ResourceNotFoundException(String message) {
       super(message);
   }
}