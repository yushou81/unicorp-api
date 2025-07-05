package com.csu.unicorp.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.service.MapService;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 地图服务实现类
 * 负责调用高德地图API获取地理位置、天气等信息
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MapServiceImpl implements MapService {

    private final RestTemplate restTemplate;

    @Value("${amap.key}")
    private String amapKey;
    // 结构化地址转经纬度api
    private static final String AMAP_GEOCODE_URL = "https://restapi.amap.com/v3/geocode/geo?address={address}&key={key}&output=JSON";
    // 经纬度转结构化地址api
    private static final String AMAP_REGEOCODE_URL = "https://restapi.amap.com/v3/geocode/regeo?location={location}&key={key}&output=JSON";
    // 根据adcode获取天气信息api
    private static final String AMAP_WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo?city={city}&key={key}&output=JSON";
    // 搜索地点api
    private static final String AMAP_SEARCH_URL = "https://restapi.amap.com/v3/place/text?key={key}&keywords={keywords}&city={city}&citylimit={citylimit}&children={children}&offset={offset}&page={page}&types={types}&extensions={extensions}";
    // 周边搜索api
    private static final String AMAP_NEARBY_URL = "https://restapi.amap.com/v3/place/around?key={key}&location={location}&radius={radius}";

    @Override
    public Map<String, String> getMapConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("jsUrl", "/api/v1/map/js");
        return config;
    }

    @Override
    public void getMapScript(HttpServletResponse response) throws IOException {
        // 从高德获取地图脚本并转发，附加key
        String amapUrl = "https://webapi.amap.com/maps?v=2.0&key=" + amapKey + "&plugin=AMap.PlaceSearch,AMap.IndoorMap";
        
        URL url = new URL(amapUrl);
        URLConnection connection = url.openConnection();
        
        response.setContentType("application/javascript");
        
        try (InputStream in = connection.getInputStream();
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }

    @Override
    public JsonNode searchLocation(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException("搜索关键词不能为空");
        }
        
        log.info("开始搜索地点，关键词：{}", keyword);
        
        String city = "";
        if (keyword.contains("成都") || keyword.contains("四川")) {
            city = "510100";  // 成都市的城市编码
            log.info("检测到搜索成都相关地点，已添加城市: city={}", city);
        }
        
        // 调用高级搜索，修正参数
        return advancedSearch(keyword, null, city, null, 20, 1, "base");
    }

    @Override
    public JsonNode getNearbyPoi(BigDecimal longitude, BigDecimal latitude, Integer radius) {
        if (longitude == null || latitude == null) {
            throw new BusinessException("经纬度不能为空");
        }
        
        int searchRadius = (radius == null || radius <= 0) ? 1000 : radius;
        if (searchRadius > 50000) {
            searchRadius = 50000; // 高德API限制最大搜索半径为50000米
        }
        
        try {
            String location = longitude.toPlainString() + "," + latitude.toPlainString();
            JsonNode response = restTemplate.getForObject(AMAP_NEARBY_URL, JsonNode.class, amapKey, location, searchRadius);
            
            if (response == null || !"1".equals(response.path("status").asText())) {
                throw new BusinessException("获取附近兴趣点失败");
            }
            
            return response;
        } catch (Exception e) {
            throw new BusinessException("获取附近兴趣点失败: " + e.getMessage(), e);
        }
    }

    @Override
    public JsonNode getWeatherByLocation(BigDecimal longitude, BigDecimal latitude) {
        // 1. 根据经纬度获取adcode
        JsonNode regeoNode = getReGeoCodeByLocation(longitude, latitude);
        JsonNode regeocode = regeoNode.path("regeocode");
        if (regeocode.isMissingNode() || regeocode.path("addressComponent").isMissingNode() || regeocode.path("addressComponent").path("adcode").isMissingNode()) {
            throw new BusinessException("获取adcode失败");
        }
        String adcode = regeocode.path("addressComponent").path("adcode").asText();

        // 2. 根据adcode获取天气信息
        JsonNode weatherNode = restTemplate.getForObject(AMAP_WEATHER_URL, JsonNode.class, adcode, amapKey);
        if (weatherNode == null || !"1".equals(weatherNode.path("status").asText())) {
            throw new BusinessException("获取天气信息失败");
        }
        return weatherNode;
    }

    @Override
    public JsonNode getGeoCodeByAddress(String address) {
        JsonNode response = restTemplate.getForObject(AMAP_GEOCODE_URL, JsonNode.class, address, amapKey);
        if (response == null || !"1".equals(response.path("status").asText())) {
            throw new BusinessException("地址转经纬度失败");
        }
        return response;
    }

    @Override
    public JsonNode getReGeoCodeByLocation(BigDecimal longitude, BigDecimal latitude) {
        String location = longitude.toPlainString() + "," + latitude.toPlainString();
        JsonNode response = restTemplate.getForObject(AMAP_REGEOCODE_URL, JsonNode.class, location, amapKey);
        if (response == null || !"1".equals(response.path("status").asText())) {
            throw new BusinessException("经纬度转地址失败");
        }
        return response;
    }

    // 编码参数，保留逗号、冒号、分号
    private String encodeParam(String value) {
        if (value == null) return null;
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        encoded = encoded.replace("%2C", ",")
                         .replace("%3A", ":")
                         .replace("%3B", ";");
        return encoded;
    }

    @Override
    public byte[] getStaticMap(String location, String zoom, String size, String scale, String markers, String labels, String paths, String traffic) {
        StringBuilder url = new StringBuilder("https://restapi.amap.com/v3/staticmap?key=" + amapKey);
        if (location != null) url.append("&location=").append(encodeParam(location));
        if (zoom != null) url.append("&zoom=").append(encodeParam(zoom));
        if (size != null) url.append("&size=").append(encodeParam(size));
        if (scale != null) url.append("&scale=").append(encodeParam(scale));
        if (markers != null) url.append("&markers=").append(encodeParam(markers));
        if (labels != null) url.append("&labels=").append(encodeParam(labels));
        if (paths != null) url.append("&paths=").append(encodeParam(paths));
        if (traffic != null) url.append("&traffic=").append(encodeParam(traffic));
        return restTemplate.getForObject(url.toString(), byte[].class);
    }

    @Override
    public Map<String, Object> getDynamicMapConfig(String center, Integer zoom, String markers) {
        Map<String, Object> config = new HashMap<>();
        
        // 添加高德地图API密钥（不要在前端暴露真实的密钥，这里传递是为了演示）
        config.put("key", amapKey);
        
        // 设置地图初始中心点
        if (center != null && !center.isEmpty()) {
            config.put("center", center);
        } else {
            // 默认中心点（成都市中心）
            config.put("center", "104.066801,30.572961");
        }
        
        // 设置地图缩放级别
        if (zoom != null && zoom >= 3 && zoom <= 18) {
            config.put("zoom", zoom);
        } else {
            // 默认缩放级别
            config.put("zoom", 11);
        }
        
        // 处理标记点数据
        if (markers != null && !markers.isEmpty()) {
            try {
                // 尝试解析标记点数据
                List<Map<String, Object>> markersList = parseMarkers(markers);
                config.put("markers", markersList);
            } catch (Exception e) {
                log.warn("解析标记点数据失败: {}", e.getMessage());
                config.put("markers", new ArrayList<>());
            }
        } else {
            config.put("markers", new ArrayList<>());
        }
        
        // 添加地图类型、插件等配置
        config.put("mapType", "normal"); // 普通地图
        config.put("plugins", Arrays.asList("AMap.ToolBar", "AMap.Scale", "AMap.HawkEye")); // 常用插件
        config.put("viewMode", "3D"); // 3D视图
        
        return config;
    }

    /**
     * 解析标记点数据字符串为对象列表
     * 
     * 格式: "longitude1,latitude1,title1,content1|longitude2,latitude2,title2,content2"
     * 
     * @param markersStr 标记点数据字符串
     * @return 标记点列表
     */
    private List<Map<String, Object>> parseMarkers(String markersStr) {
        List<Map<String, Object>> markers = new ArrayList<>();
        
        if (markersStr == null || markersStr.isEmpty()) {
            return markers;
        }
        
        String[] markerArray = markersStr.split("\\|");
        for (String marker : markerArray) {
            String[] parts = marker.split(",", 4);
            if (parts.length >= 2) {
                Map<String, Object> markerMap = new HashMap<>();
                try {
                    markerMap.put("position", Arrays.asList(
                            Double.parseDouble(parts[0]), 
                            Double.parseDouble(parts[1])
                    ));
                    
                    // 如果有标题和内容，添加到标记点信息中
                    if (parts.length >= 3) {
                        markerMap.put("title", parts[2]);
                    }
                    if (parts.length >= 4) {
                        markerMap.put("content", parts[3]);
                    }
                    
                    // 设置默认图标和样式
                    markerMap.put("icon", "https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png");
                    Map<String, Object> labelOptions = new HashMap<>();
                    labelOptions.put("offset", Arrays.asList(0, -30));
                    markerMap.put("label", labelOptions);
                    
                    markers.add(markerMap);
                } catch (NumberFormatException e) {
                    log.warn("解析标记点坐标失败: {}", e.getMessage());
                }
            }
        }
        
        return markers;
    }

    @Override
    public JsonNode searchLocationInCity(String keyword, String city) {
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException("搜索关键词不能为空");
        }
        if (city == null || city.isBlank()) {
            throw new BusinessException("城市不能为空");
        }
        
        log.info("开始在指定城市搜索地点，关键词：{}，城市：{}", keyword, city);
        
        // 获取城市编码
        String cityCode = getCityCode(city);
        
        // 调用高级搜索，删除cityLimit参数
        return advancedSearch(keyword, null, cityCode, null, 20, 1, "base");
    }
    
    /**
     * 获取城市编码
     * 
     * @param city 城市名称
     * @return 城市编码
     */
    private String getCityCode(String city) {
        // 常见城市编码映射，实际应用中可以使用数据库或配置文件存储
        Map<String, String> cityCodeMap = new HashMap<>();
        cityCodeMap.put("北京", "110000");
        cityCodeMap.put("上海", "310000");
        cityCodeMap.put("广州", "440100");
        cityCodeMap.put("深圳", "440300");
        cityCodeMap.put("成都", "510100");
        cityCodeMap.put("重庆", "500000");
        cityCodeMap.put("武汉", "420100");
        cityCodeMap.put("杭州", "330100");
        cityCodeMap.put("南京", "320100");
        cityCodeMap.put("西安", "610100");
        cityCodeMap.put("天津", "120000");
        cityCodeMap.put("苏州", "320500");
        cityCodeMap.put("长沙", "430100");
        cityCodeMap.put("郑州", "410100");
        
        // 尝试直接匹配
        if (cityCodeMap.containsKey(city)) {
            return cityCodeMap.get(city);
        }
        
        // 尝试部分匹配
        for (Map.Entry<String, String> entry : cityCodeMap.entrySet()) {
            if (city.contains(entry.getKey()) || entry.getKey().contains(city)) {
                return entry.getValue();
            }
        }
        
        // 如果是四川的城市，默认使用成都
        if (city.contains("四川")) {
            return "510100";
        }
        
        // 默认返回输入的城市名称
        return city;
    }

    @Override
    public JsonNode advancedSearch(String keywords, String types, String city, 
                                 Integer children, Integer offset, Integer page, String extensions) {
        // 验证必填参数
        if ((keywords == null || keywords.isBlank()) && (types == null || types.isBlank())) {
            throw new BusinessException("关键词和POI类型至少需要提供一个");
        }
        
        // 设置默认值
        String searchKeywords = keywords != null ? keywords : "";
        String searchTypes = types != null ? types : "";
        String searchCity = city != null ? city : "";
        int searchChildren = children != null ? children : 0;
        int searchOffset = offset != null ? Math.min(offset, 25) : 20; // 强制限制不超过25
        int searchPage = page != null ? Math.max(page, 1) : 1;
        String searchExtensions = "base".equalsIgnoreCase(extensions) || "all".equalsIgnoreCase(extensions) ? extensions : "base";
        
        log.info("开始高德地图搜索，关键词：{}，类型：{}，城市：{}，层级展示：{}，每页数量：{}，页码：{}，扩展信息：{}", 
                searchKeywords, searchTypes, searchCity, searchChildren, searchOffset, searchPage, searchExtensions);
        
        try {
            // 使用RestTemplate的URI模板功能，让RestTemplate自动处理URL编码
            String uriTemplate = "https://restapi.amap.com/v3/place/text?keywords={keywords}&city={city}&offset={offset}&page={page}&key={key}&extensions={extensions}";
            
            // 准备URI变量
            Map<String, Object> uriVariables = new HashMap<>();
            uriVariables.put("keywords", searchKeywords);
            uriVariables.put("city", searchCity);
            uriVariables.put("offset", searchOffset);
            uriVariables.put("page", searchPage);
            uriVariables.put("key", amapKey);
            uriVariables.put("extensions", searchExtensions);
            
            // 如果有其他可选参数，动态添加到URI模板
            if (!searchTypes.isEmpty()) {
                uriTemplate += "&types={types}";
                uriVariables.put("types", searchTypes);
            }
            
            if (searchChildren > 0) {
                uriTemplate += "&children={children}";
                uriVariables.put("children", searchChildren);
            }
            
            // 记录日志，隐藏key
            String logUrl = uriTemplate.replace("{key}", "***");
            for (Map.Entry<String, Object> entry : uriVariables.entrySet()) {
                if (!entry.getKey().equals("key")) {
                    logUrl = logUrl.replace("{" + entry.getKey() + "}", entry.getValue().toString());
                }
            }
            log.info("高德搜索URL模板: {}", logUrl);
            
            // 发送请求，让RestTemplate处理URI变量替换和编码
            JsonNode response = restTemplate.getForObject(uriTemplate, JsonNode.class, uriVariables);
            
            if (response == null) {
                log.error("高德搜索返回为空");
                throw new BusinessException("搜索地点失败: 服务返回为空");
            }
            
            String status = response.path("status").asText("0");
            if (!"1".equals(status)) {
                String info = response.path("info").asText("未知错误");
                String infocode = response.path("infocode").asText("");
                log.error("高德搜索失败，状态码：{}，错误信息：{}，错误代码：{}", status, info, infocode);
                throw new BusinessException("搜索地点失败: " + info);
            }
            
            int count = response.path("count").asInt(0);
            log.info("高德搜索成功，找到结果数量：{}", count);
            
            // 详细记录搜索结果
            logSearchResults(response, searchKeywords);
            
            return response;
        } catch (Exception e) {
            if (!(e instanceof BusinessException)) {
                log.error("高德搜索异常：{}", e.getMessage(), e);
            }
            throw new BusinessException("搜索地点失败: " + e.getMessage(), e);
        }
    }

    /**
     * 记录搜索结果日志
     * 
     * @param response 搜索响应
     * @param keyword 搜索关键词
     */
    private void logSearchResults(JsonNode response, String keyword) {
        if (response == null) return;
        
        // 记录建议信息
        if (response.has("suggestion") && !response.path("suggestion").isEmpty()) {
            JsonNode suggestion = response.path("suggestion");
            if (suggestion.has("cities") && suggestion.path("cities").isArray() && suggestion.path("cities").size() > 0) {
                log.info("搜索建议城市列表：");
                for (int i = 0; i < suggestion.path("cities").size(); i++) {
                    JsonNode city = suggestion.path("cities").get(i);
                    log.info("  城市[{}]：名称={}，匹配数量={}，城市编码={}，区域编码={}",
                            i+1,
                            city.path("name").asText(""),
                            city.path("num").asText("0"),
                            city.path("citycode").asText(""),
                            city.path("adcode").asText(""));
                }
            }
        }
        
        // 记录POI信息
        if (response.has("pois") && response.path("pois").isArray()) {
            JsonNode pois = response.path("pois");
            int size = pois.size();
            log.info("关键词 '{}' 返回POI点数量：{}", keyword, size);
            
            if (log.isInfoEnabled() && size > 0) {
                int displayCount = Math.min(size, 3); // 只显示前3个结果详情
                log.info("前{}个搜索结果：", displayCount);
                
                for (int i = 0; i < displayCount; i++) {
                    JsonNode poi = pois.get(i);
                    log.info("  POI[{}]：名称={}，地址={}，位置={}，省份={}，城市={}，区县={}",
                            i+1,
                            poi.path("name").asText("未知"),
                            poi.path("address").asText("未知"),
                            poi.path("location").asText("未知"),
                            poi.path("pname").asText("未知"),
                            poi.path("cityname").asText("未知"),
                            poi.path("adname").asText("未知"));
                    
                    // 如果有扩展信息，记录关键扩展信息
                    if (poi.has("biz_ext")) {
                        JsonNode bizExt = poi.path("biz_ext");
                        if (bizExt.has("rating") || bizExt.has("cost")) {
                            log.info("    扩展信息：评分={}，人均消费={}",
                                    bizExt.path("rating").asText("无"),
                                    bizExt.path("cost").asText("无"));
                        }
                    }
                }
            }
        }
    }

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
    public JsonNode exactFormatSearch(String keywords, String city, Integer offset, Integer page, String extensions) {
        try {
            // 按照完全指定的格式构建URL，保持原始中文字符
            StringBuilder url = new StringBuilder("https://restapi.amap.com/v3/place/text?");
            
            // 按照指定的参数顺序
            url.append("keywords=").append(keywords); // 保持原始中文
            
            if (city != null && !city.isEmpty()) {
                url.append("&city=").append(city);
            }
            
            if (offset != null) {
                url.append("&offset=").append(offset);
            }
            
            if (page != null) {
                url.append("&page=").append(page);
            }
            
            url.append("&key=").append(amapKey);
            
            if (extensions != null && !extensions.isEmpty()) {
                url.append("&extensions=").append(extensions);
            }
            
            String finalUrl = url.toString();
            log.info("高德搜索精确URL (隐藏key): {}", finalUrl.replaceAll("key=[^&]+", "key=***"));
            
            // 直接请求，无需编码
            JsonNode response = restTemplate.getForObject(finalUrl, JsonNode.class);
            
            if (response == null) {
                log.error("高德搜索返回为空");
                throw new BusinessException("搜索地点失败: 服务返回为空");
            }
            
            String status = response.path("status").asText("0");
            if (!"1".equals(status)) {
                String info = response.path("info").asText("未知错误");
                String infocode = response.path("infocode").asText("");
                log.error("高德搜索失败，状态码：{}，错误信息：{}，错误代码：{}", status, info, infocode);
                throw new BusinessException("搜索地点失败: " + info);
            }
            
            int count = response.path("count").asInt(0);
            log.info("高德搜索成功，找到结果数量：{}", count);
            
            // 详细记录搜索结果
            logSearchResults(response, keywords);
            
            return response;
        } catch (Exception e) {
            if (!(e instanceof BusinessException)) {
                log.error("高德搜索异常：{}", e.getMessage(), e);
            }
            throw new BusinessException("搜索地点失败: " + e.getMessage(), e);
        }
    }
} 