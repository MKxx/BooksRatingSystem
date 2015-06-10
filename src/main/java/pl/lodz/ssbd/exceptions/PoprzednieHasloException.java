/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.exceptions;

/**
 * Wyjątek jest rzucany wtedy gdy próbujemy ustawic haslo, na jedno z wczesniej wykorzystywanych hasel.
 * @author Robert Mielczarek <180640@edu.p.lodz.pl>
 */
public class PoprzednieHasloException extends SSBD05Exception {

    /**
     * Creates a new instance of <code>PoprzednieHasloException</code> without
     * detail message.
     */
    public PoprzednieHasloException() {
    }

    /**
     * Constructs an instance of <code>PoprzednieHasloException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public PoprzednieHasloException(String msg) {
        super(msg);
    }
}
