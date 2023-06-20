import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectDataBaseFromFile {

    private static final Logger logger = LoggerFactory.getLogger(ConnectDataBaseFromFile.class);
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTED_PROPERTIES_FILE_NAME = "bade.properties";
    private static final String SECRET_KEY = "secretKey.key";
    private static final Cipher cipher;

    static {
        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            logger.error("Error al inicializar el cifrado", e);
            throw new RuntimeException("Error al inicializar el cifrado: " + e.getMessage(), e);
        }
    }

    /**
     * Connects to a database using encrypted information from a properties file.
     *
     * @return a connection to the database
     * @throws InvalidKeyException if the secret key is invalid
     */
    public static Connection connectToDataBaseFromFile() throws InvalidKeyException, SQLException, IOException {
        byte[] keyData = Files.readAllBytes(Paths.get(SECRET_KEY));
        SecretKey SECRET_KEY = new SecretKeySpec(keyData, ENCRYPTION_ALGORITHM);
        Properties properties = readEncryptedPropertiesFile(SECRET_KEY);
        String url = properties.getProperty("url");
        String username = properties.getProperty("user");
        String password = properties.getProperty("password");
        String databaseName = properties.getProperty("databaseName");

        String urlFull = url + databaseName;
        Connection conn = DriverManager.getConnection(urlFull, username, password);
        logger.info("Conexión exitosa a la base de datos usando información cifrada desde el archivo");
        return conn;
    }

    /**
     * Reads and decrypts an encrypted properties file.
     *
     * @param secretKey the secret key to use for decrypting the properties file
     * @return the decrypted properties
     * @throws InvalidKeyException if the secret key is invalid
     */
    @NotNull
    private static Properties readEncryptedPropertiesFile(SecretKey secretKey) throws InvalidKeyException {
        // Leer el contenido cifrado del archivo
        byte[] encryptedContent;
        try {
            encryptedContent = Files.readAllBytes(Paths.get(ENCRYPTED_PROPERTIES_FILE_NAME));
        } catch (IOException e) {
            logger.error("Error al leer el archivo de propiedades cifrado", e);
            throw new RuntimeException("Error al leer el archivo de propiedades cifrado: " + e.getMessage(), e);
        }

        // Descifrar el contenido del archivo
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedContent;
        try {
            decryptedContent = cipher.doFinal(encryptedContent);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("Error al descifrar el archivo de propiedades", e);
            throw new RuntimeException("Error al descifrar el archivo de propiedades: " + e.getMessage(), e);
        }

        // Cargar las propiedades desde el contenido descifrado
        Properties properties = new Properties();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(decryptedContent)) {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("Error al cargar las propiedades desde el archivo descifrado", e);
            throw new RuntimeException("Error al cargar las propiedades desde el archivo descifrado: " + e.getMessage(), e);
        }
        return properties;
    }
}