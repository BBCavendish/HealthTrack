package org.healthtrack.ui;

import org.healthtrack.entity.HealthReport;
import org.healthtrack.entity.User;
import org.healthtrack.service.HealthReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 健康数据搜索页面 - 完善版
 */
public class HealthDataSearchFrame extends JFrame {
    
    private final HealthReportService healthReportService;
    private final User currentUser;
    private DefaultTableModel tableModel;
    private JComboBox<String> monthCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JTextField minStepsField;
    private JTextField maxStepsField;
    
    public HealthDataSearchFrame(HealthReportService healthReportService, User currentUser) {
        super("HealthTrack - 健康数据搜索");
        this.healthReportService = healthReportService;
        this.currentUser = currentUser;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadInitialData(); // 初始加载所有数据，不显示提示
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("健康数据搜索");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 搜索条件面板
        JPanel searchPanel = UIStyleConstants.createCardPanel();
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // 月份搜索
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel monthLabel = new JLabel("月份:");
        monthLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(monthLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        monthCombo = new JComboBox<>(generateMonthOptions());
        monthCombo.setFont(UIStyleConstants.FONT_INPUT);
        monthCombo.insertItemAt("全部", 0);
        monthCombo.setSelectedIndex(0);
        searchPanel.add(monthCombo, gbc);
        
        // 日期范围
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel dateRangeLabel = new JLabel("日期范围:");
        dateRangeLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(dateRangeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateRangePanel.setOpaque(false);
        
        SpinnerDateModel startDateModel = new SpinnerDateModel(
            java.sql.Date.valueOf(LocalDate.now().minusMonths(6)),
            null,
            null,
            java.util.Calendar.DAY_OF_MONTH
        );
        startDateSpinner = new JSpinner(startDateModel);
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        startDateSpinner.setFont(UIStyleConstants.FONT_INPUT);
        
        SpinnerDateModel endDateModel = new SpinnerDateModel(
            java.sql.Date.valueOf(LocalDate.now()),
            null,
            null,
            java.util.Calendar.DAY_OF_MONTH
        );
        endDateSpinner = new JSpinner(endDateModel);
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endDateEditor);
        endDateSpinner.setFont(UIStyleConstants.FONT_INPUT);
        
        dateRangePanel.add(new JLabel("从:"));
        dateRangePanel.add(startDateSpinner);
        dateRangePanel.add(new JLabel("到:"));
        dateRangePanel.add(endDateSpinner);
        searchPanel.add(dateRangePanel, gbc);
        
        // 步数范围
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel stepsLabel = new JLabel("步数范围:");
        stepsLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(stepsLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel stepsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        stepsPanel.setOpaque(false);
        minStepsField = UIStyleConstants.createModernTextField(10);
        maxStepsField = UIStyleConstants.createModernTextField(10);
        stepsPanel.add(new JLabel("最小:"));
        stepsPanel.add(minStepsField);
        stepsPanel.add(new JLabel("最大:"));
        stepsPanel.add(maxStepsField);
        searchPanel.add(stepsPanel, gbc);
        
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
            new Object[]{"报告ID", "报告月份", "总步数", "摘要"},
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
        
        // 添加双击事件监听器
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        String reportId = (String) tableModel.getValueAt(row, 0);
                        HealthReport report = healthReportService.getReportById(reportId);
                        if (report != null) {
                            showReportDetailDialog(report);
                        }
                    }
                }
            }
        });
        
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
    
    private String[] generateMonthOptions() {
        String[] months = new String[12];
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 0; i < 12; i++) {
            months[i] = now.minusMonths(i).format(formatter);
        }
        return months;
    }
    
    private void loadInitialData() {
        performSearch(false); // 初始加载时不显示提示
    }
    
    private void performSearch() {
        performSearch(true); // 用户主动搜索时显示提示
    }
    
    private void performSearch(boolean showNoResultMessage) {
        tableModel.setRowCount(0);
        
        if (healthReportService == null) {
            return;
        }
        
        List<HealthReport> reports = healthReportService.getReportsByUser(currentUser.getHealthId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        // 获取搜索条件
        String selectedMonth = (String) monthCombo.getSelectedItem();
        LocalDate monthDate = null;
        if (selectedMonth != null && !"全部".equals(selectedMonth)) {
            monthDate = LocalDate.parse(selectedMonth + "-01");
        }
        
        java.util.Date startDateValue = (java.util.Date) startDateSpinner.getValue();
        LocalDate startDate = new java.sql.Date(startDateValue.getTime()).toLocalDate();
        java.util.Date endDateValue = (java.util.Date) endDateSpinner.getValue();
        LocalDate endDate = new java.sql.Date(endDateValue.getTime()).toLocalDate();
        
        String minStepsStr = minStepsField.getText().trim();
        String maxStepsStr = maxStepsField.getText().trim();
        Integer minSteps = null;
        Integer maxSteps = null;
        try {
            if (!minStepsStr.isEmpty()) {
                minSteps = Integer.parseInt(minStepsStr);
            }
            if (!maxStepsStr.isEmpty()) {
                maxSteps = Integer.parseInt(maxStepsStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "步数必须是数字", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 应用搜索条件
        for (HealthReport report : reports) {
            // 月份过滤
            if (monthDate != null && report.getReportMonth() != null) {
                if (report.getReportMonth().getYear() != monthDate.getYear() ||
                    report.getReportMonth().getMonthValue() != monthDate.getMonthValue()) {
                    continue;
                }
            }
            
            // 日期范围过滤
            if (report.getReportMonth() != null) {
                if (report.getReportMonth().isBefore(startDate) || report.getReportMonth().isAfter(endDate)) {
                    continue;
                }
            }
            
            // 步数范围过滤
            if (report.getTotalSteps() != null) {
                if (minSteps != null && report.getTotalSteps() < minSteps) {
                    continue;
                }
                if (maxSteps != null && report.getTotalSteps() > maxSteps) {
                    continue;
                }
            }
            
            tableModel.addRow(new Object[]{
                report.getReportId(),
                report.getReportMonth() != null ? report.getReportMonth().format(formatter) : "",
                report.getTotalSteps() != null ? report.getTotalSteps() : 0,
                report.getSummary() != null ? (report.getSummary().length() > 30 ? 
                    report.getSummary().substring(0, 30) + "..." : report.getSummary()) : ""
            });
        }
        
        // 只在用户主动搜索且没有结果时显示提示
        if (showNoResultMessage && tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "未找到符合条件的健康数据", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void resetSearch() {
        monthCombo.setSelectedIndex(0);
        startDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now().minusMonths(6)));
        endDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
        minStepsField.setText("");
        maxStepsField.setText("");
        performSearch(false); // 重置时不显示提示
    }
    
    private void showReportDetailDialog(HealthReport report) {
        JDialog dialog = new JDialog(this, "健康报告详情", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        JPanel detailPanel = UIStyleConstants.createCardPanel();
        detailPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        addDetailRow(detailPanel, gbc, "报告ID:", report.getReportId(), 0);
        addDetailRow(detailPanel, gbc, "报告月份:", 
            report.getReportMonth() != null ? report.getReportMonth().format(formatter) : "无", 1);
        addDetailRow(detailPanel, gbc, "总步数:", 
            report.getTotalSteps() != null ? String.valueOf(report.getTotalSteps()) : "0", 2);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel summaryLabel = new JLabel("摘要:");
        summaryLabel.setFont(UIStyleConstants.FONT_LABEL);
        detailPanel.add(summaryLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea summaryArea = new JTextArea(report.getSummary() != null ? report.getSummary() : "无");
        summaryArea.setFont(UIStyleConstants.FONT_TEXT);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setBackground(UIStyleConstants.HEADER_BG);
        JScrollPane summaryScroll = new JScrollPane(summaryArea);
        summaryScroll.setPreferredSize(new Dimension(400, 100));
        detailPanel.add(summaryScroll, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        
        dialog.add(detailPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(UIStyleConstants.FONT_LABEL);
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(UIStyleConstants.FONT_TEXT);
        valueComponent.setForeground(UIStyleConstants.TEXT_PRIMARY);
        panel.add(valueComponent, gbc);
    }
}
