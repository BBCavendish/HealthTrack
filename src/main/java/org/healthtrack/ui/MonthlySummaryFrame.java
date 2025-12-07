package org.healthtrack.ui;

import org.healthtrack.HealthTrackApplication;
import org.healthtrack.entity.HealthReport;
import org.healthtrack.entity.Provider;
import org.healthtrack.entity.User;
import org.healthtrack.service.AppointmentService;
import org.healthtrack.service.HealthReportService;
import org.healthtrack.service.ProviderService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 月度健康摘要页面 - 显示健康报告列表并支持搜索
 */
public class MonthlySummaryFrame extends JFrame {
    
    private final HealthReportService healthReportService;
    private final AppointmentService appointmentService;
    private final ProviderService providerService;
    private final User currentUser;
    
    private DefaultTableModel tableModel;
    private JComboBox<String> monthCombo;
    private JComboBox<Provider> providerCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    
    public MonthlySummaryFrame(HealthReportService healthReportService,
                              AppointmentService appointmentService,
                              User currentUser) {
        super("HealthTrack - 月度健康摘要");
        this.healthReportService = healthReportService;
        this.appointmentService = appointmentService;
        this.currentUser = currentUser;
        this.providerService = HealthTrackApplication.getContext().getBean(ProviderService.class);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadReports();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("月度健康摘要");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 搜索面板
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
        
        // 提供者搜索
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel providerLabel = new JLabel("提供者:");
        providerLabel.setFont(UIStyleConstants.FONT_LABEL);
        searchPanel.add(providerLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        List<Provider> allProviders = providerService.getAllProviders();
        Provider[] providerArray = new Provider[allProviders.size() + 1];
        providerArray[0] = null;
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
        
        // 日期范围
        gbc.gridx = 0;
        gbc.gridy = 2;
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
        
        // 搜索按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        JButton searchButton = UIStyleConstants.createModernButton("搜索", UIStyleConstants.PRIMARY_BLUE);
        JButton resetButton = UIStyleConstants.createModernButton("重置", UIStyleConstants.ACCENT_ORANGE);
        JButton viewDetailButton = UIStyleConstants.createModernButton("查看详情", UIStyleConstants.SECONDARY_GREEN);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        searchButton.addActionListener(e -> performSearch());
        resetButton.addActionListener(e -> resetSearch());
        viewDetailButton.addActionListener(e -> viewReportDetail());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(viewDetailButton);
        buttonPanel.add(backButton);
        
        // 结果表格
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"报告ID", "报告月份", "总步数", "摘要", "验证者"},
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
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
    
    private void loadReports() {
        performSearch();
    }
    
    private void performSearch() {
        tableModel.setRowCount(0);
        
        if (healthReportService == null) {
            return;
        }
        
        // 获取所有报告
        List<HealthReport> reports = healthReportService.getReportsByUser(currentUser.getHealthId());
        
        // 应用搜索条件
        String selectedMonth = (String) monthCombo.getSelectedItem();
        if (selectedMonth != null && !"全部".equals(selectedMonth)) {
            LocalDate monthDate = LocalDate.parse(selectedMonth + "-01");
            reports = reports.stream()
                .filter(r -> r.getReportMonth() != null && 
                    r.getReportMonth().getYear() == monthDate.getYear() &&
                    r.getReportMonth().getMonthValue() == monthDate.getMonthValue())
                .collect(Collectors.toList());
        }
        
        // 日期范围过滤
        java.util.Date startDateValue = (java.util.Date) startDateSpinner.getValue();
        LocalDate startDate = new java.sql.Date(startDateValue.getTime()).toLocalDate();
        java.util.Date endDateValue = (java.util.Date) endDateSpinner.getValue();
        LocalDate endDate = new java.sql.Date(endDateValue.getTime()).toLocalDate();
        
        reports = reports.stream()
            .filter(r -> r.getReportMonth() != null && 
                !r.getReportMonth().isBefore(startDate) && 
                !r.getReportMonth().isAfter(endDate))
            .collect(Collectors.toList());
        
        // 提供者过滤（通过预约）
        Provider selectedProvider = (Provider) providerCombo.getSelectedItem();
        if (selectedProvider != null && appointmentService != null) {
            // 这里简化处理，实际应该通过appointment关联来过滤报告
            // 暂时保留所有报告，后续可以根据需要实现更精确的过滤逻辑
        }
        
        // 显示结果
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (HealthReport report : reports) {
            String verifierName = "";
            if (report.getVerifierId() != null && providerService != null) {
                Provider verifier = providerService.getProviderById(report.getVerifierId());
                if (verifier != null) {
                    verifierName = verifier.getName();
                }
            }
            
            tableModel.addRow(new Object[]{
                report.getReportId(),
                report.getReportMonth() != null ? report.getReportMonth().format(formatter) : "",
                report.getTotalSteps() != null ? report.getTotalSteps() : 0,
                report.getSummary() != null ? (report.getSummary().length() > 30 ? 
                    report.getSummary().substring(0, 30) + "..." : report.getSummary()) : "",
                verifierName
            });
        }
    }
    
    private void resetSearch() {
        monthCombo.setSelectedIndex(0);
        providerCombo.setSelectedIndex(0);
        startDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now().minusMonths(6)));
        endDateSpinner.setValue(java.sql.Date.valueOf(LocalDate.now()));
        performSearch();
    }
    
    private void viewReportDetail() {
        // 简化获取选中行
        JTable table = null;
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component c : panel.getComponents()) {
                    if (c instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) c;
                        if (scrollPane.getViewport().getView() instanceof JTable) {
                            table = (JTable) scrollPane.getViewport().getView();
                            break;
                        }
                    }
                }
            }
        }
        
        if (table == null) {
            // 直接查找表格
            table = findComponent(JTable.class, getContentPane());
        }
        
        if (table != null) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请选择要查看的报告", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String reportId = (String) tableModel.getValueAt(selectedRow, 0);
            HealthReport report = healthReportService.getReportById(reportId);
            
            if (report != null) {
                showReportDetailDialog(report);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Component> T findComponent(Class<T> type, Container container) {
        for (Component comp : container.getComponents()) {
            if (type.isInstance(comp)) {
                return (T) comp;
            }
            if (comp instanceof Container) {
                T found = findComponent(type, (Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
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
        
        String verifierName = "";
        if (report.getVerifierId() != null && providerService != null) {
            Provider verifier = providerService.getProviderById(report.getVerifierId());
            if (verifier != null) {
                verifierName = verifier.getName() + " (" + verifier.getLicenseNumber() + ")";
            }
        }
        addDetailRow(detailPanel, gbc, "验证者:", verifierName.isEmpty() ? "未验证" : verifierName, 4);
        
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
