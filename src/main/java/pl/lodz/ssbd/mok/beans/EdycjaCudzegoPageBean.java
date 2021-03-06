package pl.lodz.ssbd.mok.beans;

import java.util.Locale;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import pl.lodz.ssbd.entities.Uzytkownik;
import pl.lodz.ssbd.exceptions.PoprzednieHasloException;
import pl.lodz.ssbd.exceptions.UzytkownikException;
import pl.lodz.ssbd.utils.Bundle;

/**
 * Klasa obslugujaca widok edycji cudzego konta
 * @author Maciej
 */
@Named(value = "edycjaCudzegoPageBean")
@RequestScoped
public class EdycjaCudzegoPageBean {
    
    @Inject
    private UzytkownikSession uzytkownikSession;
    private String noweHaslo;
    private String powtorzHaslo;

    public String getNoweHaslo() {
        return noweHaslo;
    }

    public void setNoweHaslo(String noweHaslo) {
        this.noweHaslo = noweHaslo;
    }

    public String getPowtorzHaslo() {
        return powtorzHaslo;
    }

    public void setPowtorzHaslo(String powtorzHaslo) {
        this.powtorzHaslo = powtorzHaslo;
    }

     public void setUzytkownikSession(UzytkownikSession uzytkownikSession) {
        this.uzytkownikSession = uzytkownikSession;
    }
    
    public Uzytkownik getUzytkownikEdycja() {
        return uzytkownikSession.getUzytkownikEdycja();
    }

    /**
     * edytuje Uzytkownika
     * @return String z przkierowaniem do strony po edycji
     */
    public String edytujUzytkownika() {
        boolean zmianaHasla = false;
        if(!noweHaslo.equals(powtorzHaslo)){
            FacesContext fctx = FacesContext.getCurrentInstance();
            FacesMessage fmsg = new FacesMessage("Hasła się nie zgadzają");
            fctx.addMessage(null, fmsg);
            return null;
        }
        if(!(noweHaslo.equals("") || noweHaslo == null)){
            zmianaHasla = true;
        }
        try {
            uzytkownikSession.zapiszUzytkownikaPoEdycji(zmianaHasla, noweHaslo);
        } catch (UzytkownikException ex) {
            return "nieaktualnedane";
        } catch (PoprzednieHasloException ex) {
            FacesContext fctx = FacesContext.getCurrentInstance();
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            FacesMessage fmsg = new FacesMessage(Bundle.internalizuj(ex.getMessage(), locale));
            fctx.addMessage(null, fmsg);
            return null;
        }
        return "panelAdm";
    }
}
