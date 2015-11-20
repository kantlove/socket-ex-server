import socketex.core.HostName;
import socketex.core.StringEx;
import socketex.core.console;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mt on 11/19/2015.
 */
public class ServerBase {
    public final String server_ip = "127.0.0.1";
    public final int server_port = 2015;
    public final int autosave_delay = 15000; // 15 sec

    protected Map<String, UserRecord> allUsers; // key: username - value: user obj
    protected Map<String, HostName> userIps; // key: username - value: user current ip

    public ServerBase() throws IOException {
        allUsers = Storage.getUsers();
        userIps = new HashMap<>();

        createRoutes();
        startAutosave();
    }

    /**
     * Override to specify your custom routes
     * @throws IOException
     */
    protected void createRoutes() throws IOException {}

    protected String saveUser(String username, String password, HostName hostName) throws IOException {
        UserRecord user = new UserRecord(username, password, hostName);
        allUsers.put(username, user);
        Storage.saveUser(new UserRecord(username, password, hostName));
        return user.id;
    }

    protected boolean userExists(String username, String password) {
        UserRecord userToCheck = new UserRecord(username, password);
        return allUsers.containsValue(userToCheck);
    }

    protected boolean usernameExists(String username) {
        return allUsers.containsKey(username);
    }

    /**
     * Get all registered users
     * @param exclude exclude this username from the list
     * @return
     */
    protected String[] getUserArray(String exclude) {
        UserRecord[] a = new UserRecord[allUsers.size()];
        allUsers.values().toArray(a);
        String []s = new String[a.length];

        for(int i = 0; i < a.length; ++i) {
            if(!StringEx.isNullOrWhiteSpace(exclude) && a[i].username.equals(exclude))
                continue;
            s[i] = a[i].username;
        }
        return s;
    }

    /**
     * Auto-save user list
     */
    protected void startAutosave() {
        Timer timer = new Timer("Autosave Timer");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                console.system("Auto-saving user list.");
                try {
                    Storage.saveAllUsers(allUsers);
                }
                catch (IOException e) {
                    console.error(e.getMessage());
                }
            }
        }, autosave_delay, autosave_delay); // auto save each 5 seconds
    }
}
