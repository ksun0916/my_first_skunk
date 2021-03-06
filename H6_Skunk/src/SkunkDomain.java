import java.util.ArrayList;
import edu.princeton.cs.introcs.*;

public class SkunkDomain
{
	private static final int PENALTY_FOR_ONE_SKUNK = 1;
	private static final int PENALTY_FOR_SKUNK_DEUCE = 2;
	private static final int PENALTY_FOR_TWO_SKUNK = 4;
	private static final int ZERO_SCORE = 0;
	private static final int DICE_FOR_SKUNK_DEUCE = 3;
	private static final int DICE_FOR_TWO_SKUNK = 2;
	private static final int DIE_FOR_ONE_SKUNK = 1;
	public SkunkUI skunkUI;
	public UI ui;
	public int numberOfPlayers;
	public String[] playerNames;
	public ArrayList<Player> players;
	public int kitty;

	public Player activePlayer;
	public int activePlayerIndex;

	public boolean wantsToQuit;
	public boolean oneMoreRoll;

	public Dice skunkDice;

	public SkunkDomain(SkunkUI ui)
	{
		this.skunkUI = ui;
		this.ui = ui; // hide behind the interface UI
		
		this.playerNames = new String[20];
		this.players = new ArrayList<Player>();
		this.skunkDice = new Dice();
		this.wantsToQuit = false;
		this.oneMoreRoll = false;
	}

	public boolean run()
	{
		ui.println("Welcome to Skunk 0.47\n");

		String numberPlayersString = skunkUI.promptReadAndReturn("How many players?");
		this.numberOfPlayers = Integer.parseInt(numberPlayersString);

		for (int playerNumber = 0; playerNumber < numberOfPlayers; playerNumber++)
		{
			ui.print("Enter name of player " + (playerNumber + 1) + ": ");
			playerNames[playerNumber] = StdIn.readLine();
			this.players.add(new Player(50));
		}
		activePlayerIndex = 0;
		activePlayer = players.get(activePlayerIndex);

		ui.println("Starting game...\n");
		boolean gameNotOver = true;

		while (gameNotOver)
		{
			ui.println("Next player is " + playerNames[activePlayerIndex] + ".");
			// Extract constant for ZERO_SCORE = 0
			activePlayer.setTurnScore(ZERO_SCORE);
			
			// Extract method to check whether player want to roll
			boolean wantsToRoll = whetherPlayerWantToRoll();
			
			while (wantsToRoll)
			{
				// Extract constant for ZERO_SCORE = 0
				activePlayer.setRollScore(ZERO_SCORE);
				skunkDice.roll();
				// Extract constant for DICE_FOR_TWO_SKUNK = 2
				if (skunkDice.getLastRoll() == DICE_FOR_TWO_SKUNK)
				{
					ui.println("Two Skunks! You lose the turn, zeroing out both turn and game scores and paying 4 chips to the kitty");
					
					// Extract constant for PENALTY_FOR_TWO_SKUNK = 4
					// Move adjust kitty and change player chips in to method
					wantsToRoll = adjustKittyAndPlayerChips(PENALTY_FOR_TWO_SKUNK, true, true);
					
					break;
				}
				// Extract constant for DICE_FOR_SKUNK_DEUCE = 3
				else if (skunkDice.getLastRoll() == DICE_FOR_SKUNK_DEUCE)
				{
					ui.println(
							"Skunks and Deuce! You lose the turn, zeroing out the turn score and paying 2 chips to the kitty");
					
					// Extract constant for PENALTY_FOR_SKUNK_DEUCE = 2
					// Move adjust kitty and change player chips in to method
					wantsToRoll = adjustKittyAndPlayerChips(PENALTY_FOR_SKUNK_DEUCE, true, false);
					
					break;
				}
				// Extract constant for DIE_FOR_ONE_SKUNK = 1
				else if (skunkDice.getDie1().getLastRoll() == DIE_FOR_ONE_SKUNK || skunkDice.getDie2().getLastRoll() == DIE_FOR_ONE_SKUNK)
				{
					ui.println("One Skunk! You lose the turn, zeroing out the turn score and paying 1 chip to the kitty");
					
					// Extract constant for PENALTY_FOR_ONE_SKUNK = 1
					// Move adjust kitty and change player chips in to method
					wantsToRoll = adjustKittyAndPlayerChips(PENALTY_FOR_ONE_SKUNK, true, false);
					
					break;

				}

				activePlayer.setRollScore(skunkDice.getLastRoll());
				activePlayer.setTurnScore(activePlayer.getTurnScore() + skunkDice.getLastRoll());
				ui.println(
						"Roll of " + skunkDice.toString() + ", gives new turn score of " + activePlayer.getTurnScore());

				// Extract method to check whether player want to roll
				wantsToRoll = whetherPlayerWantToRoll();				

			}

			ui.println("End of turn for " + playerNames[activePlayerIndex]);
			ui.println("Score for this turn is " + activePlayer.getTurnScore() + ", added to...");
			ui.println("Previous game score of " + activePlayer.getGameScore());
			activePlayer.setGameScore(activePlayer.getGameScore() + activePlayer.getTurnScore());
			ui.println("Gives new game score of " + activePlayer.getGameScore());

			ui.println("");
			if (activePlayer.getGameScore() >= 100)
				gameNotOver = false;

			
			// Extract method print score board
			printScoreBoard();

			ui.println("Turn passes to right...");

			activePlayerIndex = (activePlayerIndex + 1) % numberOfPlayers;
			activePlayer = players.get(activePlayerIndex);

		}
		// last round: everyone but last activePlayer gets another shot

		ui.println("**** Last turn for all... ****");

		for (int i = activePlayerIndex, count = 0; count < numberOfPlayers-1; i = (i++) % numberOfPlayers, count++)
		{
			ui.println("Last turn for player " + playerNames[activePlayerIndex] + "...");
			// Extract constant for ZERO_SCORE = 0
			activePlayer.setTurnScore(ZERO_SCORE);

			// Extract method to check whether player want to roll
			boolean wantsToRoll = whetherPlayerWantToRoll();

			while (wantsToRoll)
			{
				skunkDice.roll();
				ui.println("Roll is " + skunkDice.toString() + "\n");

				// Extract constant for DICE_FOR_TWO_SKUNK = 2
				if (skunkDice.getLastRoll() == DICE_FOR_TWO_SKUNK)
				{
					ui.println("Two Skunks! You lose the turn, zeroing out both turn and game scores and paying 4 chips to the kitty");
					
					// Extract constant for PENALTY_FOR_TWO_SKUNK = 4
					// Move adjust kitty and change player chips in to method
					wantsToRoll = adjustKittyAndPlayerChips(PENALTY_FOR_TWO_SKUNK, true, true);
					
					break;
				}
				// Extract constant for DICE_FOR_SKUNK_DEUCE = 3
				else if (skunkDice.getLastRoll() == DICE_FOR_SKUNK_DEUCE)
				{
					ui.println(
							"Skunks and Deuce! You lose the turn, zeroing out the turn score and paying 2 chips to the kitty");
					
					// Extract constant for PENALTY_FOR_SKUNK_DEUCE = 2
					// Move adjust kitty and change player chips in to method
					wantsToRoll = adjustKittyAndPlayerChips(PENALTY_FOR_SKUNK_DEUCE, true, false);
										
				}
				// Extract constant for DIE_FOR_ONE_SKUNK = 1
				else if (skunkDice.getDie1().getLastRoll() == DIE_FOR_ONE_SKUNK || skunkDice.getDie2().getLastRoll() == DIE_FOR_ONE_SKUNK)
				{
					ui.println("One Skunk!  You lose the turn, zeroing out the turn score and paying 1 chip to the kitty");
					
					// Extract constant for PENALTY_FOR_ONE_SKUNK = 1
					// Move adjust kitty and change player chips in to method
					wantsToRoll = adjustKittyAndPlayerChips(PENALTY_FOR_ONE_SKUNK, true, false);
										
				}
				else
				{
					activePlayer.setTurnScore(activePlayer.getRollScore() + skunkDice.getLastRoll());
					ui.println("Roll of " + skunkDice.toString() + ", giving new turn score of "
							+ activePlayer.getTurnScore());

					ui.println("");
					
					// Extract method print score board
					printScoreBoard();

					// Extract method to check whether player want to roll
					wantsToRoll = whetherPlayerWantToRoll();
				}

			}

			activePlayer.setTurnScore(activePlayer.getRollScore() + skunkDice.getLastRoll());
			ui.println("Final roll of " + skunkDice.toString() + ", giving final game score of "
					+ activePlayer.getRollScore());

		}

		int winner = 0;
		int winnerScore = 0;

		for (int player = 0; player < numberOfPlayers; player++)
		{
			Player nextPlayer = players.get(player);
			ui.println("Final game score for " + playerNames[player] + " is " + nextPlayer.getGameScore());
			if (nextPlayer.getGameScore() > winnerScore)
			{
				winner = player;
				winnerScore = nextPlayer.getGameScore();
			}
		}

		ui.println(
				"Game winner is " + playerNames[winner] + " with score of " + players.get(winner).getGameScore());
		players.get(winner).setNumberChips(players.get(winner).getNumberChips() + kitty);
		ui.println("Game winner earns " + kitty + " chips , finishing with " + players.get(winner).getNumberChips());

		// Extract method print score board
		ui.println("");
		ui.print("Final ");
		printScoreBoard();
		
		return true;
	}

	private void printScoreBoard() {
		ui.println("Scoreboard: ");
		ui.println("Kitty has " + kitty + " chips.");
		ui.println("Player name -- Turn score -- Game score -- Total chips");
		ui.println("-----------------------");

		for (int pNumber = 0; pNumber < numberOfPlayers; pNumber++)
		{
			ui.println(playerNames[pNumber] + " -- " + players.get(pNumber).getTurnScore() + " -- "
					+ players.get(pNumber).getGameScore() + " -- " + players.get(pNumber).getNumberChips());
		}
		ui.println("-----------------------");
	}

	// Extract method to check whether player want to roll
	private boolean whetherPlayerWantToRoll() {
		String wantsToRollStr = ui.promptReadAndReturn("Do you want to roll? y or n");
		return 'y' == wantsToRollStr.toLowerCase().charAt(0);
	}

	
	// Move adjust kitty and change player chips into method
	private boolean adjustKittyAndPlayerChips(int k, boolean resetTurnScore, boolean resetGameScore) {
		kitty += k;
		activePlayer.setNumberChips(activePlayer.getNumberChips() - k);
		if(resetTurnScore) {
			// Extract constant for ZERO_SCORE = 0
			activePlayer.setTurnScore(ZERO_SCORE);
		}
		if(resetGameScore) {
			// Extract constant for ZERO_SCORE = 0
			activePlayer.setGameScore(ZERO_SCORE);
		}
		return false;
	}

}
