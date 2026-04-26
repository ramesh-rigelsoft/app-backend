package com.rigel.app.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TaskException {
	
   
	
//          @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
            @ExceptionHandler(BadGatewayRequest.class)
		    public ResponseEntity<Map<String,Object>> addTaskException(BadGatewayRequest badGatewayRequest) {
            	badGatewayRequest.printStackTrace();
            	HashMap<String, Object> response=new  HashMap<>();		        
		    	response.put("data", "");
				response.put("status", "BAD_GATEWAY");
				response.put("code", "400");
				response.put("message",badGatewayRequest.getMessage());
		        return new ResponseEntity<>(response,HttpStatus.BAD_GATEWAY);
		    }
            
            @ExceptionHandler(TaskTitleException.class)
		    public ResponseEntity<Map<String,Object>> taskTitleException(TaskTitleException taskTitleException) {
            	taskTitleException.printStackTrace();
            	HashMap<String, Object> response=new  HashMap<>();		        
		    	response.put("data", "");
				response.put("status", "BAD_GATEWAY");
				response.put("code", "400");
				response.put("message",taskTitleException.getMessage());
		        return new ResponseEntity<>(response,HttpStatus.BAD_GATEWAY);
		    }
            
//            @ExceptionHandler(Exception.class)
//		    public ResponseEntity<Map<String,Object>> exception(Exception taskTitleException) {
//		        HashMap<String, Object> response=new  HashMap<>();		        
//		    	response.put("data", "");
//				response.put("status", "INTERNAL_SERVER_ERROR");
//				response.put("code", "500");
//				response.put("message", "Something went wrong");
//		        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//		    }
            
            @ExceptionHandler(TaskTitleNotFound.class)
		    public ResponseEntity<Map<String,Object>> taskTitleNotFound(TaskTitleNotFound taskTitleException) {
            	taskTitleException.printStackTrace();
            	HashMap<String, Object> response=new  HashMap<>();		        
		    	response.put("data", "");
				response.put("status", "NOT_FOUND");
				response.put("code", "404");
				response.put("message",taskTitleException.getMessage());
		        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
		    }
            
            @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
		    public ResponseEntity<Map<String,Object>> nethodNotAllow(HttpRequestMethodNotSupportedException taskTitleException) {
            	taskTitleException.printStackTrace();
            	HashMap<String, Object> response=new  HashMap<>();		        
		    	response.put("data", "");
				response.put("status", "METHOD_NOT_ALLOW");
				response.put("code", "405");
				response.put("message",taskTitleException.getMessage());
		        return new ResponseEntity<>(response,HttpStatus.METHOD_NOT_ALLOWED);
		    }
            
            @ExceptionHandler(Exception.class)
		    public ResponseEntity<Map<String,Object>> taskTitleNotFound(Exception exception) {
            	exception.printStackTrace();
		        Map<String, Object> response=new  HashMap<>();		        
		    	response.put("data", "");
				response.put("status", "INTERNAL_SERVER_ERROR");
				response.put("code", "500");
				response.put("message",exception.getMessage());
//				exception.printStackTrace();
		        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		    }
            
            @ExceptionHandler(MethodArgumentNotValidException.class)
            public ResponseEntity<Map<String,Object>> methodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
            	methodArgumentNotValidException.printStackTrace();
            	HashMap<String, Object> response=new  HashMap<>();		        
            	response.put("data", "");
        		response.put("status", "NOT_ACCEPTABLE");
        		response.put("code", "202");
        		response.put("message",methodArgumentNotValidException.getMessage());
                return new ResponseEntity<>(response,HttpStatus.NOT_ACCEPTABLE);
            }        
}
