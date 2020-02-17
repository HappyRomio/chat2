package com.tele2test.controller;

import com.tele2test.entity.Message;
import com.tele2test.services.MessageHistoryService;
import com.tele2test.services.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class MainController {

    private ConcurrentHashMap<String, Integer> certificationCosts = new ConcurrentHashMap<>();
    private Set<String> activeUsers = certificationCosts.newKeySet();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");

    @Autowired
    UserDataService userDataService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private WebSocketMessageBrokerStats webSocketMessageBrokerStats;

    @Autowired
    private MessageHistoryService messageHistoryService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String adminPage(@RequestParam(required = false) boolean error, Model model) {
        model.addAttribute("message", error ? "Пользователь с таким именем уже существует!" : "");

        return "index";
    }

    @RequestMapping(value = "/chat", method = RequestMethod.GET)
    public String loginRegist(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        webSocketMessageBrokerStats.setLoggingPeriod(5000);

        return "chat";
    }

    @EventListener
    private void onSessionConnectedEvent(SessionConnectedEvent event) throws Exception {
        activeUsers.add(event.getUser().getName());
        messageHistoryService.saveToDB();

    }

    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) {
        System.out.println("Disconnected");
        activeUsers.remove(event.getUser().getName());
        userDataService.dropUser(event.getUser().getName());
        simpMessagingTemplate.convertAndSend("/chat/onlineList", activeUsers);
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) throws InterruptedException {
        String sessionId = event.getMessage().getHeaders().get("simpSessionId").toString();

        if (event.getMessage().getHeaders().get("simpDestination").toString().startsWith("/user")) {
            for (Message msg : userDataService.getTop20ofHistory()) {
                simpMessagingTemplate.convertAndSendToUser(sessionId, "/messages", msg);
            }
        }
        Thread.sleep(100);
        if (event.getMessage().getHeaders().get("simpDestination").toString().startsWith("/chat/onlineList")) {
            simpMessagingTemplate.convertAndSend("/chat/onlineList", activeUsers);
        }

    }

    @MessageMapping("/message")
    @SendTo("/chat/messages")
    public Message getMessages(Message message, Principal principal) {
        System.out.println(sdf.format(new Date()));
        message.setTime(sdf.format(new Date()));
        message.setFrom(principal.getName());
        messageHistoryService.addToBuffer(message);
        return message;
    }

    @MessageMapping("/chat-messaging")
    public void sendSpecific(
            @Payload Message msg,
            Principal user,
            @Header("simpSessionId") String sessionId) throws Exception {
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/messages", msg);

    }

    @RequestMapping(value = {"/signin"}, method = RequestMethod.GET)
    public String signInPage(Model model) {
        return "/chat";
    }
}
