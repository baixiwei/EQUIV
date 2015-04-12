package assessment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import utils.*;

public class Fraction_NLE extends Module {
	
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
	protected int STATE_RESPONSE_RECEIVED	= 3; // response received, waiting to go on to next trial
	protected Timer trialTimer				= new Timer();
	String version;
	String mode;

	// stimuli
	protected List<Fraction> stimuli;
	
	// input & data
	SimpleDateFormat format 				= new SimpleDateFormat("MM/dd/yyyy HH:mm");
	protected class TrialData {
		public int trialIdx;
		public String stimTxt;
		public double stimVal;
		public double respVal;
		public double PAE;
		public long rt;
		public String toString() {
			return( trialIdx + "," + stimTxt + "," + stimVal + "," + respVal + "," + PAE + "," + rt );
		}
	}
	protected TrialData data;
	protected Point lastClicked;
	
	// display
	Graphics2D g2;
	protected int stimH;							// y-axis positioning of stimuli
	protected int lineH; 							// y-axis positioning of number line
	protected int lEnd						= 0;	// left endpoint of number line
	protected int rEnd	 					= 1;	// right endpoint of number line
	protected int msgH;								// y-axis positioning of text messages following response on trials

	public Fraction_NLE(String v, int w, int h, String fN, String mN, String m) {
		super(w,h,fN,mN);
		mode 		= m;
		// initialize stimuli
		version 	= v;
		Fraction[] stimarray = new Fraction[]{};
		if ( version.equals("A") ) {
			stimarray = new Fraction[] {
					new Fraction(1,6), new Fraction(3,18), new Fraction(4,24), 
					new Fraction(1,5), new Fraction(3,15), new Fraction(5,25), 
					new Fraction(3,8), new Fraction(6,16), new Fraction(9,24), 
					new Fraction(3,7), new Fraction(6,14), new Fraction(9,21), 
					new Fraction(5,10), new Fraction(11,22), new Fraction(12,24), 
					new Fraction(3,5), new Fraction(6,10), new Fraction(15,25), 
					new Fraction(2,3), new Fraction(4,6), new Fraction(8,12), 
					new Fraction(4,5), new Fraction(8,10), new Fraction(12,25), 
					new Fraction(5,6), new Fraction(10,12), new Fraction(15,18) };
		} else if ( version.equals("B") ) {
			stimarray = new Fraction[] {
					new Fraction(1,8), new Fraction(2,16), new Fraction(3,24), 
					new Fraction(1,7), new Fraction(2,14), new Fraction(3,21), 
					new Fraction(1,3), new Fraction(6,18), new Fraction(8,24), 
					new Fraction(2,5), new Fraction(6,15), new Fraction(10,25), 
					new Fraction(1,2), new Fraction(2,4), new Fraction(9,18), 
					new Fraction(5,8), new Fraction(10,16), new Fraction(15,24), 
					new Fraction(3,4), new Fraction(6,8), new Fraction(9,12), 
					new Fraction(6,7), new Fraction(12,14), new Fraction(18,21), 
					new Fraction(7,8), new Fraction(14,16), new Fraction(21,24) };
		} else {
			System.out.println("Unrecognized version in Fraction_NLE constructor: "+version);
		}
		stimuli = new ArrayList<Fraction>(Arrays.asList(stimarray));
		// put the stimuli in an acceptable order
		do {
			Collections.shuffle(stimuli);
		} while ( !checkStimuliOrder() );
		// if in testing mode, select a subset of the stimuli
		if ( mode=="testing" ) {
			stimuli = stimuli.subList(1,4);
		}
		// set variables to control graphical display based on window size
		stimH		= 1*height/4;
		lineH 		= 2*height/4;
		msgH		= 3*height/4;
	}
	
	public static void main(String[] args) {
		SimpleDateFormat format	= new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		Date now 				= new Date();
		String fName 			= "NLE " + format.format(now) + ".txt";
		String version			= new String[]{ "A", "B" }[ utils.Utils.getRand(0, 1) ];
		Fraction_NLE c = new Fraction_NLE( version, 1300, 700, fName, "NLE", "normal" );
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
		boolean order_ok = true;
		for ( int i=0; i<stimuli.size()-1; i++ ) {
			order_ok = order_ok &&
				( stimuli.get(i).compareTo(stimuli.get(i+1))!=0 ) &&	// no successive equivalent fractions
				( stimuli.get(i).num!=stimuli.get(i+1).num ) &&			// no successive same numerator fractions
				( stimuli.get(i).den!=stimuli.get(i+1).den );			// no successive same denominator fractions
		}
		return order_ok;
	}
	
	@Override
	public void start() {
		logLineToFile( mName );
		logLineToFile( "Version " + version );
		logLineToFile( "Start time: " + format.format(new Date()) );
		moduleTimer.start();
        addKeyListener(this);
        addMouseListener(this);
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
			data.trialIdx	= trialIdx;
			data.stimTxt	= stimuli.get(trialIdx).toString();
			data.stimVal	= stimuli.get(trialIdx).getFloat();
			if ( mode.equals("auto") ) {
				// show blank screen
				trialState = STATE_INITIAL_DELAY;
				// start trial
				trialState = STATE_AWAITING_RESPONSE;
				trialTimer.start();
				// simulate response
				lastClicked = utils.NumCog.randomNumberLineClick(width,lineH);
				recordResponse( lastClicked );
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
				// after a delay, start the trial
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
			// the instructions are rather long so they are read aloud and not displayed on screen
			// the text of the instructions is given in a Word file "Catch the Monster Instructions" that accompanies this program
			utils.NumCog.displayNumberLine(g2, width, 2*height/5, lEnd, rEnd);
			utils.NumCog.displayText("Press the spacebar to begin.", g2, width/2, 4*height/5, "normal", Color.green);		
		} else if (moduleState==STATE_CONCLUSION) {
			utils.NumCog.displayText("Please ask the experimenter to start the next phase of the experiment.", g2, width/2, 2*height/5, "normal", Color.red);
		} else if (moduleState==STATE_TRIALS) {
			if ( trialState>=STATE_AWAITING_RESPONSE ) {
				utils.NumCog.displayFraction(stimuli.get(trialIdx), g2, width/2, stimH, "top", "large", Color.black);
				utils.NumCog.displayNumberLine(g2, width, lineH, lEnd, rEnd);
			}
			if ( trialState>STATE_AWAITING_RESPONSE ) {
				utils.NumCog.markNumberLineResponse(g2,width,lineH,lastClicked.x);
				utils.NumCog.displayText("Thank you! The next fraction will appear in a moment.", g2, width/2, msgH, "normal", Color.green);		
			}
		}
	}
	
	// input/output
	@Override
	public void mouseClicked(MouseEvent event) {
		// if currently running a trial and awaiting a response, and a valid response was received
		if ( moduleState==STATE_TRIALS && trialState==STATE_AWAITING_RESPONSE
				&& (event.getX() >= ((width-utils.NumCog.lineL)/2)) 
				&& (event.getX() <= width - ((width-utils.NumCog.lineL)/2)) 
				&& (event.getY() > lineH - 50) 
				&& (event.getY() < lineH + 50) ) {
			// record the response
			lastClicked = event.getPoint();
			recordResponse(lastClicked);
			// advance trial state and repaint
			trialState = STATE_RESPONSE_RECEIVED;
			repaint();
	        // set timer to advance trial after delay
			new java.util.Timer().schedule( new CallbackTimerTask( new Callback() {
					public void call() {
						advance();
					} } ), 750 );
		} else {
			return;
		}
	}
		
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if ( key==KeyEvent.VK_SPACE && ( moduleState==STATE_INTRODUCTION || moduleState==STATE_CONCLUSION ) ) {
				advance();
		} else {
			return;
		}
	}

	protected void recordResponse(Point clicked) {
		data.respVal	= utils.NumCog.convertPosToVal(clicked.x, width, lEnd, rEnd);
		data.PAE		= 100 * Math.abs( (data.respVal-data.stimVal)/(rEnd-lEnd) );
		data.rt			= trialTimer.get();
		logLineToFile( data.toString() );
	}
}
