/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simpleformat2clutograph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import simpleformat.FileFormatNotSupportedException;

/**
 *
 * @author sinan
 */
public class Test {

    public static void main(String[] args) {

        File inFile = new File("./test/txt/xmi2graphml.txt");
        File outFile = new File("./test/graph/xmi2graphml.graph");

        SimpleFormat2ClutoGraph sf2cg = new SimpleFormat2ClutoGraph(inFile);
        
        try {
            sf2cg.write(outFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileFormatNotSupportedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
