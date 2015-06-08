/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.lodz.ssbd.exceptions;

/**
 *
 * @author Robert Mielczarek <180640@edu.p.lodz.pl>
 */
public class PoziomDostepuException extends SSBD05Exception {

    /**
     * Creates a new instance of <code>PoziomDostepuException</code> without
     * detail message.
     */
    public PoziomDostepuException() {
    }

    /**
     * Wjątek rzucany w przypadku problemów z edycją poziomu dostępu.
     *
     * @param msg the detail message.
     */
    public PoziomDostepuException(String msg) {
        super(msg);
    }
}
