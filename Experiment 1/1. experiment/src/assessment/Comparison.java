// Comparison.java by David W. Braithwaite
// Module for fraction magnitude comparison task

package assessment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import utils.*;

public class Comparison extends Module {
	
	protected static final long serialVersionUID = 1L;
	
	// module control
	protected int moduleState;
	protected int STATE_INTRODUCTION		= 1; // show introductory message
	protected int STATE_TRIALS				= 2; // run trials
	protected int STATE_CONCLUSION			= 3; // show concluding message
	protected int STATE_END					= 4; // end the module
	public int trialIdx 					= 0;
	protected Timer moduleTimer				= new Timer();
	
	// trial control
	protected int trialState;
	protected int STATE_INITIAL_DELAY		= 1; // blank screen shown before stimulus
	protected int STATE_AWAITING_RESPONSE	= 2; // stimulus shown, awaiting response
	protected int STATE_AWAITING_RESP_MASK	= 3; // stimulus masked, awaiting response
	protected int STATE_RESPONSE_RECEIVED	= 4; // response received
	protected Timer trialTimer				= new Timer();
	String version;
	String mode;
	
	// stimuli
	protected List<Fraction> stimuli;
	protected Fraction standardFraction		= new Fraction( 3,5 );
	protected double standardValue			= (double) standardFraction.getFloat();
	
	// input & data
	SimpleDateFormat format 				= new SimpleDateFormat("MM/dd/yyyy HH:mm");
	protected class TrialData {
		public int trialIdx;
		public String stimTxt;
		public double stimVal;
		public int key;
		public int response;
		public int accuracy;
		public long rt;
		public String toString() {
			return( trialIdx + "," + stimTxt + "," + stimVal + "," + key + "," + response + "," + accuracy + "," + rt );
		}
	}
	protected TrialData data;

	// display
	Graphics2D g2;
	int lEnd = 0;
	int rEnd = 1;

	public Comparison(String v, int w, int h, String fN, String mN, String m) {
		super(w,h,fN,mN);
		version = v;
		mode = m;
		Fraction[] stimarray = new Fraction[]{};
		if ( version.equals("A") ) {
			stimarray = new Fraction[] { new Fraction(3,8), new Fraction(5,8), new Fraction(2,9), new Fraction(4,5), 
					new Fraction(4,7), new Fraction(5,9), new Fraction(8,9), new Fraction(2,3),
					new Fraction(5,5), new Fraction(5,6), new Fraction(4,8), new Fraction(1,7),
					new Fraction(9,10), new Fraction(3,7), new Fraction(4,10) };
		} else if ( version.equals("B") ) {
			stimarray = new Fraction[] { new Fraction(4,8), new Fraction(7,8), new Fraction(6,9), new Fraction(2,5),
					new Fraction(5,7), new Fraction(4,9), new Fraction(7,9), new Fraction(3,4),
					new Fraction(6,6), new Fraction(1,6), new Fraction(3,6), new Fraction(2,7),
					new Fraction(3,10), new Fraction(6,7), new Fraction(2,10) };
		} else {
			System.out.println("Unrecognized version in Comparison constructor: "+version);
		}
		stimuli = new ArrayList<Fraction>(Arrays.asList(stimarray));
		Collections.shuffle(stimuli);
		if ( mode=="testing" ) {
			stimuli = stimuli.subList(1,4);
		}
	}
	
	public static void main(String[] args) {
		SimpleDateFormat format	= new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		Date now 				= new Date();
		String fName 			= "comparison " + format.format(now) + ".txt";
		Comparison c 			= new Comparison( "A", 1300, 700, fName, "comparison", "normal" );
		JFrame f 				= new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.setVisible(true);
		f.add(c, BorderLayout.CENTER);
		f.pack();
		f.setLocationRelativeTo(null);
//		f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		c.start();
	}
	
	public void start() {
		logLineToFile(mName);
		logLineToFile("Version " + version);
		logLineToFile( "Start time: " + format.format(new Date()) );
		moduleTimer.start();
        addKeyListener(this);
		requestFocusInWindow();
		moduleState = STATE_INTRODUCTION;
		if ( mode.equals("auto") ) {
			advance();
		} else {
			repaint();
		}
	}
	
	protected void advance() {
		// set module and trial state variables
		if ( moduleState==STATE_INTRODUCTION ) {
			moduleState		= STATE_TRIALS;
			trialIdx		= 0;
		} else if ( moduleState==STATE_TRIALS && (trialIdx+1)<stimuli.size() ) {
			trialIdx++;
		} else if ( moduleState==STATE_TRIALS ) {
			moduleState		= STATE_CONCLUSION;
		} else if ( moduleState==STATE_CONCLUSION ) {
			moduleState		= STATE_END;
		}
		// display whatever is appropriate for current state
		if ( moduleState==STATE_INTRODUCTION || moduleState==STATE_CONCLUSION ) {
			if ( mode.equals("auto") ) {
				advance();
			} else {
				repaint();
			}
		} else if ( moduleState==STATE_TRIALS ) {
			// initialize trial data
			data 			= new TrialData();
			data.trialIdx 	= trialIdx;
			data.stimTxt	= stimuli.get(trialIdx).toString();
			data.stimVal	= stimuli.get(trialIdx).getFloat();
			data.key		= (data.stimVal<standardValue) ? 0 : 1;
			if ( mode=="auto" ) {
				// show blank screen
				trialState	= STATE_INITIAL_DELAY;
				// start trial
				trialState 	= STATE_AWAITING_RESPONSE;
				trialTimer.start();
				// simulate response
				recordResponse( utils.Utils.getRand(0, 1), trialTimer.get() );
				trialState = STATE_RESPONSE_RECEIVED;
				// advance to next trial after short delay
				new java.util.Timer().schedule( new CallbackTimerTask( new Callback() {
					public void call() {
						advance();
					} } ), 100 );
			} else {
				// show blank screen
				trialState		= STATE_INITIAL_DELAY;
				repaint();
				// start trial after variable delay
				new java.util.Timer().schedule( new CallbackTimerTask( new Callback() {
					public void call() {
						trialState = STATE_AWAITING_RESPONSE;
						trialTimer.start();
						repaint();
					} } ), utils.Utils.getRand(500,1500) );
			}
		} else {
			end();
		}
	}
	
	public void end() {
		logLineToFile("End time: " + format.format(new Date()) );
		logLineToFile("Total time: " + moduleTimer.get());
		if ( callback==null ) {
			System.out.println("Module callback not set in module "+mName+", closing.");
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
			topFrame.dispose();
		} else {
			callback.call();
		}		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2	= (Graphics2D)g;
		if (moduleState==STATE_INTRODUCTION) {
			utils.NumCog.displayNumberLine(g2, width, height/5, 0, 1);
			utils.NumCog.markFractionPos(standardFraction, g2, "normal", width, height/5, 0, 1, Color.black);
			utils.NumCog.displayFraction(standardFraction, g2, width/2, 2*height/5, "top", "large", Color.black);
			utils.NumCog.displayText("Press the spacebar to begin.", g2, width/2, 4*height/5, "normal", Color.green);		
		} else if (moduleState==STATE_TRIALS) {
			if ( trialState>STATE_INITIAL_DELAY ) {
				// if trial has started, show standard for comparison
				utils.NumCog.displayNumberLine(g2, width, height/5, 0, 1);
				utils.NumCog.markFractionPos(standardFraction, g2, "normal", width, height/5, 0, 1, Color.black);
			}
			if ( trialState>STATE_INITIAL_DELAY && trialState!=STATE_AWAITING_RESP_MASK ) {
				// if trial has started and mask isn't shown, show stimulus
				utils.NumCog.displayText("Is the following fraction less than or greater than " + standardFraction.toString() + "?", g2, width/2, 2*height/5, "normal", Color.black);
				utils.NumCog.displayFraction(stimuli.get(trialIdx), g2, width/2, 3*height/5, "top", "large", Color.blue);
			} else if ( trialState==STATE_AWAITING_RESP_MASK ) {
				// if trial has started and mask is to be shown, show mask
				utils.NumCog.displayText("show mask instead of stimulus", g2, width/2, 3*height/5, "normal", Color.black );
			}
			if ( trialState>STATE_INITIAL_DELAY && trialState<STATE_RESPONSE_RECEIVED ) {
				utils.NumCog.displayText("Press \'left\' for \'less\' and \'right\' for \'greater\'.", g2, width/2, 4*height/5, "normal", Color.black);
			}
			if ( trialState==STATE_RESPONSE_RECEIVED ) {
				// if participant has responded, acknowledge response and continue after delay
				utils.NumCog.displayText("Thank you! The next fraction will appear in a moment.", g2, width/2, 4*height/5, "normal", Color.green );
			}
		} else if (moduleState==STATE_CONCLUSION) {
			utils.NumCog.displayText("Please ask the experimenter to start the next phase of the experiment.", g2, width/2, 2*height/5, "normal", Color.red);
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if ( ( moduleState==STATE_INTRODUCTION || moduleState==STATE_CONCLUSION ) && key==KeyEvent.VK_SPACE ) {
			System.out.println("keyReleased trialState " + trialState + " moduleState "+moduleState);
			// if in introduction or conclusion mode and spacebar was pressed, advance
			advance();
		} else if ( moduleState==STATE_TRIALS && trialState==STATE_AWAITING_RESPONSE && ( key==KeyEvent.VK_Z || key==KeyEvent.VK_SLASH ) ) {
			// if running trial and awaiting response and a valid response was pressed, record the response
			long rt = trialTimer.get();
			int response = (key==KeyEvent.VK_Z) ? 0 : 1;
			recordResponse(response,rt);
			// acknowledge it on screen
			trialState = STATE_RESPONSE_RECEIVED;
			repaint();
			// advance to the next trial after a delay
			new java.util.Timer().schedule( new CallbackTimerTask( new Callback() {
				public void call() {
					advance();
				} } ), 750 );
		} else {
			return;
		}
	}
	
	protected void recordResponse(int response, long rt) {
		data.response	= response;
		data.rt			= rt;
		data.accuracy	= (data.key==data.response) ? 1 : 0;
		logLineToFile( data.toString() );
	}
}
