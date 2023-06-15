package com.tidc.utils;

/**
 * 打卡工具类
 *
 * @author 宋佳豪
 * @version 1.0
 */

public class ClockInUtil {

    /**
     * 中心纬度
     */
    private static final double centerLat = "your centerLat";
    /**
     * 中心经度
     */
    private static final double centerLon = "your centerLon";
    /**
     * 地球半径
     */
    private static final double R = 6378.137;

    /**
     * 根据两点的经纬度计算大圆距离
     *
     * @param lat 当前纬度
     * @param lon 当前经度
     * @return 两点之间的距离(米为单位)
     */
    public static double calculateDistance(double lat, double lon) {
        double radCurrentLat = Math.toRadians(lat);
        double radCenterLat = Math.toRadians(centerLat);
        double radCurrentLon = Math.toRadians(lon);
        double radCenterLon = Math.toRadians(centerLon);
        double a = radCurrentLat - radCenterLat; // 两点纬度之差的弧度值
        double b = radCurrentLon - radCenterLon; // 两点经度之差的弧度值
        double s = 2 * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(a / 2), 2) + Math.cos(radCurrentLat) * Math.cos(radCenterLat) * Math.pow(Math.sin(b / 2), 2)
                )
        );
        double distance = s * R; // 弧度距离 * 地球半径, 以获取实际距离
        distance = (distance * 10000) / 10;
        return distance; // 返回实际距离的整数值(米)
    }

    /**
     * 获取纬度
     *
     * @return centerLat
     */
    public static double getCenterLat() {
        return centerLat;
    }

    /**
     * 获取经度
     *
     * @return centerLon
     */
    public static double getCenterLon() {
        return centerLon;
    }


}
