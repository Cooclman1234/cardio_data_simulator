package com.alerts;

import java.util.List;

import com.data_management.AlertStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/* 
reason for this comment: TA asked me to write a reason for the adding of AlertStorage to the checkAlert methods, instead of using List<Alert>
justifications:
- Alert Storage's purpose it to centrally store all alerts triggered. Therefore I though it would be best practice
to add emitted alerts into one storage.  

- If I use one centralized Alert Storage instead of seperate Lists of Alerts for each Strategy, management becomes easier

- this change also corrsponds to the Single responsability principle (SRP): If I would choose to give each AlertStrategy also the job to store 
the alerts which are being eimitted, it would not be very SRP friendly.
 */

public interface AlertStrategy {
    void checkAlert(Patient patient, List<PatientRecord> records, AlertStorage alertStorage);
}
