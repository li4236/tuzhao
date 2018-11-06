package com.tianzhili.www.myselfsdk.okgo.exception;

/**
 * Created by juncoder on 2018/11/6.
 * <p>
 * token过期
 * </p>
 */
public class TokenExpiredException extends Exception {

    public TokenExpiredException(String message) {
        super(message);
    }

}
