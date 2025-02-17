package com.laverne.servicediscover.Model;

public class Service {
    private String name;
    private String address;
    private String website;
    private String phoneNo;
    private double latitude;
    private double longitude;
    private float currentDistance;
    private int schoolType;
    private int category;
    private String museumType;
    private String museumDescription;

    public Service(String name, String address, double latitude, double longitude, float currentDistance,  int category) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentDistance = currentDistance;
        this.category = category;
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

    public int getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(int schoolType) {
        this.schoolType = schoolType;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getMuseumType() {
        return museumType;
    }

    public void setMuseumType(String museumType) {
        this.museumType = museumType;
    }

    public String getMuseumDescription() {
        return museumDescription;
    }

    public void setMuseumDescription(String museumDescription) {
        this.museumDescription = museumDescription;
    }
}
