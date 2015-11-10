/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.uppaal;

import org.w3c.dom.Document;

/**
 * Interface for genes that will create instances of uppaal templates.
 * @author bencaldwell
 */
public interface ITaTemplate {
    
    public void write(Document document);
    
}
