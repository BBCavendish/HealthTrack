package org.healthtrack.ui;

import org.healthtrack.dto.HealthIndicatorStats;
import org.healthtrack.entity.User;
import org.healthtrack.service.HealthReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 健康指标统计界面
 * 支持查找体重、血压等指标每月的平均值/最小值/最大值
 */
public class HealthIndicatorStatsFrame extends JFrame {
    
    private final HealthReportService healthReportService;
    private final User currentUser;
    
    private JComboBox<String> monthCombo;
    private JComboBox<String> indicatorCombo;
    private DefaultTableModel tableModel;
    
    public HealthIndicatorStatsFrame(HealthReportService healthReportService, User currentUser) {
        super("HealthTrack - 健康指标统计");
        this.healthReportService = healthReportService;
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("健康指标统计");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 搜索条件面板
        JPanel searchPanel = UIStyleConstants.createCardPanel();
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel monthLabel = new JLabel("选择月份:");
        monthLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(monthLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        monthCombo = new JComboBox<>(generateMonthOptions());
        monthCombo.setFont(UIStyleConstants.FONT_TEXT);
        searchPanel.add(monthCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel indicatorLabel = new JLabel("选择指标:");
        indicatorLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(indicatorLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        indicatorCombo = new JComboBox<>(new String[]{"体重 (kg)", "收缩压 (mmHg)", "舒张压 (mmHg)"});
        indicatorCombo.setFont(UIStyleConstants.FONT_TEXT);
        searchPanel.add(indicatorCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        JButton searchButton = UIStyleConstants.createModernButton("查询统计", UIStyleConstants.PRIMARY_BLUE);
        searchButton.addActionListener(e -> performSearch());
        searchPanel.add(searchButton, gbc);
        
        // 结果表格
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"指标类型", "平均值", "最小值", "最大值", "数据点数量"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(UIStyleConstants.FONT_TEXT);
        table.setRowHeight(30);
        table.getTableHeader().setFont(UIStyleConstants.FONT_LABEL);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setOpaque(false);
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        closeButton.addActionListener(e -> dispose());
        bottomPanel.add(closeButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // 使用嵌套布局
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    private String[] generateMonthOptions() {
        String[] months = new String[12];
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 0; i < 12; i++) {
            months[i] = now.minusMonths(i).format(formatter);
        }
        return months;
    }
    
    private void performSearch() {
        tableModel.setRowCount(0);
        
        if (healthReportService == null) {
            JOptionPane.showMessageDialog(this, "健康报告服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedMonth = (String) monthCombo.getSelectedItem();
        String selectedIndicator = (String) indicatorCombo.getSelectedItem();
        
        if (selectedMonth == null || selectedIndicator == null) {
            JOptionPane.showMessageDialog(this, "请选择月份和指标", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 转换指标类型
        String indicatorType;
        String displayName;
        switch (selectedIndicator) {
            case "体重 (kg)":
                indicatorType = "weight";
                displayName = "体重 (kg)";
                break;
            case "收缩压 (mmHg)":
                indicatorType = "blood_pressure_systolic";
                displayName = "收缩压 (mmHg)";
                break;
            case "舒张压 (mmHg)":
                indicatorType = "blood_pressure_diastolic";
                displayName = "舒张压 (mmHg)";
                break;
            default:
                indicatorType = "weight";
                displayName = "体重 (kg)";
        }
        
        HealthIndicatorStats stats = healthReportService.getMonthlyIndicatorStats(
            currentUser.getHealthId(), selectedMonth, indicatorType);
        
        if (stats.getCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "该月份未找到相关健康指标数据", 
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // 显示统计结果
        tableModel.addRow(new Object[]{
            displayName,
            stats.getAverage() != null ? String.format("%.2f", stats.getAverage()) : "无",
            stats.getMin() != null ? String.format("%.2f", stats.getMin()) : "无",
            stats.getMax() != null ? String.format("%.2f", stats.getMax()) : "无",
            stats.getCount()
        });
    }
}

