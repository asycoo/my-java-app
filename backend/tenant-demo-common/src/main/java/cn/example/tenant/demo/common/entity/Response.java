package cn.example.tenant.demo.common.entity;

import lombok.Data;

/**
 * 统一 API 响应包装类。
 *
 * <p><b>职责</b>：所有 Controller 对外返回同一 JSON 结构，便于前端统一处理。</p>
 * <p><b>与 Next/Prisma 的类比</b>：类似你在 Route Handler 里固定返回
 * {@code { success, code, message, result }}，而不是直接裸返回业务对象。</p>
 * <p><b>请求生命周期</b>：Controller 最后一层组装 → 序列化为 JSON 响应体。</p>
 * <p><b>关键语法</b>：{@code <T>} 是泛型，表示 {@code result} 字段可以是任意类型；
 * {@code static} 方法属于类本身，无需 new 即可调用 {@link #ok(Object)}。</p>
 */
@Data
public class Response<T> {

    /** 是否成功，true 表示业务成功 */
    private boolean success;

    /** 业务错误码，成功时通常为 null 或空 */
    private String code;

    /** 提示信息，失败时展示给用户 */
    private String message;

    /** 业务数据，对应参考工程的 result 字段 */
    private T result;

    public static <T> Response<T> ok(T data) {
        Response<T> r = new Response<>();
        r.success = true;
        r.result = data;
        return r;
    }

    public static <T> Response<T> fail(String code, String message) {
        Response<T> r = new Response<>();
        r.success = false;
        r.code = code;
        r.message = message;
        return r;
    }

    public boolean isSuccess() {
        return success;
    }
}
