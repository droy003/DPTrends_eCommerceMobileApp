package com.company.dptrends.Model;

public class Users {

    private String Name;
    private String Password;
    private String Phone;
    private String Image;
    private String Address;
    private String status;
    public Users() {
    }

    public Users(String name, String password, String phone, String image, String address,String status) {
        this.Name = name;
        this.Password = password;
        this.Phone = phone;
        this.Image = image;
        this.Address = address;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
