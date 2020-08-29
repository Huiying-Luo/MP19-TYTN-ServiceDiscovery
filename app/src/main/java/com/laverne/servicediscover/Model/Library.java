package com.laverne.servicediscover.Model;

public class Library {
    private String name;
    private String address;
    private String website;
    private String phoneNo;
    private String distance;


    public Library(String name, String address, String website, String phoneNo, String distance) {
        this.name = name;
        this.address = address;
        this.website = website;
        this.phoneNo = phoneNo;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
