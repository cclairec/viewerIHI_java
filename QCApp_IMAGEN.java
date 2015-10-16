import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class QCApp_IMAGEN extends JComponent
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static JFrame 				f;
	static JFrame 				f2;
	static JTextArea			status;
	static JTextArea			status2;
	static JButton				chooseButton;
	static JButton				saveButton;
	static JButton				loadButton;
	static JButton				statButton;
	static String 						file_name;
	static JPanel				panel1;
	static JPanel				panel2;
	static JSlider				slider1;
	static JSlider				slider2;
	static JScrollPane imagesScrollPane;
	static JScrollPane images2ScrollPane;
	static GroupLayout 			layout;
	static JTable 				table;
	static DefaultTableModel	model;
	static MyImages 			images;
	static MyImages 			images2;
	static MyGraphs 			graphs;
	static String				subjectsDir;
	static QCApp_IMAGEN				me;
	
	/*public QCApp_IMAGEN()
	{
		super(new BorderLayout());
	}*/
	
	public static void mouseDownOnImage(MouseEvent e)
	{
		
		int	i;
		int xx=panel2.getLocation().x+e.getPoint().x+f.getLocation().x;

	if(xx>=e.getLocationOnScreen().x){		
		if(images.selectedImage==0)
		{
	
			for(i=0;i<images.image.img.length;i++)
			{
				if(images.rect[i].contains(e.getPoint()))
				{
					images.selectedImage=i+1;
					slider1.setMaximum(images.getSize().height);
					slider1.setMinimum(0);
					
					break;
				}
			}
			images.repaint();
		}
		else
		{
			Dimension d=images.getParent().getSize();
			Rectangle backRect=new Rectangle(1,1,48,20);
			if(backRect.contains(e.getPoint()))
			{
				images.selectedImage=0; // retour à l'affichage des imagettes
				images.selectedSlice=0.5;
				images.repaint();

			}
		}
	}
	else { 		System.out.println("panel2.getLocation(): "+panel2.getLocation()+" slider1.getLocation "+slider1.getLocation()+ " f.getLocation() "+ f.getLocation());
		System.out.println("Mouse clicked at x: "+xx+" e.getLocationOnScreen().x : "+e.getLocationOnScreen().x);		
	}
if(xx<e.getLocationOnScreen().x){
	if(images2.selectedImage==0)
{
	
	for(i=0;i<images2.image.img.length;i++)
			{
				if(images2.rect[i].contains(e.getPoint()))
				{
					images2.selectedImage=i+1;
					slider2.setMaximum(images2.getSize().height);
					slider2.setMinimum(0);
					break;
				}
			}
			images2.repaint();
	}

		else
		{
			Dimension d=images2.getParent().getSize();
			Rectangle backRect=new Rectangle(1,1,48,20); // position du rectangle BACK
			if(backRect.contains(e.getPoint()))
			{
				images2.selectedImage=0;
				images2.selectedSlice=0.5;
				images2.repaint();

			}
		}
}

	}
	
	/*public void mouseClicked(MouseEvent e)
	{
	
	 System.out.println("Mouse clicked at: "+e.getPoint());
	}*/
	
	/*public static void stateChanged_bis(ChangeEvent evt) {
		JSlider slider = (JSlider) evt.getSource();
  		images.selectedSlice=slider.getValue();
  		slider1.revalidate();
  		System.out.println("Slider1 aa: " + slider1.getValue()+" images.selectedSlice "+ images.selectedSlice);
		images.repaint();
      }*/
	
	/*public static void mouseDraggedOnImage(MouseEvent e)
	{
		int	i;
		Dimension d1=images.getParent().getSize();
		Dimension d2=images2.getParent().getSize();
		

		images.selectedSlice=slider1.getValue();//e.getPoint().y/(double)images.getSize().height;
		images.repaint();

		images2.selectedSlice=e.getPoint().y/(double)images2.getSize().height;
		images2.repaint();
		
	}*/
	public static void selectSubject(JTable table)
	{
		int		iii=table.getSelectedRows()[0];
		float Lamy=-1,Lamy_=-1,Lhip=-1,Lhip_=-1,Ramy=-1,Ramy_=-1,Rhip=-1,Rhip_=-1;
		//float Lamy=new float(),Lamy_=new float(),Lhip=new float(),Lhip_=new float(),Ramy=new float(),Ramy_=new float(),Rhip=new float(),Rhip_=new float();
		//float Lamy,Lamy_,Lhip,Lhip_,Ramy,Ramy_,Rhip,Rhip_;
		double x[]= new double[8];
		//for(int iii=i; iii<=2041; iii++){
		String a;
		String	subject=model.getValueAt(iii,2).toString();
		BufferedReader	input;
		Scanner			sc;
		
		images.volBack=null;
		images.image.setVolume(subjectsDir+"/"+subject,Lamy,Lamy_,Lhip,Lhip_,Ramy,Ramy_,Rhip,Rhip_);
		images.repaint();
		images2.volBack=null;
		images2.image.setVolume(subjectsDir+"/"+subject,Lamy,Lamy_,Lhip,Lhip_,Ramy,Ramy_,Rhip,Rhip_);
		images2.repaint();
    	
		
		try
		{
			
			input=new BufferedReader(new FileReader(subjectsDir+"/"+subject+"/QC/LandRvolumes_SACHA.txt"));
			//System.out.println(subjectsDir+"/"+subject+"/QC/LandRvolumes_SACHA.txt");
			input.readLine();
			sc=new Scanner(input.readLine());
			
			sc.useDelimiter(",");
			sc.next(); // skip subject ID column
			for(int ii=0;ii < x.length;ii++){
				a=sc.next();
				//System.out.println("a "+a);
				//System.out.println("     "+x[ii]);
				x[ii]=Double.valueOf(a.trim()).doubleValue();
				//System.out.println("     "+ii+"       "+x[ii]);
			}
			sc.close();
input.close();
		
    	//images.image.printStatusMessage("Subject: "+subject+". ");
    	images2.image.printStatusMessage("Subject: "+subject+". LHip_voxels = "+x[2]+" / Lhip_ = "+x[3]+" // Rhip_voxels = "+x[6]+" / Rhip_ = "+x[7]); 
		}
		catch (IOException e)
		{
			System.out.println("erreur: "+subjectsDir+"/"+subject+"/QC/LandRvolumes_SACHA.txt   "+e.getMessage());
		}
		catch (InputMismatchException e)
		{
			System.out.println("erreur: "+subjectsDir+"/"+subject+"/QC/LandRvolumes_SACHA.txt   "+e.getMessage());
		}
		
    	graphs.setSelectedSubject(subject);
		//} // fin for en commentaire
}
	
	public static void chooseDirectory() throws IOException
	{
		// select Subjects directory
		final JFileChooser fc=new JFileChooser();
		File ff = new File(new File("/lena16/dartagnan2/IMAGEN").getCanonicalPath());
		fc.setCurrentDirectory(ff);
		fc.setDialogTitle("Choose Subjects Directory...");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal=fc.showOpenDialog(null);
		if(returnVal!=JFileChooser.APPROVE_OPTION)
			return;
		File file = fc.getSelectedFile();
		subjectsDir=file.getAbsolutePath();
    	images.image.printStatusMessage("Subjects Directory: "+subjectsDir+".");
	// images2.image.printStatusMessage("                                                                        Subjects Directory: "+subjectsDir+".");	
		// add files to table

		File			files[]=file.listFiles();
        Vector<Object>	row;
        int				n=1;
        int				i,j,k,l;
        Arrays.sort(files);
		for(i=0;i<files.length;i++)
		{
			if(files[i].getName().charAt(0)=='.' || files[i].isFile())
				continue;
			
			images.image.printStatusMessage((i+1)+"/"+files.length/2);
			//images2.image.printStatusMessage((i+1)+"/"+files.length);
			row= new Vector<Object>();
			row.add(n);
			row.add(1);
			row.add(files[i].getName());
			row.add(new String());
			model.insertRow(model.getRowCount(),row);
			n++;
    	}
    	
    	// configure stats graphs
    	graphs.configure(subjectsDir);
    	
    	// if there is a qc.txt file inside subjectsDir, load it.
    	file_name = subjectsDir+"/qc.txt";
    	File			f=new File(subjectsDir+"/qc.txt");
    	if(f.exists())
    	{
    		System.out.println("qc.txt file present, read it.");

		BufferedReader	input;
    		//StringTokenizer	st;
    		int				qc;
    		String			sub;
    		String			comment;
    		String			line;
			try
			{
				
				input=new BufferedReader(new FileReader(f));
				input.readLine(); // skip header row
				
i=0;
					line=input.readLine();
					j=line.indexOf("\t");
					k=line.indexOf("\t",j+1);
					l=line.indexOf("\t",k+1);
					sub=line.substring(0,j);
					qc=Integer.parseInt(line.substring(j+1,k));
					comment=line.substring(k+1,l);

				for(i=0;i<model.getRowCount();i++)
				{
					//System.out.println("blaaaaaaaaaaa "+sub+"  =?= "+model.getValueAt(i,2).toString());
					if(sub.equals(model.getValueAt(i,2).toString()))
					{
						
						//images.image.printStatusMessage("ERROR: qc.txt file does not match current Subjects directory");
						//return;
						
					model.setValueAt(qc,i,1);
					model.setValueAt(comment,i,3);
					model.setValueAt(" ",i,4);
					model.setValueAt(" ",i,5);
					model.setValueAt(" ",i,6);
					model.setValueAt(" ",i,7);
					/*model.setValueAt(" ",i,8);
					model.setValueAt(" ",i,9);
					model.setValueAt(" ",i,10);
					model.setValueAt(" ",i,11);*/
					
					}
					else i--;
					line=input.readLine();
					if (line==null){
						//System.out.println("ligne vide.");
					}
					else{

					j=line.indexOf("\t");
					k=line.indexOf("\t",j+1);
					l=line.indexOf("\t",k+1);
					sub=line.substring(0,j);
					qc=Integer.parseInt(line.substring(j+1,k));
					comment=line.substring(k+1,l);
					}

					//if(!sub.equals(model.getValueAt(i,2).toString()))
					//{
					//	System.out.println("ERROR: qc.txt file does not match current Subjects directory ["+sub+" vs. "+model.getValueAt(i,2).toString()+"]");
						//images.image.printStatusMessage("ERROR: qc.txt file does not match current Subjects directory");
					//	return;
					//}
					//model.setValueAt(qc,i,1);
					//model.setValueAt(comment,i,3);
				}
				input.close();
			}
			catch (IOException e){}
    	}

    	images.image.printStatusMessage(model.getRowCount()+" subjects read.");
    	images2.image.printStatusMessage(model.getRowCount()+" subjects read.");
	}
	public void setQC(String subject,int QCValue, String msg)
	{
		int	i;
		for(i=0;i<model.getRowCount();i++)
			if(model.getValueAt(i,2).toString().equals(subject))
			{
				model.setValueAt(0,i,1);
				model.setValueAt(msg,i,3);
				break;
			}
	}
	public static void saveQC()
	{
		// Save QC
		final JFileChooser fc=new JFileChooser();
		fc.setDialogTitle("Save QC File...");
		int returnVal=fc.showSaveDialog(null);
		if(returnVal!=JFileChooser.APPROVE_OPTION)
			return;
			
		File	file = fc.getSelectedFile();
		file_name = file.getPath();
	    try
	    {
	    	int		i,j;
	    	double	x[]=new double[16];
	    	Writer	output = new BufferedWriter(new FileWriter(file));
	    	String	sub;
	    	
	    	output.write("Subject\tQC\tComments\tIHI_L\tIHI_R\tHseg_L\tHseg_R\t");//\tLseg_q\tRseg_t\tRseg_c\tRseg_q\tLHseg_moy\tRHseg_moy\t"); //ecrit la premiére ligne du fichier (n'enregistre pas les colonnes correspondantes)
	    	for(j=0;j<16;j++)
	    		if(j<15)
	    			output.write(graphs.regions[j]+"\t");
	    		else
	    			output.write(graphs.regions[j]+"\n");
	    		
			for(i=0;i<model.getRowCount();i++)
			{
				//System.out.println("sujet "+i+": ");
				//System.out.println(model.getValueAt(i,2).toString());
				sub=model.getValueAt(i,2).toString();
				output.write(sub+"\t");									// Subject
				output.write(model.getValueAt(i,1).toString()+"\t");	// QC
				if(model.getValueAt(i,3).toString().equals("Segmentation results unavailable"))
					output.write(" ");
				else
					output.write(model.getValueAt(i,3).toString()+"\t");	// Comments
				//System.out.println(" blabla "+model.getValueAt(i,4));
				output.write(model.getValueAt(i,4).toString()+"\t");
				output.write(model.getValueAt(i,5).toString()+"\t");
				output.write(model.getValueAt(i,6).toString()+"\t");
				output.write(model.getValueAt(i,7).toString()+"\t");
				output.write(model.getValueAt(i,8).toString()+"\t");
				output.write(model.getValueAt(i,9).toString()+"\t");

				graphs.getVolumesForSubject(sub,x);						// Volumes
				for(j=0;j<16;j++)
					if(j<15){
						//System.out.println(x[j]);
						//System.out.println("selectedSubjectVolumes[j] "+QCApp_IMAGEN.graphs.selectedSubjectVolumes[j]);
						output.write(x[j]+"\t");
					}
					else
						output.write(x[j]+"\n");
			}
			output.close();
			System.out.println(" Fichier enregistré");
		}
		catch (IOException e){}
	}
	
	
	public static void loadQC()
	{
		// select Subjects directory
		final JFileChooser fc=new JFileChooser();
		fc.setDialogTitle("load qc_date.txt...");
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal=fc.showOpenDialog(null);
		if(returnVal!=JFileChooser.APPROVE_OPTION)
			return;

    	// configure stats graphs
    	graphs.configure(subjectsDir);
    	
    	// if there is a qc.txt file inside subjectsDir, load it.
    	File			f=fc.getSelectedFile();//new File(subjectsDir+"/qc.txt");
    	if(f.exists())
    	{
    		
    		file_name=f.getPath();
    		System.out.println(fc.getSelectedFile()+"  file present, read it.");
		BufferedReader	input;
    		StringTokenizer	st;
    		int 			i,j,k,l,m,m1,m2,m3,m4,m5,m6,m7;
    		int				qc;
    		String			sub;
    		String			comment,IHI_L,IHI_R,Hseg_L,Hseg_R, C5_L,C5_R; //LHseg_q,RHseg_t,RHseg_c,RHseg_q;
    		String			line;
			try
			{
				
				input=new BufferedReader(new FileReader(f));
				input.readLine(); // skip header row
				
i=0;
					line=input.readLine();
					j=line.indexOf("\t");
					k=line.indexOf("\t",j+1);
					l=line.indexOf("\t",k+1);
					m=line.indexOf("\t",l+1);
					m1=line.indexOf("\t",m+1);
					m2=line.indexOf("\t",m1+1);
					m3=line.indexOf("\t",m2+1);
					m4=line.indexOf("\t",m3+1);
					m5=line.indexOf("\t",m4+1);
					m6=line.indexOf("\t",m5+1);
					m7=line.indexOf("\t",m6+1);
					
					sub=line.substring(0,j);
					qc=Integer.parseInt(line.substring(j+1,k));
					comment=line.substring(k+1,l);
					IHI_L=line.substring(l+1,m);
					IHI_R=line.substring(m+1,m1);
					Hseg_L=line.substring(m1+1,m2);
					Hseg_R=line.substring(m2+1,m3);
					C5_L = line.substring(m3+1,m4);
					C5_R = line.substring(m4+1,m5);
					

				for(i=0;i<model.getRowCount();i++)
				{
					//System.out.println("blaaaaaaaaaaa "+sub+"  =?= "+model.getValueAt(i,2).toString());
					if(sub.equals(model.getValueAt(i,2).toString()))
					{
						
						//images.image.printStatusMessage("ERROR: qc.txt file does not match current Subjects directory");
						//return;
						
					model.setValueAt(qc,i,1);
					model.setValueAt(comment,i,3);
					model.setValueAt(IHI_L,i,4);
					model.setValueAt(IHI_R,i,5);
					model.setValueAt(Hseg_L,i,6);
					model.setValueAt(Hseg_R,i,7);
					model.setValueAt(C5_L,i,8);
					model.setValueAt(C5_R,i,9);
					}
					else i--;
					
					line=input.readLine();
					if (line==null){
						System.out.println("ligne vide.");
					}
					else{
					j=line.indexOf("\t"); // premiere tabulation
					k=line.indexOf("\t",j+1);
					l=line.indexOf("\t",k+1);
					m=line.indexOf("\t",l+1);
					m1=line.indexOf("\t",m+1);
					m2=line.indexOf("\t",m1+1);
					m3=line.indexOf("\t",m2+1);
					m4=line.indexOf("\t",m3+1);
					m5=line.indexOf("\t",m4+1);
					m6=line.indexOf("\t",m5+1);
					m7=line.indexOf("\t",m6+1);
					sub=line.substring(0,j);
					qc=Integer.parseInt(line.substring(j+1,k));
					comment=line.substring(k+1,l);
					IHI_L=line.substring(l+1,m);
					IHI_R=line.substring(m+1,m1);
					Hseg_L=line.substring(m1+1,m2);
				//	LHseg_c=line.substring(m2+1,m3);
					//LHseg_q=line.substring(m3+1,m4);
					Hseg_R=line.substring(m2+1,m3);
					C5_L = line.substring(m3+1,m4);
					C5_R = line.substring(m4+1,m5);
					//RHseg_c=line.substring(m5+1,m6);
					//RHseg_q=line.substring(m6+1,m7);
					}

					//if(!sub.equals(model.getValueAt(i,2).toString()))
					//{
					//	System.out.println("ERROR: qc.txt file does not match current Subjects directory ["+sub+" vs. "+model.getValueAt(i,2).toString()+"]");
						//images.image.printStatusMessage("ERROR: qc.txt file does not match current Subjects directory");
					//	return;
					//}
					//model.setValueAt(qc,i,1);
					//model.setValueAt(comment,i,3);
				}
				input.close();
			}
			catch (IOException e){}
    	}
    	
    	images.image.printStatusMessage(model.getRowCount()+" subjects read.");
    	images2.image.printStatusMessage(model.getRowCount()+" subjects read.");
	}
	
	public static void statistiques()
	{
		f2 = new JFrame("Statistiques : ");
		f2.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		f2.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.out.println("fenêtre statistique fermée");}});
		f2.setVisible(true);
		f2.setBounds(200, 200, 500, 500);
		String texte;//="Pas d'informations disponible";
		
		 JLabel  texte_stat = new JLabel("texte",SwingConstants.CENTER);
		 texte_stat.setPreferredSize(new Dimension(250,25));//On lui donne une taille
		 
		  f2.getContentPane().add(texte_stat);
		  Border line = BorderFactory.createLineBorder(Color.red);
		  Border cadre = BorderFactory.createTitledBorder(line,file_name );
		  
		  texte_stat.setBorder(cadre);
		int try_i=1;
		String ligne = null;
	    try
	    {
	    	InputStream ips=new FileInputStream(file_name); 

			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			 ligne = br.readLine();
			//System.out.println("String ligne = br.readLine(); : "+ligne);
			ligne = br.readLine();
			int i,j,k,l,m,m1,m2,m3,m4,m5,m6,m7;
			String ihi_l, ihi_r;
			int yes_g=0;
			int no_g=0;
			int yes_bilateral=0;
			double c1_g,c2_g,c3_g,c4_g;
			double seg_g_00, seg_g_1, seg_g_15, seg_g_2, seg_g_25, seg_g_3,seg_g_35,seg_g_4;
			seg_g_00= seg_g_1=seg_g_15=seg_g_2=seg_g_25=seg_g_3=seg_g_35=seg_g_4=0;
			c1_g=c2_g=c3_g=c4_g=0;
			int yes_d=0;
			int no_d=0;
			double c1_d,c2_d,c3_d,c4_d;
			double seg_d_00, seg_d_1, seg_d_15, seg_d_2, seg_d_25, seg_d_3,seg_d_35,seg_d_4;
			seg_d_00=seg_d_1=seg_d_15=seg_d_2=seg_d_25=seg_d_3=seg_d_35=seg_d_4=0;
			c1_d=c2_d=c3_d=c4_d=0;
			double c1_no_g,c2_no_g,c3_no_g,c4_no_g;
			c1_no_g=c2_no_g=c3_no_g=c4_no_g=0;
			double c1_no_d,c2_no_d,c3_no_d,c4_no_d;
			c1_no_d=c2_no_d=c3_no_d=c4_no_d=0;
			j=ligne.indexOf("\t");
			k=ligne.indexOf("\t",j+1);
			l=ligne.indexOf("\t",k+1);// entre l et m: IHI_L
			m=ligne.indexOf("\t",l+1); // IHI_R
			m1=ligne.indexOf("\t",m+1);
			m2=ligne.indexOf("\t",m1+1);
			//System.out.println("ligne.substring(m+1, m1) : "+ligne.substring(m+1, m1));
			//System.out.println("ligne.substring(l+1,m) : "+ligne.substring(l+1,m));
			while(!ligne.substring(m+1, m1).equals(" ")){
				System.out.println(try_i+"ligne: "+ ligne);

			int ind_c1_l,ind_c2_l,ind_c3_l,ind_c4_l;
			int ind_seg_l, ind_seg_r;
			int ind_c1_r,ind_c2_r,ind_c3_r,ind_c4_r;
			


				
				ihi_l = ligne.substring(l+1,m);
				ihi_r = ligne.substring(m+1,m1);


				ind_c1_l=ihi_l.indexOf(","); // c1 se trouve donc avant cet index
				ind_c2_l=ihi_l.indexOf(",",ind_c1_l+1);
				ind_c3_l=ihi_l.indexOf(",",ind_c2_l+1);
				ind_c4_l=ihi_l.indexOf("]",ind_c3_l+1);
				

				ind_c1_r=ihi_r.indexOf(","); // c1 se trouve donc avant cet index
				ind_c2_r=ihi_r.indexOf(",",ind_c1_r+1);
				ind_c3_r=ihi_r.indexOf(",",ind_c2_r+1);
				ind_c4_r=ihi_r.indexOf("]",ind_c3_r+1);
				
				ind_seg_l=m1+ligne.substring(m1,m1+11).indexOf("\t",m1+1); // la tabulation entre seg_g et seg_d
				ind_seg_r=m1+ligne.substring(m1,m1+11).indexOf("\t",ind_seg_l+1); // la tabulation aprés seg_d

				///// GAUCHE ____ LEFT ///////
				
				if((ihi_r.substring(1,2).equals("Y"))&&(ihi_l.substring(1, 2).equals("Y"))){
					yes_bilateral=yes_bilateral+1;

				}
				if(ihi_l.substring(1,2).equals("Y")){
						
					yes_g=yes_g+1;
					c1_g=c1_g+Double.valueOf((ihi_l.substring(3,ind_c1_l).trim())).doubleValue();
					c2_g=c2_g+Double.valueOf((ihi_l.substring(ind_c1_l+1,ind_c2_l).trim())).doubleValue();
					c3_g=c3_g+Double.valueOf((ihi_l.substring(ind_c2_l+1,ind_c3_l).trim())).doubleValue();
					c4_g=c4_g+Double.valueOf((ihi_l.substring(ind_c3_l+1,ind_c4_l).trim())).doubleValue();
					
				}
				else if(ihi_l.substring(1, 2).equals("N")){
					no_g=no_g+1;
					c1_no_g=c1_no_g+Double.valueOf((ihi_l.substring(3,ind_c1_l).trim())).doubleValue();
					c2_no_g=c2_no_g+Double.valueOf((ihi_l.substring(ind_c1_l+1,ind_c2_l).trim())).doubleValue();
					c3_no_g=c3_no_g+Double.valueOf((ihi_l.substring(ind_c2_l+1,ind_c3_l).trim())).doubleValue();
					c4_no_g=c4_no_g+Double.valueOf((ihi_l.substring(ind_c3_l+1,ind_c4_l).trim())).doubleValue();
				}
				System.out.println(m1+" "+ind_seg_l+" "+ ind_seg_r);
				System.out.println(ligne);
				System.out.println(ligne.substring(m1,m1+11));
				System.out.println(ligne.substring(m1,m1+11).substring(m1,ind_seg_l));
				
				 double aa= Double.valueOf(ligne.substring(m1,m1+11).substring(m1+2,ind_seg_l).trim()).doubleValue();
				 System.out.println(aa);
				if(aa==0)seg_g_00=seg_g_00+1;
				if(aa== 1) seg_g_1=seg_g_1+1;
				if(aa== 1.5)seg_g_15=seg_g_15+1;
				if(aa== 2)seg_g_2=seg_g_2+1;
				if(aa== 2.5)seg_g_25=seg_g_25+1;
				if(aa== 3)seg_g_3=seg_g_3+1;
				if(aa== 3.5)seg_g_35=seg_g_35+1;
				if(aa== 4)seg_g_4=seg_g_4+1;
				
				
				//// DROITE ____ RIGHT ////

				if(ihi_r.substring(1,2).equals("Y")){
					yes_d=yes_d+1;
					c1_d=c1_d+Double.valueOf((ihi_r.substring(3,ind_c1_r).trim())).doubleValue();
					c2_d=c2_d+Double.valueOf((ihi_r.substring(ind_c1_r+1,ind_c2_r).trim())).doubleValue();
					c3_d=c3_d+Double.valueOf((ihi_r.substring(ind_c2_r+1,ind_c3_r).trim())).doubleValue();
					c4_d=c4_d+Double.valueOf((ihi_r.substring(ind_c3_r+1,ind_c4_r).trim())).doubleValue();
					
				}
				else if(ihi_r.substring(1,2).equals("N")){
					
					no_d=no_d+1;
					c1_no_d=c1_no_d+Double.valueOf((ihi_r.substring(3,ind_c1_r).trim())).doubleValue();
					c2_no_d=c2_no_d+Double.valueOf((ihi_r.substring(ind_c1_r+1,ind_c2_r).trim())).doubleValue();
					c3_no_d=c3_no_d+Double.valueOf((ihi_r.substring(ind_c2_r+1,ind_c3_r).trim())).doubleValue();
					c4_no_d=c4_no_d+Double.valueOf((ihi_r.substring(ind_c3_r+1,ind_c4_r).trim())).doubleValue();
				}
				
				aa= Double.valueOf((ligne.substring(m1,m1+11).substring(ind_seg_l+1,ind_seg_r).trim())).doubleValue();
				if(aa==0)seg_d_00=seg_d_00+1;
				if(aa== 1)seg_d_1=seg_d_1+1;
				if(aa== 1.5)seg_d_15=seg_d_15+1;
				if(aa== 2)seg_d_2=seg_d_2+1;
				if(aa== 2.5)seg_d_25=seg_d_25+1;
				if(aa== 3)seg_d_3=seg_d_3+1;
				if(aa== 3.5)seg_d_35=seg_d_35+1;
				if(aa== 4)seg_d_4=seg_d_4+1;
				
	

				try_i=try_i+1;
				ligne = br.readLine();
				j=ligne.indexOf("\t");
				k=ligne.indexOf("\t",j+1);
				l=ligne.indexOf("\t",k+1);// entre l et m: IHI_L
				m=ligne.indexOf("\t",l+1); // IHI_R
				m1=ligne.indexOf("\t",m+1);
			} // fin while
			ligne = br.readLine();
			j=ligne.indexOf("\t");
			k=ligne.indexOf("\t",j+1);
			l=ligne.indexOf("\t",k+1);// entre l et m: IHI_L
			m=ligne.indexOf("\t",l+1); // IHI_R
			m1=ligne.indexOf("\t",m+1);
texte="";
double c1_y= (c1_d+c1_g)/(yes_d+yes_g);
double c2_y= (c2_d+c2_g)/(yes_d+yes_g);
double c3_y= (c3_d+c3_g)/(yes_d+yes_g);
double c4_y= (c4_d+c4_g)/(yes_d+yes_g);
double c1_n= (c1_no_d+c1_no_g)/(no_d+no_g);
double c2_n= (c2_no_d+c2_no_g)/(no_d+no_g);
double c3_n= (c3_no_d+c3_no_g)/(no_d+no_g);
double c4_n= (c4_no_d+c4_no_g)/(no_d+no_g);

			texte= texte+"<html> "+yes_g+", malrotations à GAUCHE parmis les "+(try_i-1)+" sujets notés, soit: <br>";
			texte= texte+"  "+(yes_g*100/(try_i-1))+" % des hippocampes gauches.<br>";
			texte= texte+"<br>";
			texte= texte+yes_d+" malrotations à DROITE, soit: <br>";
			texte= texte+"  "+(yes_d*100.0/(try_i-1))+" % des hippocampes droits. <br>";
			texte= texte+"<br>";
			texte= texte+" Il y a donc en tous "+(yes_d+yes_g)+" hippocampes malformés. Soit "+((yes_d+yes_g)*100)/((try_i-1)*2)+" % de l'ensemble des hippocampes <br>";
			texte= texte+"<br>";
			texte= texte+yes_bilateral+" sujets ont des malrotations bilatérales, soit "+((yes_bilateral*100)/(try_i-1))+"% des sujets <br>";
			texte= texte+"<br>";
			texte= texte+(2*try_i-(yes_g+no_g)-(yes_d+no_d)-2)+" ne sont pas classés <br>";
			texte= texte+"<br>";
			texte= texte+"<br>";	
			texte= texte+"Dans le cas des malrotations, note moyenne des critéres: <br>";
			texte= texte+" - critére 1= "+c1_y+"/2 <br>";
			texte= texte+" - critére 2= "+c2_y+"/2<br>";
			texte= texte+" - critére 3= "+c3_y+"/2<br>";
			texte= texte+" - critére 4= "+c4_y+"/2<br>";
			texte= texte+"<br>";			
			texte= texte+"<br>";	
			texte= texte+"Dans le cas des hippocampes bien formés, note moyenne des critéres: <br>";
			texte= texte+" - critére 1= "+c1_n+"/2<br>";
			texte= texte+" - critére 2= "+c2_n+"/2<br>";
			texte= texte+" - critére 3= "+c3_n+"/2<br>";
			texte= texte+" - critére 4= "+c4_n+"/2<br>";
			texte= texte+"<br>";			
			texte= texte+"<br>";
			texte= texte+"<br>";			
			texte= texte+"<br>";
			texte= texte+"<br>";			
			texte= texte+"<br>";
			texte= texte+"Notes de segmentations pour les hippocampes gauches: <br>";
			texte= texte+" - Note 0= "+seg_d_00+"<br>";
			texte= texte+" - Note 1= "+seg_d_1+"<br>";
			texte= texte+" - Note 1.5= "+seg_d_15+"<br>";
			texte= texte+" - Note 2= "+seg_d_2+"<br>";
			texte= texte+" - Note 2.5= "+seg_d_25+"<br>";
			texte= texte+" - Note 3= "+seg_d_3+"<br>";
			texte= texte+" - Note 3.5= "+seg_d_35+"<br>";
			texte= texte+" - Note 4= "+seg_d_4+"<br>";
			texte= texte+"<br>";
			texte= texte+"Notes de segmentations pour les hippocampes droits: <br>";
			texte= texte+" - Note 0= "+seg_g_00+"<br>";
			texte= texte+" - Note 1= "+seg_g_1+"<br>";
			texte= texte+" - Note 1.5= "+seg_g_15+"<br>";
			texte= texte+" - Note 2= "+seg_g_2+"<br>";
			texte= texte+" - Note 2.5= "+seg_g_25+"<br>";
			texte= texte+" - Note 3= "+seg_g_3+"<br>";
			texte= texte+" - Note 3.5= "+seg_g_35+"<br>";
			texte= texte+" - Note 4= "+seg_g_4+"<br>";
			
			texte= texte+"<br></html>";	// derniére ligne
			
			texte_stat.setText(texte); 
		}
		catch (IOException e){System.out.println(e.getMessage()); System.out.println(try_i+"ligne: "+ ligne);}
	}
	
	
	
	public static void createAndShowGUI()
	{
		f = new JFrame("QCApp_IMAGEN");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});

		// Status text
		status=new JTextArea("Choose a Subjects Directory");
		status.setOpaque(false);
		
		// Choose Button
		chooseButton=new JButton("Choose Subjects Directory...");
		chooseButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){try {
			chooseDirectory();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}}});
		
		// Save Button
		saveButton=new JButton("Save QC...");
		saveButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){saveQC();}});
		
		// Load Button
		loadButton=new JButton("Load QC...");
		loadButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){loadQC();}});
		
		statButton=new JButton(" Statistiques ");
		statButton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){statistiques();}});
		
		

		
		// Table
		model=new DefaultTableModel(); // la javax.swing.table.DefaultTableModel modélise l'ensemble des données contenues par la table. 
		table=new JTable(model); // on met dans l'objet graphique de type JTable la table des données. 
		model.addColumn("#");
		model.addColumn("QC");
		model.addColumn("Subject");
		model.addColumn("Comments");
		model.addColumn("IHI_L");
		model.addColumn("IHI_R");
		model.addColumn("Hseg_L");
		model.addColumn("Hseg_R");
		model.addColumn("C5_L");
		model.addColumn("C5_R");		
		table.setPreferredScrollableViewportSize(new Dimension(250,90)); // représente la partie visible du sous-composant contenu. On fixe donc ici la taille de la partie visible de la table, qui sera au départ toute la table.
		table.setFillsViewportHeight(true);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){public void valueChanged(ListSelectionEvent e){selectSubject(table);}});
		JScrollPane scrollPane=new JScrollPane(table); // conteneur permettant de minur un composant de barres de defilement.
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.getColumnModel().getColumn(0).setMinWidth(30);//table.(Returns the TableColumnModel that contains all column information of this table).(Returns the TableColumn object for the column at columnIndex).
		table.getColumnModel().getColumn(0).setMaxWidth(52);
		table.getColumnModel().getColumn(1).setMaxWidth(32);//
		table.getColumnModel().getColumn(1).setMinWidth(12);
		table.getColumnModel().getColumn(2).setMinWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(150);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);
		table.getColumnModel().getColumn(4).setMinWidth(40);
		table.getColumnModel().getColumn(4).setPreferredWidth(100);
		table.getColumnModel().getColumn(5).setMinWidth(40);
		table.getColumnModel().getColumn(5).setPreferredWidth(100);

		// Graphs
		graphs=new MyGraphs();
		graphs.setPreferredSize(new Dimension(250,250));
		graphs.myQCApp_IMAGEN=me;

		// Image
		images=new MyImages();
		images.setPreferredSize(new Dimension(800,512));
		images.addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){
			System.out.println("Mouse clicked at: "+e.getPoint()+" scale: "+images.scale);
			int xx=panel2.getLocation().x+e.getPoint().x+f.getLocation().x;
			System.out.println("Mouse clicked at xx: "+xx+" e.getLocationOnScreen().x : "+e.getLocationOnScreen().x);

			mouseDownOnImage(e);
			}});
		images.addMouseMotionListener(new MouseAdapter(){public void mouseDragged(MouseEvent e){
			images.selectedSlice=e.getPoint().y/(double)images.getSize().height;
			images.repaint();
			//mouseDraggedOnImage(e);
			}});
		images.addMouseWheelListener(new MouseWheelListener() {public void mouseWheelMoved(MouseWheelEvent e){
			images.x_souris=e.getX();
			images.y_souris=e.getY();
			images.scale_changed=true;
			if (e.getWheelRotation()<0){ // Rotated Up	
			images.scale=(images.scale*3)/2;
			/*System.out.println("scale: "+images.scale);
			BufferedImage imgScal = images.image.img[images.selectedImage];
			Graphics2D grph = imgScal.createGraphics();
			System.out.println("widht: "+imgScal.getWidth());
			
			grph.scale(images.scale,images.scale);
			grph.dispose();*/
			//imgScal.getScaledInstance(imgScal.getWidth(),imgScal.getHeight(),BufferedImage.SCALE_FAST);
			}
			else {images.scale=(images.scale*2)/3; } 
			images.repaint();
			}
		});
		
		images.myQCApp_IMAGEN=me;
		images.image.myQCApp_IMAGEN=me;
		imagesScrollPane=new JScrollPane(images);

			slider1 = new JSlider(JSlider.VERTICAL,0,images.getSize().height,(int)(images.getSize().height/2));

		
		slider1.setPreferredSize(new Dimension(30, 300));
		slider1.setMajorTickSpacing(1);
		slider1.setPaintTicks(true);
		slider1.setBounds(10,10, 5, 500);
		slider1.setLayout(new BorderLayout());
		slider1.addChangeListener(new ChangeListener(){
			 public void stateChanged(ChangeEvent evt) {
			  		images.selectedSlice=1-slider1.getValue()/(double)slider1.getMaximum();
					images.repaint();
			      }});
		panel1 = new JPanel(new BorderLayout());
		panel1.add(imagesScrollPane,BorderLayout.CENTER);
		panel1.add(slider1,BorderLayout.WEST);
		slider1.revalidate();   		
		

		images2=new MyImages();
		images2.setPreferredSize(new Dimension(800,512));
		images2.addMouseListener(new MouseAdapter(){public void mouseClicked(MouseEvent e){
			System.out.println("Mouse clicked at: "+e.getPoint()+" scale: "+images.scale);
			int xx=panel2.getLocation().x+e.getPoint().x+f.getLocation().x;
			System.out.println("Mouse clicked at xx: "+xx+" e.getLocationOnScreen().x : "+e.getLocationOnScreen().x);
			mouseDownOnImage(e);
			}});
		images2.addMouseMotionListener(new MouseAdapter(){public void mouseDragged(MouseEvent e){
		images2.selectedSlice=e.getPoint().y/(double)images2.getSize().height;
		images2.repaint();
			//mouseDraggedOnImage(e);
			}});
		
		images2.addMouseWheelListener(new MouseWheelListener() {public void mouseWheelMoved(MouseWheelEvent e){
		images2.x_souris=e.getX();
		images2.y_souris=e.getY();
		images2.scale_changed=true;
			if (e.getWheelRotation()<0){ // Rotated Up	
			images2.scale=(images2.scale*3)/2;
			}
			else {images2.scale=(images2.scale*2)/3; } 
			images2.repaint();
			}
		});
		
		images2.myQCApp_IMAGEN=me;
		images2.image.myQCApp_IMAGEN=me;
		images2ScrollPane=new JScrollPane(images2);
		slider2 = new JSlider(JSlider.VERTICAL,0,images2.getSize().height,(int)(images2.getSize().height/2));
		slider2.setPreferredSize(new Dimension(30, 300));
		slider2.setMajorTickSpacing(1);
		slider2.setPaintTicks(true);
		slider2.setBounds(10,10, 5, 500);
		slider2.setLayout(new BorderLayout());
		slider2.addChangeListener(new ChangeListener(){
			 public void stateChanged(ChangeEvent evt) {
			  		images2.selectedSlice=1-slider2.getValue()/(double)slider2.getMaximum();
					images2.repaint();
			      }});
		panel2 = new JPanel(new BorderLayout());
		panel2.add(images2ScrollPane,BorderLayout.CENTER);
		panel2.add(slider2,BorderLayout.WEST);
		slider2.revalidate();

		JSplitPane splitPaneForImages=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel1,panel2);
		splitPaneForImages.setOneTouchExpandable(true);
		splitPaneForImages.setResizeWeight(0.4);

		// Split Pane for Table and Graphs
		JSplitPane splitPaneForTableAndGraphs=new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollPane,graphs);
		splitPaneForTableAndGraphs.setOneTouchExpandable(true);
		splitPaneForTableAndGraphs.setDividerLocation(350);
		splitPaneForTableAndGraphs.setResizeWeight(1.0); // Affecte le taux de redistribution de l'espace supplémentaire lorsque la taille du JSplitPane change. d=0 : tout va à droite, d=1 tout va à gauche.

		// Split Pane for the previous and Images
		JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,splitPaneForTableAndGraphs,splitPaneForImages);//imagesScrollPane); // Crée un JSplitPane orienté suivant orientation et à affichage non continu. Les deux composants sont cGauche et cDroit.
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(350); // Affecte la position (en valeur absolue) de la barre de division.
		
		// Layout the GUI
	     layout = new GroupLayout(f.getContentPane()); // GroupLayout est un LayoutManager crée tout spécialement pour faciliter la tâche de construction des interface utilisateurs.
        f.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true); // Sets whether a gap between components should automatically be created
        layout.setAutoCreateContainerGaps(true); // Sets whether a gap between the container and components that touch the border of the container should automatically be created.
        layout.setHorizontalGroup
        (	layout.createParallelGroup()
        	.addGroup
        	(	layout.createSequentialGroup()
        		.addComponent(chooseButton)
        		.addComponent(saveButton)
        		.addComponent(loadButton)
        		.addComponent(statButton)
        	)
        	.addComponent(splitPane)
        	.addComponent(status)
        );
        layout.setVerticalGroup
        (	layout.createSequentialGroup()
        	.addGroup
        	(	layout.createParallelGroup() //BASELINE
				.addComponent(chooseButton)
				.addComponent(saveButton)
				.addComponent(loadButton)
				.addComponent(statButton)

        	)
        	.addComponent(splitPane)
	        .addComponent(status,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
        );
		f.pack();
		f.setVisible(true);
    }
 /*   public static void main(String[] args)
    {
	System.out.println("/lena16/dartagnan/IMAGEN/DataBaseIMAGEN/IMAGEN/");
	System.out.println("/lena16/dartagnan/IMAGEN/DataBaseIMAGEN_test2/IMAGEN/");
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
    		* /
    		me=new QCApp_IMAGEN();
    		me.createAndShowGUI();
    	}
    } */
}