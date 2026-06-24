package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Slf4j
@Api(tags = "地址簿接口")
public class AddressBookController {
    @Autowired
    AddressBookService addressBookService;

    @PostMapping
    @ApiOperation("添加地址")
    public Result<String>  insert(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookService.insert(addressBook);
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的地址")
    public Result<List<AddressBook>> selectAll(){
        List<AddressBook> addressBookList=addressBookService.selectAll();
        return  Result.success(addressBookList);
    }
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> selectDefault(){
        AddressBook addressBook= addressBookService.selectDefault();
        return  Result.success(addressBook);
    }
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result<String> update(@RequestBody AddressBook addressBook){
        addressBookService.update(addressBook);
        return Result.success();
    }
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除地址")
    public Result<String> delete(@PathVariable Long id){
        addressBookService.delete(id);
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> selectById(@PathVariable Long id){
        AddressBook addressBook=addressBookService.getById(id);
        return  Result.success(addressBook);
    }
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result<String>   setDefault(@RequestBody AddressBook addressBook){
        addressBookService.setDefault(addressBook);
        return Result.success();
    }
}
