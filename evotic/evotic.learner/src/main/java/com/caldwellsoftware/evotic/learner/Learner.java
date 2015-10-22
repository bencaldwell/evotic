/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner;

import com.caldwellsoftware.evotic.learner.genes.TOff;
import com.caldwellsoftware.evotic.learner.genes.TOn;
import com.caldwellsoftware.evotic.learner.genes.Input;
import com.caldwellsoftware.evotic.learner.examples.DigLatch;
import java.net.URL;
import org.apache.log4j.Logger;
import org.jgap.InvalidConfigurationException;
import org.jgap.event.GeneticEvent;
import org.jgap.event.GeneticEventListener;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Equals;
import org.jgap.gp.function.GreaterThan;
import org.jgap.gp.function.If;
import org.jgap.gp.function.Or;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.False;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.True;
import org.jgap.gp.terminal.Variable;
import org.jgap.util.SystemKit;

/**
 *
 * @author bencaldwell
 */
public class Learner extends GPProblem {
    
    private final String configFilePath;
    private final String templateFilePath;
    static Logger log = Logger.getLogger(Learner.class.getName());
    protected static Variable vx;   
    
    
    public Learner(String[] args) {
        String configFilePath = null;
        String templateFilePath = null;
        
        if (args.length == 1) {
            configFilePath = args[0];
            log.debug("Config file found: " + configFilePath);
        } else {
            log.fatal("No config file provided.");
            System.exit(1);
        }
        
        URL templateURL = Learner.class.getResource("/com/caldwellsoftware/evotic/learner/templates/template.xml");
        try {
            templateFilePath = templateURL.toString();
            log.debug("Template file found: " + templateFilePath);
        } catch (Exception e) {
            log.fatal("No template file found: " + e);
            System.exit(1);
        }

        this.configFilePath = configFilePath;
        this.templateFilePath = templateFilePath;
    }
    
    public GPGenotype create() throws InvalidConfigurationException {
        GPConfiguration conf = getGPConfiguration();
        // The resulting GP program returns a boolean.
        // -------------------------------------------
        Class[] types = {CommandGene.BooleanClass}; // TODO: array/subprogram? return types for each output
        Class[][] argTypes = { {}
        };
        // The commands and terminals allowed to find a solution.
        // ------------------------------------------------------
        // TODO: create new commands that map to uppaal templates
        // TODO: what happens with terminals in context of uppaal?
        // Add command gene for each possible TA template
        CommandGene[][] nodeSets = { {
            // TON - on delay timer
            new TOn(conf), 
            // TOFF - off delay timer
            new TOff(conf),
            // Input - inputs to the SUL (must be 0 arity to be considered a terminal)
            new Input(conf, CommandGene.BooleanClass)
        }
        };
        // Initialize the GPGenotype.
        // --------------------------
        return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets,
            100, true);
  }
    
    public void start() throws Exception {
        GPConfiguration config = new GPConfiguration();
        config.setMaxInitDepth(5);
        config.setPopulationSize(80);
        config.setFitnessFunction(new SimpleFitnessFunction());
        config.setStrictProgramCreation(false);
        config.setProgramCreationMaxTries(5);
        config.setMaxCrossoverDepth(5);
        // Lower fitness value is better as fitness value indicates error rate.
        // --------------------------------------------------------------------
        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        super.setGPConfiguration(config);
        GPGenotype geno = create();
        // Simple implementation of running evolution in a thread.
        // -------------------------------------------------------
        config.getEventManager().addEventListener(GeneticEvent.GPGENOTYPE_EVOLVED_EVENT, new GeneticEventListener() {
            public void geneticEventFired(GeneticEvent a_firedEvent) {
                GPGenotype genotype = (GPGenotype) a_firedEvent.getSource();
                int evno = genotype.getGPConfiguration().getGenerationNr();
                double freeMem = SystemKit.getFreeMemoryMB();
                if (evno % 100 == 0) {
                  IGPProgram best = genotype.getAllTimeBest();
                  System.out.println("Evolving generation " + evno);
                  genotype.outputSolution(best);
                }
                if (evno > 3000) {
                  System.exit(1);
                }
              }
            });
        config.getEventManager().addEventListener(GeneticEvent.GPGENOTYPE_NEW_BEST_SOLUTION, new GeneticEventListener() {
          /**
           * New best solution found.
           *
           * @param a_firedEvent GeneticEvent
           */
            public void geneticEventFired(GeneticEvent a_firedEvent) {
              GPGenotype genotype = (GPGenotype) a_firedEvent.getSource();
              IGPProgram best = genotype.getAllTimeBest();
              double bestFitness = genotype.getFittestProgram().
                  getFitnessValue();
              if (bestFitness < 0.1) {
                // Quit, when the solutions seems perfect.
                // ---------------------------------------
                genotype.outputSolution(best);
                System.exit(0);
              }
            }
        });
        geno.evolve(10000);
    }
    
    public static void main(String[] args) {
        Learner learner = new Learner(args);
    }
    
    class SimpleFitnessFunction extends GPFitnessFunction {
        protected double evaluate(final IGPProgram ind) {
          int error = 0;
          // TODO: evaulate complexity metric
          // TODO: ioco check against SUL
          return error;
        }
    }
}
