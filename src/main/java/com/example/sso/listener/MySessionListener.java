package com.example.sso.listener;

import com.example.sso.db.MockDB;
import com.example.sso.pojo.ClientInfo;
import com.example.sso.utils.Utils;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.List;
@WebListener
public class MySessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override //销毁事件
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String token = (String) session.getAttribute("token");
        //销毁用户信息
        MockDB.T_TOKEN.remove(token);
        List<ClientInfo> clientInfoList = MockDB.T_CILENT_INFO.remove(token);
        for (ClientInfo clientInfo : clientInfoList) {
            //遍历通知客户端注销
            try {
                Utils.httpRequest(clientInfo.getUrl(),clientInfo.getJsessionid());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
