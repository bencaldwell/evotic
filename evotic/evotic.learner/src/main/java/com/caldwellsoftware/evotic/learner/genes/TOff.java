/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner.genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.GPConfiguration;
import org.w3c.dom.Document;

/**
 *
 * @author bencaldwell
 */
public class TOff extends TaComponent {

    public TOff(GPConfiguration a_conf, Class a_returnType) throws InvalidConfigurationException {
        super(a_conf, 1, a_returnType);
    }

    public TOff(GPConfiguration a_conf) throws InvalidConfigurationException {
        // create CommandGene with arity=1 and Boolean return type
        this(a_conf, CommandGene.BooleanClass);
    }

    @Override
    public String toString() {
        return "TOff{" + '}';
    }

    @Override
    public void write(Document document) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
