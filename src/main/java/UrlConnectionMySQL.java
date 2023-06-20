import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UrlConnectionMySQL {

    private final IpAddressConfig ipAddress;
    private final PortConnect port;
    private final UserConnect user;
    private final PasswordConnect password;
    private final DataBaseNameConnect databaseName;
    private static final String BASE_URL = "jdbc:mysql://";
    private static final Logger LOGGER = Logger.getLogger(UrlConnectionMySQL.class.getName());
    private Connection connection;

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     */
    public UrlConnectionMySQL(IpAddressConfig ipAddress) {
        this(ipAddress, null, null, null, null);
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     * @param port      the port of the MySQL server
     */
    public UrlConnectionMySQL(IpAddressConfig ipAddress, PortConnect port) {
        this(ipAddress, port, null, null, null);
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     * @param port      the port of the MySQL server
     * @param user      the port of the MySQL server
     */
    public UrlConnectionMySQL(IpAddressConfig ipAddress, PortConnect port, UserConnect user) {
        this(ipAddress, port, user, null, null);
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     * @param port      the port of the MySQL server
     * @param user      the port of the MySQL server
     * @param password  the port of the MySQL server
     */
    public UrlConnectionMySQL(IpAddressConfig ipAddress, PortConnect port, UserConnect user, PasswordConnect password) {
        this(ipAddress, port, user, password, null);
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     */
    public UrlConnectionMySQL(IpAddressConfig ipAddress, PortConnect port, UserConnect user, PasswordConnect password, DataBaseNameConnect databaseName) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.user = user;
        this.password = password;
        this.databaseName = databaseName;
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     */
    public UrlConnectionMySQL(String ipAddress) {
        this.ipAddress = new IpAddressConfig(ipAddress);
        this.port = null;
        this.user = null;
        this.password = null;
        this.databaseName = null;
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     * @param port      the port of the MySQL server
     */
    public UrlConnectionMySQL(String ipAddress, int port) {
        this.ipAddress = new IpAddressConfig(ipAddress);
        this.port = new PortConnect(port);
        this.user = null;
        this.password = null;
        this.databaseName = null;
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     * @param port      the port of the MySQL server
     */
    public UrlConnectionMySQL(String ipAddress, int port, String user) {
        this.ipAddress = new IpAddressConfig(ipAddress);
        this.port = new PortConnect(port);
        this.user = new UserConnect(user);
        this.password = null;
        this.databaseName = null;
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     * @param port      the port of the MySQL server
     */
    public UrlConnectionMySQL(String ipAddress, int port, String user, String password) {
        this.ipAddress = new IpAddressConfig(ipAddress);
        this.port = new PortConnect(port);
        this.user = new UserConnect(user);
        this.password = new PasswordConnect(password);
        this.databaseName = null;
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Creates a new instance of UrlConnectionMysql.
     *
     * @param ipAddress the IP address of the MySQL server
     * @param port      the port of the MySQL server
     */

    public UrlConnectionMySQL(String ipAddress, int port, String user, String password, String databaseName) {
        this.ipAddress = new IpAddressConfig(ipAddress);
        this.port = new PortConnect(port);
        this.user = new UserConnect(user);
        this.password = new PasswordConnect(password);
        this.databaseName = new DataBaseNameConnect(databaseName);
        System.out.println("URL: " + getFullUrl());
        LOGGER.log(Level.INFO, "URL: " + getFullUrl());
    }

    /**
     * Connects to the MySQL database using the provided configuration.
     */
    public void urlConnectionMySQL() {
        String url = getFullUrl();
        String user = this.user.getUserConnect();
        String password = this.password.getPasswordConnect();

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión a la base de datos exitosa");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * Disconnects from the MySQL database.
     */
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Desconexión de la base de datos exitosa");
            } catch (SQLException e) {
                handleSQLException(e);
            }
        }
    }

    private void handleSQLException(SQLException e) {
        LOGGER.log(Level.SEVERE, "Error al conectar a la base de datos", e);
        System.out.println("Error al conectar a la base de datos: " + e.getMessage());
    }

    /**
     * Returns the IP address of the MySQL server.
     *
     * @return the IP address of the MySQL server
     */
    public IpAddressConfig getIpAddress() {
        return ipAddress;
    }

    /**
     * Returns the port number of the MySQL server.
     *
     * @return the port number of the MySQL server
     */

    public PortConnect getPort() {
        return port;
    }

    /**
     * Returns the username for connecting to the MySQL server.
     *
     * @return the username for connecting to the MySQL server
     */
    public UserConnect getUser() {
        return user;
    }

    /**
     * Returns the password for connecting to the MySQL server.
     *
     * @return the password for connecting to the MySQL server
     */
    public PasswordConnect getPassword() {
        return password;
    }

    /**
     * Returns the name of the MySQL database to connect to.
     *
     * @return the name of the MySQL database to connect to
     */
    public DataBaseNameConnect getDatabaseName() {
        return databaseName;
    }

    /**
     * Returns the base URL for connecting to the MySQL server.
     *
     * @return the base URL for connecting to the MySQL server
     */
    public String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Returns the full URL for connecting to the MySQL server.
     *
     * @return the full URL for connecting to the MySQL server
     */
    public String getFullUrl() {
        if (ipAddress == null || port == null || databaseName == null) {
            throw new IllegalStateException("Missing required fields for constructing URL");
        }
        return getBaseUrl() + ipAddress.getIpAddressConfig() + ":" + port.getPortConnect() + "/" + databaseName.getDataBaseNameConnect();
    }

    /**
     * Returns the connection to the MySQL database.
     *
     * @return the connection to the MySQL database
     */
    public Connection getConnection() {
        return connection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlConnectionMySQL that = (UrlConnectionMySQL) o;
        return Objects.equals(ipAddress, that.ipAddress) &&
                Objects.equals(port, that.port) &&
                Objects.equals(user, that.user) &&
                Objects.equals(password, that.password) &&
                Objects.equals(databaseName, that.databaseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress, port, user, password, databaseName);
    }

    @Override
    public String toString() {
        return "DatabaseConnection{" +
                "ipAddress=" + ipAddress +
                ", port=" + port +
                ", user=" + user +
                ", password=" + password +
                ", databaseName=" + databaseName +
                '}';
    }
}