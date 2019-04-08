package com.td.tddrivingbehavior.entity;

import java.util.Objects;

public class Vehicle {

    private String vehicleplatenumber;          //车牌号
    private String device_num;                  //设备号
    private String direction_angle;             //方向角
    private String lng;                         //经度
    private String lat;                         //纬度
    private String acc_state;                   //点火1/熄火0
    private String right_turn_signals;          //灭0/开1
    private String left_turn_signals;           //灭0/开1
    private String hand_brake;                  //灭0/开1
    private String foot_brake;                  //灭0/有1
    private String location_time;               //采集时间
    private String gps_speed;                   //GPS速度
    private String mileage;                     //GPS里程

    public Vehicle(){

    }

    private Vehicle(Builder builder) {
        setVehicleplatenumber(builder.vehicleplatenumber);
        setDevice_num(builder.device_num);
        setDirection_angle(builder.direction_angle);
        setLng(builder.lng);
        setLat(builder.lat);
        setAcc_state(builder.acc_state);
        setRight_turn_signals(builder.right_turn_signals);
        setLeft_turn_signals(builder.left_turn_signals);
        setHand_brake(builder.hand_brake);
        setFoot_brake(builder.foot_brake);
        setLocation_time(builder.location_time);
        setGps_speed(builder.gps_speed);
        setMileage(builder.mileage);
    }

    public String getVehicleplatenumber() {
        return vehicleplatenumber;
    }

    public void setVehicleplatenumber(String vehicleplatenumber) {
        this.vehicleplatenumber = vehicleplatenumber;
    }

    public String getDevice_num() {
        return device_num;
    }

    public void setDevice_num(String device_num) {
        this.device_num = device_num;
    }

    public String getDirection_angle() {
        return direction_angle;
    }

    public void setDirection_angle(String direction_angle) {
        this.direction_angle = direction_angle;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAcc_state() {
        return acc_state;
    }

    public void setAcc_state(String acc_state) {
        this.acc_state = acc_state;
    }

    public String getRight_turn_signals() {
        return right_turn_signals;
    }

    public void setRight_turn_signals(String right_turn_signals) {
        this.right_turn_signals = right_turn_signals;
    }

    public String getLeft_turn_signals() {
        return left_turn_signals;
    }

    public void setLeft_turn_signals(String left_turn_signals) {
        this.left_turn_signals = left_turn_signals;
    }

    public String getHand_brake() {
        return hand_brake;
    }

    public void setHand_brake(String hand_brake) {
        this.hand_brake = hand_brake;
    }

    public String getFoot_brake() {
        return foot_brake;
    }

    public void setFoot_brake(String foot_brake) {
        this.foot_brake = foot_brake;
    }

    public String getLocation_time() {
        return location_time;
    }

    public void setLocation_time(String location_time) {
        this.location_time = location_time;
    }

    public String getGps_speed() {
        return gps_speed;
    }

    public void setGps_speed(String gps_speed) {
        this.gps_speed = gps_speed;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(getVehicleplatenumber(), vehicle.getVehicleplatenumber()) &&
                Objects.equals(getDevice_num(), vehicle.getDevice_num()) &&
                Objects.equals(getDirection_angle(), vehicle.getDirection_angle()) &&
                Objects.equals(getLng(), vehicle.getLng()) &&
                Objects.equals(getLat(), vehicle.getLat()) &&
                Objects.equals(getAcc_state(), vehicle.getAcc_state()) &&
                Objects.equals(getRight_turn_signals(), vehicle.getRight_turn_signals()) &&
                Objects.equals(getLeft_turn_signals(), vehicle.getLeft_turn_signals()) &&
                Objects.equals(getHand_brake(), vehicle.getHand_brake()) &&
                Objects.equals(getFoot_brake(), vehicle.getFoot_brake()) &&
                Objects.equals(getLocation_time(), vehicle.getLocation_time()) &&
                Objects.equals(getGps_speed(), vehicle.getGps_speed()) &&
                Objects.equals(getMileage(), vehicle.getMileage());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getVehicleplatenumber(), getDevice_num(), getDirection_angle(), getLng(), getLat(), getAcc_state(), getRight_turn_signals(), getLeft_turn_signals(), getHand_brake(), getFoot_brake(), getLocation_time(), getGps_speed(), getMileage());
    }

    //Builder模式：内部静态类
    private static class Builder {
        private String vehicleplatenumber;          //车牌号
        private String device_num;                  //设备号
        private String direction_angle;             //方向角
        private String lng;                         //经度
        private String lat;                         //纬度
        private String acc_state;                   //点火1/熄火0
        private String right_turn_signals;          //灭0/开1
        private String left_turn_signals;           //灭0/开1
        private String hand_brake;                  //灭0/开1
        private String foot_brake;                  //灭0/有1
        private String location_time;               //采集时间
        private String gps_speed;                   //GPS速度
        private String mileage;                     //GPS里程

        public Builder() {
        }


        public Builder vehicleplatenumber(String val) {
            vehicleplatenumber = val;
            return this;
        }

        public Builder device_num(String val) {
            device_num = val;
            return this;
        }

        public Builder direction_angle(String val) {
            direction_angle = val;
            return this;
        }

        public Builder lng(String val) {
            lng = val;
            return this;
        }

        public Builder lat(String val) {
            lat = val;
            return this;
        }

        public Builder acc_state(String val) {
            acc_state = val;
            return this;
        }

        public Builder right_turn_signals(String val) {
            right_turn_signals = val;
            return this;
        }

        public Builder left_turn_signals(String val) {
            left_turn_signals = val;
            return this;
        }

        public Builder hand_brake(String val) {
            hand_brake = val;
            return this;
        }

        public Builder foot_brake(String val) {
            foot_brake = val;
            return this;
        }

        public Builder location_time(String val) {
            location_time = val;
            return this;
        }

        public Builder gps_speed(String val) {
            gps_speed = val;
            return this;
        }

        public Builder mileage(String val) {
            mileage = val;
            return this;
        }

        public Vehicle build() {
            return new Vehicle(this);
        }
    }
}
