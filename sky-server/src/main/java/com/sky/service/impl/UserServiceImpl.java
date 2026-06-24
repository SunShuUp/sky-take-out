package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    WeChatProperties weChatProperties;

    @Autowired
    UserMapper userMapper;
    @Autowired
    JwtProperties jwtProperties;

    @Override
    public UserLoginVO wxLogin(UserLoginDTO userLoginDTO) {
       Map<String,String> map = new HashMap<>();
       map.put("appid",weChatProperties.getAppid());
       map.put("secret",weChatProperties.getSecret());
       map.put("js_code",userLoginDTO.getCode());
       map.put("grant_type","authorization_code");

       String json= HttpClientUtil.doGet(WX_LOGIN,map);
       JSONObject jsonObject = JSONObject.parseObject(json);
       String open_id=jsonObject.getString("openid");
       if(open_id==null){
           throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
       }
      User user=userMapper.getByopenid(open_id);
       if(user==null){
          user= User.builder().openid(open_id).createTime(LocalDateTime.now()).build();
          userMapper.insert(user);
       }
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", user.getId());   // 关键：存入 userId

        String token=JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(), claims);
        return UserLoginVO.builder().id(user.getId()).openid(user.getOpenid()).token(token).build();
    }
}
