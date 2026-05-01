package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the generation method for a particular patient for all patient data generators.
 */
public interface PatientDataGenerator {

    /**
     * Generates health data for a specified patient and outputs it through an output strategy.
     * 
     * @param patientId a unique identifier for particular patient for whom data is generated given as an integer
     * @param outputStrategy the strategy used to output the health data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
