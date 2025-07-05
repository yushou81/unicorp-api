package com.csu.unicorp.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.service.MapService;
import com.csu.unicorp.vo.ResultVO;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/v1/map", "/api/v1/map"})
@RequiredArgsConstructor
@Tag(name = "地图服务", description = "提供与高德地图相关的API")
public class MapController {

    private final MapService mapService;

    @Operation(summary = "获取地图配置", description = "获取地图配置信息，包含加载地图的JS URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取配置成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/config")
    public ResultVO<Map<String, String>> getMapConfig() {
        Map<String, String> config = mapService.getMapConfig();
        return ResultVO.success("获取地图配置成功", config);
    }

    @Operation(summary = "获取地图JavaScript脚本", description = "加载高德地图JavaScript脚本")
    @GetMapping("/js")
    public void getMapScript(HttpServletResponse response) throws IOException {
        mapService.getMapScript(response);
    }

    @Operation(summary = "搜索地点", description = "根据关键字搜索地点")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "搜索参数无效",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/search")
    public ResultVO<JsonNode> search(@RequestParam String keyword) {
        try {
            // 使用searchLocation方法
            JsonNode result = mapService.searchLocation(keyword);
            
            int count = result.path("count").asInt(0);
            String message = String.format("搜索成功，找到%d条结果", count);
            
            // 如果有建议城市，添加到消息中
            if (result.has("suggestion") && result.path("suggestion").has("cities") && 
                result.path("suggestion").path("cities").isArray() && 
                result.path("suggestion").path("cities").size() > 0) {
                message += "，同时有相关城市建议";
            }
            
            return ResultVO.success(message, result);
        } catch (Exception e) {
            return ResultVO.error("搜索失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "指定城市搜索地点", description = "根据关键字在指定城市内搜索地点")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "搜索参数无效",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/search-city")
    public ResultVO<JsonNode> searchLocationInCity(
            @RequestParam String keyword, 
            @RequestParam(required = false) String city) {
        try {
            // 直接使用高级搜索API
            JsonNode result = mapService.searchLocationInCity(keyword, city);
            
            int count = result.path("count").asInt(0);
            String message = String.format("在%s内搜索成功，找到%d条结果", city, count);
            
            return ResultVO.success(message, result);
        } catch (Exception e) {
            return ResultVO.error("搜索失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取附近兴趣点", description = "根据经纬度获取附近兴趣点")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数无效",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/nearby-poi")
    public ResultVO<JsonNode> getNearbyPoi(
            @RequestParam BigDecimal lon, 
            @RequestParam BigDecimal lat,
            @RequestParam(required = false, defaultValue = "1000") Integer radius) {
        JsonNode result = mapService.getNearbyPoi(lon, lat, radius);
        return ResultVO.success("获取附近兴趣点成功", result);
    }

    @Operation(summary = "根据经纬度获取天气信息", description = "调用高德地图API，根据经纬度获取实时天气")
    @GetMapping("/weather")
    public ResultVO<JsonNode> getWeatherByLocation(@RequestParam BigDecimal lon, @RequestParam BigDecimal lat) {
        JsonNode weatherInfo = mapService.getWeatherByLocation(lon, lat);
        return ResultVO.success("获取天气信息成功", weatherInfo);
    }

    @Operation(summary = "地址转经纬度（地理编码）", description = "将结构化的地址信息转换为经纬度坐标")
    @GetMapping("/geocode")
    public ResultVO<JsonNode> getGeoCodeByAddress(@RequestParam String address) {
        JsonNode geoCode = mapService.getGeoCodeByAddress(address);
        return ResultVO.success("地址转经纬度成功", geoCode);
    }

    @Operation(summary = "经纬度转地址（逆地理编码）", description = "将经纬度坐标转换为详细的地址描述")
    @GetMapping("/regeocode")
    public ResultVO<JsonNode> getReGeoCodeByLocation(@RequestParam BigDecimal lon, @RequestParam BigDecimal lat) {
        JsonNode reGeoCode = mapService.getReGeoCodeByLocation(lon, lat);
        return ResultVO.success("经纬度转地址成功", reGeoCode);
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
    
    @Operation(summary = "获取动态地图配置", description = "获取用于前端渲染的高德动态地图配置")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取配置成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/dynamicmap")
    public ResultVO<Map<String, Object>> getDynamicMap(
            @RequestParam(required = false) String center,
            @RequestParam(required = false) Integer zoom,
            @RequestParam(required = false) String markers
    ) {
        Map<String, Object> config = mapService.getDynamicMapConfig(center, zoom, markers);
        return ResultVO.success("获取动态地图配置成功", config);
    }

    @Operation(summary = "高级地点搜索", description = "使用高德地图API进行地点搜索")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "搜索参数无效",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/advanced-search")
    public ResultVO<JsonNode> advancedSearch(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String types,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer children,
            @RequestParam(required = false, defaultValue = "20") Integer offset,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "all") String extensions) {
        
        if ((keywords == null || keywords.isBlank()) && (types == null || types.isBlank())) {
            return ResultVO.error("关键词和POI类型至少需要提供一个");
        }
        
        try {
            JsonNode result = mapService.advancedSearch(keywords, types, city, 
                                                  children, offset, page, extensions);
            
            int count = result.path("count").asInt(0);
            String message = String.format("搜索成功，找到%d条结果", count);
            
            // 如果有建议城市，添加到消息中
            if (result.has("suggestion") && result.path("suggestion").has("cities") && 
                result.path("suggestion").path("cities").isArray() && 
                result.path("suggestion").path("cities").size() > 0) {
                message += "，同时有相关城市建议";
            }
            
            return ResultVO.success(message, result);
        } catch (BusinessException e) {
            return ResultVO.error(e.getMessage());
        } catch (Exception e) {
            return ResultVO.error("搜索过程中发生错误：" + e.getMessage());
        }
    }

    @Operation(summary = "精确格式搜索", description = "使用完全匹配高德API格式的搜索，保持中文原样")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "搜索参数无效",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/exact-search")
    public ResultVO<JsonNode> exactSearch(
            @RequestParam String keywords,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "20") Integer offset,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "all") String extensions) {
        try {
            // 使用精确格式搜索
            JsonNode result = mapService.exactFormatSearch(keywords, city, offset, page, extensions);
            
            int count = result.path("count").asInt(0);
            String message = String.format("搜索成功，找到%d条结果", count);
            
            return ResultVO.success(message, result);
        } catch (BusinessException e) {
            return ResultVO.error(e.getMessage());
        } catch (Exception e) {
            return ResultVO.error("搜索失败: " + e.getMessage());
        }
    }
} 