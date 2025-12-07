package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 注册页面
 */
public class RegisterFrame extends JFrame {
    
    private final UserService userService;
    private JTextField healthIdField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JTextField familyIdField;
    
    public RegisterFrame(UserService userService) {
        super("HealthTrack - 注册");
        this.userService = userService;
        
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("创建新账户");
        JLabel subtitleLabel = UIStyleConstants.createSubtitleLabel("填写以下信息完成注册");
        subtitleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // 注册表单卡片
        JPanel cardPanel = UIStyleConstants.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        // 健康ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel healthIdLabel = new JLabel("健康ID *:");
        healthIdLabel.setFont(UIStyleConstants.FONT_LABEL);
        healthIdLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(healthIdLabel, gbc);
        
        gbc.gridx = 1;
        healthIdField = UIStyleConstants.createModernTextField(20);
        cardPanel.add(healthIdField, gbc);
        
        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("姓名 *:");
        nameLabel.setFont(UIStyleConstants.FONT_LABEL);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        nameField = UIStyleConstants.createModernTextField(20);
        cardPanel.add(nameField, gbc);
        
        // 电话号码
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel phoneLabel = new JLabel("电话号码:");
        phoneLabel.setFont(UIStyleConstants.FONT_LABEL);
        phoneLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1;
        phoneField = UIStyleConstants.createModernTextField(20);
        cardPanel.add(phoneField, gbc);
        
        // 邮箱
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel emailLabel = new JLabel("邮箱地址 *:");
        emailLabel.setFont(UIStyleConstants.FONT_LABEL);
        emailLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        emailField = UIStyleConstants.createModernTextField(20);
        cardPanel.add(emailField, gbc);
        
        // 角色
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel roleLabel = new JLabel("角色:");
        roleLabel.setFont(UIStyleConstants.FONT_LABEL);
        roleLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(roleLabel, gbc);
        
        gbc.gridx = 1;
        String[] roles = {"regular user", "administer"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(UIStyleConstants.FONT_INPUT);
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setForeground(UIStyleConstants.TEXT_PRIMARY);
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cardPanel.add(roleComboBox, gbc);
        
        // 家庭ID
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel familyIdLabel = new JLabel("家庭ID:");
        familyIdLabel.setFont(UIStyleConstants.FONT_LABEL);
        familyIdLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(familyIdLabel, gbc);
        
        gbc.gridx = 1;
        familyIdField = UIStyleConstants.createModernTextField(20);
        cardPanel.add(familyIdField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton registerButton = UIStyleConstants.createModernButton("注册", UIStyleConstants.SECONDARY_GREEN);
        JButton backButton = UIStyleConstants.createModernButton("返回登录", UIStyleConstants.TEXT_SECONDARY);
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegister();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        // 组装界面
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(550, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void performRegister() {
        String healthId = healthIdField.getText().trim();
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        String familyId = familyIdField.getText().trim();
        
        // 验证必填项
        if (healthId.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "健康ID、姓名和邮箱地址是必填项！",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 检查用户是否已存在
        if (userService.existsUser(healthId)) {
            JOptionPane.showMessageDialog(this,
                "该健康ID已被注册，请使用其他ID",
                "注册失败",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 创建用户
        User user = new User();
        user.setHealthId(healthId);
        user.setName(name);
        user.setPhone(phone.isEmpty() ? null : phone);
        user.setRole(role);
        user.setVerificationStatus("Unverified");
        user.setFamilyId(familyId.isEmpty() ? null : familyId);
        
        if (userService.saveUser(user)) {
            // 添加邮箱
            userService.addUserEmail(healthId, email, true);
            
            JOptionPane.showMessageDialog(this,
                "注册成功！请返回登录页面登录",
                "注册成功",
                JOptionPane.INFORMATION_MESSAGE);
            
            backToLogin();
        } else {
            JOptionPane.showMessageDialog(this,
                "注册失败，请重试",
                "注册失败",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void backToLogin() {
        this.setVisible(false);
        this.dispose();
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(userService);
            loginFrame.setVisible(true);
        });
    }
}

