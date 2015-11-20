import socketex.core.HostName;
import socketex.core.Packet;
import socketex.core.SocketEx;

import java.io.IOException;

/**
 * Created by mt on 11/15/2015.
 */
public class client {
    public static void main(final String []args) throws IOException, InterruptedException {
        SocketEx socket = new SocketEx("127.0.0.1", 4000);
        socket.connect("192.168.137.166", 2015);

        socket.broadcast("custom", new Packet("The jokes on you!"));
    }
}
