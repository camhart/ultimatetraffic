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
	
	private SQLiteAccessor SQLITE;
	private String DATABASE_NAME;
	private SQLite sqlite;
	
	public SQLiteAccessor() {
		sqlite = new SQLite();
	}
	
	public void setDataBaseName(String databaseName) {
		
		if(DATABASE_NAME != null && !DATABASE_NAME.equals(databaseName) && SQLITE != null) {
			SQLITE.close();
		}
		DATABASE_NAME = databaseName;
	}
	
	private static class Holder {
		private static final SQLiteAccessor INSTANCE = new SQLiteAccessor();
		private static final SQLiteAccessor INSTANCE2 = new SQLiteAccessor();
	}
	
	public static SQLiteAccessor getSQLite() {
		//thread safe headache...
//		if(SQLITE.DATABASE_NAME == null) {
//			SQLITE.init(DATABASE_NAME);
//		}
		return Holder.INSTANCE;
	}
	
	public static SQLiteAccessor getSQLite2() {
		//thread safe headache...
//		if(SQLITE.DATABASE_NAME == null) {
//			SQLITE.init(DATABASE_NAME);
//		}
		return Holder.INSTANCE2;
	}
	
	static class SQLite {
		private static final String JDBC_DRIVER = "org.sqlite.JDBC";;
		private static final String DATABASE_PATH = "jdbc:sqlite:";
		
		private Connection connection;
		
		private String databaseName = "db.sqlite";
		
		public void setDatabaseName(String newName) {
			databaseName = newName;
		}
		public String getDatabaseName() {
			return databaseName;
		}
		
		public void initializeDatabase() {
			try {
				Class.forName(JDBC_DRIVER);
			} catch(ClassNotFoundException e) {
				System.out.println("Class not found...");
			}		
		}
		
		public Connection getConnection() {
			return connection;
		}	
		
		public boolean startTransaction() {
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
		
		public void endTransaction() {
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
		sqlite.setDatabaseName(databaseName);
		sqlite.initializeDatabase();
		sqlite.startTransaction();
	}
	
	public void close() {
		sqlite.endTransaction();
	}
	
	public HashMap<Integer, CarData> getCarData(int iteration) {
		HashMap<Integer, CarData> data = new HashMap<Integer, CarData>();
		
		Connection con = sqlite.getConnection();
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
		Connection con = sqlite.getConnection();
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
		
		Connection con = sqlite.getConnection();
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
