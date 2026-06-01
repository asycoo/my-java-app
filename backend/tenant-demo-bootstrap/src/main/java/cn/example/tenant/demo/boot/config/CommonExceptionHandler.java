package cn.example.tenant.demo.boot.config;

import cn.example.tenant.demo.common.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器。
 *
 * <p><b>职责</b>：把 Java 异常转成统一的 {@link Response} JSON，避免直接 500 堆栈暴露给前端。</p>
 * <p><b>与 Next/Prisma 的类比</b>：类似在 API Route 最外层 try/catch 返回固定错误结构。</p>
 * <p><b>请求生命周期</b>：Controller/Service 抛异常后，由 Spring 回调对应 {@code @ExceptionHandler}。</p>
 * <p><b>关键语法</b>：{@code @RestControllerAdvice} 全局生效；方法参数类型决定匹配哪种异常。</p>
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("参数校验异常: {}", message);
        return Response.fail("VALIDATION_FAILED", message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("业务参数非法: {}", e.getMessage());
        return Response.fail("ILLEGAL_ARGUMENT", e.getMessage());
    }

    /**
     * 接口路径不存在（常见于改了代码但未重启，仍跑旧版本 Controller）。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public Response<Void> handleNotFound(NoResourceFoundException e) {
        log.warn("接口不存在: {}", e.getResourcePath());
        return Response.fail("NOT_FOUND", "接口不存在，请确认路径或重启后端: " + e.getResourcePath());
    }

    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Response.fail("SYSTEM_ERROR", "系统异常，请稍后重试");
    }
}
