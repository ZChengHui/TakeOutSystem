package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.to.reggie.common.R;
import com.to.reggie.dto.SetmealDto;
import com.to.reggie.entity.Setmeal;
import com.to.reggie.service.ISetmealService;
import com.to.reggie.service.ex.SetmealCanNotUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController extends BaseController{

    @Autowired
    private ISetmealService iSetmealService;

    /**
     * 修改套餐状态
     * @param state
     * @param ids
     * @return
     */
    @PostMapping("/status/{state}")
    public R<String> updateStatus(@PathVariable int state, @RequestParam List<Long> ids, HttpSession session) {
        Long operationId = (Long) session.getAttribute("employeeId");
        iSetmealService.updateStatus(state, ids, operationId);
        return R.success("修改套餐状态成功");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteBach(@RequestParam List<Long> ids) {
        iSetmealService.deleteBach(ids);
        return R.success("删除成功");
    }

    /**
     * 添加套餐
     * @param setmealDto
     * @param session
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto, HttpSession session) {
        Long operationId = (Long) session.getAttribute("employeeId");
        //log.info(setmealDto.toString());
        iSetmealService.saveWithDish(setmealDto, operationId);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询套餐
     * @param page
     * @param pageSize
     * @param setmeal
     * @return
     */
    @GetMapping("/page")
    public R<IPage<SetmealDto>> getSetmealPage(int page, int pageSize, Setmeal setmeal) {
        //log.info(setmeal.toString());
        IPage<SetmealDto> iPage = iSetmealService.getSetmealPage(page, pageSize, setmeal);
        return R.success(iPage);
    }


}
