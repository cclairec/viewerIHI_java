import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;


class MyImage
{           
	int				initialized;
	QCApp_IMAGEN			myQCApp_IMAGEN;
	String			subject;
	String			imgList[];
	String			imgListName[];
	BufferedImage	img[];										// bitmap image
	int				cmap[]=new int[59*3];						// colour map for FIRST segmented image
	float			X[]={0,1,0,0, 0,0,1,0, 1,0,0,0, 0,0,0,1};	// X-plane transformation matrix
	float			Y[]={1,0,0,0, 0,0,1,0, 0,1,0,0, 0,0,0,1};	// Y-plane transformation matrix
	float			Z[]={1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1};	// Z-plane transformation matrix

	
	public void multMatVec(float rV[], float M[], float V[])
	{
		rV[0]=M[0]*V[0]+M[1]*V[1]+M[2]*V[2]+M[3];
		rV[1]=M[4]*V[0]+M[5]*V[1]+M[6]*V[2]+M[7];
		rV[2]=M[8]*V[0]+M[9]*V[1]+M[10]*V[2]+M[11];
	}
	public float detMat(float M[])
	{
		return	M[1]*M[11]*M[14]*M[4]-M[1]*M[10]*M[15]*M[4]-M[11]*M[13]*M[2]*M[4]+
				M[10]*M[13]*M[3]*M[4]-M[0]*M[11]*M[14]*M[5]+M[0]*M[10]*M[15]*M[5]+
				M[11]*M[12]*M[2]*M[5]-M[10]*M[12]*M[3]*M[5]-M[1]*M[11]*M[12]*M[6]+
				M[0]*M[11]*M[13]*M[6]+M[1]*M[10]*M[12]*M[7]-M[0]*M[10]*M[13]*M[7]-
				M[15]*M[2]*M[5]*M[8]+M[14]*M[3]*M[5]*M[8]+M[1]*M[15]*M[6]*M[8]-
				M[13]*M[3]*M[6]*M[8]-M[1]*M[14]*M[7]*M[8]+M[13]*M[2]*M[7]*M[8]+
				M[15]*M[2]*M[4]*M[9]-M[14]*M[3]*M[4]*M[9]-M[0]*M[15]*M[6]*M[9]+
				M[12]*M[3]*M[6]*M[9]+M[0]*M[14]*M[7]*M[9]-M[12]*M[2]*M[7]*M[9];
	}
	public void invMat(float rM[], float M[])
	{
		float	d=detMat(M);
		int		i;
	
		rM[0] = -(M[11]*M[14]*M[5] - M[10]*M[15]*M[5] - M[11]*M[13]*M[6] + M[10]*M[13]*M[7] + M[15]*M[6]*M[9] - M[14]*M[7]*M[9]);
		rM[1] = M[1]*M[11]*M[14] - M[1]*M[10]*M[15] - M[11]*M[13]*M[2] + M[10]*M[13]*M[3] + M[15]*M[2]*M[9] - M[14]*M[3]*M[9];
		rM[2] = -(M[15]*M[2]*M[5] - M[14]*M[3]*M[5] - M[1]*M[15]*M[6] + M[13]*M[3]*M[6] + M[1]*M[14]*M[7] - M[13]*M[2]*M[7]);
		rM[3] = M[11]*M[2]*M[5] - M[10]*M[3]*M[5] - M[1]*M[11]*M[6] + M[1]*M[10]*M[7] + M[3]*M[6]*M[9] - M[2]*M[7]*M[9];
		
		rM[4] = M[11]*M[14]*M[4] - M[10]*M[15]*M[4] - M[11]*M[12]*M[6] + M[10]*M[12]*M[7] + M[15]*M[6]*M[8] - M[14]*M[7]*M[8];
		rM[5] = -(M[0]*M[11]*M[14] - M[0]*M[10]*M[15] - M[11]*M[12]*M[2] + M[10]*M[12]*M[3] + M[15]*M[2]*M[8] - M[14]*M[3]*M[8]);
		rM[6] = M[15]*M[2]*M[4] - M[14]*M[3]*M[4] - M[0]*M[15]*M[6] + M[12]*M[3]*M[6] + M[0]*M[14]*M[7] - M[12]*M[2]*M[7];
		rM[7] = -(M[11]*M[2]*M[4] - M[10]*M[3]*M[4] - M[0]*M[11]*M[6] + M[0]*M[10]*M[7] + M[3]*M[6]*M[8] - M[2]*M[7]*M[8]);
		
		rM[8] = -(M[11]*M[13]*M[4] - M[11]*M[12]*M[5] + M[15]*M[5]*M[8] - M[13]*M[7]*M[8] - M[15]*M[4]*M[9] + M[12]*M[7]*M[9]);
		rM[9] = -(M[1]*M[11]*M[12] - M[0]*M[11]*M[13] - M[1]*M[15]*M[8] + M[13]*M[3]*M[8] + M[0]*M[15]*M[9] - M[12]*M[3]*M[9]);
		rM[10]= -(M[1]*M[15]*M[4] - M[13]*M[3]*M[4] - M[0]*M[15]*M[5] + M[12]*M[3]*M[5] - M[1]*M[12]*M[7] + M[0]*M[13]*M[7]);
		rM[11]= M[1]*M[11]*M[4] - M[0]*M[11]*M[5] + M[3]*M[5]*M[8] - M[1]*M[7]*M[8] - M[3]*M[4]*M[9] + M[0]*M[7]*M[9];
		
		rM[12]= M[10]*M[13]*M[4] - M[10]*M[12]*M[5] + M[14]*M[5]*M[8] - M[13]*M[6]*M[8] - M[14]*M[4]*M[9] + M[12]*M[6]*M[9];
		rM[13]= M[1]*M[10]*M[12] - M[0]*M[10]*M[13] - M[1]*M[14]*M[8] + M[13]*M[2]*M[8] + M[0]*M[14]*M[9] - M[12]*M[2]*M[9];
		rM[14]= M[1]*M[14]*M[4] - M[13]*M[2]*M[4] - M[0]*M[14]*M[5] + M[12]*M[2]*M[5] - M[1]*M[12]*M[6] + M[0]*M[13]*M[6];
		rM[15]= -(M[1]*M[10]*M[4] - M[0]*M[10]*M[5] + M[2]*M[5]*M[8] - M[1]*M[6]*M[8] - M[2]*M[4]*M[9] + M[0]*M[6]*M[9]);
		
		for(i=0;i<16;i++)
			rM[i]*=1/d;
	}
	public void printStatusMessage(String msg)
	{
		if(myQCApp_IMAGEN.status!=null)
		{
			myQCApp_IMAGEN.status.setText(msg);
			myQCApp_IMAGEN.status.paintImmediately(myQCApp_IMAGEN.status.getVisibleRect());
		}
		else
			System.out.println(msg);
	}
	public int value2rgb(int v,int v0, int cmapindex)
	{
		// v: valeur du pixel de l'image courante
		// v0: valeur du pixel de l'image de fond.
		int	rgb=0;
		int val_min=0;
		int	r,g,b;
		double fact = 0.15; // 20% de couleur 80% de fond
		switch(cmapindex)
		{
			case 1: // greyscale
				rgb=v+val_min<<16|v+val_min<<8|v+val_min;
				break;
			case 2:	// superposition avec le masque des segmentations
				if(v==10){ 
					//rgb=(int)((1-fact)*v0+fact*100)<<16|(int)((1-fact)*v0)<<8|(int)((1-fact)*v0);
				
					rgb=(int)((1-fact)*v0+fact*150+val_min)<<16|(int)((1-fact)*v0+val_min)<<8|(int)((1-fact)*v0+val_min);
					}
				else if(v==20){ //hippo
					//r=(int)(20); g=(int)(200); b=(int)(50);
					rgb=(int)((1-fact)*v0+val_min)<<16|(int)((1-fact)*v0+fact*150+val_min)<<8|(int)((1-fact)*v0+val_min); //vert transparent
					
					}
				
				else{ 
				/*r=cmap[3*v+0]+val_min;
				g=cmap[3*v+1]+val_min;
				b=cmap[3*v+2]+val_min;*/
				rgb=v<<16|v<<8|v;
				}
				
				// encode en couleur rgb.pour decoder: int r=(rgb>>16) & 0xff; 
				break;
		}
		return rgb;
	}
	BufferedImage drawSlice(MyVolume vol, double t, int plane, int cmapindex)
	// draw slice with index 's' in the plane 'plane' at position ox, oy using colourmap 'cmapindex'
	{
		int		x,y,z;
		float	P[],invP[]=new float[16];	// view plane transformation matrix and its inverse
		float	tmp[]=new float[3],tmpd[]=new float[3];
		int		dim1[]=new int[3];
		int		s;
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=vol.dim[1];
		tmp[1]=vol.dim[2];
		tmp[2]=vol.dim[3];
		multMatVec(tmpd,P,tmp);
		dim1[0]=(int)tmpd[0];
		dim1[1]=(int)tmpd[1];
		dim1[2]=(int)tmpd[2];
		
		s=(int)(t*dim1[2]);
		if(s<0) s=0;
		if(s>=dim1[2]) s=dim1[2]-1;
		return drawSlice(vol,s,plane,cmapindex);
	}

	BufferedImage drawSlice(MyVolume vol, int s, int plane, int cmapindex)
	// draw slice with index 's' in the plane 'plane' at position ox, oy using colourmap 'cmapindex'
	{
		BufferedImage	theImg;
		int				x,y,z;
		int				x1,y1,z1;
		int				rgb;
		int				v;
		float			sliceMax=0;					// maximum slice value
		float			P[],invP[]=new float[16];	// view plane transformation matrix and its inverse
		float			tmp[]=new float[3],tmpd[]=new float[3],tmpx[]=new float[3];
		int				dim1[]=new int[3];
		Rectangle		rect=new Rectangle(0,0,1,1);
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=vol.dim[1];
		tmp[1]=vol.dim[2];
		tmp[2]=vol.dim[3];
		multMatVec(tmpd,P,tmp);
		if(tmpd[0]<0) tmpd[0]=(-1)*tmpd[0];
		dim1[0]=(int)tmpd[0];
		if(tmpd[1]<0) tmpd[1]=(-1)*tmpd[1];
		dim1[1]=(int)tmpd[1];
		if(tmpd[2]<0) tmpd[2]=(-1)*tmpd[2];
		dim1[2]=(int)tmpd[2];

		// find bounding box
		tmp[0]=(float)vol.boundingBox[0];
		tmp[1]=(float)vol.boundingBox[2];
		tmp[2]=(float)vol.boundingBox[4];
		multMatVec(tmpd,P,tmp);
		rect.x=Math.max((int)tmpd[0]-10,0);
		rect.y=Math.max((int)tmpd[1]-10,0);
		tmp[0]=(float)vol.boundingBox[1];
		tmp[1]=(float)vol.boundingBox[3];
		tmp[2]=(float)vol.boundingBox[5];
		multMatVec(tmpd,P,tmp);
		//System.out.println("rect.x: "+rect.x+" rect.y: "+rect.y+" tmpd[0]: "+ tmpd[0]+" tmpd[1]"+tmpd[1]);
		rect.width=Math.min((int)tmpd[0]-rect.x+1+10,dim1[0]);
		rect.width=Math.max(rect.width, 0-rect.width);
		rect.height=Math.min((int)tmpd[1]-rect.y+1+10,dim1[1]);
		rect.height=Math.max(rect.height, 0-rect.height);
		
		// find maximum brightness
		z=s;
		for(x=rect.x;x<rect.width+rect.x;x++)
		for(y=rect.y;y<rect.height+rect.y;y++)
		{
			tmp[0]=x;
			tmp[1]=y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			
			v=(int)vol.getValue(x1,y1,z1);
			if(v>sliceMax){
				if(vol.name.contains("_MNI"))
				sliceMax=v;
				else
				sliceMax=2000;	
			}
			
		}
		
		// draw slice
		//System.out.println("rect.width "+rect.width+" rect.height "+rect.height );
		theImg=new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_RGB);
		z=s;
		for(x=0;x<rect.width;x++)
		for(y=0;y<rect.height;y++)
		{
			tmp[0]=x+rect.x;
			tmp[1]=y+rect.y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			
			v=(int)(vol.getValue(x1,y1,z1));
			if(cmapindex==1){
				//if(sliceMax > 2000 ) {sliceMax = 2000; v=(int)(sliceMax-1);}
				//else sliceMax=sliceMax;
				//System.out.println("sliceMax en ("+x1+","+"y1"+","+z1+") = "+sliceMax);
				rgb=value2rgb((int)(v*255.0/sliceMax),v,cmapindex); //couleur des t1 seules (pas l'image de fond pour le masque)
			}
			else
				rgb=value2rgb(v,v,cmapindex);
			if(v>0)
				theImg.setRGB(x,rect.height-1-y,rgb);
		}
		return theImg;
	}
	BufferedImage drawSlice(MyVolume vol, MyVolume volBack, double t, int plane, int cmapindex)
	// draw slice with index 's' in the plane 'plane' at position ox, oy using colourmap 'cmapindex'
	{
		int		x,y,z;
		float	P[],invP[]=new float[16];	// view plane transformation matrix and its inverse
		float	tmp[]=new float[3],tmpd[]=new float[3];
		int		dim1[]=new int[3];
		int		s;
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=vol.dim[1];
		tmp[1]=vol.dim[2];
		tmp[2]=vol.dim[3];
		multMatVec(tmpd,P,tmp);
		dim1[0]=(int)tmpd[0];
		dim1[1]=(int)tmpd[1];
		dim1[2]=(int)tmpd[2];
		
		s=(int)(t*dim1[2]);
		if(s<0) s=0;
		if(s>=dim1[2]) s=dim1[2]-1;
		return drawSlice(vol,volBack,s,plane,cmapindex);
	}

	BufferedImage drawSlice(MyVolume vol,MyVolume vol2, MyVolume volBack, double t, int plane, int cmapindex, double scale)
	// draw slice with index 's' in the plane 'plane' at position ox, oy using colourmap 'cmapindex'
	{
		int		x,y,z;
		float	P[],invP[]=new float[16];	// view plane transformation matrix and its inverse
		float	tmp[]=new float[3],tmpd[]=new float[3];
		int		dim1[]=new int[3];
		int		s;
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=(float)(vol.dim[1]);
		tmp[1]=(float)(vol.dim[2]);
		tmp[2]=(float)(vol.dim[3]);
		/*tmp[0]=(float)(vol.dim[1]*scale);
		tmp[1]=(float)(vol.dim[2]*scale);
		tmp[2]=(float)(vol.dim[3]*scale);*/
		multMatVec(tmpd,P,tmp);
		dim1[0]=(int)tmpd[0];
		dim1[1]=(int)tmpd[1];
		dim1[2]=(int)tmpd[2];
		
		s=(int)(t*dim1[2]);
		if(s<0) s=0;
		if(s>=dim1[2]) s=dim1[2]-1;

		return drawSlice(vol,vol2,volBack,s,plane,cmapindex,scale);
	}
	
	
	
	
	BufferedImage drawSliceForSeg(MyVolume volL,MyVolume volR , MyVolume volBackL, MyVolume volBackR, double t, int plane, int cmapindex, double scale)
	// draw slice with index 's' in the plane 'plane' at position ox, oy using colourmap 'cmapindex'
	{
		int		x,y,z;
		float	P[],invP[]=new float[16];	// view plane transformation matrix and its inverse
		float	tmp[]=new float[3],tmpd[]=new float[3];
		int		dim1[]=new int[3];
		int		s;
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=(float)(volL.dim[1]+volR.dim[1]+10);
		tmp[1]=(float)(volL.dim[2]+volR.dim[2]+10);
		tmp[2]=(float)(volL.dim[3]+volR.dim[3]+10);
		/*tmp[0]=(float)(vol.dim[1]*scale);
		tmp[1]=(float)(vol.dim[2]*scale);
		tmp[2]=(float)(vol.dim[3]*scale);*/
		multMatVec(tmpd,P,tmp);
		dim1[0]=(int)tmpd[0];
		dim1[1]=(int)tmpd[1];
		dim1[2]=(int)tmpd[2];
		
		s=(int)(t*dim1[2]);
		if(s<0) s=0;
		if(s>=dim1[2]) s=dim1[2]-1;

		return drawSliceForSeg(volL,volR,volBackL,volBackR,s,plane,cmapindex,scale);
	}
	

	
	BufferedImage drawSlice(MyVolume vol, MyVolume volBack, int s, int plane, int cmapindex)
	// draw slice with index 's' in the plane 'plane' at position ox, oy using colourmap 'cmapindex'
	{
		BufferedImage	theImg;
		int				x,y,z;
		int				x1,y1,z1;
		int				rgb,rgb0;
		int				v,v0;
		float			sliceMax=0;					// maximum slice value
		float			slicemin=1000;
		float			P[],invP[]=new float[16];	// view plane transformation matrix and its inverse
		float			tmp[]=new float[3],tmpd[]=new float[3],tmpx[]=new float[3];
		int				dim1[]=new int[3];
		Rectangle		rect=new Rectangle(0,0,1,1);
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=volBack.dim[1];
		tmp[1]=volBack.dim[2];
		tmp[2]=volBack.dim[3];
		multMatVec(tmpd,P,tmp);
		dim1[0]=(int)tmpd[0];
		dim1[1]=(int)tmpd[1];
		dim1[2]=(int)tmpd[2];

		// find bounding box
		tmp[0]=(float)volBack.boundingBox[0];
		tmp[1]=(float)volBack.boundingBox[2];
		tmp[2]=(float)volBack.boundingBox[4];
		multMatVec(tmpd,P,tmp);
		rect.x=Math.max((int)tmpd[0]-10,0);
		rect.y=Math.max((int)tmpd[1]-10,0);
		tmp[0]=(float)volBack.boundingBox[1];
		tmp[1]=(float)volBack.boundingBox[3];
		tmp[2]=(float)volBack.boundingBox[5];
		multMatVec(tmpd,P,tmp);
		rect.width=Math.min((int)tmpd[0]-rect.x+1+10,dim1[0]);
		rect.height=Math.min((int)tmpd[1]-rect.y+1+10,dim1[1]);
		
		// find maximum brightness
		z=s;
		for(x=rect.x;x<rect.width+rect.x;x++)
		for(y=rect.y;y<rect.height+rect.y;y++)
		{
			tmp[0]=x;
			tmp[1]=y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			
			v=(int)volBack.getValue(x1,y1,z1);
			if(volBack.name.contains("_MNI")){
			//	System.out.println("mni "+v);
			//if(v>sliceMax)
			//	sliceMax=v;
			}
			if(v<slicemin)
				slicemin=v;
				sliceMax=1500;
		}
		if(!volBack.name.contains("_MNI")){
			sliceMax=1500;
			//System.out.println(volBack.name);
				
		}
		float slicediff=255/(sliceMax-slicemin);
		// draw slice
		theImg=new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_RGB);
		z=s;
		for(x=0;x<rect.width;x++)
		for(y=0;y<rect.height;y++)
		{
			tmp[0]=x+rect.x;
			tmp[1]=y+rect.y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			v=(int)(vol.getValue(x1,y1,z1));
			v0=(int)(volBack.getValue(x1,y1,z1));
			//double ev0=Math.exp((double)(-v0));
			//double esliceMax=Math.exp((double)(-sliceMax));
			rgb=value2rgb(v,(int)(((v0-slicemin)*slicediff)),cmapindex);
			rgb0=value2rgb((int)(((v0-slicemin)*slicediff)),v0,1); // pour la couleure de l'image de fond en cas de superposition, pas des autres volumes seuls.
			if(v>0)
				theImg.setRGB(x,rect.height-1-y,rgb);
			else
				theImg.setRGB(x,rect.height-1-y,rgb0);
		}
		return theImg;
	}
	
	BufferedImage drawSliceForSeg(MyVolume volL,MyVolume volR , MyVolume volBackL, MyVolume volBackR, int s, int plane, int cmapindex,double scale)
	// draw slice with index 's' in the plane 'plane' at position ox, oy using colourmap 'cmapindex'
	{
		BufferedImage	theImg;
		int				x,y,z;
		int				x1,y1,z1;
		int				rgb,rgb0;
		int				v,v1,v2,v0;
		float			sliceMax=0;					// maximum slice value
		float 			slicemin=10000;
		float			P[],invP[]=new float[16];	// view plane transformation matrix and its inverse
		float			tmp[]=new float[3],tmpd[]=new float[3],tmpx[]=new float[3];
		int				dim1[]=new int[3];
		Rectangle		rect=new Rectangle(0,0,1,1);
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=volBackL.dim[1]+volBackR.dim[1]+10;
		tmp[1]=volBackL.dim[2]+volBackR.dim[2]+10;
		tmp[2]= Math.max((float) volBackL.dim[3],(float) volBackR.dim[3])+10;
		multMatVec(tmpd,P,tmp);// multiplie une matrice P par un vecteur tmp
		dim1[0]=(int)tmpd[0];
		dim1[1]=(int)tmpd[1];
		dim1[2]=(int)tmpd[2];
		
		

		// find bounding box
		tmp[0]=volBackL.boundingBox[0]+volBackR.boundingBox[0]+10;
		tmp[1]=volBackL.boundingBox[2]+volBackR.boundingBox[2]+10;
		tmp[2]=Math.max((float) volBackL.boundingBox[4],(float) volBackR.boundingBox[4]);
		multMatVec(tmpd,P,tmp);
		rect.x=Math.max((int)tmpd[0]-10,0);
		rect.y=Math.max((int)tmpd[1]-10,0);
		tmp[0]=(float)volBackL.boundingBox[1]+volBackR.boundingBox[1]+10;
		tmp[1]=(float)volBackL.boundingBox[3]+volBackR.boundingBox[3]+10;
		tmp[2]=Math.max((float) volBackL.boundingBox[5],(float) volBackR.boundingBox[5]);
		multMatVec(tmpd,P,tmp);
		rect.width=Math.min((int)tmpd[0]-rect.x+1+10,dim1[0]);
		rect.height=Math.min((int)tmpd[1]-rect.y+1+10,dim1[1])*2;
		
		// find maximum brightness
		z=s;
		for(x=rect.x;x<rect.width+rect.x;x++)
			for(y=rect.y;y<rect.height+rect.y;y++)
			{
				tmp[0]=x;
				tmp[1]=y;
				tmp[2]=z;
				multMatVec(tmpx,invP,tmp);
				x1=(int)tmpx[0];
				y1=(int)tmpx[1];
				z1=(int)tmpx[2];
				
				v=(int)volBackL.getValue(x1,y1,z1);
				slicemin=0;
				sliceMax=1000;
			}

		float slicediff=255/(sliceMax-slicemin);
		// draw slice
		theImg=new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_RGB);
		z=s;

		// pour afficher la supperposition hippoG+fondG en haut à G.
		for(x=0;x<rect.width/2;x++)
		for(y=0;y<rect.height/2;y++)
		{
			tmp[0]=x+rect.x;
			tmp[1]=y+rect.y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			
			v=(int)(volL.getValue(x1,y1,z1));
			v0=(int)(volBackL.getValue(x1,y1,z1));
			if(v0 >= sliceMax){
				//System.out.println(v0);
				v0 = (int)(sliceMax-1);
			}
			int v0_=(int)(((v0-slicemin)*slicediff));
			rgb=value2rgb(v,(int)(v0_),cmapindex);
			rgb0=value2rgb((int)(v0_),v0,1); 

			if(v>0){
				//rgb = filterRGB(x,rect.height-1-y,rgb);
				theImg.setRGB(x,rect.height-1-y,rgb);
			}
			else{
				//rgb0 = filterRGB(x,rect.height-1-y,rgb0);
				theImg.setRGB(x,rect.height-1-y,rgb0);				
			}

		}
		
		
		// pour afficher la supperposition hippoD+fondD en haut à D.
			for(x=(int)(rect.width/2);x<rect.width;x++)
			for(y=0;y<rect.height/2;y++)
			{
				tmp[0]=x+rect.x;
				tmp[1]=y+rect.y;
				tmp[2]=z;
				multMatVec(tmpx,invP,tmp);
				x1=(int)(tmpx[0]-rect.width/2);
				y1=(int)tmpx[1];
				z1=(int)tmpx[2];
				
				v=(int)(volR.getValue(x1,y1,z1));
				v0=(int)(volBackR.getValue(x1,y1,z1));
				if(v0 >= sliceMax){
					//System.out.println(v0);
					v0 = (int)(sliceMax-1);
				}
				int v0_=(int)(((v0-slicemin)*slicediff));
				rgb=value2rgb(v,(int)(v0_),cmapindex);
				rgb0=value2rgb((int)(v0_),v0,1); 
				if(v>0){
					theImg.setRGB(x,rect.height-1-y,rgb);
				}
				else{
					//rgb0 = filterRGB(x,rect.height-1-y,rgb0);
					theImg.setRGB(x,rect.height-1-y,rgb0);				
				}
			}		
			
			
			// pour afficher le fondD en bas à D.
				for(x=(int)(rect.width/2);x<rect.width;x++)
				for(y=(int)(rect.height/2);y<rect.height;y++)
				{
					tmp[0]=x+rect.x-rect.width/2;
					tmp[1]=y+rect.y-rect.height/2;
					tmp[2]=z;
					multMatVec(tmpx,invP,tmp);
					x1=(int)(tmpx[0]);
					y1=(int)(tmpx[1]);
					z1=(int)tmpx[2];
					
					v0=(int)(volBackR.getValue(x1,y1,z1));
					if(v0 >= sliceMax){
						//System.out.println(v0);
						v0 = (int)(sliceMax-1);
					}
					int v0_=(int)(((v0-slicemin)*slicediff));
					rgb0=value2rgb((int)(v0_),v0,1); 
					theImg.setRGB(x,rect.height-1-y,rgb0);	
				}
					
					
					
					
					

			// pour afficher le fondG en bas à G.
				for(x=0;x<rect.width/2;x++)
				for(y=(int)(rect.height/2);y<rect.height;y++)
				{
					tmp[0]=x+rect.x;
					tmp[1]=y+rect.y-rect.height/2;
					tmp[2]=z;
					multMatVec(tmpx,invP,tmp);
					x1=(int)(tmpx[0]);
					y1=(int)(tmpx[1]);
					z1=(int)tmpx[2];
					
					v0=(int)(volBackL.getValue(x1,y1,z1));
					if(v0 >= sliceMax){
						//System.out.println(v0);
						v0 = (int)(sliceMax-1);
					}
					int v0_=(int)(((v0-slicemin)*slicediff));
					rgb0=value2rgb((int)(v0_),v0,1); 
					theImg.setRGB(x,rect.height-1-y,rgb0);				
				
				}	
	
		return theImg;
	}
	
	
	
	
	BufferedImage drawSlice(MyVolume vol1,MyVolume vol2 , MyVolume volBack, int s, int plane, int cmapindex,double scale)
	// draw slice with index 's' in the plane 'plane' at position ox, oy using colourmap 'cmapindex'
	{
		BufferedImage	theImg;
		int				x,y,z;
		int				x1,y1,z1;
		int				rgb,rgb0;
		int				v,v1,v2,v0;
		float			sliceMax=0;					// maximum slice value
		float 			slicemin=10000;
		float			P[],invP[]=new float[16];	// view plane transformation matrix and its inverse
		float			tmp[]=new float[3],tmpd[]=new float[3],tmpx[]=new float[3];
		int				dim1[]=new int[3];
		Rectangle		rect=new Rectangle(0,0,1,1);
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=volBack.dim[1];
		tmp[1]=volBack.dim[2];
		tmp[2]=volBack.dim[3];
		multMatVec(tmpd,P,tmp);
		dim1[0]=(int)tmpd[0];
		dim1[1]=(int)tmpd[1];
		dim1[2]=(int)tmpd[2];

		// find bounding box
		tmp[0]=(float)volBack.boundingBox[0];
		tmp[1]=(float)volBack.boundingBox[2];
		tmp[2]=(float)volBack.boundingBox[4];
		multMatVec(tmpd,P,tmp);
		rect.x=Math.max((int)tmpd[0]-10,0);
		rect.y=Math.max((int)tmpd[1]-10,0);
		tmp[0]=(float)volBack.boundingBox[1];
		tmp[1]=(float)volBack.boundingBox[3];
		tmp[2]=(float)volBack.boundingBox[5];
		multMatVec(tmpd,P,tmp);
		rect.width=Math.min((int)tmpd[0]-rect.x+1+10,dim1[0]);
		rect.height=Math.min((int)tmpd[1]-rect.y+1+10,dim1[1]);
		
		// find maximum brightness
		z=s;

		for(x=rect.x;x<rect.width+rect.x;x++)
		for(y=rect.y;y<rect.height+rect.y;y++)
		{
			tmp[0]=x;
			tmp[1]=y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			
			v1=(int)volBack.getValue(x1,y1,z1);
			if(volBack.name.contains("_MNI")){
				System.out.println("mni "+v1);
			//if(v1>sliceMax)
			//	sliceMax=v1;
			}
			if(v1<slicemin)
				slicemin=v1;
				sliceMax=1500;
				
		}
		//System.out.println("slicemin: "+slicemin+" SliceMax: "+sliceMax);
		if(!volBack.name.contains("_MNI")) 
			sliceMax=1500;//(1*sliceMax)/2;
		//System.out.println("slicemin: "+slicemin+" SliceMax: "+sliceMax);
		float slicediff=255/(sliceMax-slicemin);
		// draw slice
		theImg=new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_RGB);
		z=s;

		
		for(x=0;x<rect.width;x++)
		for(y=0;y<rect.height;y++)
		{
			tmp[0]=x+rect.x;
			tmp[1]=y+rect.y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			
			v1=(int)(vol1.getValue(x1,y1,z1));
			v2=(int)(vol2.getValue(x1,y1,z1));
			v=v1+v2;
//System.out.print(" _"+v);

			v0=(int)(volBack.getValue(x1,y1,z1));
			if(v0 >= sliceMax){
				//System.out.println(v0);
				v0 = (int)(sliceMax-1);
			}
			//double ev0=Math.exp((double)(-v0));
			//double esliceMax=Math.exp((double)(-sliceMax));
			int v0_=(int)(((v0-slicemin)*slicediff));//255)/sliceMax); //slicediff=(255/(sliceMax-slicemin));
		//	System.out.println(v0_);
			rgb=value2rgb(v,(int)(v0_),cmapindex);//(int)(v0*255.0/sliceMax)
			rgb0=value2rgb((int)(v0_),v0,1); // pour la couleure de l'image de fond en cas de superposition, pas des autres volumes seuls.

			if(v>0){
				//rgb = filterRGB(x,rect.height-1-y,rgb);
				theImg.setRGB(x,rect.height-1-y,rgb);

			}
			else{
				//rgb0 = filterRGB(x,rect.height-1-y,rgb0);
				theImg.setRGB(x,rect.height-1-y,rgb0);
				
			}

		}
		
		
		return theImg;
	}
	
	
	
	
	
	
	  public int filterRGB(int x, int y, int rgb) {
		    // Adjust the alpha value
		    int alpha = (rgb >> 24) & 0xff;
		    alpha = (alpha * alpha) / 255;

		    // Return the result
		    return ((rgb & 0x00ffffff) | (alpha << 24));
		  }
	
	BufferedImage drawVolume(MyVolume vol, int plane, int cmapindex)
	{
		BufferedImage	theImg;
		int		x,y,z,x1,y1,z1,rgb;
		float	s0,s1;
		int		v;
		float	sliceMax=0;
		float	P[],invP[]=new float[16],tmp[]=new float[3],tmpd[]=new float[3],tmpx[]=new float[3];
		int		dim1[]=new int[3];
		int		r,g,b;
		Rectangle	rect=new Rectangle(0,0,0,0);
		
		// transform volume to view plane
		P=X;
		switch(plane)
		{
			case 1: P=X; break;
			case 2: P=Y; break;
			case 3: P=Z; break;
		}
		invMat(invP,P);
		tmp[0]=vol.dim[1];
		tmp[1]=vol.dim[2];
		tmp[2]=vol.dim[3];
		multMatVec(tmpd,P,tmp);
		dim1[0]=(int)tmpd[0];
		dim1[1]=(int)tmpd[1];
		dim1[2]=(int)tmpd[2];

		// find 1st and last non-empty slices (for lighting) and bounding box
		s0=dim1[2]; // 1st
		s1=0;		// last
		rect.x=dim1[0];
		rect.y=dim1[1];
		for(z=0;z<dim1[2];z++)
		for(x=0;x<dim1[0];x++)
		for(y=0;y<dim1[1];y++)
		{
			tmp[0]=x;
			tmp[1]=y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			
			v=(int)vol.getValue(x1,y1,z1);

			if(v>0)
			{
				if(z<s0)	s0=z;
				if(z>s1)	s1=z;

				if(x<rect.x)		rect.x=x;
				if(y<rect.y)		rect.y=y;
				if(x>rect.width)	rect.width=x;
				if(y>rect.height)	rect.height=y;
			}
		}
		rect.x=Math.max(rect.x-10,0);
		rect.y=Math.max(rect.y-10,0);
		rect.width=Math.min(rect.width-rect.x+1+10,dim1[0]);
		rect.height=Math.min(rect.height-rect.y+1+10,dim1[1]);

		// draw volume
		theImg=new BufferedImage(rect.width,rect.height,BufferedImage.TYPE_INT_RGB);
		for(z=0;z<dim1[2];z++)
		for(x=0;x<rect.width;x++)
		for(y=0;y<rect.height;y++)
		{
			tmp[0]=x+rect.x;
			tmp[1]=y+rect.y;
			tmp[2]=z;
			multMatVec(tmpx,invP,tmp);
			x1=(int)tmpx[0];
			y1=(int)tmpx[1];
			z1=(int)tmpx[2];
			
			v=(int)vol.getValue(x1,y1,z1);
			if(v==0)
				continue;
			rgb=value2rgb(v,v,cmapindex); //if(rgb==0) System.out.println("missing label:"+v);
			//System.out.println("rgb: "+rgb);
			// light
			r=(int)((rgb>>16)        *Math.pow((z-s0)/(s1-s0),0.5));
			g=(int)(((rgb&0xff00)>>8)*Math.pow((z-s0)/(s1-s0),0.5));
			b=(int)((rgb&0xff)       *Math.pow((z-s0)/(s1-s0),0.5));
			rgb=r<<16|g<<8|b;

			theImg.setRGB(x,rect.height-1-y,rgb);
		}
		return theImg;
	}
	BufferedImage drawErrorSlice()
	{
		BufferedImage	theImg=new BufferedImage(128,128,BufferedImage.TYPE_INT_RGB);
		Graphics2D 		g2=theImg.createGraphics();
		
		g2.setFont(new Font("Helvetica", Font.BOLD, 14));
		g2.drawString("UNAVAILABLE",15,64);
		g2.drawRect(1,1,126,126);
		
		return theImg;
	}
    String getVolumeName(String name)
    {
    	int	i;
    	i=name.indexOf(".");
    	return name.substring(0,i);
    }
    String getPlaneName(String name)
    {
    	int	i0,i1;
    	i0=name.indexOf(".");
    	i1=name.lastIndexOf(".");
    	return name.substring(i0+1,i1);
    }
    String getImageTypeName(String name)
    {
    	int	i;
    	i=name.lastIndexOf(".");
    	return name.substring(i+1,name.length());
    }
    
    
	public int setVolume(java.lang.String filename,float Lamy,float Lamy_,float Lhip,float Lhip_,float Ramy,float Ramy_,float Rhip,float Rhip_)
	{
		// Pour creer les imagettes.
		File		f;
		int			i;
		String		volName="",tmp;
		MyVolume	vol=null;
		MyVolume 	volR=null;
		int			err=0;
		float valL,valR;
		 Ramy=0;
		 Rhip=0;
		 Lamy=0;
		 Lhip=0;
		 Ramy_=0;
		 Rhip_=0;
		 Lamy_=0;
		 Lhip_=0;
		
		subject=filename;
		
		img=new BufferedImage[imgList.length];
		for(i=0;i<imgList.length;i++)
		{
			String	name=subject+"/QC/png/"+imgList[i]+".png"; // adresse des imagettes png si elles existent
			f=new File(name);
			tmp=getVolumeName(imgList[i]);
			volName=tmp;
			if(f.exists())
			{
				// QC images available: load them
				printStatusMessage("Loading image \""+name+"\"...");
				try{img[i] = ImageIO.read(f);}
				catch (IOException e){}
			}
			else
			{
				// QC images unavailable: make them (and save them)
				tmp=getVolumeName(imgList[i]);
				//System.out.println(subject+"/QC/png/"+imgList[i]+".png n'existe pas ! et tmp ="+tmp);
				
				//if(!volName.equals(tmp))
				//{
					volName=tmp;
					printStatusMessage("Loading volume \""+volName+"\"...");
					//System.out.println("MyVolume("+subject+"/QC/vol/"+volName+".nii");
					vol=new MyVolume(subject+"/QC/vol/"+volName+".nii.gz");
					if(vol.volume==null) 
						vol=new MyVolume(subject+"/QC/vol/"+volName+".nii");
				//}
				//	System.out.println("vol.volume==null ?? "+vol.volume);
					
					// ajouter si volName == MNI => le chercher dans ProcessFSL, pour pouvoir regarder les hippos sans attendre la fin de la creation de la BD
					if (volName.equals("_nobiais")){
						vol=new MyVolume(subject+"/Process_FSL/001r.nii.gz");
						if(vol.volume==null) vol=new MyVolume(subject+"/Process_FSL/001r.nii");	
					}
					
					if(vol.volume==null) // Si le volume subject+"/QC/vol/"+volName+".nii.gz" n'existe pas
				{
					printStatusMessage("ERROR: Volume \""+subject+"/QC/vol/"+volName+".nii.gz\" unavailable.");
					System.out.println("ERROR: Volume \""+subject+"/QC/vol/"+volName+".nii.gz\" unavailable.");
					img[i]=drawErrorSlice(); // affiche imagette "UNAVAILABLE"
					err=1;
				}
				else
				{
					System.out.println("MyVolume("+subject+"/QC/vol/"+volName+".nii), l'imagette est en creation");
					String		volPlane=getPlaneName(imgList[i]);
					String		imgType=getImageTypeName(imgList[i]);
					int			cmapindex;
					int			plane;
	
					printStatusMessage("Drawing volume \""+volName+"\", plane:"+volPlane+"...");
					
					plane=1;
					if(volPlane.equals("X")) plane=1;
					if(volPlane.equals("Y")) plane=2;
					if(volPlane.equals("Z")) plane=3;
					
					cmapindex=1;
					if(volName.equals("_a_lha") || volName.equals("_a_rha")) // en cas de superposition de 2 volumes...
						cmapindex=2;
					
					if(imgType.equals("2D")){
						img[i]=drawSlice(vol,0.3,plane,cmapindex);
						}
					else
						img[i]=drawVolume(vol,plane,cmapindex);
					
					// save image (create directory qc if it does not exist)
					File qcdir=new File(subject+"/QC/png");
					if(!qcdir.exists())
						qcdir.mkdir();
					try{ImageIO.write(img[i],"png",f);}catch(IOException e){}
					
				}
			}
			// Dans tous les cas :
			String		volPlane=getPlaneName(imgList[i]);
			int plane;
			plane=2;
			if(volPlane.equals("X")) plane=1;
			if(volPlane.equals("Y")) plane=2;
			if(volPlane.equals("Z")) plane=3;
			if(volName.equals("_a_lha") && (plane==1)){
			vol=new MyVolume(subject+"/QC/vol/"+volName+".nii.gz");
			if(vol.volume==null) 
				vol=new MyVolume(subject+"/QC/vol/"+volName+".nii");
			File fichier = new File(subject+"/QC/LandRvolumes_SACHA.txt") ;
			//System.out.println(fichier.length());
			if(vol.volume!=null && fichier.length()<2){  //&& !fichier.exists()

			// calcul du volume de l'hippocampe et de l'amygdale et ajout dans le fichier LandRvolumes.csv

				volR = new MyVolume(subject+"/QC/vol/_a_rha.nii.gz");
				if(volR.volume==null)
					volR = new MyVolume(subject+"/QC/vol/_a_rha.nii");
				try{
					fichier = new File(subject+"/QC/LandRvolumes_SACHA.txt") ;
					//PrintWriter out = new PrintWriter(new FileWriter(fichier)) ;
				//FileWriter fichier = new FileWriter(subject+"QC/LandRvolumes_FSL_SACHA.txt");
				//fichier.write("");
				fichier.createNewFile();
				
				} 
				catch(IOException e){
				System.out.println("Impossible de creer le fichier "+subject+"/QC/LandRvolumes_FSL_SACHA.txt: "+e.getMessage());
			}

				System.out.println(" Calcul du volume des hippocampes du sujet "+subject);
				for (int xx=0;xx<vol.getDim()[1]; xx++){
					for (int yy=0;yy<vol.getDim()[2]; yy++){
						for (int zz=0;zz<vol.getDim()[3]; zz++){
							valR = volR.getValue(xx, yy, zz);
							valL = vol.getValue(xx, yy, zz);
							if(valR == 10)
								Ramy++;
							if(valR ==20)
								Rhip++;
							if(valL == 10)
								Lamy++;
							if(valL ==20)
								Lhip++;
						}
					}
				}
				float	pixDimL[]=new float[4];
				float	pixDimR[]=new float[4];
				pixDimL=vol.getPixdim();
				pixDimR=volR.getPixdim();
				//System.out.println("        Ramy: "+Ramy+" Rhip: "+Rhip+" Lamy: "+Lamy+" Lhip: "+Lhip);
				 Ramy_=Ramy*pixDimR[1]*pixDimR[2]*pixDimR[3];
				 Rhip_=Rhip*pixDimR[1]*pixDimR[2]*pixDimR[3];
				 Lamy_=Lamy*pixDimL[1]*pixDimL[2]*pixDimL[3];
				 Lhip_=Lhip*pixDimL[1]*pixDimL[2]*pixDimL[3];
				//System.out.println(volName+": Ramy: "+Ramy_+" Rhip: "+Rhip_+" Lamy: "+Lamy_+" Lhip: "+Lhip_);
				printStatusMessage("Subject: "+subject+". LHip = "+Lhip+" / Lhip_ = "+Lhip_+" // Rhip = "+Rhip+" / Rhip_ = "+Rhip);
				
				try{
					FileWriter fw = new FileWriter(subject+"/QC/LandRvolumes_SACHA.txt",false);
					BufferedWriter output = new BufferedWriter(fw);
					
					String Newligne=System.getProperty("line.separator"); 
					output.write("SubjID,Lamy,Lamy_,Lhip,Lhip_,Ramy,Ramy_,Rhip,Rhip_"+Newligne);
					output.write(subject+","+Lamy+","+Lamy_+","+Lhip+","+Lhip_+","+Ramy+","+Ramy_+","+Rhip+","+Rhip_+"\n");
					output.flush();
					output.close();
					fw.close();
				}
				catch(IOException ioe){
					System.out.print("Erreur : ");
					ioe.printStackTrace();
					}
				} // fin if

			}
		}
		initialized=1;
		
		return err;
		
	}
    public MyImage()
    {
 		int i;
 		
 		initialized=0;
 		
		// init segmentation label colourmap		
		int	tmp[]={	16,2,2,1,	// BrainStem
					10,1,0,0,	// Lthal
					49,0,1,0,	// Rthal
					11,1,0,1,	// LCa
					50,0,1,1,	// Rca
					12,1,0,2,	// LPu
					51,0,1,2,	// RPu
					13,1,1,0,	// LPa
					52,1,1,2,	// RPa
					17,1,2,0,	// LHip
					53,2,1,0,	// RHip
					18,2,1,1,	// LAmy
					54,1,2,1,	// RAmy
					26,1,2,2,	// LAcc
					58,2,1,2};	// RAcc
		for(i=0;i<15;i++)
		{
			cmap[3*tmp[i*4]+0]=tmp[i*4+1]*127;
			cmap[3*tmp[i*4]+1]=tmp[i*4+2]*127;
			cmap[3*tmp[i*4]+2]=tmp[i*4+3]*127;
			//System.out.println("i="+i+": cmap1: "+tmp[i*4+1]*127+" / cmap2: "+tmp[i*4+2]*127+" / cmap3: "+tmp[i*4+3]*127);
		}

		// init image list
		String tmpList[]={"_a_lha.X.2D","_a_lha.Y.2D","_a_lha.Z.2D",
							"_a_rha_seg.X.2D","_a_rha_seg.Y.2D","_a_rha_seg.Z.2D",
							"_nobiais.X.2D","_nobiais.Y.2D","_nobiais.Z.2D",
							"_MNI.X.2D","_MNI.Y.2D","_MNI.Z.2D"};
		imgList=new String[tmpList.length];
		for(i=0;i<tmpList.length;i++)
			imgList[i]=tmpList[i];
		
		imgListName=new String[tmpList.length];
		imgListName[0]= "natif_seg_sag";
		imgListName[1]=	"natif_seg_cor";
		imgListName[2]= "natif_seg_axi";
		imgListName[3]= "box_seg_sag";
		imgListName[4]= "box_seg_cor";
		imgListName[5]= "box_seg_axi";
		imgListName[6]= "natif_sag";
		imgListName[7]="natif_cor";
		imgListName[8]="natif_axi";
		imgListName[9]="MNI_sag_";
		imgListName[10]="MNI_cor_";
		imgListName[11]="MNI_axi_";
		
    }
}