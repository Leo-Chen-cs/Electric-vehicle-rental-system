package vip.xiaonuo.auth.modular.register.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.auth.modular.register.param.RegisterParam;
import vip.xiaonuo.client.modular.user.entity.ClientUser;

public interface RegisterService extends IService<ClientUser> {
    void doRegister(RegisterParam registerParam);
}
