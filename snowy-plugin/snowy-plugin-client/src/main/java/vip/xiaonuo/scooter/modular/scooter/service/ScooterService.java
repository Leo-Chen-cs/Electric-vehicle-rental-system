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
package vip.xiaonuo.scooter.modular.scooter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.scooter.modular.scooter.entity.Scooter;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterAddParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterEditParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterIdParam;
import vip.xiaonuo.scooter.modular.scooter.param.ScooterPageParam;

import java.util.List;

/**
 * 滑板管理Service接口
 *
 * @author 张一风
 * @date  2025/02/10 12:59
 **/
public interface ScooterService extends IService<Scooter> {

    /**
     * 获取滑板管理分页
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    Page<Scooter> page(ScooterPageParam scooterPageParam);

    /**
     * 添加滑板管理
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    void add(ScooterAddParam scooterAddParam);

    /**
     * 编辑滑板管理
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    void edit(ScooterEditParam scooterEditParam);

    /**
     * 删除滑板管理
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    void delete(List<ScooterIdParam> scooterIdParamList);

    /**
     * 获取滑板管理详情
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     */
    Scooter detail(ScooterIdParam scooterIdParam);

    /**
     * 获取滑板管理详情
     *
     * @author 张一风
     * @date  2025/02/10 12:59
     **/
    Scooter queryEntity(String id);

    List<Scooter> getAvailableScooter(Integer num);


}
