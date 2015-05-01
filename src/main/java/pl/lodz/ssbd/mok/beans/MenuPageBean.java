/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.mok.beans;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import pl.lodz.ssbd.utils.SprawdzaczRoli;

/**
 *
 * @author Maciej
 */
@Named(value = "menuPageBean")
@RequestScoped
public class MenuPageBean {
    /**
     * Creates a new instance of MenuPageBean
     */
    public MenuPageBean() {
    }
    
    public boolean getIsGosc(){
        if(FacesContext.getCurrentInstance().getExternalContext().getRemoteUser() == null){
            System.out.println("nie ma usera");
            return true;
        }
        return false;
    }
    
    public boolean getIsAdmin(){
        return SprawdzaczRoli.sprawdzRole("ADMINISTRATOR");
    }
    
    public boolean getIsUzytkownik(){
        return SprawdzaczRoli.sprawdzRole("UZYTKOWNIK");
    }
    
    public boolean getIsModerator(){
        return SprawdzaczRoli.sprawdzRole("MODERATOR");
    }
}
