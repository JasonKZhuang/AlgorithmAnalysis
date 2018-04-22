package nearestNeigh;
/***********************************************************************
 * COSC2123_1710 - Algorithms and Analysis
 * Semester 1 2017 Assignment #1 
 * Partner 1 full Name        : Kaizhi.Zhuang
 * Partner 1 Student Number   : s3535252
 * Partner 2 full Name        : Yang Xu
 * Partner 2 Student Number   : s3577404
 * Course Code      : COSC2123_1710
 * Skeleton code provided by Jeffrey, Youhan
 **********************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class is required to be implemented. Kd-tree implementation.
 * 
 * @author Jeffrey, Youhan
 */
public class KDTreeNN implements NearestNeigh
{
	public static long addTime = 0;
	
	public static long delTime = 0;
	
	// the initial dimension value
	private int initDimension = 0;

	// the root Node of Kd-Tree-Restaurant
	private static Node rootNodeRestaurant;
	// the root Node of Kd-Tree-Hospital
	private static Node rootNodeHospital;
	// the root Node of Kd-Tree-Education
	private static Node rootNodeEducation;

	// all data list
	public static ArrayList<Point> pointsList;
	// Restaurant data list
	public static ArrayList<Point> pointsListRestaurant;
	// Hospital data list
	public static ArrayList<Point> pointsListHospital;
	// Education data list
	public static ArrayList<Point> pointsListEducation;

	@Override
	public void buildIndex(List<Point> points)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
		
		rootNodeRestaurant = new Node();
		rootNodeHospital   = new Node();
		rootNodeEducation  = new Node();
		pointsList = (ArrayList<Point>) points;
		pointsListRestaurant = new ArrayList<Point>();
		pointsListHospital   = new ArrayList<Point>();
		pointsListEducation  = new ArrayList<Point>();
		for (int i=0;i<pointsList.size();i++)
		{
			Point p = pointsList.get(i);
			if (p.cat==Category.HOSPITAL)
			{
				pointsListHospital.add(p);
			}
			if (p.cat==Category.EDUCATION)
			{
				pointsListEducation.add(p);
			}
			if (p.cat==Category.RESTAURANT)
			{
				pointsListRestaurant.add(p);
			}
		}
		
		rootNodeRestaurant.partitionDimension = initDimension;
		rootNodeRestaurant.parentNode = null;
		rootNodeHospital.partitionDimension   = initDimension;
		rootNodeHospital.parentNode   = null;
		rootNodeEducation.partitionDimension  = initDimension;
		rootNodeEducation.parentNode  = null;
		
		buildTree(rootNodeRestaurant, pointsListRestaurant,null);
		buildTree(rootNodeHospital, pointsListHospital,null);
		buildTree(rootNodeEducation, pointsListEducation,null);
		
		long endTime   = System.currentTimeMillis();
		System.out.println("Building Index using Kd-Tree method, which spends "
		                + (endTime-startTime)+" milli seconds.");
		
		//KDTreeUtilities.inorder(rootNodeRestaurant);
		//KDTreeUtilities.inorder(rootNodeHospital);
		//KDTreeUtilities.inorder(rootNodeEducation);
	}

	@SuppressWarnings("unused")
	@Override
	public List<Point> search(Point searchTerm, int k)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
        //System.out.println("Using Kd-Tree method to add new Points,which spends "+ addTime +" milliseconds.");
        //System.out.println("Using Kd-Tree method to delete Points,which spends "+ delTime +" milliseconds.");
		addTime = 0;
		delTime = 0;
		
		ArrayList<Point> nearestList = new ArrayList<Point>();
		// save the path of searching
		Stack<Node> stack = new Stack<Node>();
		
		double nearest_distance = 0;
		Node   nearest_node;
		Node leafNode = new Node() ;
		
		// find leaf node
		switch (searchTerm.cat )
		{
			case RESTAURANT:
				leafNode = searchDFS(rootNodeRestaurant, searchTerm, stack);
				break;
			case HOSPITAL:
				leafNode = searchDFS(rootNodeHospital, searchTerm, stack);
				break;
			case EDUCATION:
				leafNode = searchDFS(rootNodeEducation, searchTerm, stack);
				break;
			default:
				break;
		}
		
		// calculating the distance between leaf node and target node
		nearest_distance = leafNode.value.distTo(searchTerm);
		
		// press the leaf point into new stack
		addNearestPointToList(nearestList, leafNode, searchTerm, k);
		//nearestList.add(leafNode.value);
		
		// store the nearest Node
		nearest_node = leafNode;
		
		Node unWindNodeOld = leafNode;
		
		// iterate stack
		while (stack.size() != 0)
		{
			Node unWindNode = stack.pop();
			
			// the same node
			if (unWindNode.value.equals(leafNode.value))
			{
				continue;
			}
			
			addNearestPointToList(nearestList, unWindNode, searchTerm, k);
			//nearestList.add(unWindNode.value);
			double dis_Temp_target = unWindNode.value.distTo(searchTerm);
			
			if (unWindNode.partitionDimension == 0)
			{
				double dis_x = Math.abs(searchTerm.lat - unWindNode.value.lat);
				// if it is less than nearest distance
				if (dis_x <= dis_Temp_target)
				{
					// determine which space will go into
					
					if (unWindNodeOld.value.lat < unWindNode.value.lat)
					{
						// turn right
						if (unWindNode.rightNode !=null)
						{
							leafNode = searchDFS(unWindNode.rightNode,searchTerm, stack);
							addNearestPointToList(nearestList, leafNode, searchTerm, k);
							//nearestList.add(leafNode.value);
						}
					} else
					{
						// turn left
						if (unWindNode.leftNode != null)
						{
							leafNode = searchDFS(unWindNode.leftNode,searchTerm, stack);
							addNearestPointToList(nearestList, leafNode, searchTerm, k);
							//nearestList.add(leafNode.value);
						}
					}
				}
			} else
			{
				double dis_y = Math.abs(searchTerm.lon - unWindNode.value.lon);
				// if it is less than nearest distance
				if (dis_y <= dis_Temp_target)
				{
					// determine which space will go into
					if (unWindNodeOld.value.lon < unWindNode.value.lon)
					{
						// turn right
						if (unWindNode.rightNode !=null)
						{
							leafNode = searchDFS(unWindNode.rightNode,searchTerm, stack);
							addNearestPointToList(nearestList, leafNode, searchTerm, k);
							//nearestList.add(leafNode.value);
						}
					} else
					{
						// turn left
						if (unWindNode.leftNode != null)
						{
							leafNode = searchDFS(unWindNode.leftNode,searchTerm, stack);
							addNearestPointToList(nearestList, leafNode, searchTerm, k);
							//nearestList.add(leafNode.value);
						}
					}
				}
			}
			unWindNodeOld = unWindNode;
		}
		long endTime   = System.currentTimeMillis();
	    System.out.println("Using Kd-Tree method to search k nearest points,which spends "
	                + (endTime-startTime)+" milli seconds.");
	        
		return nearestList;
	}

	@Override
	public boolean addPoint(Point point)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
		
		Node tempNode = new Node();
		switch (point.cat)
		{
			case RESTAURANT:
				tempNode = rootNodeRestaurant;
				break;
			case HOSPITAL:
				tempNode = rootNodeHospital;
				break;
			case EDUCATION:
				tempNode = rootNodeEducation;
				break;
			default:
				break;
		}
		
		Node newNode = new Node(point, null, null);
		newNode.isLeaf = true;
		while (tempNode != null)
		{
			if (tempNode.value.equals(point))
			{
				return false;
			}
			
			if (tempNode.partitionDimension == 0)
			{
				if (point.lat < tempNode.value.lat)
				{
					if (tempNode.leftNode == null)
					{
						break;
					} else
					{
						tempNode = tempNode.leftNode;
					}
				} else
				{
					if (tempNode.rightNode == null)
					{
						break;
					} else
					{
						tempNode = tempNode.rightNode;
					}
				}
			} else
			{
				if (point.lon < tempNode.value.lon)
				{
					if (tempNode.leftNode == null)
					{
						break;
					} else
					{
						tempNode = tempNode.leftNode;
					}
				} else
				{
					if (tempNode.rightNode == null)
					{
						break;
					} else
					{
						tempNode = tempNode.rightNode;
					}
				}
			}
		}

		if (tempNode.value.equals(point))
		{
			return false;
		}

		tempNode.isLeaf = false;

		if (tempNode.partitionDimension == 0)
		{
			newNode.partitionDimension = 1;
		} else
		{
			newNode.partitionDimension = 0;
		}

		switch (tempNode.partitionDimension)
		{
			case 0:
				if (point.lat < tempNode.value.lat)
				{
					tempNode.leftNode = newNode;
				} else
				{
					tempNode.rightNode = newNode;
				}
				break;
			case 1:
				if (point.lon < tempNode.value.lon)
				{
					tempNode.leftNode = newNode;
				} else
				{
					tempNode.rightNode = newNode;
				}
				break;
			default:
				break;
		}

		newNode.parentNode = tempNode;

		long endTime   = System.currentTimeMillis();
		
		addTime = addTime + (endTime-startTime);
		
//        System.out.println("Using Kd-Tree method to add a new Point(id="+point.id+"),which spends "
//                + (endTime-startTime)+" milli seconds.");
        
		return true;
	}

	@Override
	public boolean deletePoint(Point point)
	{
		// To be implemented.
		/*
		if (point.id.equals("id593"))
		{
			System.out.println("delete 593");
		}
		*/
		long startTime = System.currentTimeMillis();
		boolean retFlag = false;
		boolean foundFlag = false;
		Node tempNode = new Node();
		switch (point.cat)
		{
			case RESTAURANT:
				tempNode = rootNodeRestaurant;
				break;
			case HOSPITAL:
				tempNode = rootNodeHospital;
				break;
			case EDUCATION:
				tempNode = rootNodeEducation;
				break;
			default:
				break;
		}		

		while (tempNode != null)
		{
			if (tempNode.value.equals(point))
			{
				foundFlag = true;
				break;
			}
			switch (tempNode.partitionDimension)
			{
				case 0:
					if (point.lat < tempNode.value.lat)
					{
						tempNode = tempNode.leftNode;
					} else
					{
						tempNode = tempNode.rightNode;
					}
					break;
				case 1:
					if (point.lon < tempNode.value.lon)
					{
						tempNode = tempNode.leftNode;
					} else
					{
						tempNode = tempNode.rightNode;
					}
					break;
				default:
					break;
			}
		}

		ArrayList<Point> tempList = new ArrayList<Point>();
		
		//if the point has been found
		if (foundFlag)
		{
			if (tempNode.isLeaf)
			{
				switch (tempNode.parentNode.partitionDimension)
				{
					case 0:
						if (tempNode.value.lat < tempNode.parentNode.value.lat)
						{
							tempNode.parentNode.leftNode = null;
						}else
						{
							tempNode.parentNode.rightNode = null;
						}
						break;
					case 1:
						if (tempNode.value.lon < tempNode.parentNode.value.lon)
						{
							tempNode.parentNode.leftNode = null;
						}else
						{
							tempNode.parentNode.rightNode = null;
						}
						break;
					default:
						break;
				}
				if (tempNode.parentNode.leftNode == null 
				    && tempNode.parentNode.rightNode == null)
				{
					tempNode.parentNode.isLeaf = true;
				}
				
			} else
			{
				// put all children node of tempnode into tempList;
				traverseKdTree(tempNode, tempList);
				//delete the point
				tempList.remove(0);
				
				//define the new root Node
				Node newRootNode = new Node();
				
				newRootNode.partitionDimension = tempNode.partitionDimension;
				
				// build a new children tree which parentNode of tempNode is as
				// rootNode
				//the parentNode could be null
				Node parentNode = tempNode.parentNode;
				
				buildTree(newRootNode, tempList, parentNode);
				
				if (parentNode == null)//delete root 
				{
					switch (point.cat)
					{
						case RESTAURANT:
							rootNodeRestaurant = newRootNode;
							break;
						case HOSPITAL:
							rootNodeHospital = newRootNode;
							break;
						case EDUCATION:
							rootNodeEducation = newRootNode;
							break;
						default:
							break;
					}		
				}else
				{
					newRootNode.parentNode = parentNode;
				
					if (parentNode.partitionDimension == 0)
					{
						if (tempNode.value.lat < parentNode.value.lat)
						{
							parentNode.leftNode = newRootNode;
						}else
						{
							parentNode.rightNode = newRootNode;
						}
					}else
					{
						if (tempNode.value.lon < parentNode.value.lon)
						{
							parentNode.leftNode = newRootNode;
						}else
						{
							parentNode.rightNode = newRootNode;
						}
					}
				}
			}
			retFlag = true;
		} else
		{
			retFlag =  false;
		}
		
		long endTime   = System.currentTimeMillis();
		
		delTime = delTime + (endTime-startTime);
		
//        System.out.println("Using Kd-Tree method to delete a Point(id="+point.id+"),which spends "
//                + (endTime-startTime)+" milli seconds.");
		
		return retFlag;
	}

	@Override
	public boolean isPointIn(Point point)
	{
		// To be implemented.
		long startTime = System.currentTimeMillis();
		
		boolean retFlag = false;
		
		Node tempNode = new Node();
		switch (point.cat)
		{
			case RESTAURANT:
				tempNode = rootNodeRestaurant;
				break;
			case HOSPITAL:
				tempNode = rootNodeHospital;
				break;
			case EDUCATION:
				tempNode = rootNodeEducation;
				break;
			default:
				break;
		}	
		
		double targetDimensionalValue = 0;
		double nodeDimensionalValue = 0;
		while (tempNode != null)
		{
			if (tempNode.value.equals(point))
			{
				retFlag = true;
				break;
			}
			switch (tempNode.partitionDimension)
			{
				case 0: // compare with x Dimension
					nodeDimensionalValue = tempNode.value.lat;
					targetDimensionalValue = point.lat;
					break;
				case 1: // compare with y Dimension
					nodeDimensionalValue = tempNode.value.lon;
					targetDimensionalValue = point.lon;
					break;
				default:
					break;
			}

			if (targetDimensionalValue < nodeDimensionalValue) // left tree
			{
				if (tempNode.leftNode == null)
				{
					if (tempNode.rightNode != null)
					{
						tempNode = tempNode.rightNode;
					}else
					{
						break;
					}
				} else
				{
					tempNode = tempNode.leftNode;
				}
			} else
			{
				if (tempNode.rightNode == null)
				{
					if (tempNode.leftNode != null)
					{
						tempNode = tempNode.leftNode;
					}else
					{
						break;
					}
				} else
				{
					tempNode = tempNode.rightNode;
				}
			}

		}
		
		long endTime   = System.currentTimeMillis();
        System.out.println("Using Kd-Tree method to check whether a Point is in the tree,"
        		+ " which spends " + (endTime-startTime)+" milli seconds.");
		return retFlag;
	}

	
	private void buildTree(Node node, ArrayList<Point> pList, Node argParentNode)
	{
		int listLength = pList.size();
		if (listLength == 0)
		{
			return;
		}
		if (listLength == 1)
		{
			node.isLeaf = true;
			node.value = pList.get(0);
			node.parentNode = argParentNode;
			return;
		}

		//KDTreeUtilities.doSelectionSort(pList, node.partitionDimension);
		int midIndex = KDTreeUtilities.getMiddleIndex(pList, node.partitionDimension);
		
		if (node.value == null)
		{
			node.value = pList.get(midIndex);
			
			if (node.partitionDimension == 0)
			{
				node.partitionValue = node.value.lat;
			} else
			{
				node.partitionValue = node.value.lon;
			}
		}
		/*
		if (node.value.id.equals("id839"))
		{
			System.out.println("id839");
		}
		if (node.value.id.equals("id700"))
		{
			System.out.println("id700");
		}
		*/
		ArrayList<Point> leftTreeList  = new ArrayList<Point>();
		ArrayList<Point> rightTreeList = new ArrayList<Point>();

		for(int i=0;i<pList.size();i++)
		{
			Point point = pList.get(i);
			if (i<midIndex)
			{
				leftTreeList.add(point);
			}else if (i>midIndex)
			{
				rightTreeList.add(point);
			}
		}
		
		Node leftNode  = new Node();
		Node rightNode = new Node();

		int left_listLength  = leftTreeList.size();
		int right_listLength = rightTreeList.size();

		if (left_listLength > 0)
		{
			if (node.partitionDimension == 0)
			{
				leftNode.partitionDimension = 1;
			} else
			{
				leftNode.partitionDimension = 0;
			}
			node.leftNode = leftNode;
			node.isLeaf = false;
			leftNode.parentNode = node;
			buildTree(leftNode, leftTreeList , node );
		}
		//
		if (right_listLength > 0)
		{
			if (node.partitionDimension == 0)
			{
				rightNode.partitionDimension = 1;
			} else
			{
				rightNode.partitionDimension = 0;
			}
			node.rightNode = rightNode;
			node.isLeaf = false;
			rightNode.parentNode = node;
			buildTree(rightNode, rightTreeList, node);
		}

	}
	
	/**
	 * find the nearest leaf node via DFS
	 * @param startNode
	 * @param searchTerm
	 * @param argStack
	 * @return
	 */
	private Node searchDFS(Node startNode, Point searchTerm, Stack<Node> argStack)
	{
		Node tempNode = startNode;
		double targetDimensionalValue = 0;
		double nodeDimensionalValue = 0;
		while (tempNode != null)
		{
			switch (tempNode.partitionDimension)
			{
				case 0: // compare with x Dimension
					nodeDimensionalValue   = tempNode.value.lat;
					targetDimensionalValue = searchTerm.lat;
					break;
				case 1: // compare with y Dimension
					nodeDimensionalValue   = tempNode.value.lon;
					targetDimensionalValue = searchTerm.lon;
					break;
				default:
					break;
			}

			argStack.add(tempNode);

			if (targetDimensionalValue < nodeDimensionalValue) // left tree
			{
				if (tempNode.leftNode == null)
				{
					if (tempNode.rightNode != null)
					{
						tempNode = tempNode.rightNode;
					}else
					{
						break;
					}
				} else
				{
					tempNode = tempNode.leftNode;
				}
				// System.out.println("left");
			} else
			{
				if (tempNode.rightNode == null)
				{
					if (tempNode.leftNode != null)
					{
						tempNode = tempNode.leftNode;
					}else
					{
						break;
					}
				} else
				{
					tempNode = tempNode.rightNode;
				}
				// System.out.println("right");
			}

		}
		
		return tempNode;
	}

	private void traverseKdTree(Node argRootNode, ArrayList<Point> argPointList)
	{
		if (argRootNode != null)
		{
			argPointList.add(argRootNode.value);
			traverseKdTree(argRootNode.leftNode, argPointList);
			traverseKdTree(argRootNode.rightNode, argPointList);
		}
	}
	
	private void addNearestPointToList(ArrayList<Point> list,Node node,Point target, int k)
	{
		int listSize = list.size();
		if (listSize == 0)
		{
			list.add(node.value);
			return;
		}
		
		double disNewNode = node.value.distTo(target);
		double disTmpNode ;
		for(int i = 0;i<listSize;i++)
		{
			Point tempPoint = list.get(i);
			disTmpNode = tempPoint.distTo(target);
			if (disNewNode<disTmpNode)
			{
				list.add(i, node.value);
				break;
			}else
			{
				if (i==(listSize-1))
				{
					list.add(listSize,node.value);
					break;
				}
			}
		}
	
		if (list.size()==k+1)
		{
			list.remove(k);
		}
	}

}
