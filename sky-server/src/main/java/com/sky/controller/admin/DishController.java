package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
public class DishController {
    /**
     * 新增菜品
     */
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){

        dishService.save(dishDTO);
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);

        return Result.success();
    }
    /**+
     * 分页查询菜品
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        dishService.delete(ids);
        cleanCache("dish_*");
        return Result.success();

    }

    @GetMapping("/{id}")
    public Result<DishVO> get(@PathVariable Long id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.update(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status, id);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品列表（新增套餐时选择菜品用）
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}




























