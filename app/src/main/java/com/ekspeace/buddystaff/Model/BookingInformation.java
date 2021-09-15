package com.ekspeace.buddystaff.Model;


import com.google.firebase.Timestamp;

public class BookingInformation {
    private String categoryName,serviceId, servicePrice, serviceName, customerId;
    private String customerName;
    private String customerPhone;
    private String customerPlayerId;
    private String time;
    private String customerAddress;
    private Long slot;
    private Timestamp timestamp;
    private String verified;

    public BookingInformation() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerPlayerId() {
        return customerPlayerId;
    }

    public void setCustomerPlayerId(String customerPlayerId) {
        this.customerPlayerId = customerPlayerId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryname) {
        this.categoryName = categoryname;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String servicename) {
        this.serviceName = servicename;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

}
