package com.example.weather.models;

public class Hours {
    private String hour;
    private int image;
    private String temp;

    public Hours(String hour, int image, String temp) {
        this.hour = hour;
        this.image = image;
        this.temp = temp;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
