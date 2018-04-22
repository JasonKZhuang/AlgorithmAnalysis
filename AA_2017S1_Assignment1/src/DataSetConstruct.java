
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
public class DataSetConstruct
{
	//"-50" "-1" "1" "200" 20000 "restaurant"
	public static void main(String[] args)
	{
		int argSize = args.length;
		if (argSize != 6)
		{
			System.out.println("require input 6 arguments:\n"
					+ "Minnum x : Maxinum x \n"
					+ "Minnum y : Maxinum y \n"
					+ "the number of dataset \n"
					+ "the category of dataset.");
					
			return;
		}
		for (int i=0;i<argSize;i++)
		{
			System.out.println("arg["+i+"]:"+args[i]);
		}
		double xMin=0d,xMax=0d,yMin=0d,yMax=0d;
		int    pCount = 0;
		String cat="";
		if (  args[0]==null 
		   || args[1]==null 
		   || args[2]==null
		   || args[3]==null
		   || args[4]==null
		   || args[5]==null)
		{
			System.out.println("require input 6 arguments");
			return;
		}
		
		xMin = Double.parseDouble(args[0]);
		xMax = Double.parseDouble(args[1]);
		yMin = Double.parseDouble(args[2]);
		yMax = Double.parseDouble(args[3]);
		pCount = Integer.parseInt(args[4]);
		cat    = args[5].toLowerCase();
		double[] xDimension = randomArray(xMin, xMax, pCount);
		double[] yDimension = randomArray(yMin, yMax, pCount);
		
		String outputFileName = "sampleData20000.txt";
		File outputFile = new File(outputFileName);
		FileWriter  fWriter;
		String outputPoint;
		BufferedWriter out;
		try
		{
			fWriter = new FileWriter(outputFile);
			out = new BufferedWriter(fWriter);
			for(int i=0;i<pCount;i++)
			{
				outputPoint = "id"+i+ " "
		                    + cat   + " " 
					        + xDimension[i] +" " 
					        + yDimension[i]+"\n" ;
				out.write(outputPoint);
			}
			out.flush();
			out.close();
	        
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}catch (Exception e)
		{
			e.printStackTrace();
		} 

	}
	
	public static double[] randomArray(double rangeMin,double rangeMax,int n)
	{  
	    if(rangeMax < rangeMin)
	    {  
	        return null;  
	    }  
	    double[] source = new double[n];
	    double[] result = new double[n]; 
	    Random rd = new Random();
	    for (int i = 0; i < n; i++)
	    {  
	       source[i] = rangeMin + (rangeMax - rangeMin) * rd.nextDouble();  
	    }
	    boolean existFlag = false;
	    double rData = 0d;
	    for(int i=0;i<n;i++)
	    {
	    	existFlag = true;
	    	while(existFlag)
	    	{
	    		rData = rangeMin + (rangeMax - rangeMin) * rd.nextDouble(); 
	    		for(int j=0;j<n;j++)
	    		{
	    			if (rData == source[j])
	    			{
	    				existFlag = true;
	    				break;
	    			}else
	    			{
	    				existFlag = false;
	    			}
	    		}
	    	}
	    	result[i] = rData;
	    }
	    return result;  
	} 

}
