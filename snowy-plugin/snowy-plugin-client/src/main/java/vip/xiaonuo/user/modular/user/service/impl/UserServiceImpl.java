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
package vip.xiaonuo.user.modular.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.common.enums.CommonSortOrderEnum;
import vip.xiaonuo.common.exception.CommonException;
import vip.xiaonuo.common.page.CommonPageRequest;
import vip.xiaonuo.user.modular.user.entity.ClientUser;
import vip.xiaonuo.user.modular.user.mapper.UserMapper;
import vip.xiaonuo.user.modular.user.param.*;
import vip.xiaonuo.user.modular.user.service.ClientUserService;

import java.util.List;

/**
 * 用户管理Service接口实现类
 *
 * @author 张一风
 * @date  2025/02/09 23:14
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, ClientUser> implements ClientUserService {

    @Override
    public Page<ClientUser> page(ClientUserPageParam clientUserPageParam) {
        QueryWrapper<ClientUser> queryWrapper = new QueryWrapper<ClientUser>().checkSqlInjection();
        if(ObjectUtil.isNotEmpty(clientUserPageParam.getAccount())) {
            queryWrapper.lambda().like(ClientUser::getAccount, clientUserPageParam.getAccount());
        }
        if(ObjectUtil.isNotEmpty(clientUserPageParam.getName())) {
            queryWrapper.lambda().like(ClientUser::getName, clientUserPageParam.getName());
        }
        if(ObjectUtil.isNotEmpty(clientUserPageParam.getGender())) {
            queryWrapper.lambda().eq(ClientUser::getGender, clientUserPageParam.getGender());
        }
        if(ObjectUtil.isAllNotEmpty(clientUserPageParam.getSortField(), clientUserPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(clientUserPageParam.getSortOrder());
            queryWrapper.orderBy(true, clientUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(clientUserPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ClientUser::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ClientUserAddParam clientUserAddParam) {
        ClientUser clientUser = BeanUtil.toBean(clientUserAddParam, ClientUser.class);
        this.save(clientUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ClientUserEditParam clientUserEditParam) {
        ClientUser clientUser = this.queryEntity(clientUserEditParam.getId());
        BeanUtil.copyProperties(clientUserEditParam, clientUser);
        this.updateById(clientUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ClientUserIdParam> clientUserIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(clientUserIdParamList, ClientUserIdParam::getId));
    }

    @Override
    public ClientUser detail(ClientUserIdParam clientUserIdParam) {
        return this.queryEntity(clientUserIdParam.getId());
    }

    @Override
    public ClientUser queryEntity(String id) {
        ClientUser clientUser = this.getById(id);
        if(ObjectUtil.isEmpty(clientUser)) {
            throw new CommonException("用户管理不存在，id值为：{}", id);
        }
        return clientUser;
    }

    @Override
    public ClientUser info(ClientUserAccountParam clientUserAccountParam) {
        return this.getOne(new QueryWrapper<ClientUser>().lambda().eq(ClientUser::getAccount, clientUserAccountParam.getAccount()));
    }

    @Override
    public void saveBankCard(ClientUserBankCardParam clientUserBankCardParam) {
        ClientUser clientUser = this.getOne(new QueryWrapper<ClientUser>().lambda().eq(ClientUser::getAccount, clientUserBankCardParam.getAccount()));
        BeanUtil.copyProperties(clientUserBankCardParam, clientUser);
        this.updateById(clientUser);
    }
}
