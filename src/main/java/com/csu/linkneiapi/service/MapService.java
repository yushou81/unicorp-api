package com.csu.linkneiapi.service;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 地图服务，用于调用高德地图API
 */
public interface MapService {
    /**
     * 根据经纬度查询天气
     * @param longitude 经度
     * @param latitude 纬度
     * @return 天气信息
     */
    JsonNode getWeatherByLocation(BigDecimal longitude, BigDecimal latitude);

    /**
     * 根据结构化地址获取经纬度
     * @param address 结构化地址
     * @return 经纬度信息
     */
    JsonNode getGeoCodeByAddress(String address);

    /**
     * 根据经纬度获取地址信息（逆地理编码）
     * @param longitude 经度
     * @param latitude 纬度
     * @return 地址信息
     */
    JsonNode getReGeoCodeByLocation(BigDecimal longitude, BigDecimal latitude);

    /**
     * 获取高德静态地图图片
     * @param location 中心点经纬度（可选，格式：经度,纬度）
     * @param zoom 地图级别（可选，1-17）
     * @param size 图片大小（可选，默认400*400）
     * @param scale 普通/高清（可选，1或2）
     * @param markers 标注（可选）
     * @param labels 标签（可选）
     * @param paths 折线（可选）
     * @param traffic 交通路况（可选，0或1）
     * @return 图片二进制数据
     */
    byte[] getStaticMap(String location, String zoom, String size, String scale, String markers, String labels, String paths, String traffic);
}
