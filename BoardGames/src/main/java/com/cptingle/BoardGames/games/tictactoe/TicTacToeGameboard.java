package com.cptingle.BoardGames.games.tictactoe;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.framework.Gameboard;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.tictactoe.components.TicTacToeMaterial;
import com.cptingle.BoardGames.games.tictactoe.components.TicTacToeRegionPoint;
import com.cptingle.BoardGames.util.GridPoint2D;

public class TicTacToeGameboard extends Gameboard {
	// board size const
	private static int BOARD_SIZE = 3;

	// Board stuff
	private PlayerType[][] board;

	public TicTacToeGameboard(Game game) {
		super(game, TicTacToeRegionPoint.BOARD);

		int size = game.getSpecificSettings().getInt("board-size", 3);

		if (size >= 3 && size <= 15 && size % 2 == 1)
			BOARD_SIZE = size;

		init();
		if (anchorPoint != null && direction != null)
			reset();
	}

	// Materials
	@Override
	protected void initMaterials() {
		gameMaterials.put(TicTacToeMaterial.P1_PIECE, getMaterial(TicTacToeMaterial.P1_PIECE, "BLUE_CARPET"));
		gameMaterials.put(TicTacToeMaterial.P2_PIECE, getMaterial(TicTacToeMaterial.P2_PIECE, "RED_CARPET"));
		gameMaterials.put(TicTacToeMaterial.SQUARE, getMaterial(TicTacToeMaterial.SQUARE, "POLISHED_ANDESITE"));
	}

	@Override
	public void reset() {
		create();
	}

	@Override
	protected void init() {
		this.board = new PlayerType[BOARD_SIZE][BOARD_SIZE];
	}

	/**
	 * Creates the game
	 */
	protected void create() {
		clear();
		for (int x = 0; x < BOARD_SIZE; x++) {
			for (int z = 0; z < BOARD_SIZE; z++) {
				GridPoint2D p = new GridPoint2D(x, z);

				int mod = direction.getModX() != 0 ? direction.getModX() : direction.getModZ();
				Location l;
				if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
					l = new Location(anchorPoint.getWorld(), anchorPoint.getBlockX() + -mod * x,
							anchorPoint.getBlockY(), anchorPoint.getBlockZ() + mod * z);
				} else {
					l = new Location(anchorPoint.getWorld(), anchorPoint.getBlockX() + mod * x, anchorPoint.getBlockY(),
							anchorPoint.getBlockZ() + mod * z);
				}
				board[x][z] = PlayerType.NONE;
				locationPointMap.put(p, l);

				l.getBlock().setType(gameMaterials.get(TicTacToeMaterial.SQUARE));
				l.clone().add(0, 1, 0).getBlock().setType(Material.AIR);
			}
		}
	}

	@Override
	public boolean isLocationInBoard(Location l) {
		if (l.getBlockY() > (anchorPoint.getBlockY() + 2) || l.getBlockY() < anchorPoint.getBlockY()) {
			return false;
		}
		GridPoint2D gp = new GridPoint2D(l.getBlockX() - anchorPoint.getBlockX(),
				l.getBlockZ() - anchorPoint.getBlockZ());

		return locationPointMap.containsKey(gp);
	}

	@Override
	public boolean blockClicked(Location l, Player p) {
		// Return if block is outside board
		if (!isLocationInBoard(l))
			return false;

		// Return if not this players turn
		if (game.getTypeFromPlayer(p) != game.turn())
			return false;

		GridPoint2D point = getPointFromLocation(l);
		// Check that board square is empty
		if (board[point.X()][point.Z()] != PlayerType.NONE && board[point.X()][point.Z()] != null)
			return false;

		board[point.X()][point.Z()] = game.turn();
		if (game.turn() == PlayerType.PLAYER_ONE)
			l.clone().add(0, 1, 0).getBlock().setType(getMaterial(TicTacToeMaterial.P1_PIECE));
		else
			l.clone().add(0, 1, 0).getBlock().setType(getMaterial(TicTacToeMaterial.P2_PIECE));

		for (Player plr : game.getAllPlayers())
			p.playSound(plr.getLocation(), Sound.BLOCK_WOOL_PLACE, 10, 1);

		PlayerType winner = checkForWinner();

		if (winner != PlayerType.NONE) {
			game.doWin(winner);
			return true;
		}

		game.nextTurn();

		return true;
	}

	/**
	 * Checks for winner
	 */
	protected PlayerType checkForWinner() {
		PlayerType win = PlayerType.NONE;
		win = checkPlayerWin(PlayerType.PLAYER_ONE);
		win = (win == PlayerType.NONE) ? checkPlayerWin(PlayerType.PLAYER_TWO) : win;

		if (win == PlayerType.NONE) {
			for (PlayerType[] bc : board) {
				for (PlayerType t : bc) {
					if (t == PlayerType.NONE) {
						return win;
					}
				}
			}
			game.end("Cat's Game!");
			return null;
		}
		return win;
	}

	private PlayerType checkPlayerWin(PlayerType pt) {
		boolean tempdiag1 = true;
		boolean tempdiag2 = true;
		boolean toReturn = false;
		for (int r = 0; r < BOARD_SIZE; r++) {
			boolean temprow = true;
			boolean tempcol = true;
			for (int c = 0; c < BOARD_SIZE; c++) {
				temprow = temprow && (board[r][c] == pt);
				tempcol = tempcol && (board[c][r] == pt);
				if (r == c)
					tempdiag1 = tempdiag1 && (board[r][c] == pt);
				if (r == (BOARD_SIZE - 1 - c))
					tempdiag2 = tempdiag2 && (board[r][c] == pt);
			}
			toReturn = toReturn || temprow || tempcol;
		}
		toReturn = toReturn || tempdiag1 || tempdiag2;
		return toReturn ? pt : PlayerType.NONE;
	}

}
