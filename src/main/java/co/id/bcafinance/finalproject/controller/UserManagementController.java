package co.id.bcafinance.finalproject.controller;

import co.id.bcafinance.finalproject.dto.Signature.SignatureRequestDTO;
import co.id.bcafinance.finalproject.service.SignatureService;
import co.id.bcafinance.finalproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/20/2024 10:17 AM
@Last Modified 5/20/2024 10:17 AM
Version 1.0
*/
@RestController
@RequestMapping("/api/user-mgmnt")
public class UserManagementController {

    @Autowired
    private UserService userService;


    // Get All User
    @GetMapping("/get-all")
    public ResponseEntity<Object> getAllUser(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return userService.getAllUser(authorizationHeader,request);
    }

    // Get All User to use in dropdown list in frontend to assign approver, without admin and user itself

    @GetMapping("/get-all-for-assign-approver")
    public ResponseEntity<Object> getAllUserForAssignApprover(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return userService.getAllUserForAssignApprover(authorizationHeader,request);
    }



}
    

