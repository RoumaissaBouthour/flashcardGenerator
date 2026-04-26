package com.example.flashcardgenerator.api;

import java.util.List;

public class AIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    public static class Choice {
        public int index;
        public Message message;
        public String finish_reason;
        
        public static class Message {
            public String role;
            public String content;
            
            public String getContent() {
                return content;
            }
        }
        
        public Message getMessage() {
            return message;
        }
    }
    
    public static class Usage {
        public int prompt_tokens;
        public int completion_tokens;
        public int total_tokens;
    }
    
    public String getId() {
        return id;
    }
    
    public String getObject() {
        return object;
    }
    
    public long getCreated() {
        return created;
    }
    
    public String getModel() {
        return model;
    }
    
    public List<Choice> getChoices() {
        return choices;
    }
    
    public Usage getUsage() {
        return usage;
    }
}
