package gui.sqlite;

import gui.data.CarData;
import gui.data.LightData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteAccessor {
	
	private static SQLiteAccessor SQLITE;
	private static String DATABASE_NAME;
	
	public static void setDataBaseName(String databaseName) {
		
		if(SQLiteAccessor.DATABASE_NAME != null && SQLiteAccessor.DATABASE_NAME != databaseName && SQLITE != null) {
			SQLITE.close();
		}
		SQLiteAccessor.DATABASE_NAME = databaseName;
	}
	
	private static class Holder {
		private static final SQLiteAccessor INSTANCE = new SQLiteAccessor();
	}
	
	public static SQLiteAccessor getSQLite() {
		//thread safe headache...
//		if(SQLITE.DATABASE_NAME == null) {
//			SQLITE.init(DATABASE_NAME);
//		}
		return Holder.INSTANCE;
	}
	
	static class SQLite {
		private static final String JDBC_DRIVER = "org.sqlite.JDBC";;
		private static final String DATABASE_PATH = "jdbc:sqlite:";
		
		private static Connection connection;
		
		private static String databaseName = "db.sqlite";
		
		public static void setDatabaseName(String newName) {
			SQLite.databaseName = newName;
		}
		public static String getDatabaseName() {
			return databaseName;
		}
		
		public static void initializeDatabase() {
			try {
				Class.forName(JDBC_DRIVER);
			} catch(ClassNotFoundException e) {
				System.out.println("Class not found...");
			}		
		}
		
		public static Connection getConnection() {
			return connection;
		}	
		
		public static boolean startTransaction() {
			try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
				}
				
				connection = DriverManager.getConnection(DATABASE_PATH + getDatabaseName());
				connection.setAutoCommit(false);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		public static void endTransaction() {
			try {		
				if (connection == null || connection.isClosed()) {
					System.out.println("endTransaction null / is closed");
					return;
				}
				connection.close();
			} catch (SQLException e) {
				System.out.println("SQLException in endTransation: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void init(String databaseName) {
		SQLite.setDatabaseName(databaseName);
		SQLite.initializeDatabase();
		SQLite.startTransaction();
		
	}
	
	public void close() {
		SQLite.endTransaction();
	}
	
	public HashMap<Integer, CarData> getCarData(int iteration) {
		HashMap<Integer, CarData> data = new HashMap<Integer, CarData>();
		
		Connection con = SQLite.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM car_output WHERE iterationCount = ?;");
			
			ps.setInt(1, iteration);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				CarData car = new CarData();
				car.setId(rs.getInt(1));
				car.setIterationCount(rs.getInt(2));
				car.setPosition(rs.getDouble(3));
				car.setLane(rs.getInt(4));
				car.setVelocity(rs.getDouble(5));
				car.setAcceleration(rs.getDouble(6));
				//simulationId
				car.setLightId(rs.getInt(8));
			
				data.put(car.getId(), car);
			}
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public int getTotalIteration() {
		int result = -1;
		Connection con = SQLite.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT MAX(iterationCount) FROM car_output;");
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
//				System.out.println(rs.getInt(1));
				result = rs.getInt(1);
			}
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public ArrayList<LightData> getLightData(int iteration) {
		ArrayList<LightData> data = new ArrayList<LightData>();
		
		Connection con = SQLite.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("SELECT * FROM light_output WHERE iterationCount = ?;");
			
			ps.setInt(1, iteration);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				LightData light = new LightData();
				light.setId(rs.getInt(1));
				light.setIterationCount(rs.getInt(2));
				light.setPosition(rs.getDouble(3));
				light.setColor(LightData.Color.valueOf(rs.getString(4)));
				//position 5 is simulation id
				light.setTimeUntilChange(rs.getDouble(6));
				
				data.add(light);
			}
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
}
