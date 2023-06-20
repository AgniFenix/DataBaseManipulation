import org.jetbrains.annotations.NotNull;

public class DataBaseNameConnect {
    private static final String DEFAULT_DATABASE_NAME_CONNECT = "fenix";
    private static String databasenameconnect;

    /**
     * Constructor for DataBaseNameConnect.
     */

    public DataBaseNameConnect() {
        this(DEFAULT_DATABASE_NAME_CONNECT);
    }

    public DataBaseNameConnect(String databasenameconnect) {
        setDataBaseNameConnect(databasenameconnect);
    }

    /**
     * Checks if the given database name is valid.
     *
     * @param databasename the name of the database to check
     * @return true if the database name is valid, false otherwise
     */
    private static boolean isValidDatabaseName(@NotNull String databasename) {
        // Add more validation logic here if needed
        return databasename.trim().length() > 0 && databasename.trim().length() <= 9 && !databasename.isEmpty();
    }

    /**
     * Returns the name of the database to connect to.
     *
     * @return the name of the database to connect to
     */
    public String getDataBaseNameConnect() {
        return databasenameconnect;
    }

    /**
     * Sets the name of the database to connect to.
     *
     * @param databasenameconnect the name of the database to connect to
     */
    public static void setDataBaseNameConnect(String databasenameconnect) {
        if (isValidDatabaseName(databasenameconnect)) {
            DataBaseNameConnect.databasenameconnect = databasenameconnect;
            System.out.println("La base de datos se ha cambiado a: " + databasenameconnect);
        } else {
            throw new IllegalArgumentException("El nombre de la base de datos no es válido");
        }
    }
}