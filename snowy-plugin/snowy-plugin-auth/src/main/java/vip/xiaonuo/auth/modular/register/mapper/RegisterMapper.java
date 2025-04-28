package vip.xiaonuo.auth.modular.register.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import vip.xiaonuo.client.modular.user.entity.ClientUser;

@Mapper
public interface RegisterMapper extends BaseMapper<ClientUser> {
}
