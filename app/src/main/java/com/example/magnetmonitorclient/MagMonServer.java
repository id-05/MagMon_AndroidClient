package com.example.magnetmonitorclient;

public class MagMonServer {
    int id;
    Boolean Connect;
    String Name;
    String IP;
    String Port;

    public Boolean getConnect() {
        return Connect;
    }

    public void setConnect(Boolean connect) {
        Connect = connect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String port) {
        Port = port;
    }
}
