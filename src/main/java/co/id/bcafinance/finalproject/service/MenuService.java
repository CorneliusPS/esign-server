package co.id.bcafinance.finalproject.service;

import co.id.bcafinance.finalproject.configuration.OtherConfig;
import co.id.bcafinance.finalproject.core.Crypto;
import co.id.bcafinance.finalproject.core.IService;
import co.id.bcafinance.finalproject.core.security.JwtUtility;
import co.id.bcafinance.finalproject.dto.MenuDTO;
import co.id.bcafinance.finalproject.handler.RequestCapture;
import co.id.bcafinance.finalproject.handler.ResponseHandler;
import co.id.bcafinance.finalproject.model.Menu;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.repo.MenuRepo;
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
 * Modul Code 04
 * Type of Error -> Validation = FV , Engine Error = FE
 * ex : FE04001 (Error di Modul Menu Functional Save)
 */

/**
 * Service yang mengelola operasi CRUD untuk Menu.
 */
@Service
@Transactional
public class MenuService implements IService<Menu> {

    @Autowired
    private MenuRepo menuRepo;

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
     * Menyimpan data menu baru.
     *
     * @param menu               Data menu yang akan disimpan.
     * @param authorizationHeader Header otorisasi.
     * @param request            Permintaan HTTP.
     * @return                   ResponseEntity yang berisi hasil dari operasi.
     */
    @Override
    public ResponseEntity<Object> save(Menu menu, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);

        if (!optionalUser.get().getAkses().getNamaAkses().equals("superadmin") && !optionalUser.get().getAkses().getNamaAkses().equals("admin")) {
            return new ResponseHandler().generateResponse("Hanya superadmin dan admin yang dapat melakukan perubahan data", HttpStatus.UNAUTHORIZED, null, "FV04001", request);//FAILED VALIDATION
        } else if (menu == null) {
            return new ResponseHandler().generateResponse("Data Tidak Valid", HttpStatus.BAD_REQUEST, null, "FV04001", request);//FAILED VALIDATION
        }
        try {
            menuRepo.save(menu);
        } catch (Exception e) {
            strExceptionArr[1] = "save(Menu menu, HttpServletRequest request) LINE 72" + RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

            return new ResponseHandler().generateResponse("Data Gagal Disimpan !! ", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE04001", request);//FAILED ERROR
        }

        return new ResponseHandler().generateResponse("Berhasil Disimpan!!",
                HttpStatus.CREATED,
                null,
                null, request
        );
    }

    /**
     * Mengubah data menu yang sudah ada.
     *
     * @param id                 ID menu yang akan diubah.
     * @param menu               Data menu yang akan diubah.
     * @param authorizationHeader Header otorisasi.
     * @param request            Permintaan HTTP.
     * @return                   ResponseEntity yang berisi hasil dari operasi.
     */
    public ResponseEntity<Object> edit(Long id, Menu menu, String authorizationHeader, HttpServletRequest request) {
        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);
        Optional<Menu> optionalMenu = menuRepo.findById(id);
        if (!optionalUser.get().getAkses().getNamaAkses().equals("superadmin") && !optionalUser.get().getAkses().getNamaAkses().equals("admin")) {
            return new ResponseHandler().generateResponse("Hanya superadmin dan admin yang dapat melakukan perubahan data", HttpStatus.UNAUTHORIZED, null, "FV04001", request);//FAILED VALIDATION
        } else if (optionalMenu.isEmpty()) {
            return new ResponseHandler().generateResponse("Data Tidak Ditemukan", HttpStatus.BAD_REQUEST, null, "FV04001", request);//FAILED VALIDATION
        }

        MenuDTO menuDTO = null;

        try {
            Menu menuNext = optionalMenu.get();
            menuNext.setNamaMenu(menu.getNamaMenu());
            menuNext.setPathMenu(menu.getPathMenu());
            menuNext.setModifiedDate(new Date());
            menuNext.setModifiedBy(1L);

            menuDTO = modelMapper.map(menuNext, new TypeToken<MenuDTO>() {}.getType());
        } catch (Exception e) {
            strExceptionArr[1] = "edit(Menu menu, HttpServletRequest request) LINE 108" + RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

            return new ResponseHandler().generateResponse("Data Gagal Diubah", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE04002", request);//FAILED ERROR
        }

        return new ResponseHandler().generateResponse("Berhasil Diubah!!",
                HttpStatus.CREATED,
                menuDTO,
                null, request
        );
    }

    /**
     * Menghapus data menu berdasarkan ID.
     *
     * @param id                 ID menu yang akan dihapus.
     * @param authorizationHeader Header otorisasi.
     * @param request            Permintaan HTTP.
     * @return                   ResponseEntity yang berisi hasil dari operasi.
     */
    @Override
    public ResponseEntity<Object> delete(Long id, String authorizationHeader, HttpServletRequest request) {
        Optional<Menu> getOneMenu = menuRepo.findById(id);

        String username = null;
        username = jwtUtility.getUsernameFromToken(Crypto.performDecrypt(authorizationHeader.substring(7)));

        Optional<User> optionalUser = userRepo.findByUsername(username);

        if (!optionalUser.get().getAkses().getNamaAkses().equals("superadmin") && !optionalUser.get().getAkses().getNamaAkses().equals("admin")) {
            return new ResponseHandler().generateResponse("Hanya superadmin dan admin yang dapat melakukan perubahan data", HttpStatus.UNAUTHORIZED, null, "FV04001", request);//FAILED VALIDATION
        } else if (getOneMenu.isEmpty()) {
            return new ResponseHandler().generateResponse("Menu tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV04002", request);
        }

        try {
            menuRepo.delete(getOneMenu.get());
        } catch (Exception e) {
            strExceptionArr[1] = "delete(Menu menu, HttpServletRequest request) LINE 139" + RequestCapture.allRequest(request);
            LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

            return new ResponseHandler().generateResponse("Data Gagal Dihapus", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE04002", request);//FAILED ERROR
        }

        return new ResponseHandler().generateResponse("Menu berhasil dihapus",
                HttpStatus.OK,
                null,
                null,
                request
        );
    }

    /**
     * Memperbarui data menu yang sudah ada.
     *
     * @param id                 ID menu yang akan diperbarui.
     * @param menu               Data menu yang akan diperbarui.
     * @param authorizationHeader Header otorisasi.
     * @param request            Permintaan HTTP.
     * @return                   ResponseEntity yang berisi hasil dari operasi.
     */
    @Override
    public ResponseEntity<Object> update(Long id, Menu menu, String authorizationHeader, HttpServletRequest request) {
        return null;
    }

    /**
     * Mencari menu berdasarkan ID.
     *
     * @param id      ID menu yang akan dicari.
     * @param request Permintaan HTTP.
     * @return        ResponseEntity yang berisi hasil dari operasi.
     */
    @Override
    public ResponseEntity<Object> findById(Long id, HttpServletRequest request) {
        Optional<Menu> getOneMenu = menuRepo.findById(id);

        if (getOneMenu.isEmpty()) {
            return new ResponseHandler().generateResponse("Menu tidak ditemukan", HttpStatus.NOT_FOUND, null, "FV04001", request);
        }

        return new ResponseHandler().generateResponse("OK",
                HttpStatus.OK,
                getOneMenu.get(),
                null,
                request
        );
    }

    /**
     * Mencari menu berdasarkan kriteria tertentu dengan pagination.
     *
     * @param pageable            Informasi halaman untuk hasil pencarian.
     * @param columFirst          Nama kolom untuk filtering data.
     * @param valueFirst          Nilai untuk filtering data.
     * @param authorizationHeader Header otorisasi.
     * @param request             Permintaan HTTP.
     * @return                    ResponseEntity yang berisi hasil dari operasi.
     */
    @Override
    public ResponseEntity<Object> find(Pageable pageable, String columFirst, String valueFirst, String authorizationHeader, HttpServletRequest request) {
        Page<Menu> pageMenu = null;
        List<Menu> listMenu = null;

        if (columFirst.equals("id")) {
            if (!valueFirst.equals("") && valueFirst != null) {
                try {
                        /*
                            UNTUK ID YANG BER TIPE NUMERIC
                            TIDAK PERLU DIGUNAKAN JIKA ID BER TIPE STRING
                         */
                    Long.parseLong(valueFirst);
                } catch (Exception e) {
                    strExceptionArr[1] = "find(Pageable pageable, String columFirst, String valueFirst, HttpServletRequest request) --- LINE 252";
                    LoggingFile.exceptionStringz(strExceptionArr, e, OtherConfig.getFlagLoging());

                    return new ResponseHandler().generateResponse("DATA FILTER TIDAK SESUAI FORMAT HARUS ANGKA", HttpStatus.INTERNAL_SERVER_ERROR, null, "FE04003", request);
                }
            }
        }

        pageMenu = getDataByValue(pageable, columFirst, valueFirst);
        listMenu = pageMenu.getContent();
        if (listMenu.isEmpty()) {
            return new ResponseHandler().generateResponse("DATA TIDAK DITEMUKAN", HttpStatus.NOT_FOUND, null, "FV04001", request);
        }
        List<MenuDTO> ltMenuDTO = modelMapper.map(listMenu, new TypeToken<List<MenuDTO>>() {}.getType());
        mapResult = transformToDTO.transformObject(mapResult,
                ltMenuDTO,
                pageMenu,
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

    /**
     * Mencari menu tanpa pembagian halaman.
     *
     * @param request Permintaan HTTP.
     * @return        ResponseEntity yang berisi hasil dari operasi.
     */
    @Override
    public ResponseEntity<Object> findWithoutPage(HttpServletRequest request) {
        return null;
    }

    private Page<Menu> getDataByValue(Pageable pageable, String columnFirst, String valueFirst) {
        /**
         * Menampilkan data default
         */
        System.out.println(columnFirst);
        System.out.println(valueFirst);
        if (valueFirst.equals("") || valueFirst == null) {
            System.out.println("Sini keknya");
            return menuRepo.findAll(pageable);
        }

        if (columnFirst.equals("namaMenu")) {
            System.out.println("Masuk sini");
            return menuRepo.findByNamaMenuContainingIgnoreCase(pageable, valueFirst);
        } else if (columnFirst.equals("pathMenu")) {
            System.out.println("Sini");
            return menuRepo.findByPathMenuContainingIgnoreCase(pageable, valueFirst);
        }
        System.out.println("Ini");
        return menuRepo.findAll(pageable);// ini default kalau parameter search nya tidak sesuai--- asumsi nya di hit bukan dari web
    }
}
