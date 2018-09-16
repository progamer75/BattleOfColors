package Screens;

import com.BattleOfColors.game.BattleOfColorsGame;
import com.BattleOfColors.game.GameBoard;
import com.BattleOfColors.game.GameCell;
import com.BattleOfColors.game.HSV;
import com.BattleOfColors.game.IntVector;
import com.BattleOfColors.game.MyButton;
import com.BattleOfColors.game.PlayStage;
import com.BattleOfColors.game.YesAction;
import com.BattleOfColors.game.PlayStage.OnHardKeyListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen, InputProcessor{
	private enum GameResult {Win, Loss, Draw}

	private BattleOfColorsGame game;
	private SpriteBatch batch;
	private GameBoard gameBoard;
	private int activePlayer;
	public int maxAIDepth = 1;
	private int cell_size;


	private PlayStage stage;
	private Label lbStart;
	private Image btns[];
    private Table tabBottom, tabTop, tabButtons;
    private LabelStyle labelStyle;
    private Label[] lbScore;
    private IntVector coord = new IntVector(0, 0);
	private long maxTime = 2000; //максимальное время обдумывания в мс
	private boolean blink, blinking;
	private int blinkStep;
	private final int animationFrames = 6;
	private long startTimeBlink;
	private boolean rendering;
	private int activePlayerColor;
	private int itogo;
	private final float bgR = 0.2f, bgG = 0.2f, bgB = 0.2f;
	private float kkk;
	
	private FrameBuffer buffer;

	public GameScreen(final BattleOfColorsGame floodGame) {
		blink = false;
		game = floodGame;
		GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder((int)game.screenWidth,
				(int)game.screenHeight);
		frameBufferBuilder.addBasicColorTextureAttachment(Format.RGBA8888);
		buffer = frameBufferBuilder.build();
		//buffer = new FrameBuffer(Format.RGBA8888, (int)game.screenWidth, (int)game.screenHeight, false);
		OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);
        
        batch = new SpriteBatch(1000, game.shader);
        batch.setProjectionMatrix(camera.combined);

	    stage = new PlayStage(new ScreenViewport());

        tabTop = new Table();
        tabTop.setFillParent(true);
        tabTop.top();
        tabTop.padLeft(8*game.scale).padRight(8*game.scale).padTop(2*game.scale);

	    tabBottom = new Table();
        tabBottom.setFillParent(true);
        tabBottom.bottom();
		tabBottom.padLeft(8*game.scale).padRight(8*game.scale).padBottom(4*game.scale);
		tabBottom.setDebug(false);

		tabButtons = new Table();
		tabButtons.setFillParent(true);
		tabButtons.bottom();
		tabButtons.setDebug(false);

		labelStyle = new LabelStyle();
        labelStyle.font = game.midiFont;
        
        LabelStyle lbs = new LabelStyle();
        lbs.font = game.font;
        lbStart = new Label("", lbs);
        lbStart.setFillParent(true);
		lbStart.setAlignment(Align.center);
		lbStart.setColor(Color.BLACK);
		lbStart.setFontScale(1.5f);
		lbStart.setTouchable(Touchable.disabled);
		
		stage.addActor(tabTop);
		stage.addActor(tabBottom);
		//stage.addActor(tabButtons);
        stage.addActor(lbStart);

        stage.setHardKeyListener(
        	new OnHardKeyListener() {
	            @Override
	            public void onHardKey(int keyCode, int state) {
	                if ((keyCode == Keys.BACK) && (state == 1)){
	                	game.dlgYesNo.show(stage);
	                }
	                
	                if ((keyCode == Keys.MENU) && (state == 1)){
	                    game.dlgHelp.show(stage);    
	                }
	            }
        	}
        );
		game.dlgYesNo.SetAction(new YesAction() {
			@Override
			public void Action() {
				//TODO при прерывании игры засчитывать поражение
				// пока просто выходим, не засчитываем за поражение
				GotoMain();
			}
		});

        //playerScore = new int[4];
	}
	
    private void GameCycle(int color) {
    	activePlayer = 0;
    	activePlayerColor = color;
		rendering = true;
		Gdx.graphics.setContinuousRendering(true);
	}

	private void GameOver() {
		//TODO При ококнчании игры показывать максимальный рейтинг и дату
		//int myScore = gameBoard.GetPlayerScore1000(0);
		int old_score = game.pref.GetScore(game.numPlayer, game.maxColors);
		if(old_score < 0)
			old_score = 1000;

		if(game.order == 0)
			game.order = 1;
		else
			game.order = 0;

		WindowStyle ws = new WindowStyle();
		ws.titleFont = game.font;
		
		final Dialog dialog = new Dialog("", ws) {
		    protected void result (Object object) {
		    }
	    };

		String strBack, strNew;
		if(game.language == 0) {
			strBack = "Main menu";
			strNew = "New game";
		} else {
			strBack = "Главное меню";
			strNew = "Новая игра";
		}

	    MyButton but = new MyButton(strBack, game.midiFont, game.btnStyle);
	    dialog.button(but, game);
	    Cell<MyButton> cell = dialog.getButtonTable().getCell(but);
	    but.SetBtnScale(cell, game.btnScale * 0.6f);
		but.addListener(
			new ClickListener() {
				@Override
				public void clicked (InputEvent event, float x, float y) {
					GotoMain();
				}}
		);

		MyButton butNew = new MyButton(strNew, game.midiFont, game.btnStyle);
		dialog.button(butNew, game);
		cell = dialog.getButtonTable().getCell(butNew);
		butNew.SetBtnScale(cell, game.btnScale * 0.6f);
		butNew.addListener(
			new ClickListener() {
				@Override
				public void clicked (InputEvent event, float x, float y) {
					StopGame();
					NewGame();
				}}
		);

    	Label lb = new Label("", labelStyle);
    	Label lb2 = new Label("", labelStyle);
        LabelStyle labelStyle2 = new LabelStyle();
        labelStyle2.font = game.miniFont;
    	Label lbRash = new Label("", labelStyle2);
    	lb.setAlignment(Align.center);
    	lb.setColor(game.colRed);
    	//lb2.setFontScale(0.5f);
    	lbRash.setAlignment(Align.right);
    	lbRash.setColor(game.colWhite);
    	lb2.setAlignment(Align.right);
    	lb2.setColor(game.colRed);
    	
    	String str1, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16, str17, str18, str19, str20, str21;
    	if(game.language == 0) {
        	str1 = "Your current rating: ";
    		str2 = "your hexagons multiplied by 100 = ";
        	str3 = "\n divided by the total number of hexagons (";
        	str4 = "\n multiplied by the number of players (";
        	str5 = "\n bonus for the difficulty of the game - 30%";
        	str6 = "In this game you score: ";
			//str6 = "You score ";
        	str7 = " Wins ";
        	str8 = " Losses ";
        	str9 = " Score ";
        	str10 = "Draw!";
        	str11 = "Congratulation! You won!";
        	str12 = "You lose";
        	str13 = " You";
        	str14 = " Player ";
			str15 = "Congratulation! You won!!!";
			str16 = "You lose";
        	str17 = "\nYour score ";
        	str18 = "\nNEW PERSONAL RECORD !!!";
        	str19 = "\nYour personal record - ";
			str20 = " Game statistics:";
        	str21 = "You are signed-in with Google. Your achievements and scores will be saved automatically.";
			strBack = "Go to main menu";
			strNew = "New game";
    	} else {
			str1 = "Ваш текущий рейтинг: ";
    		str2 = "ваши клетки, умноженные на 100 = ";
			str3 = "\n деленные на общее число клеток (";
			str4 = "\n умноженные на число игроков (";
			str5 = "\n бонус за сложность - 30%";
			str6 = "За эту игру вы получили: ";
			str7 = " Побед ";
			str8 = " Поражений ";
			str9 = " Очков ";
			str10 = "Ничья!";
			str11 = "Поздравляю! Вы победили!";
			str12 = "Вы проиграли";
			str13 = " Вы";
			str14 = " Игрок ";
			str15 = "Поздравляю! Вы выиграли!!!";
			str16 = "Вы проиграли";
			str17 = "\nВаши очки ";
			str18 = "\nНОВЫЙ ПЕРСОНАЛЬНЫЙ РЕКОРД !!!";
			str19 = "\nВаш персональный рекорд - ";
			str20 = " Статистика игры:";
			str21 = "Вы вошли в Google. Ваши очки будут сохранены автоматически.";
			strBack = "Возврат в главное меню";
			strNew = "Новая игра";
		}

		CharSequence str = "";
		int sc = gameBoard.GetPlayerScore(0, false, 0);
		int tot = gameBoard.width * gameBoard.height;
		int wonIndx = 0;
		GameResult result = GameResult.Draw;
		int maxScore = sc;
		for(int i = 1; i < game.numPlayer; i++) {
			int oppScore = gameBoard.GetPlayerScore(i, false, 0);
			if(maxScore != oppScore)
				result = GameResult.Win;
			if(maxScore < oppScore) {
				wonIndx = i;
				maxScore = oppScore;
			}
		}

		if(result!=GameResult.Draw)
			if(wonIndx==0)
				result = GameResult.Win;
			else
				result = GameResult.Loss;

		CharSequence strRecord = "";
		int myScore = 0;
		float p = (float)sc / (float)tot * (float)game.numPlayer / 2.0f;
		int bot_score = 1000;
		if(game.difficult == BattleOfColorsGame.Difficulties.Normal)
			bot_score = 2000;
		switch(result) {
			case Win: myScore = Math.round(100.f * p * (float)bot_score / old_score);
				lb.setText(str11);
				strRecord = strRecord + str15;
				game.pref.AddWin(game.numPlayer, game.maxColors, 1);
				game.actionResolver.submitEventGPGS(game.numPlayer, game.maxColors, "wins", 1);
				break;
			case Loss: myScore = Math.round(100.f * (p - 1.0f) * (float)old_score / bot_score);
				strRecord = strRecord + str16;
				game.pref.AddLoss(game.numPlayer, game.maxColors, 1);
				game.actionResolver.submitEventGPGS(game.numPlayer, game.maxColors, "losses", 1);
				lb.setText(str12);
				break;
			case Draw: myScore = Math.round(25.f * ((float)bot_score / old_score - (float)old_score / bot_score));
				game.pref.AddDraw(game.numPlayer, game.maxColors, 1);
				game.actionResolver.submitEventGPGS(game.numPlayer, game.maxColors, "draws", 1);
				lb.setText(str10);
				break;
		}
		int new_score = myScore + old_score;
		game.pref.SetScore(game.numPlayer, game.maxColors, new_score);
		game.actionResolver.submitScoreGPGS(game.numPlayer, game.maxColors, new_score);
        //TODOo правила рассчета рейтинга:
        // Ra(new) = 100 Rb/Ra*p - при победе
        // Ra(new) = -100 Ra/Rb*(1-p) - при поражении
        // Ra(new) = 25 (Rb/Ra - Ra/Rb) - ничья
        // где Rb - рейтинг соперника (easy - 1000, normal - 2000), p - доля занятых ячеек

    	CharSequence strRash = "";// = str2 + sc;
    	//strRash = strRash + str3 + tot + ") = " + String.format("%.1f", (float)sc / (float)(tot));
    	//strRash = strRash + str4 + game.numPlayer + ") = " + String.format("%.1f", (float)(sc) / (float)(tot * game.numPlayer));
    	lbRash.setText(strRash);
    	str = str6 + myScore + "\n" +
			str1 + new_score + "\n";

    	LabelStyle lbss = new LabelStyle();
    	lbss.font = game.miniFont;
    	lbss.fontColor = game.colDark;
    	lbss.background = game.skin.getDrawable("btn_big");

    	//game.pref.Synchronize(game.numPlayer, game.maxColors, -1000, -1000, -1000, -1000);
    	lb2.setText(str);
    	
    	float pad = 20 * game.scale;
    	dialog.getContentTable().add(lb).pad(pad).center().row();
    	dialog.getContentTable().add(lbRash).right().padLeft(pad).padRight(pad).row();
    	dialog.getContentTable().add(lb2).left().padLeft(pad).padRight(pad).row();
    	
    	dialog.getButtonTable().padTop(2 * pad).padBottom(pad);
		dialog.setModal(true);
		dialog.setMovable(false);
		dialog.setResizable(false);
		dialog.setBackground(game.skin.getTiledDrawable("bgTex"));
		dialog.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
		dialog.show(stage);

	}

	private void SetButtons() {
		for(int i = 0; i < game.maxColors; i++)
			btns[i].setVisible(true);

		HSV hsv;
		String str, str1, str2;
		if(game.language == 0) {
			str1 = "You-";
			str2 = "Player";
		} else {
			str1 = "Вы-";
			str2 = "Игрок";
		}
		for(int player = 0; player < game.numPlayer; player++) {
        	int color = gameBoard.GetPlayerColor(player);    		
    		btns[color].setVisible(false);
    		
    		hsv = game.cellColors[gameBoard.GetPlayerColor(player)];
    		lbScore[player].setColor(hsv.r, hsv.g, hsv.b, 1f);
    		str = "";
    		if(player == 0)
    			str = str1;
    		else
    			str = str2 + player + "-";
    		lbScore[player].setText(str + gameBoard.GetPlayerScore(player, false, 0));
		}
	}

	private int DoAI() {
		itogo = 0;
		int sum = 0;
		for(int player = 0; player < game.numPlayer; player++) {
			 sum += gameBoard.GetPlayerScore(player, false, 0);
		}
		boolean endshpil = ((float)sum / (gameBoard.height * gameBoard.width)) > 0.32f;
		
		long startTime = TimeUtils.millis();
		int tmp_activePlayer = activePlayer;
//		if(activePlayer == 1) {
//			maxAIDepth = 2;
			kkk = 5;
//		}
//		else {
			//maxAIDepth = 2;
			kkk = 0f;
//		}
		gameBoard.SetAI(true);
		
		int[] ps = new int[4];

		//если не стоит, то подсчитываем результативность хода и заканчиваем
		
		/*		int[] playerScore = new int[4];
		for(int player = 0; player < game.numPlayer; player++)
			playerScore[player] = -1000;*/
		int bestColor = -1;
		int bestScore = -1000;
		int rezColor = -1;
		for(int color = 0; color < game.maxColors; color++) {
			gameBoard.Copy2tmpColor();

			//отфильтруем занятые цвета
			boolean continueColor = false;
			for(int player = 0; player < game.numPlayer; player++) {
	        	if(color == gameBoard.GetPlayerColor(player)) {
	        		continueColor = true;
	        		break;
	        	}
			}
			if(continueColor)
				continue;
			
			rezColor = color;
			
			if(!gameBoard.IsBorder(activePlayer, color)) // фильтруем если этот цвет не граничит с нашей областью
				continue;								// это выполняется быстро

			//Gdx.app.log("start", "" + activePlayer + " / " + color);
			boolean gameOver = PlayerStep(color, true);

			int score = 0;
			//score = AIStep(0, startTime);
			ps = AIStep(0, startTime, endshpil);
			activePlayer = tmp_activePlayer;
			score = ps[activePlayer];
			if(score == -1000) {
				break;
			}
//			Gdx.app.log("Color ", color + ", score " + score);
			if(score > bestScore) {
				bestScore = score;
				bestColor = color;
			}
		}
		
		gameBoard.SetAI(false);
		
		//Gdx.app.log("AI", "" + (TimeUtils.millis()-startTime) + " / " + itogo + " / " + endshpil);
		
		//System.gc();
		Runtime.getRuntime().gc(); // 40 мс

		if(bestColor == -1)
			return rezColor;
		else
			return bestColor;
	}
	
	private int[] AIStep(int depth, long startTime, boolean endshpil) {
		boolean gameOver = false;
		int tmp_activePlayer = activePlayer;
		int result;
		
		int[] ps = new int[4];
		int[] playerScore = new int[4];
		for(int player = 0; player < game.numPlayer; player++)
			playerScore[player] = -1000;
		
		GameCell[][] tmpBoard = new GameCell[gameBoard.width][gameBoard.height];
		for(int x = 0; x < gameBoard.width; x++)
			for(int y = 0; y < gameBoard.height; y++)
				tmpBoard[x][y] = new GameCell(0);

		//while(true) {

// включить, если скоро конец (% занятых от общего числа)
			if(endshpil) {
				activePlayer++;
				if(activePlayer == game.numPlayer)
					activePlayer = 0;
				tmp_activePlayer = activePlayer;
			}
			
			gameBoard.Copytmp2Board(tmpBoard); // от начала функции < 1 мс

			int bestColor = 0;
			int bestScore = -1000;
			int rezColor = 0;
			
			for(int color = 0; color < game.maxColors; color++) {
				//отфильтруем занятые цвета
				boolean continueColor = false;
				for(int player = 0; player < game.numPlayer; player++) {
		        	if(color == gameBoard.GetPlayerColor(player)) {
		        		continueColor = true;
		        		break;
		        	}
				}
				if(continueColor)
					continue;
				
				rezColor = color;
				
				if(!gameBoard.IsBorder(activePlayer, color)) // фильтруем если этот цвет не граничит с нашей областью
					continue;

				//Gdx.app.log("AI-pre-step", "" + (TimeUtils.millis()-startTime));
				gameOver = PlayerStep(color, true);
				//Gdx.app.log("AI-after-step", "" + (TimeUtils.millis()-startTime));
				//Gdx.app.log("AI" + depth, "" + activePlayer + " / " + color + " / " + (TimeUtils.millis()-startTime));
				//Gdx.app.log("player #" + activePlayer, " Time - " + deltaTime + " Heap - " + Gdx.app.getJavaHeap());
				int score = 0;

				// проверяем стоит ли идти дальше
				//TODO: НА СЧЕТ GAMEOVER ПРОВЕРИТЬ!!!

				long curTime = TimeUtils.millis();
				long deltaTime = curTime - startTime;

				if((depth >= maxAIDepth)||(gameOver)/*||(deltaTime > maxTime )*/) {
					for(int player = 0; player < game.numPlayer; player++) {
						ps[player] = 0;
						//gameBoard.FillHiddenCells(player);
					}
					//gameBoard.FillHiddenCells(activePlayer);

					//если не стоит, то подсчитываем результативность хода и заканчиваем
					for(int player = 0; player < game.numPlayer; player++) {
						result = gameBoard.GetPlayerScore(player, !endshpil, kkk); // можно и 0.4f и 0,5
						for(int ii = 0; ii < game.numPlayer; ii++)	
							if(player == ii)
								ps[ii] += result;
							else
								ps[ii] -= result;
					}
					score = ps[activePlayer];
					itogo++;
					//Gdx.app.log("!" + activePlayer + " / d=" + depth + " / c=" + color, "sc=" + ps[activePlayer]);
				} else {
					//иначе уходим на глубину
				
					//score = AIStep(depth + 1, startTime);
					ps = AIStep(depth + 1, startTime, endshpil);
					activePlayer = tmp_activePlayer;
					score = ps[activePlayer];
					//Gdx.app.log("ret: pl=" + activePlayer + " / d=" + depth + " / c=" + color, "sc=" + score);
				}
				
				if(score > bestScore) {
					for(int player = 0; player < game.numPlayer; player++)
						playerScore[player] = ps[player];					
					bestScore = score;
					bestColor = color;
				}
				
				//Gdx.app.log("" + activePlayer + " / " + depth + " / " + bestColor, "" + bestScore);
				
				gameBoard.CopyBoard2tmp(tmpBoard);
			}
			
			if(bestColor == -1)
				bestColor = rezColor;
			
			gameOver = PlayerStep(bestColor, true);
			//Gdx.app.log("best" + depth, "" + activePlayer + " / bc=" + bestColor + " / " + (TimeUtils.millis()-startTime));
			
			//if(activePlayer == tmp_activePlayer) {
				return playerScore;// bestScore;
			//}

		//}
	}
    
	private boolean PlayerStep(int color ,boolean fill_hidden) {
		long startTime = TimeUtils.millis();
		/*
		new Thread(new Runnable() {
			@Override
			public void run() {
				Gdx.app.postRunnable(new Runnable() {
			         @Override
			         public void run() {
			         }
			      });
			}
		}).start();
		*/
		gameBoard.GetPlayerCoord(coord, activePlayer);
		
		int colorID = gameBoard.GetCellColor(coord.x, coord.y);

		gameBoard.FillColor(coord.x, coord.y, color, colorID); // < 1 ms
		gameBoard.CopyColor();
		// а теперь закрасим пойманные клетки
		if(fill_hidden)
			if(gameBoard.FillHiddenCells(activePlayer)) // 90-140 ms
				if(!gameBoard.forAI) { // важно оставить инфу в cell.tmp
						blink = true;
						gameBoard.blink = true;
						blinkStep = animationFrames;
						blinking = false;
						startTimeBlink = TimeUtils.millis();
				}


		// проверка на конец игры
		int[] playerColors = new int[game.numPlayer];
		for(int i = 0; i < game.numPlayer; i++)
			playerColors[i] = gameBoard.GetPlayerColor(i);

		boolean found;
		for(int x = 0; x < gameBoard.width; x++)
			for(int y = 0; y < gameBoard.height; y++) {
				colorID = gameBoard.GetCellColor(x, y);
				found = false;
				for(int i = 0; i < game.numPlayer; i++)
					found = found || (colorID == playerColors[i]);
				if(!found)
					return false;
			}
		//Gdx.app.log("AI-playerStep", "" + (TimeUtils.millis()-startTime));

		return true; // gameOver
	};

	@Override
    public void resize(int width, int height) {
		//stage.getViewport().update(width, height, true);
	}

	private void NewGame() {
		//todo отображать во время игры предполагаемые очки и рейтинг
		Gdx.graphics.setContinuousRendering(false);
		Gdx.input.setInputProcessor(stage);

		lbScore = new Label[game.numPlayer];
		for(int i=0; i<lbScore.length; i++) {
			lbScore[i] = new Label("", labelStyle);
		}
		Cell cellScore0 = tabBottom.add(lbScore[0]).expandX().left();
		Cell cellScore1 = cellScore0, cellScore2 = cellScore0, cellScore3 = cellScore0;
		if(game.numPlayer == 4) {
			cellScore3 = tabBottom.add(lbScore[3]).expandX().right();
			cellScore2 = tabTop.add(lbScore[2]).expandX().left();
			cellScore1 = tabTop.add(lbScore[1]).expandX().right();
		} else {
			cellScore1 = tabTop.add(lbScore[1]).expandX().right();
		}
		tabBottom.row().colspan(0);

		btns = new Image[game.maxColors];
		for(int i = 0; i < game.maxColors; i++) {
			//btns[i] = new Image(game.skin.getDrawable(game.colorNames[i]));
			btns[i] = new Image(game.buttonTex);
			btns[i].setColor(game.cellColors[i].GetColor());
			btns[i].addListener(new ClickListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return true;
				};
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					if(rendering)
						return;
					//Gdx.app.log("GameScreen FPS",  event.getListenerActor().getZIndex() + "");
					int color = event.getListenerActor().getZIndex();
					GameCycle(color);
				}
			});
			btns[i].setOrigin(btns[i].getWidth() / 2, btns[i].getHeight() / 2);
			btns[i].setScaling(Scaling.fit);
			//btns[i].setScale(game.scale);
			tabButtons.add(btns[i]).expandX()/*padBottom(4*game.scale).*/.prefSize(btns[i].getWidth() * game.scale * 0.6f, btns[i].getHeight() * game.scale * 0.6f);
		}
		//tabButtons.setDebug(true);
		tabBottom.add(tabButtons).expandX();

		float cell_scale = 1.0f;
		switch (game.boardSize) {
			case 0: cell_scale = 0.8f; break;
			case 2: cell_scale = 1.2f; break;
		}
		cell_size = (int) (Gdx.graphics.getPpcX() * BattleOfColorsGame.cell_size_sm * cell_scale);
		int cell_size60 = (int) (cell_size * Math.sin(60 * Math.PI / 180));
		int full_cell_size = cell_size * 3 - 1;
		int gameBoardWidth = (int) ((game.screenWidth - 8f * game.scale) / full_cell_size);
		int gameBoardPx = gameBoardWidth * full_cell_size;
		float btnsHeight = (int) tabTop.getPrefHeight() + (int) tabBottom.getPrefHeight();

		int gameBoardHeight = (int) ((game.screenHeight - btnsHeight - 16f*game.scale) / cell_size60) - 1;
		gameBoard = new GameBoard(gameBoardWidth, gameBoardHeight, game, cell_size);
		gameBoard.x_offset = (int) (game.screenWidth - gameBoardPx) / 2 - 1;
		gameBoard.y_offset = (int) (tabBottom.getPrefHeight() + 10f*game.scale);

		float scorePadLeft = cell_size*2 + gameBoard.x_offset;
		float scorePadRight = game.screenWidth - gameBoard.x_offset - gameBoardPx + full_cell_size;
		cellScore0.padLeft(scorePadLeft);
		cellScore1.padRight(scorePadRight);
		if(game.numPlayer == 4) {
			cellScore2.padLeft(scorePadLeft);
			cellScore3.padRight(scorePadRight);
		}

		SetButtons();

		maxAIDepth = game.difficult.GetValue();
		rendering = true;
		Gdx.graphics.requestRendering();

		if(game.order == 1) {
			// Ходит AI
			activePlayer = 1;
			rendering = true;
			Gdx.graphics.setContinuousRendering(true);
		}
	}

	@Override
    public void show() {
		NewGame();
	}

	private void StopGame() {
		gameBoard.dispose();
		//tabBottom.remove();
		tabBottom.clear();
		tabButtons.clear();
		tabTop.clear();
		Runtime.getRuntime().gc();
	}

    @Override
    public void hide() {
		StopGame();
    	//Gdx.app.log("hide", "");
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

	@Override
	public void render(float delta) {
		if(Gdx.input.isKeyPressed(Keys.BACKSPACE)) {
			game.dlgYesNo.show(stage);
		}
		
		if(blink) {
        	long ms = TimeUtils.millis();
        	long dt = ms - startTimeBlink;
        	if(dt > 250) {
        		blinkStep--;
        		startTimeBlink = ms;
        		gameBoard.blink = !gameBoard.blink;
        	}

        	if(blinkStep <= 0) {
            	blink = false;
            	gameBoard.blink = false;
        	}
        	Draw(delta);
        	//Gdx.app.log("draw blink", "");
        	
        	return;
        }
        
		if(!rendering) {
			//Gdx.app.log("draw 1", "");
	        Draw(delta);
			return;
		}
		
		long startTime;

//		camera.update();
		
		if(!Gdx.graphics.isContinuousRendering()) {
			//Gdx.app.log("draw 2", "");
	        Draw(delta);
	        rendering = false;
	                
	        return;
		}
		
    	if(activePlayer >= game.numPlayer) {
    		Gdx.app.log("draw 3", "");
    		SetButtons();
    		Draw(delta);
    		
    		rendering = false;
    		Gdx.graphics.setContinuousRendering(false);
    		
    		//GameCycle(0);
    		
    		return;
    	}
    	
		int color;
		if(activePlayer == 0)
			color = activePlayerColor;
		else
			color = DoAI();
		if(PlayerStep(color, true)) {
			Gdx.app.log("draw 4", "");
	        Draw(delta);
    		rendering = false;
    		Gdx.graphics.setContinuousRendering(false);
	        GameOver();
		}
		activePlayer++;
		Gdx.app.log("draw 5", "");
		SetButtons();
        Draw(delta);
	}

	public void GotoMain() {
		game.setScreen(game.mainScreen);		
	}

	private void Draw(float delta) {
		Gdx.gl.glClearColor(bgR, bgG, bgB, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT /*|(Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0)*/);

		/*SpriteBatch batch2 = new SpriteBatch();
		batch2.begin();
		batch2.draw(game.backTex, 0, 0, game.screenWidth, game.screenHeight);
		batch2.end();
		batch2.dispose();*/
		gameBoard.DrawShader(batch);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
		gameBoard.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}	
}
