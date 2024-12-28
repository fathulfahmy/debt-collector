package com.patui.debtcollector;

public class Debt {
    private String id;
    private String name;
    private double amount;
    private String timestamp;

    public Debt() {}

    public Debt(String id, String name, double amount, String timestamp) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
