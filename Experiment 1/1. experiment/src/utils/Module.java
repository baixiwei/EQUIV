package utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public abstract class Module extends JPanel implements KeyListener, MouseListener {
	
	protected static final long serialVersionUID = 1L;
	protected Callback callback;	// function to call on module completion
	protected String mName;			// name of module
	protected String fName;			// name of file for data output
	protected File fOut;			// file stream for data output
	public int width;				// size of content to be displayed
	public int height;
	
	public Module(int width, int height, String fileName, String moduleName) {
		this.fName	= fileName;
		this.fOut	= new File(fileName);
		this.mName 	= moduleName;
		this.width 	= width;
		this.height = height;
		setPreferredSize(new Dimension(width,height));
        setBackground(Color.white);
	}
	
	public void setCallback(Callback c) {
		this.callback = c;
	}
	
	public String toString() {
		return mName;
	}
	
	// override start() to make the module actually do something
	abstract public void start();

	// override end() to make more happen on module close, e.g. data storage
	public void end() {
		if ( callback==null ) {
			System.out.println("Module callback not set in module "+mName+", closing.");
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
			topFrame.dispose();
		} else {
			callback.call();
		}
	}

	// override some of the following in order to respond to user input
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	protected void logLineToFile(String output) {
		System.out.println(output);
		// following lines will only work if constructor is modified to set value of fOut
		if (fOut != null) {
			try {
				FileOutputStream out = new FileOutputStream(fOut, true);
				out.write((output+"\n").getBytes());
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println( "fOut is null" );
		}
	}
}
