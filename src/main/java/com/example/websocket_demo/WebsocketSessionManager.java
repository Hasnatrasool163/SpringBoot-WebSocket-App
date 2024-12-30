package com.example.websocket_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class WebsocketSessionManager {

    public final ArrayList<String> activeUsernames = new ArrayList<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebsocketSessionManager(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void addUsername(String username){
        activeUsernames.add(username);
    }

    public void removeUsername(String username){
        activeUsernames.remove(username);
    }

    public void BroadCastActiveUsernames(){
        messagingTemplate.convertAndSend("/topic/users",activeUsernames);
        System.out.println("BroadCasting to /topic/users"  + activeUsernames);
    }
}
