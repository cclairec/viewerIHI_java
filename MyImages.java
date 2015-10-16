import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;


class MyImages extends JComponent
{           
	QCApp_IMAGEN		myQCApp_IMAGEN;
	MyImage		image;
	Rectangle	rect[];
	int			selectedImage;
	double		selectedSlice;
	MyVolume	vol,vol2, volL, volR;
	MyVolume	volBack,volBackL,volBackR;
	String		volName;
	String		subName;
	double		scale;
	boolean 	scale_changed;
	int x_souris;
	int y_souris;

    public void paint(Graphics g)
    {
    	Dimension dim=this.getSize();
    	g.setColor(new Color(20,10,50));
    	g.fillRect(0,0,dim.width,dim.height);
		if(image.initialized==0)
			return;
		
		if(rect==null)
			rect=new Rectangle[image.img.length];
		
		if(selectedImage==0)
		{
			// All images view
			int		i;
			int		xoff=0,yoff=0,maxHeight;
			int		x0,y0,x1,y1;
			double	z=1.3,k1,k2;
			
			maxHeight=0;
			for(i=0;i<image.img.length;i++)
			{
				if(xoff+z*image.img[i].getWidth()>=this.getParent().getSize().width)
				{
					xoff=0;
					yoff+=maxHeight+48;
					maxHeight=0;
				}
				
				if(image.img[i].getHeight()>100){
					k2=100;
					k1=(k2/image.img[i].getHeight())*image.img[i].getWidth();
				}	
				else {
					k2=image.img[i].getWidth();
					k1=image.img[i].getWidth();
				}
				
				if(image.img[i].getHeight()<90){ 
					k2=90;
					k1=(k2/image.img[i].getHeight())*image.img[i].getWidth();
				}
				else {
					k2=image.img[i].getWidth();
					k1=image.img[i].getWidth();
				}
				
				
				Dimension d=new Dimension(this.getParent().getSize().width,yoff+maxHeight);
				//System.out.println("image.img["+i+"].getWidth(): "+ image.img[i].getWidth());

				//g.drawImage(image.img[i],xoff,yoff,(int)(z*image.img[i].getWidth()),(int)(z*image.img[i].getHeight()),null);
				g.drawImage(image.img[i],xoff,yoff,(int)(z*k1),(int)(z*k2),null);
				//rect[i]=new Rectangle(xoff,yoff,(int)(z*image.img[i].getWidth()),(int)(z*image.img[i].getHeight()));
				rect[i]=new Rectangle(xoff,yoff,(int)(z*k1),(int)(z*k2));
				//xoff+=(int)(z*image.img[i].getWidth());
				xoff+=(int)(z*k1+48);
				// pour afficher du texte sur l'imagette. Ajouté par moi

				g.setColor(Color.white);
				g.drawRoundRect((int)(xoff-k1/2-58),yoff,image.imgListName[i].length()*9+2 ,20,15,15);
				//String	texte=" "+image.getVolumeName(image.imgListName[i]);
				FontMetrics fm = this.getFontMetrics(this.getFont());
				int width = fm.stringWidth(image.imgListName[i]);
				int height = fm.getHeight();
				//g.drawString(texte,d.width-10-48 +(48-k1)/2,10+20-(20-height)/2-3);
				g.drawString(image.imgListName[i],(int)(xoff-k1/2-40),yoff+15);
				//g.setXORMode(Color.white); // Si 2 surfaces de couleur différentes se superposent, alors la dernière dessinée recouvre la précédente sauf si on invoque la méthode setXORMode(). Dans ce cas, la couleur de l'intersection prend une autre couleur. L'argument à fournir est une couleur alternative. La couleur d'intersection représente une combinaison de la couleur originale et de la couleur alternative.
				// fin de l'ajout
				if(z*image.img[i].getHeight()>maxHeight)
					maxHeight=(int)(z*image.img[i].getHeight());
			}
			
			// adjust image size for scroll
			Dimension d=new Dimension(this.getParent().getSize().width,yoff+maxHeight);
			if(!d.equals(this.getParent().getSize()))
			{
				this.setPreferredSize(d);
				this.revalidate();
			}
		}
		else
		{
			// Single volume view			
			int				i=selectedImage-1;
			BufferedImage	img;
			String			tmp;
			String			filename;

			// load volume
			tmp=image.getVolumeName(image.imgList[i]);
		if(volName==null || !volName.equals(tmp) || !subName.equals(image.subject))
		{
				volName=tmp;
				subName=image.subject;
				image.printStatusMessage("Loading volume \""+volName+"\"...");
				filename=image.subject+"/QC/vol/"+volName+".nii.gz";
				
				vol=new MyVolume(filename);
				if(vol.volume==null)
					vol=new MyVolume(image.subject+"/QC/vol/"+volName+".nii");
				if(vol.volume==null && volName.equals("_nobiais")){
					vol=new MyVolume(image.subject+"/Process_FSL/001r.nii.gz");
					if(vol.volume==null) vol=new MyVolume(image.subject+"/Process_FSL/001r.nii");
				}
				image.printStatusMessage("Volume: "+filename);
				if(volName.equals("a")) // Si le volume courrant est la t1 biais corrected, elle sert aussi en image de fond.
					volBack=vol;
			}
			String		volPlane=image.getPlaneName(image.imgList[i]);
			int			cmapindex;
			int			plane;
			plane=1;
			if(volPlane.equals("X")) plane=1;
			if(volPlane.equals("Y")) plane=2;
			if(volPlane.equals("Z")) plane=3;
			cmapindex=1;
			if(vol.volume==null){ // si y'a pas de volume, on affiche que le bouton "BACK".
				System.out.println("Le volume demandé n'existe pas.");
				img=image.drawErrorSlice();
			}
		else{
			if(volName.equals("_a_lha") ) // si on veut regarder les segmentations.
			{
				if(volBack==null){
					volBack=new MyVolume(image.subject+"/QC/vol/_nobiais.nii.gz"); // arriére plan pour superposition: nobiais
					if(volBack.volume==null)
						volBack=new MyVolume(image.subject+"/QC/vol/_nobiais.nii");
				}
				cmapindex=2;
				vol2=new MyVolume(image.subject+"/QC/vol/_a_rha.nii.gz");
				if(vol2.volume==null)
					vol2=new MyVolume(image.subject+"/QC/vol/_a_rha.nii");
				
				img=image.drawSlice(vol,vol2,volBack,selectedSlice,plane,cmapindex,scale);
			}
			//else
			//	{img=image.drawSlice(vol,selectedSlice,plane,cmapindex);}
				
			else{ if(volName.equals("_a_rha_seg") ) // si on veut regarder les segmentations.
			{
				volBackL=new MyVolume(image.subject+"/QC/vol/_lha_zone.nii.gz");	
				if(volBackL.volume==null)		
					volBackL=new MyVolume(image.subject+"/QC/vol/_lha_zone.nii");
					
					cmapindex=2;
					volL=new MyVolume(image.subject+"/QC/vol/_a_lha_seg.nii.gz");
					if(volL.volume==null)
						volL=new MyVolume(image.subject+"/QC/vol/_a_lha_seg.nii");
					
					volBackR=new MyVolume(image.subject+"/QC/vol/_rha_zone.nii.gz");	
					if(volBackR.volume==null)		
						volBackR=new MyVolume(image.subject+"/QC/vol/_rha_zone.nii");
					
				/*	if(volBackR==null){
						volBackR=new MyVolume(image.subject+"/QC/vol/_rha_zone.nii.gz"); // arriére plan: boite de segmentation						
						if(volBackR.volume==null)
						volBackR=new MyVolume(image.subject+"/QC/vol/_rha_zone.nii");
					} */
					cmapindex=2;
					volR=new MyVolume(image.subject+"/QC/vol/_a_rha_seg.nii.gz");
					if(volR.volume==null)
						volR=new MyVolume(image.subject+"/QC/vol/_a_rha_seg.nii");
					
					
					
					img=image.drawSliceForSeg(volL,volR,volBackL,volBackR ,selectedSlice,plane,cmapindex,scale);
				} 
				
			else
				img=image.drawSlice(vol,selectedSlice,plane,cmapindex);
			}
				
			
			if ((int)(img.getWidth()*scale)>0 && (int)(img.getHeight()*scale)>0 && scale <100 && (scale_changed || scale!=1)){

				Graphics2D grph = (Graphics2D) g;

				AffineTransform t = new AffineTransform();

				double currentImgWidth = img.getWidth()*scale, currentImgHeight = img.getHeight()*scale;
				//System.out.println("translation: "+ (img.getWidth()/2-currentImgWidth/2-x_souris)+", "+ (img.getHeight()/2-currentImgHeight/2-y_souris));
			//	System.out.println("avec: "+img.getWidth()+" et "+x_souris+" et "+y_souris);
				t.translate(img.getWidth()/2-currentImgWidth/2+x_souris/2, img.getHeight()/2-currentImgHeight/2+y_souris/2);
				
				t.scale(scale, scale);

				grph.drawImage(img, t, null);

				grph.dispose();
				
				//repaint();
				scale_changed=false;
			}
			

			}
			
			// adjust image size for scroll
			Dimension	d=this.getParent().getSize();
			if(!d.equals(this.getSize()))
			{
				this.setPreferredSize(d);
				this.revalidate();
				return;
			}

			// draw image
			double		scale=this.getSize().height/(double)img.getHeight();
			int			xoff,yoff;
			
			xoff=(int)((this.getSize().width-img.getWidth())/2.0);
			yoff=0;
			g.drawImage(img,xoff,yoff,(int)(img.getWidth()),(int)(img.getHeight()),null);
			g.setColor(Color.white);
			g.drawRoundRect(1,1,48,20,15,15);
			String	back="BACK";
			FontMetrics fm = this.getFontMetrics(this.getFont());
			int width = fm.stringWidth(back);
			int height = fm.getHeight();
			g.drawString("BACK",1 +(48-width)/2,1+20-(20-height)/2-3);
			
		}
		
    }
    public MyImages()
    {
    	selectedImage=0;
    	selectedSlice=0.5;
    	vol=null;
    	volBack=null;
    	vol2=null;
    	volL=null;
    	volR=null;
    	volBackL=null;
    	volBackR=null;
    	
    	volName=null;
    	subName=null;
    	scale = 1;
    	scale_changed=false;
    	//myQCApp_IMAGEN=this.getParent();
    	image=new MyImage();
    	//image.myQCApp_IMAGEN=myQCApp_IMAGEN;
    }
}
