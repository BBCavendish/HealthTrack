-- HealthTrack健康追踪平台 - 数据库初始化脚本（修正版）
-- 符合Phase 2设计规范，email作为多值属性单独存储

SET FOREIGN_KEY_CHECKS = 0;

-- ==================== 表结构创建 ====================

-- 1. 家庭组表
CREATE TABLE IF NOT EXISTS family_group (
                                            family_id VARCHAR(20) PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 用户表（移除email字段）
CREATE TABLE IF NOT EXISTS app_user (
                                        health_id VARCHAR(20) PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        phone VARCHAR(15),
                                        verification_status VARCHAR(10),
                                        role VARCHAR(20),
                                        family_id VARCHAR(20),
                                        FOREIGN KEY (family_id) REFERENCES family_group(family_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 用户邮箱多值属性表（新增）
CREATE TABLE IF NOT EXISTS user_email (
                                          health_id VARCHAR(20),
                                          email_address VARCHAR(100),
                                          is_primary BOOLEAN DEFAULT FALSE,
                                          PRIMARY KEY (health_id, email_address),
                                          FOREIGN KEY (health_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 医疗提供者表（移除email字段）
CREATE TABLE IF NOT EXISTS provider (
                                        license_number VARCHAR(20) PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        specialty VARCHAR(50),
                                        verified_status VARCHAR(10),
                                        phone VARCHAR(15)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 提供者邮箱多值属性表（新增）
CREATE TABLE IF NOT EXISTS provider_email (
                                              license_number VARCHAR(20),
                                              email_address VARCHAR(100),
                                              is_primary BOOLEAN DEFAULT FALSE,
                                              PRIMARY KEY (license_number, email_address),
                                              FOREIGN KEY (license_number) REFERENCES provider(license_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 健康挑战表
CREATE TABLE IF NOT EXISTS wellness_challenge (
                                                  challenge_id VARCHAR(20) PRIMARY KEY,
                                                  goal TEXT,
                                                  start_date DATE NOT NULL,
                                                  end_date DATE NOT NULL,
                                                  description TEXT,
                                                  creator_id VARCHAR(20) NOT NULL,
                                                  FOREIGN KEY (creator_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 健康报告表
CREATE TABLE IF NOT EXISTS health_report (
                                             report_id VARCHAR(20) PRIMARY KEY,
                                             report_month DATE NOT NULL,
                                             total_steps INTEGER,
                                             summary TEXT,
                                             user_id VARCHAR(20) NOT NULL,
                                             verifier_id VARCHAR(20),
                                             FOREIGN KEY (user_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                             FOREIGN KEY (verifier_id) REFERENCES provider(license_number) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. 预约表
CREATE TABLE IF NOT EXISTS appointment (
                                           appointment_id VARCHAR(20) PRIMARY KEY,
                                           date_time DATETIME NOT NULL,
                                           type VARCHAR(10),
                                           note TEXT,
                                           status VARCHAR(15),
                                           cancel_reason TEXT,
                                           user_id VARCHAR(20) NOT NULL,
                                           report_id VARCHAR(20),
                                           FOREIGN KEY (user_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                           FOREIGN KEY (report_id) REFERENCES health_report(report_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. 邀请表
CREATE TABLE IF NOT EXISTS invitation (
                                          invitation_id VARCHAR(20) PRIMARY KEY,
                                          invitee_contact VARCHAR(100) NOT NULL,
                                          sent_time DATETIME NOT NULL,
                                          expired_time DATETIME NOT NULL,
                                          status VARCHAR(10),
                                          invitation_type VARCHAR(20),
                                          inviter_id VARCHAR(20) NOT NULL,
                                          related_challenge_id VARCHAR(20),
                                          FOREIGN KEY (inviter_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                          FOREIGN KEY (related_challenge_id) REFERENCES wellness_challenge(challenge_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. 用户-提供者关联表
CREATE TABLE IF NOT EXISTS user_provider_link (
                                                  health_id VARCHAR(20),
                                                  license_number VARCHAR(20),
                                                  is_primary BOOLEAN DEFAULT FALSE,
                                                  PRIMARY KEY (health_id, license_number),
                                                  FOREIGN KEY (health_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                                  FOREIGN KEY (license_number) REFERENCES provider(license_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. 预约-提供者关联表
CREATE TABLE IF NOT EXISTS appointment_provider (
                                                    appointment_id VARCHAR(20),
                                                    license_number VARCHAR(20),
                                                    PRIMARY KEY (appointment_id, license_number),
                                                    FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                                    FOREIGN KEY (license_number) REFERENCES provider(license_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. 参与记录表
CREATE TABLE IF NOT EXISTS participation (
                                             health_id VARCHAR(20),
                                             challenge_id VARCHAR(20),
                                             progress INTEGER,
                                             PRIMARY KEY (health_id, challenge_id),
                                             FOREIGN KEY (health_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                             FOREIGN KEY (challenge_id) REFERENCES wellness_challenge(challenge_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. 挑战-报告关联表
CREATE TABLE IF NOT EXISTS challenge_report (
                                                challenge_id VARCHAR(20),
                                                report_id VARCHAR(20),
                                                PRIMARY KEY (challenge_id, report_id),
                                                FOREIGN KEY (challenge_id) REFERENCES wellness_challenge(challenge_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                                FOREIGN KEY (report_id) REFERENCES health_report(report_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ==================== 数据插入 ====================

-- 1. 插入家庭组数据
INSERT IGNORE INTO family_group (family_id) VALUES
                                                ('fam001'), ('fam002'), ('fam003'), ('fam004'), ('fam005');

-- 2. 插入用户数据（不含email字段）
INSERT IGNORE INTO app_user (health_id, name, phone, verification_status, role, family_id) VALUES
                                                                                               ('user001', '张三', '13800138000', 'Verified', '普通用户', 'fam001'),
                                                                                               ('user002', '李四', '13900139000', 'Verified', '管理员', 'fam001'),
                                                                                               ('user003', '王五', '13600136000', 'Unverified', '普通用户', 'fam002'),
                                                                                               ('user004', '赵六', '13500135000', 'Verified', '普通用户', NULL),
                                                                                               ('user005', '钱七', '13700137000', 'Verified', '普通用户', 'fam003'),
                                                                                               ('user006', '孙八', '13400134000', 'Unverified', '普通用户', 'fam004'),
                                                                                               ('user007', '周九', '13300133000', 'Verified', '管理员', 'fam005'),
                                                                                               ('user008', '吴十', '13200132000', 'Verified', '普通用户', 'fam002'),
                                                                                               ('user009', '郑十一', '13100131000', 'Verified', '普通用户', 'fam003'),
                                                                                               ('user010', '王十二', '13000130000', 'Unverified', '普通用户', NULL);

-- 3. 插入用户邮箱数据（多值属性）
INSERT IGNORE INTO user_email (health_id, email_address, is_primary) VALUES
                                                                         ('user001', 'zhangsan@email.com', TRUE),
                                                                         ('user001', 'zhangsan_personal@email.com', FALSE),
                                                                         ('user001', 'zhangsan_work@company.com', FALSE),
                                                                         ('user002', 'lisi@email.com', TRUE),
                                                                         ('user002', 'lisi_family@email.com', FALSE),
                                                                         ('user003', 'wangwu@email.com', TRUE),
                                                                         ('user004', 'zhaoliu@email.com', TRUE),
                                                                         ('user005', 'qianqi@email.com', TRUE),
                                                                         ('user005', 'qianqi_backup@email.com', FALSE),
                                                                         ('user006', 'sunba@email.com', TRUE),
                                                                         ('user007', 'zhoujiu@email.com', TRUE),
                                                                         ('user008', 'wushi@email.com', TRUE),
                                                                         ('user009', 'zhengshiyi@email.com', TRUE),
                                                                         ('user010', 'wangshier@email.com', TRUE);

-- 4. 插入医疗提供者数据（不含email字段）
INSERT IGNORE INTO provider (license_number, name, specialty, verified_status, phone) VALUES
                                                                                          ('doc001', '王医生', '心脏病学', 'Verified', '13600136000'),
                                                                                          ('doc002', '李医生', '神经科', 'Verified', '13700137000'),
                                                                                          ('doc003', '张医生', '儿科', 'Unverified', '13800138000'),
                                                                                          ('doc004', '刘医生', '骨科', 'Verified', '13900139000'),
                                                                                          ('doc005', '陈医生', '眼科', 'Verified', '13500135000'),
                                                                                          ('doc006', '杨医生', '皮肤科', 'Verified', '13400134000'),
                                                                                          ('doc007', '黄医生', '牙科', 'Verified', '13300133000'),
                                                                                          ('doc008', '周医生', '内科', 'Unverified', '13200132000'),
                                                                                          ('doc009', '吴医生', '外科', 'Verified', '13100131000'),
                                                                                          ('doc010', '郑医生', '中医', 'Verified', '13000130000');

-- 5. 插入提供者邮箱数据（多值属性）
INSERT IGNORE INTO provider_email (license_number, email_address, is_primary) VALUES
                                                                                  ('doc001', 'wangdoctor@hospital.com', TRUE),
                                                                                  ('doc001', 'wangdoctor_personal@email.com', FALSE),
                                                                                  ('doc002', 'lidoctor@hospital.com', TRUE),
                                                                                  ('doc003', 'zhangdoctor@hospital.com', TRUE),
                                                                                  ('doc004', 'liudoctor@hospital.com', TRUE),
                                                                                  ('doc004', 'liudoctor_research@hospital.com', FALSE),
                                                                                  ('doc005', 'chendoctor@hospital.com', TRUE),
                                                                                  ('doc006', 'yangdoctor@hospital.com', TRUE),
                                                                                  ('doc007', 'huangdoctor@hospital.com', TRUE),
                                                                                  ('doc008', 'zhoudactor@hospital.com', TRUE),
                                                                                  ('doc009', 'wudoctor@hospital.com', TRUE),
                                                                                  ('doc010', 'zhengdoctor@hospital.com', TRUE);

-- 6. 插入健康挑战数据
INSERT IGNORE INTO wellness_challenge (challenge_id, goal, start_date, end_date, description, creator_id) VALUES
                                                                                                              ('chal001', '每日万步挑战', '2024-01-01', '2024-01-31', '坚持每天走10000步', 'user001'),
                                                                                                              ('chal002', '健康饮食月', '2024-02-01', '2024-02-28', '坚持健康饮食30天', 'user002'),
                                                                                                              ('chal003', '戒烟挑战', '2024-03-01', '2024-03-31', '30天不吸烟挑战', 'user001'),
                                                                                                              ('chal004', '瑜伽挑战', '2024-04-01', '2024-04-30', '每天瑜伽练习30分钟', 'user005'),
                                                                                                              ('chal005', '早睡挑战', '2024-05-01', '2024-05-31', '坚持每晚11点前睡觉', 'user007'),
                                                                                                              ('chal006', '冥想挑战', '2024-06-01', '2024-06-30', '每天冥想15分钟', 'user002'),
                                                                                                              ('chal007', '阅读挑战', '2024-07-01', '2024-07-31', '每天阅读30分钟', 'user004'),
                                                                                                              ('chal008', '饮水挑战', '2024-08-01', '2024-08-31', '每天喝8杯水', 'user006'),
                                                                                                              ('chal009', '运动挑战', '2024-09-01', '2024-09-30', '每周运动5次', 'user003'),
                                                                                                              ('chal010', '学习挑战', '2024-10-01', '2024-10-31', '每天学习新技能1小时', 'user008');

-- 7. 插入健康报告数据
INSERT IGNORE INTO health_report (report_id, report_month, total_steps, summary, user_id, verifier_id) VALUES
                                                                                                           ('rep001', '2024-01-01', 250000, '一月份健康报告，步数达标', 'user001', 'doc001'),
                                                                                                           ('rep002', '2024-01-01', 180000, '一月份健康报告，需要增加运动', 'user002', NULL),
                                                                                                           ('rep003', '2024-02-01', 300000, '二月份健康报告，表现优秀', 'user001', 'doc002'),
                                                                                                           ('rep004', '2024-02-01', 220000, '二月份健康报告，进步明显', 'user003', NULL),
                                                                                                           ('rep005', '2024-03-01', 280000, '三月份健康报告，保持良好', 'user005', 'doc004'),
                                                                                                           ('rep006', '2024-03-01', 190000, '三月份健康报告，需改进', 'user004', NULL),
                                                                                                           ('rep007', '2024-04-01', 320000, '四月份健康报告，非常活跃', 'user001', 'doc001'),
                                                                                                           ('rep008', '2024-04-01', 210000, '四月份健康报告，稳步提升', 'user006', NULL),
                                                                                                           ('rep009', '2024-05-01', 270000, '五月份健康报告，表现稳定', 'user007', 'doc005'),
                                                                                                           ('rep010', '2024-05-01', 230000, '五月份健康报告，良好进展', 'user008', NULL);

-- 8. 插入预约数据
INSERT IGNORE INTO appointment (appointment_id, date_time, type, note, status, user_id, report_id) VALUES
                                                                                                       ('apt001', '2024-01-15 10:00:00', 'In-Person', '年度体检', 'Completed', 'user001', 'rep001'),
                                                                                                       ('apt002', '2024-01-20 14:30:00', 'Virtual', '在线咨询', 'Completed', 'user002', NULL),
                                                                                                       ('apt003', '2024-02-10 09:00:00', 'In-Person', '复诊检查', 'Scheduled', 'user001', NULL),
                                                                                                       ('apt004', '2024-02-15 11:00:00', 'Virtual', '健康咨询', 'Cancelled', 'user004', NULL),
                                                                                                       ('apt005', '2024-03-05 15:30:00', 'In-Person', '专科检查', 'Scheduled', 'user005', NULL),
                                                                                                       ('apt006', '2024-03-12 08:45:00', 'Virtual', '随访咨询', 'Completed', 'user003', NULL),
                                                                                                       ('apt007', '2024-04-08 13:15:00', 'In-Person', '体检复查', 'Scheduled', 'user006', NULL),
                                                                                                       ('apt008', '2024-04-18 16:00:00', 'Virtual', '在线诊断', 'Completed', 'user007', NULL),
                                                                                                       ('apt009', '2024-05-22 10:30:00', 'In-Person', '年度评估', 'Scheduled', 'user008', NULL),
                                                                                                       ('apt010', '2024-05-28 14:00:00', 'Virtual', '健康指导', 'Cancelled', 'user009', NULL);

-- 9. 插入邀请数据
INSERT IGNORE INTO invitation (invitation_id, invitee_contact, sent_time, expired_time, status, invitation_type, inviter_id, related_challenge_id) VALUES
                                                                                                                                                       ('inv001', 'friend@email.com', '2024-01-01 10:00:00', '2024-01-16 10:00:00', 'Accepted', 'Challenge', 'user001', 'chal001'),
                                                                                                                                                       ('inv002', 'colleague@company.com', '2024-01-02 14:00:00', '2024-01-17 14:00:00', 'Pending', 'Platform', 'user002', NULL),
                                                                                                                                                       ('inv003', 'family@email.com', '2024-02-01 09:00:00', '2024-02-16 09:00:00', 'Expired', 'Challenge', 'user005', 'chal004'),
                                                                                                                                                       ('inv004', 'neighbor@email.com', '2024-03-01 16:00:00', '2024-03-16 16:00:00', 'Cancelled', 'Platform', 'user007', NULL),
                                                                                                                                                       ('inv005', 'teammate@club.com', '2024-04-05 11:30:00', '2024-04-20 11:30:00', 'Accepted', 'Challenge', 'user003', 'chal006'),
                                                                                                                                                       ('inv006', 'partner@business.com', '2024-05-10 15:45:00', '2024-05-25 15:45:00', 'Pending', 'Platform', 'user004', NULL),
                                                                                                                                                       ('inv007', 'relative@family.com', '2024-06-15 08:20:00', '2024-06-30 08:20:00', 'Accepted', 'Challenge', 'user006', 'chal008'),
                                                                                                                                                       ('inv008', 'classmate@school.com', '2024-07-20 12:10:00', '2024-08-04 12:10:00', 'Expired', 'Platform', 'user008', NULL),
                                                                                                                                                       ('inv009', 'coworker@office.com', '2024-08-25 17:00:00', '2024-09-09 17:00:00', 'Cancelled', 'Challenge', 'user009', 'chal010'),
                                                                                                                                                       ('inv010', 'mentor@university.com', '2024-09-30 09:50:00', '2024-10-15 09:50:00', 'Pending', 'Platform', 'user010', NULL);

-- 10. 插入用户-提供者关联数据
INSERT IGNORE INTO user_provider_link (health_id, license_number, is_primary) VALUES
                                                                                  ('user001', 'doc001', TRUE),
                                                                                  ('user001', 'doc002', FALSE),
                                                                                  ('user002', 'doc001', TRUE),
                                                                                  ('user003', 'doc003', TRUE),
                                                                                  ('user004', 'doc004', TRUE),
                                                                                  ('user005', 'doc005', TRUE),
                                                                                  ('user006', 'doc006', TRUE),
                                                                                  ('user007', 'doc007', TRUE),
                                                                                  ('user008', 'doc008', TRUE),
                                                                                  ('user009', 'doc009', TRUE),
                                                                                  ('user010', 'doc010', TRUE);

-- 11. 插入预约-提供者关联数据
INSERT IGNORE INTO appointment_provider (appointment_id, license_number) VALUES
                                                                             ('apt001', 'doc001'),
                                                                             ('apt002', 'doc002'),
                                                                             ('apt003', 'doc001'),
                                                                             ('apt004', 'doc003'),
                                                                             ('apt005', 'doc004'),
                                                                             ('apt006', 'doc005'),
                                                                             ('apt007', 'doc006'),
                                                                             ('apt008', 'doc007'),
                                                                             ('apt009', 'doc008'),
                                                                             ('apt010', 'doc009');

-- 12. 插入参与记录数据
INSERT IGNORE INTO participation (health_id, challenge_id, progress) VALUES
                                                                         ('user001', 'chal001', 85),
                                                                         ('user002', 'chal001', 90),
                                                                         ('user001', 'chal002', 75),
                                                                         ('user003', 'chal001', 60),
                                                                         ('user004', 'chal003', 95),
                                                                         ('user005', 'chal004', 80),
                                                                         ('user007', 'chal005', 70),
                                                                         ('user006', 'chal006', 88),
                                                                         ('user008', 'chal007', 92),
                                                                         ('user009', 'chal008', 78),
                                                                         ('user010', 'chal009', 65),
                                                                         ('user003', 'chal010', 83),
                                                                         ('user004', 'chal001', 87),
                                                                         ('user005', 'chal002', 79),
                                                                         ('user006', 'chal003', 91);

-- 13. 插入挑战-报告关联数据
INSERT IGNORE INTO challenge_report (challenge_id, report_id) VALUES
                                                                  ('chal001', 'rep001'),
                                                                  ('chal001', 'rep003'),
                                                                  ('chal002', 'rep003'),
                                                                  ('chal004', 'rep005'),
                                                                  ('chal005', 'rep009'),
                                                                  ('chal006', 'rep007'),
                                                                  ('chal007', 'rep008'),
                                                                  ('chal008', 'rep010'),
                                                                  ('chal009', 'rep004'),
                                                                  ('chal010', 'rep006');

-- ==================== 数据验证查询 ====================

SELECT '数据库初始化完成' AS status;
SELECT COUNT(*) AS family_count FROM family_group;
SELECT COUNT(*) AS user_count FROM app_user;
SELECT COUNT(*) AS user_email_count FROM user_email;
SELECT COUNT(*) AS provider_count FROM provider;
SELECT COUNT(*) AS provider_email_count FROM provider_email;
SELECT COUNT(*) AS challenge_count FROM wellness_challenge;
SELECT COUNT(*) AS report_count FROM health_report;
SELECT COUNT(*) AS appointment_count FROM appointment;
SELECT COUNT(*) AS invitation_count FROM invitation;
SELECT COUNT(*) AS participation_count FROM participation;