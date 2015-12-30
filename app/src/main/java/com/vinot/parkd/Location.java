package com.vinot.parkd;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Location implements Parcelable {
    public static final String TAG = Location.class.getSimpleName();

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

    // Parcelable

    private static final String KEY_INTS = Location.class.getCanonicalName() + ".KEY_INTS";
    private static final String KEY_STRINGS = Location.class.getCanonicalName() + ".KEY_STRINGS";
    private static final String KEY_DOUBLES = Location.class.getCanonicalName() + ".KEY_DOUBLES";
    private static final String KEY_FLOAT = Location.class.getCanonicalName() + ".KEY_FLOAT";

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            try {
                Bundle bundle = source.readBundle();
                int ints[] = bundle.getIntArray(KEY_INTS);
                String strings[] = bundle.getStringArray(KEY_STRINGS);
                double doubles[] = bundle.getDoubleArray(KEY_DOUBLES);
                return (new Location.Builder())
                        .setId(ints[0])
                        .setPostcode(ints[1])
                        .setNumberOfParks(ints[2])
                        .setName(strings[0])
                        .setSuburb(strings[1])
                        .setState(strings[2])
                        .setLatitude(doubles[0])
                        .setLongitude(doubles[1])
                        .setCurrentPrice(bundle.getFloat(KEY_FLOAT))
                        .build();
            } catch (NullPointerException e) {
                Log.wtf(TAG, e);
            }
            return null;
        }

        @Override
        public Location[] newArray(int size) {
            // todo implement this correctly
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putIntArray(KEY_INTS, new int[]{mId, mPostcode, mNumberOfParks});
        bundle.putStringArray(KEY_STRINGS, new String[]{mName, mSuburb, mState});
        bundle.putDoubleArray(KEY_DOUBLES, new double[]{mLatitude, mLongitude});
        bundle.putFloat(KEY_FLOAT, mCurrentPrice);
        dest.writeBundle(bundle);
    }
}
