package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 电话管理页面
 */
public class PhoneManagementFrame extends JFrame {
    
    private final UserService userService;
    private final User currentUser;
    private JTextField phoneField;
    
    public PhoneManagementFrame(UserService userService, User currentUser) {
        super("HealthTrack - 电话管理");
        this.userService = userService;
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("电话管理");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表单卡片
        JPanel cardPanel = UIStyleConstants.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel phoneLabel = new JLabel("电话号码:");
        phoneLabel.setFont(UIStyleConstants.FONT_LABEL);
        phoneLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        phoneField = UIStyleConstants.createModernTextField(20);
        phoneField.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        cardPanel.add(phoneField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePhone();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void savePhone() {
        String phone = phoneField.getText().trim();
        currentUser.setPhone(phone.isEmpty() ? null : phone);
        
        if (userService.saveUser(currentUser)) {
            JOptionPane.showMessageDialog(this,
                "电话号码保存成功！",
                "成功",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "保存失败，请重试",
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

