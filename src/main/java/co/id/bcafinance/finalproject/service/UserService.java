package co.id.bcafinance.finalproject.service;

import co.id.bcafinance.finalproject.configuration.OtherConfig;
import co.id.bcafinance.finalproject.core.BcryptImpl;
import co.id.bcafinance.finalproject.core.Crypto;
import co.id.bcafinance.finalproject.core.IService;
import co.id.bcafinance.finalproject.core.security.JwtUtility;
import co.id.bcafinance.finalproject.dto.auth.UserDTO;
import co.id.bcafinance.finalproject.handler.RequestCapture;
import co.id.bcafinance.finalproject.handler.ResponseHandler;
import co.id.bcafinance.finalproject.model.Akses;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.repo.AksesRepo;
import co.id.bcafinance.finalproject.repo.UserRepo;
import co.id.bcafinance.finalproject.util.ExecuteSMTP;
import co.id.bcafinance.finalproject.util.LoggingFile;
import co.id.bcafinance.finalproject.util.TransformToDTO;
import io.jsonwebtoken.ExpiredJwtException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Company Code - Not Necessery
 *  Modul Code 04
 *  Type of Error -> Validation = FV , Engine Error = FE
 *  ex : FE01001 (Error di Modul GroupMenu Functional Save)
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    private Map<String,Object> mapz = new HashMap<>();
    private StringBuilder sBuild = new StringBuilder();

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AksesRepo aksesRepo;

    @Autowired
    private JwtUtility jwtUtility;

    private String[] strExceptionArr = new String[2];

    @Autowired
    private ModelMapper modelMapper;

    Map<String, Object> mapResult = new HashMap<>();

    TransformToDTO transformToDTO = new TransformToDTO();

    /*flow untuk registrasi STEP 1*/
    public ResponseEntity<Object> checkRegis(User user, HttpServletRequest request) {//RANGE RGS 001-010

        if (user == null) {
            return new ResponseHandler().generateResponse(
                    "Data tidak Valid",//message
                    HttpStatus.BAD_REQUEST,//httpstatus
                    null,//object
                    "FVRGS001",//errorCode Fail Validation modul-code RGS sequence 001 range 001 - 010
                    request
            );
        }

        int intVerification = new Random().nextInt(100000, 999999);//TOKEN YANG AKAN DIKIRIM KE EMAIL
        Optional<User> opUserResult = userRepo.findTop1ByUsernameOrEmail(user.getUsername(), user.getEmail());//INI VALIDASI USER IS EXISTS
        Optional<Akses> getOneAkses = aksesRepo.findByNamaAkses("customer");

        if (getOneAkses.isEmpty()) {
            user.setAkses(null);
        }

        try {
            //kondisi mengecek apakah user sudah terdaftar artinya user baru atau sudah ada
            if (!opUserResult.isEmpty()) {
                User nextUser = opUserResult.get();


                    //NOTIFIKASI SAAT REGISTRASI BAGIAN MANA YANG SUDAH TERDAFTAR (USERNAME, EMAIL ATAU NOHP)
                    //kasus nya bisa saja user ingin memiliki 2 akun , namun dari sistem tidak memperbolehkan untuk duplikasi username,email atau no hp
                    //jika user ingin memiliki 2 akun , maka dia harus menggunakan username,email dan nohp yang berbeda dan belum terdaftar di sistem
                    /*  
                        ex : username : paul, noHP : 628888888, email:paul@gmail.com lalu ingin mendaftar lagi dengan format
                        username : paul123, noHP : 6283333333, email:paul@gmail.com ,di kasus ini user harus menggunakan email lain walau username dan noHp sudah yang baru
                     */
                    if (nextUser.getUsername().equals(user.getUsername())) {
                        return new ResponseHandler().generateResponse("USERNAME SUDAH TERDAFTAR", HttpStatus.NOT_ACCEPTABLE, null, "FVRGS004", request);//USERNAME SUDAH TERDAFTAR DAN AKTIF
                    } else if (nextUser.getEmail().equals(user.getEmail())) {
                        return new ResponseHandler().generateResponse("EMAIL SUDAH TERDAFTAR !!", HttpStatus.NOT_ACCEPTABLE, null, "FVRGS002", request);//EMAIL SUDAH TERDAFTAR DAN AKTIF
                    } else {
                        /*
                            seharusnya tidak akan pernah masuk kesini karena dari query hanya 3 saja autentikasi nya yaitu :
                            username , email dan no HP
                         */
                        return new ResponseHandler().generateResponse("SEMUA BISA TERJADI BRO !!", HttpStatus.NOT_ACCEPTABLE, null, "FVRGS005", request);//KARENA YANG DIAMBIL DATA YANG PERTAMA JADI ANGGAPAN NYA SUDAH TERDAFTAR SAJA
                    }
            } else {//user belum terdaftar sama sekali artinya user benar-benar baru menndaftar
                user.setPassword(BcryptImpl.hash(user.getPassword() + user.getUsername()));
                user.setAkses(getOneAkses.get());
                userRepo.save(user);
            }

        } catch (Exception e) {
            strExceptionArr[1] = "checkRegis(User user, HttpServletRequest request)  --- LINE 178 \n ALL - REQUEST"+ RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

            return new ResponseHandler().generateResponse("GAGAL DIPROSES", HttpStatus.INTERNAL_SERVER_ERROR, null, "FERGS001", request);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token", authManager(user, request));
        return new ResponseHandler().generateResponse(
                "Registrasi Berhasil",
                HttpStatus.CREATED,
                map,
                null,
                request
        );
    }

    public ResponseEntity<Object> doLogin(User userz, HttpServletRequest request) {
        /**
         *  KITA TIDAK TAHU KALAU USER MEMASUKKAN EMAIL, USERNAME ATAUPUN NO HP
         *  JADI APAPUN INPUTAN USER KITA MAPPING KE 3 FIELD DI DALAM OBJECT USER
         */
        userz.setEmail(userz.getUsername());

        Optional<User> opUserResult = userRepo.findTop1ByUsernameOrEmail(userz.getEmail(), userz.getEmail());//DATANYA PASTI HANYA 1
        User nextUser = null;
        try {
            if (!opUserResult.isEmpty()) {
                nextUser = opUserResult.get();
                if (!BcryptImpl.verifyHash(userz.getPassword() + nextUser.getUsername(), nextUser.getPassword()))//dicombo dengan userName
                {
                    return new ResponseHandler().generateResponse("Password salah!", HttpStatus.NOT_ACCEPTABLE, null, "FV07001", request);
                }

                /**
                 * Ketiga Informasi ini kalau butuh dibuatan saja di Model User nya
                 * kalau digunakan pastikan flow nya di check lagi !!
                 */
//                nextUser.setLastLoginDate(new Date());
//                nextUser.setTokenCounter(0);//SETIAP KALI LOGIN BERHASIL , BERAPA KALIPUN UJI COBA REQUEST TOKEN YANG SEBELUMNYA GAGAL AKAN SECARA OTOMATIS DIRESET MENJADI 0
//                nextUser.setPasswordCounter(0);//SETIAP KALI LOGIN BERHASIL , BERAPA KALIPUN UJI COBA YANG SEBELUMNYA GAGAL AKAN SECARA OTOMATIS DIRESET MENJADI 0
                nextUser.setModifiedBy(nextUser.getIdUser());
                nextUser.setModifiedDate(new Date());

            } else {
                return new ResponseHandler().generateResponse("User Tidak Terdaftar", HttpStatus.NOT_ACCEPTABLE, null, "FV07001", request);
            }

        } catch (Exception e) {
            strExceptionArr[1]="doLogin(Userz userz,WebRequest request)  --- LINE 230";
            LoggingFile.exceptionStringz(strExceptionArr,e, OtherConfig.getFlagLoging());

            return new ResponseHandler().generateResponse("GAGAL DIPROSES", HttpStatus.INTERNAL_SERVER_ERROR, null, "FELG001", request);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token", authManager(nextUser, request));
        map.put("akses", opUserResult.get().getAkses());
        map.put("idUser", opUserResult.get().getIdUser());
        map.put("email",opUserResult.get().getEmail());
        return new ResponseHandler().generateResponse(
                "Login Berhasil",
                HttpStatus.CREATED,
                map,
                null,
                request
        );
    }



    public ResponseEntity<Object> userUpdate(Long idUser, User user, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);

        Optional<User> findOneUser = userRepo.findById(idUser);

        if (!optionalUser.get().getAkses().getNamaAkses().equals("superadmin")) {
            return new ResponseHandler().generateResponse("Hanya superadmin yang dapat melakukan perubahan data", HttpStatus.UNAUTHORIZED, null, "FV02001", request);//FAILED VALIDATION
        } else if (findOneUser.isEmpty()) {
            return new ResponseHandler().generateResponse("User tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);//FAILED VALIDATION
        } else if (user.getUsername().isEmpty()) {
            return new ResponseHandler().generateResponse("Username harus diisi", HttpStatus.BAD_REQUEST, null, "FV02001", request);//FAILED VALIDATION
        }

        User updatedUser = findOneUser.get();
        try {
            Akses newakses = new Akses();
            updatedUser.setUsername(user.getUsername());
            updatedUser.setFullName(user.getFullName());
            updatedUser.setPassword(BcryptImpl.hash(user.getPassword() + user.getUsername()));
            newakses.setIdAkses(user.getAkses().getIdAkses());
            updatedUser.setAkses(newakses);
        } catch (Exception e) {
            strExceptionArr[1] = "userUpdate(User user, HttpServletRequest request) LINE 317" + RequestCapture.allRequest(request);

            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());
            return new ResponseHandler().generateResponse("Data Gagal Diupdate !! ", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE07001", request);//FAILED ERROR
        }

        return new ResponseHandler().generateResponse("Berhasil Diupdate!!",
                HttpStatus.OK,
                null,
                null, request
        );
    }

    public ResponseEntity<Object> getOneUser(Long idUser, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);

        Optional<User> findOneUser = userRepo.findById(idUser);

        if (!optionalUser.get().getAkses().getNamaAkses().equals("superadmin")) {
            return new ResponseHandler().generateResponse("Hanya superadmin yang dapat melihat data", HttpStatus.UNAUTHORIZED, null, "FV02001", request);//FAILED VALIDATION
        } else if (findOneUser.isEmpty()) {
            return new ResponseHandler().generateResponse("User tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV02001", request);//FAILED VALIDATION
        }

        return new ResponseHandler().generateResponse("Berhasil mendapatkan data",
                HttpStatus.OK,
                findOneUser.get(),
                null, request
        );
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        /**
            WARNING !!
            username yang ada di parameter otomatis hanya username , bukan string yang di kombinasi dengan password atau informasi lainnya...
            userName yang ada di parameter belum tentu adalah username...
            karena sistem memperbolehkan login dengan email, nohp ataupun username
            pastikan harus mengecek flag user teregistrasi atau belum
         */
        Optional<User> opUser = userRepo.findTop1ByUsernameOrEmail(s,s);
        if(opUser.isEmpty()) {
            return null;
        }
        User userNext = opUser.get();
         /**
            PARAMETER KE 3 TIDAK MENGGUNAKAN ROLE DARI SPRINGSECURITY CORE
            ROLE MODEL AKAN DITAMBAHKAN DI METHOD authManager DAN DIJADIKAN INFORMASI DI DALAM JWT AGAR LEBIH DINAMIS
         */
        return new org.springframework.security.core.userdetails.User(userNext.getUsername(),userNext.getPassword(),new ArrayList<>());
    }

    /**
     * Method untuk merangkai Claims yang kita modifikasi untuk dijadikan informasi di dalam TOKEN
     */
    //RANGE 006-010
    public String authManager(User user, HttpServletRequest request) {
        /* Untuk memasukkan user ke dalam */
        sBuild.setLength(0);
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        if(userDetails==null) {
            return "FAILED";
        }

        /* Isi apapun yang perlu diisi ke dalam object mapz !! */
        mapz.put("uid",user.getIdUser());
        mapz.put("ml",user.getEmail());//5-6-10

        String strAppendMenu = "";
        sBuild.setLength(0);

        String token = jwtUtility.generateToken(userDetails,mapz);
//        String expToken = token.
        token = Crypto.performEncrypt(token);

        return token;
    }

    private Page<User> getDataByValue(Pageable pageable, String filterBy, String value) {
        if (value.equals("") || value == null) {
            Long newValue = Long.valueOf(value);
            return userRepo.findByIdUser(pageable, newValue);
        }
        if (filterBy.equals("idUser")) {
            Long newValue = Long.valueOf(value);
            return userRepo.findByIdUser(pageable, newValue);
        } else if (filterBy.equals("username")) {
            return userRepo.findUserByUsernameContainingIgnoreCase(pageable, value);
        }
        Long newValue = Long.valueOf(value);
        return userRepo.findByIdUser(pageable, newValue);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }

    private Page<User> getDataByValue(Pageable pageable) {
        return userRepo.findAll(pageable);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }


    public ResponseEntity<Object> getAllUser(String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);

        return new ResponseHandler().generateResponse("Berhasil mendapatkan data",
                HttpStatus.OK,
                userRepo.findAll(),
                null, request
        );
    }

    // Get All User to use in dropdown list in frontend to assign approver, without admin and user itself
    public ResponseEntity<Object> getAllUserForAssignApprover(String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();
            List<User> allUsers = userRepo.findAll();

            // Filter out the admin and the current user
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> !user.getAkses().getNamaAkses().equals("admin")) // Assuming the admin's role is named "admin"
                    .filter(user -> !user.getUsername().equals(currentUser.getUsername()))
                    .collect(Collectors.toList());

            return new ResponseHandler().generateResponse("Berhasil mendapatkan data",
                    HttpStatus.OK,
                    filteredUsers,
                    null, request
            );
        } else {
            return new ResponseHandler().generateResponse("User not found",
                    HttpStatus.NOT_FOUND,
                    null,
                    null, request
            );
        }
    }
}
