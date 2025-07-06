package com.csu.unicorp.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 地图服务接口，用于处理地图相关功能
 */
public interface MapService {
    
    /**
     * 获取地图配置信息
     *
     * @return 地图配置信息
     */
    Map<String, String> getMapConfig();
    
    /**
     * 获取地图JavaScript脚本
     *
     * @param response HTTP响应对象
     * @throws IOException 如果发生I/O异常
     */
    void getMapScript(HttpServletResponse response) throws IOException;
    
    /**
     * 搜索地点
     *
     * @param keyword 搜索关键词
     * @return 搜索结果
     */
    JsonNode searchLocation(String keyword);
    
    /**
     * 在指定城市中搜索地点
     *
     * @param keyword 搜索关键词
     * @param city 城市名称或编码
     * @return 搜索结果
     */
    JsonNode searchLocationInCity(String keyword, String city);
    
    /**
     * 获取附近兴趣点
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 搜索半径（米）
     * @return 附近兴趣点信息
     */
    JsonNode getNearbyPoi(BigDecimal longitude, BigDecimal latitude, Integer radius);
    
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
    
    /**
     * 获取动态地图配置信息
     * 
     * @param center 地图中心点坐标（经度,纬度）
     * @param zoom 缩放级别（3-18）
     * @param markers 标记点数据
     * @return 动态地图配置信息
     */
    Map<String, Object> getDynamicMapConfig(String center, Integer zoom, String markers);
    
    /**
     * 高级地点搜索，支持高德地图API的所有主要参数
     *
     * @param keywords 查询关键字
     * @param types 查询POI类型，多个类型用"|"分割
     * @param city 查询城市，可选值：城市中文、citycode、adcode
     * @param children 是否按照层级展示子POI数据，1表示层级展示
     * @param offset 每页记录数据，建议不超过25
     * @param page 当前页数
     * @param extensions 返回结果控制，base或all
     * @return 搜索结果
     */
    JsonNode advancedSearch(String keywords, String types, String city,
                          Integer children, Integer offset, Integer page, String extensions);
    
    /**
     * 按照完全匹配的URL格式进行搜索，保持参数顺序和中文原样
     * 
     * @param keywords 查询关键字
     * @param city 城市名称
     * @param offset 每页记录数
     * @param page 页码
     * @param extensions 返回信息扩展
     * @return 搜索结果
     */
    JsonNode exactFormatSearch(String keywords, String city, Integer offset, Integer page, String extensions);
}