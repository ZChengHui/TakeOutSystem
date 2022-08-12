package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.entity.AddressBook;
import com.to.reggie.mapper.IAddressBookMapper;
import com.to.reggie.service.IAddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<IAddressBookMapper, AddressBook> implements IAddressBookService {
}
