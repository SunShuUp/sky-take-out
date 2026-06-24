package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    @Select("select * from address_book where user_id =#{currentId}")
    List<AddressBook> selectAll(Long currentId);
    @Select("select * from address_book where user_id=#{userId} and is_default=#{enable}")
    AddressBook selectDefault(Long userId, Integer enable);
    @Insert("insert into address_book" +
            "        (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code," +
            "         district_name, detail, label, is_default)" +
            "        values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}," +
            "                #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);
    @Update("update address_book set is_default=1 where id=#{id}")
    void setDefault(AddressBook addressBook);
    @Select("select * from address_book where id=#{id}")
    AddressBook getById(Long id);
    @Delete("delete from address_book where id=#{id}")
    void delete(Long id);

    void update(AddressBook addressBook);
}
