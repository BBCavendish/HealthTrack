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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 创建挑战页面
 */
public class CreateChallengeFrame extends JFrame {
    
    private final WellnessChallengeService challengeService;
    private final User currentUser;
    private final InvitationService invitationService;
    private final ParticipationService participationService;
    private final UserService userService;
    
    private JLabel challengeIdLabel;
    private JTextField goalField;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JTextArea descriptionArea;
    private String generatedChallengeId;
    
    public CreateChallengeFrame(WellnessChallengeService challengeService, User currentUser) {
        super("HealthTrack - 创建挑战");
        this.challengeService = challengeService;
        this.currentUser = currentUser;
        
        // 获取服务
        this.invitationService = HealthTrackApplication.getContext().getBean(InvitationService.class);
        this.participationService = HealthTrackApplication.getContext().getBean(ParticipationService.class);
        this.userService = HealthTrackApplication.getContext().getBean(UserService.class);
        
        // 生成挑战ID
        this.generatedChallengeId = generateChallengeId();
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
    }
    
    /**
     * 生成唯一的挑战ID
     */
    private String generateChallengeId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "CHL" + timestamp.substring(timestamp.length() - 8) + uuid.toUpperCase();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("创建健康挑战");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表单卡片
        JPanel cardPanel = UIStyleConstants.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        
        // 挑战ID（自动生成，只读显示）
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel idLabel = new JLabel("挑战ID:");
        idLabel.setFont(UIStyleConstants.FONT_LABEL);
        cardPanel.add(idLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        challengeIdLabel = new JLabel(generatedChallengeId);
        challengeIdLabel.setFont(UIStyleConstants.FONT_INPUT);
        challengeIdLabel.setForeground(UIStyleConstants.TEXT_SECONDARY);
        challengeIdLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        challengeIdLabel.setOpaque(true);
        challengeIdLabel.setBackground(UIStyleConstants.HEADER_BG);
        cardPanel.add(challengeIdLabel, gbc);
        
        // 目标
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel goalLabel = new JLabel("目标:");
        goalLabel.setFont(UIStyleConstants.FONT_LABEL);
        cardPanel.add(goalLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        goalField = UIStyleConstants.createModernTextField(20);
        cardPanel.add(goalField, gbc);
        
        // 开始日期
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel startDateLabel = new JLabel("开始日期 *:");
        startDateLabel.setFont(UIStyleConstants.FONT_LABEL);
        cardPanel.add(startDateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        SpinnerDateModel startDateModel = new SpinnerDateModel(
            java.sql.Date.valueOf(LocalDate.now()),
            null,
            null,
            java.util.Calendar.DAY_OF_MONTH
        );
        startDateSpinner = new JSpinner(startDateModel);
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        startDateSpinner.setFont(UIStyleConstants.FONT_INPUT);
        cardPanel.add(startDateSpinner, gbc);
        
        // 结束日期
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel endDateLabel = new JLabel("结束日期 *:");
        endDateLabel.setFont(UIStyleConstants.FONT_LABEL);
        cardPanel.add(endDateLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        SpinnerDateModel endDateModel = new SpinnerDateModel(
            java.sql.Date.valueOf(LocalDate.now().plusMonths(1)),
            null,
            null,
            java.util.Calendar.DAY_OF_MONTH
        );
        endDateSpinner = new JSpinner(endDateModel);
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endDateEditor);
        endDateSpinner.setFont(UIStyleConstants.FONT_INPUT);
        cardPanel.add(endDateSpinner, gbc);
        
        // 描述（加宽）
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel descLabel = new JLabel("描述:");
        descLabel.setFont(UIStyleConstants.FONT_LABEL);
        cardPanel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(8, 40); // 增加行数和列数
        descriptionArea.setFont(UIStyleConstants.FONT_INPUT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyleConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        descriptionArea.setBackground(Color.WHITE);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(400, 150));
        descScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        descScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cardPanel.add(descScroll, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = UIStyleConstants.createModernButton("创建并邀请", UIStyleConstants.SECONDARY_GREEN);
        JButton createOnlyButton = UIStyleConstants.createModernButton("仅创建", UIStyleConstants.PRIMARY_BLUE);
        JButton cancelButton = UIStyleConstants.createModernButton("取消", UIStyleConstants.DANGER_RED);
        
        saveButton.addActionListener(e -> createChallengeAndInvite());
        createOnlyButton.addActionListener(e -> createChallenge());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(createOnlyButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(650, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }
    
    private void createChallenge() {
        String goal = goalField.getText().trim();
        java.util.Date startDateValue = (java.util.Date) startDateSpinner.getValue();
        LocalDate startDate = new java.sql.Date(startDateValue.getTime()).toLocalDate();
        java.util.Date endDateValue = (java.util.Date) endDateSpinner.getValue();
        LocalDate endDate = new java.sql.Date(endDateValue.getTime()).toLocalDate();
        String description = descriptionArea.getText().trim();
        
        if (endDate.isBefore(startDate)) {
            JOptionPane.showMessageDialog(this,
                "结束日期不能早于开始日期！",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        WellnessChallenge challenge = new WellnessChallenge();
        challenge.setChallengeId(generatedChallengeId);
        challenge.setGoal(goal.isEmpty() ? null : goal);
        challenge.setStartDate(startDate);
        challenge.setEndDate(endDate);
        challenge.setDescription(description.isEmpty() ? null : description);
        challenge.setCreatorId(currentUser.getHealthId());
        
        if (challengeService.saveChallenge(challenge)) {
            // 创建者自动加入挑战
            if (participationService != null) {
                participationService.joinChallenge(currentUser.getHealthId(), generatedChallengeId);
            }
            
            JOptionPane.showMessageDialog(this,
                "挑战创建成功！\n挑战ID: " + generatedChallengeId + "\n您已自动加入此挑战",
                "成功",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "创建失败，请重试",
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createChallengeAndInvite() {
        // 先创建挑战
        String goal = goalField.getText().trim();
        java.util.Date startDateValue = (java.util.Date) startDateSpinner.getValue();
        LocalDate startDate = new java.sql.Date(startDateValue.getTime()).toLocalDate();
        java.util.Date endDateValue = (java.util.Date) endDateSpinner.getValue();
        LocalDate endDate = new java.sql.Date(endDateValue.getTime()).toLocalDate();
        String description = descriptionArea.getText().trim();
        
        if (endDate.isBefore(startDate)) {
            JOptionPane.showMessageDialog(this,
                "结束日期不能早于开始日期！",
                "输入错误",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        WellnessChallenge challenge = new WellnessChallenge();
        challenge.setChallengeId(generatedChallengeId);
        challenge.setGoal(goal.isEmpty() ? null : goal);
        challenge.setStartDate(startDate);
        challenge.setEndDate(endDate);
        challenge.setDescription(description.isEmpty() ? null : description);
        challenge.setCreatorId(currentUser.getHealthId());
        
        if (!challengeService.saveChallenge(challenge)) {
            JOptionPane.showMessageDialog(this,
                "创建失败，请重试",
                "错误",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 创建者自动加入挑战
        if (participationService != null) {
            participationService.joinChallenge(currentUser.getHealthId(), generatedChallengeId);
        }
        
        // 创建成功后，打开邀请对话框
        showInviteDialog(generatedChallengeId);
    }
    
    private void showInviteDialog(String challengeId) {
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
                participationService.isUserParticipating(user.getHealthId(), challengeId)) {
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
        JButton skipButton = UIStyleConstants.createModernButton("跳过", UIStyleConstants.TEXT_SECONDARY);
        JButton finishButton = UIStyleConstants.createModernButton("完成", UIStyleConstants.PRIMARY_BLUE);
        
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
                invitation.setRelatedChallengeId(challengeId);
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
                refreshUserList(listModel, userMap, challengeId);
            }
        });
        
        skipButton.addActionListener(e -> dialog.dispose());
        finishButton.addActionListener(e -> {
            dialog.dispose();
            dispose(); // 关闭创建挑战窗口
        });
        
        buttonPanel.add(inviteButton);
        buttonPanel.add(skipButton);
        buttonPanel.add(finishButton);
        
        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(listPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void refreshUserList(DefaultListModel<String> listModel, java.util.Map<String, User> userMap, String challengeId) {
        listModel.clear();
        userMap.clear();
        
        java.util.List<User> allUsers = userService != null ? userService.getAllUsers() : java.util.Collections.emptyList();
        
        for (User user : allUsers) {
            if (user.getHealthId().equals(currentUser.getHealthId())) {
                continue; // 跳过自己
            }
            
            // 检查是否已参与
            if (participationService != null && 
                participationService.isUserParticipating(user.getHealthId(), challengeId)) {
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
}
