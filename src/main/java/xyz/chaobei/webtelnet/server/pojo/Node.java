package xyz.chaobei.webtelnet.server.pojo;

import java.io.Serializable;

/**
 * @author xinxian
 * @create 2021-07-02 16:51
 **/
public class Node implements Serializable {

    private int port;

    private String ip;

    public Node(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
