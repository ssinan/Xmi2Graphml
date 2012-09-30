
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import simpleformat.*;
import simpleformat2clutograph.SimpleFormat2ClutoGraph;

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
            throws TransformerConfigurationException, TransformerException, IOException, FileNotFoundException, FileFormatNotSupportedException {      
        
        if (args.length != 3) {
            System.err.println("Usage:");
            System.err.println("  java -jar Xmi2Graphml.jar xmiFileName"
                    + " authorityThreshold(float) hubThreshold(float)");
            System.exit(1);
        }
        
        File xmiFile = new File("./" + args[0]);
        File xslGraphml = new File("./xmi2graphml.xsl");
        File xslSimpleFormat = new File("./xmi2simpleformat.xsl");        

        Source xmiSource = new StreamSource(xmiFile);
        Source xsltGraphml = new StreamSource(xslGraphml);
        Source xsltSimpleFormat = new StreamSource(xslSimpleFormat);
        
        String fileName = args[0].substring(0, args[0].indexOf(".xmi"));
        File graphml = new File("./graphml/" + fileName + ".graphml");
        File simpleFormat = new File("./simpleformat/" + fileName + ".txt");

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
        
        // calculate authority, hub and cycle classes from simpleformat
        System.out.println("Calculating authority, hub and cycle classes.");
        Tokenizer tokenizer = new Tokenizer(simpleFormat);
        List<simpleformat.Class> klasses = tokenizer.tokenize();

        AuthorityClassFinder authorityClassFinder =
                new AuthorityClassFinder(klasses, tokenizer.getEdgeCount(), (float) Float.parseFloat(args[1]));
        List<simpleformat.Class> authorityList = authorityClassFinder.find();
        printResults("Authority", authorityList);

        HubClassFinder hubClassFinder =
                new HubClassFinder(klasses, tokenizer.getEdgeCount(), (float) Float.parseFloat(args[2]));
        List<simpleformat.Class> hubList = hubClassFinder.find();
        printResults("Hub", hubList);

        CycleClassFinder cycleClassFinder = new CycleClassFinder(klasses);
        List<simpleformat.Class> cycleList = cycleClassFinder.find();
        printResults("Cycle", cycleList);
        
        // create sparse graph matrix file for cluto clustering tool
        File graphFile = new File("./graph/" + fileName + ".graph");
        if (createFile(graphFile)) {
            System.out.println("Creating graph file...");
            SimpleFormat2ClutoGraph sf2cg = new SimpleFormat2ClutoGraph(simpleFormat);
            sf2cg.write(graphFile);
        }
        
        System.out.println("Transformation completed. Output file: " + graphFile.getCanonicalPath());
        
    }
    
    private static void printResults(String title, List<simpleformat.Class> list) {
        System.out.println(title + " count: " + String.valueOf(list.size()));
        for (simpleformat.Class c : list) {
            System.out.println("- " + c.getName());
        }
    }
    
    private static boolean createFile(File f) throws IOException {
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        if (!f.exists())
            return f.createNewFile();
        return false;
    }
    
}
