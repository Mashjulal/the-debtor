package com.mashjulal.the_debtor;

import android.net.Uri;

public class Debt {

    private Long id;
    private String type;
    private String thingName;
    private Uri photoPath;
    private Double moneyAmount;
    private Long borrowDate;
    private Long returnDate;
    private String description;
    private String recipientName;

    public Debt(String type, String thingName, Uri photoPath, Double moneyAmount, Long borrowDate, Long returnDate, String description, String recipientName) {
        this.type = type;
        this.thingName = thingName;
        this.photoPath = photoPath;
        this.moneyAmount = moneyAmount;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.description = description;
        this.recipientName = recipientName;
    }

    public Debt(Long id, String type, String thingName, Uri photoPath, Double moneyAmount, Long borrowDate, Long returnDate, String description, String recipientName) {
        this.id = id;
        this.type = type;
        this.thingName = thingName;
        this.photoPath = photoPath;
        this.moneyAmount = moneyAmount;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.description = description;
        this.recipientName = recipientName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThingName() {
        return thingName;
    }

    public void setThingName(String thingName) {
        this.thingName = thingName;
    }

    public Uri getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(Uri photoPath) {
        this.photoPath = photoPath;
    }

    public Double getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(Double moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public Long getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Long borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Long getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Long returnDate) {
        this.returnDate = returnDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
