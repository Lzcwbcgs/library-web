package com.xiazihan.webback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiazihan.webback.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}