import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;


class MyVolume
{           
	byte	volume[];					// 3d volume data
	short	dim[]=new short[4];			// 3d volume dimensions (1st:dim[1])
	float	pixdim[]=new float[4];		// 3d volume pixel dimensions
	short	datatype;					// 3d volume data type
	int		boundingBox[]=new int[6];	// 3d volume bounding box
	String  name;
	int bytesPerVoxel()
	{
		int bpv=0;
		switch(datatype)
		{
			case 2:		bpv=1; break;//DT_UINT8
			case 4:		bpv=2; break;//DT_INT16
			case 8:		bpv=4; break;//DT_INT32
			case 16:	bpv=4; break;//DT_FLOAT32
		}
		return bpv;
	}
	int loadNiftiVolume(String filename)
	{
		int	err=0;
		
		try
		{
			DataInputStream	dis;
			if (filename.charAt(filename.length()-1)=='z'){
			// Read volume data
			FileInputStream	fis=new FileInputStream(filename/*"/tmp/t1w.nii"*/);
			GZIPInputStream gis = new GZIPInputStream(fis);
			dis=new DataInputStream(gis);
			}
			else{
				FileInputStream	fis=new FileInputStream(filename/*"/tmp/t1w.nii"*/);
				dis=new DataInputStream(fis);
			}
			byte 			b[]=new byte[352];
			ByteBuffer		bb=ByteBuffer.wrap(b);
			
			dis.readFully(b,0,352);

			bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
			//System.out.println("sizeof_hdr: "+bb.getInt());
			dim[0]=bb.getShort(40);
			dim[1]=bb.getShort(42);
			dim[2]=bb.getShort(44);
			dim[3]=bb.getShort(46);
			//System.out.println("dim: "+dim[0]+","+dim[1]+","+dim[2]+","+dim[3]);
			datatype=bb.getShort(70);
			//System.out.println("datatype: "+datatype);
			pixdim[0]=bb.getFloat(76);
			pixdim[1]=bb.getFloat(80);
			pixdim[2]=bb.getFloat(84);
			pixdim[3]=bb.getFloat(88);
			//System.out.println("pixdim: "+pixdim[0]+","+pixdim[1]+","+pixdim[2]+","+pixdim[3]);
			
			byte ext[]=new byte[4];
			ext[0]=bb.get(348);
			ext[1]=bb.get(349);
			ext[2]=bb.get(350);
			ext[3]=bb.get(351);
			//System.out.println("ext:"+ext[0]+","+ext[1]+","+ext[2]+","+ext[3]);
			if(ext[0]==1)
			{
				int	extSize;
				dis.readFully(b,0,8);
				extSize=bb.getInt(0);
				//System.out.println("extsize:"+extSize);
				dis.skip(extSize-8);
			}
			
			volume=new byte[dim[1]*dim[2]*dim[3]*bytesPerVoxel()];
			dis.readFully(volume,0,volume.length);
			
			dis.close();
			
			// Get bounding box
			boundingBox[0]=dim[1];	// min i
			boundingBox[1]=0;		// max i
			boundingBox[2]=dim[2];	// min j
			boundingBox[3]=0;		// max j
			boundingBox[4]=dim[3];	// min k
			boundingBox[5]=0;		// max k
			float	val;
			for(int i=0;i<dim[1];i+=5)		// there's no need to scan all voxels...
			for(int j=0;j<dim[2];j+=5)
			for(int k=0;k<dim[3];k+=5)
			{
				val=getValue(i,j,k);
				if(val>0)
				{
					if(i<boundingBox[0])	boundingBox[0]=i;
					if(i>boundingBox[1])	boundingBox[1]=i;
					if(j<boundingBox[2])	boundingBox[2]=j;
					if(j>boundingBox[3])	boundingBox[3]=j;
					if(k<boundingBox[4])	boundingBox[4]=k;
					if(k>boundingBox[5])	boundingBox[5]=k;
				}
			}
		}
		catch(IOException e)
		{
			err=1;
		}
		
		return err;
	}
	
	public short[] getDim(){
		
		return this.dim;
	}
	
	public float[] getPixdim(){
		
		return this.pixdim;
	}
	
	public float getValue(int i)
	{
		// get value at voxel with absolute index i
		ByteBuffer		bb=ByteBuffer.wrap(volume);
		bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		float	v=0;
		switch(datatype)
		{
			case 2://DT_UINT8
				v=bb.get(i);
				break;
			case 4://DT_INT16
				v=bb.getShort(2*i);
				break;
			case 8://DT_INT32
				v=bb.getInt(4*i);
				break;
			case 16://DT_FLOAT32
				v=bb.getFloat(4*i);
				break;
		}
		return v;
	}
	public float getValue(int i, int j, int k)
	// get value at voxel with index coordinates i,j,k
	{
		ByteBuffer		bb=ByteBuffer.wrap(volume);
		bb.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		float	v=0;
		if(i>=dim[1]||j>=dim[2]||k>=dim[3])
			return v;
		switch(datatype)
		{
			case 2://DT_UINT8
				v=bb.get(k*dim[2]*dim[1]+j*dim[1]+i);
				break;
			case 4://DT_INT16
				v=bb.getShort(2*(k*dim[2]*dim[1]+j*dim[1]+i));
				break;
			case 8://DT_INT32
				v=bb.getInt(4*(k*dim[2]*dim[1]+j*dim[1]+i));
				break;
			case 16://DT_FLOAT32
				v=bb.getFloat(4*(k*dim[2]*dim[1]+j*dim[1]+i));
				break;
		}
		return v;
	}
    public MyVolume(String filename)
    {
    	volume=null;
    	int	err;
    	err=loadNiftiVolume(filename);
    	name=filename;
    }
}
