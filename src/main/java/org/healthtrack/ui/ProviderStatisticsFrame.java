package org.healthtrack.ui;

import org.healthtrack.entity.Appointment;
import org.healthtrack.entity.Provider;
import org.healthtrack.service.AppointmentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 医疗提供者统计信息页面
 */
public class ProviderStatisticsFrame extends JFrame {
    
    private final AppointmentService appointmentService;
    private final Provider currentProvider;
    
    public ProviderStatisticsFrame(AppointmentService appointmentService, Provider currentProvider) {
        super("HealthTrack - 预约统计");
        this.appointmentService = appointmentService;
        this.currentProvider = currentProvider;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadStatistics();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("预约统计信息");
        JLabel subtitleLabel = new JLabel("提供者: " + currentProvider.getName() + " (" + currentProvider.getLicenseNumber() + ")");
        subtitleLabel.setFont(UIStyleConstants.FONT_SUBTITLE);
        subtitleLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(new EmptyBorder(10, 0, 20, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // 统计内容面板
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // 创建统计卡片
        contentPanel.add(createOverallStatsCard());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createStatusStatsCard());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createTypeStatsCard());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR));
        scrollPane.getViewport().setBackground(UIStyleConstants.BACKGROUND);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.ACCENT_ORANGE);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        refreshButton.addActionListener(e -> loadStatistics());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(700, 600);
        setLocationRelativeTo(null);
    }
    
    private JPanel createOverallStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        
        JLabel title = new JLabel("总体统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 1, 15, 15));
        statsPanel.setOpaque(false);
        
        JLabel totalLabel = createStatLabel("totalAppointments", "总预约数", "0");
        statsPanel.add(totalLabel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createStatusStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        
        JLabel title = new JLabel("按状态统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        statsPanel.setOpaque(false);
        
        JLabel scheduledLabel = createStatLabel("scheduledAppointments", "已预约", "0");
        JLabel completedLabel = createStatLabel("completedAppointments", "已完成", "0");
        JLabel cancelledLabel = createStatLabel("cancelledAppointments", "已取消", "0");
        
        statsPanel.add(scheduledLabel);
        statsPanel.add(completedLabel);
        statsPanel.add(cancelledLabel);
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTypeStatsCard() {
        JPanel card = UIStyleConstants.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        
        JLabel title = new JLabel("按类型统计");
        title.setFont(UIStyleConstants.FONT_HEADING);
        title.setForeground(UIStyleConstants.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        
        // 类型统计将在loadStatistics中动态添加
        statsPanel.setName("typeStatsPanel");
        
        card.add(title, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JLabel createStatLabel(String name, String label, String value) {
        JLabel statLabel = new JLabel("<html><b>" + label + ":</b> " + value + "</html>");
        statLabel.setName(name);
        statLabel.setFont(UIStyleConstants.FONT_TEXT);
        statLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        statLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
        statLabel.setOpaque(true);
        statLabel.setBackground(UIStyleConstants.HEADER_BG);
        return statLabel;
    }
    
    private void loadStatistics() {
        if (appointmentService == null) {
            JOptionPane.showMessageDialog(this, "预约服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Appointment> appointments = appointmentService.getAppointmentsByProvider(currentProvider.getLicenseNumber());
        
        // 更新总体统计
        int total = appointments.size();
        updateLabel("totalAppointments", String.valueOf(total));
        
        // 按状态统计
        long scheduled = appointments.stream()
            .filter(a -> "Scheduled".equals(a.getStatus()))
            .count();
        long completed = appointments.stream()
            .filter(a -> "Completed".equals(a.getStatus()))
            .count();
        long cancelled = appointments.stream()
            .filter(a -> "Cancelled".equals(a.getStatus()))
            .count();
        
        updateLabel("scheduledAppointments", String.valueOf(scheduled));
        updateLabel("completedAppointments", String.valueOf(completed));
        updateLabel("cancelledAppointments", String.valueOf(cancelled));
        
        // 按类型统计
        Map<String, Long> typeCounts = appointments.stream()
            .filter(a -> a.getType() != null && !a.getType().isEmpty())
            .collect(Collectors.groupingBy(Appointment::getType, Collectors.counting()));
        
        // 更新类型统计面板
        updateTypeStatsPanel(typeCounts);
        
        revalidate();
        repaint();
    }
    
    private void updateLabel(String name, String value) {
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            JLabel label = findLabelByName(comp, name);
            if (label != null) {
                String text = label.getText();
                int colonIndex = text.indexOf(":");
                if (colonIndex > 0) {
                    String labelText = text.substring(text.indexOf("<b>") + 3, text.indexOf("</b>"));
                    label.setText("<html><b>" + labelText + ":</b> " + value + "</html>");
                }
                break;
            }
        }
    }
    
    private JLabel findLabelByName(Component comp, String name) {
        if (comp instanceof JLabel && name.equals(comp.getName())) {
            return (JLabel) comp;
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                JLabel found = findLabelByName(child, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    
    private void updateTypeStatsPanel(Map<String, Long> typeCounts) {
        Component[] components = getContentPane().getComponents();
        JPanel typeStatsPanel = null;
        
        for (Component comp : components) {
            typeStatsPanel = findPanelByName(comp, "typeStatsPanel");
            if (typeStatsPanel != null) {
                break;
            }
        }
        
        if (typeStatsPanel != null) {
            typeStatsPanel.removeAll();
            
            if (typeCounts.isEmpty()) {
                JLabel noDataLabel = new JLabel("暂无类型数据");
                noDataLabel.setFont(UIStyleConstants.FONT_TEXT);
                noDataLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
                noDataLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
                typeStatsPanel.add(noDataLabel);
            } else {
                for (Map.Entry<String, Long> entry : typeCounts.entrySet()) {
                    JLabel typeLabel = createStatLabel(null, entry.getKey(), String.valueOf(entry.getValue()));
                    typeStatsPanel.add(typeLabel);
                    typeStatsPanel.add(Box.createVerticalStrut(5));
                }
            }
        }
    }
    
    private JPanel findPanelByName(Component comp, String name) {
        if (comp instanceof JPanel && name.equals(comp.getName())) {
            return (JPanel) comp;
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                JPanel found = findPanelByName(child, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}

