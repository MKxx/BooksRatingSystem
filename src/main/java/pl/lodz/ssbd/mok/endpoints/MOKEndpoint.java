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
import pl.lodz.ssbd.exceptions.PoziomDostepuException;
import pl.lodz.ssbd.exceptions.UzytkownikException;
import pl.lodz.ssbd.interceptors.DziennikZdarzenInterceptor;
import pl.lodz.ssbd.mok.facades.PoziomDostepuFacadeLocal;
import pl.lodz.ssbd.mok.facades.UzytkownikFacadeLocal;

/**
 *
 * @author Robert Mielczarek <180640@edu.p.lodz.pl>
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
    public void rejestrujUzytkownika(Uzytkownik nowyUzytkownik) throws UzytkownikException {
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
        return uzytkownikFacade.findByImieiNazwisko(wartosc);
    }

    @Override
    @RolesAllowed("AutoryzacjaKonta")
    public void potwierdzUzytkownika(Uzytkownik uzytkownik) {
        Uzytkownik u = uzytkownikFacade.find(uzytkownik.getIdUzytkownik());
        u.setPotwierdzony(true);
    }

    @Override
    @RolesAllowed("BlokowanieOdblokowanieUzytkownia")
    public void zablokujUzytkownika(Uzytkownik uzytkownik) {
        Uzytkownik u = uzytkownikFacade.find(uzytkownik.getIdUzytkownik());
        u.setAktywny(false);
    }

    @Override
    @RolesAllowed("BlokowanieOdblokowanieUzytkownia")
    public void odblokujUzytkownika(Uzytkownik uzytkownik) {
        Uzytkownik u = uzytkownikFacade.find(uzytkownik.getIdUzytkownik());
        u.setAktywny(true);
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
    @RolesAllowed("ModyfikowanieDanychCudzegoKonta")
    public Uzytkownik pobierzUzytkownikaDoEdycji(String login) {
        uzytkownikEdycja = uzytkownikFacade.findByLogin(login);
        hasloPrzedEdycja = uzytkownikEdycja.getHasloMd5();
        System.out.println(uzytkownikEdycja.getImie());
        return uzytkownikEdycja;
    }

    @Override
    @RolesAllowed({"ModyfikowanieDanychSwojegoKonta","ModyfikowanieDanychCudzegoKonta"}) 
    public void zapiszKontoPoEdycji(Uzytkownik uzytkownik, boolean zmianaHasla) throws UzytkownikException {
        if (null == uzytkownikEdycja) {
            throw new IllegalArgumentException("Brak wczytanego uzytkownika do modyfikacji");
        }
        if (!uzytkownikEdycja.equals(uzytkownik)) {
            throw new IllegalArgumentException("Modyfikowany uzytkownik niezgodny z wczytanym");
        }
        if(zmianaHasla){
            if (!hasloPrzedEdycja.equals(uzytkownikEdycja.getHasloMd5())) {
                for(PoprzednieHaslo poprzednieHaslo: uzytkownikEdycja.getPoprzednieHasloList()){
                    if(uzytkownikEdycja.getHasloMd5().equals(poprzednieHaslo.getStareHasloMd5())){
                        throw new IllegalArgumentException("Haslo to juz bylo wykorzystywane");
                    }
                }
                PoprzednieHaslo popHaslo = new PoprzednieHaslo();
                popHaslo.setIdUzytkownik(uzytkownik);
                popHaslo.setStareHasloMd5(hasloPrzedEdycja);
                uzytkownik.getPoprzednieHasloList().add(popHaslo);
            } else {
                throw new IllegalArgumentException("Zmiana na to samo hasło");
            }
        }
        uzytkownikFacade.edit(uzytkownikEdycja);
        uzytkownikEdycja = null;
    }

    @Override
    @RolesAllowed("NadanieOdebraniePoziomuDostepu")
    public void nadajPoziom(PoziomDostepu poziom) throws PoziomDostepuException {
        poziom.setAktywny(true);
        poziomDostepuFacade.edit(poziom);

    }

    @Override
    @RolesAllowed("NadanieOdebraniePoziomuDostepu")
    public void odbierzPoziom(PoziomDostepu poziom) throws PoziomDostepuException {
        poziom.setAktywny(false);
        poziomDostepuFacade.edit(poziom);
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
