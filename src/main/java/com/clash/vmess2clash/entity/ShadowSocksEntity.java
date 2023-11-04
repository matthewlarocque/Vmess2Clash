package com.clash.vmess2clash.entity;

public class ShadowSocksEntity extends BaseEntity {
    private String encMethod; // Encryption Method

    public String getEncMethod() {
        return encMethod;
    }

    public void setEncMethod(String encMethod) {
        this.encMethod = encMethod;
    }

    public ShadowSocksEntity() {}

    public ShadowSocksEntity(String ps, int port, String id, String add, String encMethod) {
        super(ps, port, id, add);
        this.encMethod = encMethod;
    }

    @Override
    public String toString() {
        return "ShadowSocksEntity{" +
                "ps='" + this.getPs() + '\'' +
                ", port='" + this.getPort() + '\'' +
                ", id='" + this.getId() + '\'' +
                ", add='" + this.getAdd() + '\'' +
                ", encMethod='" + encMethod + '\'' +
                '}';
    }
}
