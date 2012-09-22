/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmi2graphml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.OutputKeys;
import java.io.Console;
import java.util.List;
import simpleformat.AuthorityClassFinder;
import simpleformat.CycleClassFinder;
import simpleformat.FileFormatNotSupportedException;

/**
 *
 * @author saricas
 */
public class Xmi2Graphml {

    public enum TransformType {
        GRAPHML,
        SIMPLEFORMAT
    }

    public static String XSL_GRAPHML = "./src/xmi2graphml/xmi2graphml.xsl";
    public static String XSL_SIMPLEFORMAT = "./src/xmi2simpleformat/xmi2simpleformat.xsl";

    public static String FILE_EXTENSION_GRAPHML = "graphml";
    public static String FILE_EXTENSION_SIMPLEFORMAT = "txt";

    public static String FOLDER_NAME_GRAPHML = "graphml";
    public static String FOLDER_NAME_SIMPLEFORMAT = "txt";

    /**
     * Accept two command line arguments: the name of an XML file, and
     * the name of an XSLT stylesheet. The result of the transformation
     * is written to stdout.
     */
    public static void main(String[] args)
            throws javax.xml.transform.TransformerException {
        if (args.length <= 2) {
            System.err.println("Usage:");
            System.err.println("  java " + Xmi2Graphml.class.getName(  )
                    + " xmlFileName xsltFileName xmlOutputName(opt)");
            System.exit(1);
        }

        File xsltFile = new File(args[0]);
        File xmlFile = new File(args[1]);
 
        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(xsltFile);
        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(xmlFile);
        javax.xml.transform.Result result;
        if (args.length == 3) {
            File outputFile = new File(args[2]);
            result = new javax.xml.transform.stream.StreamResult(outputFile);
        } else {
            result = new javax.xml.transform.stream.StreamResult(System.out);   
        }
 
        // create an instance of TransformerFactory
        javax.xml.transform.TransformerFactory transFact =
                javax.xml.transform.TransformerFactory.newInstance();
 
        javax.xml.transform.Transformer trans =
                transFact.newTransformer(xsltSource);
 
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        trans.transform(xmlSource, result);
        if (args.length == 3)
            System.out.println("Transformation completed. Output file: " + args[2]);

        File txtFile = new File("./test/txt/testdiagram.txt");
        simpleformat.Tokenizer tokenizer = new simpleformat.Tokenizer(txtFile);
        try {
            List<simpleformat.Class> klasses = tokenizer.tokenize();
            AuthorityClassFinder authorityClassFinder = 
                    new AuthorityClassFinder(klasses, tokenizer.getEdgeCount(), (float) 0.05);
            List<simpleformat.Class> authorityList = authorityClassFinder.find();
            authorityList.size();
            CycleClassFinder cycleClassFinder = new CycleClassFinder(klasses);
            List<simpleformat.Class> cycleList = cycleClassFinder.find();
            cycleList.size();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Xmi2Graphml.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Xmi2Graphml.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileFormatNotSupportedException ex) {
            Logger.getLogger(Xmi2Graphml.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
