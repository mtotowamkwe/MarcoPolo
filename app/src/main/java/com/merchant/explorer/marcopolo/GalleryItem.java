package com.merchant.explorer.marcopolo;

public class GalleryItem {
    private String mCaption; // Title of the product
    private String mId;
    private String mUrl;
    private String mDescriptionFull; // Meta stuff about the product like vendor etc

    @Override
    public String toString() {
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getDescriptionFull() {
        return mDescriptionFull;
    }

    public void setDescriptionFull(String descriptionFull) {
        mDescriptionFull = descriptionFull;
    }
}
