package com.example.creditmanagement;

public class UserInformation {
    public String name;
    public String address;
    public String email;
    public int credit;


     public UserInformation()
    {

    }

    public UserInformation(String name, String address,String email,int credit) {
        this.name = name;
        this.address = address;
        this.email=email;
        this.credit=credit;

    }


    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getEmail() {
        return email;
    }

    public int getCredit() {
        return credit;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
