/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.moks.facades;

import pl.lodz.ssbd.moks.*;
import pl.lodz.ssbd.facades.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pl.lodz.ssbd.entities.Uzytkownik;

/**
 *
 * @author Robert Mielczarek <180640@edu.p.lodz.pl>
 */
@Stateless
public class UzytkownikFacade extends AbstractFacade<Uzytkownik> implements UzytkownikFacadeLocal {
    @PersistenceContext(unitName = "ssbd05moks")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UzytkownikFacade() {
        super(Uzytkownik.class);
    }
    
}
