/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hswgt.teachingbox.core.exceptions;

public class IllegalConfigurationException extends IllegalStateException {

    Throwable cause;

    public IllegalConfigurationException(){
	super();
    }

    public IllegalConfigurationException(String message){
	super(message);
    }

    public IllegalConfigurationException(Throwable t){
	cause = t;
    }

    public Throwable getCause(){
	return cause;
    }
}