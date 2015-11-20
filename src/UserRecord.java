import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.javaws.exceptions.InvalidArgumentException;
import socketex.core.HostName;
import socketex.core.StringEx;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

/**
 * Created by mt on 11/18/2015.
 */
public class UserRecord implements Comparable<UserRecord> {
    public String username, password, id;
    public HostName hostName;

    public UserRecord(String username, String password) {
        this.username = username;
        this.password = StringEx.getMD5(password);
        this.id = "" + hashCode();
    }

    public UserRecord(String username, String password, boolean noMD5) {
        this.username = username;
        this.password = noMD5 ? password : StringEx.getMD5(password);
        this.id = "" + hashCode();
    }

    public UserRecord(String username, String password, HostName hostName) {
        this(username, password);
        this.hostName = hostName;
    }

    public static UserRecord valueOf(String s) throws IllegalArgumentException {
        return valueOf(s, false);
    }

    public static UserRecord valueOf(String s, boolean noMD5) throws IllegalArgumentException {
        if(StringEx.isNullOrWhiteSpace(s))
            throw new IllegalArgumentException("String is not UserRecord");
        String []parts = s.split(" ");
        if(parts.length != 2) throw new IllegalArgumentException("String is not UserRecord");

        return new UserRecord(parts[0], parts[1], noMD5);
    }

    public static List<UserRecord> parseList(String json) {
        if(StringEx.isNullOrWhiteSpace(json))
            throw new IllegalArgumentException("String is not a valid json");

        Type listType = new TypeToken<ArrayList<UserRecord>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    @Override
    public int hashCode() {
        return username.hashCode() ^ password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof UserRecord))
            return false;
        if(this == obj)
            return true;
        UserRecord o = (UserRecord)obj;
        return username.equals(o.username) && password.equals(o.password);
    }

    @Override
    public String toString() {
        return username.concat(" ").concat(password);
    }

    @Override
    public int compareTo(UserRecord o) {
        return id.compareTo(o.id);
    }
}
