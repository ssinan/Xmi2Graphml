/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleformat2clutograph;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import simpleformat.Class;
import simpleformat.FileFormatNotSupportedException;
import simpleformat.Tokenizer;

/**
 *
 * @author sinan
 */
public class SimpleFormat2ClutoGraph {

    private File file;

    public SimpleFormat2ClutoGraph(File file) {
        this.file = file;
    }

    public void write(File outfile) throws FileNotFoundException,
            IOException, FileFormatNotSupportedException {

        Tokenizer tokenizer = new Tokenizer(file);
        List<Class> classList = tokenizer.tokenize();

        int edgeCount = tokenizer.getEdgeCount();
        int vertexCount = classList.size();

        // set integer tags since cluto works with ints
        for (int i = 0; i < classList.size(); i++) {
            classList.get(i).setTag(i + 1);
        }

        FileOutputStream ostream = new FileOutputStream(outfile);
        DataOutputStream out = new DataOutputStream(ostream);
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));

        wr.write(String.valueOf(vertexCount) + " " + String.valueOf(edgeCount));

        for (int i = 0; i < classList.size(); i++) {
            wr.newLine();
            // only use in or out list
            for (Class c :  classList.get(i).getOutList()) {
                wr.append(String.valueOf(c.getTag()));
                wr.append(" 1 ");
            }
        }

        wr.newLine();
        wr.close();
    }
}
