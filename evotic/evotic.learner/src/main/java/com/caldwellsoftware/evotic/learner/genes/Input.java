/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner.genes;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.impl.GPConfiguration;

/**
 *
 * @author bencaldwell
 */
public class Input extends TaComponent {

    public Input(GPConfiguration a_conf, Class a_returnType) throws InvalidConfigurationException {
        super(a_conf, 0, a_returnType);
    }

    @Override
    public String toString() {
        return "Input{" + '}';
    }
    
    
}