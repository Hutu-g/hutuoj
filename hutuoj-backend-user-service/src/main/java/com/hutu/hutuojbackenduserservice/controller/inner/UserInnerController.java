package com.hutu.hutuojbackenduserservice.controller.inner;

import com.hutu.hutuojbackendserviceclient.service.UserFeignClient;
import com.hutu.hutuojbackenduserservice.service.UserService;
import com.hutu.hutuojmodel.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 用户接口（内部）
 * @author hutu-g
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;
    /**
     * 根据id获取用户信息
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    @Override
    public User getById(@RequestParam("userId") long userId){
        return userService.getById(userId);
    };
    /**
     * 根据id列表获取用户列表
     * @param userIds
     * @return
     */
    @GetMapping("/get/ids")
    @Override
    public List<User> listByIds(@RequestParam("userIds")Collection<Long> userIds){
        return userService.listByIds(userIds);
    };
}
