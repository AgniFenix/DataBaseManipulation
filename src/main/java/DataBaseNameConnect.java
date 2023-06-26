public class DataBaseNameConnect {
    private static final String DEFAULT_DATABASE_NAME_CONNECT = "fenix";
    private String databasenameconnect; // Se ha eliminado la palabra clave static

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
    private static boolean isValidDatabaseName(String databasename) {
        // Add more validation logic here if needed
        return databasename.trim().length() > 0 && databasename.trim().length() <= 9;
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
    public void setDataBaseNameConnect(String databasenameconnect) { // Se ha eliminado la palabra clave static
        if (isValidDatabaseName(databasenameconnect)) {
            this.databasenameconnect = databasenameconnect;
            System.out.println("La base de datos se ha cambiado a: " + databasenameconnect);
        } else {
            throw new IllegalArgumentException("El nombre de la base de datos no es válido");
        }
    }
}