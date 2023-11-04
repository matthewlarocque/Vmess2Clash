package com.clash.vmess2clash.entity;

public class VmessEntity extends BaseEntity {

    private int aid; // Alter ID

    private String net; // Transport Protocol

    private String type; // Uses http?

    private String tls; // Uses which type of tls option?

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTls() {
        return tls;
    }

    public void setTls(String tls) {
        this.tls = tls;
    }

    public VmessEntity() {}

    public VmessEntity(String ps, int port, String id, String add, int aid, String net, String type, String tls) {
        super(ps, port, id, add);
        this.aid = aid;
        this.net = net;
        this.type = type;
        this.tls = tls;
    }

    @Override
    public String toString() {
        return "VmessEntity{" +
                "ps='" + this.getPs() + '\'' +
                ", port='" + this.getPort() + '\'' +
                ", id='" + this.getId() + '\'' +
                ", add='" + this.getAdd() + '\'' +
                ", aid=" + aid +
                ", net='" + net + '\'' +
                ", type='" + type + '\'' +
                ", tls='" + tls + '\'' +
                '}';
    }
}
