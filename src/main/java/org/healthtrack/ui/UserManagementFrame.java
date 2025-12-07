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
 * Modern desktop UI for managing users with beautiful color scheme.
 */
public class UserManagementFrame extends JFrame {

    // 现代配色方案 - 专业健康管理系统风格
    private static final Color PRIMARY_BLUE = new Color(52, 152, 219);     // 主色调 - 蓝色
    private static final Color SECONDARY_GREEN = new Color(46, 204, 113);  // 成功/添加 - 绿色
    private static final Color ACCENT_ORANGE = new Color(230, 126, 34);    // 强调色 - 橙色
    private static final Color DANGER_RED = new Color(231, 76, 60);        // 危险/删除 - 红色
    private static final Color PURPLE = new Color(155, 89, 182);           // 功能色 - 紫色
    private static final Color BACKGROUND = new Color(245, 247, 250);      // 背景色 - 浅灰蓝
    private static final Color CARD_BG = new Color(255, 255, 255);         // 卡片背景 - 白色
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);       // 主要文字 - 深灰蓝
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);  // 次要文字 - 灰色
    private static final Color HEADER_BG = new Color(236, 240, 241);       // 表头背景 - 浅灰
    private static final Color BORDER_COLOR = new Color(220, 224, 228);    // 边框色 - 浅灰
    
    private final UserService userService;
    private final DefaultTableModel tableModel;
    private final JTable userTable;

    public UserManagementFrame(UserService userService) {
        super("HealthTrack - 用户管理系统");
        this.userService = userService;
        
        // 正确初始化表格模型（包含主邮箱列）
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 设置窗口背景
        getContentPane().setBackground(BACKGROUND);
        
        // 主面板 - 使用渐变背景
        GradientPanel mainPanel = new GradientPanel(new BorderLayout(15, 15));
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
        JLabel titleLabel = new JLabel("健康追踪系统");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // 副标题
        JLabel subtitleLabel = new JLabel("用户管理模块");
        subtitleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 15, 0));
        
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // 表格标题
        JLabel tableTitle = new JLabel("用户列表");
        tableTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(35);
        userTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        userTable.setForeground(TEXT_PRIMARY);
        userTable.setBackground(CARD_BG);
        userTable.setGridColor(BORDER_COLOR);
        
        // 表头样式
        userTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(HEADER_BG);
        userTable.getTableHeader().setForeground(TEXT_PRIMARY);
        userTable.getTableHeader().setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        // 选中行样式
        userTable.setSelectionBackground(PRIMARY_BLUE.brighter());
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
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(CARD_BG);
        
        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        panel.setOpaque(false);
        
        // 创建现代化按钮 - 修复：为深色按钮使用白色文字
        JButton refreshButton = createModernButton("刷新列表", PRIMARY_BLUE);
        JButton addButton = createModernButton("添加用户", SECONDARY_GREEN);
        JButton deleteButton = createModernButton("删除用户", DANGER_RED);
        JButton exportButton = createModernButton("导出数据", PURPLE);
        JButton statsButton = createModernButton("统计信息", ACCENT_ORANGE);
        
        refreshButton.addActionListener(e -> loadUsers());
        addButton.addActionListener(e -> showAddUserDialog());
        deleteButton.addActionListener(e -> deleteSelectedUser());
        exportButton.addActionListener(e -> exportData());
        statsButton.addActionListener(e -> showStats());
        
        panel.add(refreshButton);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(exportButton);
        panel.add(statsButton);
        
        return panel;
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        
        // 统一使用白色文字，因为这些颜色都比较深
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(UIStyleConstants.TEXT_PRIMARY);
        button.setFocusPainted(false);
        
        // 固定边框，不随鼠标悬停改变
        int borderThickness = 1;
        int padding = 10;
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), borderThickness),
            BorderFactory.createEmptyBorder(padding, 25, padding, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 鼠标悬停效果 - 只改变背景色，不改变边框大小
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
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
                        String emailDisplay = null;
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
                            user.getPhone(),
                            emailDisplay,
                            statusDisplay,
                            user.getRole(),
                            user.getFamilyId()
                        });
                    }
                    
                    // 在状态栏显示加载信息
                    showStatusMessage("加载完成，共 " + users.size() + " 条记录");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    showStatusMessage("加载用户数据失败: " + e.getMessage());
                    JOptionPane.showMessageDialog(UserManagementFrame.this,
                        "加载用户数据失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void showStatusMessage(String message) {
        System.out.println(message);
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "添加新用户", true);
        dialog.getContentPane().setBackground(BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        // 对话框标题
        JLabel dialogTitle = new JLabel("添加新用户");
        dialogTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        dialogTitle.setForeground(TEXT_PRIMARY);
        dialogTitle.setHorizontalAlignment(SwingConstants.CENTER);
        dialogTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // 定义角色选项
        String[] roleOptions = {"administer", "regular user"};
        
        JLabel[] labels = {
            new JLabel("健康ID:"),
            new JLabel("姓名:"),
            new JLabel("电话号码:"),
            new JLabel("角色:"),
            new JLabel("家庭ID:")
        };
        
        JTextField healthIdField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(roleOptions);
        JTextField familyIdField = new JTextField(20);
        
        JComponent[] fields = {
            healthIdField,
            nameField,
            phoneField,
            roleComboBox,
            familyIdField
        };
        
        // 设置输入框样式
        for (int i = 0; i < fields.length; i++) {
            JComponent field = fields[i];
            
            if (field instanceof JTextField) {
                JTextField textField = (JTextField) field;
                textField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                textField.setForeground(TEXT_PRIMARY);
            } else if (field instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) field;
                comboBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                comboBox.setBackground(Color.WHITE);
                comboBox.setForeground(TEXT_PRIMARY);
                comboBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                
                // 设置下拉列表样式
                comboBox.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value,
                            int index, boolean isSelected, boolean cellHasFocus) {
                        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
                        label.setForeground(TEXT_PRIMARY);
                        return label;
                    }
                });
            }
        }
        
        // 添加标签和输入框
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            labels[i].setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            labels[i].setForeground(TEXT_PRIMARY);
            formPanel.add(labels[i], gbc);
            
            gbc.gridx = 1;
            gbc.gridy = i;
            formPanel.add(fields[i], gbc);
        }
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // 创建对话框按钮
        JButton saveButton = createModernButton("保存", SECONDARY_GREEN);
        JButton cancelButton = createModernButton("取消", DANGER_RED);
        
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
            user.setPhone(phoneField.getText().trim());
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
    
    private void exportData() {
        // 模拟导出进度
        JOptionPane.showMessageDialog(this,
            "数据导出功能开发中...",
            "提示",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showStats() {
        int rowCount = tableModel.getRowCount();
        JOptionPane.showMessageDialog(this,
            String.format("统计信息\n\n总用户数: %d\n已验证用户: 待统计\n未验证用户: 待统计", rowCount),
            "系统统计",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // 渐变背景面板
    private class GradientPanel extends JPanel {
        public GradientPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // 创建渐变背景
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(245, 247, 250),
                getWidth(), getHeight(), new Color(228, 233, 240)
            );
            
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}