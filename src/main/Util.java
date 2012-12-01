/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.*;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author saricas
 */
public class Util {
    
    public static void copy(String resource, String destination) throws FileNotFoundException, IOException {
        InputStream resStreamIn = Util.class.getResourceAsStream(resource);
        File resDestFile = new File(destination);
        Util.createFile(resDestFile);
        OutputStream resStreamOut = new FileOutputStream(resDestFile);
        int readBytes;
        byte[] buffer = new byte[4096];
        while ((readBytes = resStreamIn.read(buffer)) > 0) {
            resStreamOut.write(buffer, 0, readBytes);
        }
    }

    public static boolean deleteFile(File f) throws IOException {
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        if (f.exists()) {
            return f.delete();
        }
        return false;
    }     
    
    public static boolean createFile(File f) throws IOException {
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        if (!f.exists()) {
            return f.createNewFile();
        }
        return false;
    }    

    public static void printResults(String title, List<simpleformat.Class> list, BufferedWriter wr)
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
    
    public static Document getDocumentFromFile(File file) 
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

}
