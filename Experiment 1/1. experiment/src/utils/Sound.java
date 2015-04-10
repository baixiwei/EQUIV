// Sound.java by Lisa K. Fazio

package utils;

import java.util.ArrayList;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Creates a reusable Sound object.
 * 
 * !IMPORTANT! a JFXPanel must be created first, before any sounds will play.
 * This can easily be accomplished by calling Sound.createPanel().
 * 
 * In newer versions of Java, the sound may play anyway, but you will see a 
 * java.lang.IllegalStateException because the JFX Toolkit not initialized.
 * 
 * Also, note that the panel will keep the thread from which it is called open, so System.exit(0) should
 * be invoked to close the java application when it is finished.
 * 
 * For Java 1.6 and below, I *think* you need to install JavaFX and then include the "jfxrt.jar" library in your build path.
 * 
 * @author pwb
 *
 */
public class Sound {
	protected double volume = 1.0;
	public boolean loop = false;
	public int loopCount = javafx.scene.media.MediaPlayer.INDEFINITE;
	public ArrayList<MediaPlayer> mediaPlayers = new ArrayList<MediaPlayer>(); // Holds all the currently playing and paused MediaPlayers for this Sound

	protected String url = "";
	protected SoundThread st = null;
	protected boolean isPaused = false;
	protected boolean isPlaying = false;
	protected boolean panelCreated = false; // If the sound fails to play the first time, create a JFXPanel and try it again. Only try this once, however.
	public Sound(String url) {
		this.url = url;
		this.st = new SoundThread(url);
	}
	
	public static JFXPanel createPanel() {
		return new JFXPanel(); // We need this prepare JavaFX toolkit and environment. Gross, I know.
	}
	
	/**
	 * @return the latest created media player (that is either playing or paused) for this sound.
	 */
	public MediaPlayer getCurrentMediaPlayer() {
		if (mediaPlayers.size() == 0) {
			return null;
		}
		return mediaPlayers.get(mediaPlayers.size()-1);
	}
	
	/**
	 * Play the specified sound
	 * @return the MediaPlayer that plays this sound.
	 */
	public MediaPlayer play() {
		if (!isPaused) {
			st.run();
		} else {
			getCurrentMediaPlayer().play();
		}
		isPaused = false;
		isPlaying = true;
		return getCurrentMediaPlayer();
	}
	
	public void playAll() {
		for (MediaPlayer p : mediaPlayers) {
			if (!isPaused) {
				st.run();
			} else {
				p.play();
			}
			isPaused = false;
			isPlaying = true;
		}
	}
	
	public void pause() {
		getCurrentMediaPlayer().pause();
		isPaused = true;
		isPlaying = false;
	}
	
	public void mute(boolean doMute) {
		getCurrentMediaPlayer().setMute(doMute);
	}
	
	public void muteAll(boolean doMute) {
		for (MediaPlayer p : mediaPlayers) {
			p.setMute(doMute);
		}
	}
	
	public void stop() {
		if (getCurrentMediaPlayer() != null) {
			getCurrentMediaPlayer().stop();
			isPaused = false;
			isPlaying = false;
			removeFromMediaPlayers(getCurrentMediaPlayer());
		}
	}
	
	public void stopAll() {
		for (MediaPlayer p : mediaPlayers) {
			p.stop();
		}
		isPaused = false;
		isPlaying = false;
		mediaPlayers.clear();
	}
	
	public void pauseAll() {
		for (MediaPlayer p : mediaPlayers) {
			p.pause();
		}
		isPaused = true;
		isPlaying = false;
	}
	
	public boolean isPaused() {
		return isPlaying;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void seek(Duration d) {
		getCurrentMediaPlayer().seek(d);
	}
	
	protected class SoundThread implements Runnable {
		protected String url = "";
		protected MediaPlayer mp = null;
		
		SoundThread(String url) {
			this.url = url;
		}

		@Override
		public void run() {
			try {
				mp = new MediaPlayer(new Media(url));
				mediaPlayers.add(mp);
				mp.setVolume(volume);
				if (loop) {
					 mp.setCycleCount(loopCount);
				}
				mp.play();
				
				// Automatically remove the MediaPlayer if the end is reached or there is an error.
				mp.setOnEndOfMedia(new Runnable() {
					@Override
					public void run() {
						removeFromMediaPlayers(mp);
					}
				});
				mp.setOnError(mp.getOnEndOfMedia());
			} catch (java.lang.IllegalStateException e) {
				if (panelCreated) {
					//Logger.log(Level.SEVERE, "Could not play " + url + ". The JFXPanel may not have been initialized. Call Sound.createPanel() before creating a Sound onject.", e);
				} else {
					createPanel();
					panelCreated = true;
					run();
				}
			}
		}
	}
	
	protected synchronized void removeFromMediaPlayers(MediaPlayer mp) {
		int index = mediaPlayers.indexOf(mp);
		if (index > -1) {
			mediaPlayers.remove(index);
		}
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
		MediaPlayer mp = this.getCurrentMediaPlayer();
		if (mp != null) {
			mp.setVolume(volume);
		}
	}
	
	public static void main(String[] args) {
		// Create the sound panel (see the javadoc for this class for more info on why you need to do this).
		Sound.createPanel();
		
		// Set the path to the sound. Note that this must be a valid URL. No unencoded spaces, for instance. Use UTF-8 encoding on the path if necessary.
		Sound s = new Sound("file:///"+"C:/Users/pwb/Desktop/imBack.mp3");
		
		// This is how you would set it to loop.
		s.loop = true;
		
		// Now play it.
		s.play();
	}
}