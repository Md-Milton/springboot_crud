package com.example.milton.exception;


import com.example.milton.domain.UsersDto;

public class CustomErrorType extends UsersDto {

	private String errorMessage;
	 
    public CustomErrorType(final String errorMessage){
        this.errorMessage = errorMessage;
    }
 
    public String getErrorMessage() {
        return errorMessage;
    }
}
