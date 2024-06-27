package co.id.bcafinance.finalproject.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MstMenu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDMenu")
    private Long idMenu;

    @Column(name = "NamaMenu", nullable = false, length = 30)
    private String namaMenu;

    @Column(name = "PathMenu", nullable = false, length = 50)
    private String pathMenu;

    @ManyToMany(mappedBy = "ltMenu")
    private List<Akses> ltAkses;

    /**
     * Start Group Audit trails
     */

    @Column(name = "CreatedBy", nullable = false)
    private Long createdBy = 1L;

    @Column(name = "CreatedDate", nullable = false)
    private Date createdDate = new Date();

    @Column(name = "ModifiedBY")
    private Long modifiedBy;

    @Column(name = "ModifiedDate")
    private Date modifiedDate;

    /**
     * End Group Audit trails
     */

    public List<Akses> getLtAkses() {
        return ltAkses;
    }

    public void setLtAkses(List<Akses> ltAkses) {
        this.ltAkses = ltAkses;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
