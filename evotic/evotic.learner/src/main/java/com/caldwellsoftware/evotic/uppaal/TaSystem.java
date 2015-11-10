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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
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
    
}
