package com.tianzhili.www.myselfsdk.okgo.exception;

/**
 * Created by turbo on 2017/2/20.
 */

//token存在异常，非过期失效

public class TokenInvalideException extends RuntimeException {

    public TokenInvalideException(String message) {
        super(message);
    }

}
