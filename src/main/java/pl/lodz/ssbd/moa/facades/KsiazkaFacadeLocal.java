/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.moa.facades;


import java.util.List;
import javax.ejb.Local;
import pl.lodz.ssbd.entities.Ksiazka;
import pl.lodz.ssbd.exceptions.KsiazkaException;

/**
 *
 * @author Marta
 */
@Local
public interface KsiazkaFacadeLocal {

    void create(Ksiazka ksiazka) throws KsiazkaException;

    void edit(Ksiazka ksiazka) throws KsiazkaException;

    Ksiazka find(Object id);

    List<Ksiazka> findAll();
    
    List<Ksiazka> findNieocenione();
    
}
