package com.sanli.mallsystem.dao;

import com.sanli.mallsystem.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int countByUsername(String username); // 是否存在Username，存在则返回存在个数（但要求唯一）

    int countByEmail(String email); // 是否存在Email，存在则返回存在个数（但要求唯一）

    User selectByUsername(String username);

    User selectByUsernameToNotPassword(String username);

}