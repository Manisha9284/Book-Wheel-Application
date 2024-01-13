package com.bookwheelapp.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bookwheelapp.payload.ApiResponse;



@RestControllerAdvice
public class GlobalExceptionHandler {
//
//	******************************************
//	Handler for Resource not found exception
//	******************************************
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		
		return new ResponseEntity<ApiResponse>(apiResponse , HttpStatus.NOT_FOUND);
	}
	
	
//	******************************************
//	Handler for method argument not valid exception
//	******************************************
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex)
	{
		//map for result of respected fieldname and corresponding message e.g. fieldname-> name
		Map<String, String> resp = new HashMap<>();
		
		
		//for above purpose using ex get all binded results to field then getAllErrors after that
		// single fieldname and corresponding message too using forEach iterator method 
		  ex.getBindingResult().getAllErrors().forEach((error)->{
			String fieldName = ((FieldError)error).getField();//get filed name
			String message =  error.getDefaultMessage();// get proper error message
			
			
			resp.put(fieldName, message);//put received fieldname and message in hashmap using put method in key-value pair
		});
		
		return new ResponseEntity<Map<String, String>>(resp , HttpStatus.BAD_REQUEST);
	}
	
	
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse> apiExceptionHandler(ApiException ex){
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, true);
		
		return new ResponseEntity<ApiResponse>(apiResponse , HttpStatus.BAD_REQUEST);
	}
}
