package co.id.bcafinance.finalproject.dto;

/**
 * Object untuk validasi menu di class Controller
 */
public class MenuDTO {
    private Long idMenu;
    private String namaMenu;
    private String pathMenu;

    public Long getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(Long idMenu) {
        this.idMenu = idMenu;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public void setNamaMenu(String namaMenu) {
        this.namaMenu = namaMenu;
    }

    public String getPathMenu() {
        return pathMenu;
    }

    public void setPathMenu(String pathMenu) {
        this.pathMenu = pathMenu;
    }
}
