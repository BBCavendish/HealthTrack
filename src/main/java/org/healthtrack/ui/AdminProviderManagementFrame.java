package org.healthtrack.ui;

import org.healthtrack.entity.Provider;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 管理员医疗提供者管理界面
 * 提供完整的提供者管理功能：查看、添加、编辑、删除
 */
public class AdminProviderManagementFrame extends JFrame {
    
    private final ProviderService providerService;
    private DefaultTableModel tableModel;
    private JTable providerTable;
    
    public AdminProviderManagementFrame(ProviderService providerService) {
        super("HealthTrack - 管理员医疗提供者管理");
        this.providerService = providerService;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadProviders();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("医疗提供者管理系统");
        JLabel subtitleLabel = new JLabel("管理员 - 提供者管理模块");
        subtitleLabel.setFont(UIStyleConstants.FONT_SUBTITLE);
        subtitleLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 15, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // 表格面板
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        JLabel tableTitle = new JLabel("提供者列表");
        tableTitle.setFont(UIStyleConstants.FONT_HEADING);
        tableTitle.setForeground(UIStyleConstants.TEXT_PRIMARY);
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        tableModel = new DefaultTableModel(
            new Object[]{"许可证号", "姓名", "专业领域", "电话", "主邮箱", "验证状态"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        providerTable = new JTable(tableModel);
        providerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        providerTable.setRowHeight(35);
        providerTable.setFont(UIStyleConstants.FONT_TEXT);
        providerTable.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        providerTable.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        providerTable.getTableHeader().setForeground(UIStyleConstants.TEXT_PRIMARY);
        providerTable.setSelectionBackground(UIStyleConstants.PRIMARY_BLUE.brighter());
        providerTable.setSelectionForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(providerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR));
        
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.PRIMARY_BLUE);
        JButton addButton = UIStyleConstants.createModernButton("添加提供者", UIStyleConstants.SECONDARY_GREEN);
        JButton editButton = UIStyleConstants.createModernButton("编辑提供者", UIStyleConstants.ACCENT_ORANGE);
        JButton deleteButton = UIStyleConstants.createModernButton("删除提供者", UIStyleConstants.DANGER_RED);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        refreshButton.addActionListener(e -> loadProviders());
        addButton.addActionListener(e -> showAddProviderDialog());
        editButton.addActionListener(e -> showEditProviderDialog());
        deleteButton.addActionListener(e -> deleteSelectedProvider());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }
    
    private void loadProviders() {
        SwingWorker<List<Provider>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Provider> doInBackground() {
                return providerService.getAllProviders();
            }
            
            @Override
            protected void done() {
                try {
                    List<Provider> providers = get();
                    tableModel.setRowCount(0);
                    
                    for (Provider provider : providers) {
                        // 获取主邮箱
                        String emailDisplay = "无";
                        try {
                            var primaryEmail = providerService.getPrimaryProviderEmail(provider.getLicenseNumber());
                            emailDisplay = primaryEmail != null ? primaryEmail.getEmailAddress() : "无";
                        } catch (Exception e) {
                            emailDisplay = "加载失败";
                        }
                        
                        tableModel.addRow(new Object[]{
                            provider.getLicenseNumber(),
                            provider.getName(),
                            provider.getSpecialty() != null ? provider.getSpecialty() : "",
                            provider.getPhone() != null ? provider.getPhone() : "",
                            emailDisplay,
                            provider.getVerifiedStatus() != null ? provider.getVerifiedStatus() : "Unverified"
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AdminProviderManagementFrame.this,
                        "加载提供者数据失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void showAddProviderDialog() {
        JDialog dialog = new JDialog(this, "添加新提供者", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        JLabel dialogTitle = new JLabel("添加新医疗提供者");
        dialogTitle.setFont(UIStyleConstants.FONT_HEADING);
        dialogTitle.setForeground(UIStyleConstants.TEXT_PRIMARY);
        dialogTitle.setHorizontalAlignment(SwingConstants.CENTER);
        dialogTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = UIStyleConstants.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        JLabel licenseLabel = new JLabel("许可证号:");
        licenseLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField licenseField = UIStyleConstants.createModernTextField(20);
        
        JLabel nameLabel = new JLabel("姓名:");
        nameLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField nameField = UIStyleConstants.createModernTextField(20);
        
        JLabel specialtyLabel = new JLabel("专业领域:");
        specialtyLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField specialtyField = UIStyleConstants.createModernTextField(20);
        
        JLabel phoneLabel = new JLabel("电话:");
        phoneLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField phoneField = UIStyleConstants.createModernTextField(20);
        
        String[] statusOptions = {"Verified", "Unverified"};
        JLabel statusLabel = new JLabel("验证状态:");
        statusLabel.setFont(UIStyleConstants.FONT_LABEL);
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setFont(UIStyleConstants.FONT_INPUT);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(licenseLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(licenseField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(specialtyLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(specialtyField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(statusComboBox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(e -> {
            String licenseNumber = licenseField.getText().trim();
            String name = nameField.getText().trim();
            
            if (licenseNumber.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "许可证号和姓名是必填项", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Provider provider = new Provider();
            provider.setLicenseNumber(licenseNumber);
            provider.setName(name);
            provider.setSpecialty(specialtyField.getText().trim().isEmpty() ? null : specialtyField.getText().trim());
            provider.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
            provider.setVerifiedStatus((String) statusComboBox.getSelectedItem());
            
            if (providerService.saveProvider(provider)) {
                JOptionPane.showMessageDialog(dialog, "提供者添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadProviders();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加提供者失败！", "错误", JOptionPane.ERROR_MESSAGE);
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
    
    private void showEditProviderDialog() {
        int selectedRow = providerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的提供者", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String licenseNumber = (String) tableModel.getValueAt(selectedRow, 0);
        Provider provider = providerService.getProviderById(licenseNumber);
        
        if (provider == null) {
            JOptionPane.showMessageDialog(this, "提供者不存在", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "编辑提供者", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        JLabel dialogTitle = new JLabel("编辑提供者: " + provider.getName());
        dialogTitle.setFont(UIStyleConstants.FONT_HEADING);
        dialogTitle.setForeground(UIStyleConstants.TEXT_PRIMARY);
        dialogTitle.setHorizontalAlignment(SwingConstants.CENTER);
        dialogTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = UIStyleConstants.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        JLabel nameLabel = new JLabel("姓名:");
        nameLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField nameField = UIStyleConstants.createModernTextField(20);
        nameField.setText(provider.getName());
        
        JLabel specialtyLabel = new JLabel("专业领域:");
        specialtyLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField specialtyField = UIStyleConstants.createModernTextField(20);
        specialtyField.setText(provider.getSpecialty() != null ? provider.getSpecialty() : "");
        
        JLabel phoneLabel = new JLabel("电话:");
        phoneLabel.setFont(UIStyleConstants.FONT_LABEL);
        JTextField phoneField = UIStyleConstants.createModernTextField(20);
        phoneField.setText(provider.getPhone() != null ? provider.getPhone() : "");
        
        String[] statusOptions = {"Verified", "Unverified"};
        JLabel statusLabel = new JLabel("验证状态:");
        statusLabel.setFont(UIStyleConstants.FONT_LABEL);
        JComboBox<String> statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setSelectedItem(provider.getVerifiedStatus() != null ? provider.getVerifiedStatus() : "Unverified");
        statusComboBox.setFont(UIStyleConstants.FONT_INPUT);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(specialtyLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(specialtyField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(statusComboBox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(e -> {
            provider.setName(nameField.getText().trim());
            provider.setSpecialty(specialtyField.getText().trim().isEmpty() ? null : specialtyField.getText().trim());
            provider.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
            provider.setVerifiedStatus((String) statusComboBox.getSelectedItem());
            
            if (providerService.saveProvider(provider)) {
                JOptionPane.showMessageDialog(dialog, "提供者更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadProviders();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "更新提供者失败！", "错误", JOptionPane.ERROR_MESSAGE);
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
    
    private void deleteSelectedProvider() {
        int row = providerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要删除的提供者", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String licenseNumber = (String) tableModel.getValueAt(row, 0);
        String providerName = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("确定要删除提供者 '%s' (%s) 吗？此操作不可撤销！", providerName, licenseNumber),
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (providerService.deleteProvider(licenseNumber)) {
                JOptionPane.showMessageDialog(this, "提供者删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadProviders();
            } else {
                JOptionPane.showMessageDialog(this, "删除提供者失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}



