/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bridge;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author saricas
 */
public class Test {
    
    public static void main(String[] args) throws IOException {
        
        File graphFile = new File("./test/graph/testbridge.graph");
        MyClust bridgeCluster = new MyClust(graphFile.getCanonicalPath(), "1", "2", "1");
        Map<Integer[], Integer[]> bridgeMap = bridgeCluster.getBridgeNodes();
        for (Integer[] bridges : bridgeMap.values()) {
            if (bridges.length > 2) {
                System.out.println(bridges.length);
            }
        }
    }
    
}
