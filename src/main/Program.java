package main;


import bridge.MyClust;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import sdmetrics.SDMetrics;
import sdmetrics.SDMetricsConfigurationException;
import simpleformat.*;
import simpleformat.Class;
import simpleformat.FileFormatNotSupportedException;
import simpleformat.GodClassFinder;
import simpleformat.HubClassFinder;
import simpleformat.clutograph.SimpleFormat2ClutoGraph;
import simpleformat.graph.SimpleFormat2Graph;
import simpleformat.mclgraph.MclCluster;
import simpleformat.mclgraph.MclResultReader;
import simpleformat.mclgraph.SimpleFormat2MclGraph;

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
            SAXException, InterruptedException, SDMetricsConfigurationException {
        
        if (args.length != 5) {
            System.err.println("Usage:");
            System.err.println("  java -jar Xmi2Graphml.jar xmiFileName"
                    + " authorityThreshold(float) hubThreshold(float) pathToSDMetrics.jar clearMetricsFiles");
            System.exit(1);
        }
        
        File xmiFile = new File("./" + args[0]);
        InputStream xslGraphml = 
                Program.class.getResourceAsStream("/xmi2graphml/xmi2graphml.xsl");
        InputStream xslSimpleFormat = 
                Program.class.getResourceAsStream("/simpleformat/xsl/xmi2simpleformat.xsl");

        Source xmiSource = new StreamSource(xmiFile);
        Source xsltGraphml = new StreamSource(xslGraphml);
        Source xsltSimpleFormat = new StreamSource(xslSimpleFormat);
        
        String fileName = args[0].substring(0, args[0].indexOf(".xmi"));
        File graphml = new File("./" + fileName + ".graphml");
        File simpleFormat = new File("./" + fileName + ".smpl");
        
        if (Util.createFile(graphml)) {
            System.out.println("Creating graphml file...");
            XSLTransformer.transform(xmiSource, xsltGraphml, graphml);
            System.out.println("Transformation completed. Output file: " + graphml.getCanonicalPath());
        }
        
        if (Util.createFile(simpleFormat)) {
            System.out.println("Creating simpleformat file...");
            XSLTransformer.transform(xmiSource, xsltSimpleFormat, simpleFormat);
            System.out.println("Transformation completed. Output file: " + simpleFormat.getCanonicalPath());
        }   
        
        // calculate authority, hub and cycle classes from simpleformat
        System.out.println("Calculating authority, hub, cycle and god classes.");
        Tokenizer tokenizer = new Tokenizer(simpleFormat);
        List<Class> klasses = tokenizer.tokenize();
        
        File resultsFile = new File("./" + fileName + ".txt");
        Util.createFile(resultsFile);
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
        List<Class> authorityList = authorityClassFinder.find();

        HubClassFinder hubClassFinder =
                new HubClassFinder(klasses, tokenizer.getEdgeCount(), Float.parseFloat(args[2]));
        List<Class> hubList = hubClassFinder.find();
        
        // filter out god classes
        GodClassFinder godClassFinder =
                new GodClassFinder(authorityList, hubList);
        List<Class> godList = godClassFinder.find();
        
        CycleClassFinder cycleClassFinder = new CycleClassFinder(klasses);
        List<Class> cycleList = cycleClassFinder.find();

        Util.printResults("God", godList, wr);
        Util.printResults("Authority", authorityList, wr);        
        Util.printResults("Hub", hubList, wr);
        Util.printResults("Cycle", cycleList, wr);
        Util.printResults("God", godList, wr);
        wr.close();
        
        // create sparse graph matrix file for cluto clustering tool
        File graphFile = new File("./" + fileName + ".graph");
        if (Util.createFile(graphFile)) {
            System.out.println("Creating graph file...");
            SimpleFormat2ClutoGraph sf2cg = new SimpleFormat2ClutoGraph(simpleFormat);
            sf2cg.write(graphFile);
            System.out.println("Transformation completed. Output file: " + graphFile.getCanonicalPath());
        }        
        
        // create graph matrix file for mcl clustering tool
        File mciFile = new File("./" + fileName + ".mci");
        if (Util.createFile(mciFile)) {
            System.out.println("Creating mci file...");
            SimpleFormat2MclGraph sf2mg = new SimpleFormat2MclGraph(simpleFormat);
            sf2mg.write(mciFile);
            System.out.println("Transformation completed. Output file: " + mciFile.getCanonicalPath());
        } 
        
        // calculate metrics with SDMetrics
        String pathToSDMetricsjar = args[3];
        String clearMetricsFiles = args[4];
        SDMetrics sdMetrics = new SDMetrics(pathToSDMetricsjar, fileName);
        if (Integer.parseInt(clearMetricsFiles) == 1) {
            sdMetrics.clearMetricsFiles();
        }
        sdMetrics.writeMetricsOfClasses(authorityList, hubList, cycleList);
        
        // create cluster file mciFileName.I20 
        MclCluster mcl = new MclCluster(mciFile.getCanonicalPath());
        File clusterResultFile = mcl.cluster();
        
        MclResultReader mclReader = new MclResultReader(clusterResultFile, klasses);
        HashMap<String, Integer> map = mclReader.read();
        
        sdMetrics.setAuthorityList(authorityList);
        sdMetrics.setHubList(hubList);
        sdMetrics.setCycleList(cycleList);
        sdMetrics.setGodList(godList);
        sdMetrics.writeMetricsOfIslands(map, SDMetrics.ISLAND_METRICS_PER_CLASS, true);
        
        // create graph matrix file for mcl clustering tool
        File grph = new File("./" + fileName + ".grph");
        if (Util.createFile(grph)) {
            System.out.println("Creating grph file...");
            SimpleFormat2Graph sf2g = new SimpleFormat2Graph(simpleFormat);
            sf2g.write(grph);
            System.out.println("Transformation completed. Output file: " + grph.getCanonicalPath());
        }         
        
        MyClust bridgeCluster = new MyClust(grph.getCanonicalPath(), "1", "2", "1");
        bridgeCluster.getBridgeNodes();
        
    }

}
