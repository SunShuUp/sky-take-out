package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart/")
@Slf4j
@Api(tags = "用户购物车接口")
public class ShoppingCarController {
    @Autowired
    ShoppingCarService shoppingCarService;

    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> shoppingCartList=shoppingCarService.list();
        return Result.success(shoppingCartList);
    }
    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public Result<String> add(@RequestBody  ShoppingCartDTO shoppingCartDTO) {
        shoppingCarService.add(shoppingCartDTO);
        return Result.success();
    }
    @PostMapping("/sub")
    @ApiOperation("删除购物车中的一个商品")
    public Result<String> sub( @RequestBody  ShoppingCartDTO shoppingCartDTO) {
        shoppingCarService.sub(shoppingCartDTO);
        return Result.success();
    }
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result<String> clean() {
        shoppingCarService.clean();
        return Result.success();
    }

}
