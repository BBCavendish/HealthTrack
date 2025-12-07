package org.healthtrack.ui;

import org.healthtrack.entity.Provider;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 提供者搜索页面 - 完善版
 */
public class ProviderSearchFrame extends JFrame {
    
    private final ProviderService providerService;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField specialtyField;
    private JComboBox<String> statusCombo;
    
    public ProviderSearchFrame(ProviderService providerService) {
        super("HealthTrack - 提供者搜索");
        this.providerService = providerService;
        
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("提供者搜索");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 搜索条件面板
        JPanel searchPanel = UIStyleConstants.createCardPanel();
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        // 姓名搜索
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel nameLabel = new JLabel("姓名:");
        nameLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        nameField = UIStyleConstants.createModernTextField(20);
        searchPanel.add(nameField, gbc);
        
        // 专业领域搜索
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel specialtyLabel = new JLabel("专业领域:");
        specialtyLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(specialtyLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        specialtyField = UIStyleConstants.createModernTextField(20);
        searchPanel.add(specialtyField, gbc);
        
        // 验证状态
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel statusLabel = new JLabel("验证状态:");
        statusLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(statusLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        statusCombo = new JComboBox<>(new String[]{"全部", "Verified", "Unverified"});
        statusCombo.setFont(UIStyleConstants.FONT_INPUT);
        searchPanel.add(statusCombo, gbc);
        
        // 搜索按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        JButton searchButton = UIStyleConstants.createModernButton("搜索", UIStyleConstants.PRIMARY_BLUE);
        JButton resetButton = UIStyleConstants.createModernButton("重置", UIStyleConstants.ACCENT_ORANGE);
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        
        searchButton.addActionListener(e -> performSearch());
        resetButton.addActionListener(e -> resetSearch());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(closeButton);
        
        // 结果表格
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"许可证号", "姓名", "专业领域", "电话", "验证状态"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(UIStyleConstants.FONT_TEXT);
        table.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        table.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 布局
        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setOpaque(false);
        searchContainer.add(searchPanel, BorderLayout.CENTER);
        searchContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(searchContainer, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void performSearch() {
        tableModel.setRowCount(0);
        
        if (providerService == null) {
            return;
        }
        
        String name = nameField.getText().trim();
        String specialty = specialtyField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        if ("全部".equals(status)) {
            status = null;
        }
        
        // 获取所有提供者
        List<Provider> providers = providerService.getAllProviders();
        
        // 应用搜索条件
        for (Provider provider : providers) {
            // 姓名过滤
            if (!name.isEmpty() && (provider.getName() == null || 
                !provider.getName().toLowerCase().contains(name.toLowerCase()))) {
                continue;
            }
            
            // 专业领域过滤
            if (!specialty.isEmpty() && (provider.getSpecialty() == null || 
                !provider.getSpecialty().toLowerCase().contains(specialty.toLowerCase()))) {
                continue;
            }
            
            // 验证状态过滤
            if (status != null && (provider.getVerifiedStatus() == null || 
                !provider.getVerifiedStatus().equals(status))) {
                continue;
            }
            
            // 添加到结果表格
            tableModel.addRow(new Object[]{
                provider.getLicenseNumber(),
                provider.getName(),
                provider.getSpecialty() != null ? provider.getSpecialty() : "",
                provider.getPhone() != null ? provider.getPhone() : "",
                provider.getVerifiedStatus() != null ? provider.getVerifiedStatus() : ""
            });
        }
        
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "未找到符合条件的提供者", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetSearch() {
        nameField.setText("");
        specialtyField.setText("");
        statusCombo.setSelectedIndex(0);
        tableModel.setRowCount(0);
    }
}
