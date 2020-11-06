
package com.easyplex.data.model.status;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Status {


    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("sold_at")
    @Expose
    private String soldAt;
    @SerializedName("license")
    @Expose
    private String license;
    @SerializedName("support_amount")
    @Expose
    private String supportAmount;
    @SerializedName("supported_until")
    @Expose
    private Object supportedUntil;
    @SerializedName("item")
    @Expose
    private Item item;

    @SerializedName("code")
    @Expose
    private String code;


    @SerializedName("buyer")
    @Expose
    private String buyer;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(String soldAt) {
        this.soldAt = soldAt;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getSupportAmount() {
        return supportAmount;
    }

    public void setSupportAmount(String supportAmount) {
        this.supportAmount = supportAmount;
    }

    public Object getSupportedUntil() {
        return supportedUntil;
    }

    public void setSupportedUntil(Object supportedUntil) {
        this.supportedUntil = supportedUntil;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }



}
