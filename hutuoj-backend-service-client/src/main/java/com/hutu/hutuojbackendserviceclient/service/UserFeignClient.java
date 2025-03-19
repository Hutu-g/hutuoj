package com.hutu.hutuojbackendserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hutu.hutuojcommon.common.BaseResponse;
import com.hutu.hutuojcommon.common.ErrorCode;
import com.hutu.hutuojcommon.exception.BusinessException;
import com.hutu.hutuojmodel.model.dto.user.UserQueryRequest;
import com.hutu.hutuojmodel.model.dto.user.UserUpdateMyRequest;
import com.hutu.hutuojmodel.model.entity.User;
import com.hutu.hutuojmodel.model.enums.UserRoleEnum;
import com.hutu.hutuojmodel.model.vo.LoginUserVO;
import com.hutu.hutuojmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.hutu.hutuojcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 *
 */
@FeignClient(name = "hutuoj-backend-user-service",path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 根据id获取用户信息
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") long userId);
    /**
     * 根据id列表获取用户列表
     * @param userIds
     * @return
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("userIds")Collection<Long> userIds);
    /**
     * 获取当前登录用户
     * 不选择open feign 因为HttpServletRequest 设计序列化之类的很复杂
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request){
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    };
    /**
     * 是否为管理员
     * 不选择open feign 因为逻辑简单不需要数据库操作
     * @param user
     * @return
     */
    default boolean isAdmin(User user){
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    };
    /**
     * 获取脱敏的用户信息
     * @param user
     * @return
     */
    default UserVO getUserVO(User user){
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    };

}
