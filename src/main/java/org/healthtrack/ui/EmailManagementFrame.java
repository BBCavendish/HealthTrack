package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.entity.UserEmail;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 邮箱管理页面
 */
public class EmailManagementFrame extends JFrame {
    
    private final UserService userService;
    private final User currentUser;
    private DefaultTableModel tableModel;
    private JTable emailTable;
    
    public EmailManagementFrame(UserService userService, User currentUser) {
        super("HealthTrack - 邮箱管理");
        this.userService = userService;
        this.currentUser = currentUser;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadEmails();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("邮箱管理");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表格面板
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"邮箱地址", "是否主邮箱"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        emailTable = new JTable(tableModel);
        emailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        emailTable.setRowHeight(35);
        emailTable.setFont(UIStyleConstants.FONT_TEXT);
        emailTable.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        emailTable.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        
        JScrollPane scrollPane = new JScrollPane(emailTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        JButton addButton = UIStyleConstants.createModernButton("添加邮箱", UIStyleConstants.SECONDARY_GREEN);
        JButton setPrimaryButton = UIStyleConstants.createModernButton("设为主邮箱", UIStyleConstants.PRIMARY_BLUE);
        JButton deleteButton = UIStyleConstants.createModernButton("删除邮箱", UIStyleConstants.DANGER_RED);
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.ACCENT_ORANGE);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        addButton.addActionListener(e -> showAddEmailDialog());
        setPrimaryButton.addActionListener(e -> setPrimaryEmail());
        deleteButton.addActionListener(e -> deleteEmail());
        refreshButton.addActionListener(e -> loadEmails());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(setPrimaryButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(600, 500);
        setLocationRelativeTo(null);
    }
    
    private void loadEmails() {
        List<UserEmail> emails = userService.getUserEmails(currentUser.getHealthId());
        tableModel.setRowCount(0);
        
        for (UserEmail email : emails) {
            tableModel.addRow(new Object[]{
                email.getEmailAddress(),
                email.getIsPrimary() ? "是" : "否"
            });
        }
    }
    
    private void showAddEmailDialog() {
        JDialog dialog = new JDialog(this, "添加邮箱", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = UIStyleConstants.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel emailLabel = new JLabel("邮箱地址:");
        emailLabel.setFont(UIStyleConstants.FONT_LABEL);
        formPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        JTextField emailField = UIStyleConstants.createModernTextField(20);
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        JCheckBox primaryCheckBox = new JCheckBox("设为主邮箱");
        primaryCheckBox.setFont(UIStyleConstants.FONT_LABEL);
        formPanel.add(primaryCheckBox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入邮箱地址", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (userService.addUserEmail(currentUser.getHealthId(), email, primaryCheckBox.isSelected())) {
                JOptionPane.showMessageDialog(dialog, "邮箱添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadEmails();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void setPrimaryEmail() {
        int row = emailTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要设置的邮箱", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String email = (String) tableModel.getValueAt(row, 0);
        if (userService.setPrimaryEmail(currentUser.getHealthId(), email)) {
            JOptionPane.showMessageDialog(this, "主邮箱设置成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            loadEmails();
        } else {
            JOptionPane.showMessageDialog(this, "设置失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEmail() {
        int row = emailTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的邮箱", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String email = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要删除邮箱 " + email + " 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userService.removeUserEmail(currentUser.getHealthId(), email)) {
                JOptionPane.showMessageDialog(this, "邮箱删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadEmails();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

