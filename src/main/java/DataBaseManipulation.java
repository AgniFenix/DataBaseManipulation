import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.sql.*;
import java.util.*;

public class DataBaseManipulation {
    private final Logger logger = LoggerFactory.getLogger(ConnectDataBaseFromFile.class);

    /**
     * Crea una tabla en una base de datos.
     *
     * @param conn      la conexión a la base de datos
     * @param tableName el nombre de la tabla a crear
     * @param columns   un mapa que representa las columnas de la tabla y sus tipos de datos
     * @throws SQLException              si ocurre un error al crear la tabla
     * @throws InvalidParameterException si alguno de los parámetros de entrada no es válido
     */

    public void createTable(Connection conn, String tableName, Map<String, String> columns) throws SQLException, InvalidParameterException {
        validateConnection(conn);
        validateTableName(tableName);
        validateColumns(columns);

        if (!tableExists(conn, tableName)) {
            throw new SQLException("La tabla " + tableName + " ya existe");
        }

        String sql = buildCreateTableSql(tableName, columns);

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error("Error al crear la tabla {}: {}", tableName, e.getMessage());
            throw new SQLException("Error al crear la tabla " + tableName, e);

        }
    }

    private boolean isValidName(String name) {
        System.out.println("Valor de name: " + name);
        return name != null && !name.isEmpty() && name.matches("[a-zA-Z0-9_\"'.,:;]+");
    }

    private boolean tableExists(@NotNull Connection conn, String tableName) throws SQLException {
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        return !tables.next();
    }

    private void validateColumns(@NotNull Map<String, String> columns) {
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            if (isValidName(entry.getKey())) {
                logger.info("Valor de columna: " + columns + " es valido");
            } else {
                throw new InvalidParameterException("El nombre de la columna " + columns + " no es válido");
            }
            if (isValidType(entry.getValue())) {
                logger.info("Tipo de columna: " + columns + " es valido");
            } else {
                throw new InvalidParameterException("El tipo de la columna " + columns + " no es válido");
            }
        }
    }
    private boolean isValidType(String value) {
        return (value != null && !value.isEmpty() && value.matches("[a-zA-Z0-9_\"'.,:;]+")) || value.matches("\\d{2}/\\d{2}/\\d{4}") || (value.startsWith("VARCHAR(") && value.endsWith(")")) || (value.startsWith("CHAR(") && value.endsWith(")")) || (value.startsWith("DECIMAL(") && value.endsWith(")")) || value.equals("DATE");
    }


    /**
     * Construye una consulta SQL CREATE TABLE a partir de los parámetros proporcionados.
     *
     * @param tableName el nombre de la tabla a crear
     * @param columns   un mapa que representa las columnas de la tabla y sus tipos de datos
     * @return una cadena que representa la consulta SQL CREATE TABLE construida
     */
    @NotNull
    private String buildCreateTableSql(String tableName, @NotNull Map<String, String> columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ")
                .append(tableName)
                .append(" (");

        for (Map.Entry<String, String> entry : columns.entrySet()) {
            sql.append(entry.getKey())
                    .append(" ")
                    .append(entry.getValue())
                    .append(", ");
        }

        // Eliminar la última coma y espacio
        sql.setLength(sql.length() - 2);

        sql.append(")");

        return sql.toString();
    }

    /**
     * Actualiza una fila en una tabla en una base de datos.
     *
     * @param conn        la conexión a la base de datos
     * @param tableName   el nombre de la tabla
     * @param values      un mapa que representa los valores a actualizar en la fila
     * @param whereClause la cláusula WHERE para identificar la fila a actualizar
     * @throws SQLException si ocurre un error al actualizar la fila
     */

    public void updateRow(Connection conn, String tableName, Map<String, Object> values, String whereClause) throws SQLException {
        validateConnection(conn);
        validateTableName(tableName);
        validateValues(values);
        validateWhereClause(whereClause);

        if (!tableExists(conn, tableName)) {
            throw new SQLException("La tabla " + tableName + " no existe");
        }

        String sql = buildUpdateRowSql(tableName, values, whereClause);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 1;
            for (Object value : values.values()) {
                pstmt.setObject(i++, value);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al actualizar una fila en la tabla {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    private void validateConnection(Connection conn) {
        if (conn != null) {
            try {
                if (conn.isClosed()) {
                    throw new NullPointerException("La conexión a la base de datos se encuentra cerrada");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new NullPointerException("La conexión a la base de datos no puede ser nula");
        }
    }


    /**
     * Construye una consulta SQL UPDATE a partir de los parámetros proporcionados.
     *
     * @param tableName   el nombre de la tabla
     * @param values      un mapa que representa los valores a actualizar en la fila
     * @param whereClause la cláusula WHERE para identificar la fila a actualizar
     * @return una cadena que representa la consulta SQL UPDATE construida
     */
    @NotNull
    private String buildUpdateRowSql(String tableName, @NotNull Map<String, Object> values, String whereClause) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ")
                .append(tableName)
                .append(" SET ");

        for (String column : values.keySet()) {
            sql.append(column)
                    .append(" = ?, ");
        }

        // Eliminar la última coma y espacio
        sql.setLength(sql.length() - 2);

        sql.append(" WHERE ")
                .append(whereClause);

        return sql.toString();
    }


    public void deleteFromTable(Connection conn, String tableName, String whereClause) {
        validateTableName(tableName);
        validateWhereClause(whereClause);

        try {
            if (tableExists(conn, tableName)) {
                logger.error("La tabla {} no existe", tableName);
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        deleteDataFromTable(conn, tableName, whereClause);
    }

    private void deleteDataFromTable(@NotNull Connection conn, String tableName, String whereClause) {
        String sql = buildDeleteSql(tableName, whereClause);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int rowsAffected = pstmt.executeUpdate();
            logger.info("{} fila(s) eliminada(s) de la tabla {}", rowsAffected, tableName);
        } catch (SQLException e) {
            logger.error("Error al eliminar datos de la tabla {}: {}", tableName, e.getMessage());
        }
    }

    @NotNull
    private String buildDeleteSql(String tableName, String whereClause) {
        return new StringBuilder()
                .append("DELETE FROM ")
                .append(tableName)
                .append(" WHERE ")
                .append(whereClause)
                .toString();
    }

    /**
     * Inserta varias filas en una tabla en una base de datos.
     *
     * @param conn      la conexión a la base de datos
     * @param tableName el nombre de la tabla
     * @param rowsList  una lista de mapas que representan las filas a insertar
     * @throws InvalidParameterException si alguno de los parámetros de entrada no es válido
     */

    public void insertIntoTable(Connection conn, String tableName, List<Map<String, Object>> rowsList) throws InvalidParameterException, SQLException {
        validateConnection(conn);
        validateTableName(tableName);
        validateRowsList(rowsList);

        if (!tableExists(conn, tableName)) {
            throw new SQLException("La tabla " + tableName + " no existe");
        }

        try {
            conn.setAutoCommit(false);

            for (Map<String, Object> row : rowsList) {
                if (isRowValid(row)) {
                    insertRowIntoTable(conn, tableName, row);
                }
            }

            int[] rowsAffected = conn.createStatement().executeBatch();
            logger.info("{} fila(s) insertada(s) en la tabla {}", Arrays.stream(rowsAffected).sum(), tableName);

            conn.commit();
        } catch (SQLException e) {
            logger.error("Error al insertar datos en la tabla {}: {}", tableName, e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Error al deshacer las inserciones: {}", ex.getMessage());
            }
            throw new SQLException("Error al insertar filas en la tabla " + tableName, e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error("Error al reactivar el modo de confirmación automática: {}", e.getMessage());
            }
        }
    }

    /**
     * Inserta una fila en una tabla en una base de datos.
     *
     * @param conn      la conexión a la base de datos
     * @param tableName el nombre de la tabla
     * @param row       un mapa que representa la fila a insertar
     * @throws SQLException si ocurre un error al insertar la fila
     */
    private void insertRowIntoTable(Connection conn, String tableName, Map<String, Object> row) throws SQLException {
        // Construir la consulta SQL
        String sql = buildInsertSql(tableName, row);

        // Preparar y ejecutar la consulta
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 1;
            for (Object value : row.values()) {
                pstmt.setObject(i, value);
                i++;
            }
            pstmt.addBatch();
        } catch (SQLException e) {
            logger.error("Error al preparar la consulta: {}", e.getMessage());
            throw new SQLException("Error al insertar una fila en la tabla " + tableName, e);
        }
    }

    /**
     * Valida una lista de filas.
     *
     * @param rowsList la lista de filas a validar
     * @throws InvalidParameterException si la lista de filas no es válida
     */
    private void validateRowsList(List<Map<String, Object>> rowsList) throws InvalidParameterException {
        if (rowsList == null || rowsList.isEmpty()) {
            throw new InvalidParameterException("La lista de filas no puede ser nula o estar vacía");
        }
    }

    /**
     * Verifica si una fila es válida para ser insertada en una tabla.
     *
     * @param row la fila a validar
     * @return verdadero si la fila es válida, falso en caso contrario
     */
    private boolean isRowValid(Map<String, Object> row) {
        if (row == null || row.isEmpty()) {
            logger.warn("Las filas no pueden ser nulas o vacías. Omitiendo fila.");
            return false;
        }

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String column = entry.getKey();
            Object value = entry.getValue();

            if (!isValidName(column)) {
                logger.warn("El nombre de la columna {} no es válido. Omitiendo fila.", column);
                return false;
            }

            if (!isValidValue(value)) {
                logger.warn("El valor {} no es válido. Omitiendo fila.", value);
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica si un valor es válido para ser insertado en una tabla.
     *
     * @param value el valor a validar
     * @return verdadero si el valor es válido, falso en caso contrario
     */
    private boolean isValidValue(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof String) {
            return isValidStringValue((String) value);
        }
        return false;
    }

    /**
     * Verifica si un valor de tipo String es válido para ser insertado en una tabla.
     *
     * @param stringValue el valor de tipo String a validar
     * @return verdadero si el valor es válido, falso en caso contrario
     */
    private boolean isValidStringValue(@NotNull String stringValue) {
        if (!stringValue.matches("[a-zA-Z0-9]+")) {
            return false;
        }

        if (stringValue.length() > 250) {
            return false;
        }

        // Agregar más validaciones según sea necesario

        // Si todas las validaciones pasan, devolver true
        return true;
    }

    /**
     * Construye una consulta SQL INSERT a partir de los parámetros proporcionados.
     *
     * @param tableName el nombre de la tabla
     * @param row       un mapa que representa la fila a insertar
     * @return una cadena que representa la consulta SQL INSERT construida
     */
    @NotNull
    private String buildInsertSql(String tableName, @NotNull Map<String, Object> row) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            placeholders.append("?,");
        }
        placeholders.setLength(placeholders.length() - 1); // remove the last comma

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(", ", row.keySet()))
                .append(") VALUES (")
                .append(placeholders)
                .append(")");
        return sql.toString();
    }

    /**
     * Este método selecciona datos de una tabla en una base de datos.
     * Primero, valida los parámetros de entrada y verifica si la tabla existe.
     * Si la tabla existe, selecciona los datos y los devuelve como una lista de mapas.
     *
     * @param conn        la conexión a la base de datos
     * @param tableName   el nombre de la tabla
     * @param columns     las columnas a seleccionar
     * @param whereClause la cláusula WHERE para filtrar los resultados
     * @return una lista de mapas que representan los resultados de la consulta
     * @throws SQLException              si ocurre un error al acceder a la base de datos
     * @throws InvalidParameterException si alguno de los parámetros de entrada no es válido
     */

    public List<Map<String, Object>> selectFromTable(Connection conn, String tableName, String[] columns, String whereClause) throws SQLException, InvalidParameterException {
        validateConnection(conn);
        validateTableName(tableName);
        validateColumns(columns);
        validateWhereClause(whereClause);

        if (!tableExists(conn, tableName)) {
            throw new SQLException("La tabla " + tableName + " no existe");
        }

        return selectDataFromTable(conn, tableName, columns, whereClause);
    }

    /**
     * Valida el nombre de la tabla.
     *
     * @param tableName el nombre de la tabla a validar
     * @throws InvalidParameterException si el nombre de la tabla no es válido
     */
    private void validateTableName(String tableName) throws InvalidParameterException {
        if (isValidName(tableName)) {
            logger.info("El nombre de la tabla " + tableName + "es valida");
        } else {
            throw new InvalidParameterException("El nombre de la tabla no es válido");
        }
    }

    /**
     * Valida las columnas a seleccionar.
     *
     * @param columns las columnas a validar
     * @throws InvalidParameterException si las columnas no son válidas
     */
    private void validateColumns(String[] columns) throws InvalidParameterException {
        if (columns == null || columns.length == 0) {
            throw new InvalidParameterException("Debe proporcionar al menos una columna para seleccionar");
        }
        for (String column : columns) {
            if (isValidName(column)) {
                throw new InvalidParameterException("El nombre de la columna no es válido");
            }
        }
    }

    /**
     * Valida la cláusula WHERE.
     *
     * @param whereClause la cláusula WHERE a validar
     * @throws InvalidParameterException si la cláusula WHERE no es válida
     */
    private void validateWhereClause(String whereClause) throws InvalidParameterException {
        if (whereClause == null || whereClause.isEmpty()) {
            throw new InvalidParameterException("La cláusula WHERE no puede estar vacía");
        }
    }

    /**
     * Selecciona datos de una tabla en una base de datos.
     *
     * @param conn        la conexión a la base de datos
     * @param tableName   el nombre de la tabla
     * @param columns     las columnas a seleccionar
     * @param whereClause la cláusula WHERE para filtrar los resultados
     * @return una lista de mapas que representan los resultados de la consulta
     * @throws SQLException si ocurre un error al acceder a la base de datos
     */
    @NotNull
    private List<Map<String, Object>> selectDataFromTable(Connection conn, String tableName, String[] columns, String whereClause) throws SQLException {
        String sql = buildSelectSql(tableName, columns, whereClause);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            return resultSetToList(rs);
        } catch (SQLException e) {
            logger.error("Error al seleccionar datos de la tabla {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Construye una consulta SQL SELECT a partir de los parámetros proporcionados.
     *
     * @param tableName   el nombre de la tabla
     * @param columns     las columnas a seleccionar
     * @param whereClause la cláusula WHERE para filtrar los resultados
     * @return una cadena que representa la consulta SQL SELECT construida
     */
    @NotNull
    private String buildSelectSql(String tableName, String[] columns, String whereClause) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append(String.join(", ", columns))
                .append(" FROM ")
                .append(tableName)
                .append(" WHERE ")
                .append(whereClause);
        return sql.toString();
    }

    /**
     * Convierte un ResultSet en una lista de mapas.
     *
     * @param rs el ResultSet a convertir
     * @return una lista de mapas que representan los resultados del ResultSet
     * @throws SQLException si ocurre un error al acceder al ResultSet
     */
    @NotNull
    private List<Map<String, Object>> resultSetToList(@NotNull ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            results.add(row);
        }
        return results;
    }

    public List<Map<String, Object>> generateReport(Connection conn, String tableName, String whereClause) {
        validateConnection(conn);
        validateTableName(tableName);
        validateWhereClause(whereClause);

        String sql = buildSelectSql(tableName, whereClause);

        List<Map<String, Object>> reportData = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                reportData.add(row);
            }
        } catch (SQLException e) {
            logger.error("Error al generar el informe: {}", e.getMessage());
        }

        return reportData;
    }

    @NotNull
    private String buildSelectSql(String tableName, String whereClause) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ")
                .append(tableName)
                .append(" WHERE ")
                .append(whereClause);
        return sql.toString();
    }


    /**
     * Elimina una tabla en una base de datos.
     *
     * @param conn      la conexión a la base de datos
     * @param tableName el nombre de la tabla a eliminar
     * @throws SQLException si ocurre un error al eliminar la tabla
     */

    public void dropTable(Connection conn, String tableName) throws SQLException {
        validateConnection(conn);
        validateTableName(tableName);

        if (!tableExists(conn, tableName)) {
            throw new SQLException("La tabla " + tableName + " no existe");
        }

        String sql = buildDropTableSql(tableName);

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error("Error al eliminar la tabla {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Construye una consulta SQL DROP TABLE a partir del nombre de la tabla proporcionado.
     *
     * @param tableName el nombre de la tabla a eliminar
     * @return una cadena que representa la consulta SQL DROP TABLE construida
     */
    @NotNull
    @Contract(pure = true)
    private String buildDropTableSql(String tableName) {
        return "DROP TABLE " + tableName;
    }

    /**
     * Inserta una fila en una tabla en una base de datos.
     *
     * @param conn      la conexión a la base de datos
     * @param tableName el nombre de la tabla
     * @param values    un mapa que representa los valores a insertar en la fila
     * @throws SQLException si ocurre un error al insertar la fila
     */

    public void insertRow(Connection conn, String tableName, Map<String, Object> values) throws SQLException {
        validateConnection(conn);
        validateTableName(tableName);
        validateValues(values);

        if (!tableExists(conn, tableName)) {
            throw new SQLException("La tabla " + tableName + " no existe");
        }

        String sql = buildInsertRowSql(tableName, values);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int i = 1;
            for (Object value : values.values()) {
                pstmt.setObject(i++, value);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al insertar una fila en la tabla {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    private void validateValues(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            throw new InvalidParameterException("La fila no puede estar vacía");
        }
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (entry.getValue() == null) {
                throw new InvalidParameterException("La fila no puede estar vacía");
            }
        }
    }


    /**
     * Construye una consulta SQL INSERT a partir de los parámetros proporcionados.
     *
     * @param tableName el nombre de la tabla
     * @param values    un mapa que representa los valores a insertar en la fila
     * @return una cadena que representa la consulta SQL INSERT construida
     */
    @NotNull
    private String buildInsertRowSql(String tableName, @NotNull Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ")
                .append(tableName)
                .append(" (")
                .append(String.join(", ", values.keySet()))
                .append(") VALUES (");

        for (int i = 0; i < values.size(); i++) {
            sql.append("?, ");
        }

        // Eliminar la última coma y espacio
        sql.setLength(sql.length() - 2);

        sql.append(")");

        return sql.toString();
    }

    /**
     * Cuenta el número de filas en una tabla en una base de datos.
     *
     * @param conn      la conexión a la base de datos
     * @param tableName el nombre de la tabla
     * @return el número de filas en la tabla
     * @throws SQLException si ocurre un error al contar las filas
     */

    public int countRows(Connection conn, String tableName) throws SQLException {
        validateConnection(conn);
        validateTableName(tableName);

        if (!tableExists(conn, tableName)) {
            throw new SQLException("La tabla " + tableName + " no existe");
        }

        String sql = buildCountRowsSql(tableName);

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Error al contar las filas en la tabla " + tableName);
            }
        } catch (SQLException e) {
            logger.error("Error al contar las filas en la tabla {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Construye una consulta SQL COUNT(*) a partir del nombre de la tabla proporcionado.
     *
     * @param tableName el nombre de la tabla
     * @return una cadena que representa la consulta SQL COUNT(*) construida
     */
    @NotNull
    @Contract(pure = true)
    private String buildCountRowsSql(String tableName) {
        return "SELECT COUNT(*) FROM " + tableName;
    }

    public boolean restaurarBaseDeDatos(@NotNull Connection conn, String rutaDeRespaldo, String nombreBD, boolean reemplazar) throws SQLException {
        boolean exito = false;
        String sql = construirSqlRestaurarBaseDeDatos(nombreBD, reemplazar);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rutaDeRespaldo);
            pstmt.executeUpdate();
            exito = true;
        } catch (SQLException e) {
            manejarExcepcion(e, "Ocurrió un error al restaurar la base de datos");
        }

        if (exito) {
            exito = verificarRestauracion(conn);
        }
        return exito;
    }

    @NotNull
    private String construirSqlRestaurarBaseDeDatos(String nombreBD, boolean reemplazar) {
        StringBuilder sql = new StringBuilder();
        sql.append("RESTORE DATABASE ");
        if (nombreBD != null) {
            sql.append(nombreBD).append(" ");
        }
        sql.append("FROM DISK = ?");
        if (reemplazar) {
            sql.append(" WITH REPLACE");
        }
        return sql.toString();
    }

    private void manejarExcepcion(@NotNull SQLException e, String mensaje) throws SQLException {

        // Aquí puedes registrar el error en un archivo de registro en lugar de imprimirlo en la consola
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID() + ".sql");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(e.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.err.println("Ocurrió un error: " + e.getMessage());
        logger.error(mensaje + ": " + e.getMessage());
        throw e;
    }

    private boolean verificarRestauracion(Connection conn) throws SQLException {
        boolean exito = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM someTable")) {
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("La base de datos restaurada contiene " + count + " filas en someTable");
                exito = true;
            }
        } catch (SQLException e) {
            manejarExcepcion(e, "Ocurrió un error al verificar la base de datos restaurada");
        }
        return exito;
    }

    public boolean respaldarBaseDeDatos(@NotNull Connection conn, String rutaDeRespaldo, String nombreBD) throws SQLException {
        boolean exito = false;
        String sql = construirSqlRespaldarBaseDeDatos(nombreBD);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rutaDeRespaldo);
            pstmt.executeUpdate();
            exito = true;
        } catch (SQLException e) {
            manejarExcepcion(e, "Ocurrió un error al respaldar la base de datos");
        }

        if (exito) {
            exito = verificarRespaldo(rutaDeRespaldo);
        }
        return exito;
    }

    @NotNull
    private String construirSqlRespaldarBaseDeDatos(String nombreBD) {
        StringBuilder sql = new StringBuilder();
        sql.append("BACKUP DATABASE ");
        if (nombreBD != null) {
            sql.append(nombreBD).append(" ");
        }
        sql.append("TO DISK = ?");
        return sql.toString();
    }

    private boolean verificarRespaldo(String rutaDeRespaldo) {
        boolean exito = false;
        File archivoDeRespaldo = new File(rutaDeRespaldo);
        if (archivoDeRespaldo.exists() && archivoDeRespaldo.length() > 0) {
            System.out.println("Archivo de respaldo creado con éxito: " + rutaDeRespaldo);
            exito = true;
        } else {
            System.err.println("El archivo de respaldo no se creó o está vacío: " + rutaDeRespaldo);
        }
        return exito;
    }

    public List<String> verificarIntegridadDeBaseDeDatos(Connection conn, String nombreBD, String opcionDeReparacion, boolean usarTablock) throws SQLException {
        List<String> resultados = new ArrayList<>();
        String sql = construirSqlVerificarIntegridadDeBaseDeDatos(opcionDeReparacion, usarTablock);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreBD);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String mensaje = rs.getString("MessageText");
                resultados.add(mensaje);
            }
        } catch (SQLException e) {
            manejarExcepcion(e, "Ocurrió un error al verificar la integridad de la base de datos");
        }
        return resultados;
    }

    @NotNull
    private String construirSqlVerificarIntegridadDeBaseDeDatos(String opcionDeReparacion, boolean usarTablock) {
        StringBuilder sql = new StringBuilder();
        sql.append("DBCC CHECKDB(?) WITH NO_INFOMSGS, ALL_ERRORMSGS");
        if (opcionDeReparacion != null) {
            sql.append(", REPAIR_").append(opcionDeReparacion);
        }
        if (usarTablock) {
            sql.append(", TABLOCK");
        }
        return sql.toString();
    }
}