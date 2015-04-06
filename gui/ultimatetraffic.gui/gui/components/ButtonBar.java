package gui.components;

import gui.SimulatorGui;
import gui.data.StateData;
import gui.sqlite.SQLiteAccessor;
import gui.worker.DataWorker;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

//import org.jdesktop.xswingx.PromptSupport;

public class ButtonBar extends JToolBar implements ActionListener {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private static JChannel channel;
	
	private static boolean useChannel = false;
	private int iterationCountValue = 0;
	private int startIterationCountValue = 0;
	private DataWorker dataWorker;
	private boolean justSent;
	
	JFileChooser fileChooser;
	JButton setDatabaseButton;
	JButton runButton;
	JCheckBox controlAllGuis;
	JTextField iterationTextField;
	JTextField startIterationTextField;

	private JButton resetDatabaseButton;
	private JButton cancelButton;
	private JButton pauseButton;

	private boolean workerDone;
	private boolean databaseSet;

	private boolean paused;

	public ButtonBar() {
		super();

		
		this.paused = false;
		this.workerDone = true;
		this.databaseSet = false;
		
		fileChooser = new JFileChooser(new File(new File("").getAbsolutePath()));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith(".sqlite") || f.isDirectory();
			}

			@Override
			public String getDescription() {
				return "SQLite";
			}
			
		});
		
		this.setFloatable(false);
		this.setRollover(true);
		
		setDatabaseButton = new JButton("Set Database");
		setDatabaseButton.addActionListener(this);

		
		resetDatabaseButton = new JButton("Reset Database");
		resetDatabaseButton.setEnabled(false);
		resetDatabaseButton.addActionListener(this);
		
		runButton = new JButton("Run");
		runButton.addActionListener(this);
		
		startIterationTextField = new JTextField();
		PlainDocument doc1 = new PlainDocument();
		doc1.setDocumentFilter(new DocumentFilter() {
		    @Override
		    public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
		    		throws BadLocationException {
		        fb.insertString(off, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    } 
		    
		    @Override
		    public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
		    		throws BadLocationException {
		        fb.replace(off, len, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    }
		});

		doc1.addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				String text = startIterationTextField.getText();
				startIterationCountValue = Integer.parseInt(text.length() > 0 ? text : "0");
				SimulatorGui.getInstance().setCurrentIteration(startIterationCountValue);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				String text = startIterationTextField.getText();
				startIterationCountValue = Integer.parseInt(text.length() > 0 ? text : "0");
				SimulatorGui.getInstance().setCurrentIteration(startIterationCountValue);
			}			
		});
		startIterationTextField.setDocument(doc1);
		startIterationTextField.setText("0");
//		PromptSupport.setPrompt("Start iteration", startIterationTextField);
		
		iterationTextField = new JTextField();

		PlainDocument doc = new PlainDocument();
		doc.setDocumentFilter(new DocumentFilter() {
		    @Override
		    public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
		    		throws BadLocationException {
		        fb.insertString(off, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    } 
		    
		    @Override
		    public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
		    		throws BadLocationException {
		        fb.replace(off, len, str.replaceAll("\\D++", ""), attr);  // remove non-digits
		    }
		});

		doc.addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				String text = iterationTextField.getText();
				iterationCountValue = Integer.parseInt(text.length() > 0 ? text : "0");
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				String text = iterationTextField.getText();
				iterationCountValue = Integer.parseInt(text.length() > 0 ? text : "0");
			}			
		});
		iterationTextField.setDocument(doc);
		iterationTextField.setText("10000");
//		PromptSupport.setPrompt("Number of Iterations", iterationTextField);
		
		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(this);
		

		
		cancelButton = new JButton("Cancel run");
		cancelButton.addActionListener(this);
		
		controlAllGuis = new JCheckBox("Control all GUIs");
		
		controlAllGuis.addActionListener(this);
		
		
		this.add(setDatabaseButton);
		this.add(resetDatabaseButton);	
		this.add(Box.createRigidArea(new Dimension(15,0)));
		
		this.add(runButton);
		this.add(pauseButton);
		this.add(cancelButton);
		this.add(controlAllGuis);
		
		this.add(Box.createRigidArea(new Dimension(400,0)));
		
		this.add(new JLabel("Start at iteration: "));
		this.add(startIterationTextField);
		this.add(Box.createRigidArea(new Dimension(5,0)));
		this.add(new JLabel("Iterations to run: "));
		this.add(iterationTextField);
			
		updateButtons();
	}
	
	public void chooseDb() {
		int choice = fileChooser.showOpenDialog(this);
		if(choice == JFileChooser.APPROVE_OPTION) {
			SQLiteAccessor.getSQLite().init(fileChooser.getSelectedFile().getAbsolutePath());
			this.databaseSet = true;
		}
		updateButtons();
	}
	
	public void runSimulator() {
		dataWorker = new DataWorker(this.iterationCountValue);
		dataWorker.execute();
		updateButtons();
	}
	
	public void togglePause() {
		this.paused = !this.paused;
		dataWorker.setPause(this.paused);
		pauseButton.setText((this.paused == true) ? "Unpause" : " Pause ");
		updateButtons();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		switch(event.getActionCommand()) {
		case "Set Database":
			chooseDb();
			break;
		case "Reset Database":
			if(this.dataWorker != null && !this.dataWorker.isCancelled())
				this.dataWorker.cancel(true);
			SQLiteAccessor.getSQLite().close();
			this.databaseSet = false;
			SimulatorGui.getInstance().getCanvas().clearData();
			SimulatorGui.getInstance().setCurrentIteration(this.startIterationCountValue);
			updateButtons();
			break;
		case "Run":
			if(this.useChannel) {
				try {
					this.justSent = true;
					channel.send(new Message(null, "run"));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			this.runSimulator();
			break;
		case "Unpause":
		case " Pause ":
			if(this.useChannel) {
				try {
					this.justSent = true;
					channel.send(new Message(null, "pause"));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			this.togglePause();
			break;
		case "Cancel run":
			this.paused = false;
			pauseButton.setText((this.paused == true) ? "Unpause" : " Pause ");
			dataWorker.cancel(false);
			SimulatorGui.setState(new StateData());
			SimulatorGui.getInstance().setCurrentIteration(this.startIterationCountValue);
			SimulatorGui.getInstance().getCanvas().clearData();
			updateButtons();
			break;
		case "Control all GUIs":
			if(channel != null && channel.isConnected() || this.useChannel) {
				channel.disconnect();
			} else {
				try {
					this.useChannel = !this.useChannel;
					if(channel == null)
						channel = new JChannel();
					ButtonBar.channel.connect("simulator_gui");
					ButtonBar.channel.setReceiver(new ReceiverAdapter() {
						@Override
						public void receive(Message msg) {
							if(justSent) {
								justSent = false;
								return;
							}
							System.out.println("recieved: " + msg.getObject());
							if(((String)msg.getObject()).equals("run")) {
								runSimulator();
							} else if(((String)msg.getObject()).equals("pause")) {
								togglePause();
							}
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
			break;
		case "":
			break;
		default:
			System.out.println(event.getActionCommand());
			break;
		}
	}
	
	

	public void setWorkerDone(boolean workerDone) {
		this.workerDone = workerDone;
		updateButtons();
	}

	private void updateButtons() {
		iterationTextField.setEnabled(this.workerDone);
		startIterationTextField.setEnabled(this.workerDone);
		runButton.setEnabled(this.workerDone && this.databaseSet);
		
		this.setDatabaseButton.setEnabled(!this.databaseSet);
		this.resetDatabaseButton.setEnabled(this.databaseSet && this.workerDone);
		
		setDatabaseButton.setEnabled(!this.databaseSet);
		resetDatabaseButton.setEnabled(this.databaseSet);
		
		cancelButton.setEnabled(!this.workerDone);
//		cancelButton.setEnabled(false);
		
		pauseButton.setEnabled(!this.workerDone && dataWorker != null && !dataWorker.isDone() && !dataWorker.isCancelled());
		pauseButton.setText((paused == true) ? "Unpause" : " Pause ");
	}

	public static boolean messageServerRunning() {
		return useChannel;
	}
}
