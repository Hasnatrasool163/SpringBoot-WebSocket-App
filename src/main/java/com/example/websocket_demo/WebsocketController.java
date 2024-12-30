package com.example.websocket_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebsocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebsocketSessionManager sessionManager;

    @Autowired // allow spring to auto-inject dependencies when creating a instance
    public WebsocketController(SimpMessagingTemplate messagingTemplate, WebsocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/message")
    public void handleMessage(Message message){
        System.out.println("Message Received from " + message.getUser() + ": " + message.getMessage());
        messagingTemplate.convertAndSend("/topic/messages",message);
        System.out.println("Sent Message To /topic/messages : " + message.getUser() + ": " + message.getMessage());
    }

    @MessageMapping("/connect")
    public void connectUser(String username){
        sessionManager.addUsername(username);
        sessionManager.BroadCastActiveUsernames();
        System.out.println(username + " is connected");
    }

    @MessageMapping("/disconnect")
    public void disconnectUser(String username){
        sessionManager.removeUsername(username);
        sessionManager.BroadCastActiveUsernames();
        System.out.println(username + " is disconnected");
    }

    @MessageMapping("/request-users")
    public void requestUsers(){
        sessionManager.BroadCastActiveUsernames();

    }
}
