package com.company.dptrends.Model;

public class UserOrders {
    private String address,date,
            deliveryAddress,deliveryName,deliveryPhone,
            discount, status,time,totalAmount,
            userName,userPhone,deliveredOn;

    public UserOrders() {
    }

    public UserOrders(String address, String date,
                      String deliveryAddress, String deliveryName,
                      String deliveryPhone, String discount,
                      String status, String time, String totalAmount,
                      String userName, String userPhone,String deliveredOn) {
        this.address = address;
        this.date = date;
        this.deliveryAddress = deliveryAddress;
        this.deliveryName = deliveryName;
        this.deliveryPhone = deliveryPhone;
        this.discount = discount;
        this.status = status;
        this.time = time;
        this.totalAmount = totalAmount;
        this.userName = userName;
        this.userPhone = userPhone;
        this.deliveredOn = deliveredOn;
    }

    public String getAddress() {
        return address;
    }

    public String getDeliveredOn() {
        return deliveredOn;
    }

    public void setDeliveredOn(String deliveredOn) {
        this.deliveredOn = deliveredOn;
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

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryPhone() {
        return deliveryPhone;
    }

    public void setDeliveryPhone(String deliveryPhone) {
        this.deliveryPhone = deliveryPhone;
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
