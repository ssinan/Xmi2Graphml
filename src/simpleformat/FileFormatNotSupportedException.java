/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleformat;

/**
 *
 * @author sinan
 */
public class FileFormatNotSupportedException extends Exception {

    public FileFormatNotSupportedException() {}
    
    public FileFormatNotSupportedException(String message) {
        super(message);
    }

}
