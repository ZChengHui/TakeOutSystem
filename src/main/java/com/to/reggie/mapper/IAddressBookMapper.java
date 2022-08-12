package com.to.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.to.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IAddressBookMapper extends BaseMapper<AddressBook> {

}
