package com.data_management;

import java.io.IOException;

public class MockReader implements DataReader {

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        // does nothing ... mock data reader...

         throw new UnsupportedOperationException("Unimplemented method 'readData'");
    }
    
}
