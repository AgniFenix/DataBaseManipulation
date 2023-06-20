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
import java.util.Properties;

/**
 * Class for creating a database and generating a properties file.
 */
public class CreateFileFromDataBaseConnect {

    private static final Logger logger = LoggerFactory.getLogger(ConnectDataBaseFromFile.class);
    private static final String PROPERTIES_FILE_NAME = "bade.properties";
    private static final String ENCRYPTED_PROPERTIES_FILE_NAME = "bade.properties";
    private IpAddressConfig ipAddress;
    private PortConnect portConnect;
    private UserConnect userConnect;
    private PasswordConnect passwordConnect;
    private DataBaseNameConnect dataBaseNameConnect;
    private static final String BASE_URL = "jdbc:mysql://";
    private static final String FILE_KEY_PATCH = "secretKey.key";


    /**
     * Creates a database and generates a properties file.
     *
     */
    public CreateFileFromDataBaseConnect(IpAddressConfig ipAddress, PortConnect portConnect, UserConnect userConnect, PasswordConnect passwordConnect, DataBaseNameConnect dataBaseNameConnect) {
        try {
            String url = BASE_URL + ipAddress.getIpAddressConfig() + ":" + portConnect.getPortConnect() + "/";
            Connection connection = DriverManager.getConnection(url, userConnect.getUserConnect(), passwordConnect.getPasswordConnect());
            if (connection == null) {
                logger.error("Error a crear archivos para la conexion {}", dataBaseNameConnect);
            } else {
                logger.info("Archivos de conexion para la base de datos {} creados", dataBaseNameConnect);
            }
            this.ipAddress = ipAddress;
            this.portConnect = portConnect;
            this.userConnect = userConnect;
            this.passwordConnect = passwordConnect;
            this.dataBaseNameConnect = dataBaseNameConnect;
            Properties properties = new Properties();
            properties.setProperty("url", url);
            properties.setProperty("ip", ipAddress.getIpAddressConfig());
            properties.setProperty("port", String.valueOf(portConnect.getPortConnect()));
            properties.setProperty("user", userConnect.getUserConnect());
            properties.setProperty("password", passwordConnect.getPasswordConnect());
            properties.setProperty("databaseName", dataBaseNameConnect.getDataBaseNameConnect());
            saveProperties(properties);

            // Cifrar el archivo de propiedades después de guardarlo
            encryptFile(PROPERTIES_FILE_NAME, ENCRYPTED_PROPERTIES_FILE_NAME, FILE_KEY_PATCH);

        } catch (Exception e) {
            logger.error("Error a crear archivos para la conexion {}", dataBaseNameConnect);
            try {
                throw e;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public CreateFileFromDataBaseConnect(String ipAddress, String portConnect, String userConnect, String passwordConnect, String dataBaseNameConnect) {
        try {
            String url = BASE_URL + ipAddress + ":" + portConnect + "/";
            Connection connection = DriverManager.getConnection(url, userConnect, passwordConnect);
            if (connection == null) {
                logger.info("Error a crear archivos para la conexion {}", dataBaseNameConnect);
            } else {
                logger.info("Archivos de conexion para la base de datos {} creados", dataBaseNameConnect);
            }
            this.ipAddress = new IpAddressConfig(ipAddress);
            this.portConnect = new PortConnect(portConnect);
            this.userConnect = new UserConnect(userConnect);
            this.passwordConnect = new PasswordConnect(passwordConnect);
            this.dataBaseNameConnect = new DataBaseNameConnect(dataBaseNameConnect);

            Properties properties = new Properties();
            properties.setProperty("url", url);
            properties.setProperty("ip", ipAddress);
            properties.setProperty("port", portConnect);
            properties.setProperty("user", userConnect);
            properties.setProperty("password", passwordConnect);
            properties.setProperty("databaseName", dataBaseNameConnect);
            saveProperties(properties);

            // Cifrar el archivo de propiedades después de guardarlo
            encryptFile(PROPERTIES_FILE_NAME, ENCRYPTED_PROPERTIES_FILE_NAME, FILE_KEY_PATCH);

        } catch (Exception e) {
            logger.error("Error a crear archivos para la conexion {}", dataBaseNameConnect);
            try {
                throw e;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void saveProperties(@NotNull Properties properties) {
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE_NAME)) {
            properties.store(out, null);
        } catch (IOException e) {
            logger.error("Error al guardar el archivo de propiedades: {}", e.getMessage());
            try {
                throw e;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
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
        logger.info("Archivo cifrado exitosamente");
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
        logger.info("Archivo descifrado exitosamente");
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

    public void setIpAddress(IpAddressConfig ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Returns the port number of the MySQL server.
     *
     * @return the port number of the MySQL server
     */
    public PortConnect getPortConnect() {
        return portConnect;
    }

    public void setPortConnect(PortConnect portConnect) {
        this.portConnect = portConnect;
    }

    /**
     * Returns the username for connecting to the MySQL server.
     *
     * @return the username for connecting to the MySQL server
     */
    public UserConnect getUserConnect() {
        return userConnect;
    }

    public void setUserConnect(UserConnect userConnect) {
        this.userConnect = userConnect;
    }

    /**
     * Returns the password for connecting to the MySQL server.
     *
     * @return the password for connecting to the MySQL server
     */
    public PasswordConnect getPasswordConnect() {
        return passwordConnect;
    }

    public void setPasswordConnect(PasswordConnect passwordConnect) {
    this.passwordConnect = passwordConnect;
    }
    /**
     * Returns the name of the MySQL database to connect to.
     *
     * @return the name of the MySQL database to connect to
     */

    public DataBaseNameConnect getDataBaseNameConnect() {
        return dataBaseNameConnect;
    }

    public void setDataBaseNameConnect(DataBaseNameConnect dataBaseNameConnect) {
        this.dataBaseNameConnect = dataBaseNameConnect;
    }

    @Override
    public String toString() {
        return "DatabaseConnection{" +
                "ipAddress=" + ipAddress +
                ", port=" + portConnect +
                ", user=" + userConnect +
                ", password=" + passwordConnect +
                ", databaseName=" + dataBaseNameConnect +
                '}';
    }
}