package scriptutils.common; 

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    
    
   

    public static void findAndReplace(File file, String regex, String replacement) throws Exception {
        String fileAsStr = readFile(file.getAbsolutePath());
        writeToFile(findAndReplace(fileAsStr, regex, replacement), file);
    }
    
    public static String findAndReplace(String fileAsStr, String regex, String replacement) throws Exception {
        return fileAsStr.replaceAll(regex, replacement);
    }

    public static String readFromStream(InputStream in) throws IOException {
        StringBuffer wsdlStr = new StringBuffer();

        int read;

        byte[] buf = new byte[1024];
        while ((read = in.read(buf)) > 0) {
            wsdlStr.append(new String(buf, 0, read));
        }
        in.close();
        return wsdlStr.toString();
    }

    public static String readFile(String file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        byte[] content = new byte[in.available()];
        in.read(content);
        in.close();
        return new String(content);
    }
    
    public static void writeToFile(String data, File file) throws IOException {
        FileWriter fw = new FileWriter(file);

        // get the standard out of the application and write to file
        fw.write(data);
        fw.close();
    }
    
   
}
