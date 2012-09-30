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

    public static final int MODE_COMMANDLINE = 0;
    public static final int MODE_IDE_RUN = 1;

    public static final int GRAPHML = 100;
    public static final int SIMPLEFORMAT = 101;

    public static String XSL_GRAPHML = "./src/xmi2graphml/xmi2graphml.xsl";
    public static String XSL_SIMPLEFORMAT = "./src/xmi2simpleformat/xmi2simpleformat.xsl";

    public static String EXTENSION_GRAPHML = "graphml";
    public static String EXTENSION_SIMPLEFORMAT = "txt";
    public static String EXTENSION_XML = "xmi";

    /**
     * Accept two command line arguments: the name of an XML file, and
     * the name of an XSLT stylesheet. The result of the transformation
     * is written to stdout.
     */
    public static void main(String[] args)
            throws javax.xml.transform.TransformerException {

        int mode = MODE_IDE_RUN;

        File xsltFile = null;
        File xmlFile = null;
        File outputFile = null;

        if (mode == MODE_COMMANDLINE) {
            if (args.length < 2) {
                System.err.println("Usage:");
                System.err.println("  java " + Xmi2Graphml.class.getName(  )
                        + " xmlFileName xsltFileName xmlOutputName(opt)");
                System.exit(1);
            }
            xsltFile = new File(args[0]);
            xmlFile = new File(args[1]);
            
            if (args.length == 3) {
                outputFile = new File(args[2]);
            }

        } else {

            int type = SIMPLEFORMAT;
            String fileName = "testdiagram";

            switch (type) {
                case SIMPLEFORMAT:
                    xsltFile = new File(XSL_SIMPLEFORMAT);
                    outputFile = new File("./test/" + EXTENSION_SIMPLEFORMAT
                            + "/" + fileName + "." + EXTENSION_SIMPLEFORMAT);
                    break;
                default:
                    xsltFile = new File(XSL_GRAPHML);
                    outputFile = new File("./test/" + EXTENSION_GRAPHML + "/"
                            + fileName + "." + EXTENSION_GRAPHML);
                    break;
            }
            xmlFile = new File("./test/" + EXTENSION_XML + "/" + fileName + "." + EXTENSION_XML);
        }

        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(xsltFile);
        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(xmlFile);
        javax.xml.transform.Result result;

        if (mode == MODE_COMMANDLINE && args.length != 3) {
            result = new javax.xml.transform.stream.StreamResult(System.out);
        } else {
            result = new javax.xml.transform.stream.StreamResult(outputFile);
        }
 
        // create an instance of TransformerFactory
        javax.xml.transform.TransformerFactory transFact =
                javax.xml.transform.TransformerFactory.newInstance();
 
        javax.xml.transform.Transformer trans =
                transFact.newTransformer(xsltSource);
 
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        trans.transform(xmlSource, result);
        if (mode == MODE_IDE_RUN || (mode == MODE_COMMANDLINE && args.length == 3))
            System.out.println("Transformation completed. Output file: " + outputFile);
    }
}
