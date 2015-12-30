package com.vinot.parkd;

public class Location {
    private final static String DEFAULT_LOCATION = "Default Location";
    private final static String DEFAULT_SUBURB = "Default Suburb";
    private final static String DEFAULT_STATE = "Default State";
    private final static int DEFAULT_POSTCODE = 9999;
    private final static int DEFAULT_ID = -1;
    private final static int DEFAULT_NUMBER_OF_PARKS = 5;
    private final static float DEFAULT_PRICE = 4f;

    private int mId, mPostcode, mNumberOfParks;
    private String mName, mSuburb, mState;
    private double mLatitude, mLongitude;
    private float mCurrentPrice;

    private Location(Builder builder) {
        this.mId = builder.mId;
        this.mName = builder.mName;
        this.mSuburb = builder.mSuburb;
        this.mState = builder.mState;
        this.mPostcode = builder.mPostcode;
        this.mLatitude = builder.mLatitude;
        this.mLongitude = builder.mLongitude;
        this.mCurrentPrice = builder.mCurrentPrice;
        this.mNumberOfParks = builder.mNumberOfParks;
    }

    public static class Builder {
        private int mId = DEFAULT_ID;
        private int mPostcode = DEFAULT_POSTCODE;
        private int mNumberOfParks = DEFAULT_NUMBER_OF_PARKS;
        private String mName = DEFAULT_LOCATION;
        private String mSuburb = DEFAULT_SUBURB;
        private String mState = DEFAULT_STATE;
        private double mLatitude, mLongitude;
        private float mCurrentPrice = DEFAULT_PRICE;
        public Location.Builder setId(int id) {
            mId = id;
            return Location.Builder.this;
        }
        public Location.Builder setName(String name) {
            mName = name;
            return Location.Builder.this;
        }
        public Location.Builder setSuburb(String suburb) {
            mSuburb = suburb;
            return Location.Builder.this;
        }
        public Location.Builder setState(String state) {
            mState = state;
            return Location.Builder.this;
        }
        public Location.Builder setPostcode(int postcode) {
            mPostcode = postcode;
            return Location.Builder.this;
        }
        public Location.Builder setLatitude(double latitude) {
            mLatitude = latitude;
            return Location.Builder.this;
        }
        public Location.Builder setLongitude(double longitude) {
            mLongitude = longitude;
            return Location.Builder.this;
        }
        public Location.Builder setCurrentPrice(float currentPrice) {
            mCurrentPrice = currentPrice;
            return Location.Builder.this;
        }
        public Location.Builder setNumberOfParks(int numberOfParks) {
            mNumberOfParks = numberOfParks;
            return Location.Builder.this;
        }
        public Location build() { return new Location(Builder.this); }
    }

    public int getId() { return mId; }
    public int getNumberOfParks() { return mNumberOfParks; }
    public String getName() { return mName; }
    public String getSuburb() { return mSuburb; }
    public String getState() { return mState; }
    public int getPostcode() { return mPostcode; }
    public double getLatitude() { return mLatitude; }
    public double getLongitude() { return mLongitude; }
    public float getCurrentPrice() { return mCurrentPrice; }
}
