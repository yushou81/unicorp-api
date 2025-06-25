package com.csu.linkneiapi.controller;

import java.math.BigDecimal;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.csu.linkneiapi.service.MapService;
import com.csu.linkneiapi.vo.ResultVO;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
@Tag(name = "地图服务", description = "提供与高德地图相关的API")
public class MapController {

    private final MapService mapService;

    @Operation(summary = "根据经纬度获取天气信息", description = "调用高德地图API，根据经纬度获取实时天气")
    @GetMapping("/weather")
    public ResultVO<JsonNode> getWeatherByLocation(@RequestParam BigDecimal lon, @RequestParam BigDecimal lat) {
        JsonNode weatherInfo = mapService.getWeatherByLocation(lon, lat);
        return ResultVO.success(weatherInfo);
    }

    @Operation(summary = "地址转经纬度（地理编码）", description = "将结构化的地址信息转换为经纬度坐标")
    @GetMapping("/geocode")
    public ResultVO<JsonNode> getGeoCodeByAddress(@RequestParam String address) {
        JsonNode geoCode = mapService.getGeoCodeByAddress(address);
        return ResultVO.success(geoCode);
    }

    @Operation(summary = "经纬度转地址（逆地理编码）", description = "将经纬度坐标转换为详细的地址描述")
    @GetMapping("/regeocode")
    public ResultVO<JsonNode> getReGeoCodeByLocation(@RequestParam BigDecimal lon, @RequestParam BigDecimal lat) {
        JsonNode reGeoCode = mapService.getReGeoCodeByLocation(lon, lat);
        return ResultVO.success(reGeoCode);
    }

    @Operation(summary = "获取高德静态地图图片", description = "根据参数获取静态地图图片")
    @GetMapping(value = "/staticmap", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getStaticMap(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String zoom,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String scale,
            @RequestParam(required = false) String markers,
            @RequestParam(required = false) String labels,
            @RequestParam(required = false) String paths,
            @RequestParam(required = false) String traffic
    ) {
        byte[] image = mapService.getStaticMap(location, zoom, size, scale, markers, labels, paths, traffic);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }
} 