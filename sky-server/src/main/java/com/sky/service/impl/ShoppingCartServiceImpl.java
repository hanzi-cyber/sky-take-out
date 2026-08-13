package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list=shoppingCartMapper.get(shoppingCart);

        if(list!=null&&list.size()>0){
            ShoppingCart existCart=list.get(0);
            existCart.setNumber(existCart.getNumber()+1);
            shoppingCartMapper.update(existCart);
        }else {
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();
            if (dishId != null) {
                Dish dish=dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                SetmealVO setmeal=setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.cleanByUserId(userId);
    }

    @Override
    public void delete(ShoppingCartDTO shoppingCartDTO) {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setUserId(userId);
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.get(shoppingCart);
        if(shoppingCarts != null && shoppingCarts.size() > 0){
            ShoppingCart cart = shoppingCarts.get(0);
            Integer number = cart.getNumber();
            if (number > 1) {
                cart.setNumber(number - 1);
                shoppingCartMapper.update(cart);
            } else {
                shoppingCartMapper.delete(cart);
            }
        }
    }
}

























