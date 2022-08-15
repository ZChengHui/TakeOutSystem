package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.entity.District;
import com.to.reggie.mapper.IDistrictMapper;
import com.to.reggie.service.IDistrictService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictServiceImpl extends ServiceImpl<IDistrictMapper, District> implements IDistrictService {

    //通过父节点代码查询子行政区信息
    @Override
    public List<District> getByParent(String parent) {
        LambdaQueryWrapper<District> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(District::getParent, parent);

        List<District> list = this.list(queryWrapper);

        //清空无关信息
        for (District d : list) {
            d.setId(null);
            d.setParent(null);
        }
        return list;
    }

    //获取行政区名称
    @Override
    public String getNameByCode(String code) {
        LambdaQueryWrapper<District> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(District::getCode, code);

        District district = this.getOne(queryWrapper);

        String name = district.getName();
        return name;
    }
}
