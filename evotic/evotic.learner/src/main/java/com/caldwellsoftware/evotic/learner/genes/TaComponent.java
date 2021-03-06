/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner.genes;

import com.caldwellsoftware.evotic.uppaal.ITaTemplate;
import com.caldwellsoftware.evotic.uppaal.TaSystem;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;

/**
 * Abstract base class for Timed Automata Components.
 * 
 * A TaComponent represents an instance of a TA template. 
 * Instances receive and send on 0 or more broadcast sync channels (arity).
 * @author bencaldwell
 */
public abstract class TaComponent extends CommandGene implements ITaTemplate {

     public TaComponent( final GPConfiguration a_conf, 
                        final int a_arity, 
                        final Class a_returnType) 
            throws InvalidConfigurationException {
        
        super(a_conf, a_arity, a_returnType);
        
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     
    @Override
    public void execute_void(ProgramChromosome c, int n, Object[] args) {
        if (args==null || args.length == 0 || !(args[0] instanceof TaSystem)) {
            throw new UnsupportedOperationException("The TaSystem must be passed to each TA component gene.");
        }
    }
    
}

