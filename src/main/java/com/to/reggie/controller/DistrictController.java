package com.to.reggie.controller;

import com.to.reggie.common.R;
import com.to.reggie.entity.District;
import com.to.reggie.service.IDistrictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/district")
public class DistrictController extends BaseController{

    @Autowired
    private IDistrictService iDistrictService;

    /**
     * 查询行政区列表
     * @param parent
     * @return
     */
    @GetMapping("/{parent}")
    public R<List<District>> getList(@PathVariable String parent) {
        List<District> list = iDistrictService.getByParent(parent);
        return R.success(list);
    }

}
