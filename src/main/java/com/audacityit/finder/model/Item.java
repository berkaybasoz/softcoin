package com.audacityit.finder.model;

import java.util.Arrays;

/**
 * @author Audacity IT Solutions Ltd.
 * @class Item
 * @brief data structure class for storing business information
 */

public class Item {

    private int type;
    private String id;
    private String title;
    private String address;
    private String districtId;
    private String contactPhoneNumber;
    private String telephoneNumber;
    private String mobileNumber;
    private String emailAddress;
    private String webUrl;
    private String facebookUrl;
    private String twitterUrl;
    private String googlePlusUrl;
    private String linkedInUrl;
    private double latitude;
    private double longitude;
    private String hotLine;
    private String tagLine;
    private float rating;
    private int ratingCount;
    private String description;
    private boolean isVerified;
    private String districtTitle;
    private String categoryId;
    private String tag;
    private String[] imageThumbUrls;
    private String[] imageLargeUrls;
    private String titleImg;
    private String adUrl;
    private String price;
    private String sellerName;
    private String totalQuantity;
    private String leavesQuantity;
    private String sellerUserName;
    /**
     * @brief get title image of item
     * @return titleImg in String
     */
    public String getTitleImg() {
        return titleImg;
    }

    /**
     * @brief set title image of item
     * @param titleImg in String
     */
    public void setTitleImg(String titleImg) {
        this.titleImg = titleImg;
    }

    /**
     * @brief get Ad url of item
     * @return adUrl in String
     */
    public String getAdUrl() {
        return adUrl;
    }

    /**
     * @brief set ad url image of item
     * @param adUrl in String
     */
    public void setAdUrl(String adUrl) {
        this.adUrl = adUrl;
    }

    /**
     * @brief get type of item
     * @return type in int
     */
    public int getType() {
        return type;
    }

    /**
     * @brief set type of item
     * @param type in int
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @brief get id of item
     * @return id in String
     */
    public String getId() {
        return id;
    }

    /**
     * @brief set id of item
     * @param id in String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @brief get title of item
     * @return title in String
     */
    public String getTitle() {
        return title;
    }

    /**
     * @brief set title of item
     * @param title in String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @brief get address of item
     * @return address in String
     */
    public String getAddress() {
        return address;
    }

    /**
     * @brief set address of item
     * @param address in String
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @brief get district id of item
     * @return districtId in String
     */
    public String getDistrictId() {
        return districtId;
    }

    /**
     * @brief set district id of item
     * @param districtId in String
     */
    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    /**
     * @brief get phone number of item
     * @return contactPhoneNumber in String
     */
    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    /**
     * @brief set phone number of item
     * @param  contactPhoneNumber in String
     */
    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    /**
     * @brief get Telephone number of item
     * @return telephoneNumber in String
     */
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    /**
     * @brief set Telephone number of item
     * @param telephoneNumber in String
     */
    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    /**
     * @brief get mobile number of item
     * @return mobileNumber in String
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @brief set mobile number of item
     * @param mobileNumber in String
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * @brief get email of item
     * @return emailAddress in String
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @brief set email of item
     * @param emailAddress in String
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @brief get website of item
     * @return webUrl in String
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * @brief set website of item
     * @param webUrl in String
     */
    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    /**
     * @brief get facebook url of item
     * @return facebookUrl in String
     */
    public String getFacebookUrl() {
        return facebookUrl;
    }

    /**
     * @brief set facebook url of item
     * @param facebookUrl in String
     */
    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    /**
     * @brief get twitter url of item
     * @return twitter in String
     */
    public String getTwitterUrl() {
        return twitterUrl;
    }

    /**
     * @brief set twitter url of item
     * @param twitterUrl in String
     */
    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    /**
     * @brief get google plus url of item
     * @return googlePlusUrl in String
     */
    public String getGooglePlusUrl() {
        return googlePlusUrl;
    }

    /**
     * @brief set google plus url of item
     * @param googlePlusUrl in String
     */
    public void setGooglePlusUrl(String googlePlusUrl) {
        this.googlePlusUrl = googlePlusUrl;
    }

    /**
     * @brief get LinkedIn Url of item
     * @return linkedInUrl in String
     */
    public String getLinkedInUrl() {
        return linkedInUrl;
    }

    /**
     * @brief set LinkedIn Url of item
     * @param linkedInUrl in String
     */
    public void setLinkedInUrl(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }

    /**
     * @brief get latitude of item location
     * @return latitude in double
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @brief set latitude of item location
     * @param latitude in double
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * @brief get longitude of item location
     * @return longitude in double
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @brief set longitude of item location
     * @param longitude in double
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * @brief get rating of item
     * @return rating in float
     */
    public float getRating() {
        return rating;
    }

    /**
     * @brief set rating of item
     * @param rating in float
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * @brief get number of rating/comments
     * @return ratingCount in int
     */
    public int getRatingCount() {
        return ratingCount;
    }

    /**
     * @brief set number of rating/comments
     * @param ratingCount in int
     */
    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    /**
     * @brief get hotline of item
     * @return hotLine in String
     */
    public String getHotLine() {
        return hotLine;
    }

    /**
     * @brief set hotline of item
     * @param  hotLine in String
     */
    public void setHotLine(String hotLine) {
        this.hotLine = hotLine;
    }

    /**
     * @brief get tag line of item
     * @return tagLine in String
     */
    public String getTagLine() {
        return tagLine;
    }

    /**
     * @brief set tag line of item
     * @param tagLine in String
     */
    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    /**
     * @brief get description of item
     * @return description in String
     */
    public String getDescription() {
        return description;
    }

    /**
     * @brief set description of item
     * @param description in String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @brief check item is verified or not
     * @return true or false
     */
    public boolean isVerified() {
        return isVerified;
    }

    /**
     * @brief set item is verified or not
     * @param isVerified
     */
    public void setVerification(boolean isVerified) {
        this.isVerified = isVerified;
    }

    /**
     * @brief get category id  of the item
     * @return categoryId in String
     */
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * @brief set category id  of the item
     * @param  categoryId in String
     */
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @brief get district title  of the item
     * @return districtTitle in String
     */
    public String getDistrictTitle() {
        return districtTitle;
    }

    /**
     * @brief set district title  of the item
     * @param  districtTitle in String
     */
    public void setDistrictTitle(String districtTitle) {
        this.districtTitle = districtTitle;
    }

    /**
     * @brief get thumb image urls
     * @return imageThumbUrls in String[]
     */
    public String[] getImageThumbUrls() {
        return imageThumbUrls;
    }

    /**
     * @brief set thumb image urls
     * @param  imageThumbUrls in String[]
     */
    public void setImageThumbUrls(String[] imageThumbUrls) {
        this.imageThumbUrls = imageThumbUrls;
    }

    /**
     * @brief get large image urls
     * @return imageLargeUrls in String[]
     */
    public String[] getImageLargeUrls() {
        return imageLargeUrls;
    }

    /**
     * @brief set large image urls
     * @param  imageLargeUrls in String[]
     */
    public void setImageLargeUrls(String[] imageLargeUrls) {
        this.imageLargeUrls = imageLargeUrls;
    }

    /**
     * @brief get tag of item
     * @return tag in String
     */
    public String getTag() {
        return tag;
    }

    /**
     * @brief set tag of item
     * @param tag in String
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", districtId='" + districtId + '\'' +
                ", contactPhoneNumber='" + contactPhoneNumber + '\'' +
                ", telephoneNumber='" + telephoneNumber + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", facebookUrl='" + facebookUrl + '\'' +
                ", twitterUrl='" + twitterUrl + '\'' +
                ", googlePlusUrl='" + googlePlusUrl + '\'' +
                ", linkedInUrl='" + linkedInUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", hotLine='" + hotLine + '\'' +
                ", tagLine='" + tagLine + '\'' +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                ", description='" + description + '\'' +
                ", isVerified=" + isVerified +
                ", districtTitle='" + districtTitle + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", imageThumbUrls=" + Arrays.toString(imageThumbUrls) +
                ", imageLargeUrls=" + Arrays.toString(imageLargeUrls) +
                '}';
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(String totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getLeavesQuantity() {
        return leavesQuantity;
    }

    public void setLeavesQuantity(String leavesQuantity) {
        this.leavesQuantity = leavesQuantity;
    }

    public String getSellerUserName() {
        return sellerUserName;
    }

    public void setSellerUserName(String sellerUserName) {
        this.sellerUserName = sellerUserName;
    }
}
