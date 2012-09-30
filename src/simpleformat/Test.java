/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleformat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sinan
 */
public class Test {

    public static void main(String[] args) {

        File txtFile = new File("./test/txt/scout.txt");
        simpleformat.Tokenizer tokenizer = new simpleformat.Tokenizer(txtFile);

        try {

            List<simpleformat.Class> klasses = tokenizer.tokenize();

            AuthorityClassFinder authorityClassFinder =
                    new AuthorityClassFinder(klasses, tokenizer.getEdgeCount(), (float) 0.03);
            List<simpleformat.Class> authorityList = authorityClassFinder.find();
            authorityList.size();

            HubClassFinder hubClassFinder =
                    new HubClassFinder(klasses, tokenizer.getEdgeCount(), (float) 0.03);
            List<simpleformat.Class> hubList = hubClassFinder.find();
            hubList.size();

            CycleClassFinder cycleClassFinder = new CycleClassFinder(klasses);
            List<simpleformat.Class> cycleList = cycleClassFinder.find();
            cycleList.size();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileFormatNotSupportedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
