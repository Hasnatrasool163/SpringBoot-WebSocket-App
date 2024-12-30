package com.example.websocket_demo;

import com.example.websocket_demo.client.ClientGUI;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class App {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            String username = JOptionPane.showInputDialog(null,"Enter UserName (Max 14 Character )",
                    "Chat Applicaition",
                    JOptionPane.QUESTION_MESSAGE);
            if(username ==null || username.length() > 14 || username.isEmpty()){
                JOptionPane.showMessageDialog(null,
                        "Invalid Username",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ClientGUI clientGUI;
            try {
                clientGUI = new ClientGUI(username);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            clientGUI.setVisible(true);
        });
    }
}
