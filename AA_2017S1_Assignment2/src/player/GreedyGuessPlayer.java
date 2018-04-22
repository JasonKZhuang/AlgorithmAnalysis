package player;

import java.util.Random;

import ship.Ship;
import world.World;

/**
 * Greedy guess player (task B). Please implement this class.
 * 
 * @author Youhan, Jeffrey
 */
public class GreedyGuessPlayer implements Player
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
		//the ship row index array
		int[] rowIndex = { -1, -1, -1, -1, -1 };
		//the ship column index array
		int[] clnIndex = { -1, -1, -1, -1, -1 };
		//to mark which part is hitted
		boolean[] isHitted = { true, true, true, true, true };
		//construct method of this inner class
		private MyInnerShip()
		{

		}
	}
	
	//the array of the inner ships
	private MyInnerShip[] myShips;
	//store the flag of whether the cell was guessed
	private boolean[][] isOppositeCellGuessed;
	//store the flag of whether the cell was hitted
	private boolean[][] isOppositeCellHitted;
	
	//coordinate inner class
	private class Coordinate
	{
		int row;
		int col;
		private Coordinate()
		{

		}
	}
	//the array of coordinate which is going to store less coordiates
	private Coordinate[] lessCoordinates;
	
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
	
	@Override
	public void initialisePlayer(World world)
	{
		this.rowCount = world.numRow;
		this.colCount = world.numColumn;
		this.myShips = new MyInnerShip[shipsCount];
		//create new array of the guessing cells and initialize them
		isOppositeCellGuessed = new boolean[rowCount][colCount];
		//create new array of the hitting cells and initialize them
		isOppositeCellHitted = new boolean[rowCount][colCount];
		for (int r = 0; r < rowCount; r++)
		{
			for (int c = 0; c < colCount; c++)
			{
				isOppositeCellGuessed[r][c] = false;
				isOppositeCellHitted[r][c] = false;
			}
		}
		
		// initialize the array of inner ships
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

		//create the array of coordinates
		lessCoordinates = new Coordinate[rowCount*colCount/2];
		int index = 0;
		//initialize this array
		for(int row=0;row<rowCount;row = row + 1)
		{
			for(int col=row%2;col<colCount;col = col + 2)
			{
				Coordinate cd = new Coordinate();
				cd.row = row;
				cd.col = col;
				lessCoordinates[index] = cd;
				index = index + 1;
			}
		}
		//System.out.println("====================");
	} // end of initialisePlayer()

	@Override
	public Answer getAnswer(Guess guess)
	{
		// To implemented get the answer from the guess
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
			return newHunting();//return new hunting guess
		}
		//the game state is in targeting model
		if (gameState == 1) //need new targeting
		{
			do
			{
				//this is left
				if (fireDirection==0)
				{
					fireDirection = 1;//next turn to top
					fireCount = fireCount + 1;//count the number of fires
					// left cell to fire
					if ((centerHitY - 1) >= 0 
							&& isOppositeCellGuessed[centerHitX][centerHitY - 1] == false)
					{
						myGuess.row    = centerHitX;
						myGuess.column = centerHitY - 1;
						isOppositeCellGuessed[myGuess.row][myGuess.column] = true;
						return myGuess;
					}else
					{
						continue;
					}
				}
				
				//this is top
				if (fireDirection == 1)
				{
					fireDirection = 2;//next turn to right
					fireCount = fireCount + 1;//count the number of fires
					// top cell to fire
					if ((centerHitX + 1) < rowCount	
							&& isOppositeCellGuessed[centerHitX + 1][centerHitY] == false)
					{
						myGuess.row = centerHitX + 1;
						myGuess.column = centerHitY;
						isOppositeCellGuessed[myGuess.row][myGuess.column] = true;
						return myGuess;
					}else
					{
						continue;
					}
				}
				
				//this is right
				if (fireDirection == 2)
				{
					fireDirection = 3;//next turn to bottom
					fireCount = fireCount + 1;//count the number of fires
					// right cell to fire
					if ((centerHitY + 1) < colCount	
							&& isOppositeCellGuessed[centerHitX][centerHitY + 1] == false)// right
					{
						myGuess.row    = centerHitX;
						myGuess.column = centerHitY + 1;
						isOppositeCellGuessed[myGuess.row][myGuess.column] = true;
						return myGuess;
					}else
					{
						continue;
					}
				}
				
				if (fireDirection == 3)
				{
					fireDirection = 4;//next turn to break
					fireCount = fireCount + 1;
					// bottom cell to fire
					if ((centerHitX - 1) >= 0 && isOppositeCellGuessed[centerHitX - 1][centerHitY] == false)// bottom
					{
						myGuess.row    = centerHitX - 1;
						myGuess.column = centerHitY;
						isOppositeCellGuessed[myGuess.row][myGuess.column] = true;
						return myGuess;
					}else
					{
						continue;
					}
				}

			}while(fireCount<4);
		}
		
		gameState = 0;// recover to hunting model
		fireDirection = 0; //recover to initial 
		fireCount = 0;////recover to initial 
		
		return newHunting();
		
	} // end of makeGuess()

	@Override
	public void update(Guess guess, Answer answer)
	{
		this.isOppositeCellGuessed[guess.row][guess.column] = true;
		// player 1 guess and get player's answer
		if (gameState==0)//hunting model
		{
			//if the cell is hitted, then set new center coordinate value
			if (answer.isHit == true)
			{
				this.isOppositeCellHitted[guess.row][guess.column] = true;
				centerHitX = guess.row;
				centerHitY = guess.column;
				gameState = 1;//turn to targeting model
			}else
			{
				return;
			}
		}else// targeting model
		{
			if (answer.isHit == true)
			{
				this.isOppositeCellHitted[guess.row][guess.column] = true;
				//centerHitX = guess.row;
				//centerHitY = guess.column;
				//fireDirection = 0;
				//fireCount = 0;
			}else
			{
				return;
			}
		}
		
	} // end of update()

	@Override
	public boolean noRemainingShips()
	{
		// To calculate all ships' state
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
	 * start a new hunting, select a noguessed cell randomly
	 * @return a new guess class
	 */
	private Guess newHunting()
	{
		Guess myGuess = new Guess();
		Random random = new Random();
		int arrLength = lessCoordinates.length;
		int arrIndex  = 0;
		Coordinate cod ;
		do
		{
			//generate a random index value within the length of array
			arrIndex = random.nextInt(arrLength);
			cod = lessCoordinates[arrIndex];
			if (this.isOppositeCellGuessed[cod.row][cod.col] == false)
			{
				myGuess.row    = cod.row;
				myGuess.column = cod.col;
				this.isOppositeCellGuessed[cod.row][cod.col] = true;
				return myGuess;
			}
		} while (isOppositeCellGuessed[cod.row][cod.col] != false);
		
		return myGuess;
		
	}
} // end of class GreedyGuessPlayer
