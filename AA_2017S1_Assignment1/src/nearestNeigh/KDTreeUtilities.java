package nearestNeigh;
/***********************************************************************
 * COSC2123_1710 - Algorithms and Analysis
 * Semester 1 2017 Assignment #1 
 * Partner 1 full Name        : Kaizhi.Zhuang
 * Partner 1 Student Number   : s3535252
 * Partner 2 full Name        : Yang Xu
 * Partner 1 Student Number   : s3577404
 * Course Code      : COSC2123_1710
 * Skeleton code provided by Jeffrey, Youhan
 **********************************************************************/

import java.util.ArrayList;

public class KDTreeUtilities
{

	private static int i_loop=0;
	
	/**
	 * Sorts the input array.
	 * @param array  Array to be sorted.
	 */
	public static void doBubbleSort(int[] array)
	{

		for (int i = 0; i < array.length; i++)
		{
			for (int j = 0; j < array.length - 1; j++)
			{
				// check if we need to swap
				if (array[j] > array[j + 1])
				{
					Integer temp = array[j];
					array[j] = array[j + 1];
					array[j + 1] = temp;
				}
			}
		}

	} 

	/**
	 * using bubble to sort the distance 
	 * between target point to every point in the array list
	 * @param array
	 * @param targetPoint
	 */
	public static void doBubbleSort(ArrayList<Point> array, Point targetPoint)
	{
		double distance1,distance2;
		
		for (int i = 0; i < array.size(); i++)
		{
			for (int j = 0; j < array.size() - 1; j++)
			{
				// check if we need to swap
				distance1 = targetPoint.distTo(array.get(j));
				distance2 = targetPoint.distTo(array.get(j+1));
				if (distance1 > distance2)
				{
					Point temp = array.get(j);
					array.set(j, array.get(j + 1));
					array.set(j + 1, temp);
				}
			}
		}
	} 
	
	
	/**
	 * implement Selection Sort algorithm
	 * 
	 * @param arr
	 * @return
	 */
	public static double[] doSelectionSort(double[] arr)
	{
		for (int i = 0; i < arr.length - 1; i++)
		{
			int index = i;
			for (int j = i + 1; j < arr.length; j++)
			{
				if (arr[j] < arr[index])
				{
					index = j;
				}
			}
			double smallerNumber = arr[index];
			arr[index] = arr[i];
			arr[i] = smallerNumber;
		}
		return arr;
	}

	/**
	 * implement Selection Sort algorithm
	 * 
	 * @param arr
	 * @param dimension
	 *            x or y
	 * @return
	 */
	public static void doSelectionSort(ArrayList<Point> arr, int dimension)
	{
		// x dimension
		if (dimension == 0)
		{
			for (int i = 0; i < arr.size() - 1; i++)
			{
				int index = i;
				for (int j = i + 1; j < arr.size(); j++)
				{
					if (arr.get(j).lat < arr.get(index).lat)
					{
						index = j;
					}
				}
				Point smaller = arr.get(index);
				arr.set(index, arr.get(i));
				arr.set(i, smaller);
			}

		}

		if (dimension == 1)
		{
			for (int i = 0; i < arr.size() - 1; i++)
			{
				int index = i;
				for (int j = i + 1; j < arr.size(); j++)
				{
					if (arr.get(j).lon < arr.get(index).lon)
					{
						index = j;
					}
				}
				Point smaller = arr.get(index);
				arr.set(index, arr.get(i));
				arr.set(i, smaller);
			}
		}
	}
	
	/**
	 * implement Insert Sort algorithm
	 * @param arr
	 * @return
	 */
	public static ArrayList<Point> doInsertSort(ArrayList<Point> arr, int dimension)
	{
		int length = arr.size();
		int j;
		int i;
		Point key;
		if (dimension==0)
		{
			for(j=1;j<length;j++)
			{
				key=arr.get(j);
				i=j-1;
				while(i>=0 && arr.get(i).lat >key.lat)
				{
					arr.set(i+1, arr.get(i));
					i--;
				}
				arr.set(i+1, key);
			}
		}
		
		if (dimension==1)
		{
			for(j=1;j<length;j++)
			{
				key=arr.get(j);
				i=j-1;
				while(i>=0 && arr.get(i).lon >key.lon)
				{
					arr.set(i+1, arr.get(i));
					i--;
				}
				arr.set(i+1, key);
			}
		}
		
		return arr;
	}
	
	
	
	/**
	 * implement Insert Sort algorithm
	 * @param arr
	 * @return
	 */
	public static double[] doInsertSort(double[] arr)
	{
		int length = arr.length;
		int j;
		int i;
		double key;
		
		for(j=1;j<length;j++)
		{
			key=arr[j];
			i=j-1;
			while(i>=0 && arr[i]>key)
			{
				arr[i+1] = arr[i];
				i--;
			}
			arr[i+1] = key;
		}
		return arr;
	}
	
	
	/**
	 * print point and distance
	 * 
	 * @param arg_pointList
	 * @param search_point
	 * @param k
	 */
	public static void DisplayPointDistance(ArrayList<Point> arg_pointList,
			Point search_point, int k)
	{
		System.out.println("============================================");
		System.out.println("Searche [Point: id:" + search_point.id + "]"
				+ "[Category:" + search_point.cat + "]+[" + search_point.lat
				+ "," + search_point.lon + "]");
		for (int i = 0; i < k; i++)
		{
			Point temPoint = arg_pointList.get(i);
			System.out.print("[Nearest Point Id:" + temPoint.id + "]");
			System.out.print("[Category:" + temPoint.cat + "]");
			System.out.println("[distance:" + temPoint.distTo(search_point)	+ "]");
		}
	}
	
	public static int getMiddleIndex(ArrayList<Point> pList, int dimension)
	{
		int retIndex = 0;
		double min = 0;
		double max = 0;
		double avg = 0;
		int listSize = 0;
		if (pList.size() == 0)
			return 0;
		else
			listSize = pList.size();
		
		KDTreeUtilities.doSelectionSort(pList,dimension);
		
		if (dimension == 0)
		{
			min = pList.get(0).lat;
			max = pList.get(pList.size() - 1).lat;
			avg = max - (max - min)/2;
			double minDis = Math.abs(pList.get(0).lat - avg);
			for (int i=0;i<listSize;i++)
			{
				double tempDis = Math.abs(pList.get(i).lat - avg);
				if (tempDis < minDis)
				{
					retIndex = i;
					minDis = tempDis;
				}
			}
			
		}else
		{
			min = pList.get(0).lon;
			max = pList.get(pList.size() - 1).lon;
			avg = max - (max - min)/2;
			double minDis = Math.abs(pList.get(0).lon - avg);
			for (int i=0;i<listSize;i++)
			{
				double tempDis = Math.abs(pList.get(i).lon - avg);
				if (tempDis < minDis);
				{
					retIndex = i;
					minDis = tempDis;
				}
			}
		}
		
		return retIndex;
	}

    /**
     * Inorder traversal of tree.
     *
     * @param root Root node of tree.
     */
	public static void inorder(Node root) {
		if (root == null) {
			return;
		}
		i_loop = i_loop + 1;
		inorder(root.leftNode);
		// In general, might be better to use a function object here, but for this lab, we just print to stdout
		System.out.print(root.value.id + " ");
		inorder(root.rightNode);
	} // end of inorder()
	
	
	public static int getLoop()
	{
		return i_loop;
	}

}
