package org.healthtrack.ui;

import org.healthtrack.HealthTrackApplication;
import org.healthtrack.entity.Invitation;
import org.healthtrack.entity.User;
import org.healthtrack.entity.WellnessChallenge;
import org.healthtrack.service.InvitationService;
import org.healthtrack.service.ParticipationService;
import org.healthtrack.service.UserService;
import org.healthtrack.service.WellnessChallengeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 挑战详情页面
 */
public class ChallengeDetailFrame extends JFrame {
    
    private final WellnessChallenge challenge;
    private final User currentUser;
    private final InvitationService invitationService;
    private final ParticipationService participationService;
    private final UserService userService;
    
    public ChallengeDetailFrame(WellnessChallengeService challengeService,
                               ParticipationService participationService,
                               WellnessChallenge challenge,
                               User currentUser) {
        super("HealthTrack - 挑战详情");
        this.challenge = challenge;
        this.currentUser = currentUser;
        this.participationService = participationService;
        
        // 获取服务
        this.invitationService = HealthTrackApplication.getContext().getBean(InvitationService.class);
        this.userService = HealthTrackApplication.getContext().getBean(UserService.class);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("挑战详情");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 详情卡片
        JPanel cardPanel = UIStyleConstants.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        addDetailRow(cardPanel, gbc, "挑战ID:", challenge.getChallengeId(), 0);
        addDetailRow(cardPanel, gbc, "目标:", challenge.getGoal() != null ? challenge.getGoal() : "无", 1);
        addDetailRow(cardPanel, gbc, "开始日期:", challenge.getStartDate().format(formatter), 2);
        addDetailRow(cardPanel, gbc, "结束日期:", challenge.getEndDate().format(formatter), 3);
        addDetailRow(cardPanel, gbc, "描述:", challenge.getDescription() != null ? challenge.getDescription() : "无", 4);
        
        if (participationService != null) {
            int participants = participationService.getChallengeParticipantsCount(challenge.getChallengeId());
            addDetailRow(cardPanel, gbc, "参与人数:", String.valueOf(participants), 5);
        }
        
        // 检查当前用户是否已参与
        boolean isParticipating = participationService != null && 
            participationService.isUserParticipating(currentUser.getHealthId(), challenge.getChallengeId());
        boolean isCreator = challenge.getCreatorId().equals(currentUser.getHealthId());
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        
        // 如果是创建者，显示邀请按钮
        if (isCreator) {
            JButton inviteButton = UIStyleConstants.createModernButton("邀请他人", UIStyleConstants.SECONDARY_GREEN);
            inviteButton.addActionListener(e -> showInviteDialog());
            buttonPanel.add(inviteButton);
            
            // 如果创建者还没有参与，也显示加入按钮
            if (!isParticipating) {
                JButton joinButton = UIStyleConstants.createModernButton("加入挑战", UIStyleConstants.PRIMARY_BLUE);
                joinButton.addActionListener(e -> joinChallenge());
                buttonPanel.add(joinButton);
            }
        } else {
            // 如果不是创建者且未参与，显示加入按钮
            if (!isParticipating) {
                JButton joinButton = UIStyleConstants.createModernButton("加入挑战", UIStyleConstants.PRIMARY_BLUE);
                joinButton.addActionListener(e -> joinChallenge());
                buttonPanel.add(joinButton);
            }
        }
        
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void showInviteDialog() {
        JDialog dialog = new JDialog(this, "邀请他人加入挑战", true);
        dialog.getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("选择要邀请的用户（可多选）");
        titleLabel.setFont(UIStyleConstants.FONT_HEADING);
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 用户列表（多选）
        JPanel listPanel = UIStyleConstants.createCardPanel();
        listPanel.setLayout(new BorderLayout());
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        java.util.List<User> allUsers = userService != null ? userService.getAllUsers() : java.util.Collections.emptyList();
        java.util.Map<String, User> userMap = new java.util.HashMap<>();
        
        // 过滤掉当前用户和已参与的用户
        for (User user : allUsers) {
            if (user.getHealthId().equals(currentUser.getHealthId())) {
                continue; // 跳过自己
            }
            
            // 检查是否已参与
            if (participationService != null && 
                participationService.isUserParticipating(user.getHealthId(), challenge.getChallengeId())) {
                continue; // 跳过已参与的用户
            }
            
            String display = user.getName() + " (" + user.getHealthId() + ")";
            listModel.addElement(display);
            userMap.put(display, user);
        }
        
        if (listModel.isEmpty()) {
            listModel.addElement("没有可邀请的用户");
        }
        
        JList<String> userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userList.setFont(UIStyleConstants.FONT_TEXT);
        JScrollPane scrollPane = new JScrollPane(userList);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton inviteButton = UIStyleConstants.createModernButton("发送邀请", UIStyleConstants.SECONDARY_GREEN);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        inviteButton.addActionListener(e -> {
            int[] selectedIndices = userList.getSelectedIndices();
            if (selectedIndices.length == 0) {
                JOptionPane.showMessageDialog(dialog, "请至少选择一个用户", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int successCount = 0;
            int failCount = 0;
            
            for (int index : selectedIndices) {
                String display = listModel.get(index);
                User invitee = userMap.get(display);
                
                if (invitee == null) {
                    failCount++;
                    continue;
                }
                
                // 获取用户邮箱（用于邀请）
                String contact = invitee.getHealthId();
                if (userService != null) {
                    try {
                        var primaryEmail = userService.getPrimaryEmail(invitee.getHealthId());
                        if (primaryEmail != null && primaryEmail.getEmailAddress() != null) {
                            contact = primaryEmail.getEmailAddress();
                        }
                    } catch (Exception ex) {
                        // 使用健康ID作为联系方式
                    }
                }
                
                // 创建邀请
                Invitation invitation = new Invitation();
                invitation.setInvitationId("INV" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                invitation.setInviterId(currentUser.getHealthId());
                invitation.setInviteeContact(contact);
                invitation.setRelatedChallengeId(challenge.getChallengeId());
                invitation.setInvitationType("Challenge");
                invitation.setStatus("Pending");
                invitation.setSentTime(LocalDateTime.now());
                invitation.setExpiredTime(LocalDateTime.now().plusDays(7)); // 7天后过期
                
                if (invitationService != null && invitationService.saveInvitation(invitation)) {
                    successCount++;
                } else {
                    failCount++;
                }
            }
            
            String message;
            if (failCount == 0) {
                message = "成功邀请 " + successCount + " 位用户！";
            } else {
                message = "成功邀请 " + successCount + " 位用户，失败 " + failCount + " 位";
            }
            JOptionPane.showMessageDialog(dialog, message, successCount > 0 ? "成功" : "提示", 
                successCount > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            
            // 刷新列表，移除已邀请的用户
            if (successCount > 0) {
                refreshUserList(listModel, userMap);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(inviteButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(listPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void refreshUserList(DefaultListModel<String> listModel, java.util.Map<String, User> userMap) {
        listModel.clear();
        userMap.clear();
        
        java.util.List<User> allUsers = userService != null ? userService.getAllUsers() : java.util.Collections.emptyList();
        
        for (User user : allUsers) {
            if (user.getHealthId().equals(currentUser.getHealthId())) {
                continue; // 跳过自己
            }
            
            // 检查是否已参与
            if (participationService != null && 
                participationService.isUserParticipating(user.getHealthId(), challenge.getChallengeId())) {
                continue; // 跳过已参与的用户
            }
            
            String display = user.getName() + " (" + user.getHealthId() + ")";
            listModel.addElement(display);
            userMap.put(display, user);
        }
        
        if (listModel.isEmpty()) {
            listModel.addElement("没有可邀请的用户");
        }
    }
    
    private void joinChallenge() {
        if (participationService == null) {
            JOptionPane.showMessageDialog(this, "参与服务未初始化", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "确定要加入此挑战吗？",
            "确认加入",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (participationService.joinChallenge(currentUser.getHealthId(), challenge.getChallengeId())) {
                JOptionPane.showMessageDialog(this, "成功加入挑战！", "成功", JOptionPane.INFORMATION_MESSAGE);
                // 重新初始化UI以更新按钮状态
                getContentPane().removeAll();
                initUI();
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "加入挑战失败，可能已经参与", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(UIStyleConstants.FONT_LABEL);
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(UIStyleConstants.FONT_TEXT);
        valueComponent.setForeground(UIStyleConstants.TEXT_PRIMARY);
        panel.add(valueComponent, gbc);
    }
}

