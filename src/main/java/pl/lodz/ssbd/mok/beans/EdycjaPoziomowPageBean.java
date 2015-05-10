/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.mok.beans;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import pl.lodz.ssbd.entities.PoziomDostepu;
import pl.lodz.ssbd.entities.Uzytkownik;
import pl.lodz.ssbd.exceptions.PoziomDostepuException;

/**
 *
 * @author Maciej
 */
@Named(value = "edycjaPoziomowPageBean")
@RequestScoped
public class EdycjaPoziomowPageBean {
    
    @Inject
    private UzytkownikSession uzytkownikSession;
    
    private DataModel<PoziomDostepu> poziomyDataModel;

    public DataModel<PoziomDostepu> getPoziomyDataModel() {
        return poziomyDataModel;
    }
    
    @PostConstruct
    private void initModel(){
        List<PoziomDostepu> poziomy = getUzytkownikEdycja().getPoziomDostepuList();
        poziomyDataModel = new ListDataModel<PoziomDostepu>(poziomy);
    }

     public void setUzytkownikSession(UzytkownikSession uzytkownikSession) {
        this.uzytkownikSession = uzytkownikSession;
    }
    
    public Uzytkownik getUzytkownikEdycja() {
        return uzytkownikSession.getUzytkownikEdycja();
    }
    
    public void nadajPoziom(){
        try {
            uzytkownikSession.nadajPoziom(poziomyDataModel.getRowData());
        } catch (PoziomDostepuException ex) {
            Logger.getLogger(EdycjaPoziomowPageBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        uzytkownikSession.pobierzUzytkownikaDoEdycji(uzytkownikSession.getUzytkownikEdycja());
        initModel();
    }
    
    public void odbierzPoziom(){
        try {
            uzytkownikSession.odbierzPoziom(poziomyDataModel.getRowData());
        } catch (PoziomDostepuException ex) {
            Logger.getLogger(EdycjaPoziomowPageBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        uzytkownikSession.pobierzUzytkownikaDoEdycji(uzytkownikSession.getUzytkownikEdycja());
        initModel();
    }
}
