package player;

import java.util.Random;

import ship.Ship;
import world.World;

public class SampleRandomGuessPlayer implements Player
{
	static final int numShips = 5;

	//static final int maxLen = 5;

	//static final int[] rowDeltas = { 1, 0, -1, 0, 1, 0, -1, 0 };

	//static final int[] clnDeltas = { 0, -1, 0, 1, 1, 0, -1, 0 };

	int rowSize = 0;

	int clnSize = 0;

	boolean isHex = false;

	private class OwnShip
	{
		Ship ship = null;

		int[] rowCdns = { -1, -1, -1, -1, -1 };

		int[] clnCdns = { -1, -1, -1, -1, -1 };

		boolean[] isdown = { true, true, true, true, true };

		private OwnShip()
		{
		}
	}

	OwnShip[] ownShips = new OwnShip[5];

	boolean[][] isguessed;

	public void initialisePlayer(World paramWorld)
	{
		this.rowSize = paramWorld.numRow;
		this.clnSize = paramWorld.numColumn;
		this.isHex = paramWorld.isHex;
		this.isguessed = new boolean[this.rowSize][this.clnSize	+ (this.rowSize + 1) / 2];
		int i = 0;
		for (World.ShipLocation localShipLocation : paramWorld.shipLocations)
		{
			this.ownShips[i] = new OwnShip();
			this.ownShips[i].ship = localShipLocation.ship;
			for (int j = 0; j < this.ownShips[i].ship.len(); j++)
			{
				this.ownShips[i].rowCdns[j] = ((World.Coordinate) localShipLocation.coordinates.get(j)).row;
				this.ownShips[i].clnCdns[j] = ((World.Coordinate) localShipLocation.coordinates.get(j)).column;
				this.ownShips[i].isdown[j] = false;
			}
			i++;
		}
	}

	public Answer getAnswer(Guess paramGuess)
	{
		Answer localAnswer = new Answer();
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < this.ownShips[i].ship.len(); j++)
			{
				if ((paramGuess.row == this.ownShips[i].rowCdns[j])
						&& (paramGuess.column == this.ownShips[i].clnCdns[j]))
				{
					localAnswer.isHit = true;
					ownShips[i].isdown[j] = true;
					int k = 1;
					for (int m = 0; m < this.ownShips[i].ship.len(); m++)
					{
						if (ownShips[i].isdown[m] == false)
						{
							k = 0;
						}
					}
					if (k != 0)
					{
						localAnswer.shipSunk = this.ownShips[i].ship;
					}
					return localAnswer;
				}
			}
		}
		return localAnswer;
	}

	public Guess makeGuess()
	{
		Random localRandom = new Random();
		int i;
		int j;
		do
		{
			i = localRandom.nextInt(this.rowSize);
			j = localRandom.nextInt(this.clnSize);
			if (this.isHex)
			{
				j += (i + 1) / 2;
			}
		} while (isguessed[i][j] != false);
		Guess localGuess = new Guess();
		localGuess.row = i;
		localGuess.column = j;
		isguessed[i][j] = true;
		return localGuess;
	}

	public void update(Guess paramGuess, Answer paramAnswer)
	{
	}

	public boolean noRemainingShips()
	{
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < this.ownShips[i].ship.len(); j++)
			{
				if (ownShips[i].isdown[j] == false)
				{
					return false;
				}
			}
		}
		return true;
	}
}
