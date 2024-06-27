package co.id.bcafinance.finalproject.dto;

import java.util.List;

/**
 *   Object untuk validasi akses di class Controller
 */
public class AksesDTO {

    private Long idAkses;

    private String namaAkses;

    private List<MenuDTO> ltMenu;

    public Long getIdAkses() {
        return idAkses;
    }

    public void setIdAkses(Long idAkses) {
        this.idAkses = idAkses;
    }

    public String getNamaAkses() {
        return namaAkses;
    }

    public void setNamaAkses(String namaAkses) {
        this.namaAkses = namaAkses;
    }

    public List<MenuDTO> getLtMenu() {
        return ltMenu;
    }

    public void setLtMenu(List<MenuDTO> ltMenu) {
        this.ltMenu = ltMenu;
    }
}
