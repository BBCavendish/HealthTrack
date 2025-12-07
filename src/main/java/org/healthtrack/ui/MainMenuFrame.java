package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 主菜单页面 - 导航中心
 */
public class MainMenuFrame extends JFrame {
    
    private final UserService userService;
    private AppointmentService appointmentService;
    private WellnessChallengeService challengeService;
    private HealthReportService healthReportService;
    private ProviderService providerService;
    private UserProviderLinkService userProviderLinkService;
    private InvitationService invitationService;
    private ParticipationService participationService;
    
    private final User currentUser;
    
    public MainMenuFrame(UserService userService, User currentUser) {
        super("HealthTrack - 主菜单");
        this.userService = userService;
        this.currentUser = currentUser;
        
        // 从Spring上下文获取其他服务
        try {
            org.springframework.context.ApplicationContext context = 
                org.healthtrack.HealthTrackApplication.getContext();
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
                    this.providerService = context.getBean(ProviderService.class);
                } catch (Exception e) {
                    System.err.println("获取ProviderService失败: " + e.getMessage());
                }
                try {
                    this.userProviderLinkService = context.getBean(UserProviderLinkService.class);
                } catch (Exception e) {
                    System.err.println("获取UserProviderLinkService失败: " + e.getMessage());
                }
                try {
                    this.invitationService = context.getBean(InvitationService.class);
                } catch (Exception e) {
                    System.err.println("获取InvitationService失败: " + e.getMessage());
                }
                try {
                    this.participationService = context.getBean(ParticipationService.class);
                } catch (Exception e) {
                    System.err.println("获取ParticipationService失败: " + e.getMessage());
                }
            } else {
                System.err.println("警告: Spring应用上下文为null，某些功能可能无法使用");
            }
        } catch (IllegalStateException e) {
            System.err.println("Spring上下文尚未初始化: " + e.getMessage());
            // 继续执行，但某些功能可能不可用
        } catch (Exception e) {
            System.err.println("获取服务失败: " + e.getMessage());
            e.printStackTrace();
            // 继续执行，但某些功能可能不可用
        }
        
        // 设置现代外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
    }
    
    // 带所有服务的构造函数
    public MainMenuFrame(UserService userService, 
                        AppointmentService appointmentService,
                        WellnessChallengeService challengeService,
                        HealthReportService healthReportService,
                        ProviderService providerService,
                        UserProviderLinkService userProviderLinkService,
                        InvitationService invitationService,
                        ParticipationService participationService,
                        User currentUser) {
        super("HealthTrack - 主菜单");
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.challengeService = challengeService;
        this.healthReportService = healthReportService;
        this.providerService = providerService;
        this.userProviderLinkService = userProviderLinkService;
        this.invitationService = invitationService;
        this.participationService = participationService;
        this.currentUser = currentUser;
        
        // 设置现代外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        // 主面板
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题区域
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("健康追踪系统");
        JLabel welcomeLabel = new JLabel("欢迎，" + currentUser.getName() + " (" + currentUser.getHealthId() + ")");
        welcomeLabel.setFont(UIStyleConstants.FONT_SUBTITLE);
        welcomeLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(welcomeLabel, BorderLayout.SOUTH);
        
        // 功能菜单卡片
        JPanel menuPanel = UIStyleConstants.createCardPanel();
        menuPanel.setLayout(new GridLayout(3, 2, 20, 20));
        
        // 账户信息管理
        JButton accountButton = createMenuButton("账户信息管理", UIStyleConstants.PRIMARY_BLUE, 
            e -> openAccountManagementFrame());
        
        // 预约管理
        JButton appointmentButton = createMenuButton("预约管理", UIStyleConstants.SECONDARY_GREEN,
            e -> openAppointmentManagementFrame());
        
        // 健康挑战管理
        JButton challengeButton = createMenuButton("健康挑战管理", UIStyleConstants.ACCENT_ORANGE,
            e -> openChallengeManagementFrame());
        
        // 月度健康摘要
        JButton summaryButton = createMenuButton("月度健康摘要", UIStyleConstants.PURPLE,
            e -> openMonthlySummaryFrame());
        
        // 搜索记录
        JButton searchButton = createMenuButton("搜索记录", UIStyleConstants.PRIMARY_BLUE,
            e -> openSearchFrame());
        
        // 家庭组管理
        JButton familyButton = createMenuButton("家庭组管理", UIStyleConstants.SECONDARY_GREEN,
            e -> openFamilyGroupFrame());
        
        menuPanel.add(accountButton);
        menuPanel.add(appointmentButton);
        menuPanel.add(challengeButton);
        menuPanel.add(summaryButton);
        menuPanel.add(searchButton);
        menuPanel.add(familyButton);
        
        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setOpaque(false);
        
        JButton logoutButton = UIStyleConstants.createModernButton("退出登录", UIStyleConstants.DANGER_RED);
        logoutButton.addActionListener(e -> logout());
        
        bottomPanel.add(logoutButton);
        
        // 组装界面
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private JButton createMenuButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        button.setBackground(color);
        
        // 统一使用深色文字
        button.setForeground(UIStyleConstants.TEXT_PRIMARY);
        
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(30, 20, 30, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        
        // 鼠标悬停效果
        final Color originalColor = color;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color hoverColor = originalColor.brighter();
                button.setBackground(hoverColor);
                button.setForeground(UIStyleConstants.TEXT_PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                button.setForeground(UIStyleConstants.TEXT_PRIMARY);
            }
        });
        
        return button;
    }
    
    private void openAccountManagementFrame() {
        if (providerService == null || userProviderLinkService == null) {
            JOptionPane.showMessageDialog(this,
                "服务未初始化，请重启应用程序",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        AccountManagementFrame frame = new AccountManagementFrame(userService, providerService, 
            userProviderLinkService, currentUser);
        frame.setVisible(true);
    }
    
    private void openAppointmentManagementFrame() {
        if (appointmentService == null || providerService == null) {
            JOptionPane.showMessageDialog(this,
                "服务未初始化，请重启应用程序",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        AppointmentManagementFrame frame = new AppointmentManagementFrame(appointmentService, 
            providerService, currentUser);
        frame.setVisible(true);
    }
    
    private void openChallengeManagementFrame() {
        if (challengeService == null || participationService == null) {
            JOptionPane.showMessageDialog(this,
                "服务未初始化，请重启应用程序",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        ChallengeManagementFrame frame = new ChallengeManagementFrame(challengeService, 
            participationService, currentUser);
        frame.setVisible(true);
    }
    
    private void openMonthlySummaryFrame() {
        if (healthReportService == null || appointmentService == null) {
            JOptionPane.showMessageDialog(this,
                "服务未初始化，请重启应用程序",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        MonthlySummaryFrame frame = new MonthlySummaryFrame(healthReportService, 
            appointmentService, currentUser);
        frame.setVisible(true);
    }
    
    private void openSearchFrame() {
        if (appointmentService == null || providerService == null || healthReportService == null) {
            JOptionPane.showMessageDialog(this,
                "服务未初始化，请重启应用程序",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        SearchFrame frame = new SearchFrame(appointmentService, providerService, 
            healthReportService, currentUser);
        frame.setVisible(true);
    }
    
    private void openFamilyGroupFrame() {
        if (invitationService == null) {
            JOptionPane.showMessageDialog(this,
                "服务未初始化，请重启应用程序",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        FamilyGroupFrame frame = new FamilyGroupFrame(userService, invitationService, 
            currentUser);
        frame.setVisible(true);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要退出登录吗？",
            "确认退出",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            this.dispose();
            
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(userService);
                loginFrame.setVisible(true);
            });
        }
    }
}

