import java.io.IOException;
import java.net.ServerSocket;

public class PortConnect {
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;
    private static final int DEFAULT_PORT = 3306;
    private int portConnect;

    public PortConnect() {
        this(DEFAULT_PORT);
    }

    public PortConnect(int portConnect) {
        setPortConnect(portConnect);
    }

    public int getPortConnect() {
        return portConnect;
    }

    public void setPortConnect(int portConnect) {
        if (isPortNotAvailable(portConnect)) {
            this.portConnect = portConnect;
            System.out.println("El puerto " + portConnect + " es valido");
        } else {
            throw new IllegalArgumentException("El puerto no es válido o no está disponible");
        }
    }

    private boolean isPortNotAvailable(int portConnect) {
        if (portConnect <= MIN_PORT || portConnect > MAX_PORT) {
            return false;
        }
        try (ServerSocket serverSocket = new ServerSocket(portConnect)) {
            return false;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return true;
        }
    }
}