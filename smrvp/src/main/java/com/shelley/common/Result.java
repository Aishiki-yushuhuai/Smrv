package com.shelley.common;

import lombok.Data;

@Data
public class Result {
    private Boolean success;
    private String message;
    private Object data;

    public static Result success(String message) {
        Result result = new Result();
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }

    public static Result success(String message, Object data) {
        Result result = new Result();
        result.setSuccess(true);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
