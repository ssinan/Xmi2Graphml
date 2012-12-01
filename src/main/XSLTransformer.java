/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author saricas
 */
public class XSLTransformer {
    
    public static void transform(Source xmiSource, Source xsltSource, File outputfile) 
            throws TransformerConfigurationException, TransformerException {
        
        Result result = new StreamResult(outputfile);
        
        TransformerFactory transFact = TransformerFactory.newInstance();
        Transformer tranformer = transFact.newTransformer(xsltSource);

        tranformer.setOutputProperty(OutputKeys.INDENT, "yes");
        tranformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        
        tranformer.transform(xmiSource, result);
    }
    
}
