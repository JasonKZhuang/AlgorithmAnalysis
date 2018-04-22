package player;

import java.util.Random;

import ship.Ship;
import world.World;

/**
 * Random guess player (task A). Please implement this class.
 * 
 * @author Youhan, Jeffrey
 */
public class RandomGuessPlayer implements Player
{
	// the number of ships
	private static final int shipsCount = 5;

	// the count number of row
	private int rowCount = 0;

	// the count number of column
	private int colCount = 0;

	// inner class ship
	private class MyInnerShip
	{
		Ship ship = null;

		// the ship row index array
		int[] rowIndex = { -1, -1, -1, -1, -1 };

		// the ship column index array
		int[] clnIndex = { -1, -1, -1, -1, -1 };

		// to mark which part is hitted
		boolean[] isHitted = { true, true, true, true, true };

		// construct method of this inner class
		private MyInnerShip()
		{

		}
	}

	// the array of the inner ships
	private MyInnerShip[] myShips;

	// store the flag of whether the cell was guessed
	private boolean[][] isOppositeCellGuessed;

	@Override
	public void initialisePlayer(World world)
	{
		int i = 0;
		this.rowCount = world.numRow;
		this.colCount = world.numColumn;
		this.myShips = new MyInnerShip[shipsCount];
		// create new array of the guessing cells and initialize the cell
		// guess array
		isOppositeCellGuessed = new boolean[rowCount][colCount];
		for (int r = 0; r < rowCount; r++)
		{
			for (int c = 0; c < colCount; c++)
			{
				isOppositeCellGuessed[r][c] = false;
			}
		}

		// initialize the array of inner ships
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
			myShips[i] = myShip;
			i = i + 1;
		}

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
			MyInnerShip myShip = myShips[i];
			for (int j = 0; j < myShip.ship.len(); j++)
			{
				if ((guess.row == myShip.rowIndex[j])
						&& (guess.column == myShip.clnIndex[j]))
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
		// create a random class
		Random random = new Random();
		int i;
		int j;
		// seek a no guessed cess
		do
		{
			i = random.nextInt(this.rowCount);
			j = random.nextInt(this.colCount);
		} while (isOppositeCellGuessed[i][j] != false);

		// set guess value
		Guess myGuess = new Guess();
		myGuess.row = i;
		myGuess.column = j;
		// mark the cell that has guessed
		isOppositeCellGuessed[i][j] = true;
		return myGuess;
	} // end of makeGuess()

	@Override
	public void update(Guess guess, Answer answer)
	{

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

} // end of class RandomGuessPlayer
