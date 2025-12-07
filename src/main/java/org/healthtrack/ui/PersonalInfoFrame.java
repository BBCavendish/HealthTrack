package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 个人信息修改页面
 */
public class PersonalInfoFrame extends JFrame {
    
    private final UserService userService;
    private User currentUser;
    private JTextField nameField;
    private JComboBox<String> roleComboBox;
    private JTextField familyIdField;
    
    public PersonalInfoFrame(UserService userService, User currentUser) {
        super("HealthTrack - 个人信息修改");
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("个人信息修改");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表单卡片
        JPanel cardPanel = UIStyleConstants.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        
        // 健康ID（只读）
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel healthIdLabel = new JLabel("健康ID:");
        healthIdLabel.setFont(UIStyleConstants.FONT_LABEL);
        healthIdLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(healthIdLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField healthIdField = new JTextField(currentUser.getHealthId());
        healthIdField.setEditable(false);
        healthIdField.setFont(UIStyleConstants.FONT_INPUT);
        healthIdField.setBackground(UIStyleConstants.HEADER_BG);
        healthIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        cardPanel.add(healthIdField, gbc);
        
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
        nameField.setText(currentUser.getName());
        cardPanel.add(nameField, gbc);
        
        // 角色（下拉选择框）
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel roleLabel = new JLabel("角色:");
        roleLabel.setFont(UIStyleConstants.FONT_LABEL);
        roleLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(roleLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        String[] roleOptions = {"Regular User", "Administrator"};
        roleComboBox = new JComboBox<>(roleOptions);
        roleComboBox.setFont(UIStyleConstants.FONT_INPUT);
        String currentRole = currentUser.getRole();
        if (currentRole != null && (currentRole.equals("Regular User") || currentRole.equals("Administrator"))) {
            roleComboBox.setSelectedItem(currentRole);
        } else {
            roleComboBox.setSelectedItem("Regular User");
        }
        cardPanel.add(roleComboBox, gbc);
        
        // 家庭ID
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel familyIdLabel = new JLabel("家庭ID:");
        familyIdLabel.setFont(UIStyleConstants.FONT_LABEL);
        familyIdLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(familyIdLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        familyIdField = UIStyleConstants.createModernTextField(20);
        familyIdField.setText(currentUser.getFamilyId() != null ? currentUser.getFamilyId() : "");
        cardPanel.add(familyIdField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePersonalInfo();
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
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void savePersonalInfo() {
        String name = nameField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        String familyId = familyIdField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "姓名不能为空！",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentUser.setName(name);
        currentUser.setRole(role);
        currentUser.setFamilyId(familyId.isEmpty() ? null : familyId);
        
        if (userService.saveUser(currentUser)) {
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

