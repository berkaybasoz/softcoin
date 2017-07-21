package com.audacityit.finder.model;

import java.util.List;


public class OrderList {

    private static List<Order> orderList;

    public static List<Order> getOrderList() {
        return orderList;
    }

    public static void setOrderList(List<Order> orders) {
        orderList = orders;
    }
}
