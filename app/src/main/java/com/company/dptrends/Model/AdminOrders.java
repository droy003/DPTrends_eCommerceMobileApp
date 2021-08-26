package com.company.dptrends.Model;

public class AdminOrders {
    private String address, date, deliveryAddress, deliveryPhone, deliveryName,
            discount, status, time, totalAmount, userName, userPhone,shippedBy,
            shippedOn,deliveredBy,deliveredOn;

    public AdminOrders() {
    }

    public AdminOrders(String address, String date,
                       String deliveryAddress, String deliveryPhone,
                       String deliveryName, String discount, String status,
                       String time, String totalAmount, String userName,
                       String userPhone,String shippedBy,String shippedOn,
                       String deliveredBy, String deliveredOn) {
        this.address = address;
        this.date = date;
        this.deliveryAddress = deliveryAddress;
        this.deliveryPhone = deliveryPhone;
        this.deliveryName = deliveryName;
        this.discount = discount;
        this.status = status;
        this.time = time;
        this.totalAmount = totalAmount;
        this.userName = userName;
        this.userPhone = userPhone;
        this.shippedBy = shippedBy;
        this.shippedOn = shippedOn;
        this.deliveredBy = deliveredBy;
        this.deliveredOn = deliveredOn;
    }

    public String getDeliveredBy() {
        return deliveredBy;
    }

    public void setDeliveredBy(String deliveredBy) {
        this.deliveredBy = deliveredBy;
    }

    public String getDeliveredOn() {
        return deliveredOn;
    }

    public void setDeliveredOn(String deliveredOn) {
        this.deliveredOn = deliveredOn;
    }

    public String getShippedBy() {
        return shippedBy;
    }

    public void setShippedBy(String shippedBy) {
        this.shippedBy = shippedBy;
    }

    public String getShippedOn() {
        return shippedOn;
    }

    public void setShippedOn(String shippedOn) {
        this.shippedOn = shippedOn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}