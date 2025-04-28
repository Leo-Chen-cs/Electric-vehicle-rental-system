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
package vip.xiaonuo.area.modular.area.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.area.modular.area.entity.Area;
import vip.xiaonuo.area.modular.area.param.AreaAddParam;
import vip.xiaonuo.area.modular.area.param.AreaEditParam;
import vip.xiaonuo.area.modular.area.param.AreaIdParam;
import vip.xiaonuo.area.modular.area.param.AreaPageParam;

import java.util.List;

/**
 * 区域管理Service接口
 *
 * @author 胡勋
 * @date 2025/04/19 16:38
 */
public interface AreaService extends IService<Area> {

    /**
     * 获取区域管理分页
     *
     * @author 胡勋
     * @date 2025/04/19 16:38
     */
    Page<Area> page(AreaPageParam areaPageParam);

    /**
     * 新增区域管理
     *
     * @author 胡勋
     * @date 2025/04/19 16:38
     */
    void add(AreaAddParam areaAddParam);

    /**
     * 编辑区域管理
     *
     * @author 胡勋
     * @date 2025/04/19 16:38
     */
    void edit(AreaEditParam areaEditParam);

    /**
     * 删除区域管理
     *
     * @author 胡勋
     * @date 2025/04/19 16:38
     */
    void delete(List<AreaIdParam> areaIdParam);

    /**
     * 获取区域管理详情
     *
     * @author 胡勋
     * @date 2025/04/19 16:38
     */
    Area detail(AreaIdParam areaIdParam);

    /**
     * 通过ID获取区域管理详情
     *
     * @author 胡勋
     * @date 2025/04/19 16:39
     */
    Area queryEntity(String id);

    /**
     * 获取所有区域列表
     *
     * @author 胡勋
     * @date 2025/04/19 16:41
     */
    List<Area> getActiveAreas();

    /**
     * 获取指定类型区域列表
     *
     * @author 胡勋
     * @date 2025/04/19 16:42
     */
    List<Area> getAreasByType(String areaType);

    /**
     * 检查点是否在可运行区域内
     *
     * @author 胡勋
     * @date 2025/04/19 16:43
     */
    Boolean isPointInOperationalArea(Double longitude, Double latitude);

    /**
     * 检查点是否在禁停区域内
     *
     * @author 胡勋
     * @date 2025/04/19 16:45
     */
    Boolean isPointInNoParkingArea(Double longitude, Double latitude);
}
