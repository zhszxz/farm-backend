package com.farm.security;

import com.farm.common.ErrorCode;
import com.farm.common.Result;
import com.farm.entity.SysUser;
import com.farm.mapper.SysUserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final SysUserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return writeUnauthorized(response);
        }
        try {
            Claims claims = jwtUtil.parse(auth.substring(7));
            Long userId = Long.valueOf(claims.getSubject());
            // 以数据库中的最新角色/状态为准，避免「登录后被审核通过」等场景 token 角色过期
            SysUser user = userMapper.selectById(userId);
            if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
                return writeUnauthorized(response);
            }
            UserContext.set(new LoginUser(userId, user.getRole()));
            return true;
        } catch (Exception e) {
            return writeUnauthorized(response);
        }
    }

    private boolean writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        Result.fail(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage())));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
