package co.id.bcafinance.finalproject.controller;

import co.id.bcafinance.finalproject.dto.Signature.SignatureRequestDTO;
import co.id.bcafinance.finalproject.service.SignatureService;
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
@RequestMapping("/api/signature")
public class SignatureController {

    @Autowired
    private SignatureService signatureService;

    // save sign-document
    @PostMapping("/{idDocument}/sign-document")
    public ResponseEntity<Object> signDocument(@Valid @ModelAttribute @RequestBody SignatureRequestDTO signatureRequestDTO, @PathVariable Long idDocument, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return signatureService.signDocument(idDocument, signatureRequestDTO, authorizationHeader,request);
    }

    // get one signature
    @GetMapping("/get-one/{idSignature}")
    public ResponseEntity<Object> getOneSignature(@PathVariable Long idSignature, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return signatureService.getOneSignature(idSignature, authorizationHeader, request);
    }


}
    

