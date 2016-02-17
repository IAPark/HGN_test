package edu.kent.cs.aisp.hgn_test.findeye;

import android.content.Context;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.kent.cs.aisp.hgn_test.R;

/**
 * Created by isaac on 2/15/16.
 */
public class utility {
    static String resource_to_file(String file_name, int resource, Context context) throws IOException {
            InputStream is = context.getResources().openRawResource(resource);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File file = new File(cascadeDir, file_name);
            FileOutputStream os = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            return file.getAbsolutePath();
    }
}
