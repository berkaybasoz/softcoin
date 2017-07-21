package com.audacityit.finder.model;

/**
 * @author Audacity IT Solutions Ltd.
 * @class User
 * @brief data structure class for storing user information
 */

public class User {

    private String id;
    private String phoneNumber;
    private String name;
    private String email;
    private String genderId;

    /**
     * @brief default constructor
     */
    public User() {

    }

    /**
     * @brief get id of user
     * @return id in String
     */
    public String getId() {
        return id;
    }

    /**
     * @brief set id of user
     * @param id in String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @brief get phone number of user
     * @return phoneNumber in String
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @brief set phoneNumber of user
     * @param phoneNumber in String
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @brief get name of user
     * @return name in String
     */
    public String getName() {
        return name;
    }

    /**
     * @brief set name of user
     * @param name in String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief get email of user
     * @return email in String
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief set email of user
     * @param email in String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief get gender of user
     * @return gender in String
     */
    public String getGenderId() {
        return genderId;
    }

    /**
     * @brief set gender of user
     * @param genderId in String
     */
    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }
}
