package simulator.validator;

import gui.sqlite.SQLiteAccessor;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import simulator.Simulator;

public class Validator {
	
	public static final Logger LOG = Logger.getLogger(Simulator.class.getName());
	
	static {
		LOG.setLevel(Level.SEVERE);
		LOG.setUseParentHandlers(false);
	}
	
	protected ArrayList<Validator> validators;
	protected SQLiteAccessor sqlite;

	public Validator(String databasePath) {
		validators = new ArrayList<Validator>();
		
		sqlite = SQLiteAccessor.getSQLite();
		sqlite.init(databasePath);
	}
	
	public Validator(SQLiteAccessor sqlite) {
		validators = new ArrayList<Validator>();
		this.sqlite = sqlite;
	}
	
	public Validator() {
		validators = new ArrayList<Validator>();
	}
	
	public void validateData(int totalIterations) {
		for(Validator v : validators) {
			v.validateData(totalIterations);
		}
	}
	
	public void addValidator(Validator v) {
		validators.add(v);
	}
	
	public boolean removeValidator(Validator v) {
		return validators.remove(v);
	}

	public void validateData() {
		this.validateData(sqlite.getTotalIteration());
	}
	
	public static void main(String[] args) {
		String databasePath = "db_phase_" + args[0] + ".sqlite";
//		LOG.addHandler(new ConsoleHandler());
		LOG.setLevel(Level.INFO);
//		LOG.setUseParentHandlers(false);
		Validator validator = new Validator(databasePath);
		validator.addValidator(new StopLightValidator(validator.getSQLiteAccessor()));
		validator.addValidator(new CarValidator(validator.getSQLiteAccessor()));
		validator.validateData();
	}

	public SQLiteAccessor getSQLiteAccessor() {
		return sqlite;
	}
}
