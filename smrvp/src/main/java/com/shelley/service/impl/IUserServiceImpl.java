package com.shelley.service.impl;

import com.shelley.dao.IUserDao;
import com.shelley.entity.User;
import com.shelley.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private IUserDao IUserDao;

    @Override
    public void register(User user) {
        //0、根据用户输入的用户名判断用户名是否存在
        User byUserName = IUserDao.findByUserName(user.getUsername());
        if (byUserName == null){
            //1、生成用户状态
            user.setStatus("已激活");
            //2、设置用户的注册时间
            user.setRegisterTime(new Date());
            //3、调用dao
            IUserDao.save(user);
        }else {
            throw new RuntimeException("用户名已存在");
        }

    }

    @Override
    public User login(User user) {

        //1、根据用户输入的用户名进行查询
        User byUserName = IUserDao.findByUserName(user.getUsername());
        if (!Objects.isNull(byUserName)){
            //2、比较密码
            if (byUserName.getPassword().equals(user.getPassword())){
                return byUserName;
            }else {
                throw new RuntimeException("密码输入不正确");
            }
        }else {
            throw new RuntimeException("用户名输入不正确");
        }
    }

}
