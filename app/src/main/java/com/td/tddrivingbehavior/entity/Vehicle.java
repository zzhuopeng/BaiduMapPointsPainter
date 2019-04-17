package com.td.tddrivingbehavior.entity;

import java.util.Objects;

public class Vehicle {

    private String vehicleplatenumber;          //车牌号
    private String device_num;                  //设备号
    private String direction_angle;             //方向角
    private String lng;                         //经度
    private String lat;                         //纬度
    //    private String location_time;               //采集时间
    private String gps_speed;                   //GPS速度
    private String mileage;                     //GPS里程

    public Vehicle() {

    }

    public Vehicle(String vehicleplatenumber, String device_num, String direction_angle, String lng, String lat, String gps_speed, String mileage) {
        this.vehicleplatenumber = vehicleplatenumber;
        this.device_num = device_num;
        this.direction_angle = direction_angle;
        this.lng = lng;
        this.lat = lat;
        this.gps_speed = gps_speed;
        this.mileage = mileage;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(vehicleplatenumber, vehicle.vehicleplatenumber) &&
                Objects.equals(device_num, vehicle.device_num) &&
                Objects.equals(direction_angle, vehicle.direction_angle) &&
                Objects.equals(lng, vehicle.lng) &&
                Objects.equals(lat, vehicle.lat) &&
                Objects.equals(gps_speed, vehicle.gps_speed) &&
                Objects.equals(mileage, vehicle.mileage);
    }

    @Override
    public int hashCode() {

        return Objects.hash(vehicleplatenumber, device_num, direction_angle, lng, lat, gps_speed, mileage);
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

    public static final class VehicleBuilder {
        private String vehicleplatenumber;          //车牌号
        private String device_num;                  //设备号
        private String direction_angle;             //方向角
        private String lng;                         //经度
        private String lat;                         //纬度
        //    private String location_time;               //采集时间
        private String gps_speed;                   //GPS速度
        private String mileage;                     //GPS里程

        private VehicleBuilder() {
        }

        public static VehicleBuilder aVehicle() {
            return new VehicleBuilder();
        }

        public VehicleBuilder withVehicleplatenumber(String vehicleplatenumber) {
            this.vehicleplatenumber = vehicleplatenumber;
            return this;
        }

        public VehicleBuilder withDevice_num(String device_num) {
            this.device_num = device_num;
            return this;
        }

        public VehicleBuilder withDirection_angle(String direction_angle) {
            this.direction_angle = direction_angle;
            return this;
        }

        public VehicleBuilder withLng(String lng) {
            this.lng = lng;
            return this;
        }

        public VehicleBuilder withLat(String lat) {
            this.lat = lat;
            return this;
        }

        public VehicleBuilder withGps_speed(String gps_speed) {
            this.gps_speed = gps_speed;
            return this;
        }

        public VehicleBuilder withMileage(String mileage) {
            this.mileage = mileage;
            return this;
        }

        public Vehicle build() {
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleplatenumber(vehicleplatenumber);
            vehicle.setDevice_num(device_num);
            vehicle.setDirection_angle(direction_angle);
            vehicle.setLng(lng);
            vehicle.setLat(lat);
            vehicle.setGps_speed(gps_speed);
            vehicle.setMileage(mileage);
            return vehicle;
        }
    }
}
