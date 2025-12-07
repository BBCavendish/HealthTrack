package org.healthtrack.service.impl;

import org.healthtrack.dto.HealthIndicatorStats;
import org.healthtrack.entity.HealthReport;
import org.healthtrack.mapper.HealthReportMapper;
import org.healthtrack.service.HealthReportService;
import org.healthtrack.util.HealthIndicatorExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 健康报告服务实现类
 * 处理健康报告相关的业务逻辑
 */
@Service
@Transactional
public class HealthReportServiceImpl implements HealthReportService {

    @Autowired
    private HealthReportMapper healthReportMapper;

    /**
     * 获取所有健康报告
     * @return 健康报告列表
     */
    @Override
    public List<HealthReport> getAllReports() {
        try {
            return healthReportMapper.findAll();
        } catch (Exception e) {
            System.err.println("获取健康报告列表失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 根据ID获取健康报告
     * @param reportId 报告ID
     * @return 健康报告对象，如果不存在返回null
     */
    @Override
    public HealthReport getReportById(String reportId) {
        try {
            return healthReportMapper.findById(reportId);
        } catch (Exception e) {
            System.err.println("根据ID获取健康报告失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 保存健康报告（新增或更新）
     * @param report 健康报告对象
     * @return 保存成功返回true，失败返回false
     */
    @Override
    public boolean saveReport(HealthReport report) {
        try {
            HealthReport existing = healthReportMapper.findById(report.getReportId());
            if (existing != null) {
                return healthReportMapper.update(report) > 0;
            } else {
                return healthReportMapper.insert(report) > 0;
            }
        } catch (Exception e) {
            System.err.println("保存健康报告失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 删除健康报告
     * @param reportId 报告ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    public boolean deleteReport(String reportId) {
        try {
            return healthReportMapper.delete(reportId) > 0;
        } catch (Exception e) {
            System.err.println("删除健康报告失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 根据用户ID获取健康报告
     * @param userId 用户ID
     * @return 该用户的健康报告列表
     */
    @Override
    public List<HealthReport> getReportsByUser(String userId) {
        try {
            return healthReportMapper.findByUserId(userId);
        } catch (Exception e) {
            System.err.println("获取用户健康报告失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 根据日期范围获取健康报告
     * @param start 开始日期
     * @param end 结束日期
     * @return 日期范围内的健康报告列表
     */
    @Override
    public List<HealthReport> getReportsByDateRange(LocalDate start, LocalDate end) {
        try {
            return healthReportMapper.findByMonthBetween(start, end);
        } catch (Exception e) {
            System.err.println("获取日期范围健康报告失败: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * 统计用户的健康报告数量
     * @param userId 用户ID
    极 * @return 该用户的健康报告数量
     */
    @Override
    public int countReportsByUser(String userId) {
        try {
            List<HealthReport> reports = healthReportMapper.findByUserId(userId);
            return reports != null ? reports.size() : 0;
        } catch (Exception e) {
            System.err.println("统计用户健康报告数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean verifyReport(String reportId, String verifierId) {
        try {
            HealthReport report = healthReportMapper.findById(reportId);
            if (report == null) return false;

            report.setVerifierId(verifierId);
            return healthReportMapper.update(report) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public HealthIndicatorStats getMonthlyIndicatorStats(String userId, String month, String indicatorType) {
        try {
            // 解析月份
            LocalDate monthDate = LocalDate.parse(month + "-01");
            LocalDate startDate = monthDate.withDayOfMonth(1);
            LocalDate endDate = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
            
            // 获取该月的所有报告
            List<HealthReport> reports = healthReportMapper.findByUserId(userId).stream()
                .filter(r -> r.getReportMonth() != null && 
                    !r.getReportMonth().isBefore(startDate) && 
                    !r.getReportMonth().isAfter(endDate))
                .collect(Collectors.toList());
            
            HealthIndicatorStats stats = new HealthIndicatorStats(indicatorType);
            
            if (reports.isEmpty()) {
                return stats;
            }
            
            // 根据指标类型提取数据
            List<Double> values = new java.util.ArrayList<>();
            
            for (HealthReport report : reports) {
                String summary = report.getSummary();
                if (summary == null || summary.trim().isEmpty()) {
                    continue;
                }
                
                switch (indicatorType) {
                    case "weight":
                        Double weight = HealthIndicatorExtractor.extractWeight(summary);
                        if (weight != null) {
                            values.add(weight);
                        }
                        break;
                    case "blood_pressure_systolic":
                        HealthIndicatorExtractor.BloodPressure bp = HealthIndicatorExtractor.extractBloodPressure(summary);
                        if (bp != null) {
                            values.add((double) bp.getSystolic());
                        }
                        break;
                    case "blood_pressure_diastolic":
                        HealthIndicatorExtractor.BloodPressure bp2 = HealthIndicatorExtractor.extractBloodPressure(summary);
                        if (bp2 != null) {
                            values.add((double) bp2.getDiastolic());
                        }
                        break;
                }
            }
            
            if (values.isEmpty()) {
                return stats;
            }
            
            // 计算统计值
            stats.setCount(values.size());
            stats.setMin(values.stream().mapToDouble(Double::doubleValue).min().orElse(0));
            stats.setMax(values.stream().mapToDouble(Double::doubleValue).max().orElse(0));
            stats.setAverage(values.stream().mapToDouble(Double::doubleValue).average().orElse(0));
            
            return stats;
        } catch (Exception e) {
            System.err.println("获取健康指标统计失败: " + e.getMessage());
            e.printStackTrace();
            return new HealthIndicatorStats(indicatorType);
        }
    }
}