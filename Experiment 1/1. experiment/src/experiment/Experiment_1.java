package experiment;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;

import assessment.Comparison;
import assessment.Fraction_NLE;
import utils.*;

public class Experiment_1 extends ModuleContainer {
	
	protected static final long serialVersionUID = 1L;
	
	// subject data (assigned at experiment start)
	protected int subjid;							// unique subject ID
	protected int grade;							// participant's grade level
	protected String version;						// version used for pretest assessment
	protected static String VERSION_A		= "A";
	protected static String VERSION_B		= "B"; 
	protected static String[] versions		= new String[] { VERSION_A, VERSION_B };
	
	// other stuff
	protected String mode;							// whether the training runs in normal or automatic mode
	protected Timer experimentTimer	= new Timer();	// timer for the whole experiment
	SimpleDateFormat format 		= new SimpleDateFormat("MM/dd/yyyy HH:mm"); // format for date/time info in data record
	
	// constructor, main, start, and end
	
	public Experiment_1(int subjid, int grade, int width, int height, String fName, String mName, String mode) {
		super( width, height, fName, mName );
		this.subjid				= subjid;
		this.grade				= grade;
		this.version			= versions[utils.Utils.getRand(0,versions.length-1)];
		this.mode				= mode;
		// number line task
		this.add( new Fraction_NLE( this.version, width, height, fName, "NLE", this.mode ) );
		// magnitude comparison task
		this.add( new Comparison( this.version, width, height, fName, "comparison", this.mode ) );
	}
	
	public static void main(String[] args) {
		// create unique subject id; set grade
		int subjid				= utils.Utils.getRand(1, 1000000000);
		int grade				= 4;
		// create new experiment instance
		int width				= 1300;
		int height				= 700;
		String fName 			= "data " + subjid + " " + (new SimpleDateFormat("yyyy-MM-dd HH.mm.ss")).format(new Date()) + ".txt";
		String mName			= "experiment";
		String mode			 	= "normal";
//		mode="auto";
		mode="testing";
		Experiment_1 exp		= new Experiment_1( subjid, grade, width, height, fName, mName, mode );
		// create containing frame
		JFrame f 				= new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.setVisible(true);
		f.add(exp, BorderLayout.CENTER);
		f.pack();
		f.setLocationRelativeTo(null);
		// start the experiment
		exp.start();
	}

	@Override
	public void start() {
		logLineToFile("subjid: " + subjid);
		logLineToFile("grade: " + grade);
		logLineToFile("version: " + version);
		logLineToFile("Start time: " + format.format(new Date()) );
		experimentTimer.start();
		super.start();
	}

	@Override
	public void end() {
		// record completion time
		logLineToFile("End time: " + format.format(new Date()) );
		logLineToFile("Total time: " + experimentTimer.get());
		
		// close current subject's datafile
		super.end();
	}
	
}
