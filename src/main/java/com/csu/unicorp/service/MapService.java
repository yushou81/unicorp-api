package com.csu.unicorp.service;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 地图服务接口，用于处理地图相关功能
 */
public interface MapService {
    
    /**
     * 根据经纬度获取天气信息
     *
     * @param lon 经度
     * @param lat 纬度
     * @return 天气信息JSON
     */
    JsonNode getWeatherByLocation(BigDecimal lon, BigDecimal lat);
    
    /**
     * 根据地址获取地理编码
     *
     * @param address 地址
     * @return 地理编码JSON
     */
    JsonNode getGeoCodeByAddress(String address);
    
    /**
     * 根据经纬度获取逆地理编码
     *
     * @param lon 经度
     * @param lat 纬度
     * @return 逆地理编码JSON
     */
    JsonNode getReGeoCodeByLocation(BigDecimal lon, BigDecimal lat);
    
    /**
     * 获取静态地图图片
     *
     * @param location 位置
     * @param zoom 缩放级别
     * @param size 图片大小
     * @param scale 图片缩放比例
     * @param markers 标记点
     * @param labels 标签
     * @param paths 路径
     * @param traffic 是否显示交通状况
     * @return 静态地图图片字节数组
     */
    byte[] getStaticMap(String location, String zoom, String size, String scale, 
                        String markers, String labels, String paths, String traffic);
}