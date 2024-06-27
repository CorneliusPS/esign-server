package co.id.bcafinance.finalproject.controller;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 6/14/2024 14:52 PM
@Last Modified 6/14/2024 14:52 PM
Version 1.0
*/


import co.id.bcafinance.finalproject.dto.auth.OtpDto;
import co.id.bcafinance.finalproject.model.Approver;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.service.ApproverService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/approval")
public class ApproverController {
    @Autowired
    private ApproverService approverService;

    @Autowired
    private ModelMapper modelMapper;


    // get all approver by user id
    @GetMapping("/get-all-by-user")
    public ResponseEntity<Object> getOneDocument(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return approverService.getAllByUser(authorizationHeader, request);
    }

    // Get One Approver
    @GetMapping("/get-one/{idApprover}")
    public ResponseEntity<Object> getOneApprover(@PathVariable Long idApprover, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return approverService.getOneApprover(idApprover, authorizationHeader, request);
    }

    // send otp
    @PostMapping ("/send-otp")
    public ResponseEntity<Object> sendOtp(@RequestParam Long idApprover, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return approverService.sendOtp(idApprover, authorizationHeader, request);
    }

    @PostMapping("/verif-otp")
    public ResponseEntity<Object> otp(@Valid @RequestBody OtpDto otpDto, @RequestParam Long idApprover, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        Approver approver = modelMapper.map(otpDto, new TypeToken<Approver>() {}.getType());

        return approverService.verifyOtp(approver, idApprover, authorizationHeader, request);
    }

    }
    

