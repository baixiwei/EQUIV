// NumCog.java by David W. Braithwaite
// Provides commonly used graphics and audio methods for numerical cognition tasks 
// Sharing these methods across different modules will create consistency among the modules.

package utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class NumCog {
	
	// display control for ...
	// text
	public static Font fontNormal		= new Font("SansSerif", Font.PLAIN, 24);
	public static Font fontLarge		= new Font("SansSerif", Font.PLAIN, 38);
	// fractions
	protected static int vinculum_pad	= 5;			// if you don't know what a vinculum is, look it up
	// number lines
	public static final int lineL 		= 1000; 		// length of line in pixels
	public static final int lineW		= 3;

	public static void displayText(String text, Graphics2D g2, int xcent, int ytop, String size, Color color) {
		displayText(text,g2,xcent,ytop,"top",size,color);
	}
	
	public static void displayText(String text, Graphics2D g2, int xcent, int y, String yalign, String size, Color color) {
		Font font = (size=="normal") ? fontNormal : fontLarge;
		FontMetrics metricsfont = g2.getFontMetrics(font);
		g2.setFont(font);
		g2.setColor(color);
		int ybottom = y;
		if ( yalign=="top" ) {
			ybottom = y + getTextHeight(g2,size);
		} else if ( yalign=="center" ) {
			ybottom = y + getTextHeight(g2,size)/2;
		}
		g2.drawString(text, xcent-metricsfont.stringWidth(text)/2, ybottom);
	}
	
	public static int getTextHeight(Graphics2D g2, String size) {
		if ( size=="normal" ) {
			return g2.getFontMetrics(fontNormal).getAscent()-10;
		} else if ( size=="large" ) {
			return g2.getFontMetrics(fontLarge).getAscent()-14;
		} else {
			return 0;
		}
	}
	
	public static void displayImage(String imgFn, Graphics2D g2, int xcent, int ycent, ImageObserver io) {
		Image img = Toolkit.getDefaultToolkit().getImage(imgFn);
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		g2.drawImage(img, xcent-w/2, ycent-h/2, io);
	}

	public static void displayFraction(Fraction f, Graphics2D g2, int xcent, int y, String yalign, String size, Color color) {
		int numerator	= f.num;
		int denominator	= f.den;
		FontMetrics mf 	= g2.getFontMetrics((size=="normal") ? fontNormal : fontLarge);
		int strH		= getTextHeight(g2,size);
		int strW		= Math.max( mf.stringWidth(numerator+""),  mf.stringWidth(denominator+""));
		int vincH		= 3;	// height of vinculum
		if ( size=="large" ) {
			strW		= Math.max( mf.stringWidth(numerator+""),  mf.stringWidth(denominator+""));
			vincH		= 4;
		}
		int ytop = 0;
		if ( yalign=="top" ) {
			ytop = y;
		} else if ( yalign=="bottom" ) {
			ytop = y - ( vinculum_pad*2 + vincH + strH*2 );
		} else if ( yalign=="center" ) {
			ytop = y - ( vinculum_pad*2 + vincH + strH*2 )/2 ;
		}
		displayText( numerator+"", g2, xcent, ytop, size, color );
		g2.fillRect( xcent-strW/2, ytop + strH + vinculum_pad, strW, vincH );
		displayText( denominator+"", g2, xcent, ytop + strH + vinculum_pad + vincH + vinculum_pad, size, color );
	}
	
	public static void displayFractionAsMixedNumber(Fraction f, Graphics2D g2, int xcent, int y, String yalign, String size, Color color) {
		if ( f.num<f.den ) {
			displayFraction(f, g2, xcent, y, yalign, size, color);
		} else if ( f.num==f.den ) {
			displayText("1", g2, xcent, y, yalign, size, color);
		} else {
			int whole		= (int)((f.num-(f.num%f.den))/f.den);
			Fraction frac	= new Fraction(f.num%f.den, f.den);
			Font font = (size=="normal") ? fontNormal : fontLarge;
			FontMetrics metricsfont = g2.getFontMetrics(font);
			int wholeW		= metricsfont.stringWidth(whole+"");
			int fracW		= Math.max( metricsfont.stringWidth((f.num%f.den)+""), metricsfont.stringWidth(f.den+"") );
			int maxW		= Math.max( wholeW, fracW )+4;
			displayText( whole+"", g2, xcent-(maxW/2), y, yalign, size, color);
			displayFraction( frac, g2, xcent+(maxW/2), y, yalign, size, color);
		}
	}

	// display an arithmetic problem consisting of two fractions separated by an operation
	public static void displayFractionArithmetic(Fraction f1, String oper, Fraction f2, Graphics2D g2, int xcent, int y, String yalign, String size, Color color) {
		FontMetrics mf 	= g2.getFontMetrics((size=="normal") ? fontNormal : fontLarge);
		int f1w			= Math.max( mf.stringWidth(f1.num+""),  mf.stringWidth(f1.den+""));
		int f2w			= Math.max( mf.stringWidth(f2.num+""),  mf.stringWidth(f2.den+""));
		int operw		= mf.stringWidth(" " + oper + " ");
		displayFraction(f1, g2, xcent-(operw/2)-(f1w/2), y, yalign, size, color);
		displayText(" "+oper+" ", g2, xcent, y, yalign, size, color);
		displayFraction(f2, g2, xcent+(operw/2)+(f2w/2), y, yalign, size, color);
	}
	
	public static void displayNumberLine(Graphics2D g2, int panelW, int lineH, int lEnd, int rEnd) {
		// general display with variable height
		g2.setColor( Color.black );
		g2.fillRect( ((panelW-lineL)/2)-3, lineH-20, 3, 40 );			// left endpoint
		g2.fillRect( lineL+(panelW-lineL)/2, lineH-20, 3, 40 );			// right endpoint
		g2.fillRect( (panelW-lineL)/2, lineH, lineL, lineW );			// number line
		g2.setFont( fontNormal );
		g2.drawString( lEnd+"", (panelW-lineL)/2-8, lineH+60 );			// left endpoint label
		g2.drawString( rEnd+"", lineL+(panelW-lineL)/2-6, lineH+60 ); 	// right endpoint label
	}
	
	public static Point randomNumberLineClick(int panelW, int lineH) {
		return new Point(Utils.getRand((panelW-lineL)/2, (panelW+lineL)/2), lineH );
	}
	
	public static void markNumberLineResponse(Graphics2D g2, int panelW, int lineH, int clicked_x) {
		g2.setColor(Color.GREEN); 
		g2.fillRect(clicked_x, lineH - 20, 3, 40); //mark where they clicked in green
	}
	
	public static void markFractionPos(Fraction f, Graphics2D g2, String size, int panelW, int lineH, int lEnd, int rEnd, Color color) {
		// place a mark at the position of fraction f on a number line at height lineH,
		// and add a label above the mark using the given font size
		int xpos = convertValToPos( f.getFloat(), panelW, lEnd, rEnd );
		g2.setColor(color);
		g2.fillRect(xpos,lineH-20,3,40);
		displayFraction( f, g2, xpos, lineH-25, "bottom", size, color );
	}

	public static void markWholeNumberPos(int i, Graphics2D g2, String size, int panelW, int lineH, int lEnd, int rEnd, Color color) {
		// place a mark at the position of whole number i on a number line at height lineH,
		// and add a label above the mark using the given font size
		int xpos = convertValToPos( (float)i, panelW, lEnd, rEnd );
		g2.setColor(color);
		g2.fillRect(xpos,lineH-20,3,40);
		utils.NumCog.displayText( i+"", g2, xpos, lineH-25-utils.NumCog.getTextHeight(g2,size), size, color );
	}
	
	public static float convertPosToVal(int pos, int panelW, int lEnd, int rEnd) {
		// convert position on number line to numerical magnitude
		float lEndPos	= (float)((panelW-lineL)/2);
		float value		= lEnd + (rEnd-lEnd) * ( (((float)pos)-lEndPos) / lineL );
		return value;
	}

	public static int convertValToPos(float val, int panelW, int lEnd, int rEnd) {
		// convert numerical magnitude to position on number line
		return Math.round( (panelW/2 - lineL/2) + lineL * (val / (rEnd-lEnd)) );
	}
	
	public static void playSound(String fName) {
		try {
			File f = new File(fName);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
	         e.printStackTrace();
		}
	}
}
