package com.farm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表实体
 */
@Data
@TableName("sys_user")
public class SysUser {

    /** 用户ID（主键，自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号（可空，仅密码登录时使用） */
    private String username;

    /** 手机号 */
    private String phone;

    /** 密码（BCrypt 加密存储） */
    private String password;

    /** 角色：1-普通用户 2-商家 3-管理员 */
    private Integer role;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 状态：1-正常 0-禁用 */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-否 1-是 */
    private Integer deleted;
}
