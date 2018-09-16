package com.BattleOfColors.game;

enum Cache_fill {
	nil, yes, no
}
public class GameCell {
	public int colorID;
	public int tmpColor; // для AI
	public boolean tmp, tmpScore, rez;
	public int prevColor;
	public Cache_fill cache_fill; // для fillHidden
	
	public GameCell(int colorID) {
		this.colorID = colorID;
	}
}
