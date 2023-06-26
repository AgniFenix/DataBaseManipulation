import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class IpAddressConfig {

    private String ipaddressconfig;
    private static final String DEFAULT_IP = "127.0.0.1";

    /**
     * Constructor para ConfiguracionDireccionIP.
     */
    public IpAddressConfig() {
        this(DEFAULT_IP);
    }

    public IpAddressConfig(String ipaddressconfig) {
        setIpAddressConfig(ipaddressconfig);
    }


    /**
     * Comprueba si la dirección IP dada es válida.
     *
     * @param ipaddressconfig config la dirección IP a comprobar
     * @return true si la dirección IP es válida, false en caso contrario
     */
    private boolean isValidIpAddress(String ipaddressconfig) {
        return ipaddressconfig.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        // Añade más lógica de validación aquí si es necesario
        // Por ejemplo, comprueba si la dirección IP es una dirección de difusión o reservada
    }

    /**
     * Devuelve la dirección IP actual.
     *
     * @return la dirección IP actual
     */
    public String getIpAddressConfig() {
        return ipaddressconfig;
    }

    /**
     * Establece la dirección IP actual.
     *
     * @param ipaddressconfig la nueva dirección IP
     */
    public void setIpAddressConfig(String ipaddressconfig) {
        if (ipaddressconfig == null) { // Se ha añadido una comprobación de nulidad
            throw new IllegalArgumentException("La dirección IP no puede ser nula");
        }
        if (isValidIpAddress(ipaddressconfig)) {
            this.ipaddressconfig = ipaddressconfig;
            System.out.println("La ip " + ipaddressconfig + " es valida");
        } else {
            throw new IllegalArgumentException("IP address invalida");
        }
    }
}