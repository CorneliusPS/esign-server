package co.id.bcafinance.finalproject.controller;

import co.id.bcafinance.finalproject.dto.AksesDTO;
import co.id.bcafinance.finalproject.model.Akses;
import co.id.bcafinance.finalproject.service.AksesService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usr-mgmnt")
public class AksesController {
    @Autowired
    private AksesService aksesService;
    @Autowired
    private ModelMapper modelMapper;
    private Map<String, String> mapSorting = new HashMap<String, String>();

    public AksesController() {
        mapSorting();
    }

    /**
     * INI BUAT DICONVERT DARI KIRIMAN FE AGAR SESUAI DENGAN FIELD DI BE
     */
    private void mapSorting() {
        mapSorting.put("id", "idAkses");
        mapSorting.put("nama", "namaAkses");
    }

    /**
     * @desc    Membuat akses baru
     * @route   POST /api/usr-mgmnt/v1/akses
     * @access  Private (Superadmin & Admin)
     */
    @PostMapping("/v1/akses")
    public ResponseEntity<Object> save(@Valid @RequestBody AksesDTO aksesDTO, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        Akses akses = modelMapper.map(aksesDTO, new TypeToken<Akses>() {
        }.getType());
        return aksesService.save(akses, authorizationHeader, request);
    }

    /**
     * @desc    Update akses
     * @route   PUT /v1/akses/:idAkses
     * @access  Private (Superadmin & Admin)
     */
    @PutMapping("/v1/akses/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody AksesDTO aksesDTO, @PathVariable(value = "id") Long id, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        Akses akses = modelMapper.map(aksesDTO, new TypeToken<Akses>() {}.getType());
        return aksesService.edit(id, akses, authorizationHeader, request);
    }

    /**
     * @desc    Hapus akses (Pastikan akses yang akan dihapus tidak digunakan atau berelasi dengan user)
     * @route   DELETE /api/usr-mgmnt/v1/akses/:idAkses
     * @access  Private (Superadmin & Admin)
     */
    @DeleteMapping("/v1/akses/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Long id, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return aksesService.delete(id, authorizationHeader, request);
    }

    /**
     * @desc    Mencari akses berdasarkan id akses
     * @route   GET /api/usr-mgmnt/v1/akses/:idAkses
     * @access  Public
     */
    @GetMapping("/v1/akses/{id}")
    public ResponseEntity<Object> findById(@PathVariable(value = "id") Long id, HttpServletRequest request) {
        return aksesService.findById(id, request);
    }

    /**
     * @desc    Mencari akses dengan opsi filter dan pagination
     * @route   GET /api/usr-mgmnt/v1/akses/:page/:sort/:sort-by
     * @access  Public
     */
    @GetMapping("/v1/akses/{page}/{sort}/{sort-by}")
    public ResponseEntity<Object> find(
            @PathVariable(value = "page") Integer page,//page yang ke ?
            @PathVariable(value = "sort") String sort,//asc desc
            @PathVariable(value = "sort-by") String sortBy,// column Name in java Variable,
            @RequestParam("filter-by") String filterBy,
            @RequestParam("value") String value,
            @RequestParam("size") String size,
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest request
    ) {
        Pageable pageable = null;
        page = page == null ? 0 : page;
        sortBy = (sortBy == null || sortBy.equals("")) ? "idAkses" : sortBy;//penanda kalau null dari FE itu berarti kayak buka akses baru
        sort = (sort == null || sort.equals("") || sort.equals("asc")) ? "asc" : "desc";// else = asc, karena bisa jadi dari FE dikirim bukan asc, walaupun sudah dijaga null value

        sortBy = mapSorting.get(sortBy);// id = idGroupakses, nama = namaGroupakses dst....
        pageable = PageRequest.of(page, Integer.parseInt(size.equals("") ? "10" : size),
                sort.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy));
        return aksesService.find(pageable, filterBy, value, authorizationHeader, request);
    }
}
