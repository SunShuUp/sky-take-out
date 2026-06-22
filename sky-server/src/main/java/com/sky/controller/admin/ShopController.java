package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {
    public  static final String key="SHOP_STATUS";
   @Autowired
   RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer>  getShopStatus() {
        Integer status=(Integer) redisTemplate.opsForValue().get(key);
        return Result.success(status);
    }
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result<String>  setShopStatus(@PathVariable Integer  status) {
        redisTemplate.opsForValue().set(key,status);
        return Result.success();
    }
}
