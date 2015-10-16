
public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("/lena16/dartagnan2/IMAGEN/DataBaseIMAGEN/IMAGEN/");
		System.out.println("/lena16/dartagnan2/IMAGEN/DataBaseIMAGEN_test2/IMAGEN/");
	    float 	Lamy=-1,Lamy_=-1,Lhip=-1753,Lhip_=-1753,Ramy=-1,Ramy_=-1,Rhip=-1753,Rhip_=-1456;
		if(args.length==1)
	    	{
	    		MyImage	tmp=new MyImage();
	    		tmp.setVolume(args[0],Lamy,Lamy_,Lhip,Lhip_,Ramy,Ramy_,Rhip,Rhip_);
	    	}
	    	else
	    	{
		    	/*
		    	try
		    	{
		    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    	}
		    	catch(ClassNotFoundException e){}
		    	catch(InstantiationException e){}
		    	catch(IllegalAccessException e){}
		    	catch(UnsupportedLookAndFeelException e){}
		    	
	    		javax.swing.SwingUtilities.invokeLater
	    		(
	    			new Runnable()
	    			{
	    				public void run()
	    				{
	    					createAndShowGUI();
	    				}
	    			}
	    		);
	    		*/
	    		QCApp_IMAGEN me = new QCApp_IMAGEN();
	    		me.createAndShowGUI();
	    	}

	}

}
