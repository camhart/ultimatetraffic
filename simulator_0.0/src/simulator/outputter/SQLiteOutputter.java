package simulator.outputter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	private static int simulationId = -1;
	
	//singleton
	public static SQLiteOutputter getOutputter() {
		if(sqlOut == null) {
			sqlOut = new SQLiteOutputter();
		}
		return sqlOut;
	}
	
	private SQLiteOutputter() {
		outputCount = OUTPUTS_UNTIL_COMMIT;
		SQLite.initializeDatabaseDriver();
	}
	
	public void startTransaction() {
		SQLite.startTransaction();
	}
	
	public void startTransaction(String databaseName) {
		SQLite.startTransaction(databaseName);
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
			PreparedStatement ps = con.prepareStatement("INSERT INTO light_output (id, iterationCount, position, color, simulationId, timeUntilChange) VALUES (?, ?, ?, ?, ?, ?);");
			
			ps.setInt(1, light.getId());
			ps.setInt(2,  Simulator.getSimulator().getCurrentIteration());
			ps.setDouble(3, light.getPosition());
			ps.setString(4, light.getCurrentColor().toString());
			ps.setInt(5, simulationId);
			ps.setDouble(6, light.getTimeUntilChange());
			
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
			PreparedStatement ps = con.prepareStatement("INSERT INTO car_output (id, iterationCount, position, lane, velocity, acceleration, simulationId, lightId) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
			
			ps.setInt(1, car.getId());
			ps.setInt(2,  Simulator.getSimulator().getCurrentIteration());
			ps.setDouble(3, car.getPosition());
			ps.setInt(4,  car.getLane());
			ps.setDouble(5,  car.getVelocity());
			ps.setDouble(6,  car.getAcceleration());
			ps.setInt(7, simulationId);
			ps.setInt(8,  car.getLaneObject().getParentLight().getId());
			
			ps.execute();
			
			ps.close();
			
			handleCommit();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addConfigOutput(String databasePath, int roadLength, double iterationTime, String description) {
		
		if(databasePath.length() > 512) {
			throw new Error("Database path '" + databasePath +"' is too long (" + databasePath.length() +
					")  Max allowed (" + 512 + ")");
		}
		
		Connection con = SQLite.getConnection();
		try {
			PreparedStatement ps = con.prepareStatement("INSERT INTO simulation (databasePath, roadLength, iterationTime, description) VALUES (?, ?, ?, ?);");
			
			ps.setString(1, databasePath);
			ps.setInt(2,  roadLength);
			ps.setDouble(3,  iterationTime);
			ps.setString(4, description);
			
			ps.execute();
			
			ResultSet key = ps.getGeneratedKeys();
			
			if(key.next()) {
				SQLiteOutputter.simulationId = (int)key.getLong(1);
			} else {
				ps.close();
				throw new Error("something wrong with simulation id");
			}
			
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
				"acceleration REAL not NULL," +
				"simulationId INTEGER not NULL," +
				"lightId INTEGER not NULL," +
				"FOREIGN KEY(simulationId) REFERENCES simulation(id)" + 
			");";
		
		String lightTable = "CREATE TABLE light_output (" +
				"id INTEGER not NULL," + 
				"iterationCount INTEGER not NULL," +
				"position REAL not NULL," +
				"color VARCHAR(6)," +
				"simulationId INTEGER not NULL," +
				"timeUntilChange REAL not NULL," +
				"FOREIGN KEY(simulationId) REFERENCES simulation(id)" + 
			");";
		
		String simTable = "CREATE TABLE simulation (" +
				"id INTEGER PRIMARY KEY not NULL," +
				"databasePath VARCHAR(512) not NULL," +
				"roadLength INTEGER not NULL," +
				"iterationTime REAL not NULL," +
				"description VARCHAR(512) not NULL" +
			");";
		
		ResultSet carTableExists, lightTableExists, simulationTableExists;
		try {
			carTableExists = SQLite.getConnection().getMetaData().getTables(null, null, "car_output", null);
		
			if(!carTableExists.next()) {		
				try {
					Statement statement = SQLite.getConnection().createStatement();
					statement.executeUpdate(carTable);
					
					statement = SQLite.getConnection().createStatement();
					statement.executeUpdate(lightTable);
				} catch (SQLException e) {
					if(!e.getMessage().equals("table car_output already exists"))
						e.printStackTrace();
				}
			}
				
			lightTableExists = SQLite.getConnection().getMetaData().getTables(null, null, "light_output", null);
			
			if(!lightTableExists.next()) {
				try {
					Statement statement = SQLite.getConnection().createStatement();
					statement.executeUpdate(lightTable);
				} catch (SQLException e) {
					if(!e.getMessage().equals("table light_output already exists"))
						e.printStackTrace();
				}
			}
			
			simulationTableExists = SQLite.getConnection().getMetaData().getTables(null, null, "simulation", null);
			
			if(!simulationTableExists.next()) {
				try {
					Statement statement = SQLite.getConnection().createStatement();
					statement.executeUpdate(simTable);
				} catch (SQLException e) {
					if(!e.getMessage().equals("table simulation already exists"))
						e.printStackTrace();
				}
			}
		
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	static class SQLite {
		private static final String JDBC_DRIVER = "org.sqlite.JDBC";;
		private static final String DATABASE_PATH = "jdbc:sqlite:";
		
		private static Connection connection;
		
		private static String databaseName = "db.sqlite";
		private static int roadLength;
		private static double iterationTime;
		private static String description;
		
		public static void addConfigOutput(String newName, int roadLength, double iterationTime, String description) {
			SQLite.databaseName = newName;
			SQLite.roadLength = roadLength;
			SQLite.iterationTime = iterationTime;
			SQLite.description = description;
		}
		public static String getDatabaseName() {
			return databaseName;
		}
		
		public static void initializeDatabaseDriver() {
			try {
				Class.forName(JDBC_DRIVER);
			} catch(ClassNotFoundException e) {
				System.out.println("Class not found...");
			}		
		}
		
		public static Connection getConnection() {
			return connection;
		}	
		
		public static boolean startTransaction(String dbName) {
			databaseName = dbName;
			return startTransaction();
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
		int roadLength = (int)params[1];
		double iterationTime = (double)params[2];
		String description = (String)params[3];
		
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
		
		SQLite.initializeDatabaseDriver();
		SQLiteOutputter.getOutputter().startTransaction(databaseName);
		SQLiteOutputter.getOutputter().createTables();
		SQLite.addConfigOutput(databaseName, roadLength, iterationTime, description);
	}
}
