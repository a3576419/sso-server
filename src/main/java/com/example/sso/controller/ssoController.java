package com.example.sso.controller;

import com.example.sso.db.MockDB;
import com.example.sso.pojo.ClientInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/sso")
public class ssoController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @RequestMapping("/index")
    public String index(){
        return "loginRoot";
    }

    // redirectUrl 我从哪里来~
    @RequestMapping("/login")
    public String login(String username, String password,
                        String redirectUrl,
                        HttpServletRequest request,
                        Model model,
                        ModelMap modelMap){
        System.out.println(username+","+password);

        //登录成功
        if ("admin".equals(username) && "123456".equals(password)){

            // 1. 服务器端创建令牌
            String token = UUID.randomUUID().toString();
            System.out.println("token创建成功=>"+token);
            // 2. 创建全局会话，将令牌信息存入
            HttpSession session = request.getSession();
            session.setAttribute("token",token);
            session.setMaxInactiveInterval(60 * 30);
            // 3. 存在数据库中
            MockDB.T_TOKEN.add(token);
            //redisTemplate.opsForValue().set(token,username);
            // 4. 返回给用户，来时的地方
            model.addAttribute("token",token);

            return "redirect:"+redirectUrl+"?token="+token; //  ?token = xxxxx
        }
        System.out.println("用户名或密码错误！");
        modelMap.addAttribute("redirectUrl",redirectUrl);
        modelMap.addAttribute("message","用户名或密码错误！");
        // 登录的操作，保存token数据  redis。。。 mock
        return "loginRoot";
    }

    // checkLogin
    @RequestMapping("/checkLogin")
    public String checkLogin(String redirectUrl, HttpSession session,Model model){

        //1. 是否存在会话。
        String token = (String) session.getAttribute("token");
        if (StringUtils.isEmpty(token)){
            // 没有全局会话，需要登录，跳转到登录页面，需要携带我从哪里来~
            model.addAttribute("redirectUrl",redirectUrl);
            return "loginRoot";
        }else { // 存在全局会话
            // 取出令牌 token，返回给客户端
            model.addAttribute("token",token);
            return "redirect:" + redirectUrl; //model  ?token=xxxx
        }

    }

    //verify
    @RequestMapping("/verify")
    @ResponseBody
    public String verifyToken(String token,String clientUrl,String jsessionid){
        if (MockDB.T_TOKEN.contains(token)){
            System.out.println("服务器端token校验成功！"+token);
            // 存表操作！
            /*************/
            List<ClientInfo> clientInfoList = MockDB.T_CILENT_INFO.get(token);
            if (clientInfoList==null){
                clientInfoList = new ArrayList<>();
                MockDB.T_CILENT_INFO.put(token,clientInfoList);
            }
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setUrl(clientUrl);
            clientInfo.setJsessionid(jsessionid);
            clientInfoList.add(clientInfo);
            return "true";
        }
        return "false";
    }

    @RequestMapping("/logOut")
    public String logOut(HttpSession session){
        session.invalidate();
        return "loginRoot";
    }
}
