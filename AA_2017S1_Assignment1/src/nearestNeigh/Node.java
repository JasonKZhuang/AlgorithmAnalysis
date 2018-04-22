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

public class Node
{
	public Point  value;
	
	// the depth of Point, Root node is 0;
	public int    depth;
	
	//Whether it is a leaf node
	public boolean isLeaf=false;
    
    //left Tree Node
	public Node leftNode;
   
    //Right Tree Node
	public Node rightNode;
	
	//parent Node
	public Node parentNode;
	
	//0 is left child flag; 1 is right child flag
	public int childType;
    
	//which Dimension
	public int partitionDimension;

	//the value of partition
	public double partitionValue;
	
	//left==0;right==1
	public int searchIntoDirection;

	public Node()
	{
		super();
	}
	public Node(Point value, Node leftNode, Node rightNode)
	{
		this.value = value;
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	public Point getValue()
	{
		return value;
	}

	public void setValue(Point value)
	{
		this.value = value;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	public boolean isLeaf()
	{
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf)
	{
		this.isLeaf = isLeaf;
	}

	public Node getLeftNode()
	{
		return leftNode;
	}

	public void setLeftNode(Node leftNode)
	{
		this.leftNode = leftNode;
	}

	public Node getRightNode()
	{
		return rightNode;
	}

	public void setRightNode(Node rightNode)
	{
		this.rightNode = rightNode;
	}

	public Node getParentNode()
	{
		return parentNode;
	}

	public void setParentNode(Node parentNode)
	{
		this.parentNode = parentNode;
	}

	public int getChildType()
	{
		return childType;
	}

	public void setChildType(int childType)
	{
		this.childType = childType;
	}

	public int getPartitionDimension()
	{
		return partitionDimension;
	}

	public void setPartitionDimension(int partitionDimension)
	{
		this.partitionDimension = partitionDimension;
	}
	
	public double getPartitionValue()
	{
		return partitionValue;
	}
	public void setPartitionValue(double partitionValue)
	{
		this.partitionValue = partitionValue;
	}
	public int getSearchIntoDirection()
	{
		return searchIntoDirection;
	}

	public void setSearchIntoDirection(int searchIntoDirection)
	{
		this.searchIntoDirection = searchIntoDirection;
	}
	
	
	
}