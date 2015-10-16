import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.JComponent;

class MyGraphs extends JComponent
{           
	QCApp_IMAGEN	myQCApp_IMAGEN;
	String	subjectsDir;
	String	regions[]={"ICV","BV","LTh","RTh","LCa","RCa","LPu","RPu","LPa","RPa","LHip","RHip","LAmy","RAmy","LAcc","RAcc"};
	double	mean[]=new double[16];
	double	std[]=new double[16];
    double	R=50;
    double	selectedSubjectVolumes[]=new double[16]; // calculé dans getVolumesForSubject
	
    public void paint(Graphics g)
	{
    	Graphics2D	g2=(Graphics2D)g;
    	float		val;
    	int			i,x[]=new int[17];
    	Dimension	dim=this.getSize();
    	Stroke		defaultStroke=g2.getStroke();
    	BasicStroke dashed = new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,5.0f,new float[]{5.0f},0.0f);
    	
    	for(i=0;i<=16;i++)
    		x[i]=(int)((dim.width-1)*i/(double)16.0);
    	
    	// draw brain structure bars, with colours depending on selected-subject values
    	g2.setColor(Color.black);
    	for(i=0;i<16;i++)
    	{
    		if(selectedSubjectVolumes[0]!=0)
    		{
    			val=(float)((selectedSubjectVolumes[i]-mean[i])/(2.0*std[i]));
    			//System.out.println("val= "+val);
    			if(val>=0 && val<=1)	g2.setColor(new Color(val,1.0f-val,0.0f));
		    	else
		    	if(val>=-1 && val<0)	g2.setColor(new Color(0.0f,1.0f+val,-val));
		    	else
		    							g2.setColor(Color.white);
    		}
    		else
    			g2.setColor(Color.white);
			g2.fillRect(x[i],0,x[i+1],dim.height);
    		g2.setColor(Color.black);
    		g2.drawRect(x[i],0,x[i+1],dim.height);
    	}
    	
    	// draw dots for selected subject values
    	g2.setColor(Color.black);
    	if(selectedSubjectVolumes[0]!=0)
    	for (i=0;i<16;i++)
    	{
    		val=(float)(0.5f+(selectedSubjectVolumes[i]-mean[i])/(2.0*std[i])/2.0);
    		if(val<0) val=0;
    		if(val>1) val=1;
    		// fillOval(int x, int y, int width, int height) avec la couleur definit par g2.setColor(Color.black)
	    	g2.fillOval((x[i]+x[i+1])/2-5,(int)(dim.height*(1-val))-5,11,11);
    	}
    	
    	// draw mean and +/- 1 std values
    	g2.setColor(Color.black);
    	g2.drawLine(0,dim.height/2,dim.width,dim.height/2);
		g2.setStroke(dashed);
    	g2.drawLine(0,dim.height/4,dim.width,dim.height/4);
    	g2.drawLine(0,dim.height*3/4,dim.width,dim.height*3/4);

    	// draw brain structure names
    	for (i=0;i<16;i++)
    	{
			g2.translate((x[i]+x[i+1])/2,2);
			g2.rotate(Math.PI/2.0);
			g2.drawString(regions[i], 0, 0);
			g2.rotate(-Math.PI/2.0);
			g2.translate(-(x[i]+x[i+1])/2,-2);
    	}
	}
	public int getVolumesForSubject(String filename, double x[])
	{
		BufferedReader	input;
		Scanner			sc;
		int				i;
		int				err=0;
		String 			a=" ";
		for(i=0;i<16;i++)
			x[i]=-1;
		
		// Load ICV and BrainSeg data
		try
		{
			input=new BufferedReader(new FileReader(subjectsDir+"/"+filename+"/Process_FSL/size_FSL.csv"));
			sc=new Scanner(input.readLine());//"000000022453,1347750,0.825819");//
			sc.useDelimiter(",");
			//System.out.println("dans try: passe à la seconde ligne: "+sc.nextFloat());
			sc.next(); // skip subject ID column
			for(i=0;i<2;i++){
				a=sc.next();
				x[i]=Double.valueOf(a.trim()).doubleValue();		
			}
			sc.close();
		}
		catch (IOException e)
		{
			err=1;
			return err;
		}
		catch (InputMismatchException e)
		{
			System.out.println("erreur InputMismatchException "+e.getMessage());
			err=1;
			return err;
		}
		//System.out.println("subectsDir"+subjectsDir+" filename: "+filename);
		// Load Subcortical data
		try
		{
			
			input=new BufferedReader(new FileReader(subjectsDir+"/"+filename+"/Process_FSL/LandRvolumes.csv"));
			input.readLine(); // skip header row
			
			sc=new Scanner(input.readLine());
			sc.useDelimiter(",");
			sc.next(); // skip subject ID column
			for(i=2;i<16;i++){
				a=sc.next();
				x[i]=Double.valueOf(a.trim()).doubleValue();
			}
			sc.close();
		}
		catch (IOException e)
		{
			err=1;
			return err;
		}
		catch (InputMismatchException e)
		{
			err=1;
			return err;
		}

		return err;
	}
	public void configure(String dir)
	{
		subjectsDir=dir;
		double		s0=0;
		double		s1[]=new double[16];
		double		s2[]=new double[16];
		double		x[]=new double[16];
		int			i,j;
		File		files[]=(new File(dir)).listFiles();
		int			err;
		Arrays.sort(files);
		for(i=0;i<files.length;i++)
		{
			//System.out.println("charAt(0) "+files[i].getName().charAt(0));
			//System.out.println("i "+i);
			//System.out.println(files[i].getName());
			if(files[i].getName().charAt(0)=='.' ||files[i].getName().length()!=12) //files[i].getName().charAt(files[i].getName().length()-1)=='f' )
				continue;
			err=getVolumesForSubject(files[i].getName(),x);
			if(err==1)
			{
				System.out.println(i);
				System.out.println(files[i].getName());
				myQCApp_IMAGEN.setQC(files[i].getName(),2,"Segmentation results unavailable"); // apparait dans la colonne comments si y'a pas le fichier qc.txt
				continue;
			}
			
			for(j=0;j<16;j++)
			{
				s1[j]+=x[j];
				s2[j]+=x[j]*x[j];	
			}

			s0++;
			myQCApp_IMAGEN.images.image.printStatusMessage((i+1)+"/"+files.length);
			myQCApp_IMAGEN.images2.image.printStatusMessage((i+1)+"/"+files.length);
    	}
    	for(j=0;j<16;j++)
    	{
			mean[j]=s1[j]/s0;
			std[j]=Math.sqrt((s0*s2[j]-s1[j]*s1[j])/(s0*(s0-1)));
			System.out.println(regions[j]+":\t"+mean[j]+" ± "+std[j]);

		}
	}
	public void setSelectedSubject(String filename)
	{
		getVolumesForSubject(filename,selectedSubjectVolumes);
		repaint();
	}
    public MyGraphs()
    {
    	//myQCApp_IMAGEN=this.getParent();
    }
}