package org.healthtrack.ui;

import org.healthtrack.entity.Appointment;
import org.healthtrack.entity.Provider;
import org.healthtrack.entity.User;
import org.healthtrack.service.AppointmentService;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * 新建预约页面
 */
public class NewAppointmentFrame extends JFrame {
    
    private final AppointmentService appointmentService;
    private final ProviderService providerService;
    private final User currentUser;
    private JLabel appointmentIdLabel; // 显示自动生成的ID
    private JSpinner dateSpinner;
    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private JComboBox<String> typeComboBox;
    private JTextArea noteArea;
    private JComboBox<Provider> providerComboBox;
    
    public NewAppointmentFrame(AppointmentService appointmentService,
                              ProviderService providerService,
                              User currentUser) {
        super("HealthTrack - 新建预约");
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
    
    /**
     * 生成唯一的预约ID
     */
    private String generateAppointmentId() {
        // 使用时间戳 + UUID的简化版本生成唯一ID
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "APT" + timestamp.substring(timestamp.length() - 8) + uuid.toUpperCase();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("新建预约");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表单卡片
        JPanel cardPanel = UIStyleConstants.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        // 预约ID（自动生成，只读显示）
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel idLabel = new JLabel("预约ID:");
        idLabel.setFont(UIStyleConstants.FONT_LABEL);
        idLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(idLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        String generatedId = generateAppointmentId();
        appointmentIdLabel = new JLabel(generatedId);
        appointmentIdLabel.setFont(UIStyleConstants.FONT_INPUT);
        appointmentIdLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        appointmentIdLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        appointmentIdLabel.setOpaque(true);
        appointmentIdLabel.setBackground(UIStyleConstants.HEADER_BG);
        cardPanel.add(appointmentIdLabel, gbc);
        
        // 日期时间选择器
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel dateTimeLabel = new JLabel("日期时间 *:");
        dateTimeLabel.setFont(UIStyleConstants.FONT_LABEL);
        dateTimeLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(dateTimeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateTimePanel.setOpaque(false);
        
        // 日期选择器
        LocalDate today = LocalDate.now();
        SpinnerDateModel dateModel = new SpinnerDateModel(
            java.sql.Date.valueOf(today),
            null,
            null,
            java.util.Calendar.DAY_OF_MONTH
        );
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(UIStyleConstants.FONT_INPUT);
        dateSpinner.setPreferredSize(new Dimension(150, 35));
        dateTimePanel.add(dateSpinner);
        
        dateTimePanel.add(new JLabel("  "));
        
        // 小时选择器
        SpinnerNumberModel hourModel = new SpinnerNumberModel(
            LocalTime.now().getHour(), 0, 23, 1
        );
        hourSpinner = new JSpinner(hourModel);
        hourSpinner.setFont(UIStyleConstants.FONT_INPUT);
        hourSpinner.setPreferredSize(new Dimension(60, 35));
        dateTimePanel.add(new JLabel("时:"));
        dateTimePanel.add(hourSpinner);
        
        // 分钟选择器
        SpinnerNumberModel minuteModel = new SpinnerNumberModel(
            (LocalTime.now().getMinute() / 15) * 15, 0, 45, 15
        );
        minuteSpinner = new JSpinner(minuteModel);
        minuteSpinner.setFont(UIStyleConstants.FONT_INPUT);
        minuteSpinner.setPreferredSize(new Dimension(60, 35));
        dateTimePanel.add(new JLabel("分:"));
        dateTimePanel.add(minuteSpinner);
        
        cardPanel.add(dateTimePanel, gbc);
        
        // 类型
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel typeLabel = new JLabel("类型:");
        typeLabel.setFont(UIStyleConstants.FONT_LABEL);
        typeLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        // 预约类型选项：仅提供In-Person和Virtual
        String[] appointmentTypes = {
            "",  // 空选项，表示不选择
            "In-Person",
            "Virtual"
        };
        typeComboBox = new JComboBox<>(appointmentTypes);
        typeComboBox.setFont(UIStyleConstants.FONT_INPUT);
        typeComboBox.setPreferredSize(new Dimension(300, 35));
        typeComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cardPanel.add(typeComboBox, gbc);
        
        // 备注
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel noteLabel = new JLabel("备注:");
        noteLabel.setFont(UIStyleConstants.FONT_LABEL);
        noteLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(noteLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        noteArea = new JTextArea(5, 30);
        noteArea.setFont(UIStyleConstants.FONT_INPUT);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        noteArea.setBackground(Color.WHITE);
        noteArea.setForeground(UIStyleConstants.TEXT_PRIMARY);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setPreferredSize(new Dimension(300, 100));
        noteScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noteScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cardPanel.add(noteScroll, gbc);
        
        // 提供者选择下拉列表
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel providerLabel = new JLabel("医疗提供者:");
        providerLabel.setFont(UIStyleConstants.FONT_LABEL);
        providerLabel.setForeground(UIStyleConstants.TEXT_PRIMARY);
        cardPanel.add(providerLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        // 加载提供者列表
        List<Provider> providers = providerService.getVerifiedProviders();
        if (providers.isEmpty()) {
            providers = providerService.getAllProviders();
        }
        
        Provider[] providerArray = new Provider[providers.size() + 1];
        providerArray[0] = null; // 第一个选项为"请选择"
        for (int i = 0; i < providers.size(); i++) {
            providerArray[i + 1] = providers.get(i);
        }
        
        providerComboBox = new JComboBox<>(providerArray);
        providerComboBox.setFont(UIStyleConstants.FONT_INPUT);
        providerComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("请选择医疗提供者");
                } else if (value instanceof Provider) {
                    Provider p = (Provider) value;
                    setText(p.getName() + " (" + p.getLicenseNumber() + ") - " + 
                           (p.getSpecialty() != null ? p.getSpecialty() : "未指定专业"));
                }
                return this;
            }
        });
        providerComboBox.setPreferredSize(new Dimension(300, 35));
        providerComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cardPanel.add(providerComboBox, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = UIStyleConstants.createModernButton("保存", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAppointment();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(650, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void saveAppointment() {
        String appointmentId = appointmentIdLabel.getText();
        
        // 获取日期时间
        java.util.Date dateValue = (java.util.Date) dateSpinner.getValue();
        LocalDate selectedDate = new java.sql.Date(dateValue.getTime()).toLocalDate();
        int hour = (Integer) hourSpinner.getValue();
        int minute = (Integer) minuteSpinner.getValue();
        LocalDateTime dateTime = LocalDateTime.of(selectedDate, LocalTime.of(hour, minute));
        
        // 检查日期时间不能是过去的时间
        if (dateTime.isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this,
                "预约时间不能是过去的时间！",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String type = (String) typeComboBox.getSelectedItem();
        if (type != null) {
            type = type.trim();
            if (type.isEmpty()) {
                type = null;
            }
        }
        
        // 验证预约类型不能为空
        if (type == null || type.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "预约类型不能为空，请选择In-Person或Virtual！",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String note = noteArea.getText().trim();
        Provider selectedProvider = (Provider) providerComboBox.getSelectedItem();
        
        try {
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(appointmentId);
            appointment.setDateTime(dateTime);
            appointment.setUserId(currentUser.getHealthId());
            appointment.setType(type);
            appointment.setNote(note.isEmpty() ? null : note);
            appointment.setStatus("Scheduled");
            
            if (appointmentService.saveAppointment(appointment)) {
                // 如果选择了提供者，关联提供者
                if (selectedProvider != null) {
                    appointmentService.linkProviderToAppointment(appointmentId, selectedProvider.getLicenseNumber());
                }
                
                JOptionPane.showMessageDialog(this,
                    "预约创建成功！\n预约ID: " + appointmentId,
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "创建失败，请重试",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "创建预约时发生错误: " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
