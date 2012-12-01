/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleformat.mclgraph;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import simpleformat.Class;

/**
 *
 * @author saricas
 */
public class MclResultReader {

    private HashMap<String, Integer> clusters;
    private File file;
    private List<Class> classList;

    public MclResultReader(File file, List<Class> classList) {
        this.file = file;
        this.classList = classList;
    }

    private Class classByTag(int tag) {
        for (Class c : classList) {
            if (c.getTag() == tag) {
                return c;
            }
        }
        return null;
    }

    public HashMap<String, Integer> read() throws FileNotFoundException, IOException {
        clusters = new HashMap<String, Integer>();

        FileInputStream fstream = new FileInputStream(file);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        while ((strLine = br.readLine()) != null) {
            if (strLine.contains("begin")) {
                break;
            }
        }

        while ((strLine = br.readLine()) != null) {
            if (strLine.contains(")")) {
                break;
            }

            String[] stringArray = strLine.split("\\s+");
            try {
                Integer clusterNumber = Integer.parseInt(stringArray[0]);
                for (int i = 1; i < stringArray.length; i++) {
                    int tag = Integer.parseInt(stringArray[i]);
                    Class c = classByTag(tag);
                    if (c != null) {
                        clusters.put(classByTag(tag).getName(), clusterNumber);
                    }
                }
            } catch (NumberFormatException ex) {
                continue;
            }
        }

        in.close();
        return clusters;
    }
}
