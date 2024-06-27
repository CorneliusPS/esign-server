package co.id.bcafinance.finalproject.service;


import co.id.bcafinance.finalproject.configuration.OtherConfig;
import co.id.bcafinance.finalproject.core.Crypto;
import co.id.bcafinance.finalproject.core.IService;
import co.id.bcafinance.finalproject.core.security.JwtUtility;
import co.id.bcafinance.finalproject.dto.AksesDTO;
import co.id.bcafinance.finalproject.handler.RequestCapture;
import co.id.bcafinance.finalproject.handler.ResponseHandler;
import co.id.bcafinance.finalproject.model.Akses;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.repo.AksesRepo;
import co.id.bcafinance.finalproject.repo.UserRepo;
import co.id.bcafinance.finalproject.util.LoggingFile;
import co.id.bcafinance.finalproject.util.TransformToDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;


/**
 * Company Code - Not Necessery
 * Modul Code 01
 * Type of Error -> Validation = FV , Engine Error = FE
 * ex : FE03 (Error di Modul Akses Functional Save)
 */
/**
 * Service yang mengelola operasi CRUD untuk Akses.
 */
@Service
@Transactional
public class AksesService implements IService<Akses> {

    @Autowired
    private AksesRepo aksesRepo;

    @Autowired
    private ModelMapper modelMapper;

    TransformToDTO transformToDTO = new TransformToDTO();

    Map<String, Object> mapResult = new HashMap<>();

    private String[] strExceptionArr = new String[2];

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtility jwtUtility;

    /**
     * Method untuk menyimpan Akses baru.
     *
     * @param akses              Akses yang akan disimpan.
     * @param authorizationHeader Header otorisasi JWT.
     * @param request            Permintaan HTTP.
     * @return                   ResponseEntity yang berisi pesan keberhasilan atau kegagalan.
     */
    @Override
    public ResponseEntity<Object> save(Akses akses, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);
        Optional<Akses> getOneAkses = aksesRepo.findByNamaAkses(akses.getNamaAkses().toLowerCase());

        if (!optionalUser.get().getAkses().getNamaAkses().equals("superadmin") && !optionalUser.get().getAkses().getNamaAkses().equals("admin")) {
            return new ResponseHandler().generateResponse("Hanya superadmin dan admin yang dapat melakukan perubahan data", HttpStatus.UNAUTHORIZED, null, "FV01001", request);//FAILED VALIDATION
        } else if (akses == null) {
            return new ResponseHandler().generateResponse("Data Tidak Valid", HttpStatus.BAD_REQUEST, null, "FV01002", request);//FAILED VALIDATION
        } else if (getOneAkses.isPresent()) {
            return new ResponseHandler().generateResponse("Akses Sudah Ada", HttpStatus.CONFLICT, null, "FV01003", request);//FAILED VALIDATION
        }

        try {
            akses.setNamaAkses(akses.getNamaAkses().toLowerCase());
            aksesRepo.save(akses);
        } catch (Exception e) {
            strExceptionArr[1] = "save(Akses akses, HttpServletRequest request) LINE 79" + RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

            return new ResponseHandler().generateResponse("Data Gagal Disimpan", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE01001", request);//FAILED ERROR
        }

        return new ResponseHandler().generateResponse("Berhasil Disimpan!!",
                HttpStatus.CREATED,
                null,
                null,
                request
        );
    }

    /**
     * Method untuk mengedit Akses yang ada.
     *
     * @param id                  ID Akses yang akan diedit.
     * @param akses               Akses yang berisi data perubahan.
     * @param authorizationHeader Header otorisasi JWT.
     * @param request             Permintaan HTTP.
     * @return                    ResponseEntity yang berisi pesan keberhasilan atau kegagalan.
     */
    public ResponseEntity<Object> edit(Long id, Akses akses, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);

        Optional<Akses> optionalMenu = aksesRepo.findById(id);

        if (!optionalUser.get().getAkses().getNamaAkses().equals("superadmin") && !optionalUser.get().getAkses().getNamaAkses().equals("admin")) {
            return new ResponseHandler().generateResponse("Hanya superadmin dan admin yang dapat melakukan perubahan data", HttpStatus.UNAUTHORIZED, null, "FV01004", request);//FAILED VALIDATION
        } else if (id.equals(1L) && !akses.getNamaAkses().equalsIgnoreCase("superadmin") && optionalUser.get().getAkses().getNamaAkses().equals("superadmin")) {
            return new ResponseHandler().generateResponse("Tidak dapat mengubah akses superadmin", HttpStatus.BAD_REQUEST, null, "FV01005", request);//FAILED VALIDATION
        } else if (optionalMenu.isEmpty()) {
            return new ResponseHandler().generateResponse("Data Tidak Ditemukan", HttpStatus.NOT_FOUND, null, "FV01006", request);//FAILED VALIDATION
        }

        try {
            Akses aksesNext = optionalMenu.get();
            aksesNext.setNamaAkses(akses.getNamaAkses().toLowerCase());
            aksesNext.setModifiedDate(new Date());
            aksesNext.setLtMenu(akses.getLtMenu());
            aksesNext.setModifiedBy(1L);

            AksesDTO aksesDTO = modelMapper.map(aksesNext, new TypeToken<AksesDTO>() {
            }.getType());
        } catch (Exception e) {
            strExceptionArr[1] = "edit(Akses akses, HttpServletRequest request) LINE 139" + RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

            return new ResponseHandler().generateResponse("Data Gagal Diupdate", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE01002", request);//FAILED ERROR
        }

        return new ResponseHandler().generateResponse("Berhasil Diubah!!",
                HttpStatus.CREATED,
                null,
                null, request
        );
    }

    /**
     * Method untuk menghapus Akses berdasarkan ID.
     *
     * @param id      ID Akses yang akan dihapus.
     * @param request Permintaan HTTP.
     */
    @Override
    public ResponseEntity<Object> delete(Long id, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);
        Optional<Akses> getOneAkses = aksesRepo.findById(id);

        if (!optionalUser.get().getAkses().getNamaAkses().equals("superadmin") && !optionalUser.get().getAkses().getNamaAkses().equals("admin")) {
            return new ResponseHandler().generateResponse("Hanya superadmin dan admin yang dapat melakukan perubahan data", HttpStatus.UNAUTHORIZED, null, "FV01007", request);//FAILED VALIDATION
        } else if (id.equals(1L)) {
            return new ResponseHandler().generateResponse("Tidak dapat menghapus superadmin", HttpStatus.BAD_REQUEST, null, "FV01008", request);//FAILED VALIDATION
        } else if (getOneAkses.isEmpty()) {
            return new ResponseHandler().generateResponse("Data Tidak Ditemukan", HttpStatus.NOT_FOUND, null, "FV01009", request);//FAILED VALIDATION
        }

        try {
            aksesRepo.delete(getOneAkses.get());
        } catch (Exception e) {
            strExceptionArr[1] = "delete(Akses akses, HttpServletRequest request) LINE 177" + RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

            return new ResponseHandler().generateResponse("Data Gagal Dihapus", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE01003", request);//FAILED ERROR
        }

        return new ResponseHandler().generateResponse("Akses berhasil dihapus",
                HttpStatus.OK,
                null,
                null, request);
    }

    @Override
    public ResponseEntity<Object> update(Long id, Akses akses, String authorizationHeader, HttpServletRequest request) {
        return null;
    }

    /**
     * Method untuk menemukan Akses berdasarkan ID.
     *
     * @param id      ID Akses yang akan ditemukan.
     * @param request Permintaan HTTP.
     * @return        ResponseEntity yang berisi Akses yang ditemukan.
     */
    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        Optional<Akses> getOneAkses = aksesRepo.findById(id);

        if (getOneAkses.isEmpty()) {
            return new ResponseHandler().generateResponse("Data Tidak Ditemukan", HttpStatus.NOT_FOUND, null, "FV01010", request);//FAILED VALIDATION
        }

        return new ResponseHandler().generateResponse("OK",
                HttpStatus.OK,
                getOneAkses.get(),
                null, request);
    }

    /**
     * Method untuk menemukan Akses berdasarkan kriteria tertentu.
     *
     * @param pageable            Pengaturan halaman.
     * @param columFirst          Nama kolom yang akan difilter.
     * @param valueFirst          Nilai yang akan difilter.
     * @param authorizationHeader Header otorisasi JWT.
     * @param request             Permintaan HTTP.
     * @return                    ResponseEntity yang berisi daftar Akses yang memenuhi kriteria.
     */
    @Override
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, String authorizationHeader, HttpServletRequest request) {
        Page<Akses> pageAkses = null;
        List<Akses> listAkses = null;

        if (columFirst.equals("idAkses")) {
            if (!valueFirst.equals("") && valueFirst != null) {
                try {
                        /*
                            UNTUK ID YANG BER TIPE NUMERIC
                            TIDAK PERLU DIGUNAKAN JIKA ID BER TIPE STRING
                         */
                    Long.parseLong(valueFirst);
                } catch (Exception e) {
                    strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 196";
                    LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

                    return new ResponseHandler().generateResponse("DATA FILTER TIDAK SESUAI FORMAT HARUS ANGKA", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE01003", request);
                }
            }
        }

        pageAkses = getDataByValue(pageable, columFirst, valueFirst);
        listAkses = pageAkses.getContent();
        if (listAkses.isEmpty()) {
            return new ResponseHandler().generateResponse("DATA TIDAK DITEMUKAN", HttpStatus.NOT_FOUND, null, "FV010011", request);
        }
        List<AksesDTO> ltAksesDTO = modelMapper.map(listAkses, new TypeToken<List<AksesDTO>>() {}.getType());
        mapResult = transformToDTO.transformObject(mapResult,
                ltAksesDTO,
                pageAkses,
                columFirst,
                valueFirst
        );

        return new ResponseHandler().generateResponse("OK",
                HttpStatus.OK,
                mapResult,
                null,
                request
        );
    }

    @Override
    public ResponseEntity<Object> findWithoutPage(HttpServletRequest request) {
        return null;
    }

    private Page<Akses> getDataByValue(Pageable pageable, String columnFirst, String valueFirst) {
        /**
         * Menampilkan data default
         */
        if (valueFirst.equals("") || valueFirst == null) {
            return aksesRepo.findAll(pageable);
        }

        if (columnFirst.equals("namaAkses")) {
            return aksesRepo.findByNamaAksesContainingIgnoreCase(pageable, valueFirst);
        }
        return aksesRepo.findAll(pageable);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }
}
