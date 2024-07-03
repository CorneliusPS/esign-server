package co.id.bcafinance.finalproject.controller;

import co.id.bcafinance.finalproject.dto.AssignApproversRequestDTO;
import co.id.bcafinance.finalproject.dto.DocumentDTO;
import co.id.bcafinance.finalproject.model.Document;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.repo.UserRepo;
import co.id.bcafinance.finalproject.service.DocumentService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/14/2024 11:19 AM
@Last Modified 5/14/2024 11:19 AM
Version 1.0
*/
@RestController
@RequestMapping("/api/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private ModelMapper modelMapper;

    private Map<String,String> mapSorting = new HashMap<String,String>();
    @Autowired
    private UserRepo userRepo;  

    public DocumentController() {
        mapSorting();
    }

    private void mapSorting()
    {
        mapSorting.put("idDocument","idDocument");
        mapSorting.put("fileName","fileName");
        mapSorting.put("fileType","FileType");
    }

    // upload document to signature
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadDocument(@Valid @RequestBody MultipartFile file,
                                                 @RequestParam String documentName,
                                                 @RequestParam String  approvalType,
                                                 @RequestHeader("Authorization") String authorizationHeader,
                                                 HttpServletRequest request) {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setDocumentName(documentName);
        documentDTO.setFileName(file.getOriginalFilename());
        documentDTO.setFileType(file.getContentType());
        documentDTO.setFileData(file);
        documentDTO.setApprovalType(approvalType);

        return documentService.uploadDocument(documentDTO, authorizationHeader, request);
    }

    @PostMapping("/{documentId}/assign-approvers")
    public ResponseEntity<Object> assignApprovers(@PathVariable Long documentId, @RequestBody AssignApproversRequestDTO approversRequestDTO, HttpServletRequest request) {
        return documentService.assignApprovers(documentId, approversRequestDTO.getApproverIds(), request);
    }

    // sign document with signature
    @PostMapping("/sign/{idDocument}")
    public ResponseEntity<Object> signDocument(@PathVariable Long idDocument, @RequestBody MultipartFile sign, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return documentService.signDocument(idDocument, sign, authorizationHeader, request);
    }


    // download document
    @GetMapping("/download/{idDocument}")
    public ResponseEntity<Object> downloadDocument(@PathVariable Long idDocument, HttpServletRequest request) {
        return documentService.downloadDocument(idDocument, request);
    }

    // delete document
    @DeleteMapping("/delete/{idDocument}")
    public ResponseEntity<Object> deleteDocument(@PathVariable Long idDocument, HttpServletRequest request) {
        return documentService.deleteDocument(idDocument, request);
    }

    // Get Log Document by Document ID
    @GetMapping("/log/{idDocument}")
    public ResponseEntity<Object> getLogDocument(@PathVariable Long idDocument, HttpServletRequest request) {
        return documentService.getLogDocument(idDocument, request);
    }

    @GetMapping("/get-all/{page}/{sort}/{sortBy}")
    public ResponseEntity<Object> getAllDocumentPagination(@PathVariable(value = "page") Integer page,
                                                           @PathVariable(value = "sort") String sort,
                                                           @PathVariable(value = "sortBy") String sortBy,
                                                           @RequestParam("filterBy") String filterBy,
                                                           @RequestParam("value") String value,
                                                           @RequestParam("size") String size,
                                                           HttpServletRequest request){
        Pageable pageable = null;
        page = page==null?0:page-1;
        sortBy = (sortBy==null || sortBy.equals(""))?"idDocument":sortBy;
        sort   = (sort==null || sort.equals("") || sort.equals("asc"))?"asc":"desc";

        sortBy = mapSorting.get(sortBy);
        pageable = PageRequest.of(page,Integer.parseInt(size.equals("")?"3":size),
                sort.equals("desc")? Sort.by(sortBy).descending():Sort.by(sortBy));


        return documentService.getAllDocumentPagination(pageable, filterBy, value, request);
    }

    //Get All Document by Uploader without Pagination

    @GetMapping("/get-all-by-uploader")
    public ResponseEntity<Object> getAllDocumentsByUploader(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return documentService.getAllDocumentsByUploader(authorizationHeader,request);
    }


    @GetMapping("/get-all-by-approver")
    public ResponseEntity<Object> getAllDocumentsByApprover(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return documentService.getAllDocumentsByApprover(authorizationHeader, request);
    }


    @GetMapping("/uploader/{page}/{sort}/{sortBy}")
    public ResponseEntity<Object> getAllDocumentsByUploaderPagination(
            @PathVariable(value = "page") Integer page,
            @PathVariable(value = "sort") String sort,
            @PathVariable(value = "sortBy") String sortBy,
            @RequestParam("uploaderId") String uploaderId,
            @RequestParam("size") String size,
            HttpServletRequest request) {

        Pageable pageable = null;
        page = page == null ? 0 : page - 1;
        sortBy = (sortBy == null || sortBy.equals("")) ? "idDocument" : sortBy;
        sort = (sort == null || sort.equals("") || sort.equals("asc")) ? "asc" : "desc";

        pageable = PageRequest.of(page, Integer.parseInt(size.equals("") ? "3" : size),
                sort.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy));

        return documentService.getAllDocumentsByUploaderPagination(pageable, uploaderId, request);
    }

    // get one document by id
    @GetMapping("/{idDocument}")
    public ResponseEntity<Object> getOneDocument(@PathVariable Long idDocument, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return documentService.getOneDocument(idDocument, authorizationHeader, request);
    }
}
    

