package com.audacityit.finder.model;

/**
 * @author Audacity IT Solutions Ltd.
 * @class Category
 * @brief data structure class used by HomeFragment
 */

public class Category {

    private String id;
    private String title;
    private String iconUrl;
    private String imageUrl;

    public Category() {

    }

    /**
     * @brief methods for getting category id
     * @return id in String
     */
    public String getId() {
        return id;
    }

    /**
     * @brief methods for setting category id
     * @param id in String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @brief methods for getting title of category
     * @return title in String
     */
    public String getTitle() {
        return title;
    }

    /**
     * @brief methods for setting category title
     * @return title in String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @brief methods for getting imageUrl of category
     * @return imageUrl in String
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @brief methods for setting category imageUrl
     * @return imageUrl in String
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @brief methods for getting iconUrl of category
     * @return iconUrl in String
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * @brief methods for setting category iconUrl
     * @return iconUrl in String
     */
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
