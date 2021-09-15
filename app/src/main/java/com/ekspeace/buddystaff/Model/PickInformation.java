package com.ekspeace.buddystaff.Model;


import com.google.firebase.Timestamp;

public class PickInformation {
    private String serviceName,serviceId, servicePrice, customerId;
    private String customerPhone,customerName, customerAddress, customerPlayerId;
    private String dateTime;
    private String timeAgo;
    private String pickUpType;
    private String pickUpInfo;
    private Timestamp timestamp;
    private String verified;

    public PickInformation() {
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

    public PickInformation(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String time) {
        this.dateTime = time;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getPickUpType() {
        return pickUpType;
    }

    public void setPickUpType(String pickUpType) {
        this.pickUpType = pickUpType;
    }

    public String getPickUpInfo() {
        return pickUpInfo;
    }

    public void setPickUpInfo(String pickUpInfo) {
        this.pickUpInfo = pickUpInfo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }
}
