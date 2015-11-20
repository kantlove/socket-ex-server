import com.google.gson.Gson;
import socketex.core.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by mt on 11/18/2015.
 */
public class Server extends ServerBase {

    public Server() throws IOException {
    }

    @Override
    protected void createRoutes() throws IOException {
        SocketEx socket = new SocketEx(server_ip, server_port, true);
        console.logf("Listening on port %d...\n", server_port);

        socket.on("connected", (sender, req) -> {
            console.info(sender + " connected.");
            socket.emit("welcome", sender, new Packet("Welcome to the room!"));
        });

        socket.on("register", (sender, req) -> {
            console.info("/register requested.");

            String[] parts = req.message.split(" ");
            String username = parts[0], password = parts[1];

            if (usernameExists(username)) {
                console.error("Cannot register because user already exists");
                socket.emit("register_response", sender, new Packet("User already exists", PacketStatus.ERR));
                return;
            }

            String user_id = null;
            try {
                user_id = saveUser(username, password, sender);
            }
            catch (IOException e) {
                socket.emit("register_response", sender, new Packet("Cannot create user", PacketStatus.ERR));
            }
            socket.emit("register_response", sender, new Packet(user_id, PacketStatus.OK));
            console.info(username + " registered");
        });

        socket.on("login", (sender, req) -> {
            console.info("/login requested.");

            String[] parts = req.message.split(" ");
            String username = parts[0], password = parts[1];

            boolean rs = userExists(username, password);

            Packet response = new Packet();
            if(rs) { // succeed!
                response.status = PacketStatus.OK;
                response.message = allUsers.get(username).id;

                this.userIps.put(username, sender); // save ip of this user
                console.info(username + " logged in.");
            }
            else {
                response.status = PacketStatus.ERR;
                console.error(username + " logs in failed.");
            }

            socket.emit("login_response", sender, response);
        });

        socket.on("get_all_users", (sender, req) -> {
            console.info("/get_all_user requested.");

            String userString = StringEx.joins(getUserArray(req.message));
            socket.emit("get_all_users_response", sender, new Packet(userString));
        });

        socket.on("message", (sender, req) -> {
            console.info("/message requested.");

            MessagePacket packet = (MessagePacket) req;
            // Send message to all users in this room
            for (String user : packet.recipients) {
                if(!userIps.containsKey(user)) {
                    console.errorf("Message sent to %s but the IP is unknown.\n", user);
                    continue;
                }
                HostName dest = userIps.get(user);
                socket.emit(user + "_message", dest, packet);
                console.info("Message forwarded to " + user);
            }
        });

        socket.on("broadcast_message", (sender, req) -> {
            console.info("/broadcast requested");

            MessagePacket packet = (MessagePacket) req;
            socket.broadcast("broadcast", packet);
        });

        socket.on("disconnected", (sender, req) -> {
            console.info("/logout requested.");

            String username = req.message;
            socket.broadcast("announcement", new Packet(username + " has disconnected."));
        });
    }
}
