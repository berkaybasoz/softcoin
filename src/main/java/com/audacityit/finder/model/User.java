package com.audacityit.finder.model;

import java.util.ArrayList;

/**
 * @author Audacity IT Solutions Ltd.
 * @class User
 * @brief data structure class for storing user information
 */

public class User {

    private static User currentUser;

    private String id;
    private String phoneNumber;
    private String name;
    private String email;
    private String genderId;
    private String userName;
    private float coin;

    /**
     * @brief default constructor
     */
    public User() {

    }

    public static User getCurrentUser() {

        if (currentUser == null) {
            currentUser = new User();
            currentUser.id = "1215616";
            currentUser.setUserName("Aytekin");
            currentUser.setName("Aytekin Ulaş");
        }
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        User.currentUser = currentUser;
    }

    /**
     * @return id in String
     * @brief get id of user
     */
    public String getId() {
        return id;
    }

    /**
     * @param id in String
     * @brief set id of user
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return phoneNumber in String
     * @brief get phone number of user
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber in String
     * @brief set phoneNumber of user
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return name in String
     * @brief get name of user
     */
    public String getName() {
        return name;
    }

    /**
     * @param name in String
     * @brief set name of user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return email in String
     * @brief get email of user
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email in String
     * @brief set email of user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return gender in String
     * @brief get gender of user
     */
    public String getGenderId() {
        return genderId;
    }

    /**
     * @param genderId in String
     * @brief set gender of user
     */
    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getCoin() {
        return coin;
    }

    public void setCoin(float coin) {
        float oldCoin = coin;
        this.coin = coin;
        notifyCoinChanged(oldCoin, coin);

    }

    private ArrayList<IUserCoinChangedListener> userCoinChangedListeners;

    public void subscribeUserCoinChangedListeners(IUserCoinChangedListener listener) {
        if (userCoinChangedListeners == null)
            userCoinChangedListeners = new ArrayList<>();

        userCoinChangedListeners.add(listener);
    }

    public void notifyCoinChanged(float oldCoin, float newCoin) {
        if (userCoinChangedListeners != null) {
            for (IUserCoinChangedListener listener:userCoinChangedListeners) {
                listener.onCoinChanged(oldCoin, coin);
            }
        }
    }

    public interface IUserCoinChangedListener {
        void onCoinChanged(float oldCoin, float newCoin);
    }
}
