package com.simplemes.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 拦截器：校验 Authorization: Bearer &lt;token&gt;，
 * 解析工号并写入 request 属性 current_user（含 employeeId、role）。
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    public static final String ATTR_CURRENT_USER = "current_user";

    @Autowired
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        final String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            writeError(response, 401, "认证失败：缺少或无效的Token");
            return false;
        }

        final String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtils.extractAllClaims(token);
            String employeeId = claims.getSubject();
            if (!StringUtils.hasText(employeeId)) {
                writeError(response, 401, "认证失败：Token中缺少用户信息");
                return false;
            }
            Map<String, String> user = new HashMap<>();
            user.put("employeeId", employeeId);
            user.put("role", claims.get("role", String.class));
            request.setAttribute(ATTR_CURRENT_USER, user);
        } catch (ExpiredJwtException e) {
            writeError(response, 401, "认证失败：Token已过期");
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            writeError(response, 401, "认证失败：无效的Token");
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
