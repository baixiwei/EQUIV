// Comparison.java by David W. Braithwaite
// Module for fraction magnitude comparison task in equivalent fractions project experiment 1

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
	
	// stimuli, input, & data
	SimpleDateFormat format 				= new SimpleDateFormat("MM/dd/yyyy HH:mm");
	protected class TrialData {
		public int trialIdx = -1;
		public Fraction stim1;
		public String stim1Txt;
		public double stim1Val;
		public Fraction stim2;
		public String stim2Txt;
		public double stim2Val;
		public int key;
		public int response = -1;
		public int accuracy = -1;
		public long rt = 0;
		public TrialData( Fraction a, Fraction b ) {
			if ( utils.Utils.getRand(1,2)==1 ) {
				stim1 = a;
				stim2 = b;
			} else {
				stim1 = b;
				stim2 = a;
			}
			stim1Txt = stim1.toString();
			stim1Val = stim1.getFloat();
			stim2Txt = stim2.toString();
			stim2Val = stim2.getFloat();
			key		 = (stim1Val>stim2Val) ? 0 : 1 ;
		}
		public String toString() {
			return( trialIdx + "," + stim1Txt + "," + stim1Val + "," + stim2Txt + "," + stim2Val + "," + key + "," + response + "," + accuracy + "," + rt );
		}
	}
	protected List<TrialData> trials;
	protected TrialData trial;

	// display
	Graphics2D g2;
	int lEnd = 0;
	int rEnd = 1;

	public Comparison(String v, int w, int h, String fN, String mN, String m) {
		super(w,h,fN,mN);
		version = v;
		mode = m;
		TrialData[] stimarray = new TrialData[]{};
		// create stimulus set according to version 
		if ( version.equals("A") ) {
			stimarray = new TrialData[] {
				new TrialData( new Fraction(1,6), new Fraction(2,7) ),
				new TrialData( new Fraction(3,18), new Fraction(2,7) ),
				new TrialData( new Fraction(4,24), new Fraction(2,7) ),
				new TrialData( new Fraction(1,5), new Fraction(2,6) ),
				new TrialData( new Fraction(3,15), new Fraction(2,6) ),
				new TrialData( new Fraction(5,25), new Fraction(2,6) ),
				new TrialData( new Fraction(3,8), new Fraction(5,10) ),
				new TrialData( new Fraction(6,16), new Fraction(5,10) ),
				new TrialData( new Fraction(9,24), new Fraction(5,10) ),
				new TrialData( new Fraction(3,7), new Fraction(5,9) ),
				new TrialData( new Fraction(6,14), new Fraction(5,9) ),
				new TrialData( new Fraction(9,21), new Fraction(5,9) ),
				new TrialData( new Fraction(5,10), new Fraction(6,9) ),
				new TrialData( new Fraction(11,22), new Fraction(6,9) ),
				new TrialData( new Fraction(12,24), new Fraction(6,9) ),
				new TrialData( new Fraction(3,5), new Fraction(5,7) ),
				new TrialData( new Fraction(6,10), new Fraction(5,7) ),
				new TrialData( new Fraction(15,25), new Fraction(5,7) ),
				new TrialData( new Fraction(2,3), new Fraction(6,8) ),
				new TrialData( new Fraction(4,6), new Fraction(6,8) ),
				new TrialData( new Fraction(8,12), new Fraction(6,8) ),
				new TrialData( new Fraction(4,5), new Fraction(10,11) ),
				new TrialData( new Fraction(8,10), new Fraction(10,11) ),
				new TrialData( new Fraction(12,15), new Fraction(10,11) ),
				new TrialData( new Fraction(5,6), new Fraction(12,13) ),
				new TrialData( new Fraction(10,12), new Fraction(12,13) ),
				new TrialData( new Fraction(15,18), new Fraction(12,13) ) };
		} else if ( version.equals("B") ) {
			stimarray = new TrialData[] {
				new TrialData( new Fraction(1,8), new Fraction(1,5) ),
				new TrialData( new Fraction(2,16), new Fraction(1,5) ),
				new TrialData( new Fraction(3,24), new Fraction(1,5) ),
				new TrialData( new Fraction(1,7), new Fraction(1,4) ),
				new TrialData( new Fraction(2,14), new Fraction(1,4) ),
				new TrialData( new Fraction(3,21), new Fraction(1,4) ),
				new TrialData( new Fraction(1,3), new Fraction(3,7) ),
				new TrialData( new Fraction(6,18), new Fraction(3,7) ),
				new TrialData( new Fraction(8,24), new Fraction(3,7) ),
				new TrialData( new Fraction(2,5), new Fraction(1,2) ),
				new TrialData( new Fraction(6,15), new Fraction(1,2) ),
				new TrialData( new Fraction(10,25), new Fraction(1,2) ),
				new TrialData( new Fraction(1,2), new Fraction(3,5) ),
				new TrialData( new Fraction(2,4), new Fraction(3,5) ),
				new TrialData( new Fraction(9,18), new Fraction(3,5) ),
				new TrialData( new Fraction(5,8), new Fraction(7,9) ),
				new TrialData( new Fraction(10,16), new Fraction(7,9) ),
				new TrialData( new Fraction(15,24), new Fraction(7,9) ),
				new TrialData( new Fraction(3,4), new Fraction(8,9) ),
				new TrialData( new Fraction(6,8), new Fraction(8,9) ),
				new TrialData( new Fraction(9,12), new Fraction(8,9) ),
				new TrialData( new Fraction(6,7), new Fraction(14,15) ),
				new TrialData( new Fraction(12,14), new Fraction(14,15) ),
				new TrialData( new Fraction(18,21), new Fraction(14,15) ),
				new TrialData( new Fraction(7,8), new Fraction(20,21) ),
				new TrialData( new Fraction(14,16), new Fraction(20,21) ),
				new TrialData( new Fraction(21,24), new Fraction(20,21) ) };
		} else {
			System.out.println("Unrecognized version in Comparison constructor: "+version);
		}
		trials = new ArrayList<TrialData>(Arrays.asList(stimarray));
		// put the stimuli in an acceptable order
		do {
			Collections.shuffle(trials);
		} while ( !checkStimuliOrder() );
		// add id numbers to the trials
		for ( int i=0; i<trials.size(); i++ ) {
			trials.get(i).trialIdx = i;
		}
		// if in testing mode, only use the first few trials
		if ( mode.equals("testing") ) {
			trials= trials.subList(1,4);
		}
	}
	
	public static void main(String[] args) {
		SimpleDateFormat format	= new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		Date now 				= new Date();
		String fName 			= "comparison " + format.format(now) + ".txt";
		String version			= new String[]{ "A", "B" }[ utils.Utils.getRand(0, 1) ];
		Comparison c 			= new Comparison( version, 1300, 700, fName, "comparison", "normal" );
		JFrame f 				= new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.setVisible(true);
		f.add(c, BorderLayout.CENTER);
		f.pack();
		f.setLocationRelativeTo(null);
//		f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		c.start();
	}

	public boolean checkStimuliOrder() {
		// make sure that no trials contain any fractions equivalent to any fractions in immediately preceding trials
		boolean order_ok = true;
		for ( int i=0; i<trials.size()-1; i++ ) {
			order_ok = order_ok &&
				( trials.get(i).stim1.compareTo(trials.get(i+1).stim1)!=0 ) &&		// first fraction in i not equivalent to first fraction in i+1
				( trials.get(i).stim1.compareTo(trials.get(i+1).stim2)!=0 ) &&		// first fraction in i not equivalent to second fraction in i+1
				( trials.get(i).stim2.compareTo(trials.get(i+1).stim1)!=0 ) &&		// second fraction in i not equivalent to first fraction in i+1
				( trials.get(i).stim2.compareTo(trials.get(i+1).stim2)!=0 );		// second fraction in i not equivalent to second fraction in i+1
		}
		return order_ok;
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
		} else if ( moduleState==STATE_TRIALS && (trialIdx+1)<trials.size() ) {
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
			trial 			= trials.get(trialIdx);
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
				trialState	= STATE_INITIAL_DELAY;
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
			utils.NumCog.displayText("Press the spacebar to begin.", g2, width/2, 4*height/6, "normal", Color.green);		
		} else if (moduleState==STATE_TRIALS) {
			if ( trialState>STATE_INITIAL_DELAY && trialState!=STATE_AWAITING_RESP_MASK ) {
				// if trial has started and mask isn't shown, show stimulus
				utils.NumCog.displayFraction(trial.stim1, g2, 2*width/5, 2*height/6, "top", "large", Color.blue);
				utils.NumCog.displayFraction(trial.stim2, g2, 3*width/5, 2*height/6, "top", "large", Color.blue);
			} else if ( trialState==STATE_AWAITING_RESP_MASK ) {
				// if trial has started and mask is to be shown, show mask
				// not implemented
				utils.NumCog.displayText("show mask instead of stimulus", g2, width/2, 3*height/5, "normal", Color.black );
			}
			if ( trialState>STATE_INITIAL_DELAY && trialState<STATE_RESPONSE_RECEIVED ) {
				utils.NumCog.displayText("Press \'left\' if the fraction on the left is larger, and \'right\' if the fraction on the right is larger.", g2, width/2, 4*height/6, "normal", Color.black);
			}
			if ( trialState==STATE_RESPONSE_RECEIVED ) {
				// if participant has responded, acknowledge response and continue after delay
				utils.NumCog.displayText("Thank you! The next fraction will appear in a moment.", g2, width/2, 4*height/6, "normal", Color.green );
			}
		} else if (moduleState==STATE_CONCLUSION) {
			utils.NumCog.displayText("Please ask the experimenter to start the next phase of the experiment.", g2, width/2, 2*height/6, "normal", Color.red);
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
		trial.response	= response;
		trial.rt		= rt;
		trial.accuracy	= (trial.key==trial.response) ? 1 : 0;
		logLineToFile( trial.toString() );
	}
}
