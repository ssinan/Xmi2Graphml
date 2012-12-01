/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleformat.mclgraph;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author saricas
 */
public class MclCluster {
    
    private String mclExecutablePath = "/usr/local/bin/mcl";
    private String fileName;
    private File clusterResultFile;
    
    public MclCluster(String fileName) {
        this.fileName = fileName;
    }
    
    public File cluster() throws IOException, InterruptedException {
        // create cluster file mciFileName.I20
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(mclExecutablePath + " " + fileName + " -o " + fileName + ".I20");
        p.waitFor();
        clusterResultFile = new File(fileName + ".I20");
        return clusterResultFile;
    }
    
}
