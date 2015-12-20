package com.vinot.parkd;

public class Location {
    private int mId;
    private String mName;

    public Location(int id, String name) {
        this.mId = id;
        this.mName = name;
    }
    private Location(Builder builder) {
        this.mId = builder.mId;
        this.mName = builder.mName;
    }

    public static class Builder {
        private int mId = -1;
        private String mName = null;
        public Location.Builder setId(int id) {
            mId = id;
            return Location.Builder.this;
        }
        public Location.Builder setName(String name) {
            mName = name;
            return Location.Builder.this;
        }
        public Location build() { return new Location(Builder.this); }
    }

    /////////////
    // Getters //
    /////////////
    public int getId() { return mId; }
    public String getName() { return mName; }
}
