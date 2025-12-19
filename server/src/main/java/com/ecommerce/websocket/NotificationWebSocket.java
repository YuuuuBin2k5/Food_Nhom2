package com.ecommerce.websocket;

import com.ecommerce.dto.NotificationDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/notifications/{userId}")
public class NotificationWebSocket {
    
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                
                @Override
                public void write(JsonWriter out, LocalDateTime value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.format(formatter));
                    }
                }
                
                @Override
                public LocalDateTime read(JsonReader in) throws IOException {
                    return LocalDateTime.parse(in.nextString(), formatter);
                }
            })
            .create();
    
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessions.put(userId, session);
        System.out.println("WebSocket opened for user: " + userId);
        
        // Send welcome message
        try {
            session.getBasicRemote().sendText("{\"type\":\"connected\",\"message\":\"WebSocket connected successfully\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
        System.out.println("WebSocket closed for user: " + userId);
    }
    
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
        throwable.printStackTrace();
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
        // Handle incoming messages if needed (e.g., ping/pong)
    }
    
    /**
     * Send notification to a specific user
     */
    public static void sendNotificationToUser(String userId, NotificationDTO notification) {
        Session session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = gson.toJson(notification);
                session.getBasicRemote().sendText(json);
                System.out.println("Notification sent to user " + userId + ": " + notification.getTitle());
            } catch (IOException e) {
                System.err.println("Failed to send notification to user " + userId);
                e.printStackTrace();
            }
        } else {
            System.out.println("User " + userId + " is not connected via WebSocket");
        }
    }
    
    /**
     * Broadcast notification to all connected users
     */
    public static void broadcastNotification(NotificationDTO notification) {
        String json = gson.toJson(notification);
        sessions.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(json);
                } catch (IOException e) {
                    System.err.println("Failed to broadcast to user " + userId);
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Check if user is connected
     */
    public static boolean isUserConnected(String userId) {
        Session session = sessions.get(userId);
        return session != null && session.isOpen();
    }
    
    /**
     * Get number of connected users
     */
    public static int getConnectedUsersCount() {
        return (int) sessions.values().stream().filter(Session::isOpen).count();
    }
}
