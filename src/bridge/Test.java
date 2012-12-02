/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bridge;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author saricas
 */
public class Test {
    
    public static void main(String[] args) throws IOException {
        
        File graphFile = new File("./test/graph/testbridge.graph");
        MyClust bridgeCluster = new MyClust(graphFile.getCanonicalPath(), "1", "2", "1");
        bridgeCluster.getBridgeNodes();
    }
    
}
