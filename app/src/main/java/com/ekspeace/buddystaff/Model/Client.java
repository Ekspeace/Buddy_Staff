package com.ekspeace.buddystaff.Model;

public class Client {

    private String name;
    private String phone;
    private String email;
    private String password;
    private String playerId;
    private String Id;

    public Client() {
    }

    public Client(String ClientId, String name, String phone, String email, String password, String playerId) {
        this.Id = ClientId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.playerId = playerId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String ClientId) {
        this.Id = ClientId;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
