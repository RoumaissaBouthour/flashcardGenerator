package com.example.flashcardgenerator.api;

import java.util.List;

public class AIRequest {
    private String model;
    private List<Message> messages;
    private float temperature;
    private int max_tokens;
    
    public static class Message {
        public String role;
        public String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    public AIRequest(String model, List<Message> messages, float temperature, int max_tokens) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.max_tokens = max_tokens;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public List<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    
    public float getTemperature() {
        return temperature;
    }
    
    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
    
    public int getMax_tokens() {
        return max_tokens;
    }
    
    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }
}
