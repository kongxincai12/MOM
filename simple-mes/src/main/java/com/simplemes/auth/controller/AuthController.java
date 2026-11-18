package com.simplemes.auth.controller;

import com.simplemes.auth.dto.LoginRequest;
import com.simplemes.auth.dto.LoginResponse;
import com.simplemes.common.BusinessException;
import com.simplemes.common.Result;
import com.simplemes.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口：登录换取 JWT。
 * 简易演示用内置账号，实际项目应替换为真实用户表 / 密码校验。
 */
@Tag(name = "认证", description = "登录换取 JWT Token")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** 内置账号：username -> [password, employeeId, role] */
    private static final Map<String, String[]> USERS = new HashMap<>();

    static {
        USERS.put("admin", new String[]{"admin123", "10001", "ADMIN"});
        USERS.put("worker", new String[]{"worker123", "10002", "OPERATOR"});
    }

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "登录", description = "返回 Bearer Token，后续请求需在 Authorization 头携带")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("用户名和密码不能为空");
        }
        String[] account = USERS.get(request.getUsername());
        if (account == null || !account[0].equals(request.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String employeeId = account[1];
        String role = account[2];
        String token = jwtUtils.generateToken(employeeId, role);
        return Result.success(new LoginResponse(token, "Bearer", employeeId, role));
    }
}
