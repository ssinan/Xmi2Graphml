
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import simpleformat.*;
import simpleformat2clutograph.SimpleFormat2ClutoGraph;
import simpleformat2mclgraph.MclResultReader;
import simpleformat2mclgraph.SimpleFormat2MclGraph;
import simpleformat.Class;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author saricas
 */
public class Program {

    public static void main(String[] args) 
            throws TransformerConfigurationException, TransformerException, IOException, 
            FileNotFoundException, FileFormatNotSupportedException, ParserConfigurationException,
            SAXException, InterruptedException {
        
        if (args.length != 10) {
            System.err.println("Usage:");
            System.err.println("  java -jar Xmi2Graphml.jar xmiFileName"
                    + " authorityThreshold(float) hubThreshold(float) pathToSDMetrics.jar pathToAuthorityXml pathToHubXml pathToCycleXml pathToAllXml pathToFilteredXml pathToIslandXml");
            System.exit(1);
        }
        
        File xmiFile = new File("./" + args[0]);
        InputStream xslGraphml = 
                Program.class.getResourceAsStream("/xmi2graphml/xmi2graphml.xsl");
        InputStream xslSimpleFormat = 
                Program.class.getResourceAsStream("/xmi2simpleformat/xmi2simpleformat.xsl");

        Source xmiSource = new StreamSource(xmiFile);
        Source xsltGraphml = new StreamSource(xslGraphml);
        Source xsltSimpleFormat = new StreamSource(xslSimpleFormat);
        
        String fileName = args[0].substring(0, args[0].indexOf(".xmi"));
        File graphml = new File("./" + fileName + ".graphml");
        File simpleFormat = new File("./" + fileName + ".smpl");

        Result resultGraphml = new StreamResult(graphml);
        Result resultSimpleFormat = new StreamResult(simpleFormat);
        
        TransformerFactory transFact = TransformerFactory.newInstance();
        Transformer transGraphml = transFact.newTransformer(xsltGraphml);
        Transformer transSimpleFormat = transFact.newTransformer(xsltSimpleFormat);

        transGraphml.setOutputProperty(OutputKeys.INDENT, "yes");
        transGraphml.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        
        transSimpleFormat.setOutputProperty(OutputKeys.INDENT, "yes");
        transSimpleFormat.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        
        if (createFile(graphml)) {
            System.out.println("Creating graphml file...");
            transGraphml.transform(xmiSource, resultGraphml);
            System.out.println("Transformation completed. Output file: " + graphml.getCanonicalPath());
        }
        
        if (createFile(simpleFormat)) {
            System.out.println("Creating simpleformat file...");
            transSimpleFormat.transform(xmiSource, resultSimpleFormat);
            System.out.println("Transformation completed. Output file: " + simpleFormat.getCanonicalPath());
        }
        
        // create sparse graph matrix file for cluto clustering tool
        File graphFile = new File("./" + fileName + ".graph");
        if (createFile(graphFile)) {
            System.out.println("Creating graph file...");
            SimpleFormat2ClutoGraph sf2cg = new SimpleFormat2ClutoGraph(simpleFormat);
            sf2cg.write(graphFile);
            System.out.println("Transformation completed. Output file: " + graphFile.getCanonicalPath());
        }        
        
        // create graph matrix file for mcl clustering tool
        File mciFile = new File("./" + fileName + ".mci");
        if (createFile(mciFile)) {
            System.out.println("Creating mci file...");
            SimpleFormat2MclGraph sf2mg = new SimpleFormat2MclGraph(simpleFormat);
            sf2mg.write(mciFile);
            System.out.println("Transformation completed. Output file: " + mciFile.getCanonicalPath());
        }    
        
        // calculate authority, hub and cycle classes from simpleformat
        System.out.println("Calculating authority, hub and cycle classes.");
        Tokenizer tokenizer = new Tokenizer(simpleFormat);
        List<simpleformat.Class> klasses = tokenizer.tokenize();
        
        File resultsFile = new File("./" + fileName + ".txt");
        createFile(resultsFile);
        FileOutputStream ostream = new FileOutputStream(resultsFile);
        DataOutputStream out = new DataOutputStream(ostream);
        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));
        wr.write("Authority parameter: " + String.valueOf(Float.parseFloat(args[1])));
        wr.newLine();
        wr.write("Hub parameter: " + String.valueOf(Float.parseFloat(args[2])));
        wr.newLine();
        wr.newLine();

        AuthorityClassFinder authorityClassFinder =
                new AuthorityClassFinder(klasses, tokenizer.getEdgeCount(), Float.parseFloat(args[1]));
        List<simpleformat.Class> authorityList = authorityClassFinder.find();

        HubClassFinder hubClassFinder =
                new HubClassFinder(klasses, tokenizer.getEdgeCount(), Float.parseFloat(args[2]));
        List<simpleformat.Class> hubList = hubClassFinder.find();
        
        // filter out god classes
        List<simpleformat.Class> godList = new ArrayList<simpleformat.Class>();
        for (int i=0; i < authorityList.size(); i++)
        {
            simpleformat.Class c = authorityList.get(i);
            if (hubList.contains(c)) {
                c.setType(simpleformat.Class.GOD);
                authorityList.remove(c);
                hubList.remove(c);
                godList.add(c);
                i--;
            }
        }
        
        printResults("God", godList, wr);
        printResults("Authority", authorityList, wr);        
        printResults("Hub", hubList, wr);

        CycleClassFinder cycleClassFinder = new CycleClassFinder(klasses);
        List<simpleformat.Class> cycleList = cycleClassFinder.find();
        printResults("Cycle", cycleList, wr);

        wr.close();
        
        // calculate metrics with SDMetrics
        String pathToSDMetricsjar = args[3];
        String pathToAuthorityXml = args[4];
        String pathToHubXml = args[5];
        String pathToCycleXml = args[6];
        String pathToAllXml = args[7];
        String pathToFilteredXml = args[8];
        String pathToIslandXml = args[9];
        
        Runtime r = Runtime.getRuntime();
        // java -jar SDMetrics.jar -xmi projects/antlrworks-1.4.3/xmi/antlrworks_bo_12.xmi -f xml projects/antlrworks-1.4.3/xmi/antlrworks_bo_12.xml
        Process p1 = r.exec("java -jar " + pathToSDMetricsjar + " -xmi " + fileName + ".xmi -f xml " +  fileName + ".xml");
        p1.waitFor();
  
        File metricsFile = new File(fileName + "_Class.xml"); 
        // search for authority-hub-cycle class metrics
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document metricsDoc = dBuilder.parse(metricsFile);
        metricsDoc.getDocumentElement().normalize();

        File authFile = new File(pathToAuthorityXml);
        Document authDoc = dBuilder.parse(authFile);
        authDoc.getDocumentElement().normalize();

        File hubFile = new File(pathToHubXml);
        Document hubDoc = dBuilder.parse(hubFile);
        hubDoc.getDocumentElement().normalize();

        File cycleFile = new File(pathToCycleXml);
        Document cycleDoc = dBuilder.parse(cycleFile);
        cycleDoc.getDocumentElement().normalize();

        File allFile = new File(pathToAllXml);
        Document allDoc = dBuilder.parse(allFile);
        allDoc.getDocumentElement().normalize();

        File filteredFile = new File(pathToFilteredXml);
        Document filteredDoc = dBuilder.parse(filteredFile);
        filteredDoc.getDocumentElement().normalize();

        NodeList nodeList = metricsDoc.getElementsByTagName("Data");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            Node node = nodeList.item(i);
            Node attr = node.getAttributes().item(0);
            if (!"ss:Type".equals(attr.getNodeName()) || !"String".equals(attr.getNodeValue()))
                continue;

            if (i > 33)
                addNodeToDocument(node, allDoc);
            
            boolean shouldBeFilteredOut = true;
            if(!searchForMetrics(authorityList, node, authDoc)) {
                if (!searchForMetrics(hubList, node, hubDoc)) {
                    if(!searchForMetrics(cycleList, node, cycleDoc)) {
                        shouldBeFilteredOut = false;
                    }
                }
            }

            if (i > 33 && !shouldBeFilteredOut)
                addNodeToDocument(node, filteredDoc);
        }

        // write found metric values
        Transformer xmlTrans = transFact.newTransformer();
        xmlTrans.transform(new DOMSource(authDoc), new StreamResult(authFile));
        xmlTrans.transform(new DOMSource(hubDoc), new StreamResult(hubFile));
        xmlTrans.transform(new DOMSource(cycleDoc), new StreamResult(cycleFile));
        xmlTrans.transform(new DOMSource(allDoc), new StreamResult(allFile));
        xmlTrans.transform(new DOMSource(filteredDoc), new StreamResult(filteredFile));
        
        // create cluster file mciFileName.I20
        Process p2 = r.exec("/usr/local/bin/mcl " + mciFile.getCanonicalPath() + " -o " + mciFile.getCanonicalPath() + ".I20");
        p2.waitFor();
        
        File clusterResultFile = new File(mciFile.getCanonicalPath() + ".I20");
        MclResultReader mclReader = new MclResultReader(clusterResultFile, klasses);
        HashMap<String, Integer> map = mclReader.read();
        HashMap<Integer, List<Node>> clusters = new HashMap<Integer, List<Node>>();

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
        
        File islandFile = new File(pathToIslandXml);
        Document islandDoc = dBuilder.parse(islandFile);
        islandDoc.getDocumentElement().normalize();

        // write metrics per class
        int islandThreshold = 5;
        /*
         * 
        for (Integer key : clusters.keySet()) {
            List<Node> clist = clusters.get(key);
            if (clist.size() > islandThreshold) {
                for (Node node : clist) {
                    NodeList islandNodeList = islandDoc.getElementsByTagName("Table");
                    Node importedNode = islandDoc.importNode(node.getParentNode().getParentNode(), true);
                    islandNodeList.item(0).appendChild(importedNode);
                }
            }
        }
        */
        
        // write island metrics by taking average values among class metrics in an island
        for (Integer key : clusters.keySet()) {
            List<Node> clist = clusters.get(key);
            if (clist.size() > islandThreshold) {
                NodeList islandNodeList = islandDoc.getElementsByTagName("Table");
                String name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
                clist.get(0).getFirstChild().setNodeValue(name + " - island " + String.valueOf(key));
                Node rootNode = clist.get(0).getParentNode().getParentNode();
                for (int i=1; i<clist.size(); i++) {
                    rootNode = calcAverageMetrics(rootNode, clist.get(i).getParentNode().getParentNode(), i+1);
                }
                Node importedNode = islandDoc.importNode(rootNode, true);
                islandNodeList.item(0).appendChild(importedNode);
            }
        }
        
        xmlTrans.transform(new DOMSource(islandDoc), new StreamResult(islandFile));
        
    }
    
    private static Node calcAverageMetrics(Node n1, Node n2, int size) {
        for (int i=3; i<=65; i=i+2) {
            float m1 = Float.parseFloat(n1.getChildNodes().item(i).getFirstChild().getFirstChild().getNodeValue());
            float m2 = Float.parseFloat(n2.getChildNodes().item(i).getFirstChild().getFirstChild().getNodeValue());
            float average = ((m1 * (size - 1)) + m2) / size; 
            n1.getChildNodes().item(i).getFirstChild().getFirstChild().setNodeValue(String.valueOf(average));
        }
        return n1;
    }

    private static void addNodeToDocument(Node node, Document doc)
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

    private static boolean searchForMetrics(List<simpleformat.Class> list, Node node, Document doc)
    {
        if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE)
        {
            String value = node.getFirstChild().getNodeValue();
            for (simpleformat.Class c : list)
            {
                int index = value.indexOf("." + c.getName());
                if (index > 0 && (index + c.getName().length() + 1) == value.length())
                {
                    NodeList nodeList = doc.getElementsByTagName("Table");
                    Node importedNode = doc.importNode(node.getParentNode().getParentNode(), true);
                    nodeList.item(0).appendChild(importedNode);
                    return true;
                }
            }
        }
        return false;
    }

    private static void printResults(String title, List<simpleformat.Class> list, BufferedWriter wr) 
            throws IOException {
        System.out.println(title + " count: " + String.valueOf(list.size()));
        wr.write(title + " count: " + String.valueOf(list.size()));
        wr.newLine();
        for (simpleformat.Class c : list) {
            System.out.println("- " + c.getName());
            wr.write("- " + c.getName());
            wr.newLine();
        }
        System.out.println();
        wr.newLine();
    }
    
    private static boolean createFile(File f) throws IOException {
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        if (!f.exists())
            return f.createNewFile();
        return false;
    }
    
}
