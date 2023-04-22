package com.driver.model;

import javax.persistence.*;

@Entity
public class Cab{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int ratePerKm;
    private boolean available;

    @OneToOne
    @JoinColumn
    public Driver driver;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRatePerKm() {
        return ratePerKm;
    }

    public void setRatePerKm(int ratePerKm) {
        this.ratePerKm = ratePerKm;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}