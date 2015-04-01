package simulator.outputter;

import java.sql.SQLException;

import simulator.models.CarManager;
import simulator.models.stoplights.StopLight;
import simulator.outputter.SQLiteOutputter.SQLite;

public class Tester {

	public static void main(String[] args) {
		//database testing stuff
		SQLiteOutputter so = SQLiteOutputter.getOutputter();
		so.startTransaction();
		so.createTables();
		StopLight light = new StopLight("636.6,timed,40,30,0,green");
		light.setColor(StopLight.Color.GREEN);

		for(int c = 0; c < 100; c++) {
			so.addCarOutput(new CarManager("0,1,15,0,5956.8,0"));
			so.addLightOutput(light);
		}
		so.endTransaction();
		
		if(SQLite.getConnection() != null) {
			try {
				SQLite.getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Done!");
	}
}
