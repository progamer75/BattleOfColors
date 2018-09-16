package com.BattleOfColors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GamePreferences {
	private Preferences pref;
	private BattleOfColorsGame game;
	
	private static final String PREFS_NAME = "BOC";
	private static final String PREF_SCORE = "Score_";
	private static final String PREF_WIN = "Win_";
	private static final String PREF_LOSS = "Loss_";
	private static final String PREF_DRAW = "Draw_";
	private static final String PREF_POINTS = "Points_";
	private static final String PREF_COLORS = "Colors_";
	private static final String PREF_DIF = "Difficulty_";
	private static final String PREF_SIZE = "CellSize_";
	private static final String PREF_ORDER = "Order_";
	    
    //TODO шифровать статистику
	public GamePreferences(BattleOfColorsGame battleOfColorsGame) {
        pref = Gdx.app.getPreferences(PREFS_NAME);
        this.game = battleOfColorsGame;
    }
    
	public boolean Synchronize(int numPlayers, int colors, int scoreGPGS, int winsGPGS, int lossesGPGS, int drawsGPGS) {
		ActionResolver res = game.actionResolver;
    	if((!res.getSignedInGPGS())&&(!res.isConnectingGPGS())) {
    		res.loginGPGS();
    	}
		if (!res.getSignedInGPGS()) {
			return false;
		}

		if(scoreGPGS > 0) {
			int score = GetScore(numPlayers, colors);
			if(scoreGPGS > score)
				SetScore(numPlayers, colors, scoreGPGS);
			if(score > scoreGPGS)
				res.submitScoreGPGS(numPlayers, colors, score);
		}
		
		if(winsGPGS > 0) {
			int wins = GetWin(numPlayers, colors);
			if(wins > winsGPGS)
				res.submitEventGPGS(numPlayers, colors, "wins", wins - winsGPGS);
			if(winsGPGS > wins)
				AddWin(numPlayers, colors, winsGPGS - wins);
		}
		
		if(lossesGPGS > 0) {
			int losses = GetLoss(numPlayers, colors);
			if(losses > lossesGPGS)
				res.submitEventGPGS(numPlayers, colors, "losses", losses - lossesGPGS);
			if(lossesGPGS > losses)
				AddLoss(numPlayers, colors, lossesGPGS - losses);
		}
		
/*		if(drawsGPGS > 0) {
			int draws = GetDraw(numPlayers, colors);
			if(draws > drawsGPGS)
				res.submitEventGPGS(numPlayers, colors, "draws", draws - drawsGPGS);
			if(drawsGPGS > draws)
				AddDraw(numPlayers, colors, drawsGPGS - draws);
		}*/
		
		return true;
	}
    
    public int GetScore(int numPlayer, int maxColors) {
		//res.getLeaderboardGPGS(numPlayer, difficult);
		return pref.getInteger(PREF_SCORE + numPlayer + "_" + maxColors, -1000);
    }
    
    public int GetWin(int numPlayer, int maxColors) {
        return pref.getInteger(PREF_WIN + numPlayer + "_"  + maxColors, 0);
    }
    
    public int GetLoss(int numPlayer, int maxColors) {
        return pref.getInteger(PREF_LOSS + numPlayer + "_" + maxColors, 0);
    }
    
    public int GetDraw(int numPlayer, int maxColors) {
        return pref.getInteger(PREF_DRAW + numPlayer + "_" + maxColors, 0);
    }
 
    public void SetScore(int numPlayer, int maxColors, int score) {
        SetInt(PREF_SCORE, numPlayer, maxColors, score);
    }

    public void AddWin(int numPlayer, int maxColors, int inc) {
    	int val = GetWin(numPlayer, maxColors) + inc;
        SetInt(PREF_WIN, numPlayer, maxColors, val);
    }

    public void AddLoss(int numPlayer, int maxColors, int inc) {
    	int val = GetLoss(numPlayer, maxColors) + inc;
        SetInt(PREF_LOSS, numPlayer, maxColors, val);
    }

    public void AddDraw(int numPlayer, int maxColors, int inc) {
    	int val = GetDraw(numPlayer, maxColors) + inc;
        SetInt(PREF_DRAW, numPlayer, maxColors, val);
    }

    public int GetPoints(int numPlayer, int maxColors) {
        return pref.getInteger(PREF_POINTS + numPlayer + "_" + maxColors, 0);
    }
    
    public void AddPoints(int numPlayer, int maxColors, int inc) {
    	int val = GetPoints(numPlayer, maxColors) + inc;
        SetInt(PREF_POINTS, numPlayer, maxColors, val);
    }
    
    private void SetInt(String str, int numPlayer, int maxColors, int val) {
        pref.putInteger(str + numPlayer + "_" + maxColors, val);
        pref.flush();
    }
    
    public void SetColors(int val) {
    	pref.putInteger(PREF_COLORS, val);
        pref.flush();
    }
    
    public void SetDif(int val) {
    	pref.putInteger(PREF_DIF, val);
        pref.flush();
    }
    
    public void SetSize(int val) {
    	pref.putInteger(PREF_SIZE, val);
        pref.flush();
    }
    
    public int GetColors() {
    	return pref.getInteger(PREF_COLORS, -1);
    }
    
    public int GetDif() {
    	return pref.getInteger(PREF_DIF, -1);
    }
    
    public int GetSize() {
    	return pref.getInteger(PREF_SIZE, -1);
    }

	public void SetOrder(int val) {
		pref.putInteger(PREF_ORDER, val);
		pref.flush();
	}

	public int GetOrder() {
		return pref.getInteger(PREF_ORDER, 0);
	}
}
