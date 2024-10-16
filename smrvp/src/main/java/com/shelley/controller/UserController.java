package com.shelley.controller;

import com.shelley.entity.User;
import com.shelley.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private IUserService IUserService;

    @PostMapping("register")
    public Map<String, Object> register(@RequestBody User user){
        log.info("用户信息：[{}]",user.toString());
        Map<String,Object> map = new HashMap<>();

        try {
            IUserService.register(user);
            map.put("state",true);
            map.put("msg","注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("msg","注册失败:" + e.getMessage());
        }
        return map;
    }

    @PostMapping("login")
    public Map<String, Object> login(@RequestBody User user){
        Map<String, Object> map = new HashMap<>();
        try {
            User login = IUserService.login(user);
            map.put("state",true);
            map.put("msg","登录成功");
            map.put("user",login.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("msg","登录失败:" + e.getMessage());
        }
        return map;
    }



}
