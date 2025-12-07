package org.healthtrack.ui;

import org.healthtrack.entity.Provider;
import org.healthtrack.entity.User;
import org.healthtrack.entity.UserProviderLink;
import org.healthtrack.service.ProviderService;
import org.healthtrack.service.UserProviderLinkService;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * 医疗服务提供者管理页面
 */
public class ProviderManagementFrame extends JFrame {
    
    private final UserService userService;
    private final ProviderService providerService;
    private final UserProviderLinkService userProviderLinkService;
    private final User currentUser;
    private DefaultTableModel tableModel;
    private JTable providerTable;
    
    public ProviderManagementFrame(UserService userService,
                                   ProviderService providerService,
                                   UserProviderLinkService userProviderLinkService,
                                   User currentUser) {
        super("HealthTrack - 医疗服务提供者管理");
        this.userService = userService;
        this.providerService = providerService;
        this.userProviderLinkService = userProviderLinkService;
        this.currentUser = currentUser;
        
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("医疗服务提供者管理");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表格面板
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
        
        providerTable = new JTable(tableModel);
        providerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        providerTable.setRowHeight(35);
        providerTable.setFont(UIStyleConstants.FONT_TEXT);
        providerTable.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        providerTable.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        
        JScrollPane scrollPane = new JScrollPane(providerTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        JButton linkButton = UIStyleConstants.createModernButton("关联提供者", UIStyleConstants.SECONDARY_GREEN);
        JButton unlinkButton = UIStyleConstants.createModernButton("取消关联", UIStyleConstants.DANGER_RED);
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.ACCENT_ORANGE);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        linkButton.addActionListener(e -> showLinkProviderDialog());
        unlinkButton.addActionListener(e -> unlinkProvider());
        refreshButton.addActionListener(e -> loadProviders());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(linkButton);
        buttonPanel.add(unlinkButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(800, 500);
        setLocationRelativeTo(null);
    }
    
    private void loadProviders() {
        List<UserProviderLink> links = userProviderLinkService.getLinksByUserId(currentUser.getHealthId());
        tableModel.setRowCount(0);
        
        for (UserProviderLink link : links) {
            Provider provider = providerService.getProviderById(link.getLicenseNumber());
            if (provider != null) {
                tableModel.addRow(new Object[]{
                    provider.getLicenseNumber(),
                    provider.getName(),
                    provider.getSpecialty() != null ? provider.getSpecialty() : "",
                    provider.getPhone() != null ? provider.getPhone() : "",
                    provider.getVerifiedStatus() != null ? provider.getVerifiedStatus() : ""
                });
            }
        }
    }
    
    private void showLinkProviderDialog() {
        JDialog dialog = new JDialog(this, "关联医疗服务提供者", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = UIStyleConstants.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel providerLabel = new JLabel("医疗服务提供者:");
        providerLabel.setFont(UIStyleConstants.FONT_LABEL);
        formPanel.add(providerLabel, gbc);
        
        gbc.gridx = 1;
        // 获取所有可用的提供者
        List<Provider> allProviders = providerService.getAllProviders();
        // 获取已关联的提供者
        List<UserProviderLink> existingLinks = userProviderLinkService.getLinksByUserId(currentUser.getHealthId());
        Set<String> linkedLicenseNumbers = new HashSet<>();
        for (UserProviderLink link : existingLinks) {
            linkedLicenseNumbers.add(link.getLicenseNumber());
        }
        
        // 过滤掉已关联的提供者
        List<Provider> availableProviders = new ArrayList<>();
        for (Provider provider : allProviders) {
            if (!linkedLicenseNumbers.contains(provider.getLicenseNumber())) {
                availableProviders.add(provider);
            }
        }
        
        if (availableProviders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可用的医疗服务提供者", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Provider[] providerArray = new Provider[availableProviders.size()];
        for (int i = 0; i < availableProviders.size(); i++) {
            providerArray[i] = availableProviders.get(i);
        }
        
        JComboBox<Provider> providerComboBox = new JComboBox<>(providerArray);
        providerComboBox.setFont(UIStyleConstants.FONT_INPUT);
        providerComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Provider) {
                    Provider p = (Provider) value;
                    setText(p.getName() + " (" + p.getLicenseNumber() + ")" + 
                           (p.getSpecialty() != null ? " - " + p.getSpecialty() : ""));
                }
                return this;
            }
        });
        providerComboBox.setPreferredSize(new Dimension(300, 35));
        formPanel.add(providerComboBox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton saveButton = UIStyleConstants.createModernButton("关联", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(e -> {
            Provider selectedProvider = (Provider) providerComboBox.getSelectedItem();
            if (selectedProvider == null) {
                JOptionPane.showMessageDialog(dialog, "请选择医疗服务提供者", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            UserProviderLink link = new UserProviderLink();
            link.setHealthId(currentUser.getHealthId());
            link.setLicenseNumber(selectedProvider.getLicenseNumber());
            
            if (userProviderLinkService.createLink(link)) {
                JOptionPane.showMessageDialog(dialog, "关联成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadProviders();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "关联失败，可能已经关联过", "错误", JOptionPane.ERROR_MESSAGE);
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
    
    private void unlinkProvider() {
        int row = providerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要取消关联的提供者", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String licenseNumber = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要取消关联该医疗服务提供者吗？",
            "确认取消关联",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userProviderLinkService.deleteLink(currentUser.getHealthId(), licenseNumber)) {
                JOptionPane.showMessageDialog(this, "取消关联成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadProviders();
            } else {
                JOptionPane.showMessageDialog(this, "取消关联失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

