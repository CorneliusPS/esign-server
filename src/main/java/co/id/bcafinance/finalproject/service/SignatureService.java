package co.id.bcafinance.finalproject.service;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/20/2024 14:10 PM
@Last Modified 5/20/2024 14:10 PM
Version 1.0
*/

import co.id.bcafinance.finalproject.core.Crypto;

import co.id.bcafinance.finalproject.core.security.JwtUtility;
import co.id.bcafinance.finalproject.dto.Signature.SignatureRequestDTO;
import co.id.bcafinance.finalproject.handler.ResponseHandler;
import co.id.bcafinance.finalproject.model.*;
import co.id.bcafinance.finalproject.repo.*;
import co.id.bcafinance.finalproject.util.ExecuteSMTP;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Transactional
@Service
public class SignatureService{

    @Autowired
    private SignatureRepo signatureRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private LogDocumentRepo logDocumentRepo;

    @Autowired
    private JwtUtility jwtUtility;
    @Autowired
    private ApproverRepo approverRepo;

    public ResponseEntity<Object> signDocument(Long idDocument, SignatureRequestDTO signatureRequestDTO, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        //get user by username
        Optional<User> user = userRepo.findByUsername(username);

        // check document apakah ada atau tidak
        Optional<Document> existDocument = documentRepo.findById(idDocument);

        if (existDocument.isEmpty()) {
            return new ResponseHandler().generateResponse("Document not found", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }

        // check document jika document.isSigned() == true
        Boolean isSigned = existDocument.get().getSigned();

        if (isSigned) {
            return new ResponseHandler().generateResponse("Dokumen ini telah ditandatangani", HttpStatus.BAD_REQUEST, null, "FV02003", request);
        }

        Optional<Approver> approver = approverRepo.findByDocumentAndUser(existDocument.get(), user.get());

        // check approver jika approver.isApproved() == true
        if (approver.isPresent() && approver.get().isApproved()) {
            return new ResponseHandler().generateResponse("Anda sudah menandatangani document ini", HttpStatus.BAD_REQUEST, null, "FV02004", request);
        }

        // tolong check jika approver.isCurrent() == false
        if (approver.isPresent() && !approver.get().isCurrent()) {
            return new ResponseHandler().generateResponse("Saat ini belum giliran anda", HttpStatus.FORBIDDEN, null, "FV02002", request);
        }

        // tolong check jika approver.isAuthenticated() == false
        if (approver.isPresent() && !approver.get().isAuthenticated()) {
            return new ResponseHandler().generateResponse("Anda belum melakukan verifikasi", HttpStatus.BAD_REQUEST, null, "FV02005", request);
        }

        Signature signature = new Signature();
        BeanUtils.copyProperties(signatureRequestDTO, signature);
        try {
            signature.setSignatureData(signatureRequestDTO.getSignatureData().getBytes());
        }catch (Exception e){
            return new ResponseHandler().generateResponse("Failed to save signature data", HttpStatus.BAD_REQUEST, null, "FS0001", null);
        }


        if (approver.isEmpty()) {
            return new ResponseHandler().generateResponse("Anda tidak memiliki akses untuk menandatangani document ini", HttpStatus.FORBIDDEN, null, "FV02002", request);
        }

        signature.setDocument(existDocument.get());
        signature.setUser(user.get());

        // lakukan pengecekan jika existDocument.get().getApprovalType() == "SERIAL"


        // count approver by document
        Long countApprover = approverRepo.findByDocument(existDocument.get()).stream().count();

        try {
            signatureRepo.save(signature);
            existDocument.get().setFileData(signatureRequestDTO.getSignatureData().getBytes());

            Integer flagCount = existDocument.get().getFlagCount();
            existDocument.get().setDocumentStatus("Sign By " + user.get().getUsername() + " (" + flagCount + " of " + countApprover + ")");
            existDocument.get().setFlagCount(flagCount + 1);
            approver.get().setApproved(true);
            approver.get().setSignedDate(new Date());
            approverRepo.save(approver.get());

            if (existDocument.get().getApprovalType() == Document.ApprovalType.SERIAL && approver.get().isApproved()) {
                if (approver.get().getApprovalOrder() == countApprover.intValue()) {
                    checkAllApprovers(existDocument.get());
                } else {
                    Optional<Approver> nextApprover = approverRepo.findByDocumentAndApprovalOrder(existDocument.get(), approver.get().getApprovalOrder() + 1);
                    nextApprover.get().setCurrent(true);
                    // send notification to next approver
                    Optional<User> nextUser = userRepo.findByIdUser(nextApprover.get().getUser().getIdUser());
                    // send notification to nextUser

                    String[] strVerify = new String[3];
                    strVerify[0] = "Document Approval Required";
                    strVerify[1] = nextUser.get().getFullName();
                    strVerify[2] = nextApprover.get().getDocument().getDocumentName();

                    Thread first = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new ExecuteSMTP().
                                    sendSMTPNotification(
                                            nextUser.get().getEmail(),// email tujuan
                                            "Document Approval Notification",// judul email
                                            strVerify,//
                                            "notif_approval.html");// \\data\\ver_regis
                            System.out.println("Email Terkirim");
                        }
                    });
                    first.start();

                    approverRepo.save(nextApprover.get());
                }
            } else {
                checkAllApprovers(existDocument.get());

            }
        } catch (Exception e) {
            return new ResponseHandler().generateResponse("Failed to save signature", HttpStatus.BAD_REQUEST, null, "FS0002", null);
        }

        saveDocumentHistory(existDocument.get(), user.get(), "Sign", "Document signed by " + user.get().getUsername());

        return new ResponseHandler().generateResponse("Signature saved", HttpStatus.OK, null, null, null);
    }

    private void checkAllApprovers(Document document) {
        boolean allApproved = approverRepo.findByDocument(document).stream()
                .allMatch(Approver::isApproved);

        if (allApproved) {
            document.setSigned(true);
            document.setDocumentStatus("All Done");
            documentRepo.save(document);
            // Notify all approvers or relevant parties
            // Add any other logic needed when all approvers have signed the document
        }
    }

    public ResponseEntity<Object> getOneSignature(Long idSignature, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> user = userRepo.findByUsername(username);

        Optional<Signature> signature = signatureRepo.findById(idSignature);

        if (signature.isEmpty()) {
            return new ResponseHandler().generateResponse("Signature not found", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }else if (!signature.get().getUser().equals(user.get())) {
            return new ResponseHandler().generateResponse("You are not authorized to access this signature", HttpStatus.UNAUTHORIZED, null, "FV02002", request);
        }

        return new ResponseHandler().generateResponse("Success", HttpStatus.OK, signature.get(), null, request);
    }

    private void saveDocumentHistory(Document document, User user, String action, String description) {
        LogDocument logDocument = new LogDocument();
        logDocument.setDocument(document);
        logDocument.setUser(user);
        logDocument.setTimestamp(new Date());
        logDocument.setAction(action);
        logDocument.setDescription(description);
        logDocumentRepo.save(logDocument);
    }
}
    

