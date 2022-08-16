package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.to.reggie.common.BaseContext;
import com.to.reggie.common.R;
import com.to.reggie.entity.AddressBook;
import com.to.reggie.service.IAddressBookService;
import com.to.reggie.service.IDistrictService;
import com.to.reggie.service.ex.AddressBookNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController extends BaseController{

    @Autowired
    private IAddressBookService iAddressBookService;

    @Autowired
    private IDistrictService iDistrictService;

    //修改地址
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook, HttpSession session) {
        Long operationId = (Long) session.getAttribute("userId");
        BaseContext.setCurrentId(operationId);
        addressBook.setUserId(BaseContext.getCurrentId());

        //省市区信息
        addressBook.setProvinceName(iDistrictService.getNameByCode(addressBook.getProvinceCode()));
        addressBook.setCityName(iDistrictService.getNameByCode(addressBook.getCityCode()));
        addressBook.setDistrictName(iDistrictService.getNameByCode(addressBook.getDistrictCode()));

        iAddressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    //删除地址
    @DeleteMapping
    public R<String> delete(Long ids) {
        iAddressBookService.removeById(ids);
        int count = iAddressBookService.count();
        //只剩一个就设为默认
        if (count == 1) {
            LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
            AddressBook one = iAddressBookService.getOne(queryWrapper);
            one.setIsDefault(1);
            iAddressBookService.updateById(one);
        }
        return R.success("删除成功");
    }


    /**
     * 新增
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook, HttpSession session) {
        Long operationId = (Long) session.getAttribute("userId");
        BaseContext.setCurrentId(operationId);
        addressBook.setUserId(BaseContext.getCurrentId());

        int count = iAddressBookService.count();
        if (count == 0) {
            addressBook.setIsDefault(1);
        }

        //省市区信息
        addressBook.setProvinceName(iDistrictService.getNameByCode(addressBook.getProvinceCode()));
        addressBook.setCityName(iDistrictService.getNameByCode(addressBook.getCityCode()));
        addressBook.setDistrictName(iDistrictService.getNameByCode(addressBook.getDistrictCode()));

        iAddressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook, HttpSession session) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        Long operationId = (Long) session.getAttribute("userId");
        BaseContext.setCurrentId(operationId);
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        iAddressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        iAddressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = iAddressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            throw new AddressBookNotFoundException("地址不存在");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault(HttpSession session) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        Long operationId = (Long) session.getAttribute("userId");
        BaseContext.setCurrentId(operationId);
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = iAddressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            throw new AddressBookNotFoundException("地址不存在");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook, HttpSession session) {
        Long operationId = (Long) session.getAttribute("userId");
        BaseContext.setCurrentId(operationId);
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(iAddressBookService.list(queryWrapper));
    }
}
