package com.example.sso.utils;

import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

public class Utils {


    public static String httpRequest(String url, String prams) throws Exception {
        URL httpUrl = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
        urlConnection.setRequestMethod("POST");
        //是否携带参数
        urlConnection.setDoOutput(true);
        StringBuilder sb = new StringBuilder("");
        if (prams != null) {

            urlConnection.getOutputStream().write(prams.getBytes("UTF-8"));
        }
        urlConnection.connect();
        String response = StreamUtils.copyToString(urlConnection.getInputStream(), Charset.forName("UTF-8"));
        return response;
    }

    private static Properties ssoProperties = new Properties();
    public static String SERVER_URL_PREFIX; //统一认证中心地址
    public static String CLIENT_HOST_URL; //当前客户端地址

    static {
        try {
            ssoProperties.load(Utils.class.getClassLoader().getResourceAsStream("sso.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SERVER_URL_PREFIX = ssoProperties.getProperty("server_url_prefix");
        CLIENT_HOST_URL = ssoProperties.getProperty("client_host_url");
    }

    /**
     * 当客户端请求被拦截,跳往统一认证中心,需要带redirectUrl的参数,统一认证中心登录后回调的地址
     */
    public static String getRedirectUrl(HttpServletRequest request){
        //获取请求URL
        return CLIENT_HOST_URL+request.getServletPath();
    }
    /**
     * 根据request获取跳转到统一认证中心的地址,通过Response跳转到指定的地址，天猫！
     */
    public static void redirectToSSOURL(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String redirectUrl = getRedirectUrl(request);
        StringBuilder url = new StringBuilder(50)
                .append(SERVER_URL_PREFIX)
                .append("/sso/checkLogin?redirectUrl=")
                .append(redirectUrl);
        response.sendRedirect(url.toString());
    }
    /**
     * 获取客户端的完整登出地址
     */
    public static String getClientLogOutUrl(){
        return CLIENT_HOST_URL+"/module2/logOut";
    }
    /**
     * 获取认证中心的登出地址
     */
    public static String getServerLogOutUrl(){
        return SERVER_URL_PREFIX+"/sso/logOut";
    }


}
