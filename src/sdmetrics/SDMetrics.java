/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sdmetrics;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import main.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import simpleformat.Class;

/**
 *
 * @author saricas
 */
public class SDMetrics {
    
    public static int ISLAND_METRICS_PER_CLASS = 0;
    public static int ISLAND_METRICS_AVERAGE = 1;
    
    private int islandThreshold = 5;
    private String pathToAuthorityXml = "auth.xml";
    private String pathToHubXml = "hub.xml";
    private String pathToCycleXml = "cycle.xml";
    private String pathToAllXml = "all.xml";
    private String pathToIslandXml = "";
    
    private String fileName;
    private String pathToSDMetricsjar;
    private File metricsFile;
    
    private List<Class> authorityList;
    private List<Class> hubList;
    private List<Class> cycleList;
    private List<Class> godList;
    
    public SDMetrics(String pathToSDMetricsjar, String fileName) throws UnsupportedEncodingException {
        this.pathToSDMetricsjar = pathToSDMetricsjar;
        this.fileName = fileName;
        String path = SDMetrics.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        pathToAuthorityXml = decodedPath + pathToAuthorityXml;
        pathToHubXml = decodedPath + pathToHubXml;
        pathToCycleXml = decodedPath + pathToCycleXml;
        pathToAllXml = decodedPath + pathToAllXml;
        String name = fileName.substring(fileName.lastIndexOf("/"), fileName.length());
        pathToIslandXml = decodedPath + name + "_Island.xml";
    }
    
    public void clearMetricsFiles() throws IOException {
        Util.deleteFile(new File(pathToAllXml));
        Util.deleteFile(new File(pathToAuthorityXml));
        Util.deleteFile(new File(pathToHubXml));
        Util.deleteFile(new File(pathToCycleXml));
        Util.copy("/sdmetrics/excel/template.xml", pathToAllXml);
        Util.copy("/sdmetrics/excel/template.xml", pathToAuthorityXml);
        Util.copy("/sdmetrics/excel/template.xml", pathToHubXml);
        Util.copy("/sdmetrics/excel/template.xml", pathToCycleXml);        
    }
    
    public File calculateMetrics() throws IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        // java -jar SDMetrics.jar -xmi projects/antlrworks-1.4.3/xmi/antlrworks_bo_12.xmi -f xml projects/antlrworks-1.4.3/xmi/antlrworks_bo_12.xml
        Process p1 = r.exec("java -jar " + pathToSDMetricsjar + " -xmi " + fileName + ".xmi -f xml " +  fileName + ".xml");
        p1.waitFor();
        metricsFile = new File(fileName + "_Class.xml"); 
        return metricsFile;
    }
    
    public void writeMetricsOfClasses(List<Class> authorityList, List<Class> hubList, List<Class> cycleList) 
            throws ParserConfigurationException, SAXException, IOException, InterruptedException, TransformerConfigurationException, TransformerException {
        // search for authority-hub-cycle class metrics
        this.calculateMetrics();
        Document metricsDoc = Util.getDocumentFromFile(metricsFile);          

        File authFile = new File(pathToAuthorityXml);
        Document authDoc = Util.getDocumentFromFile(authFile);

        File hubFile = new File(pathToHubXml);
        Document hubDoc = Util.getDocumentFromFile(hubFile);

        File cycleFile = new File(pathToCycleXml);
        Document cycleDoc = Util.getDocumentFromFile(cycleFile);

        File allFile = new File(pathToAllXml);
        Document allDoc = Util.getDocumentFromFile(allFile);

        NodeList nodeList = metricsDoc.getElementsByTagName("Data");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            Node attr = node.getAttributes().item(0);
            if (!"ss:Type".equals(attr.getNodeName()) || !"String".equals(attr.getNodeValue()))
                continue;

            if (i > 33)
                addNodeToDocument(node, allDoc);
            
            if(!searchForMetrics(authorityList, node, authDoc)) {
                if (!searchForMetrics(hubList, node, hubDoc)) {
                    searchForMetrics(cycleList, node, cycleDoc);
                }
            }
        }

        // write found metric values
        TransformerFactory transFact = TransformerFactory.newInstance();
        Transformer xmlTrans = transFact.newTransformer();
        xmlTrans.transform(new DOMSource(authDoc), new StreamResult(authFile));
        xmlTrans.transform(new DOMSource(hubDoc), new StreamResult(hubFile));
        xmlTrans.transform(new DOMSource(cycleDoc), new StreamResult(cycleFile));
        xmlTrans.transform(new DOMSource(allDoc), new StreamResult(allFile));
    }
    
    private void addNodeToDocument(Node node, Document doc)
    {
        if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE)
        {
            String value = node.getFirstChild().getNodeValue();
            if (!"Name".equals(value))
            {
                NodeList nodeList = doc.getElementsByTagName("Table");
                Node importedNode = doc.importNode(node.getParentNode().getParentNode(), true);
                nodeList.item(0).appendChild(importedNode);
            }
        }
    }    
    
    private boolean searchForMetrics(List<Class> list, Node node, Document doc) {
        if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
            String value = node.getFirstChild().getNodeValue();
            for (Class c : list) {
                int index = value.indexOf("." + c.getName());
                if (index > 0 && (index + c.getName().length() + 1) == value.length()) {
                    NodeList nodeList = doc.getElementsByTagName("Table");
                    Node importedNode = doc.importNode(node.getParentNode().getParentNode(), true);
                    nodeList.item(0).appendChild(importedNode);
                    return true;
                }
            }
        }
        return false;
    }

    public void writeMetricsOfIslands(HashMap<String, Integer> map, int mode, boolean filterSpecialClasses)
            throws IOException, InterruptedException, ParserConfigurationException, 
            SAXException, TransformerConfigurationException, TransformerException, SDMetricsConfigurationException {
        
        if (filterSpecialClasses && (authorityList == null || hubList == null || cycleList == null || godList == null))
            throw new SDMetricsConfigurationException();
        
        this.calculateMetrics();
        Document metricsDoc = Util.getDocumentFromFile(metricsFile);

        HashMap<Integer, List<Node>> clusters = new HashMap<Integer, List<Node>>();
        NodeList nodeList = metricsDoc.getElementsByTagName("Data");

        for (int i = 34; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Node attr = node.getAttributes().item(0);
            if (!"ss:Type".equals(attr.getNodeName()) || !"String".equals(attr.getNodeValue())) {
                continue;
            }

            if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                String value = node.getFirstChild().getNodeValue();
                String reducedName = value.substring(value.lastIndexOf(".") + 1);

                if (map.containsKey(reducedName)) {

                    if (filterSpecialClasses && !authorityList.contains(new Class(reducedName)) && !hubList.contains(new Class(reducedName))
                            && !cycleList.contains(new Class(reducedName)) && !godList.contains(new Class(reducedName))) {

                        int clusterNumber = map.get(reducedName);
                        node.getFirstChild().setNodeValue(String.valueOf(clusterNumber) + " " + value);
                        if (clusters.containsKey(clusterNumber)) {
                            List<Node> clist = clusters.get(clusterNumber);
                            clist.add(node);
                        } else {
                            List<Node> clist = new ArrayList<Node>();
                            clist.add(node);
                            clusters.put(clusterNumber, clist);
                        }
                    }
                }
            }
        }
        
        File islandFile = new File(pathToIslandXml);
        Util.createFile(islandFile);
        Util.copy("/sdmetrics/excel/template.xml", pathToIslandXml);  
        Document islandDoc = Util.getDocumentFromFile(islandFile);
        
        if (mode == ISLAND_METRICS_PER_CLASS) {
            // write metrics per class
            for (Integer key : clusters.keySet()) {
                List<Node> clist = clusters.get(key);
                if (clist.size() > islandThreshold) {
                    NodeList islandNodeList = islandDoc.getElementsByTagName("Table");
                    for (Node node : clist) {
                        Node importedNode = islandDoc.importNode(node.getParentNode().getParentNode(), true);
                        islandNodeList.item(0).appendChild(importedNode);
                    }
                    Node emptyNode = getEmptyNode(clist.get(0).getParentNode().getParentNode());
                    Node importedNode = islandDoc.importNode(emptyNode, true);
                    islandNodeList.item(0).appendChild(importedNode);
                }
            }
        } else if (mode == ISLAND_METRICS_AVERAGE) {
            // write island metrics by taking average values among class metrics in an island
            for (Integer key : clusters.keySet()) {
                List<Node> clist = clusters.get(key);
                if (clist.size() > islandThreshold) {
                    NodeList islandNodeList = islandDoc.getElementsByTagName("Table");
                    String name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
                    clist.get(0).getFirstChild().setNodeValue(name + " - island " + String.valueOf(key));
                    Node rootNode = clist.get(0).getParentNode().getParentNode();
                    for (int i = 1; i < clist.size(); i++) {
                        rootNode = calcAverageMetrics(rootNode, clist.get(i).getParentNode().getParentNode(), i + 1);
                    }
                    Node importedNode = islandDoc.importNode(rootNode, true);
                    islandNodeList.item(0).appendChild(importedNode);
                }
            }
        }

        // write found metric values
        TransformerFactory transFact = TransformerFactory.newInstance();
        Transformer xmlTrans = transFact.newTransformer();
        xmlTrans.transform(new DOMSource(islandDoc), new StreamResult(islandFile));
    }
    
    private Node getEmptyNode(Node n1) {
        for (int i=1; i<=65; i=i+2) {
            n1.getChildNodes().item(i).getFirstChild().getFirstChild().setNodeValue("");
        }
        return n1;
    }
    
    private Node calcAverageMetrics(Node n1, Node n2, int size) {
        for (int i=3; i<=65; i=i+2) {
            float m1 = Float.parseFloat(n1.getChildNodes().item(i).getFirstChild().getFirstChild().getNodeValue());
            float m2 = Float.parseFloat(n2.getChildNodes().item(i).getFirstChild().getFirstChild().getNodeValue());
            float average = ((m1 * (size - 1)) + m2) / size; 
            n1.getChildNodes().item(i).getFirstChild().getFirstChild().setNodeValue(String.valueOf(average));
        }
        return n1;
    }

    /**
     * @return the islandThreshold
     */
    public int getIslandThreshold() {
        return islandThreshold;
    }

    /**
     * @param islandThreshold the islandThreshold to set
     */
    public void setIslandThreshold(int islandThreshold) {
        this.islandThreshold = islandThreshold;
    }

    /**
     * @return the authorityList
     */
    public List<Class> getAuthorityList() {
        return authorityList;
    }

    /**
     * @param authorityList the authorityList to set
     */
    public void setAuthorityList(List<Class> authorityList) {
        this.authorityList = authorityList;
    }

    /**
     * @return the hubList
     */
    public List<Class> getHubList() {
        return hubList;
    }

    /**
     * @param hubList the hubList to set
     */
    public void setHubList(List<Class> hubList) {
        this.hubList = hubList;
    }

    /**
     * @return the cycleList
     */
    public List<Class> getCycleList() {
        return cycleList;
    }

    /**
     * @param cycleList the cycleList to set
     */
    public void setCycleList(List<Class> cycleList) {
        this.cycleList = cycleList;
    }

    /**
     * @return the godList
     */
    public List<Class> getGodList() {
        return godList;
    }

    /**
     * @param godList the godList to set
     */
    public void setGodList(List<Class> godList) {
        this.godList = godList;
    }
    
}
