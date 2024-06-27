package co.id.bcafinance.finalproject.dto.Signature;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/22/2024 13:35 PM
@Last Modified 5/22/2024 13:35 PM
Version 1.0
*/

import org.springframework.web.multipart.MultipartFile;

public class SignatureRequestDTO {

    public MultipartFile signatureData;

    public MultipartFile getSignatureData() {
        return signatureData;
    }

    public void setSignatureData(MultipartFile signatureData) {
        this.signatureData = signatureData;
    }
}
    

