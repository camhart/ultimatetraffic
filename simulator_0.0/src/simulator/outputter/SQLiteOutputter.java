package simulator.outputter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import simulator.Simulator;
import simulator.models.CarManager;
import simulator.models.StopLight;

class SQLiteOutputter implements OutputterInterface{
	
	private static final int OUTPUTS_UNTIL_COMMIT = 1000;
	private int outputCount;
	
	private static SQLiteOutputter sqlOut; 
	
	//singleton
	public static SQLiteOutputter getOutputter() {
		if(sqlOut == null) {
			sqlOut = new SQLiteOutputter();
		}
		return sqlOut;
	}
	
	private SQLiteOutputter() {
		outputCount = OUTPUTS_UNTIL_COMMIT;
		SQLite.initializeDatabase();
	}
	
	public void startTransaction() {
		SQLite.startTransaction();
	}

	public void handleCommit() {
		if(outputCount == 0) {
			SQLite.endTransaction(true);
			SQLite.startTransaction();
			outputCount = OUTPUTS_UNTIL_COMMIT;
		} else {
			outputCount--;
		}
	}
	
	public void endTransaction() {
		SQLite.endTransaction(true);
	}

	@Override
	public void addLightOutput(StopLight light) {
		Connection con = SQLite.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("INSERT INTO light_output (id, iterationCount, position, color) VALUES (?, ?, ?, ?);");
			
			ps.setInt(1, light.getId());
			ps.setInt(2,  Simulator.getSimulator().getCurrentIteration());
			ps.setDouble(3, light.getPosition());
			ps.setString(4, light.getCurrentColor().toString());
			
			ps.execute();
			
			ps.close();
			
			handleCommit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addCarOutput(CarManager car) {
		Connection con = SQLite.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("INSERT INTO car_output (id, iterationCount, position, lane, velocity, acceleration) VALUES (?, ?, ?, ?, ?, ?);");
			
			ps.setInt(1, car.getId());
			ps.setInt(2,  Simulator.getSimulator().getCurrentIteration());
			ps.setDouble(3, car.getPosition());
			ps.setInt(4,  car.getLane());
			ps.setDouble(5,  car.getVelocity());
			ps.setDouble(6,  car.getAcceleration());
			
			ps.execute();
			
			ps.close();
			
			handleCommit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createTables() {
		String carTable = "CREATE TABLE car_output (" +
				"id INTEGER not NULL," + 
				"iterationCount INTEGER not NULL," +
				"position REAL not NULL," +
				"lane INTEGER not NULL," +
				"velocity REAL not NULL," +
				"acceleration REAL not NULL" +
				");";
		
		String lightTable = "CREATE TABLE light_output (" +
				"id INTEGER not NULL," + 
				"iterationCount INTEGER not NULL," +
				"position REAL not NULL," +
				"color VARCHAR(6)" +
				");";
		
		try {
			Statement statement = SQLite.getConnection().createStatement();
			statement.executeUpdate(carTable);
			
			statement = SQLite.getConnection().createStatement();
			statement.executeUpdate(lightTable);
		} catch (SQLException e) {
			if(!e.getMessage().equals("table car_output already exists"))
				e.printStackTrace();
		}
		
		try {
			Statement statement = SQLite.getConnection().createStatement();
			statement.executeUpdate(lightTable);
		} catch (SQLException e) {
			if(!e.getMessage().equals("table light_output already exists"))
				e.printStackTrace();
		}
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
		
		public static void endTransaction(boolean commit) {
			try {		
				if (connection == null || connection.isClosed()) {
					System.out.println("endTransaction null / is closed");
					return;
				}
				else if (commit) {
					connection.commit();
				}
				connection.close();
			} catch (SQLException e) {
				System.out.println("SQLException in endTransation: " + e.getMessage());
				e.printStackTrace();
			}
//			System.out.println("ending transaction");
		}
	}

	@Override
	public void close() {
		this.endTransaction();
		try {
			SQLite.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(Object... params) {
		String databaseName = (String)params[0];
		File dbFile = new File(new File("").getAbsolutePath() + "/" + databaseName);
		if(dbFile.exists()) {
			String[] options = {"Yes, continue and delete the database", "No, stop everything now."};
			int choice = JOptionPane.showOptionDialog(null,
				    "The database '" +
				    dbFile.getAbsolutePath() +
				    "' already exists,\n\tcontinuing will delete it and create a new one.  Do you want to continue?",
				    "Overwrite database file?",
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[1]
			);
			
			if(choice != 0) {
				System.out.println("Simulator stopped.");
				System.out.println("Database can be found at " + dbFile.getAbsolutePath());
				System.exit(0);
			}
			
			if(!dbFile.delete()) {
				System.out.println("Error deleting database file " + dbFile.getAbsolutePath());
				System.out.println("Please close any programs with it open and try again.");
				System.exit(0);
			}
		}
		
		SQLite.setDatabaseName(databaseName);
		SQLite.initializeDatabase();
		SQLiteOutputter.getOutputter().startTransaction();
		SQLiteOutputter.getOutputter().createTables();
	}
}
