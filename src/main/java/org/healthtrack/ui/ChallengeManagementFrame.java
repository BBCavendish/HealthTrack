package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.entity.WellnessChallenge;
import org.healthtrack.service.ParticipationService;
import org.healthtrack.service.WellnessChallengeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 健康挑战管理页面
 */
public class ChallengeManagementFrame extends JFrame {
    
    private final WellnessChallengeService challengeService;
    private final ParticipationService participationService;
    private final User currentUser;
    private DefaultListModel<String> challengeListModel;
    private JList<String> challengeList;
    
    public ChallengeManagementFrame(WellnessChallengeService challengeService,
                                   ParticipationService participationService,
                                   User currentUser) {
        super("HealthTrack - 健康挑战管理");
        this.challengeService = challengeService;
        this.participationService = participationService;
        this.currentUser = currentUser;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadChallenges();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("健康挑战管理");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 挑战列表
        JPanel listPanel = UIStyleConstants.createCardPanel();
        listPanel.setLayout(new BorderLayout());
        
        challengeListModel = new DefaultListModel<>();
        challengeList = new JList<>(challengeListModel);
        challengeList.setFont(UIStyleConstants.FONT_TEXT);
        JScrollPane scrollPane = new JScrollPane(challengeList);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        JButton createButton = UIStyleConstants.createModernButton("创建挑战", UIStyleConstants.SECONDARY_GREEN);
        JButton browseButton = UIStyleConstants.createModernButton("浏览所有挑战", UIStyleConstants.PRIMARY_BLUE);
        JButton detailButton = UIStyleConstants.createModernButton("挑战详情", UIStyleConstants.PRIMARY_BLUE);
        JButton progressButton = UIStyleConstants.createModernButton("进度跟踪", UIStyleConstants.ACCENT_ORANGE);
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.PURPLE);
        JButton backButton = UIStyleConstants.createModernButton("返回", UIStyleConstants.TEXT_SECONDARY);
        
        createButton.addActionListener(e -> openCreateChallengeFrame());
        browseButton.addActionListener(e -> openBrowseChallengesFrame());
        detailButton.addActionListener(e -> openChallengeDetailFrame());
        progressButton.addActionListener(e -> openProgressTrackingFrame());
        refreshButton.addActionListener(e -> loadChallenges());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(createButton);
        buttonPanel.add(browseButton);
        buttonPanel.add(detailButton);
        buttonPanel.add(progressButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(listPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private void loadChallenges() {
        if (challengeService == null) {
            JOptionPane.showMessageDialog(this, "挑战服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<WellnessChallenge> challenges = challengeService.getChallengesByCreator(currentUser.getHealthId());
        challengeListModel.clear();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (WellnessChallenge challenge : challenges) {
            String display = String.format("%s - %s (%s 至 %s)",
                challenge.getChallengeId(),
                challenge.getGoal() != null ? challenge.getGoal() : "无目标",
                challenge.getStartDate().format(formatter),
                challenge.getEndDate().format(formatter));
            challengeListModel.addElement(display);
        }
    }
    
    private void openCreateChallengeFrame() {
        CreateChallengeFrame frame = new CreateChallengeFrame(challengeService, currentUser);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                loadChallenges();
            }
        });
    }
    
    private void openChallengeDetailFrame() {
        int index = challengeList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "请选择要查看的挑战", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selected = challengeListModel.get(index);
        String challengeId = selected.split(" - ")[0];
        WellnessChallenge challenge = challengeService.getChallengeById(challengeId);
        
        if (challenge != null) {
            ChallengeDetailFrame frame = new ChallengeDetailFrame(challengeService, participationService, challenge, currentUser);
            frame.setVisible(true);
        }
    }
    
    private void openProgressTrackingFrame() {
        ProgressTrackingFrame frame = new ProgressTrackingFrame(participationService, challengeService, currentUser);
        frame.setVisible(true);
    }
    
    private void openBrowseChallengesFrame() {
        // 打开浏览所有挑战的窗口，用户可以查看并加入其他挑战
        List<WellnessChallenge> allChallenges = challengeService.getAllChallenges();
        if (allChallenges.isEmpty()) {
            JOptionPane.showMessageDialog(this, "当前没有可用的挑战", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "浏览所有挑战", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (WellnessChallenge challenge : allChallenges) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String display = String.format("%s - %s (%s 至 %s)",
                challenge.getChallengeId(),
                challenge.getGoal() != null ? challenge.getGoal() : "无目标",
                challenge.getStartDate().format(formatter),
                challenge.getEndDate().format(formatter));
            listModel.addElement(display);
        }
        
        JList<String> challengeList = new JList<>(listModel);
        challengeList.setFont(UIStyleConstants.FONT_TEXT);
        JScrollPane scrollPane = new JScrollPane(challengeList);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton viewButton = UIStyleConstants.createModernButton("查看详情", UIStyleConstants.PRIMARY_BLUE);
        JButton joinButton = UIStyleConstants.createModernButton("加入挑战", UIStyleConstants.SECONDARY_GREEN);
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        
        viewButton.addActionListener(e -> {
            int index = challengeList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(dialog, "请选择要查看的挑战", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String selected = listModel.get(index);
            String challengeId = selected.split(" - ")[0];
            WellnessChallenge challenge = challengeService.getChallengeById(challengeId);
            
            if (challenge != null) {
                dialog.dispose();
                ChallengeDetailFrame frame = new ChallengeDetailFrame(challengeService, participationService, challenge, currentUser);
                frame.setVisible(true);
            }
        });
        
        joinButton.addActionListener(e -> {
            int index = challengeList.getSelectedIndex();
            if (index == -1) {
                JOptionPane.showMessageDialog(dialog, "请选择要加入的挑战", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String selected = listModel.get(index);
            String challengeId = selected.split(" - ")[0];
            WellnessChallenge challenge = challengeService.getChallengeById(challengeId);
            
            if (challenge == null) {
                return;
            }
            
            // 检查是否已参与
            if (participationService != null && 
                participationService.isUserParticipating(currentUser.getHealthId(), challengeId)) {
                JOptionPane.showMessageDialog(dialog, "您已经参与此挑战", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // 检查是否是创建者
            if (challenge.getCreatorId().equals(currentUser.getHealthId())) {
                JOptionPane.showMessageDialog(dialog, "这是您创建的挑战，请在挑战详情页面加入", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "确定要加入此挑战吗？",
                "确认加入",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (participationService != null && 
                    participationService.joinChallenge(currentUser.getHealthId(), challengeId)) {
                    JOptionPane.showMessageDialog(dialog, "成功加入挑战！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "加入挑战失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(viewButton);
        buttonPanel.add(joinButton);
        buttonPanel.add(closeButton);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}

