package co.id.bcafinance.finalproject.controller;

import co.id.bcafinance.finalproject.dto.MenuDTO;
import co.id.bcafinance.finalproject.model.Menu;
import co.id.bcafinance.finalproject.service.MenuService;
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
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private ModelMapper modelMapper;
    private Map<String, String> mapSorting = new HashMap<String, String>();

    public MenuController() {
        mapSorting();
    }

    /**
     * INI BUAT DICONVERT DARI KIRIMAN FE AGAR SESUAI DENGAN FIELD DI BE
     */
    private void mapSorting() {
        mapSorting.put("id", "idMenu");
        mapSorting.put("nama", "namaMenu");
        mapSorting.put("path", "pathMenu");
    }

    /**
     * @desc    Membuat menu baru
     * @route   POST /api/usr-mgmnt/v1/menu
     * @access  Private (Superadmin & Admin)
     */
    @PostMapping("/v1/menu")
    public ResponseEntity<Object> save(@Valid @RequestBody MenuDTO menuDTO, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        Menu menu = modelMapper.map(menuDTO, new TypeToken<Menu>() {
        }.getType());
        return menuService.save(menu, authorizationHeader, request);
    }

    /**
     * @desc    Update menu berdasarkan idMenu
     * @route   PUT /api/usr-mgmnt/v1/menu/:idMenu
     * @access  Private (Superadmin & Admin)
     */
    @PutMapping("/v1/menu/{id}")
    public ResponseEntity<Object> edit(@Valid @RequestBody MenuDTO menuDTO, @PathVariable(value = "id") Long id, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        Menu menu = modelMapper.map(menuDTO, new TypeToken<Menu>() {
        }.getType());
        return menuService.edit(id, menu, authorizationHeader, request);
    }

    /**
     * @desc    Hapus menu
     * @route   DELETE /api/usr-mgmnt/v1/menu/:idMenu
     * @access  Private (Superadmin & Admin)
     */
    @DeleteMapping("/v1/menu/{id}")
    public ResponseEntity<Object> delete(@PathVariable(value = "id") Long id, @RequestHeader("Authorization") String authorizationHeader, HttpServletRequest request) {
        return menuService.delete(id, authorizationHeader, request);
    }

    /**
     * @desc    Mencari satu menu berdasarkan idMenu
     * @route   GET /api/usr-mgmnt/v1/menu/:idMenu
     * @access  PUBLIC
     */
    @GetMapping("/v1/menu/{id}")
    public ResponseEntity<Object> findById(@PathVariable(value = "id") Long id, HttpServletRequest request) {
        return menuService.findById(id, request);
    }

    /**
     * @desc    Mencari menu dengan opsi filter dan pagination
     * @route   GET /api/usr-mgmnt/v1/menu/:page/:sort/:sort-by
     * @access  Public
     */
    @GetMapping("/v1/menu/{page}/{sort}/{sort-by}")
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
        sortBy = (sortBy == null || sortBy.equals("")) ? "id" : sortBy;//penanda kalau null dari FE itu berarti kayak buka menu baru
        sort = (sort == null || sort.equals("") || sort.equals("asc")) ? "asc" : "desc";// else = asc, karena bisa jadi dari FE dikirim bukan asc, walaupun sudah dijaga null value

        sortBy = mapSorting.get(sortBy);// id = idGroupMenu, nama = namaGroupMenu dst....
        pageable = PageRequest.of(page, Integer.parseInt(size.equals("") ? "10" : size),
                sort.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy));
        return menuService.find(pageable, filterBy, value, authorizationHeader, request);
    }

}
