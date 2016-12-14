
package hashmap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.mysql.jdbc.Statement;
import inverted_index.InvertedIndex;
import node_info.CategoryTree;
import node_info.NodeInfo;
import tfidfDocument.DocumentParser;
import utils.StringProcessingUtils;

public class ConnectDb {
	private static Connection connection;
	static private String driver = null;
	static private String db = null;
	static private String host = null;
	static private String username = null;
	static private String password = null;
	static private int port = 0;
	private static String engine = null;

	private static Properties properties = null;
	
	public static void initProperties() {
		ConnectDb.properties = new Properties();
		try {
			ConnectDb.properties.load(new FileInputStream("taxonomy.properties"));
		} catch (final IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		String property = null;
		property = ConnectDb.properties.getProperty("driver");
		ConnectDb.setDriver(property);

		property = ConnectDb.properties.getProperty("engine");
		ConnectDb.setEngine(property);

		property = ConnectDb.properties.getProperty("host");
		ConnectDb.setHost(property);
		property = ConnectDb.properties.getProperty("db");
		ConnectDb.setDB(property);
		property = ConnectDb.properties.getProperty("username");
		ConnectDb.setUsername(property);
		property = ConnectDb.properties.getProperty("password");
		ConnectDb.setPassword(property);
		property = ConnectDb.properties.getProperty("port");
		ConnectDb.setPort(Integer.parseInt(property));

	}

	public static void initPropertiesForSave() {
		ConnectDb.properties = new Properties();
		try {
			ConnectDb.properties.load(new FileInputStream("train_test_taxonomy.properties"));
		} catch (final IOException e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		String property = null;
		property = ConnectDb.properties.getProperty("driver");
		ConnectDb.setDriver(property);

		property = ConnectDb.properties.getProperty("engine");
		ConnectDb.setEngine(property);

		property = ConnectDb.properties.getProperty("host");
		ConnectDb.setHost(property);
		property = ConnectDb.properties.getProperty("db");
		ConnectDb.setDB(property);
		property = ConnectDb.properties.getProperty("username");
		ConnectDb.setUsername(property);
		property = ConnectDb.properties.getProperty("password");
		ConnectDb.setPassword(property);
		property = ConnectDb.properties.getProperty("port");
		ConnectDb.setPort(Integer.parseInt(property));

	}
	/**
	 * @return Returns the connection to JDBC database specified in properties
	 *         file.
	 */
	public static Connection getConnection() {
		try {
			if (ConnectDb.connection == null) {
				ConnectDb.connection = ConnectDb.connect();
			} else if (ConnectDb.connection.isClosed()) {
				ConnectDb.connection = ConnectDb.connect();
			}
		} catch (final Exception e) {
			e.printStackTrace(System.err);
		}
		return ConnectDb.connection;
	}

	private static String getJDBC_URL() {
		String jdbc_url = null;
		if (ConnectDb.getEngine().compareToIgnoreCase("mysql") == 0) {
			jdbc_url = "jdbc:mysql://" + ConnectDb.getHost() + "/" + ConnectDb.getDB() + "?user="
					+ ConnectDb.getUsername() + "&password=" + ConnectDb.getPassword()
					+ "&useUnicode=true&characterEncoding=UTF-8";
		} else if (ConnectDb.getEngine().compareToIgnoreCase("postgresql") == 0) {
			jdbc_url = "jdbc:postgresql://" + ConnectDb.getHost() + "/" + ConnectDb.getDB() + "?user="
					+ ConnectDb.getUsername() + "&password=" + ConnectDb.getPassword();
		}
		return jdbc_url;
	}

	private static Connection connect() {
		String jdbcclass = null;
		jdbcclass = ConnectDb.getDriver();
		String jdbc_url = ConnectDb.getJDBC_URL();
		try {
			Class.forName(jdbcclass);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace(System.err);
			return null;
		}

		try {
			ConnectDb.setConnection(DriverManager.getConnection(jdbc_url));
			ConnectDb.getConnection().setAutoCommit(false);
			return ConnectDb.getConnection();
		} catch (final SQLException e) {
			e.printStackTrace(System.err);
			return null;
		}
	}

	private static void disconnect() {
		try {
			ConnectDb.getConnection().close();
		} catch (final SQLException e) {
			e.printStackTrace(System.err);
		}
	}

	private static void setConnection(final Connection connection) {
		ConnectDb.connection = connection;
	}

	private static void setEngine(final String engine) {
		ConnectDb.engine = engine;
	}

	public static String getEngine() {
		return ConnectDb.engine;
	}

	private static void setDB(final String db) {
		ConnectDb.db = db;
	}

	public static String getDB() {
		return ConnectDb.db;
	}

	private static void setHost(final String host) {
		ConnectDb.host = host;
	}

	public static String getHost() {
		return ConnectDb.host;
	}

	private static void setPassword(final String password) {
		ConnectDb.password = password;
	}

	private static String getPassword() {
		return ConnectDb.password;
	}

	private static void setPort(final int port) {
		ConnectDb.port = port;
	}

	private static int getPort() {
		return ConnectDb.port;
	}

	private static void setUsername(final String username) {
		ConnectDb.username = username;
	}

	public static String getUsername() {
		return ConnectDb.username;
	}

	private static void setDriver(final String driver) {
		ConnectDb.driver = driver;
	}

	private static String getDriver() {
		return ConnectDb.driver;
	}

	public static boolean checkConnection() {
		boolean result = true;
		ConnectDb.connection = ConnectDb.getConnection();
		try {
			ConnectDb.connection.close();
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			result = false;
		}
		return result;
	}

}
