/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.evotic.learner.examples;

import com.caldwellsoftware.evotic.learner.Learner;
import java.net.URL;

/**
 *
 * @author bencaldwell
 */
public class DigLatch {
    
    public static void main(String[] args) {
        URL configURL = DigLatch.class.getResource("/com/caldwellsoftware/evotic/learner/examples/diglatch/config.xml");
        String[] learnerArgs = {configURL.toString()};
        Learner learner = new Learner(learnerArgs);
    }
}
