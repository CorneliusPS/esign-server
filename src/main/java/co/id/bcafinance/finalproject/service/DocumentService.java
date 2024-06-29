package co.id.bcafinance.finalproject.service;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/14/2024 11:20 AM
@Last Modified 5/14/2024 11:20 AM
Version 1.0
*/

import co.id.bcafinance.finalproject.core.Crypto;
import co.id.bcafinance.finalproject.core.security.JwtUtility;
import co.id.bcafinance.finalproject.dto.ApproverDTO;
import co.id.bcafinance.finalproject.dto.Document.GetDocumentPaginationDTO;
import co.id.bcafinance.finalproject.dto.DocumentDTO;
import co.id.bcafinance.finalproject.dto.SearchParamDTO;
import co.id.bcafinance.finalproject.handler.ResponseHandler;
import co.id.bcafinance.finalproject.model.Approver;
import co.id.bcafinance.finalproject.model.Document;
import co.id.bcafinance.finalproject.model.Signature;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.repo.ApproverRepo;
import co.id.bcafinance.finalproject.repo.DocumentRepo;
import co.id.bcafinance.finalproject.repo.SignatureRepo;
import co.id.bcafinance.finalproject.repo.UserRepo;
import co.id.bcafinance.finalproject.util.TransformToDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ApproverRepo approverRepo;

    @Autowired
    private SignatureRepo signatureRepo;

    @Autowired
    private JwtUtility jwtUtility;


    @Autowired
    private ModelMapper modelMapper;

    Map<String,Object> mapResult = new HashMap<>();
    TransformToDTO transformToDTO = new TransformToDTO();
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();



    public ResponseEntity<Object> uploadDocument(DocumentDTO documentDTO, String authorizationHeader, HttpServletRequest request) {

        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> user = userRepo.findByUsername(username);

        Optional<Document> existDocument = documentRepo.findByIdDocument(documentDTO.getIdDocument());

        if (documentDTO.getFileName() == null || documentDTO.getFileData() == null) {
            return new ResponseHandler().generateResponse("Data Tidak Valid", HttpStatus.BAD_REQUEST, null, "FV02002", request);
        }
        else if (existDocument.isPresent()) {
            return new ResponseHandler().generateResponse("Document sudah ada", HttpStatus.CONFLICT, null, "FV02003", request);
        }
        Document document = new Document();
        BeanUtils.copyProperties(documentDTO, document);

        try {
            document.setFileData(documentDTO.getFileData().getBytes());
        } catch (Exception e) {
            return new ResponseHandler().generateResponse("Data Tidak Valid", HttpStatus.BAD_REQUEST, null, "FV02004", request);
        }

        try {
            document.setUploadBy(user.get());
            document.setDocumentStatus(Document.DocumentStatus.PENDING);
            document.setSigned(false);
            documentRepo.save(document);
        } catch (Exception e) {
            return new ResponseHandler().generateResponse("Terjadi kesalahan saat menyimpan document", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE03001", request);
        }
        return new ResponseHandler().generateResponse("Document Berhasil disimpan", HttpStatus.OK, document, null, request);
    }

    public ResponseEntity<Object> downloadDocument(Long idDocument, HttpServletRequest request) {
        Optional<Document> document = documentRepo.findByIdDocument(idDocument);
        if (!document.isPresent()) {
            return new ResponseHandler().generateResponse("Document tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }
        if (document == null) {
            return new ResponseHandler().generateResponse("Document not found", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }

        ByteArrayResource resource = new ByteArrayResource(document.get().getFileData());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.get().getFileName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return new ResponseHandler().generateResponse("OK", HttpStatus.OK, resource, null, request);
    }


    public ResponseEntity<Object> deleteDocument(Long idDocument, HttpServletRequest request) {
        Optional<Document> document = documentRepo.findByIdDocument(idDocument);
        if (!document.isPresent()) {
            return new ResponseHandler().generateResponse("Document tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }
        try {
            documentRepo.delete(document.get());
        } catch (Exception e) {
            return new ResponseHandler().generateResponse("Terjadi kesalahan saat menghapus document", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE03001", request);
        }
        return new ResponseHandler().generateResponse("Document Berhasil dihapus", HttpStatus.OK, null, null, request);
    }

    public ResponseEntity<Object> getAllDocumentPagination(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) {
        Page<Document> documentPage =null;
        List<Document> documentList = null;


        if (columFirst.equals("idUser")) {
            if (valueFirst.equals("") && valueFirst != null) {
                try {
                    Long.parseLong(valueFirst);
                } catch (Exception e) {
                    return new ResponseHandler().
                            generateResponse("Data Filter tidak sesuai harus Angka!",
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    null,
                                    null,
                                    request);
                }

            }
        }

        documentPage = getDataByValue(pageable, columFirst, valueFirst);
        documentList = documentPage.getContent();
        if (documentList.isEmpty()) {
            return new ResponseHandler().
                    generateResponse("DATA TIDAK DITEMUKAN",
                            HttpStatus.NOT_FOUND,
                            null,
                            "X-99-002",
                            request);
        }

        List<GetDocumentPaginationDTO> ltDocumentDTO =
                modelMapper.map(documentList, new TypeToken<List<GetDocumentPaginationDTO>>() {
                }.getType());
        mapResult = transformToDTO.transformObject(mapResult,
                ltDocumentDTO,
                documentPage,
                columFirst,
                valueFirst,
                listSearchParamDTO);

        return new ResponseHandler().
                generateResponse("OK",
                        HttpStatus.OK,
                        mapResult,
                        null,
                        request);
    }

    private Page<Document> getDataByValue(Pageable pageable, String columnFirst, String valueFirst) {

        if (columnFirst.equals("idDocument")) {
            return documentRepo.findByIdDocumentContainingIgnoreCase(pageable, Long.parseLong(valueFirst));
        } else if (columnFirst.equals("fileName")) {
            return documentRepo.findByFileNameContainingIgnoreCase(pageable, valueFirst);
        } else if (columnFirst.equals("FileType")) {
            return documentRepo.findByFileTypeContainingIgnoreCase(pageable, valueFirst);
        }

        return documentRepo.findAll(pageable);
    }

    public Document getDocument(Long idDocument, HttpServletRequest request) {
        Optional<Document> document = documentRepo.findByIdDocument(idDocument);
        if (!document.isPresent()) {
            return null;
        }
        return document.get();
    }

//    public ResponseEntity<Object> assignApprovers(Long documentId, List<User> approverIds, HttpServletRequest request) {
//        Optional<Document> document = documentRepo.findByIdDocument(documentId);
//        if (!document.isPresent()) {  `````````````````````````````
//            return new ResponseHandler().generateResponse("Document tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
//        }
//
//        // save batch approvers
//        List<Approver> approverList = new ArrayList<>();
//        for (User approver : approverIds) {
//            Approver newApprover = new Approver();
//            newApprover.setDocument(document.get());
//            newApprover.setUser(approver);
//            newApprover.setApproved(false);
//            approverList.add(newApprover);
//        }
//        approverRepo.saveAll(approverList);
//
//            return new ResponseHandler().generateResponse("Approver berhasil ditugaskan", HttpStatus.OK, document.get(), null, request);
//    }

    public ResponseEntity<Object> assignApprovers(Long documentId, List<ApproverDTO> approverDtos, HttpServletRequest request) {
        Optional<Document> document = documentRepo.findByIdDocument(documentId);
        if (!document.isPresent()) {
            return new ResponseHandler().generateResponse("Document tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }
        // jika documentId sudah ada di dalam table approver
        List<Approver> approverExist = approverRepo.findByDocument(document.get());
        if (!approverExist.isEmpty()) {
            return new ResponseHandler().generateResponse("Approver sudah ditugaskan", HttpStatus.CONFLICT, null, "FV02003", request);
        }

        List<Long> userIds = new ArrayList<>();
        for (ApproverDTO dto : approverDtos) {
            userIds.add(dto.getIdUser());
        }

        List<User> approvers = userRepo.findAllById(userIds);

        List<Approver> approverList = new ArrayList<>();
        for (User approver : approvers) {
            Approver newApprover = new Approver();
            newApprover.setDocument(document.get());
            newApprover.setUser(approver);
            newApprover.setApproved(false);
            newApprover.setAuthenticated(false);
            approverList.add(newApprover);
        }
        approverRepo.saveAll(approverList);

        return new ResponseHandler().generateResponse("Approver berhasil ditugaskan", HttpStatus.OK, null, null, request);
    }


    public ResponseEntity<Object> signDocument(Long idDocument, MultipartFile sign, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> user = userRepo.findByUsername(username);

        Optional<Document> document = documentRepo.findByIdDocument(idDocument);
        if (!document.isPresent()) {
            return new ResponseHandler().generateResponse("Document tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }


        Signature signature = new Signature();
        signature.setDocument(document.get());
        signature.setUser(user.get());
        try {
            signature.setSignatureData(sign.getBytes());
        } catch (Exception e) {
            return new ResponseHandler().generateResponse("Data Tidak Valid", HttpStatus.BAD_REQUEST, null, "FV02004", request);
        }
        signatureRepo.save(signature);

//        approver.get(0).setApproved(true);
//        approverRepo.save(approver.get(0));

        checkAllApprovers(document.get());

        return new ResponseHandler().generateResponse("Document berhasil ditandatangani", HttpStatus.OK, document.get(), null, request);

    }

    // checkAllApproversSigned

    private void checkAllApprovers(Document document) {
            boolean allApproved = approverRepo.findByDocument(document).stream()
                    .allMatch(Approver::isApproved);

            if (allApproved) {
                document.setSigned(true);
                documentRepo.save(document);
                // Notify all approvers or relevant parties
                // Add any other logic needed when all approvers have signed the document
        }
    }

    public ResponseEntity<Object> getAllDocumentsByApprover(String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> approver = userRepo.findByUsername(username);
        if (!approver.isPresent()) {
            return new ResponseHandler().generateResponse("Approver tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }

        List<Approver> approvers = approverRepo.findByUser(approver.get());
        if (approvers.isEmpty()) {
            return new ResponseHandler().generateResponse("Tidak ada dokumen untuk approver ini", HttpStatus.NOT_FOUND, null, "FV02002", request);
        }

        List<Document> documents = new ArrayList<>();
        for (Approver a : approvers) {
            documents.add(a.getDocument());
        }

        List<GetDocumentPaginationDTO> documentDTOs = modelMapper.map(documents, new TypeToken<List<GetDocumentPaginationDTO>>() {}.getType());

        return new ResponseHandler().generateResponse("OK", HttpStatus.OK, documentDTOs, null, request);
    }

    public ResponseEntity<Object> getAllDocumentsByUploaderPagination(Pageable pageable, String uploaderId, HttpServletRequest request) {
        Page<Document> documentPage;
        List<Document> documentList;

        if (uploaderId.equals("")) {
            return new ResponseHandler()
                    .generateResponse("Data Filter tidak sesuai harus Angka!", HttpStatus.INTERNAL_SERVER_ERROR, null, null, request);
        } else {
            try {
                Long.parseLong(uploaderId);
            } catch (Exception e) {
                return new ResponseHandler()
                        .generateResponse("Data Filter tidak sesuai harus Angka!", HttpStatus.INTERNAL_SERVER_ERROR, null, null, request);
            }
        }

        documentPage = documentRepo.findByUploadBy_IdUser(Long.parseLong(uploaderId), pageable);
        documentList = documentPage.getContent();

        if (documentList.isEmpty()) {
            return new ResponseHandler()
                    .generateResponse("DATA TIDAK DITEMUKAN", HttpStatus.NOT_FOUND, null, "X-99-002", request);
        }

        List<GetDocumentPaginationDTO> ltDocumentDTO = modelMapper.map(documentList, new TypeToken<List<GetDocumentPaginationDTO>>() {}.getType());
        mapResult = transformToDTO.transformObject(mapResult, ltDocumentDTO, documentPage, "uploadBy", uploaderId, listSearchParamDTO);

        return new ResponseHandler().generateResponse("OK", HttpStatus.OK, mapResult, null, request);
    }

    public ResponseEntity<Object> getAllDocumentsByUploader(String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> uploader = userRepo.findByUsername(username);
        if (!uploader.isPresent()) {
            return new ResponseHandler().generateResponse("Uploader tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }

        List<Document> documents = documentRepo.findByUploadBy(uploader.get());
        if (documents.isEmpty()) {
            return new ResponseHandler().generateResponse("Tidak ada dokumen untuk uploader ini", HttpStatus.NOT_FOUND, null, "FV02002", request);
        }

        List<GetDocumentPaginationDTO> documentDTOs = modelMapper.map(documents, new TypeToken<List<GetDocumentPaginationDTO>>() {}.getType());

        return new ResponseHandler().generateResponse("OK", HttpStatus.OK, documentDTOs, null, request);
    }


    public ResponseEntity<Object> getOneDocument(Long idDocument, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> approver = userRepo.findByUsername(username);
        if (!approver.isPresent()) {
            return new ResponseHandler().generateResponse("Approver tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }

        Optional<Document> document = documentRepo.findByIdDocument(idDocument);
        if (!document.isPresent()) {
            return new ResponseHandler().generateResponse("Document tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);
        }

        return new ResponseHandler().generateResponse("Document ditemukan", HttpStatus.OK, document.get(), null, request);
    }
}
    

