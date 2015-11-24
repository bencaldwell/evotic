/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner.genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.w3c.dom.Document;

/**
 *
 * @author bencaldwell
 */
public class Input extends TaComponent {

    private String m_varName;
    
    public Input(GPConfiguration a_conf, Class a_returnType, String a_varName) throws InvalidConfigurationException {
        super(a_conf, 0, a_returnType);
        m_varName = a_varName;
    }

    @Override
    public String toString() {
        return "Input{" + '}';
    }

    @Override
    public void write(Document document) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void execute_void(ProgramChromosome c, int n, Object[] args) {
        super.execute_void(c, n, args); 
        
    }
    
    
}
