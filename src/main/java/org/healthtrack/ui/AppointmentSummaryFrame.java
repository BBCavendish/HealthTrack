package org.healthtrack.ui;

import org.healthtrack.entity.Appointment;
import org.healthtrack.entity.User;
import org.healthtrack.service.AppointmentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 预约汇总页面
 */
public class AppointmentSummaryFrame extends JFrame {
    
    public AppointmentSummaryFrame(AppointmentService appointmentService, User currentUser) {
        super("HealthTrack - 预约汇总");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI(appointmentService, currentUser);
    }
    
    private void initUI(AppointmentService appointmentService, User currentUser) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("预约汇总");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 数据表格
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"预约ID", "日期时间", "类型", "状态"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        if (appointmentService != null) {
            List<Appointment> appointments = appointmentService.getAppointmentsByUser(currentUser.getHealthId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Appointment appointment : appointments) {
                tableModel.addRow(new Object[]{
                    appointment.getAppointmentId(),
                    appointment.getDateTime() != null ? appointment.getDateTime().format(formatter) : "",
                    appointment.getType() != null ? appointment.getType() : "",
                    appointment.getStatus() != null ? appointment.getStatus() : ""
                });
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

