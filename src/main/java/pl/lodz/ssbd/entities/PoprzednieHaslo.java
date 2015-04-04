/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Robert Mielczarek <180640@edu.p.lodz.pl>
 */
@Entity
@Table(name = "poprzednie_haslo")
@TableGenerator(name="PoprzednieHasloIdGen", table="generator", pkColumnName="class_name", valueColumnName="id_range", pkColumnValue="PoprzednieHaslo")
@NamedQueries({
    @NamedQuery(name = "PoprzednieHaslo.findAll", query = "SELECT p FROM PoprzednieHaslo p"),
    @NamedQuery(name = "PoprzednieHaslo.findByIdPoprzedniegeHaslo", query = "SELECT p FROM PoprzednieHaslo p WHERE p.idPoprzedniegeHaslo = :idPoprzedniegeHaslo"),
    @NamedQuery(name = "PoprzednieHaslo.findByStareHasloMd5", query = "SELECT p FROM PoprzednieHaslo p WHERE p.stareHasloMd5 = :stareHasloMd5"),
    @NamedQuery(name = "PoprzednieHaslo.findByWersjaEncji", query = "SELECT p FROM PoprzednieHaslo p WHERE p.wersjaEncji = :wersjaEncji")})
public class PoprzednieHaslo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_poprzedniege_haslo", unique = true, updatable = false, nullable = false)
    @GeneratedValue(strategy= GenerationType.TABLE, generator="PoprzednieHasloIdGen")
    private Long idPoprzedniegeHaslo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 32, max = 32)
    @Column(name = "stare_haslo_md5", updatable = false, nullable = false, length = 32)
    private String stareHasloMd5;
    @Basic(optional = false)
    @NotNull
    @Column(name = "wersja_encji", nullable = false)
    @Version
    private long wersjaEncji;
    @JoinColumn(name = "id_uzytkownik", referencedColumnName = "id_uzytkownik", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    private Uzytkownik idUzytkownik;

    public PoprzednieHaslo() {
    }

    public PoprzednieHaslo(Long idPoprzedniegeHaslo) {
        this.idPoprzedniegeHaslo = idPoprzedniegeHaslo;
    }

    public PoprzednieHaslo(Long idPoprzedniegeHaslo, String stareHasloMd5, long wersjaEncji) {
        this.idPoprzedniegeHaslo = idPoprzedniegeHaslo;
        this.stareHasloMd5 = stareHasloMd5;
        this.wersjaEncji = wersjaEncji;
    }

    public Long getIdPoprzedniegeHaslo() {
        return idPoprzedniegeHaslo;
    }

    public void setIdPoprzedniegeHaslo(Long idPoprzedniegeHaslo) {
        this.idPoprzedniegeHaslo = idPoprzedniegeHaslo;
    }

    public String getStareHasloMd5() {
        return stareHasloMd5;
    }

    public void setStareHasloMd5(String stareHasloMd5) {
        this.stareHasloMd5 = stareHasloMd5;
    }

    public long getWersjaEncji() {
        return wersjaEncji;
    }

    public void setWersjaEncji(long wersjaEncji) {
        this.wersjaEncji = wersjaEncji;
    }

    public Uzytkownik getIdUzytkownik() {
        return idUzytkownik;
    }

    public void setIdUzytkownik(Uzytkownik idUzytkownik) {
        this.idUzytkownik = idUzytkownik;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPoprzedniegeHaslo != null ? idPoprzedniegeHaslo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PoprzednieHaslo)) {
            return false;
        }
        PoprzednieHaslo other = (PoprzednieHaslo) object;
        if ((this.idPoprzedniegeHaslo == null && other.idPoprzedniegeHaslo != null) || (this.idPoprzedniegeHaslo != null && !this.idPoprzedniegeHaslo.equals(other.idPoprzedniegeHaslo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.lodz.ssbd.entities.PoprzednieHaslo[ idPoprzedniegeHaslo=" + idPoprzedniegeHaslo + " ]";
    }
    
}