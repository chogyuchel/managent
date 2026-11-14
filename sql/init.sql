-- ============================================================
-- 智能学习辅助系统 数据库初始化脚本
-- 适用：MySQL 8.x
-- 说明：包含建表语句与演示数据，执行后会重建全部业务表
-- 用法：mysql -uroot -p < sql/init.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE management;

-- 按依赖关系逆序删除，方便重复执行
DROP TABLE IF EXISTS operate_log;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS clazz;
DROP TABLE IF EXISTS emp_expr;
DROP TABLE IF EXISTS emp;
DROP TABLE IF EXISTS dept;

-- ------------------------------------------------------------
-- 部门表
-- ------------------------------------------------------------
CREATE TABLE dept (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(20)  NOT NULL COMMENT '部门名称',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dept_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '部门表';

-- ------------------------------------------------------------
-- 员工表
-- ------------------------------------------------------------
CREATE TABLE emp (
    id          INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(20)  NOT NULL COMMENT '用户名',
    password    VARCHAR(64)  DEFAULT NULL COMMENT '密码',
    name        VARCHAR(20)  NOT NULL COMMENT '姓名',
    gender      TINYINT      DEFAULT NULL COMMENT '性别 1:男 2:女',
    phone       VARCHAR(11)  DEFAULT NULL COMMENT '手机号',
    job         TINYINT      DEFAULT NULL COMMENT '职位 1:班主任 2:讲师 3:学工主管 4:教研主管 5:咨询师',
    salary      INT          DEFAULT NULL COMMENT '薪资',
    image       VARCHAR(300) DEFAULT NULL COMMENT '头像地址',
    entry_date  DATE         DEFAULT NULL COMMENT '入职日期',
    dept_id     INT          DEFAULT NULL COMMENT '部门ID',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_emp_username (username),
    KEY idx_emp_dept (dept_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '员工表';

-- ------------------------------------------------------------
-- 员工工作经历表（begin / end 为 MySQL 关键字，需用反引号）
-- ------------------------------------------------------------
CREATE TABLE emp_expr (
    id      INT         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    emp_id  INT         NOT NULL COMMENT '员工ID',
    `begin` DATE        DEFAULT NULL COMMENT '开始时间',
    `end`   DATE        DEFAULT NULL COMMENT '结束时间',
    company VARCHAR(50) DEFAULT NULL COMMENT '公司名称',
    job     VARCHAR(20) DEFAULT NULL COMMENT '职位',
    PRIMARY KEY (id),
    KEY idx_expr_emp (emp_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '员工工作经历表';

-- ------------------------------------------------------------
-- 班级表
-- ------------------------------------------------------------
CREATE TABLE clazz (
    id          INT         NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(30) NOT NULL COMMENT '班级名称',
    room        VARCHAR(20) DEFAULT NULL COMMENT '班级教室',
    begin_date  DATE        DEFAULT NULL COMMENT '开课时间',
    end_date    DATE        DEFAULT NULL COMMENT '结课时间',
    master_id   INT         DEFAULT NULL COMMENT '班主任ID',
    subject     TINYINT     DEFAULT NULL COMMENT '学科 1:Java 2:前端 3:Python 4:大数据',
    create_time DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME    DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_clazz_master (master_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '班级表';

-- ------------------------------------------------------------
-- 学员表
-- ------------------------------------------------------------
CREATE TABLE student (
    id               INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name             VARCHAR(20)  NOT NULL COMMENT '姓名',
    no               VARCHAR(10)  DEFAULT NULL COMMENT '学号',
    gender           TINYINT      DEFAULT NULL COMMENT '性别 1:男 2:女',
    phone            VARCHAR(11)  DEFAULT NULL COMMENT '手机号',
    id_card          VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
    is_college       TINYINT      DEFAULT NULL COMMENT '是否来自院校 1:是 0:否',
    address          VARCHAR(100) DEFAULT NULL COMMENT '联系地址',
    degree           TINYINT      DEFAULT NULL COMMENT '最高学历 1:初中 2:高中 3:大专 4:本科 5:硕士 6:博士',
    graduation_date  DATE         DEFAULT NULL COMMENT '毕业时间',
    clazz_id         INT          DEFAULT NULL COMMENT '班级ID',
    violation_count  SMALLINT     NOT NULL DEFAULT 0 COMMENT '违纪次数',
    violation_score  SMALLINT     NOT NULL DEFAULT 0 COMMENT '违纪扣分',
    create_time      DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time      DATETIME     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_student_clazz (clazz_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '学员表';

-- ------------------------------------------------------------
-- 操作日志表
-- ------------------------------------------------------------
CREATE TABLE operate_log (
    id             INT          NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    operate_emp_id INT          DEFAULT NULL COMMENT '操作人ID',
    operate_time   DATETIME     DEFAULT NULL COMMENT '操作时间',
    class_name     VARCHAR(100) DEFAULT NULL COMMENT '操作类名',
    method_name    VARCHAR(100) DEFAULT NULL COMMENT '操作方法名',
    method_params  VARCHAR(2000) DEFAULT NULL COMMENT '操作方法参数',
    return_value   VARCHAR(2000) DEFAULT NULL COMMENT '操作方法返回值',
    cost_time      BIGINT       DEFAULT NULL COMMENT '操作耗时(毫秒)',
    PRIMARY KEY (id),
    KEY idx_log_emp (operate_emp_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '操作日志表';

-- ============================================================
-- 演示数据
-- ============================================================

-- 部门
INSERT INTO dept (id, name, create_time, update_time) VALUES
(1, '教研部', '2026-01-05 09:00:00', '2026-01-05 09:00:00'),
(2, '咨询部', '2026-01-05 09:10:00', '2026-01-05 09:10:00'),
(3, '行政部', '2026-01-05 09:20:00', '2026-01-05 09:20:00'),
(4, '学工部', '2026-01-05 09:30:00', '2026-01-05 09:30:00'),
(5, '市场部', '2026-01-05 09:40:00', '2026-01-05 09:40:00');

-- 员工（登录账号：admin / 123456）
INSERT INTO emp (id, username, password, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time) VALUES
(1, 'admin', '123456', '管理员',   1, '13800000000', 1, 12000, NULL, '2023-01-01', 3, '2026-01-06 10:00:00', '2026-01-06 10:00:00'),
(2, 'zhangming', '123456', '张明', 1, '13800000001', 2, 15000, NULL, '2023-03-15', 1, '2026-01-06 10:05:00', '2026-01-06 10:05:00'),
(3, 'lijing',  '123456', '李静',   2, '13800000002', 1,  9000, NULL, '2023-06-01', 4, '2026-01-06 10:10:00', '2026-01-06 10:10:00'),
(4, 'wangqiang','123456','王强',   1, '13800000003', 3, 11000, NULL, '2024-02-20', 4, '2026-01-06 10:15:00', '2026-01-06 10:15:00'),
(5, 'zhaomin', '123456', '赵敏',   2, '13800000004', 5,  8000, NULL, '2024-05-10', 2, '2026-01-06 10:20:00', '2026-01-06 10:20:00'),
(6, 'chenlei', '123456', '陈磊',   1, '13800000005', 4, 18000, NULL, '2022-09-01', 1, '2026-01-06 10:25:00', '2026-01-06 10:25:00'),
(7, 'liuyang', '123456', '刘洋',   1, '13800000006', 2, 14000, NULL, '2025-03-01', 1, '2026-01-06 10:30:00', '2026-01-06 10:30:00'),
(8, 'sunyue',  '123456', '孙悦',   2, '13800000007', 1,  9500, NULL, '2025-07-01', 4, '2026-01-06 10:35:00', '2026-01-06 10:35:00'),
(9, 'zhouhang','123456', '周航',   1, '13800000008', 5,  8500, NULL, '2026-02-10', 5, '2026-01-06 10:40:00', '2026-01-06 10:40:00'),
(10,'wuxin',   '123456', '吴欣',   2, '13800000009', 3, 10000, NULL, '2026-04-01', 3, '2026-01-06 10:45:00', '2026-01-06 10:45:00');

-- 员工工作经历
INSERT INTO emp_expr (emp_id, `begin`, `end`, company, job) VALUES
(2, '2020-07-01', '2023-02-28', '北京云启科技有限公司', 'Java 开发工程师'),
(2, '2018-05-01', '2020-06-30', '天津数联信息技术有限公司', '初级 Java 开发'),
(4, '2021-03-01', '2024-01-31', '上海博思教育科技有限公司', '教务专员'),
(6, '2018-06-01', '2022-08-31', '杭州睿行网络科技有限公司', '技术负责人'),
(7, '2022-07-01', '2025-01-31', '深圳前海智学科技有限公司', '培训讲师');

-- 班级
INSERT INTO clazz (id, name, room, begin_date, end_date, master_id, subject, create_time, update_time) VALUES
(1, 'JavaEE就业班108期', '301教室', '2026-09-01', '2027-03-01', 3, 1, '2026-01-10 09:00:00', '2026-01-10 09:00:00'),
(2, '前端就业班56期',    '302教室', '2026-11-01', '2027-05-01', 8, 2, '2026-01-10 09:10:00', '2026-01-10 09:10:00'),
(3, 'Python就业班32期',  '303教室', '2026-03-01', '2026-09-01', 3, 3, '2026-01-10 09:20:00', '2026-01-10 09:20:00'),
(4, '大数据就业班12期',  '304教室', '2027-02-01', '2027-08-01', 8, 4, '2026-01-10 09:30:00', '2026-01-10 09:30:00');

-- 学员
INSERT INTO student (name, no, gender, phone, id_card, is_college, address, degree, graduation_date, clazz_id, violation_count, violation_score, create_time, update_time) VALUES
('李明', '2026001', 1, '13900000001', '110101200001011234', 1, '北京市海淀区中关村大街1号', 4, '2022-06-30', 1, 0, 0, '2026-02-01 09:00:00', '2026-02-01 09:00:00'),
('王芳', '2026002', 2, '13900000002', '110101200105122345', 1, '北京市朝阳区建国路88号', 4, '2023-06-30', 1, 1, 2, '2026-02-01 09:05:00', '2026-03-12 14:20:00'),
('张伟', '2026003', 1, '13900000003', '110101199912203456', 0, '北京市西城区金融街5号', 3, '2021-06-30', 1, 0, 0, '2026-02-01 09:10:00', '2026-02-01 09:10:00'),
('刘娜', '2026004', 2, '13900000004', '110101200203014567', 1, '北京市丰台区南三环西路12号', 5, '2024-06-30', 1, 0, 0, '2026-02-01 09:15:00', '2026-02-01 09:15:00'),
('陈杰', '2026005', 1, '13900000005', '110101200107085678', 0, '河北省石家庄市长安区中山路66号', 2, '2020-06-30', 1, 2, 5, '2026-02-01 09:20:00', '2026-04-08 10:30:00'),
('杨静', '2026006', 2, '13900000006', '110101200211116789', 1, '北京市通州区新华大街8号', 4, '2025-06-30', 2, 0, 0, '2026-03-01 09:00:00', '2026-03-01 09:00:00'),
('赵磊', '2026007', 1, '13900000007', '110101200004237890', 0, '山东省济南市历下区经十路100号', 3, '2022-06-30', 2, 0, 0, '2026-03-01 09:05:00', '2026-03-01 09:05:00'),
('黄敏', '2026008', 2, '13900000008', '110101200108158901', 1, '北京市昌平区回龙观东大街2号', 5, '2023-06-30', 2, 1, 3, '2026-03-01 09:10:00', '2026-05-20 16:40:00'),
('周涛', '2026009', 1, '13900000009', '110101199909269012', 0, '河南省郑州市金水区花园路35号', 2, '2019-06-30', 2, 0, 0, '2026-03-01 09:15:00', '2026-03-01 09:15:00'),
('吴倩', '2026010', 2, '13900000010', '110101200112300123', 1, '北京市大兴区黄村西大街15号', 4, '2024-06-30', 3, 0, 0, '2026-03-10 09:00:00', '2026-03-10 09:00:00'),
('徐鹏', '2026011', 1, '13900000011', '110101200006042234', 0, '江苏省南京市鼓楼区中山北路20号', 3, '2021-06-30', 3, 1, 1, '2026-03-10 09:05:00', '2026-06-02 11:15:00'),
('孙丽', '2026012', 2, '13900000012', '110101200209173345', 1, '北京市海淀区上地信息路6号', 6, '2024-06-30', 3, 0, 0, '2026-03-10 09:10:00', '2026-03-10 09:10:00'),
('马超', '2026013', 1, '13900000013', '110101200105224456', 0, '陕西省西安市雁塔区科技路18号', 2, '2020-06-30', 3, 2, 4, '2026-03-10 09:15:00', '2026-07-19 09:50:00'),
('朱琳', '2026014', 2, '13900000014', '110101200310305567', 1, '北京市东城区东直门南大街9号', 4, '2025-06-30', NULL, 0, 0, '2026-04-01 09:00:00', '2026-04-01 09:00:00'),
('胡斌', '2026015', 1, '13900000015', '110101200011126678', 0, '辽宁省大连市中山区人民路22号', 3, '2022-06-30', NULL, 0, 0, '2026-04-01 09:05:00', '2026-04-01 09:05:00');

-- 操作日志示例
INSERT INTO operate_log (operate_emp_id, operate_time, class_name, method_name, method_params, return_value, cost_time) VALUES
(1, '2026-02-01 09:30:12', 'com.example.controller.DeptController', 'save',  'Dept(name=市场部, createTime=null, updateTime=null)', 'Result(code=1, msg=success, data=null)', 25),
(1, '2026-02-01 09:32:45', 'com.example.controller.EmpController',  'save',  'Emp(id=null, name=周航, ...)', 'Result(code=1, msg=success, data=null)', 48),
(2, '2026-03-12 14:20:03', 'com.example.controller.StudentController', 'violation', 'id=2, score=2', 'Result(code=1, msg=success, data=null)', 17),
(1, '2026-05-20 16:40:22', 'com.example.controller.ClazzController', 'delete', 'id=9', 'Result(code=1, msg=success, data=null)', 31);

-- 重置自增起始值，避免演示数据 ID 冲突
ALTER TABLE emp AUTO_INCREMENT = 11;
ALTER TABLE student AUTO_INCREMENT = 16;