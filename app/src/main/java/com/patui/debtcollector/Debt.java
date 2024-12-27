package com.patui.debtcollector;

public class Debt {
    private String id;
    private String name;
    private double amount;
    private boolean collected;

    public Debt() { }

    public Debt(String id, String name, double amount, boolean collected) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.collected = collected;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public boolean isCollected() { return collected; }
}
