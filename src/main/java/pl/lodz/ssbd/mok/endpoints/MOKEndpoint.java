/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.mok.endpoints;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import pl.lodz.ssbd.entities.PoprzednieHaslo;
import pl.lodz.ssbd.entities.PoziomDostepu;
import pl.lodz.ssbd.entities.Uzytkownik;
import pl.lodz.ssbd.exceptions.PoprzednieHasloException;
import pl.lodz.ssbd.exceptions.PoziomDostepuException;
import pl.lodz.ssbd.exceptions.UzytkownikException;
import pl.lodz.ssbd.interceptors.DziennikZdarzenInterceptor;
import pl.lodz.ssbd.mok.facades.PoziomDostepuFacadeLocal;
import pl.lodz.ssbd.mok.facades.UzytkownikFacadeLocal;
import pl.lodz.ssbd.utils.MD5;

/**
 *
 * @author Robert Mielczarek 
 */
@Stateful
@Interceptors({DziennikZdarzenInterceptor.class})
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MOKEndpoint implements MOKEndpointLocal, SessionSynchronization {

    private static final Logger loger = Logger.getLogger(MOKEndpoint.class.getName());
    @EJB(beanName = "mokU")
    private UzytkownikFacadeLocal uzytkownikFacade;
    @EJB(beanName = "mokPD")
    private PoziomDostepuFacadeLocal poziomDostepuFacade;
    private List<Uzytkownik> uzytkownicyDostepowi;
    private Uzytkownik uzytkownikEdycja;
    private String hasloPrzedEdycja;
    private long IDTransakcji;
    private String IPOstPopZal;
    private Date CzasOstPopZal;
    private int IloscNPopZal;
    ResourceBundle rbl;
    
    public MOKEndpoint(){
        rbl = ResourceBundle.getBundle("nazwy_rol.role");
    }
    @Resource
    private SessionContext sessionContext;
    private SimpleDateFormat simpleDateHere = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss (Z)");

    @Override
    @PermitAll
    public void rejestrujUzytkownika(Uzytkownik nowyUzytkownik, String haslo) throws UzytkownikException {
        nowyUzytkownik.setHasloMd5(MD5.hash(haslo));
        String[] role = {rbl.getString("rola.admin"), rbl.getString("rola.user"), rbl.getString("rola.moderator")};
        for(String rola : role){
            PoziomDostepu poziomDost = new PoziomDostepu();
            if(rola.equals(rbl.getString("rola.user"))){
                poziomDost.setAktywny(true);
            } else {
                poziomDost.setAktywny(false);
            }
            poziomDost.setIdUzytkownik(nowyUzytkownik);
            poziomDost.setNazwa(rola);
            nowyUzytkownik.getPoziomDostepuList().add(poziomDost);
        }
        nowyUzytkownik.setAktywny(true);
        uzytkownikFacade.create(nowyUzytkownik);
    }

    @Override
    @RolesAllowed("WyswietlaniePaneluAdmina")
    public List<Uzytkownik> pobierzUzytkownikow(String wartosc) {
        uzytkownicyDostepowi = uzytkownikFacade.findByImieiNazwisko(wartosc);
        return uzytkownicyDostepowi;
    }

    @Override
    @RolesAllowed("AutoryzacjaKonta")
    public void potwierdzUzytkownika(Uzytkownik uzytkownik) throws UzytkownikException{
        Uzytkownik uz = null;
        if(uzytkownicyDostepowi.contains(uzytkownik)){
            uz = uzytkownicyDostepowi.get(uzytkownicyDostepowi.indexOf(uzytkownik));
        } else {
            throw new IllegalArgumentException("Modyfikowany uzytkownik niezgodny z wczytanym");
        }
        uz.setPotwierdzony(true);
        uzytkownikFacade.edit(uz);
    }

    @Override
    @RolesAllowed("BlokowanieOdblokowanieUzytkownia")
    public void zablokujUzytkownika(Uzytkownik uzytkownik) throws UzytkownikException {
        Uzytkownik uz = null;
        if(uzytkownicyDostepowi.contains(uzytkownik)){
            uz = uzytkownicyDostepowi.get(uzytkownicyDostepowi.indexOf(uzytkownik));
        } else {
            throw new IllegalArgumentException("Modyfikowany uzytkownik niezgodny z wczytanym");
        }
        uz.setAktywny(false);
        uzytkownikFacade.edit(uz);
    }

    @Override
    @RolesAllowed("BlokowanieOdblokowanieUzytkownia")
    public void odblokujUzytkownika(Uzytkownik uzytkownik) throws UzytkownikException {
        Uzytkownik uz = null;
        if(uzytkownicyDostepowi.contains(uzytkownik)){
            uz = uzytkownicyDostepowi.get(uzytkownicyDostepowi.indexOf(uzytkownik));
        } else {
            throw new IllegalArgumentException("Modyfikowany uzytkownik niezgodny z wczytanym");
        }
        uz.setAktywny(true);
        uzytkownikFacade.edit(uz);
    }

    @Override
    @PermitAll
    public void zalogujPoprawneUwierzytelnienie(String username, String IP) {
        Uzytkownik uzytkownik = uzytkownikFacade.findByLogin(username);
        this.IPOstPopZal = uzytkownik.getIpPopZal();
        this.CzasOstPopZal = uzytkownik.getCzasPopZal();
        this.IloscNPopZal = uzytkownik.getIloscNPopZal();
        uzytkownik.setCzasPopZal(new Date());
        uzytkownik.setIpPopZal(IP);
        if (uzytkownik.getAktywny()) {
            uzytkownik.setIloscNPopZal(0);
        }
    }

    @Override
    @PermitAll
    public void zalogujNiepoprawneUwierzytenienie(String username, String IP) {
        Uzytkownik uzytkownik = uzytkownikFacade.findByLogin(username);
        if (uzytkownik == null) {
            return;
        }
        int ilosc_niepoprawnych_zalogowan = uzytkownik.getIloscNPopZal();
        uzytkownik.setCzasNPopZal(new Date());
        if (ilosc_niepoprawnych_zalogowan == 2) {
            uzytkownik.setIloscNPopZal(ilosc_niepoprawnych_zalogowan + 1);
            uzytkownik.setAktywny(false);
        } else {
            uzytkownik.setIloscNPopZal(ilosc_niepoprawnych_zalogowan + 1);
        }
    }

    @Override
    @RolesAllowed({"ModyfikowanieDanychCudzegoKonta", "NadanieOdebraniePoziomuDostepu"})
    public Uzytkownik pobierzUzytkownikaDoEdycji(String login) {
        uzytkownikEdycja = uzytkownikFacade.findByLogin(login);
        hasloPrzedEdycja = uzytkownikEdycja.getHasloMd5();
        return uzytkownikEdycja;
    }

    @Override
    @RolesAllowed({"ModyfikowanieDanychSwojegoKonta","ModyfikowanieDanychCudzegoKonta"}) 
    public void zapiszKontoPoEdycji(Uzytkownik uzytkownik, boolean zmianaHasla, String haslo) throws UzytkownikException, PoprzednieHasloException {
        if (null == uzytkownikEdycja) {
            throw new IllegalArgumentException("Brak wczytanego uzytkownika do modyfikacji");
        }
        if (!uzytkownikEdycja.equals(uzytkownik)) {
            throw new IllegalArgumentException("Modyfikowany uzytkownik niezgodny z wczytanym");
        }
        if(zmianaHasla){
            uzytkownikEdycja.setHasloMd5(MD5.hash(haslo));
            if (!hasloPrzedEdycja.equals(uzytkownikEdycja.getHasloMd5())) {
                for(PoprzednieHaslo poprzednieHaslo: uzytkownikEdycja.getPoprzednieHasloList()){
                    if(uzytkownikEdycja.getHasloMd5().equals(poprzednieHaslo.getStareHasloMd5())){
                        throw new PoprzednieHasloException("exceptions.poprzedniehaslo.wykorzystywane");
                    }
                }
                PoprzednieHaslo popHaslo = new PoprzednieHaslo();
                popHaslo.setIdUzytkownik(uzytkownik);
                popHaslo.setStareHasloMd5(hasloPrzedEdycja);
                uzytkownik.getPoprzednieHasloList().add(popHaslo);
            } else {
                throw new PoprzednieHasloException("exceptions.poprzedniehaslo.aktualne");
            }
        }
        uzytkownikFacade.edit(uzytkownikEdycja);
        uzytkownikEdycja = null;
    }

    @Override
    @RolesAllowed("NadanieOdebraniePoziomuDostepu")
    public void nadajPoziom(PoziomDostepu poziom) throws PoziomDostepuException {
        PoziomDostepu pd = null;
        if(uzytkownikEdycja.getPoziomDostepuList().contains(poziom)){
            pd = uzytkownikEdycja.getPoziomDostepuList().get(uzytkownikEdycja.getPoziomDostepuList().indexOf(poziom));
        } else {
            throw new IllegalArgumentException("Modyfikowany poziom niezgodny z wczytanym");
        }
        pd.setAktywny(true);
        poziomDostepuFacade.edit(pd);

    }

    @Override
    @RolesAllowed("NadanieOdebraniePoziomuDostepu")
    public void odbierzPoziom(PoziomDostepu poziom) throws PoziomDostepuException {
        PoziomDostepu pd = null;
        if(uzytkownikEdycja.getPoziomDostepuList().contains(poziom)){
            pd = uzytkownikEdycja.getPoziomDostepuList().get(uzytkownikEdycja.getPoziomDostepuList().indexOf(poziom));
        } else {
            throw new IllegalArgumentException("Modyfikowany poziom niezgodny z wczytanym");
        }
        pd.setAktywny(false);
        poziomDostepuFacade.edit(pd);
    }

    @Override
    @RolesAllowed("Uwierzytelnianie")
    public Uzytkownik pobierzUzytkownika(String login) {
        return uzytkownikFacade.findByLogin(login);
    }
    
    @Override
    @RolesAllowed("ModyfikowanieDanychSwojegoKonta")
    public Uzytkownik pobierzSiebieDoEdycji(){
       String login = sessionContext.getCallerPrincipal().getName();
       uzytkownikEdycja = uzytkownikFacade.findByLogin(login);
       hasloPrzedEdycja=uzytkownikEdycja.getHasloMd5();
       return uzytkownikEdycja;
    }
    

    @Override
    public void afterBegin() throws EJBException, RemoteException {
        IDTransakcji = System.currentTimeMillis();
        loger.log(Level.INFO, simpleDateHere.format(new Date()).toString()+" || Transakcja o ID: " 
                + IDTransakcji + " zostala rozpoczeta ,przez użytkownika "
                + sessionContext.getCallerPrincipal().getName());
    }

    @Override
    public void beforeCompletion() throws EJBException, RemoteException {
        loger.log(Level.INFO, simpleDateHere.format(new Date()).toString()+" || Transakcja o ID: " + IDTransakcji 
                + " przed zakonczeniem przez użytownka "
                + sessionContext.getCallerPrincipal().getName());
    }

    @Override
    public void afterCompletion(boolean committed) throws EJBException, RemoteException {
        loger.log(Level.INFO, simpleDateHere.format(new Date()).toString()+" || Transakcja o ID: " + IDTransakcji 
                + " zostala zakonczona przez: "
                + (committed ? "zatwierdzenie" : "wycofanie") + " przez użytkownia"
                + sessionContext.getCallerPrincipal().getName());
    }

    @Override
    public String pobierzIPOstatniegoPopZalogowania() {
        return this.IPOstPopZal;
    }

    @Override
    public Date pobierzCzasOstatniegoPopZalogowania() {
        return this.CzasOstPopZal;
    }

    @Override
    public int pobierzIloscNPopZal() {
        return this.IloscNPopZal;
    }
}
