/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner;

import org.apache.log4j.Logger;

/**
 *
 * @author bencaldwell
 */
public class Learner {
    
    private String configFilePath;
    /* Get actual class name to be printed on */
   static Logger log = Logger.getLogger(Learner.class.getName());
   
    public Learner(String[] args) {
        if (args.length == 1) {
            this.configFilePath = args[0];
            log.debug("Config file found: " + this.configFilePath);
        } else {
            log.fatal("No config file provided.");
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        Learner learner = new Learner(args);
    }
}
