/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sdmetrics.weka;

import java.io.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;

/**
 *
 * @author saricas
 */
public class ARFFWriter {

    String filePath = "";
    Instances instances = null;

    public ARFFWriter(String arffFilePath) throws IOException {
        // change .xml to .arff
        filePath = arffFilePath;
        // create Arff object from template
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        ArffReader arff = new ArffReader(reader);
        instances = arff.getData();
        instances.setRelationName(filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".")));
    }

    public void addData(Node node, String type) {
        Instance instance = new Instance(instances.numAttributes());
        NodeList nodeList = node.getOwnerDocument().getElementsByTagName("Data");
        for (int i = 33; i < nodeList.getLength(); i++) {
            instance.setDataset(instances);
            // <Cell><Data ss:Type="String">Name</Data></Cell>
            if (i % 33 == 0)
                instance.setValue((Attribute) instance.attribute(i%33),
                        nodeList.item(i).getFirstChild().getNodeValue());
            else
                instance.setValue((Attribute) instance.attribute(i%33),
                        Double.parseDouble(nodeList.item(i).getFirstChild().getNodeValue()));
        }
        instance.setValue((Attribute) instance.attribute(33), type);
        instances.add(instance);
    }

    public void write() throws IOException {
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setFile(new File(filePath));
        arffSaver.setInstances(instances);
        arffSaver.writeBatch();
    }
}
