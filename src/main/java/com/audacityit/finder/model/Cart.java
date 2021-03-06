package com.audacityit.finder.model;


public class Cart {

    public Long id;
    public Long order_id = -1L;
    public Long product_id;
    public String product_name;
    public String image;
    public Integer amount = 0;
    public Long stock = 0L;
    public float price_item;
    public Long created_at = 0L;
    public int totalQuantity;
    public int leavesQuantity;
    public String sellerName;
    public String sellerUsername;
    public Cart() {
    }

    public Cart(Long product_id,  String sellerUsername,String sellerName,String product_name, String image, Integer amount, Long stock, float price_item, Long created_at,int totalQuantity,int leavesQuantity) {
        this.product_id = product_id;
        this.sellerUsername=sellerUsername;
        this.sellerName=sellerName;
        this.product_name = product_name;
        this.image = image;
        this.amount = amount;
        this.stock = stock;
        this.price_item = price_item;
        this.created_at = created_at;
        this.totalQuantity=totalQuantity;
        this.leavesQuantity=leavesQuantity;
    }



}
