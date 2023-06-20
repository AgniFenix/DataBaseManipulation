import java.io.IOException;
import java.net.ServerSocket;

public class PortConnect {
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;
    private static final int DEFAULT_PORT = 3306;
    private int port;

    public PortConnect(String portConnect) {
        this(DEFAULT_PORT);
    }

    public PortConnect(int port) {
        setPortConnect(port);
    }

    public int getPortConnect() {
        return port;
    }

    public void setPortConnect(int port) {
        if (isPortNotAvailable(port)) {
            this.port = port;
            System.out.println("El puerto " + port + " es valido");
        } else {
            throw new IllegalArgumentException("El puerto no es válido o no está disponible");
        }
    }

    private boolean isPortNotAvailable(int port) {
        if (port <= MIN_PORT || port > MAX_PORT) {
            return false;
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return true;
        }
    }
}