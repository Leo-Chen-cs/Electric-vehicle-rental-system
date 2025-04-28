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
package vip.xiaonuo.client.modular.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.auth.core.util.StpClientUtil;
import vip.xiaonuo.client.modular.user.entity.ClientUser;
import vip.xiaonuo.client.modular.user.param.*;
import vip.xiaonuo.client.modular.user.service.ClientUserService;
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.dev.api.DevEmailApi;
import vip.xiaonuo.feedback.modular.feedback.param.FeedbackAddParam;
import vip.xiaonuo.feedback.modular.feedback.service.FeedbackService;
import vip.xiaonuo.rental.modular.rental.entity.Rental;
import vip.xiaonuo.rental.modular.rental.enums.RentalEnum;
import vip.xiaonuo.rental.modular.rental.service.RentalService;
import vip.xiaonuo.scooter.modular.scooter.entity.Scooter;
import vip.xiaonuo.scooter.modular.scooter.enums.ScooterEnum;
import vip.xiaonuo.scooter.modular.scooter.service.ScooterService;
import vip.xiaonuo.client.modular.user.param.ClientUserAccountParam;
import vip.xiaonuo.client.modular.user.param.ClientUserBankCardParam;

import java.util.List;

/**
 * C端用户控制器
 *
 * @author xuyuxiang
 * @date 2022/4/22 9:34
 **/
@Tag(name = "C端用户控制器")
@RestController
@Validated
public class ClientUserController {

    @Resource
    private ClientUserService clientUserService;
    @Resource
    private FeedbackService feedbackService;
    @Resource
    private ScooterService scooterService;
    @Resource
    private RentalService rentalService;

    /**
     * 获取C端用户分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取C端用户分页")
    @GetMapping("/client/user/page")
    public CommonResult<Page<ClientUser>> page(ClientUserPageParam clientUserPageParam) {
        return CommonResult.data(clientUserService.page(clientUserPageParam));
    }

    /**
     * 添加C端用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "添加C端用户")
    @CommonLog("添加C端用户")
    @PostMapping("/client/user/add")
    public CommonResult<String> add(@RequestBody @Valid ClientUserAddParam clientUserAddParam) {
        clientUserService.add(clientUserAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑C端用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @Operation(summary = "编辑C端用户")
    @CommonLog("编辑C端用户")
    @PostMapping("/client/user/edit")
    public CommonResult<String> edit(@RequestBody @Valid ClientUserEditParam clientUserEditParam) {
        clientUserService.edit(clientUserEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除C端用户
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "删除C端用户")
    @CommonLog("删除C端用户")
    @PostMapping("/client/user/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                       List<ClientUserIdParam> clientUserIdParamList) {
        clientUserService.delete(clientUserIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取C端用户详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @Operation(summary = "获取C端用户详情")
    @GetMapping("/client/user/detail")
    public CommonResult<ClientUser> detail(@Valid ClientUserIdParam clientUserIdParam) {
        return CommonResult.data(clientUserService.detail(clientUserIdParam));
    }

    @Operation
    @PostMapping("/client/user/feedback")
    public CommonResult<String> feedback(@RequestBody @Valid FeedbackParam feedbackParam) {
        String userId = StpClientUtil.getLoginIdAsString();
        FeedbackAddParam feedbackAddParam = new FeedbackAddParam();
        feedbackAddParam.setUserId(userId);
        feedbackAddParam.setComment(feedbackParam.getComment());
        feedbackAddParam.setRating(feedbackParam.getRating());
        feedbackAddParam.setRentalId(feedbackParam.getRentalId());
        feedbackService.add(feedbackAddParam);
        return CommonResult.ok();
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/client/user/info")
    public CommonResult<ClientUser> info() {
        String userId = StpClientUtil.getLoginIdAsString();
        return CommonResult.data(clientUserService.getById(userId));
    }


    @Operation(summary = "获取用户详情")
    @GetMapping("/client/user/info2")
    public CommonResult<ClientUser> detail(@Valid ClientUserAccountParam clientUserAccountParam) {
        return CommonResult.data(clientUserService.info(clientUserAccountParam));
    }

    @Operation(summary = "保存银行卡信息")
    @PostMapping("/client/user/bankcard/save")
    public CommonResult<String> saveBankCard(@RequestBody @Valid ClientUserBankCardParam clientUserBankCardParam) {
        clientUserService.saveBankCard(clientUserBankCardParam);
        return CommonResult.ok();
    }

    @Operation(summary = "判断是否可以提供优惠")
    @GetMapping("/client/user/canOfferDiscount")
    public CommonResult<Object> canOfferDiscount() {
        String userId = StpClientUtil.getLoginIdAsString();
        //查看是否满足每周8个小时或者是老人，学生
        if (clientUserService.getById(userId).getAge() == null) {
            return CommonResult.data(false);
        }
        int age = Integer.parseInt(clientUserService.getById(userId).getAge());
        if (age  > 60 || age < 18) {
            return CommonResult.data(0.8);
        }
        //取消的租赁不算
        List<Rental> rentalList = rentalService.list(new LambdaQueryWrapper<Rental>()
                .eq(Rental::getUserId, userId)
                .eq(Rental::getStatus, RentalEnum.COMPLETED.getValue()));
        long totalHours = rentalList.stream().mapToLong(rental -> {
            long diff = rental.getEndTime().getTime() - rental.getStartTime().getTime();
            return diff / (1000 * 60 * 60);
        }).sum();
        if (totalHours < 8) {
            return CommonResult.data(false);
        }
        return CommonResult.data(0.8);
    }

}
