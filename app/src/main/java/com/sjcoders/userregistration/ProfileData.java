package com.sjcoders.userregistration;

public class ProfileData {

    String userName;
    String userEmail;
    String userPhoneNo;
    String userAddress;
    String userGender;
    String userAge;

    public ProfileData(String userName, String userEmail, String userPhoneNo, String userAddress, String userGender, String userAge) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoneNo = userPhoneNo;
        this.userAddress = userAddress;
        this.userGender = userGender;
        this.userAge = userAge;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserAge() {
        return userAge;
    }
}
