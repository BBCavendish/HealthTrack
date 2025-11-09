package org.healthtrack.controller;

import org.healthtrack.entity.*;
import org.healthtrack.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

@Component
public class HealthTrackCLI implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private WellnessChallengeService wellnessChallengeService;

    @Autowired
    private HealthReportService healthReportService;

    @Autowired
    private ProviderService providerService;

    @Autowired
    private ParticipationService participationService;

    // 删除这两个不必要的注入
    // @Autowired
    // private UserEmailService userEmailService; // 删除
    // @Autowired
    // private ProviderEmailService providerEmailService; // 删除

    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== HealthTrack 健康管理平台启动 ===");
        System.out.println("✅ 数据库连接成功");
        System.out.println("✅ 系统初始化完成");
        showMainMenu();
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n=== 主菜单 ===");
            System.out.println("1. 用户管理");
            System.out.println("2. 预约管理");
            System.out.println("3. 健康挑战");
            System.out.println("4. 健康报告");
            System.out.println("5. 医疗提供者");
            System.out.println("6. 系统统计");
            System.out.println("7. 邮箱管理");
            System.out.println("8. 用户查询工具");
            System.out.println("9. 退出系统");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    manageUsers();
                    break;
                case "2":
                    manageAppointments();
                    break;
                case "3":
                    manageChallenges();
                    break;
                case "4":
                    manageHealthReports();
                    break;
                case "5":
                    manageProviders();
                    break;
                case "6":
                    showStatistics();
                    break;
                case "7":
                    manageEmails();
                    break;
                case "8":
                    userQueryTools();
                    break;
                case "9":
                    System.out.println("感谢使用HealthTrack，再见！");
                    return;
                default:
                    System.out.println("❌ 无效选择，请重新输入");
            }
        }
    }

    // ==================== 用户管理功能 ====================
    private void manageUsers() {
        while (true) {
            System.out.println("\n=== 用户管理 ===");
            System.out.println("1. 查看所有用户");
            System.out.println("2. 添加新用户");
            System.out.println("3. 搜索用户");
            System.out.println("4. 更新用户信息");
            System.out.println("5. 删除用户");
            System.out.println("6. 查看家庭成员");
            System.out.println("7. 查看验证状态用户");
            System.out.println("8. 返回主菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    listAllUsers();
                    break;
                case "2":
                    addNewUser();
                    break;
                case "3":
                    searchUsers();
                    break;
                case "4":
                    updateUser();
                    break;
                case "5":
                    deleteUser();
                    break;
                case "6":
                    showFamilyMembers();
                    break;
                case "7":
                    showUsersByVerificationStatus();
                    break;
                case "8":
                    return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void listAllUsers() {
        System.out.println("\n=== 所有用户列表 ===");
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("暂无用户数据");
            return;
        }

        System.out.printf("%-15s %-10s %-15s %-10s %-10s %-10s%n",
                "健康ID", "姓名", "电话", "状态", "角色", "家庭ID");
        System.out.println("----------------------------------------------------------------");

        for (User user : users) {
            // 获取主邮箱
            UserEmail primaryEmail = userService.getPrimaryEmail(user.getHealthId());
            String emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无邮箱";

            System.out.printf("%-15s %-10s %-15s %-10s %-10s %-10s%n",
                    user.getHealthId(), user.getName(), user.getPhone(),
                    user.getVerificationStatus(), user.getRole(), user.getFamilyId());
        }
    }

    private void addNewUser() {
        System.out.println("\n=== 添加新用户 ===");
        System.out.print("请输入健康ID: ");
        String healthId = scanner.nextLine();

        System.out.print("请输入姓名: ");
        String name = scanner.nextLine();

        System.out.print("请输入电话: ");
        String phone = scanner.nextLine();

        System.out.print("请输入角色 (普通用户/管理员): ");
        String role = scanner.nextLine();

        System.out.print("请输入家庭ID (可选): ");
        String familyId = scanner.nextLine();

        User user = new User();
        user.setHealthId(healthId);
        user.setName(name);
        user.setPhone(phone);
        user.setRole(role);
        user.setFamilyId(familyId.isEmpty() ? null : familyId);
        user.setVerificationStatus("Unverified");

        if (userService.saveUser(user)) {
            System.out.println("✅ 用户添加成功");

            // 询问是否添加邮箱
            System.out.print("是否立即添加邮箱? (y/n): ");
            if ("y".equalsIgnoreCase(scanner.nextLine())) {
                addUserEmail(healthId);
            }
        } else {
            System.out.println("❌ 用户添加失败");
        }
    }

    private void searchUsers() {
        System.out.print("请输入要搜索的用户姓名: ");
        String name = scanner.nextLine();

        List<User> users = userService.searchUsersByName(name);
        if (users.isEmpty()) {
            System.out.println("未找到相关用户");
            return;
        }

        System.out.println("\n=== 搜索结果 ===");
        for (User user : users) {
            UserEmail primaryEmail = userService.getPrimaryEmail(user.getHealthId());
            String emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无邮箱";

            System.out.printf("ID: %s, 姓名: %s, 电话: %s, 邮箱: %s%n",
                    user.getHealthId(), user.getName(), user.getPhone(), emailDisplay);
        }
    }

    private void updateUser() {
        System.out.print("请输入要更新的用户ID: ");
        String healthId = scanner.nextLine();

        User user = userService.getUserById(healthId);
        if (user == null) {
            System.out.println("❌ 用户不存在");
            return;
        }

        System.out.println("当前用户信息:");
        UserEmail primaryEmail = userService.getPrimaryEmail(healthId);
        String emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无邮箱";
        System.out.printf("姓名: %s, 电话: %s, 邮箱: %s%n", user.getName(), user.getPhone(), emailDisplay);

        System.out.print("请输入新姓名 (直接回车保持原值): ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) {
            user.setName(name);
        }

        System.out.print("请输入新电话: ");
        String phone = scanner.nextLine();
        if (!phone.trim().isEmpty()) {
            user.setPhone(phone);
        }

        System.out.print("是否验证用户? (y/n): ");
        String verify = scanner.nextLine();
        if ("y".equalsIgnoreCase(verify)) {
            user.setVerificationStatus("Verified");
        }

        if (userService.saveUser(user)) {
            System.out.println("✅ 用户信息更新成功");
        } else {
            System.out.println("❌ 用户信息更新失败");
        }
    }

    private void deleteUser() {
        System.out.print("请输入要删除的用户ID: ");
        String healthId = scanner.nextLine();

        System.out.print("确认删除用户 " + healthId + "? (y/n): ");
        String confirm = scanner.nextLine();

        if ("y".equalsIgnoreCase(confirm)) {
            if (userService.deleteUser(healthId)) {
                System.out.println("✅ 用户删除成功");
            } else {
                System.out.println("❌ 用户删除失败");
            }
        }
    }

    private void showFamilyMembers() {
        System.out.print("请输入家庭ID: ");
        String familyId = scanner.nextLine();

        List<User> users = userService.getUsersByFamilyId(familyId);
        if (users.isEmpty()) {
            System.out.println("该家庭没有成员或家庭ID不存在");
            return;
        }

        System.out.println("\n=== 家庭成员列表 ===");
        for (User user : users) {
            UserEmail primaryEmail = userService.getPrimaryEmail(user.getHealthId());
            String emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无邮箱";

            System.out.printf("ID: %s, 姓名: %s, 电话: %s, 邮箱: %s%n",
                    user.getHealthId(), user.getName(), user.getPhone(), emailDisplay);
        }
    }

    private void showUsersByVerificationStatus() {
        System.out.print("请输入验证状态 (Verified/Unverified): ");
        String status = scanner.nextLine();

        List<User> users = userService.getUsersByVerificationStatus(status);
        if (users.isEmpty()) {
            System.out.println("没有找到符合条件的用户");
            return;
        }

        System.out.println("\n=== " + status + " 用户列表 ===");
        for (User user : users) {
            UserEmail primaryEmail = userService.getPrimaryEmail(user.getHealthId());
            String emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无邮箱";

            System.out.printf("ID: %s, 姓名: %s, 电话: %s, 邮箱: %s%n",
                    user.getHealthId(), user.getName(), user.getPhone(), emailDisplay);
        }
    }

    // ==================== 邮箱管理功能 ====================
    private void manageEmails() {
        while (true) {
            System.out.println("\n=== 邮箱管理 ===");
            System.out.println("1. 管理用户邮箱");
            System.out.println("2. 管理提供者邮箱");
            System.out.println("3. 返回主菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    manageUserEmails();
                    break;
                case "2":
                    manageProviderEmails();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void manageUserEmails() {
        System.out.print("请输入用户ID: ");
        String healthId = scanner.nextLine();

        User user = userService.getUserById(healthId);
        if (user == null) {
            System.out.println("❌ 用户不存在");
            return;
        }

        while (true) {
            System.out.println("\n=== 用户邮箱管理 (" + user.getName() + ") ===");
            System.out.println("1. 查看所有邮箱");
            System.out.println("2. 添加邮箱");
            System.out.println("3. 删除邮箱");
            System.out.println("4. 设置主邮箱");
            System.out.println("5. 查看主邮箱");
            System.out.println("6. 返回上级菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showUserEmails(healthId);
                    break;
                case "2":
                    addUserEmail(healthId);
                    break;
                case "3":
                    removeUserEmail(healthId);
                    break;
                case "4":
                    setPrimaryUserEmail(healthId);
                    break;
                case "5":
                    showPrimaryUserEmail(healthId);
                    break;
                case "6":
                    return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void showUserEmails(String healthId) {
        List<UserEmail> emails = userService.getUserEmails(healthId);
        if (emails.isEmpty()) {
            System.out.println("该用户没有邮箱");
            return;
        }

        System.out.println("\n=== 用户邮箱列表 ===");
        for (UserEmail email : emails) {
            String primaryMark = email.getIsPrimary() ? "★" : "";
            System.out.printf("%s %s (%s)%n", primaryMark, email.getEmailAddress(),
                    email.getIsPrimary() ? "主邮箱" : "备用邮箱");
        }
    }

    private void addUserEmail(String healthId) {
        System.out.print("请输入邮箱地址: ");
        String emailAddress = scanner.nextLine();

        System.out.print("是否设置为主邮箱? (y/n): ");
        boolean isPrimary = "y".equalsIgnoreCase(scanner.nextLine());

        if (userService.addUserEmail(healthId, emailAddress, isPrimary)) {
            System.out.println("✅ 邮箱添加成功");
        } else {
            System.out.println("❌ 邮箱添加失败");
        }
    }

    private void removeUserEmail(String healthId) {
        System.out.print("请输入要删除的邮箱地址: ");
        String emailAddress = scanner.nextLine();

        if (userService.removeUserEmail(healthId, emailAddress)) {
            System.out.println("✅ 邮箱删除成功");
        } else {
            System.out.println("❌ 邮箱删除失败");
        }
    }

    private void setPrimaryUserEmail(String healthId) {
        System.out.print("请输入要设置为主邮箱的地址: ");
        String emailAddress = scanner.nextLine();

        if (userService.setPrimaryEmail(healthId, emailAddress)) {
            System.out.println("✅ 主邮箱设置成功");
        } else {
            System.out.println("❌ 主邮箱设置失败");
        }
    }

    private void showPrimaryUserEmail(String healthId) {
        UserEmail primaryEmail = userService.getPrimaryEmail(healthId);
        if (primaryEmail == null) {
            System.out.println("该用户没有设置主邮箱");
            return;
        }

        System.out.println("主邮箱: " + primaryEmail.getEmailAddress());
    }

    private void manageProviderEmails() {
        System.out.print("请输入提供者许可证号: ");
        String licenseNumber = scanner.nextLine();

        Provider provider = providerService.getProviderById(licenseNumber);
        if (provider == null) {
            System.out.println("❌ 提供者不存在");
            return;
        }

        while (true) {
            System.out.println("\n=== 提供者邮箱管理 (" + provider.getName() + ") ===");
            System.out.println("1. 查看所有邮箱");
            System.out.println("2. 添加邮箱");
            System.out.println("3. 删除邮箱");
            System.out.println("4. 设置主邮箱");
            System.out.println("5. 查看主邮箱");
            System.out.println("6. 返回上级菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showProviderEmails(licenseNumber);
                    break;
                case "2":
                    addProviderEmail(licenseNumber);
                    break;
                case "3":
                    removeProviderEmail(licenseNumber);
                    break;
                case "4":
                    setPrimaryProviderEmail(licenseNumber);
                    break;
                case "5":
                    showPrimaryProviderEmail(licenseNumber);
                    break;
                case "6":
                    return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void showProviderEmails(String licenseNumber) {
        List<ProviderEmail> emails = providerService.getProviderEmails(licenseNumber);
        if (emails.isEmpty()) {
            System.out.println("该提供者没有邮箱");
            return;
        }

        System.out.println("\n=== 提供者邮箱列表 ===");
        for (ProviderEmail email : emails) {
            String primaryMark = email.getIsPrimary() ? "★" : "";
            System.out.printf("%s %s (%s)%n", primaryMark, email.getEmailAddress(),
                    email.getIsPrimary() ? "主邮箱" : "备用邮箱");
        }
    }

    private void addProviderEmail(String licenseNumber) {
        System.out.print("请输入邮箱地址: ");
        String emailAddress = scanner.nextLine();

        System.out.print("是否设置为主邮箱? (y/n): ");
        boolean isPrimary = "y".equalsIgnoreCase(scanner.nextLine());

        if (providerService.addProviderEmail(licenseNumber, emailAddress, isPrimary)) {
            System.out.println("✅ 邮箱添加成功");
        } else {
            System.out.println("❌ 邮箱添加失败");
        }
    }

    private void removeProviderEmail(String licenseNumber) {
        System.out.print("请输入要删除的邮箱地址: ");
        String emailAddress = scanner.nextLine();

        if (providerService.removeProviderEmail(licenseNumber, emailAddress)) {
            System.out.println("✅ 邮箱删除成功");
        } else {
            System.out.println("❌ 邮箱删除失败");
        }
    }

    private void setPrimaryProviderEmail(String licenseNumber) {
        System.out.print("请输入要设置为主邮箱的地址: ");
        String emailAddress = scanner.nextLine();

        if (providerService.setPrimaryProviderEmail(licenseNumber, emailAddress)) {
            System.out.println("✅ 主邮箱设置成功");
        } else {
            System.out.println("❌ 主邮箱设置失败");
        }
    }

    private void showPrimaryProviderEmail(String licenseNumber) {
        ProviderEmail primaryEmail = providerService.getPrimaryProviderEmail(licenseNumber);
        if (primaryEmail == null) {
            System.out.println("该提供者没有设置主邮箱");
            return;
        }

        System.out.println("主邮箱: " + primaryEmail.getEmailAddress());
    }

    // ==================== 用户查询工具 ====================
    private void userQueryTools() {
        while (true) {
            System.out.println("\n=== 用户查询工具 ===");
            System.out.println("1. 检查用户是否存在");
            System.out.println("2. 根据邮箱查找用户");
            System.out.println("3. 查询家庭成员");
            System.out.println("4. 返回主菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    checkUserExists();
                    break;
                case "2":
                    findUserByEmail();
                    break;
                case "3":
                    findFamilyMembers();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void checkUserExists() {
        System.out.print("请输入要检查的用户ID: ");
        String healthId = scanner.nextLine();

        boolean exists = userService.existsUser(healthId);
        if (exists) {
            System.out.println("✅ 用户 " + healthId + " 存在");

            // 显示用户详细信息
            User user = userService.getUserById(healthId);
            if (user != null) {
                UserEmail primaryEmail = userService.getPrimaryEmail(healthId);
                String emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无邮箱";

                System.out.println("📋 用户信息:");
                System.out.println("   姓名: " + user.getName());
                System.out.println("   电话: " + user.getPhone());
                System.out.println("   邮箱: " + emailDisplay);
                System.out.println("   状态: " + user.getVerificationStatus());
                System.out.println("   角色: " + user.getRole());
                System.out.println("   家庭ID: " + (user.getFamilyId() != null ? user.getFamilyId() : "无"));
            }
        } else {
            System.out.println("❌ 用户 " + healthId + " 不存在");
        }
    }

    private void findUserByEmail() {
        System.out.print("请输入要查找的邮箱地址: ");
        String email = scanner.nextLine();

        User user = userService.getUserByEmail(email);
        if (user != null) {
            System.out.println("✅ 找到用户:");
            System.out.println("   ID: " + user.getHealthId());
            System.out.println("   姓名: " + user.getName());
            System.out.println("   电话: " + user.getPhone());
            System.out.println("   状态: " + user.getVerificationStatus());

            // 显示该用户的所有邮箱
            List<UserEmail> emails = userService.getUserEmails(user.getHealthId());
            System.out.println("   📧 关联邮箱:");
            for (UserEmail userEmail : emails) {
                String primaryMark = userEmail.getIsPrimary() ? "★" : "";
                System.out.println("      " + primaryMark + userEmail.getEmailAddress() +
                        (userEmail.getIsPrimary() ? " (主邮箱)" : ""));
            }
        } else {
            System.out.println("❌ 没有找到使用邮箱 " + email + " 的用户");
        }
    }

    private void findFamilyMembers() {
        System.out.print("请输入家庭ID: ");
        String familyId = scanner.nextLine();

        List<User> familyMembers = userService.getUsersByFamilyId(familyId);
        if (familyMembers.isEmpty()) {
            System.out.println("❌ 家庭 " + familyId + " 不存在或没有成员");
            return;
        }

        System.out.println("👨‍👩‍👧‍👦 家庭成员列表 (家庭ID: " + familyId + "):");
        System.out.printf("%-15s %-10s %-15s %-10s %-10s%n",
                "健康ID", "姓名", "电话", "状态", "角色");
        System.out.println("------------------------------------------------------------");

        for (User member : familyMembers) {
            UserEmail primaryEmail = userService.getPrimaryEmail(member.getHealthId());
            String emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无邮箱";

            System.out.printf("%-15s %-10s %-15s %-10s %-10s%n",
                    member.getHealthId(), member.getName(), member.getPhone(),
                    member.getVerificationStatus(), member.getRole());
        }

        System.out.println("总计: " + familyMembers.size() + " 名成员");
    }

    // ==================== 系统统计功能 ====================
    private void showStatistics() {
        System.out.println("\n=== 系统统计 ===");

        // 用户统计
        int totalUsers = userService.getTotalUserCount();
        int verifiedUsers = userService.getVerifiedUserCount();
        System.out.println("👥 用户总数: " + totalUsers);
        System.out.println("✅ 已认证用户: " + verifiedUsers);
        System.out.println("❌ 未认证用户: " + (totalUsers - verifiedUsers));

        // 邮箱统计
        System.out.println("\n📧 邮箱统计:");
        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            int emailCount = userService.getUserEmailCount(user.getHealthId());
            System.out.println(user.getName() + ": " + emailCount + " 个邮箱");
        }

        // 活跃用户统计
        List<User> activeUsers = userService.getUsersWithMostHealthRecords(3);
        System.out.println("\n🏆 最活跃用户 (前3名):");
        for (int i = 0; i < activeUsers.size(); i++) {
            User user = activeUsers.get(i);
            System.out.println((i + 1) + ". " + user.getName() + " (" + user.getHealthId() + ")");
        }

        // 其他统计（原有）
        int appointmentCount = appointmentService.getAllAppointments().size();
        int challengeCount = wellnessChallengeService.getAllChallenges().size();
        int reportCount = healthReportService.getAllReports().size();
        int providerCount = providerService.getAllProviders().size();

        System.out.println("\n📊 预约总数: " + appointmentCount);
        System.out.println("📊 健康挑战总数: " + challengeCount);
        System.out.println("📊 健康报告总数: " + reportCount);
        System.out.println("📊 医疗提供者总数: " + providerCount);
    }

    // ==================== 预约管理功能 ====================
    private void manageAppointments() {
        while (true) {
            System.out.println("\n=== 预约管理 ===");
            System.out.println("1. 查看所有预约");
            System.out.println("2. 创建新预约");
            System.out.println("3. 更新预约状态");
            System.out.println("4. 取消预约");
            System.out.println("5. 查看用户预约");
            System.out.println("6. 查看提供者预约");
            System.out.println("7. 返回主菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    listAllAppointments();
                    break;
                case "2":
                    createAppointment();
                    break;
                case "3":
                    updateAppointmentStatus();
                    break;
                case "4":
                    cancelAppointment();
                    break;
                case "5":
                    showUserAppointments();
                    break;
                case "6":
                    showProviderAppointments();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void listAllAppointments() {
        System.out.println("\n=== 所有预约列表 ===");
        List<Appointment> appointments = appointmentService.getAllAppointments();
        if (appointments.isEmpty()) {
            System.out.println("暂无预约数据");
            return;
        }

        System.out.printf("%-15s %-20s %-10s %-15s %-10s %-15s%n",
                "预约ID", "时间", "类型", "状态", "用户ID", "报告ID");
        System.out.println("----------------------------------------------------------------");

        for (Appointment appointment : appointments) {
            System.out.printf("%-15s %-20s %-10s %-15s %-10s %-15s%n",
                    appointment.getAppointmentId(),
                    appointment.getDateTime().format(dateTimeFormatter),
                    appointment.getType(),
                    appointment.getStatus(),
                    appointment.getUserId(),
                    appointment.getReportId() != null ? appointment.getReportId() : "无");
        }
    }

    private void createAppointment() {
        System.out.println("\n=== 创建新预约 ===");
        System.out.print("请输入预约ID: ");
        String appointmentId = scanner.nextLine();

        System.out.print("请输入用户ID: ");
        String userId = scanner.nextLine();

        // 检查用户是否存在
        if (!userService.existsUser(userId)) {
            System.out.println("❌ 用户不存在");
            return;
        }

        System.out.print("请输入预约时间 (yyyy-MM-dd HH:mm): ");
        String dateTimeStr = scanner.nextLine();

        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("❌ 时间格式错误，请使用 yyyy-MM-dd HH:mm 格式");
            return;
        }

        System.out.print("请输入预约类型 (In-Person/Virtual): ");
        String type = scanner.nextLine();

        System.out.print("请输入备注: ");
        String note = scanner.nextLine();

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setUserId(userId);
        appointment.setDateTime(dateTime);
        appointment.setType(type);
        appointment.setNote(note);
        appointment.setStatus("Scheduled");

        if (appointmentService.saveAppointment(appointment)) {
            System.out.println("✅ 预约创建成功");

            // 询问是否关联医疗提供者
            System.out.print("是否关联医疗提供者? (y/n): ");
            if ("y".equalsIgnoreCase(scanner.nextLine())) {
                linkAppointmentToProvider(appointmentId);
            }
        } else {
            System.out.println("❌ 预约极创建失败");
        }
    }

    private void linkAppointmentToProvider(String appointmentId) {
        System.out.print("请输入医疗提供者许可证号: ");
        String licenseNumber = scanner.nextLine();

        // 检查提供者是否存在
        if (providerService.getProviderById(licenseNumber) == null) {
            System.out.println("❌ 医疗提供者不存在");
            return;
        }

        if (appointmentService.linkProviderToAppointment(appointmentId, licenseNumber)) {
            System.out.println("✅ 医疗提供者关联成功");
        } else {
            System.out.println("❌ 医疗提供者关联失败");
        }
    }

    private void updateAppointmentStatus() {
        System.out.print("请输入要更新的预约ID: ");
        String appointmentId = scanner.nextLine();

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment == null) {
            System.out.println("❌ 预约不存在");
        }

        System.out.println("当前预约状态: " + appointment.getStatus());
        System.out.print("请输入新状态 (Scheduled/Completed/Cancelled): ");
        String status = scanner.nextLine();

        appointment.setStatus(status);

        // 如果取消预约，需要输入原因
        if ("Cancelled".equals(status)) {
            System.out.print("请输入取消原因: ");
            String reason = scanner.nextLine();
            appointment.setCancelReason(reason);
        }

        if (appointmentService.saveAppointment(appointment)) {
            System.out.println("✅ 预约状态更新成功");
        } else {
            System.out.println("❌ 预约状态更新失败");
        }
    }

    private void cancelAppointment() {
        System.out.print("请输入要取消的预约ID: ");
        String appointmentId = scanner.nextLine();

        System.out.print("请输入取消原因: ");
        String reason = scanner.nextLine();

        if (appointmentService.cancelAppointment(appointmentId, reason)) {
            System.out.println("✅ 预约取消成功");
        } else {
            System.out.println("❌ 预约取消失败");
        }
    }

    private void showUserAppointments() {
        System.out.print("请输入用户ID: ");
        String userId = scanner.nextLine();

        List<Appointment> appointments = appointmentService.getAppointmentsByUser(userId);
        if (appointments.isEmpty()) {
            System.out.println("该用户没有预约记录");
            return;
        }

        System.out.println("\n=== 用户预约记录 ===");
        for (Appointment appointment : appointments) {
            System.out.printf("ID: %s, 时间: %s, 类型: %s, 状态: %s%n",
                    appointment.getAppointmentId(),
                    appointment.getDateTime().format(dateTimeFormatter),
                    appointment.getType(),
                    appointment.getStatus());
        }
    }

    private void showProviderAppointments() {
        System.out.print("请输入医疗提供者许可证号: ");
        String licenseNumber = scanner.nextLine();

        List<Appointment> appointments = appointmentService.getAppointmentsByProvider(licenseNumber);
        if (appointments.isEmpty()) {
            System.out.println("该提供者没有预约记录");
            return;
        }

        System.out.println("\n=== 提供者预约记录 ===");
        for (Appointment appointment : appointments) {
            System.out.printf("ID: %s, 时间: %s, 用户: %s, 类型: %s, 状态: %s%n",
                    appointment.getAppointmentId(),
                    appointment.getDateTime().format(dateTimeFormatter),
                    appointment.getUserId(),
                    appointment.getType(),
                    appointment.getStatus());
        }
    }

    // ==================== 健康挑战功能 ====================
    private void manageChallenges() {
        while (true) {
            System.out.println("\n=== 健康挑战管理 ===");
            System.out.println("1. 查看所有挑战");
            System.out.println("2. 创建新挑战");
            System.out.println("3. 参与挑战");
            System.out.println("4. 更新挑战进度");
            System.out.println("5. 查看挑战排名");
            System.out.println("6. 查看活跃挑战");
            System.out.println("7. 查看用户参与情况");
            System.out.println("8. 返回主菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    listAllChallenges();
                    break;
                case "2":
                    createChallenge();
                    break;
                case "3":
                    joinChallenge();
                    break;
                case "4":
                    updateChallengeProgress();
                    break;
                case "5":
                    showChallengeRankings();
                    break;
                case "6":
                    showActiveChallenges();
                    break;
                case "7":
                    showUserParticipations();
                    break;
                case "8":
                return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void listAllChallenges() {
        System.out.println("=== 所有健康挑战 ===");
        List<WellnessChallenge> challenges = wellnessChallengeService.getAllChallenges();
        if (challenges.isEmpty()) {
            System.out.println("暂无挑战数据");
            return;
        }

        System.out.printf("%-15s %-20s %-15s %-15s %-10s%n",
                "挑战ID", "目标", "开始日期", "结束日期", "创建者");
        System.out.println("----------------------------------------------------------------");

        for (WellnessChallenge challenge : challenges) {
            String goalPreview = challenge.getGoal().length() > 18 ?
                    challenge.getGoal().substring(0, 15) + "..." : challenge.getGoal();

            System.out.printf("%-15s %-20s %-15s %-极15s %-10s%n",
                    challenge.getChallengeId(),
                    goalPreview,
                    challenge.getStartDate().format(dateFormatter),
                    challenge.getEndDate().format(dateFormatter),
                    challenge.getCreatorId());
        }
    }

    private void createChallenge() {
        System.out.println("\n=== 创建新挑战 ===");
        System.out.print("请输入挑战ID: ");
        String challengeId = scanner.nextLine();

        System.out.print("请输入挑战目标: ");
        String goal = scanner.nextLine();

        System.out.print("请输入开始日期 (yyyy-MM-dd): ");
        String startDateStr = scanner.nextLine();

        System.out.print("请输入结束日期 (yyyy-MM-dd): ");
        String endDateStr = scanner.nextLine();

        System.out.print("请输入创建者ID: ");
        String creatorId = scanner.nextLine();

        // 检查创建者是否存在
        if (!userService.existsUser(creatorId)) {
            System.out.println("❌ 创建者用户不存在");
            return;
        }

        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startDateStr, dateFormatter);
            endDate = LocalDate.parse(endDateStr, dateFormatter);

            if (endDate.isBefore(startDate)) {
                System.out.println("❌ 结束日期不能早于开始日期");
                return;
            }
        } catch (DateTimeParseException e) {
            System.out.println("❌ 日期格式错误，请使用 yyyy-MM-dd 格式");
            return;
        }

        System.out.print("请输入挑战描述: ");
        String description = scanner.nextLine();

        WellnessChallenge challenge = new WellnessChallenge();
        challenge.setChallengeId(challengeId);
        challenge.setGoal(goal);
        challenge.setStartDate(startDate);
        challenge.setEndDate(endDate);
        challenge.setCreatorId(creatorId);
        challenge.setDescription(description);

        if (wellnessChallengeService.saveChallenge(challenge)) {
            System.out.println("✅ 挑战创建成功");
        } else {
            System.out.println("❌ 挑战创建失败");
        }
    }

    private void joinChallenge() {
        System.out.print("请输入用户ID: ");
        String userId = scanner.nextLine();

        // 检查用户是否存在
        if (!userService.existsUser(userId)) {
            System.out.println("❌ 用户不存在");
            return;
        }

        System.out.print("请输入挑战ID: ");
        String challengeId = scanner.nextLine();

        // 检查挑战是否存在
        if (wellnessChallengeService.getChallengeById(challengeId) == null) {
            System.out.println("❌ 挑战不存在");
            return;
        }

        if (participationService.joinChallenge(userId, challengeId)) {
            System.out.println("✅ 参与挑战成功");
        } else {
            System.out.println("❌ 参与挑战失败");
        }
    }

    private void updateChallengeProgress() {
        System.out.print("请输入用户ID: ");
        String userId = scanner.nextLine();

        System.out.print("请输入挑战ID: ");
        String challengeId = scanner.nextLine();

        System.out.print("请输入进度 (0-100): ");
        int progress;
        try {
            progress = Integer.parseInt(scanner.nextLine());
            if (progress < 0 || progress > 100) {
                System.out.println("❌ 进度必须在0-100之间");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 请输入有效的数字");
            return;
        }

        if (participationService.updateProgress(userId, challengeId, progress)) {
            System.out.println("✅ 进度更新成功");
        } else {
            System.out.println("❌ 进度更新失败");
        }
    }

    private void showChallengeRankings() {
        System.out.print("请输入挑战ID: ");
        String challengeId = scanner.nextLine();

        List<Participation> participations = participationService.getParticipationsByChallenge(challengeId);
        if (participations.isEmpty()) {
            System.out.println("暂无参与记录");
            return;
        }

        // 按进度排序
        participations.sort((p1, p2) -> Integer.compare(p2.getProgress(), p1.getProgress()));

        System.out.println("\n=== 挑战排名 ===");
        System.out.printf("%-5s %-15s %-10s %-10s%n", "排名", "用户ID", "进度", "完成度");
        System.out.println("----------------------------------");

        for (int i = 0; i < participations.size(); i++) {
            Participation p = participations.get(i);
            String completion = p.getProgress() == 100 ? "✅ 完成" : "进行中";
            System.out.printf("%-5d %-15s %-10d %-10s%n",
                    i + 1, p.getHealthId(), p.getProgress(), completion);
        }
    }

    private void showActiveChallenges() {
        System.out.println("\n=== 活跃挑战列表 ===");
        List<WellnessChallenge> activeChallenges = wellnessChallengeService.getActiveChallenges();
        if (activeChallenges.isEmpty()) {
            System.out.println("暂无活跃挑战");
            return;
        }

        for (WellnessChallenge challenge : activeChallenges) {
            int participantCount = participationService.getChallengeParticipantsCount(challenge.getChallengeId());
            System.out.printf("ID: %s, 目标: %s, 参与人数: %d, 截止: %s%n",
                    challenge.getChallengeId(),
                    challenge.getGoal(),
                    participantCount,
                    challenge.getEndDate().format(dateFormatter));
        }
    }

    private void showUserParticipations() {
        System.out.print("请输入用户ID: ");
        String userId = scanner.nextLine();

        List<Participation> participations = participationService.getParticipationsByUser(userId);
        if (participations.isEmpty()) {
            System.out.println("该用户没有参与任何挑战");
            return;
        }

        System.out.println("\n=== 用户参与情况 ===");
        for (Participation participation : participations) {
            WellnessChallenge challenge = wellnessChallengeService.getChallengeById(participation.getChallengeId());
            if (challenge != null) {
                System.out.printf("挑战: %s, 进度: %d%%, 目标: %s%n",
                        challenge.getChallengeId(),
                        participation.getProgress(),
                        challenge.getGoal());
            }
        }
    }

    // ==================== 健康报告功能 ====================
    private void manageHealthReports() {
        while (true) {
            System.out.println("\n=== 健康报告管理 ===");
            System.out.println("1. 查看所有报告");
            System.out.println("2. 创建健康报告");
            System.out.println("3. 查看用户报告");
            System.out.println("4. 验证健康报告");
            System.out.println("5. 返回主菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    listAllReports();
                    break;
                case "2":
                    createHealthReport();
                    break;
                case "3":
                    showUserReports();
                    break;
                case "4":
                    verifyHealthReport();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void listAllReports() {
        System.out.println("\n=== 所有健康报告 ===");
        List<HealthReport> reports = healthReportService.getAllReports();
        if (reports.isEmpty()) {
            System.out.println("暂无报告数据");
            return;
        }

        System.out.printf("%-15s %-15s %-10s %-15s %-15s%n",
                "报告ID", "月份", "步数", "用户ID", "验证者");
        System.out.println("------------------------------------------------");

        for (HealthReport report : reports) {
            System.out.printf("%-15s %-15s %-10d %-15s %-15s%n",
                    report.getReportId(),
                    report.getReportMonth().format(dateFormatter),
                    report.getTotalSteps(),
                    report.getUserId(),
                    report.getVerifierId() != null ? report.getVerifierId() : "未验证");
        }
    }

    private void createHealthReport() {
        System.out.println("\n=== 创建健康报告 ===");
        System.out.print("请输入报告ID: ");
        String reportId = scanner.nextLine();

        System.out.print("请输入用户ID: ");
        String userId = scanner.nextLine();

        // 检查用户是否存在
        if (!userService.existsUser(userId)) {
            System.out.println("❌ 用户不存在");
            return;
        }

        System.out.print("请输入月份 (yyyy-MM-dd): ");
        String monthStr = scanner.nextLine();

        System.out.print("请输入总步数: ");
        int totalSteps;
        try {
            totalSteps = Integer.parseInt(scanner.nextLine());
            if (totalSteps < 0) {
                System.out.println("❌ 步数不能为负数");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ 请输入有效的数字");
            return;
        }

        System.out.print("请输入总结: ");
        String summary = scanner.nextLine();

        LocalDate month;
        try {
            month = LocalDate.parse(monthStr, dateFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("❌ 日期格式错误");
            return;
        }

        HealthReport report = new HealthReport();
        report.setReportId(reportId);
        report.setUserId(userId);
        report.setReportMonth(month);
        report.setTotalSteps(totalSteps);
        report.setSummary(summary);

        if (healthReportService.saveReport(report)) {
            System.out.println("✅ 健康报告创建成功");
        } else {
            System.out.println("❌ 健康报告创建失败");
        }
    }

    private void showUserReports() {
        System.out.print("请输入用户ID: ");
        String userId = scanner.nextLine();

        List<HealthReport> reports = healthReportService.getReportsByUser(userId);
        if (reports.isEmpty()) {
            System.out.println("该用户没有健康报告");
            return;
        }

        System.out.println("\n=== 用户健康报告 ===");
        for (HealthReport report : reports) {
            System.out.printf("月份: %s, 步数: %d, 状态: %s%n",
                    report.getReportMonth().format(dateFormatter),
                    report.getTotalSteps(),
                    report.getVerifierId() != null ? "已验证" : "未验证");
        }
    }

    private void verifyHealthReport() {
        System.out.print("请输入报告ID: ");
        String reportId = scanner.nextLine();

        HealthReport report = healthReportService.getReportById(reportId);
        if (report == null) {
            System.out.println("❌ 报告不存在");
            return;
        }

        System.out.print("请输入医疗提供者许可证号: ");
        String licenseNumber = scanner.nextLine();

        // 检查提供者是否存在
        if (providerService.getProviderById(licenseNumber) == null) {
            System.out.println("❌ 医疗提供者不存在");
            return;
        }

        if (healthReportService.verifyReport(reportId, licenseNumber)) {
            System.out.println("✅ 报告验证成功");
        } else {
            System.out.println("❌ 报告验证失败");
        }
    }

    // ==================== 医疗提供者功能 ====================
    private void manageProviders() {
        while (true) {
            System.out.println("\n=== 医疗提供者管理 ===");
            System.out.println("1. 查看所有提供者");
            System.out.println("2. 添加提供者");
            System.out.println("3. 搜索提供者");
            System.out.println("4. 更新提供者信息");
            System.out.println("5. 删除提供者");
            System.out.println("6. 查看专业领域");
            System.out.println("7. 返回主菜单");
            System.out.print("请选择操作: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    listAllProviders();
                    break;
                case "2":
                    addProvider();
                    break;
                case "3":
                    searchProviders();
                    break;
                case "4":
                    updateProvider();
                    break;
                case "5":
                    deleteProvider();
                    break;
                case "6":
                    showProvidersBySpecialty();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("❌ 无效选择");
            }
        }
    }

    private void listAllProviders() {
        System.out.println("\n=== 所有医疗提供者 ===");
        List<Provider> providers = providerService.getAllProviders();
        if (providers.isEmpty()) {
            System.out.println("暂无提供者数据");
            return;
        }

        System.out.printf("%-15s %-10s %-15s %-10s %-15s%n",
                "许可证号", "姓名", "专业", "状态", "电话");
        System.out.println("----------------------------------------------");

        for (Provider provider : providers) {
            ProviderEmail primaryEmail = providerService.getPrimaryProviderEmail(provider.getLicenseNumber());
            String emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无邮箱";

            System.out.printf("%-15s %-10s %-15s %-10s %-15s%n",
                    provider.getLicenseNumber(),
                    provider.getName(),
                    provider.getSpecialty(),
                    provider.getVerifiedStatus(),
                    provider.getPhone());
        }
    }

    private void addProvider() {
        System.out.println("\n=== 添加医疗提供者 ===");
        System.out.print("请输入许可证号: ");
        String licenseNumber = scanner.nextLine();

        System.out.print("请输入姓名: ");
        String name = scanner.nextLine();

        System.out.print("请输入专业: ");
        String specialty = scanner.nextLine();

        System.out.print("请输入电话: ");
        String phone = scanner.nextLine();

        Provider provider = new Provider();
        provider.setLicenseNumber(licenseNumber);
        provider.setName(name);
        provider.setSpecialty(specialty);
        provider.setPhone(phone);
        provider.setVerifiedStatus("Verified");

        if (providerService.saveProvider(provider)) {
            System.out.println("✅ 医疗提供者添加成功");

            // 询问是否添加邮箱
            System.out.print("是否立即添加邮箱? (y/n): ");
            if ("y".equalsIgnoreCase(scanner.nextLine())) {
                addProviderEmail(licenseNumber);
            }
        } else {
            System.out.println("❌ 医疗提供者添加失败");
        }
    }

    private void searchProviders() {
        System.out.print("请输入要搜索的提供者姓名: ");
        String name = scanner.nextLine();

        List<Provider> providers = providerService.searchProvidersByName(name);
        if (providers.isEmpty()) {
            System.out.println("未找到相关提供者");
            return;
        }

        System.out.println("\n=== 搜索结果 ===");
        for (Provider provider : providers) {
            System.out.printf("许可证号: %s, 姓名: %s, 专业: %s, 状态: %s%n",
                    provider.getLicenseNumber(), provider.getName(),
                    provider.getSpecialty(), provider.getVerifiedStatus());
        }
    }

    private void updateProvider() {
        System.out.print("请输入要更新的提供者许可证号: ");
        String licenseNumber = scanner.nextLine();

        Provider provider = providerService.getProviderById(licenseNumber);
        if (provider == null) {
            System.out.println("❌ 提供者不存在");
            return;
        }

        System.out.println("当前提供者信息:");
        System.out.printf("姓名: %s, 专业: %s, 电话: %s%n", provider.getName(), provider.getSpecialty(), provider.getPhone());

        System.out.print("请输入新姓名 (直接回车保持原值): ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) {
            provider.setName(name);
        }

        System.out.print("请输入新专业: ");
        String specialty = scanner.nextLine();
        if (!specialty.trim().isEmpty()) {
            provider.setSpecialty(specialty);
        }

        System.out.print("请输入新电话: ");
        String phone = scanner.nextLine();
        if (!phone.trim().isEmpty()) {
            provider.setPhone(phone);
        }

        if (providerService.saveProvider(provider)) {
            System.out.println("✅ 提供者信息更新成功");
        } else {
            System.out.println("❌ 提供者信息更新失败");
        }
    }

    private void deleteProvider() {
        System.out.print("请输入要删除的提供者许可证号: ");
        String licenseNumber = scanner.nextLine();

        System.out.print("确认删除提供者 " + licenseNumber + "? (y/n): ");
        String confirm = scanner.nextLine();

        if ("y".equalsIgnoreCase(confirm)) {
            if (providerService.deleteProvider(licenseNumber)) {
                System.out.println("✅ 提供者删除成功");
            } else {
                System.out.println("❌ 提供者删除失败");
            }
        }
    }

    private void showProvidersBySpecialty() {
        System.out.print("请输入专业领域: ");
        String specialty = scanner.nextLine();

        List<Provider> providers = providerService.getProvidersBySpecialty(specialty);
        if (providers.isEmpty()) {
            System.out.println("没有找到该专业的提供者");
            return;
        }

        System.out.println("\n=== " + specialty + " 专业提供者 ===");
        for (Provider provider : providers) {
            System.out.printf("许可证号: %s, 姓名: %s, 状态: %s, 电话: %s%n",
                    provider.getLicenseNumber(), provider.getName(),
                    provider.getVerifiedStatus(), provider.getPhone());
        }
    }
}