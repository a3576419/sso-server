package com.example.sso.pojo;

public class ClientInfo {
    private String url; //客户端的登陆地址url
    private String jsessionid;//用户sessionId

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJsessionid() {
        return jsessionid;
    }

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "url='" + url + '\'' +
                ", jsessionid='" + jsessionid + '\'' +
                '}';
    }
}

