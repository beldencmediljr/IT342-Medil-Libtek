package edu.cit.medil.libtek.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public class ApiResponseFactory {
    
    // Factory Method for Standardized Success Responses
    public static ResponseEntity<Map<String, Object>> success(Object data, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("error", null);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(response);
    }

    // Factory Method for Standardized Error Responses
    public static ResponseEntity<Map<String, Object>> error(String code, String message, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("data", null);
        
        Map<String, String> errorData = new HashMap<>();
        errorData.put("code", code);
        errorData.put("message", message);
        
        response.put("error", errorData);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(response);
    }
}