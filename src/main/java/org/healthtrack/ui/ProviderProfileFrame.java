package org.healthtrack.ui;

import org.healthtrack.entity.Provider;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 医疗提供者个人信息管理页面
 */
public class ProviderProfileFrame extends JFrame {
    
    private final ProviderService providerService;
    private Provider currentProvider;
    private JTextField nameField;
    private JTextField specialtyField;
    private JTextField phoneField;
    private JLabel licenseNumberLabel;
    
    public ProviderProfileFrame(ProviderService providerService, Provider currentProvider) {
        super("HealthTrack - 个人信息管理");
        this.providerService = providerService;
        this.currentProvider = currentProvider;
        
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("个人信息管理");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表单卡片
        JPanel cardPanel = UIStyleConstants.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        
        // 许可证号（只读）
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel licenseLabel = new JLabel("许可证号:");
        licenseLabel.setFont(UIStyleConstants.FONT_LABEL);
        licenseLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(licenseLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        licenseNumberLabel = new JLabel(currentProvider.getLicenseNumber());
        licenseNumberLabel.setFont(UIStyleConstants.FONT_INPUT);
        licenseNumberLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        licenseNumberLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        licenseNumberLabel.setOpaque(true);
        licenseNumberLabel.setBackground(UIStyleConstants.HEADER_BG);
        cardPanel.add(licenseNumberLabel, gbc);
        
        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel("姓名 *:");
        nameLabel.setFont(UIStyleConstants.FONT_LABEL);
        nameLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        nameField = UIStyleConstants.createModernTextField(20);
        nameField.setText(currentProvider.getName() != null ? currentProvider.getName() : "");
        cardPanel.add(nameField, gbc);
        
        // 专业领域
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel specialtyLabel = new JLabel("专业领域:");
        specialtyLabel.setFont(UIStyleConstants.FONT_LABEL);
        specialtyLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(specialtyLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        specialtyField = UIStyleConstants.createModernTextField(20);
        specialtyField.setText(currentProvider.getSpecialty() != null ? currentProvider.getSpecialty() : "");
        cardPanel.add(specialtyField, gbc);
        
        // 电话
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel phoneLabel = new JLabel("电话:");
        phoneLabel.setFont(UIStyleConstants.FONT_LABEL);
        phoneLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        phoneField = UIStyleConstants.createModernTextField(20);
        phoneField.setText(currentProvider.getPhone() != null ? currentProvider.getPhone() : "");
        cardPanel.add(phoneField, gbc);
        
        // 验证状态（只读）
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        JLabel statusLabel = new JLabel("验证状态:");
        statusLabel.setFont(UIStyleConstants.FONT_LABEL);
        statusLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(statusLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel statusValueLabel = new JLabel(currentProvider.getVerifiedStatus() != null ? currentProvider.getVerifiedStatus() : "Unverified");
        statusValueLabel.setFont(UIStyleConstants.FONT_INPUT);
        statusValueLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        statusValueLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        statusValueLabel.setOpaque(true);
        statusValueLabel.setBackground(UIStyleConstants.HEADER_BG);
        cardPanel.add(statusValueLabel, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProviderInfo();
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
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void saveProviderInfo() {
        String name = nameField.getText().trim();
        String specialty = specialtyField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "姓名不能为空！",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentProvider.setName(name);
        currentProvider.setSpecialty(specialty.isEmpty() ? null : specialty);
        currentProvider.setPhone(phone.isEmpty() ? null : phone);
        
        if (providerService.saveProvider(currentProvider)) {
            JOptionPane.showMessageDialog(this,
                "个人信息保存成功！",
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

