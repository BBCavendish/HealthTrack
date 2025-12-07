package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.entity.Participation;
import org.healthtrack.service.ParticipationService;
import org.healthtrack.service.WellnessChallengeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 挑战进度跟踪页面
 */
public class ProgressTrackingFrame extends JFrame {
    
    private final ParticipationService participationService;
    private final WellnessChallengeService challengeService;
    private final User currentUser;
    private DefaultTableModel tableModel;
    private JTable progressTable;
    
    public ProgressTrackingFrame(ParticipationService participationService,
                               WellnessChallengeService challengeService,
                               User currentUser) {
        super("HealthTrack - 挑战进度跟踪");
        this.participationService = participationService;
        this.challengeService = challengeService;
        this.currentUser = currentUser;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadProgress();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("挑战进度跟踪");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表格面板
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"挑战ID", "进度", "排名"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        progressTable = new JTable(tableModel);
        progressTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressTable.setRowHeight(35);
        progressTable.setFont(UIStyleConstants.FONT_TEXT);
        progressTable.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        progressTable.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        
        JScrollPane scrollPane = new JScrollPane(progressTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.ACCENT_ORANGE);
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        
        refreshButton.addActionListener(e -> loadProgress());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
    
    private void loadProgress() {
        if (participationService == null) {
            JOptionPane.showMessageDialog(this, "参与服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Participation> participations = participationService.getParticipationsByUser(currentUser.getHealthId());
        tableModel.setRowCount(0);
        
        for (Participation participation : participations) {
            int rank = participationService.getUserRankInChallenge(currentUser.getHealthId(), participation.getChallengeId());
            tableModel.addRow(new Object[]{
                participation.getChallengeId(),
                participation.getProgress() != null ? participation.getProgress() + "%" : "0%",
                rank > 0 ? "#" + rank : "未排名"
            });
        }
    }
}

