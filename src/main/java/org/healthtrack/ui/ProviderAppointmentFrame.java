package org.healthtrack.ui;

import org.healthtrack.entity.Appointment;
import org.healthtrack.entity.Provider;
import org.healthtrack.service.AppointmentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 医疗提供者预约管理页面
 */
public class ProviderAppointmentFrame extends JFrame {
    
    private final AppointmentService appointmentService;
    private final Provider currentProvider;
    private DefaultTableModel tableModel;
    private JTable appointmentTable;
    
    public ProviderAppointmentFrame(AppointmentService appointmentService, Provider currentProvider) {
        super("HealthTrack - 我的预约管理");
        this.appointmentService = appointmentService;
        this.currentProvider = currentProvider;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadAppointments();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("我的预约管理");
        JLabel subtitleLabel = new JLabel("提供者: " + currentProvider.getName() + " (" + currentProvider.getLicenseNumber() + ")");
        subtitleLabel.setFont(UIStyleConstants.FONT_SUBTITLE);
        subtitleLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // 表格面板
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"预约ID", "日期时间", "类型", "状态", "患者ID", "备注"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setRowHeight(35);
        appointmentTable.setFont(UIStyleConstants.FONT_TEXT);
        appointmentTable.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        appointmentTable.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.ACCENT_ORANGE);
        JButton viewDetailsButton = UIStyleConstants.createModernButton("查看详情", UIStyleConstants.PRIMARY_BLUE);
        JButton updateStatusButton = UIStyleConstants.createModernButton("更新状态", UIStyleConstants.SECONDARY_GREEN);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        refreshButton.addActionListener(e -> loadAppointments());
        viewDetailsButton.addActionListener(e -> viewAppointmentDetails());
        updateStatusButton.addActionListener(e -> updateAppointmentStatus());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }
    
    private void loadAppointments() {
        if (appointmentService == null) {
            JOptionPane.showMessageDialog(this, "预约服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Appointment> appointments = appointmentService.getAppointmentsByProvider(currentProvider.getLicenseNumber());
        tableModel.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Appointment appointment : appointments) {
            tableModel.addRow(new Object[]{
                appointment.getAppointmentId(),
                appointment.getDateTime() != null ? appointment.getDateTime().format(formatter) : "",
                appointment.getType() != null ? appointment.getType() : "",
                appointment.getStatus() != null ? appointment.getStatus() : "",
                appointment.getUserId() != null ? appointment.getUserId() : "",
                appointment.getNote() != null ? appointment.getNote() : ""
            });
        }
    }
    
    private void viewAppointmentDetails() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要查看的预约", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String appointmentId = (String) tableModel.getValueAt(row, 0);
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "未找到预约信息", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("预约ID: ").append(appointment.getAppointmentId()).append("\n");
        details.append("日期时间: ").append(appointment.getDateTime() != null ? 
            appointment.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "未设置").append("\n");
        details.append("类型: ").append(appointment.getType() != null ? appointment.getType() : "未设置").append("\n");
        details.append("状态: ").append(appointment.getStatus() != null ? appointment.getStatus() : "未设置").append("\n");
        details.append("患者ID: ").append(appointment.getUserId() != null ? appointment.getUserId() : "未设置").append("\n");
        details.append("备注: ").append(appointment.getNote() != null ? appointment.getNote() : "无").append("\n");
        if (appointment.getCancelReason() != null && !appointment.getCancelReason().isEmpty()) {
            details.append("取消原因: ").append(appointment.getCancelReason()).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, details.toString(), "预约详情", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateAppointmentStatus() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要更新状态的预约", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String appointmentId = (String) tableModel.getValueAt(row, 0);
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            JOptionPane.showMessageDialog(this, "未找到预约信息", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String[] statusOptions = {"Scheduled", "Completed", "Cancelled"};
        String currentStatus = appointment.getStatus() != null ? appointment.getStatus() : "Scheduled";
        
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "选择新状态:",
            "更新预约状态",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            appointment.setStatus(newStatus);
            if (appointmentService.saveAppointment(appointment)) {
                JOptionPane.showMessageDialog(this, "状态更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "状态更新失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

