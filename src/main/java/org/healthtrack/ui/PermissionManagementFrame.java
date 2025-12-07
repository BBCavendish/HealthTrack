package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.InvitationService;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 成员权限管理页面
 */
public class PermissionManagementFrame extends JFrame {
    
    private final UserService userService;
    private final InvitationService invitationService;
    private final User currentUser;
    private DefaultTableModel tableModel;
    private JTable memberTable;
    
    public PermissionManagementFrame(UserService userService,
                                    InvitationService invitationService,
                                    User currentUser) {
        super("HealthTrack - 成员权限管理");
        this.userService = userService;
        this.invitationService = invitationService;
        this.currentUser = currentUser;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
        loadMembers();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("成员权限管理");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 表格面板
        JPanel tablePanel = UIStyleConstants.createCardPanel();
        tablePanel.setLayout(new BorderLayout());
        
        tableModel = new DefaultTableModel(
            new Object[]{"健康ID", "姓名", "角色", "验证状态"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setRowHeight(35);
        memberTable.setFont(UIStyleConstants.FONT_TEXT);
        memberTable.getTableHeader().setFont(UIStyleConstants.FONT_HEADING);
        memberTable.getTableHeader().setBackground(UIStyleConstants.HEADER_BG);
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setOpaque(false);
        JButton refreshButton = UIStyleConstants.createModernButton("刷新", UIStyleConstants.ACCENT_ORANGE);
        JButton closeButton = UIStyleConstants.createModernButton("关闭", UIStyleConstants.TEXT_SECONDARY);
        
        refreshButton.addActionListener(e -> loadMembers());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private void loadMembers() {
        if (currentUser.getFamilyId() == null || currentUser.getFamilyId().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "您还没有加入家庭组",
                "提示",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        List<User> members = userService.getUsersByFamilyId(currentUser.getFamilyId());
        tableModel.setRowCount(0);
        
        for (User member : members) {
            tableModel.addRow(new Object[]{
                member.getHealthId(),
                member.getName(),
                member.getRole() != null ? member.getRole() : "",
                member.getVerificationStatus() != null ? member.getVerificationStatus() : ""
            });
        }
    }
}

