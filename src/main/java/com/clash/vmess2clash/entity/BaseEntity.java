package com.clash.vmess2clash.entity;

public class BaseEntity {

    private String ps; // Name

    private int port;

    private String id; // Password

    private String add; // IP Address

    public BaseEntity() {}

    public BaseEntity(String ps, int port, String id, String add) {
        this.ps = ps;
        this.port = port;
        this.id = id;
        this.add = add;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "ps='" + ps + '\'' +
                ", port='" + port + '\'' +
                ", id='" + id + '\'' +
                ", add='" + add + '\'' +
                '}';
    }
}
