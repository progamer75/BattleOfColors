package Screens;

import com.BattleOfColors.game.ActionResolver;
import com.BattleOfColors.game.BattleOfColorsGame;
import com.BattleOfColors.game.MyButton;
import com.BattleOfColors.game.PlayStage;
import com.BattleOfColors.game.StatInfo;
import com.BattleOfColors.game.PlayStage.OnHardKeyListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Align;

public class ScoreScreen implements Screen {
	final BattleOfColorsGame game;
	private PlayStage stage;
    public Table mainTable, table1/*, table2*/;
    public LabelStyle labelStyle, labelStyleHead;
    public MyButton /*btnPersonal, btnWorld,*/ btnOK;
    private int tmpPlayer, tmpColor, progress_v;
	public boolean WorldRecordsFilled;
	public boolean SyncProcess; 
	
	private class Scores {
		int daily, weekly, allTime;
		Scores() {
		};
/*		Scores(int daily, int weekly, int allTime) {
			this.daily = daily;
			this.weekly = weekly;
			this.allTime = allTime;
		}*/
	};
	private Scores[][] scores;
	private ClickListener clLabel;
    
	public ScoreScreen(final BattleOfColorsGame gam) {
        game = gam;

        scores = new Scores[5][8];
        for(int i = 0; i < 5; i++)
        	for(int j = 0; j < 8; j++)
        		scores[i][j] = new Scores();
        	
        stage = new PlayStage(new ScreenViewport());

        mainTable = new Table();
        //mainTable.setDebug(true);
        mainTable.setFillParent(true);
        mainTable.setTransform(true);
        mainTable.setRound(true);
        mainTable.align(Align.topLeft);
        mainTable.pad(8);
        
        labelStyle = new LabelStyle();
        labelStyle.font = game.midiFont;
        labelStyle.fontColor = game.colDark;
        labelStyle.background = game.skin.newDrawable("btn_big", game.colWhite);
        
        labelStyleHead = new LabelStyle();
        labelStyleHead.font = game.miniFont;
        labelStyleHead.fontColor = game.colDark;
        labelStyleHead.background = game.skin.newDrawable("btn_big", game.colYellow);

        table1 = new Table();
        //table1.setDebug(true);
        table1.setFillParent(true);
        table1.setRound(true);
        table1.top();
        table1.defaults().expandX().uniformX().fill().space(2);

       /* table2 = new Table();
        //table2.setDebug(true);
        table2.setFillParent(true);
        table2.setRound(true);
        table2.top();
        table2.defaults().expandX().uniformX().fill().space(2);
*/
        //scrollPane = new ScrollPane(table1);
        	        	        
        //game.midiFont.setColor(col);

/*		btnPersonal = new MyButton("Your records", game.midiFont, btSt1);
    	btnPersonal.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
            	btnPersonal.setChecked(true);
            	btnWorld.setChecked(false);
            	table1.setVisible(true);
            	table2.setVisible(false);
            };
        });
    	Cell<MyButton> cell = mainTable.add(btnPersonal);
    	btnPersonal.SetBtnScale(cell, game.scale).space(8);
    	
		btnWorld = new MyButton("World records", game.midiFont, btSt1);
    	btnWorld.addListener(new ClickListener() {

			@Override
            public void clicked (InputEvent event, float x, float y) {
				if(SyncProcess) {
					btnWorld.setChecked(false);
					return;
				}	
				
            	if(!WorldRecordsFilled) {
	            	ActionResolver res = game.actionResolver;
	            	if((!res.getSignedInGPGS())&&(!res.isConnectingGPGS())) {
	            		res.loginGPGS();
	            	}
	            	if(!res.getSignedInGPGS()) {
	            		btnWorld.setChecked(false);
	            		return;
	            	}
	            		SyncProcess = true;
	            		btnWorld.text = "Sync with Google...";
	            		res.FillScoreGPGS();
	            		
            	} else {
            		btnPersonal.setChecked(false);
            		btnWorld.setChecked(true);
            		table1.setVisible(false);
            		table2.setVisible(true);
            	}
            };
        });
    	cell = mainTable.add(btnWorld);
    	btnWorld.SetBtnScale(cell, game.scale).space(8);
  */  	
		btnOK = new MyButton("OK", game.font, game.btnStyle);
    	btnOK.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
            	game.setScreen(game.mainScreen);
            };
        });
    	Table tableBottom = new Table();
    	tableBottom.setFillParent(true);
        
    	LabelStyle labelStyle2 = new LabelStyle();
        labelStyle2.font = game.midiFont;
        labelStyle2.fontColor = game.colWhite;
        String str;
        if(game.language == 0)
        	str = "Click on a row\nto open the leaderboard";
        else
        	str = "Кликните чтобы открыть\nтаблицу рекордов";
    	Label lb = new Label(str, labelStyle2);
    	//lb.setWrap(true);
    	tableBottom.add(lb).pad(16).center();
    	tableBottom.row();
    	
    	Cell<MyButton> cell = tableBottom.bottom().pad(16).add(btnOK);
		btnOK.SetBtnScale(cell, game.btnScale).center();
    	
    	table1.padTop(mainTable.getPrefHeight());
    	//table2.padTop(mainTable.getPrefHeight());
    	stage.addActor(mainTable);
    	stage.addActor(table1);
    	//stage.addActor(table2);
    	stage.addActor(tableBottom);
        stage.setHardKeyListener(new OnHardKeyListener() {          
            @Override
            public void onHardKey(int keyCode, int state) {
                if (keyCode == Keys.BACK && state == 1){
                	game.setScreen(game.mainScreen);
                }       
            }
        });
        
        clLabel = new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
            	Actor lb = event.getTarget();
            	if(lb == null)
            		return;
            	StatInfo info = (StatInfo) lb.getUserObject();
            	if(info == null)
            		return;

            	ActionResolver res = game.actionResolver;
/*            	if(!res.isConnectingGPGS())
            		res.connect();*/
            	if(!res.getSignedInGPGS())
            		res.loginGPGS();
            	if((res.getSignedInGPGS())/*&&(res.isConnectingGPGS())*/)
            		res.getLeaderboard(info.player, info.color);
            };
        };
        
		WorldRecordsFilled = false;
    }

	public void FillPersonalRecords() {
		String str1, str2, str3, str4, str5;
		if(game.language == 0) {
			str1 = "\nNumber of\nplayers\n";
			str2 = "Number of\ncolors";
			str3 = "Rating";
			str4 = "Wins";
			str5 = "Losses";
		} else {
			str1 = "\nКол-во\nигроков\n";
			str2 = "Кол-во\nцветов";
			str3 = "Рейтинг";
			str4 = "Побед";
			str5 = "Поражен.";
		};
		table1.clear();
        Label lb = new Label(str1, labelStyleHead);
        lb.setAlignment(Align.center);
        table1.add(lb);
        lb = new Label(str2, labelStyleHead);
        lb.setAlignment(Align.center);
        table1.add(lb);
        lb = new Label(str3, labelStyleHead);
        lb.setAlignment(Align.center);
        table1.add(lb);
        lb = new Label(str4, labelStyleHead);
        lb.setAlignment(Align.center);
        table1.add(lb);
        lb = new Label(str5, labelStyleHead);
        lb.setAlignment(Align.center);
        table1.add(lb);
/*        lb = new Label("Draws", labelStyleHead);
        lb.setAlignment(Align.center);
        table1.add(lb);*/

        for(int pl = 2; pl < 5; pl += 2)
        	for(int colors = pl == 2 ? 5 : 6; colors < 8; colors++) {
        		StatInfo stat = new StatInfo(pl, colors);
        		
		        table1.row();
		        lb = new Label("\n" + pl + "\n", labelStyle);
		        lb.setAlignment(Align.center);
		        lb.setUserObject(stat);
		        lb.addListener(clLabel);
		        table1.add(lb);
		        
	        	lb = new Label("\n" + colors + "\n", labelStyle);
		        lb.setAlignment(Align.center);
		        lb.setUserObject(stat);
		        lb.addListener(clLabel);
		        table1.add(lb);
		        
		        int val = game.pref.GetScore(pl, colors);
		        if(val < 0)
		        	val = 0;
		        lb = new Label("" + val, labelStyle);
		        lb.setAlignment(Align.center);
		        lb.setUserObject(stat);
		        lb.addListener(clLabel);
		        table1.add(lb);
		        
		        val = game.pref.GetWin(pl, colors);
		        lb = new Label("" + val, labelStyle);
		        lb.setAlignment(Align.center);
		        lb.setUserObject(stat);
		        lb.addListener(clLabel);
		        table1.add(lb);
		        
		        val = game.pref.GetLoss(pl, colors);
		        lb = new Label("" + val, labelStyle);
		        lb.setAlignment(Align.center);
		        lb.setUserObject(stat);
		        lb.addListener(clLabel);
		        table1.add(lb);
		        
/*		        val = game.pref.GetDraw(pl, colors);
		        lb = new Label("" + val, labelStyle);
		        lb.setAlignment(Align.center);
		        lb.setUserObject(stat);
		        lb.addListener(clLabel);
		        table1.add(lb);*/
        	}
	}
	
/*	private void FillTablesHead() {
		Label lb;
		
		table2.clear();
	    lb = new Label("\nNumber of\nplayers\n", labelStyleHead);
	    lb.setAlignment(Align.center);
	    table2.add(lb);
	    lb = new Label("Number of\ncolors", labelStyleHead);
	    lb.setAlignment(Align.center);
	    table2.add(lb);
	    lb = new Label("Daily\nbest score", labelStyleHead);
	    lb.setAlignment(Align.center);
	    table2.add(lb);
	    lb = new Label("Weekly\nbest score", labelStyleHead);
	    lb.setAlignment(Align.center);
	    table2.add(lb);
	    lb = new Label("All time\nbest score", labelStyleHead);
	    lb.setAlignment(Align.center);
	    table2.add(lb);
	}*/

/*private void FillTables() {
		FillPersonalRecords();
		Label lb;
	    ActionResolver res = game.actionResolver;
	    for(int pl = 2; pl < 5; pl += 2)
	    	for(int colors = pl == 2 ? 5 : 6; colors < 8; colors++) {
		        table2.row();
		        lb = new Label("" + pl, labelStyle);
		        lb.setAlignment(Align.center);
		        table2.add(lb);
		        
	        	lb = new Label("" + colors, labelStyle);
		        lb.setAlignment(Align.center);
		        table2.add(lb);
		        
		        int val = scores[pl][colors].daily;//res.loadScoreGPGS(pl, colors, game.daily);
		        lb = new Label("" + val, labelStyle);
		        lb.setAlignment(Align.center);
		        table2.add(lb);
		        
		        val = scores[pl][colors].weekly;//res.loadScoreGPGS(pl, colors, game.weekly);
		        lb = new Label("" + val, labelStyle);
		        lb.setAlignment(Align.center);
		        table2.add(lb);
		        
		        val = scores[pl][colors].allTime;//res.loadScoreGPGS(pl, colors, game.allTime);
		        lb = new Label("" + val, labelStyle);
		        lb.setAlignment(Align.center);
		        table2.add(lb);
	    	}			
		}
*/
	
	@Override
	public void show() {
		Gdx.graphics.setContinuousRendering(true);
		Gdx.input.setInputProcessor(stage);
    	//btnPersonal.setChecked(true);
    	//btnWorld.setChecked(false);
    	table1.setVisible(true);
    	//table2.setVisible(false);
    	btnOK.setChecked(false);
    	WorldRecordsFilled = false;
    	SyncProcess = false;
    //	FillTablesHead();
    	FillPersonalRecords();
    	
    	ActionResolver res = game.actionResolver;
 /*   	if(!res.isConnectingGPGS())
    		res.connect();*/
//    	if(!res.getSignedInGPGS())
//    		res.loginGPGS();
//    	if((res.getSignedInGPGS())/*&&(res.isConnectingGPGS())*/) {
//    		SyncProcess = true;
//    		//btnWorld.text = "Sync with Google...";
//    		res.FillScoreGPGS();
//    	}
/*		for(int numPlayers = 2; numPlayers < 5; numPlayers += 2)
			for(int colors = numPlayers == 2 ? 5 : 6; colors < 8; colors++) */{
//				game.pref.Synchronize(tmpPlayer, tmpColor);
			}
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    /*    Batch batch = stage.getBatch();
        batch.begin();
        batch.draw(background, 0, 0, game.screenWidth, game.screenHeight);
        batch.draw(nameTxt, 0, game.screenHeight - nameHeight, game.screenWidth, nameHeight);
        batch.end();*/
       // Gdx.app.log("GameScreen FPS", (1/delta) + "");
        if(false) {
        	//tmpColor++;
        	progress_v++;
        	Batch batch = stage.getBatch();
        	batch.begin();
        	float height = game.skin.getDrawable("btn_long").getMinHeight() * game.scale;
        	float width = 100 * 5 * game.scale;
        	float x = (int)(stage.getWidth() - width) / 2;
        	float y = (int)(stage.getHeight() - height) / 2;
        	game.skin.getDrawable("btn_long").draw(batch, x, y, width, height);
        	game.skin.getDrawable("btn_long_on").draw(batch, x, y, width * progress_v/5, height);
        	game.midiFont.draw(batch, "Synchronization with Google Play " + progress_v * 20 + " %",
        			x, y + height / 2 + game.midiFont.getCapHeight() / 2, width, Align.center, false);
        	batch.end();
        	
 /*       	if (tmpColor == 8) {
        		tmpPlayer += 2;
        		if(tmpPlayer > 4) {
        	    	FillTables();
        	    	SyncNeeded = false;
        	    	return;
        		}
        		tmpColor = 6;
        	}
        	game.pref.Synchronize(tmpPlayer, tmpColor);
        	ActionResolver res = game.actionResolver;
        	scores[tmpPlayer][tmpColor].daily = res.loadScoreGPGS(tmpPlayer, tmpColor, game.daily);
        	scores[tmpPlayer][tmpColor].weekly = res.loadScoreGPGS(tmpPlayer, tmpColor, game.weekly);
        	scores[tmpPlayer][tmpColor].allTime = res.loadScoreGPGS(tmpPlayer, tmpColor, game.allTime);*/
    		return;
        }
        stage.act(delta);
        stage.draw();
    }

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		game.actionResolver.StopFillScoreGPGS();		
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}