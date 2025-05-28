package com.manager.truck.truck.constant;

public class ResponseConstant {
    public static class StatusCodeResponse{
        public static final String SUCCESS_CODE = "200";
        public static final String SUCCESS_MSG = "SUCCESS";
        private StatusCodeResponse(){throw new IllegalStateException("Field !!!");}
    }
    public static class ErrorCodeResponse{
        public static final String ERROR_CODE = "400";
        public static final String ERROR_MSG = "ERROR";
        private ErrorCodeResponse(){throw new IllegalStateException("Field !!!");}
    }
}
