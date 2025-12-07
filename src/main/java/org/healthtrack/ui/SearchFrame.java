package org.healthtrack.ui;

import org.healthtrack.HealthTrackApplication;
import org.healthtrack.entity.User;
import org.healthtrack.service.AppointmentService;
import org.healthtrack.service.HealthReportService;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * 搜索记录页面
 */
public class SearchFrame extends JFrame {
    
    private final AppointmentService appointmentService;
    private final ProviderService providerService;
    private final HealthReportService healthReportService;
    private final User currentUser;
    
    public SearchFrame(AppointmentService appointmentService,
                     ProviderService providerService,
                     HealthReportService healthReportService,
                     User currentUser) {
        super("HealthTrack - 搜索记录");
        this.appointmentService = appointmentService;
        this.providerService = providerService;
        this.healthReportService = healthReportService;
        this.currentUser = currentUser;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("搜索记录");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 菜单按钮
        JPanel menuPanel = UIStyleConstants.createCardPanel();
        menuPanel.setLayout(new GridLayout(3, 2, 20, 20));
        
        JButton appointmentSearchButton = createMenuButton("预约搜索", UIStyleConstants.PRIMARY_BLUE,
            e -> openAppointmentSearchFrame());
        
        JButton providerSearchButton = createMenuButton("提供者搜索", UIStyleConstants.SECONDARY_GREEN,
            e -> openProviderSearchFrame());
        
        JButton healthDataSearchButton = createMenuButton("健康数据搜索", UIStyleConstants.ACCENT_ORANGE,
            e -> openHealthDataSearchFrame());
        
        JButton indicatorStatsButton = createMenuButton("健康指标统计", UIStyleConstants.PURPLE,
            e -> openIndicatorStatsFrame());
        
        JButton popularChallengesButton = createMenuButton("最受欢迎挑战", UIStyleConstants.ACCENT_ORANGE,
            e -> openPopularChallengesFrame());
        
        JButton activeUsersButton = createMenuButton("最活跃用户", UIStyleConstants.SECONDARY_GREEN,
            e -> openActiveUsersFrame());
        
        menuPanel.add(appointmentSearchButton);
        menuPanel.add(providerSearchButton);
        menuPanel.add(healthDataSearchButton);
        menuPanel.add(indicatorStatsButton);
        menuPanel.add(popularChallengesButton);
        menuPanel.add(activeUsersButton);
        
        // 返回按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setOpaque(false);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        backButton.addActionListener(e -> dispose());
        bottomPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(700, 550);
        setLocationRelativeTo(null);
    }
    
    private JButton createMenuButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        button.setBackground(color);
        
        // 根据背景色亮度自动选择文字颜色
        double brightness = (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
        if (brightness > 128) {
            button.setForeground(UIStyleConstants.TEXT_PRIMARY);
        } else {
            button.setForeground(UIStyleConstants.TEXT_PRIMARY);
        }
        
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(40, 20, 40, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        
        final Color originalColor = color;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color hoverColor = originalColor.brighter();
                button.setBackground(hoverColor);
                double hoverBrightness = (hoverColor.getRed() * 0.299 + hoverColor.getGreen() * 0.587 + hoverColor.getBlue() * 0.114);
                if (hoverBrightness > 128) {
                    button.setForeground(UIStyleConstants.TEXT_PRIMARY);
                } else {
                    button.setForeground(UIStyleConstants.TEXT_PRIMARY);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                double brightness = (originalColor.getRed() * 0.299 + originalColor.getGreen() * 0.587 + originalColor.getBlue() * 0.114);
                if (brightness > 128) {
                    button.setForeground(UIStyleConstants.TEXT_PRIMARY);
                } else {
                    button.setForeground(UIStyleConstants.TEXT_PRIMARY);
                }
            }
        });
        
        return button;
    }
    
    private void openAppointmentSearchFrame() {
        ProviderService providerService = HealthTrackApplication.getContext().getBean(ProviderService.class);
        AppointmentSearchFrame frame = new AppointmentSearchFrame(appointmentService, providerService, currentUser);
        frame.setVisible(true);
    }
    
    private void openProviderSearchFrame() {
        ProviderSearchFrame frame = new ProviderSearchFrame(providerService);
        frame.setVisible(true);
    }
    
    private void openHealthDataSearchFrame() {
        HealthDataSearchFrame frame = new HealthDataSearchFrame(healthReportService, currentUser);
        frame.setVisible(true);
    }
    
    private void openIndicatorStatsFrame() {
        HealthIndicatorStatsFrame frame = new HealthIndicatorStatsFrame(healthReportService, currentUser);
        frame.setVisible(true);
    }
    
    private void openPopularChallengesFrame() {
        PopularChallengesFrame frame = new PopularChallengesFrame(currentUser);
        frame.setVisible(true);
    }
    
    private void openActiveUsersFrame() {
        ActiveUsersFrame frame = new ActiveUsersFrame(currentUser);
        frame.setVisible(true);
    }
}

