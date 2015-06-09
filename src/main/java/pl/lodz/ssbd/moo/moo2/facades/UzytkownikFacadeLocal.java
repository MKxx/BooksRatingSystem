/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.moo.moo2.facades;

import java.util.List;
import pl.lodz.ssbd.entities.Uzytkownik;
import pl.lodz.ssbd.exceptions.UzytkownikException;

/**
 *
 * @author Robert Mielczarek <180640@edu.p.lodz.pl>
 */
//@Local
public interface UzytkownikFacadeLocal {

    void create(Uzytkownik uzytkownik) throws UzytkownikException;

    void edit(Uzytkownik uzytkownik) throws UzytkownikException;

    Uzytkownik find(Object id);

    List<Uzytkownik> findAll();

    List<Uzytkownik> findRange(int[] range);

    int count();
    
    public Uzytkownik findByLogin(String username) throws UzytkownikException;
    
}
