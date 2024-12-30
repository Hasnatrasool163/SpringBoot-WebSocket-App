package com.example.websocket_demo.client;

import com.example.websocket_demo.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyStompClient {

    private StompSession session;
    private String username;

    public MyStompClient(MessageListener messageListener,String username) throws ExecutionException, InterruptedException {
        this.username = username;

        // serialize message and deserialize to a format
        List<Transport> transports = new ArrayList<>();
        transports.add( new WebSocketTransport(new StandardWebSocketClient()));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter()); // json format

        MyStompSessionHandler sessionHandler = new MyStompSessionHandler(messageListener,username);
        String url = "ws://localhost:8080/ws"; // ws for websocket

        session = stompClient.connectAsync(url,sessionHandler).get();
    }

    public void sendMessage(Message message){
        try{
            session.send("/app/message",message);
            System.out.println("Message sent :  " + message.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectUser(String username){
        session.send("/app/disconnect",username);
        System.out.println("Disconnected : "+ username);
    }


}