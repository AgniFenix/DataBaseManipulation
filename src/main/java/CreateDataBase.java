import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * Class for creating a database and generating a properties file.
 */
public class CreateDataBase {

    private static final Logger logger = LoggerFactory.getLogger(ConnectDataBaseFromFile.class);
    private static final String PROPERTIES_FILE_NAME = "bade.properties";
    private static final String ENCRYPTED_PROPERTIES_FILE_NAME = "bade.properties";
    private final IpAddressConfig ipAddress;
    private final PortConnect portConnect;
    private final UserConnect user;
    private final PasswordConnect password;
    private final NewDataBaseName newDataBaseName;
    private static final String BASE_URL = "jdbc:mysql://";
    private static final String FILE_KEY_PATCH = "secretKey.key";
    private static DataBaseCreator dataBaseCreator;

    /**
     * Creates a database and generates a properties file.
     *
     * @throws SQLException if an error occurs while creating the database
     */
    public CreateDataBase(IpAddressConfig ipAddress, PortConnect portConnect, UserConnect user, PasswordConnect password, NewDataBaseName newDataBaseName) throws SQLException {
        try {
            String url = BASE_URL + ipAddress.getIpAddressConfig() + ":" + portConnect.getPortConnect() + "/";
            dataBaseCreator = new MySqlDataBaseCreator(url, user.getUserConnect(), password.getPasswordConnect());
            boolean databaseCreated = dataBaseCreator.createDataBase(newDataBaseName.getNewDataBaseName());
            if (databaseCreated) {
                logger.info("No se pudo crear la base de datos {}", newDataBaseName);
            } else {
                logger.info("Base de Datos {} creada", newDataBaseName);
            }
            this.ipAddress = ipAddress;
            this.portConnect = portConnect;
            this.user = user;
            this.password = password;
            this.newDataBaseName = newDataBaseName;
            Properties properties = new Properties();
            properties.setProperty("url", url);
            properties.setProperty("ip", ipAddress.getIpAddressConfig());
            properties.setProperty("port", String.valueOf(portConnect.getPortConnect()));
            properties.setProperty("user", user.getUserConnect());
            properties.setProperty("password", password.getPasswordConnect());
            properties.setProperty("databaseName", newDataBaseName.getNewDataBaseName());
            saveProperties(properties);

            // Cifrar el archivo de propiedades después de guardarlo
            encryptFile(PROPERTIES_FILE_NAME, ENCRYPTED_PROPERTIES_FILE_NAME, FILE_KEY_PATCH);

        } catch (SQLException e) {
            throw new SQLException("Error al crear la base de datos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new DatabaseCreationException("Error al crear la base de datos", e);
        }
    }

    private void saveProperties(@NotNull Properties properties) throws IOException {
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE_NAME)) {
            properties.store(out, null);
        } catch (IOException e) {
            logger.error("Error al guardar el archivo de propiedades: {}", e.getMessage());
            throw e;
        }
    }

    public static class DatabaseCreationException extends RuntimeException {
        public DatabaseCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void encryptFile(String inputFilePath, String outputFilePath, String keyFilePath) throws Exception {
        // Generar una clave secreta
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();

        // Guardar la clave secreta en un archivo
        try (FileOutputStream keyOutputStream = new FileOutputStream(keyFilePath)) {
            keyOutputStream.write(secretKey.getEncoded());
        }

        // Crear un objeto Cipher para cifrar el archivo
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Leer el contenido del archivo
        byte[] fileContent = Files.readAllBytes(Paths.get(inputFilePath));

        // Cifrar el contenido del archivo
        byte[] encryptedContent = cipher.doFinal(fileContent);

        // Guardar el contenido cifrado en un nuevo archivo
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            outputStream.write(encryptedContent);
        }
    }

    public static void decryptFile(String inputFilePath, String outputFilePath, String keyFilePath) throws Exception {
        // Leer la clave secreta del archivo
        byte[] keyData = Files.readAllBytes(Paths.get(keyFilePath));
        SecretKey secretKey = new SecretKeySpec(keyData, "AES");

        // Crear un objeto Cipher para descifrar el archivo
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Leer el contenido cifrado del archivo
        byte[] encryptedContent = Files.readAllBytes(Paths.get(inputFilePath));

        // Descifrar el contenido del archivo
        byte[] decryptedContent = cipher.doFinal(encryptedContent);

        // Guardar el contenido descifrado en un nuevo archivo
        try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            outputStream.write(decryptedContent);
        }
    }

    /**
     * Creates a database and generates a properties file.
     *
     * @return {@code true} if the database was successfully created, {@code false} otherwise
     */
    public static DataBaseCreator getDataBaseCreator() {
        return dataBaseCreator;
    }

    /**
     * Sets the database creator.
     *
     * @param dataBaseCreator the database creator
     */
    public static void setDataBaseCreator(DataBaseCreator dataBaseCreator) {
        CreateDataBase.dataBaseCreator = dataBaseCreator;
    }

    /**
     * Returns the base URL for connecting to the MySQL server.
     *
     * @return the base URL for connecting to the MySQL server
     */
    public String getBaseUrl() {
        return BASE_URL;
    }

    public String getFullUrl() {
        if (ipAddress == null || portConnect == null) {
            throw new IllegalStateException("Missing required fields for constructing URL");
        }
        return getBaseUrl() + ipAddress.getIpAddressConfig() + ":" + portConnect.getPortConnect() + "/";
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
    public PortConnect getPortConnect() {
        return portConnect;
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
    public NewDataBaseName getDataBaseName() {
        return newDataBaseName;
    }


    @Override
    public String toString() {
        return "DatabaseConnection{" +
                "ipAddress=" + ipAddress +
                ", port=" + portConnect +
                ", user=" + user +
                ", password=" + password +
                ", databaseName=" + newDataBaseName +
                '}';
    }

    public interface DataBaseCreator {
        boolean createDataBase(String newDataBaseName) throws SQLException;
    }

    public static class MySqlDataBaseCreator implements DataBaseCreator {

        private final String url;
        private final String user;
        private final String password;

        public MySqlDataBaseCreator(String url, String user, String password) {
            this.url = Objects.requireNonNull(url);
            this.user = Objects.requireNonNull(user);
            this.password = Objects.requireNonNull(password);
        }

        @Override
        public boolean createDataBase(String newDataBaseName) throws SQLException {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                conn.createStatement().execute("CREATE DATABASE " + newDataBaseName);
                return true;
            } catch (SQLException e) {
                throw new SQLException("Error al crear la base de datos: " + e.getMessage(), e);
            }
        }

        @Override
        public String toString() {
            return "MySqlDataBaseCreator{" +
                    "url='" + url + '\'' +
                    ", user='" + user + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    public static class PostgreSqlDataBaseCreator implements DataBaseCreator {

        private final String url;
        private final String user;
        private final String password;

        public PostgreSqlDataBaseCreator(String url, String user, String password) {
            this.url = Objects.requireNonNull(url);
            this.user = Objects.requireNonNull(user);
            this.password = Objects.requireNonNull(password);
        }

        @Override
        public boolean createDataBase(String newDataBaseName) throws SQLException {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                conn.createStatement().execute("CREATE DATABASE " + newDataBaseName);
                return true;
            } catch (SQLException e) {
                throw new SQLException("Error al crear la base de datos: " + e.getMessage(), e);
            }
        }

        @Override
        public String toString() {
            return "PostgreSqlDataBaseCreator{" +
                    "url='" + url + '\'' +
                    ", user='" + user + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
}