package co.id.bcafinance.finalproject.service;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 6/14/2024 14:53 PM
@Last Modified 6/14/2024 14:53 PM
Version 1.0
*/

import co.id.bcafinance.finalproject.core.Crypto;
import co.id.bcafinance.finalproject.core.security.JwtUtility;
import co.id.bcafinance.finalproject.dto.ApproverDTO;
import co.id.bcafinance.finalproject.dto.GetApproverDTO;
import co.id.bcafinance.finalproject.dto.SearchParamDTO;
import co.id.bcafinance.finalproject.handler.ResponseHandler;
import co.id.bcafinance.finalproject.model.Approver;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.repo.ApproverRepo;
import co.id.bcafinance.finalproject.repo.UserRepo;
import co.id.bcafinance.finalproject.util.ExecuteSMTP;
import co.id.bcafinance.finalproject.util.TransformToDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class ApproverService {

    @Autowired
    private ApproverRepo approverRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtility jwtUtility;@Autowired

    private ModelMapper modelMapper;

    Map<String,Object> mapResult = new HashMap<>();
    TransformToDTO transformToDTO = new TransformToDTO();
    private List<SearchParamDTO> listSearchParamDTO  = new ArrayList<>();



    public ResponseEntity<Object> getAllByUser(String authorizationHeader, HttpServletRequest request) {
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

        List<GetApproverDTO> approverDTOS = modelMapper.map(approvers, new TypeToken<List<GetApproverDTO>>() {}.getType());

        return new ResponseHandler().generateResponse("Berhasil mendapatkan data", HttpStatus.OK, approverDTOS, null, request);
    }

    public ResponseEntity<Object>   getOneApprover(Long idApprover, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        // lakukan check apakah user dapat mengakses data approver ini

        Optional<User> user = userRepo.findByUsername(username);

        Optional<Approver> approver = approverRepo.findByIdApproverAndUser(idApprover, user.get());

        // jika tidak ada approver dengan id tersebut maka return unauthorized
        if (approver.isEmpty()) {
            return new ResponseHandler().generateResponse("Anda tidak memiliki akses untuk data ini", HttpStatus.UNAUTHORIZED, null, "FV02004", request);
        }

        Optional<GetApproverDTO> approverDTOS = Optional.of(modelMapper.map(approver.get(), new TypeToken<GetApproverDTO>() {}.getType()));

        return new ResponseHandler().generateResponse("Berhasil mendapatkan data", HttpStatus.OK, approverDTOS.get(), null, request);

    }

    public ResponseEntity<Object> sendOtp(Long idApprover, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        // lakukan check apakah user dapat mengakses data approver ini

        Optional<User> user = userRepo.findByUsername(username);

        Optional<Approver> approver = approverRepo.findByIdApproverAndUser(idApprover, user.get());

        // jika tidak ada approver dengan id tersebut maka return unauthorized
        if (approver.isEmpty()) {
            return new ResponseHandler().generateResponse("Anda tidak memiliki akses untuk data ini", HttpStatus.UNAUTHORIZED, null, "FV02004", request);
        }

        // kirim otp ke email user
        int intVerification = new Random().nextInt(100000, 999999);//TOKEN YANG AKAN DIKIRIM KE EMAIL USER
        approver.get().setOtp(intVerification);

        approverRepo.save(approver.get());


        String[] strVerify = new String[3];
        strVerify[0] = "Verifikasi OTP";
        strVerify[1] = user.get().getFullName();
        strVerify[2] = String.valueOf(intVerification);

        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                new ExecuteSMTP().
                        sendSMTPToken(
                                user.get().getEmail(),// email tujuan
                                "OTP Verifikasi ",// judul email
                                strVerify,//
                                "ver_regis.html");// \\data\\ver_regis
                System.out.println("Email Terkirim");
            }
        });
        first.start();

        return new ResponseHandler().generateResponse("Berhasil mengirimkan OTP", HttpStatus.OK, null, null, request);



    }


    public ResponseEntity<Object> verifyOtp(Approver approver, Long idApprover, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        // lakukan check apakah user dapat mengakses data approver ini

        Optional<User> user = userRepo.findByUsername(username);

        Optional<Approver> approverData = approverRepo.findByIdApproverAndUser(idApprover, user.get());

        // jika tidak ada approver dengan id tersebut maka return unauthorized
        if (approverData.isEmpty()) {
            return new ResponseHandler().generateResponse("Anda tidak memiliki akses untuk data ini", HttpStatus.UNAUTHORIZED, null, "FV02004", request);
        }

        if (!approverData.get().getOtp().equals(approver.getOtp())) {
            return new ResponseHandler().generateResponse("OTP yang anda masukkan salah, silahkan dicoba kembali!!", HttpStatus.NOT_ACCEPTABLE, null, "FV02005", request);
        }

        approverData.get().setOtp(0);
        approverData.get().setAuthenticated(true);

        approverRepo.save(approverData.get());

        return new ResponseHandler().generateResponse("Berhasil verifikasi OTP", HttpStatus.OK, null, null, request);
    }
}
    

