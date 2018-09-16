package com.flood.game.desktop;

import com.BattleOfColors.game.ActionResolver;

public class ActionResolverDesktop implements ActionResolver {
	boolean signedInStateGPGS = false;

	@Override
	public boolean getSignedInGPGS() {
		return signedInStateGPGS;
	}

	@Override
	public void loginGPGS() {
		System.out.println("loginGPGS");
		signedInStateGPGS = true;
	}

	@Override
	public void submitScoreGPGS(int players, int dif, int score) {
		System.out.println("submitScoreGPGS " + score);
	}

	@Override
	public void unlockAchievementGPGS(String achievementId) {
		System.out.println("unlockAchievement " + achievementId);
	}

	@Override
	public void getLeaderboardGPGS() {
		System.out.println("getLeaderboardGPGS");
	}

	@Override
	public void getAchievementsGPGS() {
		System.out.println("getAchievementsGPGS");
	}

	@Override
	public void getLeaderboard(int player, int color) {
	}

	@Override
	public void submitEventGPGS(int players, int colors, String event, int inc) {
	}

	@Override
	public int loadEventGPGS(int players, int colors, String event) {
		return 0;
	}

	@Override
	public boolean isConnectingGPGS() {
		return false;
	}

	@Override
	public void FillScoreGPGS() {

	}

	@Override
	public void StopFillScoreGPGS() {

	}

	@Override
	public int SynchronizeGPGS(int players, int colors, int score, int wins, int losses, int draws) {
		return 0;
	}

	@Override
	public void connect() {
	}
}
