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
import java.util.Iterator;
import java.util.List;

/**
 * This class is required to be implemented. Naive approach implementation.
 * 
 * @author Jeffrey, Youhan
 */
public class NaiveNN implements NearestNeigh
{
	public static ArrayList<Point> pointsList;
	
	public static long addTime = 0;
	
	public static long delTime = 0;

	@Override
	public void buildIndex(List<Point> points)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
		pointsList = (ArrayList<Point>) points;
		long endTime   = System.currentTimeMillis();
		System.out.println("Building Index using Naive method, which spends "
		                + (endTime-startTime)+" milli seconds.");
	}

	@Override
	public List<Point> search(Point searchTerm, int k)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
        //System.out.println("Using Naive method to add new Points,which spends "+ addTime +" milliseconds.");
        //System.out.println("Using Naive method to delete Points,which spends "+ delTime +" milliseconds.");
		addTime = 0;
		delTime = 0;
		
		ArrayList<Point> returnList = new ArrayList<Point>();
		double distance1=0.0d;
		double distance2=0.0d;
		int len = pointsList.size();
		
		//implement Selection Sort algorithm
        for (int i = 0; i < len - 1; i++)
        {
            int index = i;
            for (int j = i + 1; j < len; j++)
            {
            	Point p1 = pointsList.get(j);
            	if (!p1.cat.equals(searchTerm.cat))
            	{
            		continue;
            	}
            	Point p2 = pointsList.get(index);
            	if (!p2.cat.equals(searchTerm.cat))
            	{
            		continue;
            	}
            	distance1 =p1.distTo(searchTerm);
            	distance2 =p2.distTo(searchTerm);
            	if (distance1 < distance2)
            	{
            		index = j;
            	}            	
            }
            Point smallerPoint = pointsList.get(index); 
            pointsList.set(index, pointsList.get(i));
            pointsList.set(i, smallerPoint);
        }
        //KDTreeUtilities.DisplayPointDistance(pointsList,searchTerm,k);
        int count = 0;
        for(int i =0;i<pointsList.size();i++)
        {
        	if (count == k )
        	{
        		break;
        	}
        	Point p = pointsList.get(i);
        	if (p.cat==searchTerm.cat)
        	{
        		returnList.add(pointsList.get(i));
        		count = count + 1;
        	}
        }
        long endTime   = System.currentTimeMillis();
        System.out.println("Using Naive method to search k nearest points,which spends "
                + (endTime-startTime)+" milli seconds.");
		return returnList;
	}

	@Override
	public boolean addPoint(Point point)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
		
		boolean find = false;
		for(int i=0;i<pointsList.size();i++)
		{
			Point temPoint = pointsList.get(i);
			if (temPoint.equals(point))
			{
				find = true;
				break;
			}
		}
		
		if (find==true)
		{
			return false;
		}
		
		long endTime   = System.currentTimeMillis();
//        System.out.println("Using Naive method to add a new Point(id="+point.id+"),which spends "
//                + (endTime-startTime)+" milli seconds.");
		addTime = addTime + (endTime-startTime);
		
		return pointsList.add(point);
	}

	@Override
	public boolean deletePoint(Point point)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
		
		boolean retValue = false;
		for(int i=0;i<pointsList.size();i++)
		{
			Point temPoint = pointsList.get(i);
			if (temPoint.equals(point))
			{
				pointsList.remove(i);
				retValue = true;
				break;
			}
		}
		
		long endTime   = System.currentTimeMillis();
		
		delTime = delTime + (endTime-startTime);
		
//        System.out.println("Using Naive method to delete a Point(id="+point.id+"),which spends "
//                + (endTime-startTime)+" milli seconds.");
		return retValue;
	}

	@Override
	public boolean isPointIn(Point point)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
		
		boolean retFlag = false;
		for (Iterator<Point> it = pointsList.iterator(); it.hasNext();)
		{
			Point p = (Point) it.next();
			if (p.equals(point))
			{
				retFlag = true;
				break;
			}
		}
		
		long endTime   = System.currentTimeMillis();
        System.out.println("Using Naive method to check whether a Point is in the list,which spends "
                + (endTime-startTime)+" milli seconds.");
		
		return retFlag;
	}
	
}
