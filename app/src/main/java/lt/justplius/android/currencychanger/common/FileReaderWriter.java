package lt.justplius.android.currencychanger.common;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * This class provides file reader and writer methods
 */
public class FileReaderWriter {

    private static final String ERROR_TAG = "ReadWriteFile.java: ";

    private Context mContext;
    private String mFileName;

    public FileReaderWriter(Context context, String fileName) {
        mContext = context;
        mFileName = fileName;
    }

    public void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    mContext.openFileOutput(mFileName, Context.MODE_PRIVATE), "UTF-8");
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(ERROR_TAG, " File write failed: " + e.toString());
        }
    }

    public String readFromFile() {
        String result = "";
        try {
            // Open input stream based on context
            InputStream inputStream = mContext.openFileInput(mFileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                // Build string from buffer lines results
                StringBuilder stringBuilder = new StringBuilder();
                String receiveString = "";

                while ( ( receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                result = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(ERROR_TAG, " File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(ERROR_TAG, " Can not read file: " + e.toString());
        }

        return result;
    }
}
