package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.UserService;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 管理员菜单页面
 */
public class AdminMenuFrame extends JFrame {
    
    private final UserService userService;
    private final ProviderService providerService;
    private final User currentAdmin;
    
    public AdminMenuFrame(UserService userService, ProviderService providerService, User currentAdmin) {
        super("HealthTrack - 管理员菜单");
        this.userService = userService;
        this.providerService = providerService;
        this.currentAdmin = currentAdmin;
        
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("系统管理平台");
        JLabel welcomeLabel = new JLabel("欢迎，管理员 " + currentAdmin.getName() + " (" + currentAdmin.getHealthId() + ")");
        welcomeLabel.setFont(UIStyleConstants.FONT_SUBTITLE);
        welcomeLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(welcomeLabel, BorderLayout.SOUTH);
        
        // 功能菜单卡片
        JPanel menuPanel = UIStyleConstants.createCardPanel();
        menuPanel.setLayout(new GridLayout(3, 2, 20, 20));
        
        // 用户管理
        JButton userManagementButton = createMenuButton("用户管理", UIStyleConstants.PRIMARY_BLUE, 
            e -> openUserManagementFrame());
        
        // 医疗提供者管理
        JButton providerManagementButton = createMenuButton("医疗提供者管理", UIStyleConstants.SECONDARY_GREEN,
            e -> openProviderManagementFrame());
        
        // 系统统计
        JButton statisticsButton = createMenuButton("系统统计", UIStyleConstants.ACCENT_ORANGE,
            e -> openSystemStatisticsFrame());
        
        // 数据管理
        JButton dataManagementButton = createMenuButton("数据管理", UIStyleConstants.PURPLE,
            e -> openDataManagementFrame());
        
        // 系统设置
        JButton settingsButton = createMenuButton("系统设置", UIStyleConstants.PRIMARY_BLUE,
            e -> openSystemSettingsFrame());
        
        // 日志查看
        JButton logsButton = createMenuButton("日志查看", UIStyleConstants.SECONDARY_GREEN,
            e -> openLogsFrame());
        
        menuPanel.add(userManagementButton);
        menuPanel.add(providerManagementButton);
        menuPanel.add(statisticsButton);
        menuPanel.add(dataManagementButton);
        menuPanel.add(settingsButton);
        menuPanel.add(logsButton);
        
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
        setSize(800, 700);
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
    
    private void openUserManagementFrame() {
        AdminUserManagementFrame frame = new AdminUserManagementFrame(userService);
        frame.setVisible(true);
    }
    
    private void openProviderManagementFrame() {
        AdminProviderManagementFrame frame = new AdminProviderManagementFrame(providerService);
        frame.setVisible(true);
    }
    
    private void openSystemStatisticsFrame() {
        AdminStatisticsFrame frame = new AdminStatisticsFrame(userService, providerService);
        frame.setVisible(true);
    }
    
    private void openDataManagementFrame() {
        JOptionPane.showMessageDialog(this,
            "数据管理功能开发中...",
            "提示",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openSystemSettingsFrame() {
        JOptionPane.showMessageDialog(this,
            "系统设置功能开发中...",
            "提示",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openLogsFrame() {
        JOptionPane.showMessageDialog(this,
            "日志查看功能开发中...",
            "提示",
            JOptionPane.INFORMATION_MESSAGE);
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
