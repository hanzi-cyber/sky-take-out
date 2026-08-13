package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void save(DishDTO dishDTO);


    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO getById(Long id);

    void update(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品列表（用于新增套餐时选择菜品）
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);

    List<DishVO> listWithFlavor(Dish dish);

    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

}
