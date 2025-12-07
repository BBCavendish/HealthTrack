package org.healthtrack.ui;

import org.healthtrack.entity.Provider;
import org.healthtrack.service.ProviderService;
import org.healthtrack.service.AppointmentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 医疗提供者菜单页面
 */
public class ProviderMenuFrame extends JFrame {
    
    private final ProviderService providerService;
    private AppointmentService appointmentService;
    private final Provider currentProvider;
    
    public ProviderMenuFrame(ProviderService providerService, Provider currentProvider) {
        super("HealthTrack - 医疗提供者菜单");
        this.providerService = providerService;
        this.currentProvider = currentProvider;
        
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
            }
        } catch (Exception e) {
            System.err.println("获取服务失败: " + e.getMessage());
        }
        
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("医疗提供者系统");
        JLabel welcomeLabel = new JLabel("欢迎，" + currentProvider.getName() + " (" + currentProvider.getLicenseNumber() + ")");
        welcomeLabel.setFont(UIStyleConstants.FONT_SUBTITLE);
        welcomeLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(welcomeLabel, BorderLayout.SOUTH);
        
        // 功能菜单卡片
        JPanel menuPanel = UIStyleConstants.createCardPanel();
        menuPanel.setLayout(new GridLayout(2, 2, 20, 20));
        
        // 我的预约管理
        JButton appointmentButton = createMenuButton("我的预约管理", UIStyleConstants.PRIMARY_BLUE, 
            e -> openAppointmentManagementFrame());
        
        // 个人信息管理
        JButton profileButton = createMenuButton("个人信息管理", UIStyleConstants.SECONDARY_GREEN,
            e -> openProfileManagementFrame());
        
        // 患者管理
        JButton patientButton = createMenuButton("患者管理", UIStyleConstants.ACCENT_ORANGE,
            e -> openPatientManagementFrame());
        
        // 预约统计
        JButton statisticsButton = createMenuButton("预约统计", UIStyleConstants.PURPLE,
            e -> openStatisticsFrame());
        
        menuPanel.add(appointmentButton);
        menuPanel.add(profileButton);
        menuPanel.add(patientButton);
        menuPanel.add(statisticsButton);
        
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
    
    private void openAppointmentManagementFrame() {
        if (appointmentService == null) {
            JOptionPane.showMessageDialog(this,
                "AppointmentService未初始化，请重启应用程序",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 创建提供者预约管理界面
        ProviderAppointmentFrame frame = new ProviderAppointmentFrame(appointmentService, currentProvider);
        frame.setVisible(true);
    }
    
    private void openProfileManagementFrame() {
        ProviderProfileFrame frame = new ProviderProfileFrame(providerService, currentProvider);
        frame.setVisible(true);
    }
    
    private void openPatientManagementFrame() {
        JOptionPane.showMessageDialog(this,
            "患者管理功能开发中...",
            "提示",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openStatisticsFrame() {
        if (appointmentService == null) {
            JOptionPane.showMessageDialog(this,
                "AppointmentService未初始化，请重启应用程序",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        ProviderStatisticsFrame frame = new ProviderStatisticsFrame(appointmentService, currentProvider);
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
                org.healthtrack.service.UserService userService = 
                    org.healthtrack.HealthTrackApplication.getContext().getBean(org.healthtrack.service.UserService.class);
                LoginFrame loginFrame = new LoginFrame(userService);
                loginFrame.setVisible(true);
            });
        }
    }
}
