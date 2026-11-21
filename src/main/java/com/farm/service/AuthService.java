package com.farm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farm.common.BusinessException;
import com.farm.common.ErrorCode;
import com.farm.dto.LoginResponse;
import com.farm.entity.SysUser;
import com.farm.mapper.SysUserMapper;
import com.farm.security.JwtUtil;
import com.farm.security.LoginUser;
import com.farm.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /** 内存验证码存储（TODO: 上线前替换为 Redis + 短信服务） */
    private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();

    public void sendSmsCode(String phone) {
        String code = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        codeStore.put(phone, new CodeEntry(code, System.currentTimeMillis() + 5 * 60 * 1000L));
        // 开发阶段：未接入短信网关，验证码打印到日志
        log.info("[开发模式] 手机号 {} 的验证码: {}", phone, code);
    }

    public LoginResponse register(String phone, String password, String nickname) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.PHONE_EXISTS);
        }
        SysUser user = new SysUser();
        user.setPhone(phone);
        user.setPassword(encoder.encode(password));
        String nick = (nickname == null || nickname.isBlank())
                ? "用户" + phone.substring(phone.length() - 4)
                : nickname;
        user.setNickname(nick);
        user.setRole(1);
        user.setStatus(1);
        userMapper.insert(user);
        return buildResponse(user);
    }

    public LoginResponse loginByPassword(String account, String password) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .and(w -> w.eq(SysUser::getPhone, account).or().eq(SysUser::getUsername, account)));
        if (user == null || !encoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        checkStatus(user);
        return buildResponse(user);
    }

    public LoginResponse loginBySms(String phone, String smsCode) {
        checkCode(phone, smsCode);
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_ERROR);
        }
        checkStatus(user);
        return buildResponse(user);
    }

    public SysUser currentUser() {
        LoginUser lu = UserContext.get();
        if (lu == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        SysUser user = userMapper.selectById(lu.getUserId());
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    private void checkStatus(SysUser user) {
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "账号已被禁用");
        }
    }

    private void checkCode(String phone, String smsCode) {
        CodeEntry entry = codeStore.get(phone);
        if (entry == null || entry.expireAt < System.currentTimeMillis() || !entry.code.equals(smsCode)) {
            throw new BusinessException(ErrorCode.SMS_CODE_ERROR);
        }
        codeStore.remove(phone);
    }

    private LoginResponse buildResponse(SysUser user) {
        LoginResponse resp = new LoginResponse();
        resp.setToken(jwtUtil.createToken(user.getId(), user.getRole()));
        resp.setUserId(user.getId());
        resp.setNickname(user.getNickname());
        resp.setPhone(user.getPhone());
        resp.setRole(user.getRole());
        return resp;
    }

    private static class CodeEntry {
        final String code;
        final long expireAt;

        CodeEntry(String code, long expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }
}
