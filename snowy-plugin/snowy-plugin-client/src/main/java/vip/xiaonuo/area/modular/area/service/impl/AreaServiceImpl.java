/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.area.modular.area.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.area.modular.area.entity.Area;
import vip.xiaonuo.area.modular.area.enums.AreaEnum;
import vip.xiaonuo.area.modular.area.mapper.AreaMapper;
import vip.xiaonuo.area.modular.area.param.AreaAddParam;
import vip.xiaonuo.area.modular.area.param.AreaEditParam;
import vip.xiaonuo.area.modular.area.param.AreaIdParam;
import vip.xiaonuo.area.modular.area.param.AreaPageParam;
import vip.xiaonuo.area.modular.area.service.AreaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 区域service接口实现类
 *
 * @author 胡勋
 * @date 2025/04/19 16:56
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<Area> page(AreaPageParam areaPageParam) {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<Area>().checkSqlInjection();
        if (ObjectUtil.isNotEmpty(areaPageParam.getAreaName())) {
            queryWrapper.lambda().like(Area::getAreaName, areaPageParam.getAreaName());
        }
        if(ObjectUtil.isNotEmpty(areaPageParam.getAreaType())) {
            queryWrapper.lambda().eq(Area::getAreaType, areaPageParam.getAreaType());
        }
        if(ObjectUtil.isNotEmpty(areaPageParam.getIsActive())) {
            queryWrapper.lambda().eq(Area::getIsActive, areaPageParam.getIsActive());
        }
        if(ObjectUtil.isNotEmpty(areaPageParam.getSearchKey())) {
            queryWrapper.lambda().like(Area::getAreaName, areaPageParam.getSearchKey());
        }
        if (ObjectUtil.isAllNotEmpty(areaPageParam.getSortField(), areaPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(areaPageParam.getSortOrder());
            queryWrapper.orderBy(true, areaPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(areaPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(Area::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(AreaAddParam areaAddParam) {
        Area area = BeanUtil.toBean(areaAddParam, Area.class);
        area.setCreatedAt(new Date());
        area.setUpdatedAt(new Date());
        this.save(area);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(AreaEditParam areaEditParam) {
        Area area = this.queryEntity(areaEditParam.getId());
        BeanUtil.copyProperties(areaEditParam, area);
        area.setUpdatedAt(new Date());
        this.updateById(area);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<AreaIdParam> areaIdParamList) {
        this.removeByIds(CollStreamUtil.toList(areaIdParamList, AreaIdParam::getId));
    }

    @Override
    public Area detail(AreaIdParam areaIdParam) {
        return this.queryEntity(areaIdParam.getId());
    }

    @Override
    public Area queryEntity(String id) {
        Area area = this.getById(id);
        if (ObjectUtil.isEmpty(area)) {
            throw new CommonException("Area does not exist, id: {}", id);
        }
        return area;
    }

    @Override
    public List<Area> getActiveAreas() {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Area::getIsActive, true);
        return this.list(queryWrapper);
    }

    @Override
    public List<Area> getAreasByType(String areaType) {
        QueryWrapper<Area> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Area::getAreaType, areaType)
                .eq(Area::getIsActive, true);
        return this.list(queryWrapper);
    }

    @Override
    public Boolean isPointInOperationalArea(Double latitude, Double longitude) {
        List<Area> operationalAreas = getAreasByType(AreaEnum.OPERATIONAL.getValue());

        // If there are no operational areas defined, return false
        if (operationalAreas.isEmpty()) {
            return false;
        }

        // Check if the point is inside any operational area
        for (Area area : operationalAreas) {
            if (isPointInPolygon(latitude, longitude, area.getCoordinates())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Boolean isPointInNoParkingArea(Double latitude, Double longitude) {
        List<Area> noParkingAreas = getAreasByType(AreaEnum.NO_PARKING.getValue());

        // If there are no no-parking areas defined, return false
        if (noParkingAreas.isEmpty()) {
            return false;
        }

        // Check if the point is inside any no-parking area
        for (Area area : noParkingAreas) {
            if (isPointInPolygon(latitude, longitude, area.getCoordinates())) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private boolean isPointInPolygon(Double latitude, Double longitude, String geoJsonCoordinates) {
        try {
            // Parse GeoJSON coordinates
            Map<String, Object> geoJson = objectMapper.readValue(geoJsonCoordinates, Map.class);

            // Check if it's a valid GeoJSON polygon
            if (!"Polygon".equals(geoJson.get("type"))) {
                return false;
            }

            List<List<List<Double>>> coordinates = (List<List<List<Double>>>) geoJson.get("coordinates");
            List<List<Double>> polygon = coordinates.get(0); // Get the outer ring

            return pointInPolygon(latitude, longitude, polygon);

        } catch (JsonProcessingException e) {
            return false;
        }
    }

    private boolean pointInPolygon(Double latitude, Double longitude, List<List<Double>> polygon) {
        boolean inside = false;
        int i, j;
        for (i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            double xi = polygon.get(i).get(0); // longitude
            double yi = polygon.get(i).get(1); // latitude
            double xj = polygon.get(j).get(0); // longitude
            double yj = polygon.get(j).get(1); // latitude

            boolean intersect = ((yi > latitude) != (yj > latitude)) &&
                    (longitude < (xj - xi) * (latitude - yi) / (yj - yi) + xi);

            if (intersect) {
                inside = !inside;
            }
        }

        return inside;
    }
}
