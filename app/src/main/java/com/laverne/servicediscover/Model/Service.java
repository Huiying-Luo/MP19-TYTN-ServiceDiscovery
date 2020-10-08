package com.laverne.servicediscover.Model;

public class Service {
    private String name;
    private String address;
    private String website;
    private String phoneNo;
    private double latitude;
    private double longitude;
    private float currentDistance;
    private String schoolType;

    public Service(String name, String address, String website, String phoneNo, double latitude, double longitude, float currentDistance) {
        this.name = name;
        this.address = address;
        this.website = website;
        this.phoneNo = phoneNo;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public float getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(float currentDistance) {
        this.currentDistance = currentDistance;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }
}
