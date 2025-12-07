package org.healthtrack.ui;

import org.healthtrack.HealthTrackApplication;
import org.healthtrack.dto.ChallengeWithParticipants;
import org.healthtrack.entity.User;
import org.healthtrack.service.WellnessChallengeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * 最受欢迎挑战界面
 * 显示参与人数最多的健康挑战
 */
public class PopularChallengesFrame extends JFrame {
    
    private WellnessChallengeService challengeService;
    private DefaultTableModel tableModel;
    
    public PopularChallengesFrame(User currentUser) {
        super("HealthTrack - 最受欢迎挑战");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            challengeService = HealthTrackApplication.getContext().getBean(WellnessChallengeService.class);
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
        JLabel titleLabel = UIStyleConstants.createTitleLabel("最受欢迎挑战");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 说明文字
        JLabel descLabel = new JLabel("按参与人数排序，显示最受欢迎的健康挑战");
        descLabel.setFont(UIStyleConstants.FONT_TEXT);
        descLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        titlePanel.add(descLabel, BorderLayout.SOUTH);
        
        // 表格面板
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"排名", "挑战ID", "目标", "开始日期", "结束日期", "参与人数"},
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
        
        // 刷新按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.PRIMARY_BLUE);
        refreshButton.addActionListener(e -> loadData());
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        
        if (challengeService == null) {
            JOptionPane.showMessageDialog(this, "挑战服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        java.util.List<ChallengeWithParticipants> challenges = challengeService.getMostPopularChallenges(20);
        
        if (challenges.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到挑战数据", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int rank = 1;
        for (ChallengeWithParticipants cwp : challenges) {
            var challenge = cwp.getChallenge();
            tableModel.addRow(new Object[]{
                rank++,
                challenge.getChallengeId(),
                challenge.getGoal() != null ? (challenge.getGoal().length() > 30 ? 
                    challenge.getGoal().substring(0, 30) + "..." : challenge.getGoal()) : "无",
                challenge.getStartDate() != null ? challenge.getStartDate().format(formatter) : "无",
                challenge.getEndDate() != null ? challenge.getEndDate().format(formatter) : "无",
                cwp.getParticipantCount()
            });
        }
    }
}

