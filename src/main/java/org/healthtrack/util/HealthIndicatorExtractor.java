package org.healthtrack.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 健康指标提取工具类
 * 从健康报告的summary字段中提取体重、血压等指标
 */
public class HealthIndicatorExtractor {
    
    // 体重模式：匹配 "体重: 70kg", "weight: 70", "70kg" 等
    private static final Pattern WEIGHT_PATTERN = Pattern.compile(
        "(?:体重|weight)[:：]?\\s*(\\d+(?:\\.\\d+)?)\\s*(?:kg|公斤)?", 
        Pattern.CASE_INSENSITIVE
    );
    
    // 血压模式：匹配 "血压: 120/80", "blood pressure: 120/80", "120/80 mmHg" 等
    private static final Pattern BLOOD_PRESSURE_PATTERN = Pattern.compile(
        "(?:血压|blood\\s*pressure|bp)[:：]?\\s*(\\d+)\\s*/\\s*(\\d+)\\s*(?:mmHg)?", 
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 从文本中提取体重值（单位：kg）
     * @param text 文本内容
     * @return 体重值，如果未找到返回null
     */
    public static Double extractWeight(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = WEIGHT_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 从文本中提取血压值
     * @param text 文本内容
     * @return 血压对象（包含收缩压和舒张压），如果未找到返回null
     */
    public static BloodPressure extractBloodPressure(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = BLOOD_PRESSURE_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                int systolic = Integer.parseInt(matcher.group(1));
                int diastolic = Integer.parseInt(matcher.group(2));
                return new BloodPressure(systolic, diastolic);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 从文本中提取所有体重值
     * @param text 文本内容
     * @return 体重值列表
     */
    public static List<Double> extractAllWeights(String text) {
        List<Double> weights = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return weights;
        }
        
        Matcher matcher = WEIGHT_PATTERN.matcher(text);
        while (matcher.find()) {
            try {
                weights.add(Double.parseDouble(matcher.group(1)));
            } catch (NumberFormatException e) {
                // 忽略无效值
            }
        }
        return weights;
    }
    
    /**
     * 从文本中提取所有血压值
     * @param text 文本内容
     * @return 血压值列表
     */
    public static List<BloodPressure> extractAllBloodPressures(String text) {
        List<BloodPressure> pressures = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return pressures;
        }
        
        Matcher matcher = BLOOD_PRESSURE_PATTERN.matcher(text);
        while (matcher.find()) {
            try {
                int systolic = Integer.parseInt(matcher.group(1));
                int diastolic = Integer.parseInt(matcher.group(2));
                pressures.add(new BloodPressure(systolic, diastolic));
            } catch (NumberFormatException e) {
                // 忽略无效值
            }
        }
        return pressures;
    }
    
    /**
     * 血压值类
     */
    public static class BloodPressure {
        private final int systolic;   // 收缩压
        private final int diastolic;  // 舒张压
        
        public BloodPressure(int systolic, int diastolic) {
            this.systolic = systolic;
            this.diastolic = diastolic;
        }
        
        public int getSystolic() {
            return systolic;
        }
        
        public int getDiastolic() {
            return diastolic;
        }
        
        @Override
        public String toString() {
            return systolic + "/" + diastolic;
        }
    }
}

