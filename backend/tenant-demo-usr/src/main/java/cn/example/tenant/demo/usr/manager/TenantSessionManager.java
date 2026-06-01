package cn.example.tenant.demo.usr.manager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * 租户 Session 管理：Redis 存会话，sessionId 格式 {@code T{tenantCode}_{uuid}}。
 */
@Slf4j
@Component
public class TenantSessionManager {

    private static final String SESSION_KEY_PREFIX = "tenant-demo:session:tenant:";
    private static final String SESSION_ID_PREFIX = "T%s_";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${session.timeout.seconds:7200}")
    private long defaultSessionTimeoutSeconds;

    public TenantSessionManager(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public UserSessionInfo createSession(String tenantCode, Long userId, String username,
                                         String nickname, String role) {
        String sessionId = buildSessionId(tenantCode);
        long now = System.currentTimeMillis();
        long expiresAt = now + defaultSessionTimeoutSeconds * 1000L;

        SessionData data = new SessionData();
        data.tenantCode = tenantCode;
        data.userId = userId;
        data.username = username;
        data.nickname = nickname != null ? nickname : username;
        data.role = role;
        data.createdAt = now;
        data.expiresAt = expiresAt;

        String key = buildRedisKey(tenantCode, sessionId);
        redisTemplate.opsForValue().set(key, toJson(data), Duration.ofSeconds(defaultSessionTimeoutSeconds));

        log.info("创建会话: tenantCode={}, sessionId={}, userId={}", tenantCode, sessionId, userId);

        UserSessionInfo info = new UserSessionInfo();
        info.setSessionId(sessionId);
        info.setTenantCode(tenantCode);
        info.setUserId(userId);
        info.setUsername(username);
        info.setNickname(data.nickname);
        info.setRole(role);
        info.setExpiresAt(expiresAt);
        return info;
    }

    public UserSessionInfo validateSession(String tenantCode, String sessionId) {
        if (tenantCode == null || sessionId == null) {
            return null;
        }

        String json = redisTemplate.opsForValue().get(buildRedisKey(tenantCode, sessionId));
        if (json == null) {
            return null;
        }

        SessionData data = fromJson(json);
        if (data == null || !tenantCode.equals(data.tenantCode)) {
            return null;
        }
        if (System.currentTimeMillis() > data.expiresAt) {
            deleteSession(tenantCode, sessionId);
            return null;
        }

        UserSessionInfo info = new UserSessionInfo();
        info.setSessionId(sessionId);
        info.setTenantCode(tenantCode);
        info.setUserId(data.userId);
        info.setUsername(data.username);
        info.setNickname(data.nickname);
        info.setRole(data.role);
        info.setExpiresAt(data.expiresAt);
        return info;
    }

    public void deleteSession(String tenantCode, String sessionId) {
        redisTemplate.delete(buildRedisKey(tenantCode, sessionId));
        log.info("删除会话: tenantCode={}, sessionId={}", tenantCode, sessionId);
    }

    private String buildSessionId(String tenantCode) {
        return String.format(SESSION_ID_PREFIX, tenantCode)
                + UUID.randomUUID().toString().replace("-", "");
    }

    private String buildRedisKey(String tenantCode, String sessionId) {
        return SESSION_KEY_PREFIX + tenantCode + ":sessions:" + sessionId;
    }

    private String toJson(SessionData data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化会话失败", e);
        }
    }

    private SessionData fromJson(String json) {
        try {
            return objectMapper.readValue(json, SessionData.class);
        } catch (JsonProcessingException e) {
            log.warn("反序列化会话失败: {}", e.getMessage());
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SessionData {
        public String tenantCode;
        public Long userId;
        public String username;
        public String nickname;
        public String role;
        public long createdAt;
        public long expiresAt;
    }
}
