package com.BattleOfColors.game;

public interface ActionResolver {
	public boolean getSignedInGPGS();
	public boolean isConnectingGPGS();
	public void loginGPGS();
	public void connect();

	public void submitEventGPGS(int players, int colors, String event, int inc);
	
	public int loadEventGPGS(int players, int colors, String event);
	
	/** Отправить результат в таблицу рекордов */
	public void submitScoreGPGS(int players, int dif, int score);

	/**
	 * Разблокировать достижение
	 * 
	 * @param achievementId
	 *            ID достижения. Берется из файла games-ids.xml
	 */
	public void unlockAchievementGPGS(String achievementId);

	/** Показать Activity с таблицей рекордов */
	public void getLeaderboardGPGS();
	
	public void getLeaderboard(int player, int color);
	
	public void FillScoreGPGS();
	public void StopFillScoreGPGS();
	/** Показать Activity с достижениями */
	public void getAchievementsGPGS();
	int SynchronizeGPGS(int players, int colors, int score, int wins, int losses, int draws);
	
}