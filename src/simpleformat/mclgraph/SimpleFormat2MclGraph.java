/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleformat.mclgraph;

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
public class SimpleFormat2MclGraph {

    private File file;

    public SimpleFormat2MclGraph(File file) {
        this.file = file;
    }

    /**
     * 
     * @param outfile
     * @throws FileNotFoundException
     * @throws IOException
     * @throws FileFormatNotSupportedException 
     * 
     *  (mclheader
     *  mcltype matrix
     *  dimensions 12x12
     *  )
     *  (mclmatrix
     *  begin
     *  0    1  5  6  9 $
     *  1    0  2  4 $
     *  2    1  3  4 $
     *  3    2  7  8 10 $
     *  4    1  2  6  7 $
     *  5    0  9 $
     *  6    0  4  9 $
     *  7    3  4  8 10 $
     *  8    3  7 10 11 $
     *  9    0  5  6 $
     *  10   3  7  8 11 $
     *  11   8 10 $
     *  )
     */
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
        
        // print mcl header
        wr.write("(mclheader");
        wr.newLine();
        wr.write("mcltype matrix");
        wr.newLine();
        wr.write("dimensions ");
        wr.write(String.valueOf(vertexCount) + "x" + String.valueOf(vertexCount));
        wr.newLine();
        wr.write(")");
        wr.newLine();
        wr.write("(mclmatrix");
        wr.newLine();
        wr.write("begin");
        wr.newLine();
        for (Class klass : classList) {
            if (klass.getOutList().size() > 0) {
                wr.write(klass.getTag() + "  ");
                for (Class c : klass.getOutList()) {
                    wr.write(c.getTag() + " ");
                }
                wr.write("$");
                wr.newLine();
            }
        }
        wr.write(")");
        wr.newLine();
        wr.close();
    }
}
