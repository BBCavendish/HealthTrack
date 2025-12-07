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
 * 健康指标图表页面
 */
public class HealthChartFrame extends JFrame {
    
    public HealthChartFrame(HealthReportService healthReportService, User currentUser) {
        super("HealthTrack - 健康指标图表");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI(healthReportService, currentUser);
    }
    
    private void initUI(HealthReportService healthReportService, User currentUser) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("健康指标图表");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 数据表格
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"报告月份", "总步数", "摘要"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        if (healthReportService != null) {
            LocalDate startDate = LocalDate.now().minusMonths(6);
            LocalDate endDate = LocalDate.now();
            List<HealthReport> reports = healthReportService.getReportsByDateRange(startDate, endDate);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            for (HealthReport report : reports) {
                if (report.getUserId().equals(currentUser.getHealthId())) {
                    tableModel.addRow(new Object[]{
                        report.getReportMonth().format(formatter),
                        report.getTotalSteps() != null ? report.getTotalSteps() : 0,
                        report.getSummary() != null ? report.getSummary() : ""
                    });
                }
            }
        }
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(UIStyleConstants.FONT_TEXT);
        table.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        table.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 关闭按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
}

