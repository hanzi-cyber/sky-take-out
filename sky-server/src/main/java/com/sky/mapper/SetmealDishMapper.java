package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    List<Long> getSetmealIdsByDishId(List<Long> ids);

    void insert(List<SetmealDish> setmealDishes);

    @Select("select * from setmeal_dish where dish_id=#{dishId}")
    List<SetmealDish> getByDishId(Long dishId);

    /**
     * 根据套餐id查询套餐关联的菜品
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 根据套餐id删除套餐菜品关联数据
     * @param setmealId
     */
    void deleteBySetmealId(List<Long> setmealId);
}
