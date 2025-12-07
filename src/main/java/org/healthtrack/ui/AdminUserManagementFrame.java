package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

/**
 * 管理员用户管理界面
 * 提供完整的用户管理功能：查看、添加、编辑、删除
 */
public class AdminUserManagementFrame extends JFrame {

    private final UserService userService;
    private final DefaultTableModel tableModel;
    private final JTable userTable;

    public AdminUserManagementFrame(UserService userService) {
        super("HealthTrack - 管理员用户管理");
        this.userService = userService;
        
        // 初始化表格模型
        this.tableModel = new DefaultTableModel(
                new Object[]{"健康ID", "姓名", "电话号码", "主邮箱", "验证状态", "角色", "家庭ID"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        
        this.userTable = new JTable(tableModel);
        
        // 设置现代外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initUI();
        loadUsers();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        // 主面板
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // 标题区域
        JPanel titlePanel = createTitlePanel();
        
        // 用户表格区域
        JPanel tablePanel = createTablePanel();
        
        // 按钮面板
        JPanel buttonPanel = createButtonPanel();
        
        // 组装界面
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(1100, 650);
        setLocationRelativeTo(null);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // 主标题
        JLabel titleLabel = UIStyleConstants.createTitleLabel("用户管理系统");
        
        // 副标题
        JLabel subtitleLabel = new JLabel("管理员 - 用户管理模块");
        subtitleLabel.setFont(UIStyleConstants.FONT_SUBTITLE);
        subtitleLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 15, 0));
        
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = UIStyleConstants.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        // 表格标题
        JLabel tableTitle = new JLabel("用户列表");
        tableTitle.setFont(UIStyleConstants.FONT_HEADING);
        tableTitle.setForeground(UIStyleConstants.TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(35);
        userTable.setFont(UIStyleConstants.FONT_TEXT);
        userTable.setForeground(UIStyleConstants.TEXT_PRIMARY);
        userTable.setBackground(UIStyleConstants.CARD_BG);
        userTable.setGridColor(UIStyleConstants.BORDER_COLOR);
        
        // 表头样式
        userTable.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        userTable.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        userTable.getTableHeader().setForeground(UIStyleConstants.TEXT_PRIMARY);
        userTable.getTableHeader().setBorder(BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR));
        
        // 选中行样式
        userTable.setSelectionBackground(UIStyleConstants.PRIMARY_BLUE.brighter());
        userTable.setSelectionForeground(Color.BLACK);
        
        // 设置列宽
        TableColumnModel columnModel = userTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120); // 健康ID
        columnModel.getColumn(1).setPreferredWidth(150); // 姓名
        columnModel.getColumn(2).setPreferredWidth(130); // 电话
        columnModel.getColumn(3).setPreferredWidth(200); // 主邮箱
        columnModel.getColumn(4).setPreferredWidth(100); // 验证状态
        columnModel.getColumn(5).setPreferredWidth(100); // 角色
        columnModel.getColumn(6).setPreferredWidth(120); // 家庭ID
        
        // 添加滚动条
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(UIStyleConstants.CARD_BG);
        
        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        panel.setOpaque(false);
        
        // 创建按钮
        JButton refreshButton = UIStyleConstants.createModernButton("刷新列表", UIStyleConstants.PRIMARY_BLUE);
        JButton addButton = UIStyleConstants.createModernButton("添加用户", UIStyleConstants.SECONDARY_GREEN);
        JButton editButton = UIStyleConstants.createModernButton("编辑用户", UIStyleConstants.ACCENT_ORANGE);
        JButton deleteButton = UIStyleConstants.createModernButton("删除用户", UIStyleConstants.DANGER_RED);
        JButton statsButton = UIStyleConstants.createModernButton("统计信息", UIStyleConstants.PURPLE);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        refreshButton.addActionListener(e -> loadUsers());
        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        statsButton.addActionListener(e -> showStats());
        backButton.addActionListener(e -> dispose());
        
        panel.add(refreshButton);
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(statsButton);
        panel.add(backButton);
        
        return panel;
    }

    private void loadUsers() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() {
                return userService.getAllUsers();
            }
            
            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    tableModel.setRowCount(0); // 清空现有数据
                    for (User user : users) {
                        // 获取主邮箱
                        String emailDisplay = "无";
                        try {
                            var primaryEmail = userService.getPrimaryEmail(user.getHealthId());
                            emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无";
                        } catch (Exception e) {
                            emailDisplay = "加载失败";
                        }
                        
                        // 根据状态设置显示
                        String statusDisplay = user.getVerificationStatus();
                        if ("Verified".equals(statusDisplay)) {
                            statusDisplay = "[已验证]";
                        } else if ("Unverified".equals(statusDisplay)) {
                            statusDisplay = "[未验证]";
                        }

                        tableModel.addRow(new Object[]{
                            user.getHealthId(),
                            user.getName(),
                            user.getPhone() != null ? user.getPhone() : "",
                            emailDisplay,
                            statusDisplay,
                            user.getRole() != null ? user.getRole() : "Regular User",
                            user.getFamilyId() != null ? user.getFamilyId() : ""
                        });
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AdminUserManagementFrame.this,
                        "加载用户数据失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "添加新用户", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        // 对话框标题
        JLabel dialogTitle = new JLabel("添加新用户");
        dialogTitle.setFont(UIStyleConstants.FONT_HEADING);
        dialogTitle.setForeground(UIStyleConstants.TEXT_PRIMARY);
        dialogTitle.setHorizontalAlignment(SwingConstants.CENTER);
        dialogTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // 表单面板
        JPanel formPanel = UIStyleConstants.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // 定义角色选项
        String[] roleOptions = {"Regular User", "Administrator"};
        
        JLabel[] labels = {
            new JLabel("健康ID:"),
            new JLabel("姓名:"),
            new JLabel("电话号码:"),
            new JLabel("角色:"),
            new JLabel("家庭ID:")
        };
        
        JTextField healthIdField = UIStyleConstants.createModernTextField(20);
        JTextField nameField = UIStyleConstants.createModernTextField(20);
        JTextField phoneField = UIStyleConstants.createModernTextField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(roleOptions);
        roleComboBox.setFont(UIStyleConstants.FONT_INPUT);
        JTextField familyIdField = UIStyleConstants.createModernTextField(20);
        
        // 添加标签和输入框
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            if (labels[i] instanceof JLabel) {
                ((JLabel) labels[i]).setFont(UIStyleConstants.FONT_LABEL);
                ((JLabel) labels[i]).setForeground(UIStyleConstants.TEXT_PRIMARY);
                formPanel.add(labels[i], gbc);
            }
            
            gbc.gridx = 1;
            gbc.gridy = i;
            switch (i) {
                case 0: formPanel.add(healthIdField, gbc); break;
                case 1: formPanel.add(nameField, gbc); break;
                case 2: formPanel.add(phoneField, gbc); break;
                case 3: formPanel.add(roleComboBox, gbc); break;
                case 4: formPanel.add(familyIdField, gbc); break;
            }
        }
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(e -> {
            // 验证输入
            if (healthIdField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "健康ID和姓名是必填项！", 
                    "输入错误", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            User user = new User();
            user.setHealthId(healthIdField.getText().trim());
            user.setName(nameField.getText().trim());
            user.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
            user.setRole((String) roleComboBox.getSelectedItem());
            user.setVerificationStatus("Unverified");
            user.setFamilyId(familyIdField.getText().trim().isEmpty() ? null : familyIdField.getText().trim());
            
            if (userService.saveUser(user)) {
                JOptionPane.showMessageDialog(dialog, 
                    "用户添加成功！", 
                    "成功", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "添加用户失败！", 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(dialogTitle, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请先选择要编辑的用户",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String healthId = (String) tableModel.getValueAt(selectedRow, 0);
        User user = userService.getUserById(healthId);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                "用户不存在",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "编辑用户", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        JLabel dialogTitle = new JLabel("编辑用户: " + user.getName());
        dialogTitle.setFont(UIStyleConstants.FONT_HEADING);
        dialogTitle.setForeground(UIStyleConstants.TEXT_PRIMARY);
        dialogTitle.setHorizontalAlignment(SwingConstants.CENTER);
        dialogTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = UIStyleConstants.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        String[] roleOptions = {"Regular User", "Administrator"};
        String[] statusOptions = {"Verified", "Unverified"};
        
        JLabel nameLabel = new JLabel("姓名:");
        nameLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField nameField = UIStyleConstants.createModernTextField(20);
        nameField.setText(user.getName());
        
        JLabel phoneLabel = new JLabel("电话号码:");
        phoneLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField phoneField = UIStyleConstants.createModernTextField(20);
        phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        
        JLabel roleLabel = new JLabel("角色:");
        roleLabel.setFont(UIStyleConstants.FONT_LABEL);
        JComboBox<String> roleComboBox = new JComboBox<>(roleOptions);
        roleComboBox.setSelectedItem(user.getRole() != null ? user.getRole() : "Regular User");
        roleComboBox.setFont(UIStyleConstants.FONT_INPUT);
        
        JLabel statusLabel = new JLabel("验证状态:");
        statusLabel.setFont(UIStyleConstants.FONT_LABEL);
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setSelectedItem(user.getVerificationStatus() != null ? user.getVerificationStatus() : "Unverified");
        statusComboBox.setFont(UIStyleConstants.FONT_INPUT);
        
        JLabel familyLabel = new JLabel("家庭ID:");
        familyLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField familyField = UIStyleConstants.createModernTextField(20);
        familyField.setText(user.getFamilyId() != null ? user.getFamilyId() : "");
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(roleLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(statusComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(familyLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(familyField, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(e -> {
            user.setName(nameField.getText().trim());
            user.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
            user.setRole((String) roleComboBox.getSelectedItem());
            user.setVerificationStatus((String) statusComboBox.getSelectedItem());
            user.setFamilyId(familyField.getText().trim().isEmpty() ? null : familyField.getText().trim());
            
            if (userService.saveUser(user)) {
                JOptionPane.showMessageDialog(dialog, "用户更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "更新用户失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(dialogTitle, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "请先选择要删除的用户",
                "提示",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String healthId = (String) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("确定要删除用户 '%s' (%s) 吗？此操作不可撤销！", userName, healthId),
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userService.deleteUser(healthId)) {
                JOptionPane.showMessageDialog(this, 
                    "用户删除成功！", 
                    "成功", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "删除用户失败！", 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showStats() {
        int totalUsers = userService.getTotalUserCount();
        int verifiedUsers = userService.getVerifiedUserCount();
        int unverifiedUsers = totalUsers - verifiedUsers;
        
        String statsMessage = String.format(
            "用户统计信息\n\n" +
            "总用户数: %d\n" +
            "已验证用户: %d\n" +
            "未验证用户: %d\n" +
            "管理员数量: 待统计",
            totalUsers, verifiedUsers, unverifiedUsers
        );
        
        JOptionPane.showMessageDialog(this,
            statsMessage,
            "系统统计",
            JOptionPane.INFORMATION_MESSAGE);
    }
}


