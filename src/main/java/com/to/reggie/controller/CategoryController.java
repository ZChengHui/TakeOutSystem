package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.to.reggie.common.R;
import com.to.reggie.entity.Category;
import com.to.reggie.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController{

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> addCategory(@RequestBody Category category, HttpSession session) {
        log.info("category:{}",category);
        Long operationId = (Long)session.getAttribute("employeeId");
        iCategoryService.addCategory(category, operationId);
        return R.success("添加分类成功");
    }

    /**
     * 多条件分页查询菜品套餐分类
     * @param cur
     * @param pageSize
     * @param category
     * @return
     */
    //@RequestParam（”“）映射前端json变量 ===> java 变量
    @GetMapping("/page")
    public R<IPage<Category>> getCategoryList(@RequestParam("page") int cur, @RequestParam("pageSize") int pageSize, Category category) {
        IPage<Category> iPage = iCategoryService.getCategoryList(cur, pageSize, category);
        return R.success(iPage);
    }

    /**
     * 删除分类，有外键关联判断
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> removeCategory(Long id) {
        iCategoryService.removeCategory(id);
        return R.success("删除成功");
    }

    /**
     * 修改菜品套餐分类信息
     * @param category
     * @param session
     * @return
     */
    @PutMapping
    public R<String> editCategory(@RequestBody Category category, HttpSession session) {
        log.info(category.toString());
        Long operationId = (Long)session.getAttribute("employeeId");
        iCategoryService.editCategory(category, operationId);
        return R.success("修改成功");
    }


}
