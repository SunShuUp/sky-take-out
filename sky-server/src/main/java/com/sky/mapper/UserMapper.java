package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid=#{openid}")
    User getByopenid(String openid);
    @Insert("insert into user(openid,name,phone,sex,id_number,avatar,create_time)" +
            "values" +
            "(#{openid},#{name},#{phone},#{sex},#{idNumber},#{avatar},#{createTime})")
    void insert(User user);
    @Select("select * from user where id=#{userId}")
    User getByUserId(Long userId);

    /**
     * 动态统计用户数量
     * @param map
     * @return
     */
    Integer countByMap( @Param("map") Map<String, Object> map);
}
