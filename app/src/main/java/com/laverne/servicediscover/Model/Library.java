package com.laverne.servicediscover.Model;

public class Library {
    private String name;
    private String address;
    private String website;
    private String phoneNo;
    private double latitude;
    private double longitude;
    private float homeDistance;
    private float currentDistance;

    public Library(String name, String address, String website, String phoneNo, double latitude, double longitude, float homeDistance, float currentDistance) {
        this.name = name;
        this.address = address;
        this.website = website;
        this.phoneNo = phoneNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.homeDistance = homeDistance;
        this.currentDistance = currentDistance;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getHomeDistance() {
        return homeDistance;
    }

    public void setHomeDistance(float homeDistance) {
        this.homeDistance = homeDistance;
    }

    public float getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(float currentDistance) {
        this.currentDistance = currentDistance;
    }
}
