-- HealthTrack Health Tracking Platform - Database Initialization Script (Revised Version)
-- Compliant with Phase 2 design specifications, email stored as multi-valued attribute separately

-- Note: SET FOREIGN_KEY_CHECKS statement is commented because Spring Boot's SQL parser may not handle it correctly
-- SET FOREIGN_KEY_CHECKS = 0;

-- ==================== Table Structure Creation ====================

-- 1. Family Group Table
CREATE TABLE IF NOT EXISTS family_group (
                                            family_id VARCHAR(20) PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. User Table (email field removed)
CREATE TABLE IF NOT EXISTS app_user (
                                        health_id VARCHAR(20) PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        phone VARCHAR(15),
                                        verification_status VARCHAR(10),
                                        role VARCHAR(20),
                                        family_id VARCHAR(20),
                                        FOREIGN KEY (family_id) REFERENCES family_group(family_id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. User Email Multi-valued Attribute Table (new)
CREATE TABLE IF NOT EXISTS user_email (
                                          health_id VARCHAR(20),
                                          email_address VARCHAR(100),
                                          is_primary BOOLEAN DEFAULT FALSE,
                                          PRIMARY KEY (health_id, email_address),
                                          FOREIGN KEY (health_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Healthcare Provider Table (email field removed)
CREATE TABLE IF NOT EXISTS provider (
                                        license_number VARCHAR(20) PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        specialty VARCHAR(50),
                                        verified_status VARCHAR(10),
                                        phone VARCHAR(15)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Provider Email Multi-valued Attribute Table (new)
CREATE TABLE IF NOT EXISTS provider_email (
                                              license_number VARCHAR(20),
                                              email_address VARCHAR(100),
                                              is_primary BOOLEAN DEFAULT FALSE,
                                              PRIMARY KEY (license_number, email_address),
                                              FOREIGN KEY (license_number) REFERENCES provider(license_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Wellness Challenge Table
CREATE TABLE IF NOT EXISTS wellness_challenge (
                                                  challenge_id VARCHAR(20) PRIMARY KEY,
                                                  goal TEXT,
                                                  start_date DATE NOT NULL,
                                                  end_date DATE NOT NULL,
                                                  description TEXT,
                                                  creator_id VARCHAR(20) NOT NULL,
                                                  FOREIGN KEY (creator_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Health Report Table
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

-- 8. Appointment Table
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

-- 9. Invitation Table
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

-- 10. User-Provider Relationship Table
CREATE TABLE IF NOT EXISTS user_provider_link (
                                                  health_id VARCHAR(20),
                                                  license_number VARCHAR(20),
                                                  is_primary BOOLEAN DEFAULT FALSE,
                                                  PRIMARY KEY (health_id, license_number),
                                                  FOREIGN KEY (health_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                                  FOREIGN KEY (license_number) REFERENCES provider(license_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. Appointment-Provider Relationship Table
CREATE TABLE IF NOT EXISTS appointment_provider (
                                                    appointment_id VARCHAR(20),
                                                    license_number VARCHAR(20),
                                                    PRIMARY KEY (appointment_id, license_number),
                                                    FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                                    FOREIGN KEY (license_number) REFERENCES provider(license_number) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. Participation Record Table
CREATE TABLE IF NOT EXISTS participation (
                                             health_id VARCHAR(20),
                                             challenge_id VARCHAR(20),
                                             progress INTEGER,
                                             PRIMARY KEY (health_id, challenge_id),
                                             FOREIGN KEY (health_id) REFERENCES app_user(health_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                             FOREIGN KEY (challenge_id) REFERENCES wellness_challenge(challenge_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. Challenge-Report Relationship Table
CREATE TABLE IF NOT EXISTS challenge_report (
                                                challenge_id VARCHAR(20),
                                                report_id VARCHAR(20),
                                                PRIMARY KEY (challenge_id, report_id),
                                                FOREIGN KEY (challenge_id) REFERENCES wellness_challenge(challenge_id) ON DELETE CASCADE ON UPDATE CASCADE,
                                                FOREIGN KEY (report_id) REFERENCES health_report(report_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Note: SET FOREIGN_KEY_CHECKS statement is commented because Spring Boot's SQL parser may not handle it correctly
-- SET FOREIGN_KEY_CHECKS = 1;

-- ==================== Data Insertion ====================

-- 1. Insert Family Group Data
INSERT IGNORE INTO family_group (family_id) VALUES
                                                ('fam001'), ('fam002'), ('fam003'), ('fam004'), ('fam005');

-- 2. Insert User Data (without email field)
INSERT IGNORE INTO app_user (health_id, name, phone, verification_status, role, family_id) VALUES
                                                                                               ('user001', 'Zhang San', '13800138000', 'Verified', 'Regular User', 'fam001'),
                                                                                               ('user002', 'Li Si', '13900139000', 'Verified', 'Administrator', 'fam001'),
                                                                                               ('user003', 'Wang Wu', '13600136000', 'Unverified', 'Regular User', 'fam002'),
                                                                                               ('user004', 'Zhao Liu', '13500135000', 'Verified', 'Regular User', NULL),
                                                                                               ('user005', 'Qian Qi', '13700137000', 'Verified', 'Regular User', 'fam003'),
                                                                                               ('user006', 'Sun Ba', '13400134000', 'Unverified', 'Regular User', 'fam004'),
                                                                                               ('user007', 'Zhou Jiu', '13300133000', 'Verified', 'Administrator', 'fam005'),
                                                                                               ('user008', 'Wu Shi', '13200132000', 'Verified', 'Regular User', 'fam002'),
                                                                                               ('user009', 'Zheng Shiyi', '13100131000', 'Verified', 'Regular User', 'fam003'),
                                                                                               ('user010', 'Wang Shier', '13000130000', 'Unverified', 'Regular User', NULL);

-- 3. Insert User Email Data (multi-valued attribute)
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

-- 4. Insert Healthcare Provider Data (without email field)
INSERT IGNORE INTO provider (license_number, name, specialty, verified_status, phone) VALUES
                                                                                          ('doc001', 'Dr. Wang', 'Cardiology', 'Verified', '13600136000'),
                                                                                          ('doc002', 'Dr. Li', 'Neurology', 'Verified', '13700137000'),
                                                                                          ('doc003', 'Dr. Zhang', 'Pediatrics', 'Unverified', '13800138000'),
                                                                                          ('doc004', 'Dr. Liu', 'Orthopedics', 'Verified', '13900139000'),
                                                                                          ('doc005', 'Dr. Chen', 'Ophthalmology', 'Verified', '13500135000'),
                                                                                          ('doc006', 'Dr. Yang', 'Dermatology', 'Verified', '13400134000'),
                                                                                          ('doc007', 'Dr. Huang', 'Dentistry', 'Verified', '13300133000'),
                                                                                          ('doc008', 'Dr. Zhou', 'Internal Medicine', 'Unverified', '13200132000'),
                                                                                          ('doc009', 'Dr. Wu', 'Surgery', 'Verified', '13100131000'),
                                                                                          ('doc010', 'Dr. Zheng', 'Traditional Chinese Medicine', 'Verified', '13000130000');

-- 5. Insert Provider Email Data (multi-valued attribute)
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

-- 6. Insert Wellness Challenge Data
INSERT IGNORE INTO wellness_challenge (challenge_id, goal, start_date, end_date, description, creator_id) VALUES
                                                                                                              ('chal001', 'Daily 10,000 Steps Challenge', '2024-01-01', '2024-01-31', 'Walk 10,000 steps every day', 'user001'),
                                                                                                              ('chal002', 'Healthy Eating Month', '2024-02-01', '2024-02-28', 'Maintain healthy diet for 30 days', 'user002'),
                                                                                                              ('chal003', 'Quit Smoking Challenge', '2024-03-01', '2024-03-31', '30 days without smoking challenge', 'user001'),
                                                                                                              ('chal004', 'Yoga Challenge', '2024-04-01', '2024-04-30', 'Practice yoga for 30 minutes daily', 'user005'),
                                                                                                              ('chal005', 'Early Sleep Challenge', '2024-05-01', '2024-05-31', 'Sleep before 11 PM every night', 'user007'),
                                                                                                              ('chal006', 'Meditation Challenge', '2024-06-01', '2024-06-30', 'Meditate for 15 minutes daily', 'user002'),
                                                                                                              ('chal007', 'Reading Challenge', '2024-07-01', '2024-07-31', 'Read for 30 minutes daily', 'user004'),
                                                                                                              ('chal008', 'Hydration Challenge', '2024-08-01', '2024-08-31', 'Drink 8 glasses of water daily', 'user006'),
                                                                                                              ('chal009', 'Exercise Challenge', '2024-09-01', '2024-09-30', 'Exercise 5 times per week', 'user003'),
                                                                                                              ('chal010', 'Learning Challenge', '2024-10-01', '2024-10-31', 'Learn new skills for 1 hour daily', 'user008');

-- 7. Insert Health Report Data
INSERT IGNORE INTO health_report (report_id, report_month, total_steps, summary, user_id, verifier_id) VALUES
                                                                                                           ('rep001', '2024-01-01', 250000, 'January health report, step goal achieved', 'user001', 'doc001'),
                                                                                                           ('rep002', '2024-01-01', 180000, 'January health report, need more exercise', 'user002', NULL),
                                                                                                           ('rep003', '2024-02-01', 300000, 'February health report, excellent performance', 'user001', 'doc002'),
                                                                                                           ('rep004', '2024-02-01', 220000, 'February health report, significant improvement', 'user003', NULL),
                                                                                                           ('rep005', '2024-03-01', 280000, 'March health report, maintaining good condition', 'user005', 'doc004'),
                                                                                                           ('rep006', '2024-03-01', 190000, 'March health report, needs improvement', 'user004', NULL),
                                                                                                           ('rep007', '2024-04-01', 320000, 'April health report, very active', 'user001', 'doc001'),
                                                                                                           ('rep008', '2024-04-01', 210000, 'April health report, steady progress', 'user006', NULL),
                                                                                                           ('rep009', '2024-05-01', 270000, 'May health report, stable performance', 'user007', 'doc005'),
                                                                                                           ('rep010', '2024-05-01', 230000, 'May health report, good progress', 'user008', NULL);

-- 8. Insert Appointment Data
INSERT IGNORE INTO appointment (appointment_id, date_time, type, note, status, user_id, report_id) VALUES
                                                                                                       ('apt001', '2024-01-15 10:00:00', 'In-Person', 'Annual physical examination', 'Completed', 'user001', 'rep001'),
                                                                                                       ('apt002', '2024-01-20 14:30:00', 'Virtual', 'Online consultation', 'Completed', 'user002', NULL),
                                                                                                       ('apt003', '2024-02-10 09:00:00', 'In-Person', 'Follow-up examination', 'Scheduled', 'user001', NULL),
                                                                                                       ('apt004', '2024-02-15 11:00:00', 'Virtual', 'Health consultation', 'Cancelled', 'user004', NULL),
                                                                                                       ('apt005', '2024-03-05 15:30:00', 'In-Person', 'Specialist examination', 'Scheduled', 'user005', NULL),
                                                                                                       ('apt006', '2024-03-12 08:45:00', 'Virtual', 'Follow-up consultation', 'Completed', 'user003', NULL),
                                                                                                       ('apt007', '2024-04-08 13:15:00', 'In-Person', 'Physical re-examination', 'Scheduled', 'user006', NULL),
                                                                                                       ('apt008', '2024-04-18 16:00:00', 'Virtual', 'Online diagnosis', 'Completed', 'user007', NULL),
                                                                                                       ('apt009', '2024-05-22 10:30:00', 'In-Person', 'Annual assessment', 'Scheduled', 'user008', NULL),
                                                                                                       ('apt010', '2024-05-28 14:00:00', 'Virtual', 'Health guidance', 'Cancelled', 'user009', NULL);

-- 9. Insert Invitation Data
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

-- 10. Insert User-Provider Relationship Data
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

-- 11. Insert Appointment-Provider Relationship Data
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

-- 12. Insert Participation Record Data
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

-- 13. Insert Challenge-Report Relationship Data
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

-- ==================== Data Validation Queries ====================
-- Note: SELECT query statements have been removed because Spring Boot's SQL initialization scripts do not support queries
-- If data validation is needed, please execute queries through other methods after the application starts