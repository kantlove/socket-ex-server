import socketex.core.console;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by mt on 11/18/2015.
 */
public class Storage {
    static final String fileDirectory = "./data/";
    static final String userFileName = "users.txt";

    private static String getUserFilePath() {
        String path = fileDirectory.concat(userFileName);
        // check existence
        File f = new File(fileDirectory);
        if(!f.exists()) {
            console.log("[ Create user data directory ]");
            f.mkdirs();
        }

        return path;
    }

    static void saveUser(UserRecord user) throws IOException {
        FileWriter fw = new FileWriter(getUserFilePath(), true);

        fw.write(user.toString());
        fw.flush();
        fw.close();
    }

    static void saveAllUsers(final Map<String, UserRecord> users) throws IOException {
        FileWriter fw = new FileWriter(getUserFilePath(), false);
        int count = 0;
        for (UserRecord ur : users.values()) {
            fw.write(ur.toString());
            fw.write(System.lineSeparator()); // new line
            count++;
        }
        console.systemf("%d users saved.\n", count);
        fw.flush();
        fw.close();
    }

    static Map<String, UserRecord> getUsers() throws IOException {
        Map<String, UserRecord> results = new HashMap<>();
        String line;
        try {
            InputStream fis = new FileInputStream(getUserFilePath());
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                UserRecord ur = UserRecord.valueOf(line, true);
                results.put(ur.username, ur);
            }

        }
        catch (FileNotFoundException e) {
        }
        return results;
    }

    static void clearFile(String file) throws IOException {
        FileWriter fw = new FileWriter(file);
        fw.write("");
        fw.close();
    }
}
