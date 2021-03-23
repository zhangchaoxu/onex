package com.nb6868.onexboot.api.modules.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.dto.LoginTypeConfig;
import com.nb6868.onexboot.api.modules.uc.entity.MenuEntity;
import com.nb6868.onexboot.api.modules.uc.entity.TokenEntity;
import com.nb6868.onexboot.api.modules.uc.entity.UserEntity;
import com.nb6868.onexboot.api.modules.uc.service.*;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import com.nb6868.onexboot.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * shiro
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    MenuService menuService;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    TokenService tokenService;
    @Autowired
    ParamService paramService;
    @Autowired
    MenuScopeService menuScopeService;

    @Value("${redis.open: false}")
    private boolean open;

    @Override
    public Set<String> getUserPermissions(UserDetail user) {
        // 系统管理员，拥有最高权限
        List<String> permissionsList;
        if (user.getType() == UcConst.UserTypeEnum.ADMIN.value()) {
            permissionsList = menuService.listObjs(new QueryWrapper<MenuEntity>().select("permissions").ne("permissions", ""), Object::toString);
        } else {
            permissionsList = menuScopeService.getPermissionsListByUserId(user.getId());
        }

        // 用户权限列表
        Set<String> set = new HashSet<>();
        for (String permissions : permissionsList) {
            if (StringUtils.isBlank(permissions)) {
                continue;
            }
            set.addAll(StringUtils.splitToList(permissions));
        }

        return set;
    }

    @Override
    public Set<Long> getUserRoles(UserDetail user) {
        List<Long> roleList = user.getType() == UcConst.UserTypeEnum.ADMIN.value() ? roleService.getRoleIdList() : roleService.getRoleIdListByUserId(user.getId());
        // 用户角色列表
        return new HashSet<>(roleList);
    }

    @Override
    public TokenEntity getUserIdAndTypeByToken(String token) {
        return tokenService.getUserIdAndTypeByToken(token);
    }

    @Override
    public UserEntity getUser(Long userId) {
        return userService.getById(userId);
    }

    @Override
    public LoginTypeConfig getLoginCfg(Integer type) {
        return paramService.getContentObject(UcConst.LOGIN_TYPE_PREFIX + type, LoginTypeConfig.class);
    }

    @Override
    public boolean renewalToken(String token, Long expire) {
        return tokenService.renewalToken(token, expire);
    }

}
