package org.healthtrack.ui;

import org.healthtrack.HealthTrackApplication;
import org.healthtrack.dto.ActiveUserStats;
import org.healthtrack.entity.User;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 最活跃用户界面
 * 显示记录健康数据最多或完成挑战最多的用户
 */
public class ActiveUsersFrame extends JFrame {
    
    private UserService userService;
    private DefaultTableModel tableModel;
    private JComboBox<String> sortCombo;
    
    public ActiveUsersFrame(User currentUser) {
        super("HealthTrack - 最活跃用户");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            userService = HealthTrackApplication.getContext().getBean(UserService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadData();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("最活跃用户");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 排序选择面板
        JPanel sortPanel = UIStyleConstants.createCardPanel();
        sortPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        JLabel sortLabel = new JLabel("排序方式:");
        sortLabel.setFont(UIStyleConstants.FONT_LABEL);
        sortPanel.add(sortLabel);
        
        sortCombo = new JComboBox<>(new String[]{"综合", "健康记录数", "完成挑战数"});
        sortCombo.setFont(UIStyleConstants.FONT_TEXT);
        sortCombo.addActionListener(e -> loadData());
        sortPanel.add(sortCombo);
        
        // 表格面板
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"排名", "用户ID", "姓名", "健康记录数", "完成挑战数", "综合得分"},
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
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.PRIMARY_BLUE);
        refreshButton.addActionListener(e -> loadData());
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(sortPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 使用嵌套布局
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(sortPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        
        if (userService == null) {
            JOptionPane.showMessageDialog(this, "用户服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedSort = (String) sortCombo.getSelectedItem();
        String sortBy = "total";
        if (selectedSort != null) {
            switch (selectedSort) {
                case "健康记录数":
                    sortBy = "health_records";
                    break;
                case "完成挑战数":
                    sortBy = "challenges";
                    break;
                default:
                    sortBy = "total";
            }
        }
        
        java.util.List<ActiveUserStats> statsList = userService.getMostActiveUsers(20, sortBy);
        
        if (statsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到用户数据", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int rank = 1;
        for (ActiveUserStats stats : statsList) {
            var user = stats.getUser();
            tableModel.addRow(new Object[]{
                rank++,
                user.getHealthId(),
                user.getName(),
                stats.getHealthRecordCount(),
                stats.getCompletedChallengeCount(),
                stats.getTotalActivityScore()
            });
        }
    }
}

