package com.example.demo.ws;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@ServerEndpoint("/chat")
@Component
// 此外，外面还要加上全局的配置类
public class webSocketController {

    private Session session;

    private static Map<String, Session> sessions = new HashMap<>();
    private Random random;

    @OnOpen
    // 连接建立时调用
    public void onOpen(Session session, EndpointConfig config){
        this.random = new Random();
        this.session = session;
        sendRandomNumberEverySecond();

        // 添加新连接的会话到sessions
        sessions.put(session.getId(), session);
    }

    @OnMessage
    // 接收客户端数据时被调用
    public void onMessage(String message,Session session) throws IOException {
        System.out.println("接收到消息: " + message);

        // 广播给所有用户
        broadcast(message);

        // 单独发送给某一用户
        sendToUser(message, session.getId());

        System.out.println("单用户信息：" + session.getId() );
    }

    @OnClose
    // 连接关闭时被调用
    public void onClose(Session session){
        // 移除关闭的会话
        sessions.remove(session.getId());
    }

    private void sendRandomNumberEverySecond() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    int randomNumber = random.nextInt(100);
                    // 如果连接畅通，发送信息给客户端
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(String.valueOf(randomNumber));
                    }
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void broadcast(String message) throws IOException {
        String clientCountMessage = "当前连接的客户端数量: " + sessions.size();
        for (Session session : sessions.values()) {
            session.getBasicRemote().sendText(clientCountMessage);
        }
    }

    private void sendToUser(String message, String sessionId) throws IOException {
        Session session = sessions.get(sessionId);
        if (session != null) {
            session.getBasicRemote().sendText("单独发送给你的消息: " + message);
        }
    }
}
