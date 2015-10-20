/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner;

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
        CommandGene[][] nodeSets = { {
            // We need a variable to feed in data (see fitness function).
            // ----------------------------------------------------------
            vx = Variable.create(conf, "X", CommandGene.IntegerClass),
            // Define the terminals (here: numbers) to try:
            // Easiest: Define two constants
            // More challenging: Define a terminal that can be within -10 and 10
            // new Constant(conf, CommandGene.IntegerClass, 0),
            // new Constant(conf, CommandGene.IntegerClass, -8),
            new Terminal(conf, CommandGene.IntegerClass,-10,10,true),
            // Boolean operators to use.
            // _------------------------
            new GreaterThan(conf, CommandGene.IntegerClass),
            new Or(conf),
            new Equals(conf, CommandGene.IntegerClass),
            // Define complex operator.
            // ------------------------
            new If(conf, CommandGene.BooleanClass),
            // Boolean terminals to use (they do not appear in an optimal solution
            // and make the task more challenging) --> Leave away if needed
            // -------------------------------------------------------------------
            // new True(conf, CommandGene.BooleanClass),
            new False(conf, CommandGene.BooleanClass)
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
          Object[] noargs = new Object[0];
          int maxDepth = ind.getChromosome(0).getDepth(0);
          if (maxDepth > 2) {
            error += maxDepth - 2;
          }
          for (int i = -10; i < 10; i++) {
            vx.set(new Integer(i));
            boolean y;
            if (i > 0) {
              y = true;
            }
            else {
              if (i != -8 && i != - 5) {
                y = false;
              }
              else {
                y = true;
              }
            }
            try {
              boolean result = ind.execute_boolean(0, noargs);
              if (result != y) {
                error += 10;
              }
            } catch (ArithmeticException ex) { // some illegal operation was executed.
              System.out.println("x = " + i);
              System.out.println(ind);
              throw ex;
            }
          }
          return error;
        }
    }
}
