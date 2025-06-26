package com.csu.unicorp.service.impl;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.service.MapService;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

/**
 * 地图服务实现类
 * 负责调用高德地图API获取地理位置、天气等信息
 */
@Service
@RequiredArgsConstructor
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
} 