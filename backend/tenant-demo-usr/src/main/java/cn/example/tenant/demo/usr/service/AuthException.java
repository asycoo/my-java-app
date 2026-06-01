package cn.example.tenant.demo.usr.service;

/**
 * 认证业务异常，由 {@code LoginController} 转为 {@code Response.fail}。
 */
public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }
}
