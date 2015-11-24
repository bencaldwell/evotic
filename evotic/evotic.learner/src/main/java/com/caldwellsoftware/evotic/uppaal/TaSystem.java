/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.uppaal;

import com.caldwellsoftware.evotic.learner.Learner;
import com.caldwellsoftware.evotic.learner.examples.DigLatch;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jgap.gp.IGPProgram;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Create an UPPAAL system representing an individual.
 * 
 * @author bencaldwell
 */
public class TaSystem {
    
    private final Document document;
    
    public TaSystem() throws Exception {
        URL templateURL = getClass().getResource("/com/caldwellsoftware/evotic/learner/templates/template.xml");
        File templateFile = new File(templateURL.getFile());
        
        DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance();
        docbf.setNamespaceAware(true);
        DocumentBuilder docbuilder;
        Document _document = null;
        try {
            docbuilder = docbf.newDocumentBuilder();
            _document = docbuilder.parse(templateFile);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(TaSystem.class.getName()).log(Level.SEVERE, null, ex);
            _document = null;
            throw new Exception("Could not create the base Uppaal system.", ex);
        } finally {
            this.document = _document;
        }
    }
    
    /**
     * Write to a default location - the users desktop
     * @throws java.lang.Exception
     */
    public void Write() throws Exception {
        File desktop = new File(System.getProperty("user.home"), "/Desktop"); 
        File outFile = new File(desktop, "evotic-ta.xml");
        this.Write(outFile);
    }
    
    /**
     * Write the uppaal TA to an xml file.
     * 
     * @param outFile
     * @throws Exception 
     */
    public void Write(File outFile) throws Exception {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            // send DOM to file
            tr.transform(new DOMSource(document),
                    new StreamResult(new FileOutputStream(outFile)));
        } catch (TransformerException | FileNotFoundException ex) {
            Logger.getLogger(TaSystem.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Could not write Uppaal file.", ex);
        }
    }
    
    public void addBroadcastChan(String name) {
        
    }
    
    public void addVariableBool(String name) {
        
    }
    
    public void addTon(     String instanceNum, 
                            String input,
                            String output,
                            int delay) {
        // get the system declarations section
        NodeList nodes = document.getElementsByTagName("system");
        String text = nodes.item(0).getTextContent();
        
        // add the new TON instance
        String instanceName = "ton" + instanceNum;
        String instanceDecl = instanceName + " = TON(exec[" + instanceNum + "], " + input + ", " + output + ", " + delay + ");" + System.lineSeparator();
        text = instanceDecl + text;
        
        nodes.item(0).setTextContent(text);
        
        // include the new TON instance in the system declaration
        addToSystem(instanceName);
        
    }
    
    /**
     * Add an instance to the system declaration.
     * This enables the instance in the TA system.
     * @param instanceName 
     */
    public void addToSystem(String instanceName) {
        
        // get the system declarations section
        NodeList nodes = document.getElementsByTagName("system");
        String text = nodes.item(0).getTextContent();
        
        // find the "system ...;" line and append the new instance
        Pattern pattern = Pattern.compile("^system.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        matcher.find();
        String systemDecl = matcher.group();
        systemDecl = systemDecl.replace(";", ", " + instanceName + ";");
        text = matcher.replaceAll(systemDecl);
        nodes.item(0).setTextContent(text);
    }
    
    public void addToff(    String instanceNum, 
                            String input,
                            String output,
                            int delay) {
        
        // get the system declarations section
        NodeList nodes = document.getElementsByTagName("system");
        String text = nodes.item(0).getTextContent();
        
        // add the new TON instance
        String instanceName = "toff" + instanceNum;
        String instanceDecl = instanceName + " = TOFF(exec[" + instanceNum + "], " + input + ", " + output + ", " + delay + ");" + System.lineSeparator();
        text = instanceDecl + text;
        
        nodes.item(0).setTextContent(text);
        
        // include the new TON instance in the system declaration
        addToSystem(instanceName);
    }
    
    public void addAnd(    String instanceNum, 
                            String input0,
                            String input1,
                            String output) {
        
        // get the system declarations section
        NodeList nodes = document.getElementsByTagName("system");
        String text = nodes.item(0).getTextContent();
        
        // add the new TON instance
        String instanceName = "ton" + instanceNum;
        String instanceDecl = instanceName + " = AND(exec[" + instanceNum + "], " + input0 + ", " + input1 + ", " + output +  ");" + System.lineSeparator();
        text = instanceDecl + text;
        
        nodes.item(0).setTextContent(text);
        
        // include the new TON instance in the system declaration
        addToSystem(instanceName);
    }
    
    public void modify(IGPProgram ind) {
        Object[] args = new Object[] {this};
        ind.execute_void(0, args);
        
    }
}
