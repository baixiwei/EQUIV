// TextPages.java by David W. Braithwaite
// Presents a series of text strings on separate pages, using spacebar to advance

package utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TextPages extends Module {
	
	protected static final long serialVersionUID = 1L;
	
	String[] text	= new String[] { "placeholder" };
	String mode;
	int idx;
	boolean ready;
	Graphics2D g2;
	
	public TextPages(String[] text, int w, int h, String fN, String mN, String m) {
		super(w,h,fN,mN);
		this.text = text.clone();
		this.mode = m;
	}
	
	public static void main( String[] args ) {
		String fName 			= "test.txt";
		TextPages c 			= new TextPages( new String[] { "this is a test", "this is another test" }, 
				1300, 700, fName, "textpages", "normal" );
		JFrame f 				= new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.setVisible(true);
		f.add(c, BorderLayout.CENTER);
		f.pack();
		f.setLocationRelativeTo(null);
		c.start();
	}
	
	@Override
	public void start() {
        addKeyListener(this);
        addMouseListener(this);
		requestFocusInWindow();
		idx = 0;
		ready = true;
		if ( mode.equals("auto") ) {
			ready = false;
			advance();
		} else {
			repaint();
		}
	}
	
	public void advance() {
		if ( (idx+1)>=text.length ) {
			end();
		} else {
			idx++;
			ready = true;
			if ( mode.equals("auto") ) {
				ready = false;
				advance();
			} else {
				repaint();
			}
		}
	}
	
	public void end() {
		if ( callback==null ) {
			System.out.println("Module callback not set in module "+mName+", closing.");
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
			topFrame.dispose();
		} else {
			callback.call();
		}		
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2	= (Graphics2D)g;
		utils.NumCog.displayText(text[idx], g2, width/2, height/2, "center", "normal", Color.black);
	}

	public void keyReleased(KeyEvent e) {
		if ( e.getKeyCode()==KeyEvent.VK_SPACE && ready ) {
			ready = false;
			new java.util.Timer().schedule( new CallbackTimerTask( new Callback() {
				public void call() {
					advance();
				} } ), 250 );
		}
	}

}
