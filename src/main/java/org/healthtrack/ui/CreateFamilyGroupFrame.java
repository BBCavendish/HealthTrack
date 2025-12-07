package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 家庭组创建页面
 */
public class CreateFamilyGroupFrame extends JFrame {
    
    private final UserService userService;
    private final User currentUser;
    private JTextField familyIdField;
    
    public CreateFamilyGroupFrame(UserService userService, User currentUser) {
        super("HealthTrack - 家庭组创建");
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("创建家庭组");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表单卡片
        JPanel cardPanel = UIStyleConstants.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel familyIdLabel = new JLabel("家庭ID *:");
        familyIdLabel.setFont(UIStyleConstants.FONT_LABEL);
        cardPanel.add(familyIdLabel, gbc);
        
        gbc.gridx = 1;
        familyIdField = UIStyleConstants.createModernTextField(20);
        cardPanel.add(familyIdField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton createButton = UIStyleConstants.createModernButton("创建", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFamilyGroup();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void createFamilyGroup() {
        String familyId = familyIdField.getText().trim();
        
        if (familyId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "家庭ID不能为空！",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentUser.setFamilyId(familyId);
        if (userService.saveUser(currentUser)) {
            JOptionPane.showMessageDialog(this,
                "家庭组创建成功！",
                "成功",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "创建失败，请重试",
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

