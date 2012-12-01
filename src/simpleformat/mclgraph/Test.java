/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleformat.mclgraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import simpleformat.FileFormatNotSupportedException;
import simpleformat.clutograph.SimpleFormat2ClutoGraph;
import simpleformat.Class;

/**
 *
 * @author saricas
 */
public class Test {

    public static void main(String[] args) {

        File mciResultFile = new File("./test/mci/antlrworks_bo_12.mci.I20");
        
        List<Class> classList = new ArrayList<Class>();
        Class c1 = new Class("c1");
        c1.setTag(115);
        Class c2 = new Class("c2");
        c2.setTag(116);
        Class c3 = new Class("c3");
        c3.setTag(28);
        classList.add(c1);
        classList.add(c2);
        classList.add(c3);
        
        HashMap<String, Integer> clusters = new HashMap<String, Integer>();
        
        MclResultReader mclReader = new MclResultReader(mciResultFile, classList);
        try {
            clusters = mclReader.read();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
