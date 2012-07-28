/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xmi2graphml;

import java.io.File;
import javax.xml.transform.OutputKeys;

/**
 *
 * @author saricas
 */
public class Xmi2Graphml {

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
        
        File xmlFile = new File(args[0]);
        File xsltFile = new File(args[1]);
 
        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(xmlFile);
        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(xsltFile);
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
    }
}
