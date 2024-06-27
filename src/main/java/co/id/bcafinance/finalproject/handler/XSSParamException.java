package co.id.bcafinance.finalproject.handler;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 4/23/2024 11:50 AM
@Last Modified 4/23/2024 11:50 AM
Version 1.0
*/

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class XSSParamException extends RuntimeException{
    public XSSParamException() {
    }

    public XSSParamException(String message) {
        super(message);
    }
}
    

