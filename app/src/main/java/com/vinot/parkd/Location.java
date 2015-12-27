package com.vinot.parkd;

public class Location {

    private int mId = -1;
    private String mName = "Default Location";
    private double mLatitude, mLongitude;

    public Location(android.location.Location location) {
        this.mLatitude = location.getLatitude();
        this.mLongitude = location.getLongitude();
    }

    public Location(int id, String name, double latitude, double longitude) {
        this.mId = id;
        this.mName = name;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }
    private Location(Builder builder) {
        this.mId = builder.mId;
        this.mName = builder.mName;
        this.mLatitude = builder.mLatitude;
        this.mLongitude = builder.mLongitude;
    }

    public static class Builder {
        private int mId = -1;
        private double mLatitude, mLongitude;
        private String mName = null;
        public Location.Builder setId(int id) {
            mId = id;
            return Location.Builder.this;
        }
        public Location.Builder setName(String name) {
            mName = name;
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
        public Location build() { return new Location(Builder.this); }
    }

    /////////////
    // Getters //
    /////////////
    public int getId() { return mId; }
    public String getName() { return mName; }
    public double getLatitude() { return mLatitude; }
    public double getLongitude() { return mLongitude; }
}
