package com.example.prekshasingla.cashlessbazar;

public class Product {

    int id;
    String name;
    String desc;
    Double mrp=0d;
    Double cbtp=0d;
    int productType;
    String ProductTypeName;
    String img;
    int categoryId;
    String categoryName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getMrp() {
        return mrp;
    }

    public void setMrp(Double mrp) {
        this.mrp = mrp;
    }

    public Double getCbtp() {
        return cbtp;
    }

    public void setCbtp(Double cbtp) {
        this.cbtp = cbtp;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public String getProductTypeName() {
        return ProductTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        ProductTypeName = productTypeName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getProductMode() {
        return productMode;
    }

    public void setProductMode(int productMode) {
        this.productMode = productMode;
    }

    public String getProductModeName() {
        return productModeName;
    }

    public void setProductModeName(String productModeName) {
        this.productModeName = productModeName;
    }

    int productMode;
    String productModeName;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
