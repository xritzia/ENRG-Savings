package com.example.enrgsavings;

/**
 * Represents the details of an invoice for energy consumption.
 * Stores information about the energy provider, billing period, invoice-specific details etc.
 */
public class InvoiceData {
    private String provider;
    private int year;
    private int month;
    private String invoiceName;
    private String colorClass;
    private double fixedRate;
    private String fixedDiscount;
    private double finalPrice;
    private String finalDiscount;
    private String comments;

    /**
     * Setting up getters and setters.
     */
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }

    public String getcolorClass() {
        return colorClass;
    }

    public void setcolorClass(String colorClass) {
        this.colorClass = colorClass;
    }

    public double getFixedRate() {
        return fixedRate;
    }

    public void setFixedRate(double fixedRate) {
        this.fixedRate = fixedRate;
    }

    public String getFixedDiscount() {
        return fixedDiscount;
    }

    public void setFixedDiscount(String fixedDiscount) {
        this.fixedDiscount = fixedDiscount;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getFinalDiscount() {
        return finalDiscount;
    }

    public void setFinalDiscount(String finalDiscount) {
        this.finalDiscount = finalDiscount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}