// SampleModule.java by David W. Braithwaite

// concrete example of the abstract module class
// presents a series of text slides
// on each slide, user must click a mouse button, then press space to continue
// trial index and rt are recorded for each trial

package utils;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;

public class ModuleSample extends Module {

	protected static final long serialVersionUID = 1L;
	
	// properties
	
	protected int trialIdx;
	protected int trialState;
	protected int STATE_AWAITING_INPUT=1;
	protected int STATE_INPUT_RECEIVED=2;
	protected Timer moduleTimer = new Timer();
	protected Timer trialTimer = new Timer();
	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	protected String[] content;

	protected Font font		= new Font("SansSerif", Font.PLAIN, 24);
	protected Font large	= new Font("SansSerif", Font.PLAIN, 38);
	Graphics2D g2;
	FontMetrics metrics;
	FontMetrics metricsfont;

	class TrialData {
		public int trialIdx;
		public long rt;
		public String toString() {
			return( trialIdx + "," + rt );
		}
	}
	protected TrialData data=new TrialData();

	// constructor & main
	
	public ModuleSample(int w, int h, String fName, String mName, String[] C) {
		super(w,h,fName,mName);
		content	= C.clone();
    }
	
	public static void main(String[] args) {
		ModuleSample m 	= new ModuleSample( 1400, 1000, "sample_module_test.txt", "module 1", new String[] {"Page 1", "Page 2", "Page 3"} );
		JFrame f		= new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m.setCallback( new Callback() {
			public void call() {
				f.dispose();
			}
		});
		f.add( m, BorderLayout.CENTER );
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		m.start();
	}

	// control
	
	@Override
	public void start() {
		logLineToFile(mName);
		logLineToFile("Start time: " + format.format(new Date()) );
		moduleTimer.start();
        addKeyListener(this);
        addMouseListener(this);
		requestFocusInWindow();
		trialIdx = -1;
		advance();
	}
	
	protected void advance() {
		trialIdx++;
		if ( trialIdx<content.length ) {
			startTrial();
		} else {
			end();
		}
	}

	protected void startTrial() {
		data.trialIdx 	= trialIdx;
		trialState		= STATE_AWAITING_INPUT;
		trialTimer.start();
		repaint();
	}

	protected void endTrial() {
		logLineToFile( data.toString() );
		advance();
	}
	
	@Override
	public void end() {
		logLineToFile("End time: " + format.format(new Date()) );
		logLineToFile("Total time: " + moduleTimer.get());
		this.callback.call();
	}
	
	// display
	
	public void paintComponent (Graphics g) {
		
		super.paintComponent(g);
		g2 = (Graphics2D)g;
		metrics = g2.getFontMetrics(large);
		metricsfont = g2.getFontMetrics(font);

		showText( content[trialIdx], height/4 );
		if (trialState==STATE_AWAITING_INPUT) {
			showText( "Please click the mouse.", height*2/4 );
		} else if (trialState==STATE_INPUT_RECEIVED) {
			showText( "Mouse clicked!", height*2/4 );
			showText( "Press the space bar to continue.", height*3/4 );
		}
		
	}
	
	protected void showText(String t, int h) {
		g2.setFont(large);
		g2.drawString(t,width/2-metrics.stringWidth(t)/2,h);
	}
	
	// input
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (trialState==STATE_AWAITING_INPUT) {
			trialState	= STATE_INPUT_RECEIVED;
			data.rt 	= trialTimer.get();
		}
		repaint();
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (trialState==STATE_INPUT_RECEIVED) {
			if ( e.getKeyCode()==KeyEvent.VK_SPACE ) {
				endTrial();
			}
		}
	}
	
}
