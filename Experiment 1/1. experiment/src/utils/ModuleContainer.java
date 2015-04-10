package utils;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * A module container is a special kind of module that contains multiple modules within itself.
 * When start() is called, the module container runs start() on the modules inside it one by one.
 * Because a module container IS a module, it can itself be contained in another module container.
 * It can then be run by the containing module container just like any other module.
 * In a hierarchical structure of module containers, the outermost container should create and control the containing JFrame (window).
 */

public class ModuleContainer extends Module {
	
	protected static final long serialVersionUID = 1L;
	
	protected ArrayList<Module> modules = new ArrayList<Module>();
	protected JPanel cards = new JPanel( new CardLayout() );

	private class ModuleContainerCallback implements Callback {
		
		protected JPanel cards;
		protected Module next_module;
		
		public ModuleContainerCallback( JPanel cd, Module nm ) {
			cards = cd;
			next_module = nm;
		}
		
		public void call() {
			((CardLayout) cards.getLayout()).next( cards );
			next_module.start();
		}
	}
	
	public ModuleContainer(int w, int h, String fName, String mName) {
		super(w,h,fName,mName);
		this.add( cards, BorderLayout.CENTER );
	}
	
	public void add( Module M ) {
		modules.add(M);
		cards.add(M,M.toString());
	}

	@Override
	public void start() {

		logLineToFile(mName);
		
		if ( modules.size()==0 ) {
		
			System.out.println("ModuleContainer run with empty module list, terminating.");
			end();
		
		} else {
		
			// set callbacks for the modules
			for (int i=0; i<modules.size(); i++) {
				if (i<(modules.size()-1)) {
					modules.get(i).setCallback( new ModuleContainerCallback( cards, modules.get(i+1) ) );
				} else {
					modules.get(i).setCallback(new Callback() {
						public void call() {
							end();
						}
					});
				}
			}
			
			// run the first module
			modules.get(0).start();
			
		}
	}

	public static void main(String[] args) {
		
		int w			= 1400;
		int h			= 1000;
		String fName 	= "data\\sample_manager_test.txt";
		String mName	= "container";
		String[] mNames	= { "part 1", "part 2" };
		String[] cont	= { "trial 1", "trial 2" };
		
		ModuleContainer mc = new ModuleContainer(w,h,fName,mName);
		mc.add( new ModuleSample( 1400, 1000, fName, mNames[0], cont ) );
		mc.add( new ModuleSample( 1400, 1000, fName, mNames[1], cont ) );

		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.add(mc, BorderLayout.CENTER);
		f.setVisible(true);
		f.pack();
		f.setLocationRelativeTo(null);
		
		mc.start();
		
	}
}
