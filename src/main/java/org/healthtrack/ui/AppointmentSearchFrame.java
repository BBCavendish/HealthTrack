package org.healthtrack.ui;

import org.healthtrack.entity.Appointment;
import org.healthtrack.entity.Provider;
import org.healthtrack.entity.User;
import org.healthtrack.service.AppointmentService;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 预约搜索页面 - 支持多属性搜索
 */
public class AppointmentSearchFrame extends JFrame {
    
    private final AppointmentService appointmentService;
    private final ProviderService providerService;
    private final User currentUser;
    
    private JComboBox<String> statusCombo;
    private JComboBox<String> typeCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JComboBox<Provider> providerCombo;
    private DefaultTableModel tableModel;
    
    public AppointmentSearchFrame(AppointmentService appointmentService, 
                                ProviderService providerService,
                                User currentUser) {
        super("HealthTrack - 预约搜索");
        this.appointmentService = appointmentService;
        this.providerService = providerService;
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("预约搜索");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 搜索条件面板
        JPanel searchPanel = UIStyleConstants.createCardPanel();
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        // 状态
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel statusLabel = new JLabel("状态:");
        statusLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(statusLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        statusCombo = new JComboBox<>(new String[]{"全部", "Scheduled", "Completed", "Cancelled"});
        statusCombo.setFont(UIStyleConstants.FONT_INPUT);
        searchPanel.add(statusCombo, gbc);
        
        // 类型
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel typeLabel = new JLabel("类型:");
        typeLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        typeCombo = new JComboBox<>(new String[]{"全部", "In-Person", "Virtual"});
        typeCombo.setFont(UIStyleConstants.FONT_INPUT);
        searchPanel.add(typeCombo, gbc);
        
        // 开始日期
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel startDateLabel = new JLabel("开始日期:");
        startDateLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(startDateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        SpinnerDateModel startDateModel = new SpinnerDateModel(
            java.sql.Date.valueOf(LocalDate.now().minusMonths(1)),
            null,
            null,
            java.util.Calendar.DAY_OF_MONTH
        );
        startDateSpinner = new JSpinner(startDateModel);
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        startDateSpinner.setFont(UIStyleConstants.FONT_INPUT);
        searchPanel.add(startDateSpinner, gbc);
        
        // 结束日期
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel endDateLabel = new JLabel("结束日期:");
        endDateLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(endDateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        SpinnerDateModel endDateModel = new SpinnerDateModel(
            java.sql.Date.valueOf(LocalDate.now().plusMonths(1)),
            null,
            null,
            java.util.Calendar.DAY_OF_MONTH
        );
        endDateSpinner = new JSpinner(endDateModel);
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endDateEditor);
        endDateSpinner.setFont(UIStyleConstants.FONT_INPUT);
        searchPanel.add(endDateSpinner, gbc);
        
        // 提供者
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        JLabel providerLabel = new JLabel("提供者:");
        providerLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(providerLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        List<Provider> allProviders = providerService.getAllProviders();
        Provider[] providerArray = new Provider[allProviders.size() + 1];
        providerArray[0] = null; // "全部"选项
        for (int i = 0; i < allProviders.size(); i++) {
            providerArray[i + 1] = allProviders.get(i);
        }
        providerCombo = new JComboBox<>(providerArray);
        providerCombo.setFont(UIStyleConstants.FONT_INPUT);
        providerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("全部");
                } else if (value instanceof Provider) {
                    Provider p = (Provider) value;
                    setText(p.getName() + " (" + p.getLicenseNumber() + ")");
                }
                return this;
            }
        });
        searchPanel.add(providerCombo, gbc);
        
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
            new Object[]{"预约ID", "日期时间", "类型", "状态", "提供者", "备注"},
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
        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    private void performSearch() {
        tableModel.setRowCount(0);
        
        if (appointmentService == null) {
            return;
        }
        
        // 获取搜索条件
        String status = (String) statusCombo.getSelectedItem();
        if ("全部".equals(status)) {
            status = null;
        }
        
        String type = (String) typeCombo.getSelectedItem();
        if ("全部".equals(type)) {
            type = null;
        }
        
        java.util.Date startDateValue = (java.util.Date) startDateSpinner.getValue();
        LocalDate startDate = new java.sql.Date(startDateValue.getTime()).toLocalDate();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        
        java.util.Date endDateValue = (java.util.Date) endDateSpinner.getValue();
        LocalDate endDate = new java.sql.Date(endDateValue.getTime()).toLocalDate();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        Provider selectedProvider = (Provider) providerCombo.getSelectedItem();
        String providerLicense = (selectedProvider != null) ? selectedProvider.getLicenseNumber() : null;
        
        // 执行搜索
        List<Appointment> appointments = appointmentService.searchAppointments(
            currentUser.getHealthId(),
            status,
            type,
            startDateTime,
            endDateTime,
            providerLicense
        );
        
        // 显示结果
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Appointment appointment : appointments) {
            // 获取提供者信息 - 简化处理，如果有搜索条件中的提供者则显示
            String providerName = "";
            if (providerLicense != null && providerService != null) {
                Provider p = providerService.getProviderById(providerLicense);
                if (p != null) {
                    providerName = p.getName();
                }
            }
            
            tableModel.addRow(new Object[]{
                appointment.getAppointmentId(),
                appointment.getDateTime() != null ? appointment.getDateTime().format(formatter) : "",
                appointment.getType() != null ? appointment.getType() : "",
                appointment.getStatus() != null ? appointment.getStatus() : "",
                providerName,
                appointment.getNote() != null ? (appointment.getNote().length() > 30 ? 
                    appointment.getNote().substring(0, 30) + "..." : appointment.getNote()) : ""
            });
        }
        
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到符合条件的预约", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetSearch() {
        statusCombo.setSelectedIndex(0);
        typeCombo.setSelectedIndex(0);
        startDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now().minusMonths(1)));
        endDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now().plusMonths(1)));
        providerCombo.setSelectedIndex(0);
        tableModel.setRowCount(0);
    }
}
