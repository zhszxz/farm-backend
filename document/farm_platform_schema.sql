-- ============================================================
-- 农副产品展示与销售管理平台 · 数据库初始化脚本
-- MySQL 8.0+ / InnoDB / utf8mb4
-- 对应文档：数据库设计.md v1.0
-- ============================================================

CREATE DATABASE IF NOT EXISTS farm_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE farm_platform;
SET NAMES utf8mb4;

-- ------------------------------------------------------------
-- 1. 用户表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username        VARCHAR(32)   DEFAULT NULL COMMENT '登录账号(可空)',
  phone           VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
  password        VARCHAR(100)  DEFAULT NULL COMMENT '密码(BCrypt加密)',
  role            TINYINT       NOT NULL DEFAULT 1 COMMENT '角色:1-普通用户,2-商家,3-管理员',
  nickname        VARCHAR(50)   DEFAULT NULL COMMENT '昵称',
  avatar          VARCHAR(255)  DEFAULT NULL COMMENT '头像URL',
  status          TINYINT       NOT NULL DEFAULT 1 COMMENT '状态:1-正常,0-禁用',
  last_login_time DATETIME      DEFAULT NULL COMMENT '最后登录时间',
  create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted         TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username ((CASE WHEN deleted = 0 THEN username ELSE NULL END)),
  UNIQUE KEY uk_phone ((CASE WHEN deleted = 0 THEN phone ELSE NULL END))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ------------------------------------------------------------
-- 2. 店铺表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shop (
  id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
  user_id       BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
  shop_name     VARCHAR(100) NOT NULL COMMENT '店铺名称',
  region        VARCHAR(100) DEFAULT NULL COMMENT '所在地区(省市区县乡镇)',
  address       VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
  contact_name  VARCHAR(50)  DEFAULT NULL COMMENT '联系人',
  contact_phone VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  wechat        VARCHAR(50)  DEFAULT NULL COMMENT '微信号',
  description   TEXT         COMMENT '店铺简介',
  license_image VARCHAR(255) DEFAULT NULL COMMENT '资质证明图片URL(可选)',
  status        TINYINT      NOT NULL DEFAULT 0 COMMENT '审核状态:0-待审核,1-通过,2-驳回',
  reject_reason VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_id (user_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺表';

-- ------------------------------------------------------------
-- 3. 产品分类表（大类/小类）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS category (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  name        VARCHAR(50)  NOT NULL COMMENT '分类名称',
  parent_id   BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级ID:0-大类',
  level       TINYINT      NOT NULL DEFAULT 1 COMMENT '层级:1-大类,2-小类',
  sort        INT          NOT NULL DEFAULT 0 COMMENT '排序(升序)',
  status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态:1-启用,0-停用',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类表(大类/小类)';

-- ------------------------------------------------------------
-- 4. 产品表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  shop_id      BIGINT UNSIGNED NOT NULL COMMENT '所属店铺ID',
  category_id  BIGINT UNSIGNED NOT NULL COMMENT '品种(小类)ID',
  name         VARCHAR(100) NOT NULL COMMENT '产品名称(展示用)',
  unit         VARCHAR(20)  DEFAULT NULL COMMENT '计量单位',
  images       JSON         COMMENT '图片URL列表',
  description  TEXT         COMMENT '产品介绍',
  origin       VARCHAR(100) DEFAULT NULL COMMENT '产地',
  season       VARCHAR(50)  DEFAULT NULL COMMENT '上市季节',
  status       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0-草稿,1-已上架,2-已下架',
  is_recommend TINYINT      NOT NULL DEFAULT 0 COMMENT '是否推荐:0-否,1-是',
  price        DECIMAL(14,2) DEFAULT NULL COMMENT '价格(商城预留)',
  stock        INT           DEFAULT NULL COMMENT '库存(商城预留)',
  sku          VARCHAR(100)  DEFAULT NULL COMMENT '规格/SKU(商城预留)',
  create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  KEY idx_shop_cat (shop_id, category_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- ------------------------------------------------------------
-- 5. 拼多多链接表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS shop_link (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '链接ID',
  shop_id     BIGINT UNSIGNED NOT NULL COMMENT '店铺ID',
  product_id  BIGINT UNSIGNED DEFAULT NULL COMMENT '关联产品ID(空=店铺级链接)',
  platform    VARCHAR(20)   NOT NULL DEFAULT 'pdd' COMMENT '平台:pdd-拼多多',
  link_name   VARCHAR(100)  DEFAULT NULL COMMENT '链接名称',
  link_url    VARCHAR(500)  NOT NULL COMMENT '跳转链接',
  status      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态:1-启用,0-停用',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  KEY idx_shop (shop_id),
  KEY idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方店铺链接表';

-- ------------------------------------------------------------
-- 6. 销售渠道字典表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS channel (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '渠道ID',
  name        VARCHAR(50) NOT NULL COMMENT '渠道名称',
  sort        INT         NOT NULL DEFAULT 0 COMMENT '排序',
  status      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态:1-启用,0-停用',
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售渠道字典表';

-- ------------------------------------------------------------
-- 7. 成本类型字典表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cost_type (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  name        VARCHAR(50) NOT NULL COMMENT '成本类型',
  sort        INT         NOT NULL DEFAULT 0 COMMENT '排序',
  status      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态:1-启用,0-停用',
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成本类型字典表';

-- ------------------------------------------------------------
-- 8. 通用字典表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_dict (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  dict_type   VARCHAR(50) NOT NULL COMMENT '字典类型:unit-单位,season-上市季节',
  dict_value  VARCHAR(50) NOT NULL COMMENT '字典值',
  dict_label  VARCHAR(50) NOT NULL COMMENT '显示名称',
  sort        INT         NOT NULL DEFAULT 0 COMMENT '排序',
  status      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态:1-启用,0-停用',
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  KEY idx_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用字典表';

-- ------------------------------------------------------------
-- 9. 销售记录表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sales_record (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  shop_id     BIGINT UNSIGNED NOT NULL COMMENT '店铺ID',
  category_id BIGINT UNSIGNED NOT NULL COMMENT '品种(小类)ID',
  year        SMALLINT      NOT NULL COMMENT '销售年份',
  channel_id  BIGINT UNSIGNED NOT NULL COMMENT '销售渠道ID',
  quantity    DECIMAL(12,2) NOT NULL COMMENT '销售数量',
  unit        VARCHAR(20)   NOT NULL COMMENT '计量单位',
  amount      DECIMAL(14,2) NOT NULL COMMENT '销售额(元)',
  unit_price  DECIMAL(14,2) DEFAULT NULL COMMENT '单价(元/单位)',
  sale_period VARCHAR(50)   DEFAULT NULL COMMENT '销售时间段',
  remark      VARCHAR(255)  DEFAULT NULL COMMENT '备注',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  KEY idx_shop_cat_year (shop_id, category_id, year),
  KEY idx_shop_channel_year (shop_id, channel_id, year),
  KEY idx_shop_year (shop_id, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售记录表';

-- ------------------------------------------------------------
-- 10. 产量记录表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS production_record (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  shop_id     BIGINT UNSIGNED NOT NULL COMMENT '店铺ID',
  category_id BIGINT UNSIGNED NOT NULL COMMENT '品种(小类)ID',
  year        SMALLINT      NOT NULL COMMENT '年份',
  area        DECIMAL(10,2) DEFAULT NULL COMMENT '种植面积(亩)',
  quantity    DECIMAL(12,2) NOT NULL COMMENT '产量',
  unit        VARCHAR(20)   NOT NULL COMMENT '计量单位',
  remark      VARCHAR(255)  DEFAULT NULL COMMENT '备注',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  KEY idx_shop_cat_year (shop_id, category_id, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产量记录表';

-- ------------------------------------------------------------
-- 11. 成本记录表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cost_record (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  shop_id      BIGINT UNSIGNED NOT NULL COMMENT '店铺ID',
  category_id  BIGINT UNSIGNED NOT NULL COMMENT '品种(小类)ID',
  year         SMALLINT      NOT NULL COMMENT '年份',
  cost_type_id BIGINT UNSIGNED NOT NULL COMMENT '成本类型ID',
  amount       DECIMAL(14,2) NOT NULL COMMENT '金额(元)',
  remark       VARCHAR(255)  DEFAULT NULL COMMENT '备注',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted      TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-否,1-是',
  PRIMARY KEY (id),
  KEY idx_shop_cat_year (shop_id, category_id, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成本记录表';

-- ------------------------------------------------------------
-- 12. 跳转统计表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS click_log (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  shop_id    BIGINT UNSIGNED NOT NULL COMMENT '店铺ID',
  product_id BIGINT UNSIGNED DEFAULT NULL COMMENT '产品ID(可空)',
  link_id    BIGINT UNSIGNED DEFAULT NULL COMMENT '链接ID',
  source     VARCHAR(50) DEFAULT NULL COMMENT '来源(可选)',
  click_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点击时间',
  PRIMARY KEY (id),
  KEY idx_shop_time (shop_id, click_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跳转统计表';

-- ------------------------------------------------------------
-- 13. 收藏关注表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS favorite (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  user_id     BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  target_type TINYINT       NOT NULL COMMENT '目标类型:1-产品,2-店铺',
  target_id   BIGINT UNSIGNED NOT NULL COMMENT '目标ID',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_target (user_id, target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏关注表(物理删除实现toggle)';

-- ------------------------------------------------------------
-- 14. 留言咨询表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS message (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  shop_id     BIGINT UNSIGNED NOT NULL COMMENT '商家(店铺)ID',
  user_id     BIGINT UNSIGNED NOT NULL COMMENT '留言用户ID',
  content     VARCHAR(1000) NOT NULL COMMENT '留言内容',
  reply       VARCHAR(1000) DEFAULT NULL COMMENT '商家回复',
  status      TINYINT       NOT NULL DEFAULT 0 COMMENT '状态:0-待回复,1-已回复',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  reply_time  DATETIME      DEFAULT NULL COMMENT '回复时间',
  PRIMARY KEY (id),
  KEY idx_shop (shop_id),
  KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言咨询表';

-- ============================================================
-- 初始化字典数据
-- ============================================================

INSERT INTO channel (name, sort) VALUES
('电商平台(拼多多)', 1),
('批发市场', 2),
('商超', 3),
('合作社', 4),
('采摘/自提', 5),
('零售', 6),
('单位采购', 7),
('直播带货', 8),
('其他', 9);

INSERT INTO cost_type (name, sort) VALUES
('化肥', 1),
('农药', 2),
('人工', 3),
('包装', 4),
('物流', 5),
('总成本(简化)', 6),
('其他', 7);

INSERT INTO sys_dict (dict_type, dict_value, dict_label, sort) VALUES
('unit', 'jin', '斤', 1),
('unit', 'kg', '公斤', 2),
('unit', 'box', '箱', 3),
('unit', 'ton', '吨', 4),
('season', 'spring', '春季', 1),
('season', 'summer', '夏季', 2),
('season', 'autumn', '秋季', 3),
('season', 'winter', '冬季', 4),
('season', 'all', '全年', 5);

INSERT INTO category (id, name, parent_id, level, sort) VALUES
(1, '苹果', 0, 1, 1),
(2, '红富士', 1, 2, 1),
(3, '秦冠', 1, 2, 2),
(4, '梨', 0, 1, 2),
(5, '鸭梨', 4, 2, 1),
(6, '果干', 0, 1, 3),
(7, '苹果干', 6, 2, 1);

-- 管理员账号（密码用 BCrypt 加密后由后端初始化，此处不直接插入明文）
