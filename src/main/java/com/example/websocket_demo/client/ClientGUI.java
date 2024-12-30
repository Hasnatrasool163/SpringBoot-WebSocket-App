package com.example.websocket_demo.client;


import com.example.websocket_demo.Message;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ClientGUI extends JFrame implements MessageListener {

    private JPanel connectedUsersPanel,messagePanel;
    private MyStompClient stompClient;
    private String username;
    private JScrollPane messagePanelScrollPane;

    public ClientGUI (String username) throws ExecutionException, InterruptedException {
        super("User : "+username);
        this.username=username;
        stompClient = new MyStompClient(this,username);

        setSize(600,600);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               int option =  JOptionPane.showConfirmDialog(ClientGUI.this,"Do you really want to exit?",
                        "Exit",JOptionPane.YES_NO_OPTION);
               if(option==JOptionPane.YES_OPTION){
                   stompClient.disconnectUser(username);
                   ClientGUI.this.dispose();
               }

            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateMessageSize();
            }
        });

        getContentPane().setBackground(Utilities.PRIMARY_COLOR);
        addGuiComponents();
    }

    private void addGuiComponents(){
        addConnectedUsersComponents();
        addChatComponents();
    }

    private void addChatComponents() {
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel,BoxLayout.Y_AXIS));
        messagePanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messagePanelScrollPane = new JScrollPane(messagePanel);
        messagePanelScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        messagePanelScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        messagePanelScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        messagePanelScrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                revalidate();
                repaint();
            }
        });
        chatPanel.add(messagePanelScrollPane,BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        inputPanel.setBorder(Utilities.addPadding(10,10,10,10));

        JTextField inputField = getTextField();

        inputPanel.add(inputField,BorderLayout.CENTER);
        chatPanel.add(inputPanel,BorderLayout.SOUTH);

        add(chatPanel,BorderLayout.CENTER);

    }

    private JTextField getTextField() {
        JTextField inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    String input = inputField.getText();

                    if(input.isEmpty()) return;

                    inputField.setText("");

                    stompClient.sendMessage(new Message(username,input));
                }
            }
        });
        inputField.setBackground(Utilities.SECONDARY_COLOR);
        inputField.setForeground(Utilities.TEXT_COLOR);
        inputField.setBorder(Utilities.addPadding(0,10,0,10));
        inputField.setSelectedTextColor(Color.MAGENTA);
        inputField.setFont(new Font("Consolas",Font.BOLD,18));
        inputField.setPreferredSize(new Dimension(inputField.getWidth(),30));
        return inputField;
    }

    private void addConnectedUsersComponents() {
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel,BoxLayout.Y_AXIS));
        connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);
        connectedUsersPanel.setPreferredSize(new Dimension(200,getHeight()));

        JLabel connectedUsersLabel = new JLabel("Connected Users: " );
        connectedUsersLabel.setFont(new Font("Consolas",Font.BOLD,18));
        connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);
        connectedUsersLabel.setBorder(Utilities.addPadding(10,10,10,10));
        connectedUsersPanel.add(connectedUsersLabel);

        add(connectedUsersPanel,BorderLayout.WEST);

    }

    private JPanel chatComponentPanel(Message message){
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        chatPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        chatPanel.setBorder(Utilities.addPadding(20,20,10,20));

        JLabel usernameLabel = new JLabel(message.getUser());
        usernameLabel.setForeground(Utilities.TEXT_COLOR);
        usernameLabel.setFont(new Font("Consolas",Font.BOLD,18));

        chatPanel.add(usernameLabel);

        JLabel messageLabel = new JLabel();
        messageLabel.setText("<html>" +
                "<body style='width:" + (0.80 * getWidth()) +"'px>"
                + message.getMessage()
                +"</body>"+
                "</html>");
        messageLabel.setFont(new Font("Consolas",Font.PLAIN,16));
        messageLabel.setForeground(Utilities.TEXT_COLOR);
        System.out.println(messageLabel.getText());

        JTextArea messageArea = new JTextArea();
        messageArea.setText(message.getMessage());
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setFont(new Font("Consolas",Font.PLAIN,16));
        messageArea.setBackground(Utilities.TRANSPARENT_COLOR);
        messageArea.setForeground(Utilities.TEXT_COLOR);
        messageArea.setMaximumSize(new Dimension(getWidth(),30));
        messageArea.setAutoscrolls(true);

        chatPanel.add(messageArea);

        return chatPanel;
    }


    @Override
    public void onMessageReceive(Message message) {
        messagePanel.add(chatComponentPanel(message));
        revalidate();
        repaint();

        messagePanelScrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
    }

    @Override
    public void onActiveUsersUpdated(ArrayList<String> users) {

        if(connectedUsersPanel.getComponents().length >=2){
            connectedUsersPanel.remove(1);
        }

        JPanel userListPanel = new JPanel();
        userListPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        userListPanel.setLayout(new BoxLayout(userListPanel,BoxLayout.Y_AXIS));
        userListPanel.setBorder(Utilities.addPadding(0,10,0,10));

        for(String user : users){
            JLabel username = new JLabel(user);
            username.setForeground(Utilities.TEXT_COLOR);
            username.setFont(new Font("Consolas",Font.BOLD,16));
            userListPanel.add(username);
        }

        connectedUsersPanel.add(userListPanel);
        revalidate();
        repaint();
    }

    private void updateMessageSize() {
        for (int i = 0; i <messagePanel.getComponents().length ; i++) {

            Component component = messagePanel.getComponent(i);
            if(component instanceof JPanel chatMessage){

                if(chatMessage.getComponent(1) instanceof JTextArea messageLabel){
                    messageLabel.setLineWrap(true);
                    messageLabel.setWrapStyleWord(true);
                    revalidate();
                    messageLabel.repaint();
                    messageLabel.setText(messageLabel.getText());
                }
            }
        }
    }

}
