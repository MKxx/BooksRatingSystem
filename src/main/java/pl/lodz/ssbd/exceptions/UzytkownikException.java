/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.exceptions;

/**
 * Wyjątek rzucany, podczas toworzenia lub edycji użytkownika, gdy jeden z parametrów jest nie do przyjęcia.
 * @author Robert Mielczarek 
 */
public class UzytkownikException extends SSBD05Exception {

    /**
     * Creates a new instance of <code>UzytkownikException</code> without detail
     * message.
     */
    public UzytkownikException() {
    }

    /**
     * Constructs an instance of <code>UzytkownikException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UzytkownikException(String msg) {
        super(msg);
    }
}
