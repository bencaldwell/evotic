/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner.genes;

import com.caldwellsoftware.evotic.uppaal.TaSystem;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
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

    @Override
    public void execute_void(ProgramChromosome c, int n, Object[] args) {
        super.execute_void(c, n, args); 
        c.execute_void(n, 0, args);
        TaSystem ta = (TaSystem)args[0];
        // TODO: determine which index should be used for uppal var array - e.g. just allow 10 vars per component?
        int varIn = 10*n;
        int varOut = 10*n + 1;
        ta.addToff(      String.format("%03d", n),
                        "var["+varIn+"]", 
                        "var["+varOut+"]", 
                        1000); //TODO: create a delay integer terminal for timers?
    }
    
    
}
