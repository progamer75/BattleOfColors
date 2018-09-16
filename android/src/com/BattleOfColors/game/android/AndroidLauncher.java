package com.BattleOfColors.game.android;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;

import com.BattleOfColors.game.ActionResolver;
import com.BattleOfColors.game.BattleOfColorsGame;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.event.EventBuffer;
import com.google.android.gms.games.event.Events;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.LoadPlayerScoreResult;
import com.google.example.games.basegameutils.GameHelper;

import java.util.Locale;

import Screens.ScoreScreen;

public class AndroidLauncher extends AndroidApplication implements
		GameHelper.GameHelperListener, ActionResolver /*GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener*/ {
	private GameHelper gameHelper;
	private BattleOfColorsGame game;
	public int score;
	
	//private ResultCallback<Events.LoadEventsResult> eventsResultCB;
	//private ResultCallback<LoadPlayerScoreResult> playerScoreResultCB;
	String str;
	
	private GetScoreAsync fillScore = null;
//	private GoogleApiClient mGoogleApiClient;
	
	private class GetScoreAsync extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onProgressUpdate(Integer... values) {
			//0-player; 1-color; 2-daily; 3-weekly; 4-alltime; 5-wins; 6-losses; 7-draws
			
			super.onProgressUpdate(values);
			
/*	        Table table2 = screen.table2;
	        LabelStyle labelStyle = screen.labelStyle;
	        
	        ClickListener cl = new ClickListener() {
	            @Override
	            public void clicked (InputEvent event, float x, float y) {
	            	Actor lb = event.getTarget();
	            	if(lb == null)
	            		return;
	            	StatInfo info = (StatInfo) lb.getUserObject();
	            	if(info == null)
	            		return;
	            	
	    			int resID = getResources().getIdentifier("leaderboard_" + info.player + "_players_" + info.color + "_colors", "string", getPackageName());
	    			String str = getString(resID);
	            	startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), str, info.time, LeaderboardVariant.COLLECTION_PUBLIC), 100);	            	
	            };
	        };
	        
	        table2.row();
	        Label lb = new Label("\n" + values[0] + "\n", labelStyle);
	        lb.setAlignment(Align.center);
	        lb.setUserObject(new StatInfo(values[0], values[1], LeaderboardVariant.TIME_SPAN_ALL_TIME));
	        lb.addListener(cl);
	        table2.add(lb);
	        
	        lb = new Label("" + values[1], labelStyle);
	        lb.setAlignment(Align.center);
	        lb.setUserObject(new StatInfo(values[0], values[1], LeaderboardVariant.TIME_SPAN_ALL_TIME));
	        lb.addListener(cl);
	        table2.add(lb);
	        
	        lb = new Label("" + values[2], labelStyle);
	        lb.setAlignment(Align.center);
	        lb.setUserObject(new StatInfo(values[0], values[1], LeaderboardVariant.TIME_SPAN_DAILY));
	        lb.addListener(cl);
	        table2.add(lb);
	        
	        lb = new Label("" + values[3], labelStyle);
	        lb.setAlignment(Align.center);
	        lb.setUserObject(new StatInfo(values[0], values[1], LeaderboardVariant.TIME_SPAN_WEEKLY));
	        lb.addListener(cl);
	        table2.add(lb);
	        
	        lb = new Label("" + values[4], labelStyle);
	        lb.setAlignment(Align.center);
	        lb.setUserObject(new StatInfo(values[0], values[1], LeaderboardVariant.TIME_SPAN_ALL_TIME));
	        lb.addListener(cl);
	        table2.add(lb);
	*/        
	        synchronized(game.pref) {
	        	game.pref.Synchronize(values[0], values[1], values[4], values[5], values[6], values[7]);
	        }
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//screen.SyncProcess = true;
			//temp = screen.btnWorld.text; 
			//screen.btnWorld.text = "Sync with Google...";
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			synchronized(game) {
				ScoreScreen screen = (ScoreScreen) game.scoreScreen;
				synchronized(screen) {
					screen.WorldRecordsFilled = true;
					screen.SyncProcess = false;
					//screen.btnWorld.text = "World records";
					screen.FillPersonalRecords();
					/*screen.btnPersonal.setChecked(false);
					screen.btnWorld.setChecked(true);
					screen.table1.setVisible(false);
					screen.table2.setVisible(true);*/
				}
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
	    	for(int player = 2; player < 5; player += 2)
	    		for(int color = player == 2 ? 5 : 6; color < 8; color++) {
	    			if (isCancelled())
	    				return null;
	    			int resID = getResources().getIdentifier("leaderboard_" + player + "_players_" + color + "_colors", "string", getPackageName());
	    			String str = getString(resID);
	    			
	    			int score = 0;
	    			int scoreDaily = 0;
					int scoreWeekly = 0;

	    			PendingResult<Leaderboards.LoadPlayerScoreResult> result3 =
	    					Games.Leaderboards.loadCurrentPlayerLeaderboardScore(/*mGoogleApiClient*/gameHelper.getApiClient(), str, LeaderboardVariant.TIME_SPAN_ALL_TIME,
	    	                LeaderboardVariant.COLLECTION_PUBLIC);

	    			if(result3 != null) {
	    				LoadPlayerScoreResult res = result3.await();

	    				//String str1 = res.getStatus().getStatusMessage();
	    				//Gdx.app.log("123", str1);
	    				if(res.getStatus().isSuccess()) {
							LeaderboardScore ls = res.getScore();
							if(ls != null)
								score = (int) ls.getRawScore();
	    				}
					}
					
					resID = getResources().getIdentifier("event_" + player + "_players_" + color + "_colors__wins", "string", getPackageName());
					str = getString(resID);
					PendingResult<Events.LoadEventsResult> wins_result = Games.Events.loadByIds(/*mGoogleApiClient*/gameHelper.getApiClient(), true, str);
					int wins = 0;
					if(wins_result != null) {
						EventBuffer buf = wins_result.await().getEvents();
						if(buf != null) {
							wins = (int) buf.get(0).getValue();
							buf.release();
						}
					}

					resID = getResources().getIdentifier("event_" + player + "_players_" + color + "_colors__losses", "string", getPackageName());
					str = getString(resID);
					PendingResult<Events.LoadEventsResult> losses_result = Games.Events.loadByIds(/*mGoogleApiClient*/gameHelper.getApiClient(), true, str);
					int losses = 0;
					if(losses_result != null) {
						EventBuffer buf = losses_result.await().getEvents();
						if(buf != null) {
							losses = (int) buf.get(0).getValue();
							buf.release();
						}
					}

					publishProgress(player, color, scoreDaily, scoreWeekly, score, wins, losses, 0);
	    		}
			return null;			
		}
	}
	
	private class SynchronizeAsync extends AsyncTask<Integer, Void, Void> {
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Integer... val) {
			//0-player, 1-color, 2-score, 3-wins, 4-losses, 5-draws
			int resID = getResources().getIdentifier("leaderboard_" + val[0] + "_players_" + val[1] + "_colors", "string", getPackageName());
			String str = getString(resID);

			PendingResult<Leaderboards.LoadPlayerScoreResult> result =
					Games.Leaderboards.loadCurrentPlayerLeaderboardScore(/*mGoogleApiClient*/gameHelper.getApiClient(), str, LeaderboardVariant.TIME_SPAN_ALL_TIME,
	                LeaderboardVariant.COLLECTION_PUBLIC);

			int score = 0;
			if(result != null) {
				LoadPlayerScoreResult res = result.await();

				if(res.getStatus().isSuccess()) {
					LeaderboardScore ls = res.getScore();
					if(ls != null)
						score = (int) ls.getRawScore();
				}
			}
			
			resID = getResources().getIdentifier("event_" + val[0] + "_players_" + val[1] + "_colors__wins", "string", getPackageName());
			str = getString(resID);
			PendingResult<Events.LoadEventsResult> wins_result = Games.Events.loadByIds(/*mGoogleApiClient*/gameHelper.getApiClient(), true, str);
			int wins = 0;
			if(wins_result != null) {
				EventBuffer buf = wins_result.await().getEvents();
				if(buf != null) {
					wins = (int) buf.get(0).getValue();
					buf.release();
				}
			}

			resID = getResources().getIdentifier("event_" + val[0] + "_players_" + val[1] + "_colors__losses", "string", getPackageName());
			str = getString(resID);
			PendingResult<Events.LoadEventsResult> losses_result = Games.Events.loadByIds(/*mGoogleApiClient*/gameHelper.getApiClient(), true, str);
			int losses = 0;
			if(losses_result != null) {
				EventBuffer buf = losses_result.await().getEvents();
				if(buf != null) {
					losses = (int) buf.get(0).getValue();
					buf.release();
				}
			}

			resID = getResources().getIdentifier("event_" + val[0] + "_players_" + val[1] + "_colors__draws", "string", getPackageName());
			str = getString(resID);
			PendingResult<Events.LoadEventsResult> draws_result = Games.Events.loadByIds(/*mGoogleApiClient*/gameHelper.getApiClient(), true, str);
			int draws = 0;
			if(draws_result != null) {
				EventBuffer buf = draws_result.await().getEvents();
				if(buf != null) {
					draws = (int) buf.get(0).getValue();
					buf.release();
				}
			}

			return null;			
		}
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(gameHelper == null)
			gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.setConnectOnStart(false);
		//gameHelper.enableDebugLog(true);
        // Create the Google API Client with access to Games
/*        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();*/

		// запретить отключение экрана без использования дополнительных
		// разрешений (меньше разрешений – больше доверие к приложению)
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		game = new BattleOfColorsGame(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
        config.useCompass = false;
		initialize(game, config);
		gameHelper.setup(this);
		gameHelper.setShowErrorDialogs(true);
		//gameHelper.setMaxAutoSignInAttempts(99);
		//gameHelper.beginUserInitiatedSignIn();

		game.allTime = LeaderboardVariant.TIME_SPAN_ALL_TIME;
		game.weekly = LeaderboardVariant.TIME_SPAN_WEEKLY;
		game.daily = LeaderboardVariant.TIME_SPAN_DAILY;
		
		game.language = 0;
		if(Locale.getDefault().getLanguage().equals("ru"))
			game.language = 1;
		
		try {
			game.versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			//int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		/*		eventsResultCB = new ResultCallback<Events.LoadEventsResult>() {
			@Override
			public void onResult(LoadEventsResult res) {
            	if(res == null)
            		return;
				if(res.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK)
					return;
	            EventBuffer c = res.getEvents();
                if(c == null)
                	return;
				for(int i = 0; i < c.getCount(); i++) {
	            	Event event = c.get(i);
					if(event == null)
						continue;
					
					String str = event.getEventId();
					long val = event.getValue();
	            	Gdx.app.log("" + str, "" + val);					
	            }
				//c.close();
			}
    	};
    	
    	playerScoreResultCB = new ResultCallback<LoadPlayerScoreResult>() {

            @Override
            public void onResult(LoadPlayerScoreResult res) {
            	if(res == null)
            		return;
            	if(res.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK)
					return;
                LeaderboardScore c = res.getScore();
                if(c == null)
                	return;
                score = (int) c.getRawScore();
            }
         };*/
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//if(!mGoogleApiClient.isConnected())
		//	mGoogleApiClient.connect();
		
		gameHelper.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		//if (mGoogleApiClient.isConnected())
        //    mGoogleApiClient.disconnect();
		gameHelper.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// здесь gameHelper принимает решение о подключении, переподключении или
		// отключении от игровых сервисов, в зависимости от кода результата
		// Activity
		
		gameHelper.onActivityResult(requestCode, resultCode, data);
		
		/*if (resultCode == Activity.RESULT_OK) {
			mGoogleApiClient.connect();
        } else if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
        	mGoogleApiClient.connect();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // User cancelled.
            mGoogleApiClient.disconnect();
        }*/
	}

	@Override
	public boolean getSignedInGPGS() {
		return gameHelper.isSignedIn();
		//return mGoogleApiClient != null && mGoogleApiClient.isConnected();
	}
	
	public boolean isConnectingGPGS() {
        //return mGoogleApiClient != null && mGoogleApiClient.isConnected();
		return gameHelper.isConnecting();
	}

    private void beginUserInitiatedSignIn() {
        /*if (mGoogleApiClient.isConnected()) {
            // nothing to do
            return;
        }

        // We don't have a pending connection result, so start anew.
        mGoogleApiClient.connect();*/
    }
    
	@Override
	public void loginGPGS() {
		try {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// инициировать вход пользователя. Может быть вызван диалог
					// входа. Выполняется в UI-потоке
					gameHelper.beginUserInitiatedSignIn();

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void submitEventGPGS(int players, int colors, String event, int inc) {
		if(!/*getSignedInGPGS()*/gameHelper.isSignedIn())
			return;
		// формат - event_2_players_5_colors_wins
		int resID = getResources().getIdentifier("event_" + players + "_players_" + colors + "_colors__" + event, "string", getPackageName());
		String str = getString(resID);
		Games.Events.increment(gameHelper.getApiClient()/*mGoogleApiClient*/, str, inc);
	}
	
	public int loadEventGPGS(int players, int colors, String event) {
		if(!/*getSignedInGPGS()*/gameHelper.isSignedIn())
			return 0;
		int resID = getResources().getIdentifier("event_" + players + "_players_" + colors + "_colors__" + event, "string", getPackageName());
		String str = getString(resID);
		PendingResult<Events.LoadEventsResult> result = Games.Events.loadByIds(/*mGoogleApiClient*/gameHelper.getApiClient(), true, str);//.setResultCallback(eventsResultCB);
		if(result == null)
			return 0;
		EventBuffer buf = result.await().getEvents();
		if(buf == null)
			return 0;
		int ret = (int) buf.get(0).getValue();
		buf.release();		
		return ret;
    }
	
	@Override
	public void submitScoreGPGS(int players, int colors, int score) {
		if(!/*getSignedInGPGS()*/gameHelper.isSignedIn())
			return;
		// формат - leaderboard_2_players_5_colors
		int resID = getResources().getIdentifier("leaderboard_" + players + "_players_" + colors + "_colors", "string", getPackageName());
		String str = getString(resID);
		//startActivityForResult
		Games.Leaderboards.submitScore(gameHelper.getApiClient()/*mGoogleApiClient*/, str, score);
	}

	@Override
	public int SynchronizeGPGS(int players, int colors, int score, int wins, int losses, int draws) {
		SynchronizeAsync sync = new SynchronizeAsync();
		sync.execute(players, colors, score, wins, losses, draws);
		
		return 0;
	}
		
	@Override
	public void FillScoreGPGS() {
		if(!/*getSignedInGPGS()*/gameHelper.isSignedIn())
			return;
		if(fillScore != null)
			StopFillScoreGPGS();
		fillScore = new GetScoreAsync();
		fillScore.execute();
    }

	@Override
	public void StopFillScoreGPGS() {
		if(fillScore != null) {
			fillScore.cancel(false);
			fillScore = null;
		}
    }
	
	@Override
	public void unlockAchievementGPGS(String achievementId) {
		if(!/*getSignedInGPGS()*/gameHelper.isSignedIn())
			return;
		Games.Achievements.unlock(/*mGoogleApiClient*/gameHelper.getApiClient(), achievementId);
	}

	@Override
	public void getLeaderboardGPGS() {
		if(!/*getSignedInGPGS()*/gameHelper.isSignedIn())
			return;
		startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(/*mGoogleApiClient*/gameHelper.getApiClient()), 100);
	}
	
	@Override
	public void getLeaderboard(int player, int color) {
		if(!/*getSignedInGPGS()*/gameHelper.isSignedIn())
			return;
		int resID = getResources().getIdentifier("leaderboard_" + player + "_players_" + color + "_colors", "string", getPackageName());
		String str = getString(resID);
		startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient()/*mGoogleApiClient*/, str, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC), 100);
	}

	@Override
	public void getAchievementsGPGS() {
		if(!/*getSignedInGPGS()*/gameHelper.isSignedIn())
			return;
		startActivityForResult(Games.Achievements.getAchievementsIntent(/*mGoogleApiClient*/gameHelper.getApiClient()), 101);
	}

/*	@Override
	public void onConnected(Bundle bundle) {
		
	}*/
	    
	@Override
	public void onSignInSucceeded() {
		FillScoreGPGS();
/*		synchronized(game) {
			MainScreen scr = (MainScreen) game.mainScreen;
			synchronized(scr) {
				scr.SetLeaderboard(true);
			}
		}*/
	}

	@Override
	public void onSignInFailed() {
/*		synchronized(game) {
			MainScreen scr = (MainScreen) game.mainScreen;
			synchronized(scr) {
				scr.SetLeaderboard(false);
			}
		}*/
	}

	@Override
	public void connect() {
		try {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					gameHelper.reconnectClient();
			        /*if (!mGoogleApiClient.isConnected()) {
			        	mGoogleApiClient.connect();
			        } else {
			            mGoogleApiClient.reconnect();
			        }*/
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}*/

}
