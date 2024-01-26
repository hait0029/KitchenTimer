package com.example.kitchentimer;

import android.os.Parcel;
import android.os.Parcelable;

public class FoodTimerItem implements Parcelable {
    private String foodType;
    private int hours;
    private int minutes;
    private int seconds;

    public FoodTimerItem(String foodType, int hours, int minutes, int seconds) {
        this.foodType = foodType;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public String getFoodType() {
        return foodType;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void decrementSecond() {
        if (seconds > 0) {
            seconds--;
        }
    }

    public void decrementMinute() {
        if (minutes > 0) {
            minutes--;
        }
    }

    public void decrementHour() {
        if (hours > 0) {
            hours--;
        }
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    protected FoodTimerItem(Parcel in) {
        foodType = in.readString();
        hours = in.readInt();
        minutes = in.readInt();
        seconds = in.readInt();
    }

    public static final Creator<FoodTimerItem> CREATOR = new Creator<FoodTimerItem>() {
        @Override
        public FoodTimerItem createFromParcel(Parcel in) {
            return new FoodTimerItem(in);
        }

        @Override
        public FoodTimerItem[] newArray(int size) {
            return new FoodTimerItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(foodType);
        dest.writeInt(hours);
        dest.writeInt(minutes);
        dest.writeInt(seconds);
    }
}
