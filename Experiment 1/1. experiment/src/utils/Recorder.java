// Recorder.java by David W. Braithwaite
// revised from http://www.codejava.net/coding/capture-and-record-sound-into-wav-file-with-java-sound-api

package utils;

import javax.sound.sampled.*;
import java.io.*;

// test as is
// constructor with output file name
// when starting, also create conditions to close it via spacebar

public class Recorder implements Runnable {
	
	// file to which recorded audio will be saved
	private String recordedFileName;
	private File wavFile;

	// format of audio file
	private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
	
	// the line from which audio data is captured
	private TargetDataLine targetDataLine;
	
	public Recorder( String recordedFileName ) {
		this.recordedFileName = recordedFileName;
		this.wavFile = new File( this.recordedFileName + ".wav" );
		try {
	        DataLine.Info dataLineInfo2 = new DataLine.Info(TargetDataLine.class,new AudioFormat(8000.0F, 16, 1, true, false));
	        this.targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo2);
		} catch (Exception e) {
	        e.printStackTrace();
	        System.exit(0);
	    }
	}
	
	public void run() {
		try {
			targetDataLine.open( new AudioFormat(8000.0F, 16, 1, true, false));
			targetDataLine.start();
			AudioSystem.write( new AudioInputStream(targetDataLine), fileType, wavFile );
		} catch (Exception e){
     	    e.printStackTrace();
 	    }
	}
	
	public void finish() {
		targetDataLine.stop();
		targetDataLine.close();
	}
}