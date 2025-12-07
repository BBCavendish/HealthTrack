package org.healthtrack.dto;

/**
 * 健康指标统计结果
 */
public class HealthIndicatorStats {
    private String indicatorType;  // 指标类型：weight, blood_pressure_systolic, blood_pressure_diastolic
    private Double average;        // 平均值
    private Double min;           // 最小值
    private Double max;           // 最大值
    private int count;            // 数据点数量
    
    public HealthIndicatorStats(String indicatorType) {
        this.indicatorType = indicatorType;
        this.count = 0;
    }
    
    public String getIndicatorType() {
        return indicatorType;
    }
    
    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }
    
    public Double getAverage() {
        return average;
    }
    
    public void setAverage(Double average) {
        this.average = average;
    }
    
    public Double getMin() {
        return min;
    }
    
    public void setMin(Double min) {
        this.min = min;
    }
    
    public Double getMax() {
        return max;
    }
    
    public void setMax(Double max) {
        this.max = max;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
}

