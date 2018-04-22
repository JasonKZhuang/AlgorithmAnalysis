package player;

import ship.AircraftCarrier;
import ship.Battleship;
import ship.Cruiser;
import ship.Destroyer;
import ship.Ship;
import ship.Submarine;
import world.World;

/**
 * Monte Carlo guess player (task C).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class MonteCarloGuessPlayer  implements Player
{
	//the number of ships
	private static final int shipsCount = 5;
	//the count number of row
	private int rowCount = 0;
	//the count number of column
	private int colCount = 0;
	//inner class ship
	private class MyInnerShip
	{
		Ship ship = null;

		int[] rowIndex = { -1, -1, -1, -1, -1 };

		int[] clnIndex = { -1, -1, -1, -1, -1 };

		boolean[] isHitted = { true, true, true, true, true };

		private MyInnerShip()
		{

		}
	}
	//the array of the inner ships
	private MyInnerShip[] myShips;
	
	//==============Opposite World==================//
	private class OppositeInnerShip
	{
		Ship ship = null;
		
		boolean isSunk = false;
		
		int [][] cells_probability_byShip;
		
		private OppositeInnerShip()
		{

		}
	}
	//the array of the Opposite inner ships
	private OppositeInnerShip[] oppositeShips;

	//the array of the guess of opposited cell
	private boolean[][] isOppositeCellGuesed;
	//the array of the hit of opposited cell
	private boolean[][] isOppositeCellHitted;
	//the array of the posibilities of all the ships
	int [][] cells_probability_total;
	
	//the hitted point X
	private int centerHitX  = 0;
	//the hitted point Y
	private int centerHitY  = 0;
	//the game state: 0 is hunting model, 1 is targeting model
	private int gameState = 0;//hunting 
	//when hitted to count how many fires have been done, the maximum is 4
	private int fireCount  = 0;
	//the direction left top right bottom
	private int fireDirection  = 0;
	//privious fire cell sate
	private int previous_state = 0;
	private int previous_x = 0;
	private int previous_y = 0;
	
	
	
	@Override
    public void initialisePlayer(World world) 
    {
		this.rowCount = world.numRow;
		this.colCount = world.numColumn;
		this.myShips  = new MyInnerShip[shipsCount];

		//initialize my ships
		int i = 0;
		for (World.ShipLocation tempShipLocation : world.shipLocations)
		{
			Ship tempShip = tempShipLocation.ship;
			MyInnerShip myShip = new MyInnerShip();
			myShip.ship = tempShip;

			for (int j = 0; j < tempShip.len(); j++)
			{
				myShip.rowIndex[j] = tempShipLocation.coordinates.get(j).row;
				myShip.clnIndex[j] = tempShipLocation.coordinates.get(j).column;
				myShip.isHitted[j] = false;
			}
			this.myShips[i] = myShip;
			i = i + 1;
		}

		/////initialize enemy ships
		this.oppositeShips = new OppositeInnerShip[shipsCount];
		for(i=0;i<shipsCount;i++)
		{
			this.oppositeShips[i] = new OppositeInnerShip();
			switch (i)
			{
				case 0:
					this.oppositeShips[i].ship = new AircraftCarrier();
					break;
				case 1:
					this.oppositeShips[i].ship = new Battleship();
					break;
				case 2:
					this.oppositeShips[i].ship = new Submarine();
					break;
				case 3:
					this.oppositeShips[i].ship = new Cruiser();
					break;
				case 4:
					this.oppositeShips[i].ship = new Destroyer();
					break;
				default:
					break;
			}
			this.oppositeShips[i].isSunk = false;
			
			this.oppositeShips[i].cells_probability_byShip = new int[rowCount][colCount];
			for(int r=0;r<rowCount;r++)
			{
				for(int c=0;c<colCount;c++)
				{
					this.oppositeShips[i].cells_probability_byShip[r][c] = 0;
				}
			}
		}
		isOppositeCellGuesed = new boolean[rowCount][colCount];
		isOppositeCellHitted = new boolean[rowCount][colCount];
		cells_probability_total= new int[rowCount][colCount];
		
		for (int r = 0; r < rowCount; r++)
		{
			for (int c = 0; c < colCount; c++)
			{
				isOppositeCellGuesed[r][c] = false;
				isOppositeCellHitted[r][c] = false;
				cells_probability_total[r][c]=0;
			}
		}
		//calculate the posibilities of every cell of all ships
		calculatePosibility();
		//System.out.println("=======sinitialize end");
		
    } // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess) 
    {
		Answer answer = new Answer();
		boolean isSunk = true;
		// iterate to mark whether the guess hitted the ship or sunk the ship or
		// miss
		for (int i = 0; i < shipsCount; i++)
		{
			MyInnerShip myShip = this.myShips[i];

			for (int j = 0; j < myShip.ship.len(); j++)
			{
				if ((guess.row == myShip.rowIndex[j]) && (guess.column == myShip.clnIndex[j]))
				{
					answer.isHit = true;
					myShip.isHitted[j] = true;
					for (int k = 0; k < myShip.ship.len(); k++)
					{
						if (myShip.isHitted[k] == false)
						{
							isSunk = false;
							break;
						}
					}
					if (isSunk == true)
					{
						answer.shipSunk = myShip.ship;
					} else
					{
						answer.shipSunk = null;
					}
					return answer;
				}
			}
		}
		return answer;
		
    } // end of getAnswer()

    @Override
    public Guess makeGuess() 
    {
    	Guess myGuess = new Guess();
		//the game state is in hunting model 
		if (gameState == 0) 
		{
			//return the highest posibility cell
			return getHighestPosibility();
		}
    	
		while(gameState == 1)
		{
			if (previous_state == 0)
			{
				myGuess = getAdjacentHighestPosibility(centerHitX, centerHitY);
			}else
			{
				myGuess = getAdjacentHighestPosibility(previous_x, previous_y);
			}
			
			if (myGuess == null)//go to another side
			{
				//===========================================//
				if (fireDirection == 0)//if previous is left then go to right
				{
					int back_x = centerHitX;
					int back_y = centerHitY;
					while(isOppositeCellHitted[back_x][back_y] == true && back_y<colCount)
					{
						back_y = back_y + 1;
					}
					if (isOppositeCellGuesed[back_x][back_y] == true)
					{
						gameState = 1;
						return getHighestPosibility();
					}
					Guess newGuess = new Guess();
					newGuess.row    = back_x;
					newGuess.column = back_y;
					return newGuess;
				}
				
				if (fireDirection == 2)//if previous is right then go to left
				{
					int back_x = centerHitX;
					int back_y = centerHitY;
					while(isOppositeCellHitted[back_x][back_y] == true && back_y >=0)
					{
						back_y = back_y - 1;
					}
					if (isOppositeCellGuesed[back_x][back_y] == true)
					{
						gameState = 1;
						return getHighestPosibility();
					}
					Guess newGuess = new Guess();
					newGuess.row    = back_x;
					newGuess.column = back_y;
					return newGuess;
				}
				
				if (fireDirection == 1)//if previous is top then go to bottom
				{
					int back_x = centerHitX;
					int back_y = centerHitY;
					while(isOppositeCellHitted[back_x][back_y] == true && back_x <rowCount)
					{
						back_x = back_x + 1;
					}
					if (isOppositeCellGuesed[back_x][back_y] == true)
					{
						gameState = 1;
						return getHighestPosibility();
					}
					Guess newGuess = new Guess();
					newGuess.row    = back_x;
					newGuess.column = back_y;
					return newGuess;
				}
				
				if (fireDirection == 3)//if previous is bottom then go to top
				{
					int back_x = centerHitX;
					int back_y = centerHitY;
					while(isOppositeCellHitted[back_x][back_y] == true && back_x >= 0)
					{
						back_x = back_x - 1;
					}
					if (isOppositeCellGuesed[back_x][back_y] == true)
					{
						gameState = 1;
						return getHighestPosibility();
					}
					Guess newGuess = new Guess();
					newGuess.row    = back_x;
					newGuess.column = back_y;
					return newGuess;
				}
				
			}else
			{
				return myGuess;
			}
		}

		return myGuess;
    } // end of makeGuess()

    @Override
    public void update(Guess guess, Answer answer) 
    {
    	this.isOppositeCellGuesed[guess.row][guess.column]= true;
    	//hunting model
    	if (gameState==0)
		{
    		if (answer.isHit == false)
    		{
    			;//calculatePosibility();
    		}else
    		{
    			this.isOppositeCellHitted[guess.row][guess.column] = true;
    			if (answer.shipSunk != null)
    			{
					if (answer.shipSunk.name().equals("AircraftCarrier"))
					{
						this.oppositeShips[0].isSunk = true;
						gameState = 0;
					}else if (answer.shipSunk.name().equals("Battleship"))
					{
						this.oppositeShips[1].isSunk = true;
						gameState = 0;
					}else if (answer.shipSunk.name().equals("Submarine"))
					{
						this.oppositeShips[2].isSunk = true;
						gameState = 0;
					}else if (answer.shipSunk.name().equals("Cruiser"))
					{
						this.oppositeShips[3].isSunk = true;
						gameState = 0;
					}else if (answer.shipSunk.name().equals("Destroyer"))
					{
						this.oppositeShips[4].isSunk = true;
						gameState = 0;
					}
    			}else
				{
					gameState = 1;//go into targeting model
					previous_state = 1;
				}
				centerHitX = guess.row;
				centerHitY = guess.column;
    		}
		}else
		{
			if (answer.isHit == false)
			{
				previous_state = 0;
			}else
			{
				this.isOppositeCellHitted[guess.row][guess.column] = true;
				if (answer.shipSunk != null)
    			{
					if (answer.shipSunk.name().equals("AircraftCarrier"))
					{
						this.oppositeShips[0].isSunk = true;
						gameState = 0;
					}else if (answer.shipSunk.name().equals("Battleship"))
					{
						this.oppositeShips[1].isSunk = true;
						gameState = 0;
					}else if (answer.shipSunk.name().equals("Submarine"))
					{
						this.oppositeShips[2].isSunk = true;
						gameState = 0;
					}else if (answer.shipSunk.name().equals("Cruiser"))
					{
						this.oppositeShips[3].isSunk = true;
						gameState = 0;
					}else if (answer.shipSunk.name().equals("Destroyer"))
					{
						this.oppositeShips[4].isSunk = true;
						gameState = 0;
					}
    			}else
				{
					//left of previous hitted cell
					if (guess.column < previous_y)
					{
						fireDirection = 0;
					}
					//right of previous hitted cell
					if (guess.column > previous_y)
					{
						fireDirection = 2;
					}
					//top of previous hitted cell
					if (guess.row < previous_x)
					{
						fireDirection = 1;
					}
					//bottom of previous hitted cell
					if (guess.row > previous_x)
					{
						fireDirection = 3;
					}
					gameState = 1;//go into targeting model
					previous_state = 1;
				}				
				centerHitX = guess.row;
				centerHitY = guess.column;
			}
		}
    	
    	previous_x = guess.row;
		previous_y = guess.column;
    	
		calculatePosibility();
    	
    } // end of update()


    @Override
    public boolean noRemainingShips() 
    {
    	for (int i = 0; i < shipsCount; i++)
		{
			MyInnerShip myShip = myShips[i];
			for (int j = 0; j < myShip.ship.len(); j++)
			{
				if (myShip.isHitted[j] == false)
				{
					return false;
				}
			}
		}
		return true;
    } // end of noRemainingShips()
    
    /**
     * calculate posiblity base on current situation
     * sum all ships' posibility together
     */
    private void calculatePosibility()
    {
    	int count=0;
    	for(int sh=0;sh<shipsCount;sh++)
		{
    		OppositeInnerShip opShip = oppositeShips[sh];
    		//if the ship is sunk the posibility should set 0
    		if (opShip.isSunk)
			{
				resetPosibility(opShip);
				continue;
			}
    		//calulate the posibility value of every cell of every ship
    		calculateSingleShipPosibility(opShip);
		}
    	
    	//sum all ship's posibility to a total value
    	for(int r=0;r<rowCount;r++)
    	{
    		for(int c=0;c<colCount;c++)
    		{
    			count=0;
    			for (int i = 0; i < shipsCount; i++)
    			{
    				count = count + oppositeShips[i].cells_probability_byShip[r][c];
    			}
    			cells_probability_total[r][c] = count;
    		}
    	}
    	//System.out.println("============calculatePosibility end ====");
    }
    
    /**
     * get the cell that has the highest Posibility
     * @return
     */
    private Guess getHighestPosibility()
    {
    	Guess retValue = new Guess();
    	///
    	int highestValue =0;
    	int tempValue =0;
    	do
    	{
	    	for(int row =0;row < rowCount;row++)
	    	{
	    		for(int col=0;col<colCount;col++)
	    		{
	    			tempValue = 0;
	    			//the cell has not yet been fired
	    			if (this.isOppositeCellGuesed[row][col] == false)
	    			{
		    			for(int sh=0;sh<shipsCount;sh++)
		    			{
		    	    		OppositeInnerShip opShip = oppositeShips[sh];
		    	    		tempValue = tempValue + opShip.cells_probability_byShip[row][col];
		    			}
		    			if (tempValue > highestValue)
		    			{
		    				highestValue = tempValue;
		    				retValue.row = row;
		    				retValue.column = col;
		    			}
	    			}else
	    			{
	    				continue;
	    			}
	    		}
	    	}
    	}while(this.isOppositeCellGuesed[retValue.row][retValue.column] == true);
    	
    	return retValue;
    }
    
    /**
     * get adjacent cell which has highest posibility
     * @param x
     * @param y
     * @return
     */
    private Guess getAdjacentHighestPosibility(int x,int y)
    {
    	//the return coordinate
    	Guess retValue = new Guess();
    	int max     = 0;
    	int left    = 0;
    	int top     = 0;
    	int right   = 0;
    	int bottom  = 0;
    	boolean b_left = true;
    	boolean b_top = true;
    	boolean b_right = true;
    	boolean b_bottom = true;
    	//to check whether left cell is the highest posibility
    	if (y-1>=0 && this.isOppositeCellGuesed[x][y-1] == false)
    	{
    		left = this.cells_probability_total[x][y-1];
    		if (left > max)
        	{
        		max = left;
        		retValue = new Guess();
        		retValue.row = x;
        		retValue.column= y-1;
        		//fireDirection = 0;
        	}
    	}else
    	{
    		b_left = false;
    	}
    	
    	//to check whether right cell is the highest posibility
    	if (y+1 < colCount && this.isOppositeCellGuesed[x][y+1] == false)
    	{
    		right = this.cells_probability_total[x][y+1];
    		try 
    		{
	    		if (right > max)
	        	{
	        		max = right;
	        		retValue = new Guess();
	        		retValue.row = x;
	        		retValue.column = y+1;
	        		//fireDirection = 2;
	        	}
    		}catch(Exception exp)
    		{
    			//System.out.println(exp.getMessage());
    		}
    	}else
    	{
    		b_right = false;
    	}
    	
    	//to check whether top cell is the highest posibility
    	if (x-1>=0 && this.isOppositeCellGuesed[x - 1][y] == false)
    	{
    		top = this.cells_probability_total[x-1][y];
    		if (top > max)
        	{
        		max = top;
        		retValue = new Guess();
        		retValue.row = x - 1;
        		retValue.column = y;
        		//fireDirection = 1;
        	}
    	}else
    	{
    		b_top = false;
    	}
    	
    	//to check whether bottom cell is the highest posibility
    	if (x+1 < rowCount && this.isOppositeCellGuesed[x + 1][y] == false)
    	{
    		bottom = this.cells_probability_total[x+1][y];
    		if (bottom > max)
        	{
        		max = bottom;
        		retValue = new Guess();
        		retValue.row = x + 1;
        		retValue.column = y;
        		//fireDirection = 3;
        	}
    	}else
    	{
    		b_bottom = false;
    	}
    	if (b_left==false && b_top==false && b_right==false && b_bottom==false)
    	{
    		return null;
    	}else if(retValue.row==0 && retValue.column==0)
    	{
    		return null;
    	}else
    	{
    		return retValue;
    	}
    	
    }
    
    /**
     * reset all cell posibility of one ship
     * @param argOppShip
     */
    private void resetPosibility(OppositeInnerShip argOppShip)
    {
    	for(int r=0;r<rowCount;r++)
    	{
    		for(int c=0;c<colCount;c++)
    		{
    			argOppShip.cells_probability_byShip[r][c] = 0;
    		}
    	}
    }
    
    /**
     * calculate posibility of a single ship
     * @param argOppShip
     */
    private void calculateSingleShipPosibility(OppositeInnerShip argOppShip)
    {
    	int shipLength = argOppShip.ship.len();
    	boolean xCanSetLeft   = true;
    	boolean xCanSetRight  = true;
    	boolean yCanSetTop    = true;
    	boolean yCanSetBottom = true;
    	int xCount = 0;
    	int yCount = 0;
    	
    	for(int r=0;r<rowCount;r++)
    	{
    		for(int c=0;c<colCount;c++)
    		{
    			xCount = 0;
    			yCount = 0;
    			xCanSetLeft   = true;
    	    	xCanSetRight  = true;
    	    	yCanSetTop    = true;
    	    	yCanSetBottom = true;
    	    	
    			//if the cell is guessed and it is not hitted
    	    	//set the posibility of the cess 0
    	    	if (isOppositeCellGuesed[r][c] == true && isOppositeCellHitted[r][c]==false)
    			{
    				argOppShip.cells_probability_byShip[r][c] = 0;
    				continue;
    			}
    			
    			//x direction
    			for(int i=1;i<shipLength;i++) //set left ship
    			{
    				int newC = (c - i);
    				if (newC < 0 )//can not set ship
    				{
    					xCanSetLeft = false;
    					break;
    				}else if (isOppositeCellGuesed[r][newC]==true && isOppositeCellHitted[r][newC]==false )
    				{
    					xCanSetLeft = false;
    					break;
    				}
    			}
    			if (xCanSetLeft == true)//left can set ship
    			{
    				xCount = xCount + 1;
    				for(int i=1;i<shipLength;i++)//move to right
    				{
    					int newC = c + i;
    					if (newC < colCount && isOppositeCellGuesed[r][newC] == false)
    					{
    						xCount = xCount + 1;
    					}else
    					{
    						break;
    					}
    				}
    			}else //goto right 
    			{
    				for(int i=1;i<shipLength;i++)
        			{
        				int newC = (c+i);
        				if (newC >= colCount) //right can not set ship
        				{
        					xCanSetRight = false;
        					break;
        				}else if (isOppositeCellGuesed[r][newC] == true && isOppositeCellHitted[r][newC]==false)
        				{
        					xCanSetRight = false;
        					break;
        				}
        			}
    				if (xCanSetRight == true)//right can set ship
        			{
    					xCount = xCount + 1;
        				for(int i=1;i<shipLength;i++)//move to left
        				{
        					int newC = c - i;
        					if ((newC >= 0) && (isOppositeCellGuesed[r][newC] == false))
        					{
        						xCount = xCount + 1;
        					}else
        					{
        						break;
        					}
        				}
        			}else //right cannot set ship
        			{
        				xCount = 0;
        			}
    			}

    			//y direction
    			for(int i=1;i<shipLength;i++)
    			{
    				int newR = (r - i);
    				if (newR < 0 )//top can not set ship
    				{
    					yCanSetTop= false;
    					break;
    				}else if (isOppositeCellGuesed[newR][c]==true && isOppositeCellHitted[newR][c]==false)
    				{
    					yCanSetTop= false;
    					break;
    				}
    			}
    			//top can set ship
    			if (yCanSetTop == true)
    			{
    				yCount = yCount + 1;
    				for(int i=1;i<shipLength;i++)//move to bottom
    				{
    					int newR = r + i;
    					if (newR < rowCount && isOppositeCellGuesed[newR][c] == false)
    					{
    						yCount = yCount + 1;
    					}else
    					{
    						break;
    					}
    				}
    			}else // go to bottom
    			{
    				for(int i=1;i<shipLength;i++)
        			{
        				int newR = (r + i);
        				if (newR >= rowCount)//can not set ship
        				{
        					yCanSetBottom = false;
        					break;
        				}else if(isOppositeCellGuesed[newR][c]==true && isOppositeCellHitted[newR][c]==false)
        				{
        					yCanSetBottom = false;
        					break;
        				}
        			}
    				//bottom can set ship
    				if (yCanSetBottom == true)
        			{
    					yCount = yCount + 1;
        				for(int i=1;i<shipLength;i++) //move to top
        				{
        					int newR = r - i;
        					if (newR>=0 && isOppositeCellGuesed[newR][c] == false)
        					{
        						yCount = yCount + 1;
        					}else
        					{
        						break;
        					}
        				}
        			}else //bottom cannot set ship
        			{
        				yCount = 0;
        			}
    			}
    			
    			argOppShip.cells_probability_byShip[r][c] = xCount + yCount;
    			
    		}
    	}
    	//System.out.println("======calculate single end !========");
    }
    

} // end of class MonteCarloGuessPlayer
