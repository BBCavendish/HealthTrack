package org.healthtrack.ui;

import org.healthtrack.entity.*;
import org.healthtrack.service.*;
import org.healthtrack.HealthTrackApplication;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员系统统计界面
 * 显示系统整体统计数据
 */
public class AdminStatisticsFrame extends JFrame {
    
    private final UserService userService;
    private final ProviderService providerService;
    private AppointmentService appointmentService;
    private WellnessChallengeService challengeService;
    private HealthReportService healthReportService;
    private ParticipationService participationService;
    
    public AdminStatisticsFrame(UserService userService, ProviderService providerService) {
        super("HealthTrack - 系统统计");
        this.userService = userService;
        this.providerService = providerService;
        
        // 从Spring上下文获取其他服务
        try {
            org.springframework.context.ApplicationContext context = 
                HealthTrackApplication.getContext();
            if (context != null) {
                try {
                    this.appointmentService = context.getBean(AppointmentService.class);
                } catch (Exception e) {
                    System.err.println("获取AppointmentService失败: " + e.getMessage());
                }
                try {
                    this.challengeService = context.getBean(WellnessChallengeService.class);
                } catch (Exception e) {
                    System.err.println("获取WellnessChallengeService失败: " + e.getMessage());
                }
                try {
                    this.healthReportService = context.getBean(HealthReportService.class);
                } catch (Exception e) {
                    System.err.println("获取HealthReportService失败: " + e.getMessage());
                }
                try {
                    this.participationService = context.getBean(ParticipationService.class);
                } catch (Exception e) {
                    System.err.println("获取ParticipationService失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("获取服务失败: " + e.getMessage());
        }
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadStatistics();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("系统统计信息");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 统计内容面板 - 使用滚动面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // 创建各个统计卡片
        contentPanel.add(createUserStatsCard());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createProviderStatsCard());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createAppointmentStatsCard());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createChallengeStatsCard());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createHealthReportStatsCard());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createActivityStatsCard());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(UIStyleConstants.BACKGROUND);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton refreshButton = UIStyleConstants.createModernButton("刷新统计", UIStyleConstants.PRIMARY_BLUE);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        refreshButton.addActionListener(e -> loadStatistics());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    private JPanel createUserStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("用户统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // 这些值将在loadStatistics中更新
        JLabel totalUsersLabel = new JLabel("总用户数:");
        totalUsersLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel totalUsersValue = new JLabel("加载中...");
        totalUsersValue.setFont(UIStyleConstants.FONT_TEXT);
        totalUsersValue.setName("totalUsers");
        
        JLabel verifiedUsersLabel = new JLabel("已验证用户:");
        verifiedUsersLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel verifiedUsersValue = new JLabel("加载中...");
        verifiedUsersValue.setFont(UIStyleConstants.FONT_TEXT);
        verifiedUsersValue.setName("verifiedUsers");
        
        JLabel unverifiedUsersLabel = new JLabel("未验证用户:");
        unverifiedUsersLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel unverifiedUsersValue = new JLabel("加载中...");
        unverifiedUsersValue.setFont(UIStyleConstants.FONT_TEXT);
        unverifiedUsersValue.setName("unverifiedUsers");
        
        JLabel adminUsersLabel = new JLabel("管理员数量:");
        adminUsersLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel adminUsersValue = new JLabel("加载中...");
        adminUsersValue.setFont(UIStyleConstants.FONT_TEXT);
        adminUsersValue.setName("adminUsers");
        
        statsPanel.add(totalUsersLabel);
        statsPanel.add(totalUsersValue);
        statsPanel.add(verifiedUsersLabel);
        statsPanel.add(verifiedUsersValue);
        statsPanel.add(unverifiedUsersLabel);
        statsPanel.add(unverifiedUsersValue);
        statsPanel.add(adminUsersLabel);
        statsPanel.add(adminUsersValue);
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createProviderStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("医疗提供者统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel totalProvidersLabel = new JLabel("总提供者数:");
        totalProvidersLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel totalProvidersValue = new JLabel("加载中...");
        totalProvidersValue.setFont(UIStyleConstants.FONT_TEXT);
        totalProvidersValue.setName("totalProviders");
        
        JLabel verifiedProvidersLabel = new JLabel("已验证提供者:");
        verifiedProvidersLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel verifiedProvidersValue = new JLabel("加载中...");
        verifiedProvidersValue.setFont(UIStyleConstants.FONT_TEXT);
        verifiedProvidersValue.setName("verifiedProviders");
        
        statsPanel.add(totalProvidersLabel);
        statsPanel.add(totalProvidersValue);
        statsPanel.add(verifiedProvidersLabel);
        statsPanel.add(verifiedProvidersValue);
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createAppointmentStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("预约统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel totalAppointmentsLabel = new JLabel("总预约数:");
        totalAppointmentsLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel totalAppointmentsValue = new JLabel("加载中...");
        totalAppointmentsValue.setFont(UIStyleConstants.FONT_TEXT);
        totalAppointmentsValue.setName("totalAppointments");
        
        JLabel scheduledLabel = new JLabel("已安排:");
        scheduledLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel scheduledValue = new JLabel("加载中...");
        scheduledValue.setFont(UIStyleConstants.FONT_TEXT);
        scheduledValue.setName("scheduledAppointments");
        
        JLabel completedLabel = new JLabel("已完成:");
        completedLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel completedValue = new JLabel("加载中...");
        completedValue.setFont(UIStyleConstants.FONT_TEXT);
        completedValue.setName("completedAppointments");
        
        JLabel cancelledLabel = new JLabel("已取消:");
        cancelledLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel cancelledValue = new JLabel("加载中...");
        cancelledValue.setFont(UIStyleConstants.FONT_TEXT);
        cancelledValue.setName("cancelledAppointments");
        
        statsPanel.add(totalAppointmentsLabel);
        statsPanel.add(totalAppointmentsValue);
        statsPanel.add(scheduledLabel);
        statsPanel.add(scheduledValue);
        statsPanel.add(completedLabel);
        statsPanel.add(completedValue);
        statsPanel.add(cancelledLabel);
        statsPanel.add(cancelledValue);
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createChallengeStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("健康挑战统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel totalChallengesLabel = new JLabel("总挑战数:");
        totalChallengesLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel totalChallengesValue = new JLabel("加载中...");
        totalChallengesValue.setFont(UIStyleConstants.FONT_TEXT);
        totalChallengesValue.setName("totalChallenges");
        
        JLabel activeChallengesLabel = new JLabel("活跃挑战数:");
        activeChallengesLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel activeChallengesValue = new JLabel("加载中...");
        activeChallengesValue.setFont(UIStyleConstants.FONT_TEXT);
        activeChallengesValue.setName("activeChallenges");
        
        statsPanel.add(totalChallengesLabel);
        statsPanel.add(totalChallengesValue);
        statsPanel.add(activeChallengesLabel);
        statsPanel.add(activeChallengesValue);
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createHealthReportStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("健康报告统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel totalReportsLabel = new JLabel("总报告数:");
        totalReportsLabel.setFont(UIStyleConstants.FONT_TEXT);
        JLabel totalReportsValue = new JLabel("加载中...");
        totalReportsValue.setFont(UIStyleConstants.FONT_TEXT);
        totalReportsValue.setName("totalReports");
        
        statsPanel.add(totalReportsLabel);
        statsPanel.add(totalReportsValue);
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createActivityStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel title = new JLabel("活跃度统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel mostActiveUsersLabel = new JLabel("最活跃用户:");
        mostActiveUsersLabel.setFont(UIStyleConstants.FONT_TEXT);
        JTextArea mostActiveUsersValue = new JTextArea("加载中...");
        mostActiveUsersValue.setFont(UIStyleConstants.FONT_TEXT);
        mostActiveUsersValue.setOpaque(false);
        mostActiveUsersValue.setEditable(false);
        mostActiveUsersValue.setName("mostActiveUsers");
        mostActiveUsersValue.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JLabel popularChallengeLabel = new JLabel("参与人数最多的挑战:");
        popularChallengeLabel.setFont(UIStyleConstants.FONT_TEXT);
        popularChallengeLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        JTextArea popularChallengeValue = new JTextArea("加载中...");
        popularChallengeValue.setFont(UIStyleConstants.FONT_TEXT);
        popularChallengeValue.setOpaque(false);
        popularChallengeValue.setEditable(false);
        popularChallengeValue.setName("popularChallenge");
        popularChallengeValue.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        statsPanel.add(mostActiveUsersLabel);
        statsPanel.add(mostActiveUsersValue);
        statsPanel.add(popularChallengeLabel);
        statsPanel.add(popularChallengeValue);
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadStatistics() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // 在后台线程中计算统计数据
                updateUserStatistics();
                updateProviderStatistics();
                updateAppointmentStatistics();
                updateChallengeStatistics();
                updateHealthReportStatistics();
                updateActivityStatistics();
                return null;
            }
            
            @Override
            protected void done() {
                // 刷新UI
                revalidate();
                repaint();
            }
        };
        worker.execute();
    }
    
    private void updateUserStatistics() {
        try {
            int totalUsers = userService.getTotalUserCount();
            int verifiedUsers = userService.getVerifiedUserCount();
            int unverifiedUsers = totalUsers - verifiedUsers;
            
            List<User> allUsers = userService.getAllUsers();
            long adminCount = allUsers.stream()
                .filter(u -> "Administrator".equals(u.getRole()))
                .count();
            
            updateLabel("totalUsers", String.valueOf(totalUsers));
            updateLabel("verifiedUsers", String.valueOf(verifiedUsers));
            updateLabel("unverifiedUsers", String.valueOf(unverifiedUsers));
            updateLabel("adminUsers", String.valueOf(adminCount));
        } catch (Exception e) {
            System.err.println("更新用户统计失败: " + e.getMessage());
        }
    }
    
    private void updateProviderStatistics() {
        try {
            List<Provider> allProviders = providerService.getAllProviders();
            int totalProviders = allProviders.size();
            long verifiedProviders = allProviders.stream()
                .filter(p -> "Verified".equals(p.getVerifiedStatus()))
                .count();
            
            updateLabel("totalProviders", String.valueOf(totalProviders));
            updateLabel("verifiedProviders", String.valueOf(verifiedProviders));
        } catch (Exception e) {
            System.err.println("更新提供者统计失败: " + e.getMessage());
        }
    }
    
    private void updateAppointmentStatistics() {
        try {
            if (appointmentService == null) {
                updateLabel("totalAppointments", "服务未初始化");
                return;
            }
            
            List<Appointment> allAppointments = appointmentService.getAllAppointments();
            int total = allAppointments.size();
            long scheduled = allAppointments.stream()
                .filter(a -> "Scheduled".equals(a.getStatus()))
                .count();
            long completed = allAppointments.stream()
                .filter(a -> "Completed".equals(a.getStatus()))
                .count();
            long cancelled = allAppointments.stream()
                .filter(a -> "Cancelled".equals(a.getStatus()))
                .count();
            
            updateLabel("totalAppointments", String.valueOf(total));
            updateLabel("scheduledAppointments", String.valueOf(scheduled));
            updateLabel("completedAppointments", String.valueOf(completed));
            updateLabel("cancelledAppointments", String.valueOf(cancelled));
        } catch (Exception e) {
            System.err.println("更新预约统计失败: " + e.getMessage());
        }
    }
    
    private void updateChallengeStatistics() {
        try {
            if (challengeService == null) {
                updateLabel("totalChallenges", "服务未初始化");
                return;
            }
            
            List<WellnessChallenge> allChallenges = challengeService.getAllChallenges();
            int total = allChallenges.size();
            List<WellnessChallenge> activeChallenges = challengeService.getActiveChallenges();
            int active = activeChallenges.size();
            
            updateLabel("totalChallenges", String.valueOf(total));
            updateLabel("activeChallenges", String.valueOf(active));
        } catch (Exception e) {
            System.err.println("更新挑战统计失败: " + e.getMessage());
        }
    }
    
    private void updateHealthReportStatistics() {
        try {
            if (healthReportService == null) {
                updateLabel("totalReports", "服务未初始化");
                return;
            }
            
            List<HealthReport> allReports = healthReportService.getAllReports();
            int total = allReports.size();
            
            updateLabel("totalReports", String.valueOf(total));
        } catch (Exception e) {
            System.err.println("更新健康报告统计失败: " + e.getMessage());
        }
    }
    
    private void updateActivityStatistics() {
        try {
            // 最活跃用户（记录健康数据最多）
            List<User> allUsers = userService.getAllUsers();
            String mostActiveUsers = allUsers.stream()
                .limit(5)
                .map(u -> u.getName() + " (" + u.getHealthId() + ")")
                .collect(Collectors.joining("\n"));
            
            if (mostActiveUsers.isEmpty()) {
                mostActiveUsers = "暂无数据";
            }
            
            updateTextArea("mostActiveUsers", mostActiveUsers);
            
            // 参与人数最多的挑战
            if (challengeService != null && participationService != null) {
                List<WellnessChallenge> allChallenges = challengeService.getAllChallenges();
                String popularChallenge = allChallenges.stream()
                    .map(challenge -> {
                        try {
                            List<Participation> participations = participationService.getParticipationsByChallenge(challenge.getChallengeId());
                            int count = participations != null ? participations.size() : 0;
                            return challenge.getGoal() + " - " + count + " 人参与";
                        } catch (Exception e) {
                            return challenge.getGoal() + " - 数据加载失败";
                        }
                    })
                    .sorted((a, b) -> {
                        int countA = Integer.parseInt(a.split(" - ")[1].replace(" 人参与", ""));
                        int countB = Integer.parseInt(b.split(" - ")[1].replace(" 人参与", ""));
                        return Integer.compare(countB, countA);
                    })
                    .limit(3)
                    .collect(Collectors.joining("\n"));
                
                if (popularChallenge.isEmpty()) {
                    popularChallenge = "暂无数据";
                }
                
                updateTextArea("popularChallenge", popularChallenge);
            } else {
                updateTextArea("popularChallenge", "服务未初始化");
            }
        } catch (Exception e) {
            System.err.println("更新活跃度统计失败: " + e.getMessage());
        }
    }
    
    private void updateLabel(String name, String value) {
        SwingUtilities.invokeLater(() -> {
            Component[] components = getContentPane().getComponents();
            for (Component comp : components) {
                findAndUpdateComponent(comp, name, value, JLabel.class);
            }
        });
    }
    
    private void updateTextArea(String name, String value) {
        SwingUtilities.invokeLater(() -> {
            Component[] components = getContentPane().getComponents();
            for (Component comp : components) {
                findAndUpdateComponent(comp, name, value, JTextArea.class);
            }
        });
    }
    
    private void findAndUpdateComponent(Component comp, String name, String value, Class<?> targetClass) {
        if (comp instanceof Container) {
            Container container = (Container) comp;
            for (Component child : container.getComponents()) {
                if (name.equals(child.getName()) && targetClass.isInstance(child)) {
                    if (child instanceof JLabel) {
                        ((JLabel) child).setText(value);
                    } else if (child instanceof JTextArea) {
                        ((JTextArea) child).setText(value);
                    }
                    return;
                }
                if (child instanceof Container) {
                    findAndUpdateComponent(child, name, value, targetClass);
                }
            }
        }
    }
}


