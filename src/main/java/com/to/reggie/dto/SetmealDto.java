package com.to.reggie.dto;


import com.to.reggie.entity.Setmeal;
import com.to.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

/**
 * setmeal和setmeal_dish组合类
 */
@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
