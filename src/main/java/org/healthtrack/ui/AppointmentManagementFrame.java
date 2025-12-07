package org.healthtrack.ui;

import org.healthtrack.entity.Appointment;
import org.healthtrack.entity.User;
import org.healthtrack.service.AppointmentService;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 预约管理页面 - 导航到各个子页面
 */
public class AppointmentManagementFrame extends JFrame {
    
    private final AppointmentService appointmentService;
    private final ProviderService providerService;
    private final User currentUser;
    private DefaultTableModel tableModel;
    private JTable appointmentTable;
    
    public AppointmentManagementFrame(AppointmentService appointmentService,
                                     ProviderService providerService,
                                     User currentUser) {
        super("HealthTrack - 预约管理");
        this.appointmentService = appointmentService;
        this.providerService = providerService;
        this.currentUser = currentUser;
        
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("预约管理");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表格面板
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"预约ID", "日期时间", "类型", "状态", "备注"},
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
        
        JButton newButton = UIStyleConstants.createModernButton("新建预约", UIStyleConstants.SECONDARY_GREEN);
        JButton listButton = UIStyleConstants.createModernButton("预约列表", UIStyleConstants.PRIMARY_BLUE);
        JButton cancelButton = UIStyleConstants.createModernButton("取消预约", UIStyleConstants.DANGER_RED);
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.ACCENT_ORANGE);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        newButton.addActionListener(e -> openNewAppointmentFrame());
        listButton.addActionListener(e -> loadAppointments());
        cancelButton.addActionListener(e -> cancelSelectedAppointment());
        refreshButton.addActionListener(e -> loadAppointments());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(newButton);
        buttonPanel.add(listButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void loadAppointments() {
        if (appointmentService == null) {
            JOptionPane.showMessageDialog(this, "预约服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Appointment> appointments = appointmentService.getAppointmentsByUser(currentUser.getHealthId());
        tableModel.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Appointment appointment : appointments) {
            tableModel.addRow(new Object[]{
                appointment.getAppointmentId(),
                appointment.getDateTime() != null ? appointment.getDateTime().format(formatter) : "",
                appointment.getType() != null ? appointment.getType() : "",
                appointment.getStatus() != null ? appointment.getStatus() : "",
                appointment.getNote() != null ? appointment.getNote() : ""
            });
        }
    }
    
    private void openNewAppointmentFrame() {
        NewAppointmentFrame frame = new NewAppointmentFrame(appointmentService, providerService, currentUser);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadAppointments();
            }
        });
    }
    
    private void cancelSelectedAppointment() {
        int row = appointmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请选择要取消的预约", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String appointmentId = (String) tableModel.getValueAt(row, 0);
        String reason = JOptionPane.showInputDialog(this, "请输入取消原因:", "取消预约", JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null && !reason.trim().isEmpty()) {
            if (appointmentService.cancelAppointment(appointmentId, reason)) {
                JOptionPane.showMessageDialog(this, "预约已取消", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "取消失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

