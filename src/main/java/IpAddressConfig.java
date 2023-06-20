import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class IpAddressConfig {
    private String ipaddressconfig;
    private static final String DEFAULT_IP = "127.0.0.1";

    /**
     * Constructor for IpAddressConfig.
     */
    public IpAddressConfig() {
        this(DEFAULT_IP);
    }

    public IpAddressConfig(String ipaddressconfig) {
        setIpAddressConfig(ipaddressconfig);
    }


    /**
     * Checks if the given IP address is valid.
     *
     * @param ipaddressconfig the IP address to check
     * @return true if the IP address is valid, false otherwise
     */
    @Contract(pure = true)
    private boolean isValidIpAddress(@NotNull String ipaddressconfig) {
        return ipaddressconfig.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        // Add more validation logic here if needed
        // For example, check if the IP address is a broadcast or reserved address
    }

    /**
     * Returns the current IP address.
     *
     * @return the current IP address
     */
    public String getIpAddressConfig() {
        return ipaddressconfig;
    }

    /**
     * Sets the current IP address.
     *
     * @param ipaddressconfig the new IP address
     */
    public void setIpAddressConfig(String ipaddressconfig) {
        if (isValidIpAddress(ipaddressconfig)) {
            this.ipaddressconfig = ipaddressconfig;
            System.out.println("La ip " + ipaddressconfig + " es valida");
        } else {
            throw new IllegalArgumentException("IP address invalida");
        }
    }
}