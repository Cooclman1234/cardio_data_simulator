package com.cardio_generator.outputs;

/**
 * Defines the output method for output strategy classes to output health data for a particular patient
*/
public interface OutputStrategy {

    /**
     * Outputs the health data of a patient
     * 
     * @param patientId the unqiue identifier of a patient
     * @param timestamp the point in time in whihc data was generated
     * @param label labels the type of health data
     * @param data is the health data
     */
    void output(int patientId, long timestamp, String label, String data);
}
