package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.entity.Provider;
import org.healthtrack.service.UserService;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录页面 - 支持User、Provider两种角色登录
 * 管理员登录：在用户登录中，邮箱为admin，userID为123456时自动进入管理员界面
 */
public class LoginFrame extends JFrame {
    
    private final UserService userService;
    private ProviderService providerService;
    private JTextField emailField;
    private JPasswordField passwordField; // 根据角色不同，可以是健康ID或执照号
    private JComboBox<String> roleComboBox;
    private JLabel passwordLabel; // 动态标签，显示"健康ID"或"执照号"
    
    public LoginFrame(UserService userService) {
        super("HealthTrack - 登录");
        this.userService = userService;
        
        // 从Spring上下文获取ProviderService
        try {
            org.springframework.context.ApplicationContext context = 
                org.healthtrack.HealthTrackApplication.getContext();
            if (context != null) {
                this.providerService = context.getBean(ProviderService.class);
            }
        } catch (Exception e) {
            System.err.println("获取ProviderService失败: " + e.getMessage());
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
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // 标题区域
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("健康追踪系统");
        JLabel subtitleLabel = UIStyleConstants.createSubtitleLabel("欢迎回来，请登录您的账户");
        subtitleLabel.setBorder(new EmptyBorder(10, 0, 30, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // 登录表单卡片 - 使用更简单的布局确保间距
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(UIStyleConstants.CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(30, 25, 30, 25)  // 上下30像素，左右25像素的内边距
        ));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        // 设置首选大小，确保有一个合适的初始宽度
        cardPanel.setPreferredSize(new Dimension(450, 280));
        
        // 添加顶部间距
        cardPanel.add(Box.createVerticalStrut(10));
        
        // 角色选择行
        JPanel rolePanel = new JPanel(new BorderLayout(10, 0));
        rolePanel.setOpaque(false);
        rolePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel roleLabel = new JLabel("登录角色:");
        roleLabel.setFont(UIStyleConstants.FONT_LABEL);
        roleLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        roleLabel.setPreferredSize(new Dimension(80, 35));
        
        roleComboBox = new JComboBox<>(new String[]{"用户 (User)", "医疗提供者 (Provider)"});
        roleComboBox.setFont(UIStyleConstants.FONT_INPUT);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setPreferredSize(new Dimension(300, 35));
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        roleComboBox.addActionListener(e -> updatePasswordLabel());
        
        rolePanel.add(roleLabel, BorderLayout.WEST);
        rolePanel.add(roleComboBox, BorderLayout.CENTER);
        cardPanel.add(rolePanel);
        
        // 行间距
        cardPanel.add(Box.createVerticalStrut(20));
        
        // 邮箱输入行
        JPanel emailPanel = new JPanel(new BorderLayout(10, 0));
        emailPanel.setOpaque(false);
        emailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel emailLabel = new JLabel("邮箱地址:");
        emailLabel.setFont(UIStyleConstants.FONT_LABEL);
        emailLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        emailLabel.setPreferredSize(new Dimension(80, 35));
        
        emailField = new JTextField();
        emailField.setFont(UIStyleConstants.FONT_INPUT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        emailField.setBackground(Color.WHITE);
        emailField.setPreferredSize(new Dimension(300, 35));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        emailPanel.add(emailLabel, BorderLayout.WEST);
        emailPanel.add(emailField, BorderLayout.CENTER);
        cardPanel.add(emailPanel);
        
        // 行间距
        cardPanel.add(Box.createVerticalStrut(20));
        
        // 密码/ID输入行（动态标签）
        JPanel passwordPanel = new JPanel(new BorderLayout(10, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        passwordLabel = new JLabel("健康ID:");
        passwordLabel.setFont(UIStyleConstants.FONT_LABEL);
        passwordLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        passwordLabel.setPreferredSize(new Dimension(80, 35));
        
        passwordField = new JPasswordField();
        passwordField.setFont(UIStyleConstants.FONT_INPUT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setBackground(Color.WHITE);
        passwordField.setEchoChar('•');
        passwordField.setPreferredSize(new Dimension(300, 35));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        cardPanel.add(passwordPanel);
        
        // 添加底部间距
        cardPanel.add(Box.createVerticalStrut(10));
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton loginButton = UIStyleConstants.createModernButton("登录", UIStyleConstants.PRIMARY_BLUE);
        JButton registerButton = UIStyleConstants.createModernButton("注册", UIStyleConstants.SECONDARY_GREEN);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterFrame();
            }
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        // 组装界面
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // 使用 pack() 自动计算窗口大小，根据内容自动扩展
        pack();
        
        // 设置最小窗口大小，防止窗口太小
        setMinimumSize(new Dimension(500, 400));
        
        // 设置窗口居中显示
        setLocationRelativeTo(null);
        
        // 允许用户调整窗口大小
        setResizable(true);
    }
    
    private void updatePasswordLabel() {
        String selectedRole = (String) roleComboBox.getSelectedItem();
        if (selectedRole != null) {
            if (selectedRole.contains("Provider")) {
                passwordLabel.setText("执照号:");
            } else {
                passwordLabel.setText("健康ID:");
            }
        }
    }
    
    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String selectedRole = (String) roleComboBox.getSelectedItem();
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "请输入邮箱地址和" + passwordLabel.getText().replace(":", ""),
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedRole == null) {
            JOptionPane.showMessageDialog(this,
                "请选择登录角色",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 根据选择的角色进行不同的登录验证
        if (selectedRole.contains("Provider")) {
            // 医疗提供者登录
            if (providerService == null) {
                JOptionPane.showMessageDialog(this,
                    "ProviderService未初始化，请重启应用程序",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Provider provider = providerService.getProviderByEmail(email);
            if (provider != null && provider.getLicenseNumber().equals(password)) {
                // 登录成功，打开提供者菜单
                this.setVisible(false);
                this.dispose();
                
                SwingUtilities.invokeLater(() -> {
                    ProviderMenuFrame providerMenu = new ProviderMenuFrame(providerService, provider);
                    providerMenu.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this,
                    "邮箱地址或执照号错误，请重试",
                    "登录失败",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // 普通用户登录
            // 首先检查是否为管理员登录（邮箱为admin，userID为123456）
            if ("admin".equalsIgnoreCase(email) && "123456".equals(password)) {
                // 管理员登录，需要获取ProviderService
                if (providerService == null) {
                    // 尝试再次获取ProviderService
                    try {
                        org.springframework.context.ApplicationContext context = 
                            org.healthtrack.HealthTrackApplication.getContext();
                        if (context != null) {
                            this.providerService = context.getBean(ProviderService.class);
                        }
                    } catch (Exception e) {
                        System.err.println("获取ProviderService失败: " + e.getMessage());
                    }
                    
                    if (providerService == null) {
                        JOptionPane.showMessageDialog(this,
                            "ProviderService未初始化，请重启应用程序",
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // 创建管理员用户对象
                User adminUser = new User();
                adminUser.setHealthId("123456");
                adminUser.setName("系统管理员");
                adminUser.setRole("Administrator");
                
                // 登录成功，打开管理员菜单
                this.setVisible(false);
                this.dispose();
                
                SwingUtilities.invokeLater(() -> {
                    AdminMenuFrame adminMenu = new AdminMenuFrame(userService, providerService, adminUser);
                    adminMenu.setVisible(true);
                });
                return;
            }
            
            // 普通用户登录验证
            User user = userService.getUserByEmail(email);
            if (user != null && user.getHealthId().equals(password)) {
                // 登录成功，打开主菜单
                this.setVisible(false);
                this.dispose();
                
                SwingUtilities.invokeLater(() -> {
                    MainMenuFrame mainMenu = new MainMenuFrame(userService, user);
                    mainMenu.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this,
                    "邮箱地址或健康ID错误，请重试",
                    "登录失败",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void openRegisterFrame() {
        this.setVisible(false);
        this.dispose();
        
        SwingUtilities.invokeLater(() -> {
            RegisterFrame registerFrame = new RegisterFrame(userService);
            registerFrame.setVisible(true);
        });
    }
}

