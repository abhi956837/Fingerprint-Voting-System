package com.app.fingerprintvoting;

public class Voter {
    private String fullName;
    private String aadharNumber;
    private String dateOfBirth;
    private int age;
    private String city;

    public Voter(String fullName, String aadharNumber, String dateOfBirth, int age, String city) {
        this.fullName = fullName;
        this.aadharNumber = aadharNumber;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.city = city;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public String getCity() {
        return city;
    }
}
