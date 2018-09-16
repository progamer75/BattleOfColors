package com.BattleOfColors.game;

import Screens.GameScreen;
import Screens.GameSetting;
import Screens.MainScreen;
import Screens.ScoreScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;

public class BattleOfColorsGame extends Game {
	public enum Difficulties {
		Easy(0), Normal(1);
		private final int value;
		Difficulties(int v) {
			value = v;
		}

		public int GetValue() {
			return value;
		}
	}

	public boolean hasUnfinishedGame;
	public HSV cellColors[];
	public String colorNames[];
	public int maxColors = 6;
	public int numPlayer = 2;
	public Difficulties difficult = Difficulties.Easy;
	public int boardSize = 1;
	
	public GamePreferences pref;
	public Screen mainScreen, settingsScreen, gameScreen, scoreScreen;
	
	public float screenWidth;
	public float screenHeight;
	public final static float cell_size_sm = 0.25f;
	public float scale;
	
	public Dialog dlgHelp;
	public YesNoDialog dlgYesNo, dlgWarnNewGame;
	private CharSequence helpText;

    public BitmapFont font, midiFont, miniFont;
	public final float bgR = 0.168f, bgG = 0.168f, bgB = 0.168f, bgA = 1.0f;
	public final Color colYellow = new Color(0xf0b017ff);
	public final Color colGreen = new Color(0x8ab71bff);
	public final Color colRed = new Color(0xee4e10ff);
	public final Color colBlue = new Color(0x2aa1d3ff);
	public final Color colBg = new Color(0x2e3138ff);
	public final Color colDialogBg = new Color(0x000000e0);
	public final Color colDark = new Color(0x23252bff);
	public final Color colWhite = new Color(0xbbbbbbff);

	public Texture backgroundTex;
	public Texture buttonTex, bigButtonTex;
	public Texture texUp, texDown;
	public ShaderProgram shader;
    private static final String FONT_CHARACTERS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\\\/?-+=()*&.;:,{}\\\"?`'<>";
    
    public Skin skin;
    public String versionName = "";
    public int language = 0; 

	public ActionResolver actionResolver;
	public int allTime = 0;
	public int weekly = 0;
	public int daily = 0;
	public Texture podsvetkaTex;
	public float btnScale;
	public TextButtonStyle btnStyle;

	public int order = 0; // кто начинает игру

	public BattleOfColorsGame(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	} 
	
    @Override
    public void create() {
    	hasUnfinishedGame = false;
		//language = 0;
    	Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

    	pref = new GamePreferences(this);
    	
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		scale = screenWidth / 600;
		btnScale = scale / 1.2f;
		//scale = screenHeight / 1067;
		
    	cellColors = new HSV[7];
    	cellColors[0] = new HSV(0, 1, 1);
    	cellColors[1] = new HSV(120, 1, 1);
    	cellColors[2] = new HSV(240, 1, 1);
    	cellColors[3] = new HSV(60, 1, 1);
    	cellColors[4] = new HSV(300, 1, 1);
    	cellColors[5] = new HSV(180, 1, 1);
    	cellColors[6] = new HSV(180, 1, 1);
    	for(int i = 0; i<6; i++)
    		cellColors[i].HSV2RGB();
    	cellColors[6].r = 0.4f;
    	cellColors[6].g = 0.4f;
    	cellColors[6].b = 0.4f;
    	
    	colorNames = new String[7];
    	colorNames[0] = new String("btn_red");
    	colorNames[1] = new String("btn_green");
    	colorNames[2] = new String("btn_blue");
    	colorNames[3] = new String("btn_yellow");
    	colorNames[4] = new String("btn_magenta");
    	colorNames[5] = new String("btn_cyan");
    	colorNames[6] = new String("btn_gray");
    	
    	FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Comfortaa-Bold.ttf"));
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        if(language == 0)
        	param.size = (int) (screenWidth / 16);
        else
        	param.size = (int) (screenWidth / 18);
        param.characters = FONT_CHARACTERS;
        font = generator.generateFont(param);
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        font.setUseIntegerPositions(true);
        
        //if(language == 0)
        	param.size = (int) (screenWidth / 24);
        //else
        	//param.size = (int) (screenWidth / 26);
        midiFont = generator.generateFont(param);
        midiFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        midiFont.setUseIntegerPositions(true);

        param.size = (int) (screenWidth / 32);
        miniFont = generator.generateFont(param);
        miniFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        miniFont.setUseIntegerPositions(true);
        
        generator.dispose();
        
		skin = new Skin();
		skin.add("colDialogBg", colDialogBg);
        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("images/mini.pack"));
        skin.addRegions(buttonAtlas);

        Texture tex = new Texture(Gdx.files.internal("images/games_leaderboards_green.png"));
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        skin.add("leaderboard_green", tex);
        tex = new Texture(Gdx.files.internal("images/games_leaderboards.png"));
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        skin.add("leaderboard", tex);
		texUp = new Texture(Gdx.files.internal("images/up_32.png"));
		texUp.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		texDown = new Texture(Gdx.files.internal("images/down_32.png"));
		texDown.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		backgroundTex = new Texture(Gdx.files.internal("images/background.png"));
		backgroundTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		skin.add("background", backgroundTex);

		// добавляем текстуру маленьких кнопок
		int pixmap_width = (int)(100f * scale);
		int pixmap_height = (int)(100f * scale);
		Pixmap pixmap2 = new Pixmap(pixmap_width, pixmap_height, Format.RGBA8888);
		GLFrameBuffer.FrameBufferBuilder frameBufferBuilder2 = new GLFrameBuffer.FrameBufferBuilder(pixmap_width, pixmap_height);
		frameBufferBuilder2.addBasicColorTextureAttachment(Format.RGBA8888);
		FrameBuffer buffer = frameBufferBuilder2.build();
		//FrameBuffer buffer = new FrameBuffer(Format.RGBA8888, pixmap_width, pixmap_height, false);
		Texture textureTmp = new Texture(pixmap2);
		String vertexShader = Gdx.files.internal("shaders/vertex.txt").readString();
		String fragmentShader = Gdx.files.internal("shaders/cell.txt").readString();
		shader = new ShaderProgram(vertexShader, fragmentShader);
		ShaderProgram.pedantic = false;
		if(!shader.isCompiled()) {
			Gdx.app.log("err", shader.getLog());
		}
		shader.begin();
		shader.setUniformf("d1", 0.03f);
		shader.setUniformf("d2", 0.06f);
		shader.setUniformf("smoothing", 1.0f / 64.0f);
		shader.setUniformf("radius", 0.08f);//0.08
		for(int i = 0; i < 6; i++) {
			shader.setUniformi("neighbors" + i, 0);
		}
		shader.end();
		SpriteBatch batch2 = new SpriteBatch(1000, shader);
		buffer.bind();
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch2.disableBlending();
		batch2.begin();
		batch2.draw(textureTmp, 0, 0);
		batch2.end();
		buffer.end();
		buttonTex = buffer.getColorBufferTexture();
		batch2.dispose();
		skin.add("btn_1", buttonTex);
		pixmap2.dispose();

// добавляем текстуру подсветки
		pixmap_width = (int)(400f * scale);
		pixmap_height = (int)(400f * scale);
		pixmap2 = new Pixmap(pixmap_width, pixmap_height, Format.RGBA8888);

		GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder(pixmap_width, pixmap_height);
		frameBufferBuilder.addBasicColorTextureAttachment(Format.RGBA8888);
		buffer = frameBufferBuilder.build();
		//buffer = new FrameBuffer(Format.RGBA8888, pixmap_width, pixmap_height, false);

		textureTmp = new Texture(pixmap2);
		vertexShader = Gdx.files.internal("shaders/vertex.txt").readString();
		fragmentShader = Gdx.files.internal("shaders/podsvetka.txt").readString();
		ShaderProgram shader2 = new ShaderProgram(vertexShader, fragmentShader);
		ShaderProgram.pedantic = false;
		if(!shader2.isCompiled()) {
			Gdx.app.log("err", shader2.getLog());
		}
		batch2 = new SpriteBatch(1000, shader2);
		batch2.setColor(colRed);
		buffer.bind();
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch2.disableBlending();
		batch2.begin();
		batch2.draw(textureTmp, 0, 0);
		batch2.end();
		buffer.end();
		podsvetkaTex = buffer.getColorBufferTexture();
		batch2.dispose();
		pixmap2.dispose();

		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(colDialogBg);
		pixmap.drawPixel(0, 0);
		Texture bgTex = new Texture(pixmap);
		skin.add("bgTex", bgTex);

		// добавляем текстуру больших кнопок
		/*pixmap_width = (int)(300f * scale);
		pixmap_height = (int)(100f * scale);
		pixmap2 = new Pixmap(pixmap_width, pixmap_height, Format.RGBA8888);
		buffer = new FrameBuffer(Format.RGBA8888, pixmap_width, pixmap_height, false);
		textureTmp = new Texture(pixmap2);
		vertexShader = Gdx.files.internal("shaders/vertex.txt").readString();
		fragmentShader = Gdx.files.internal("shaders/btn.txt").readString();
		ShaderProgram shader2 = new ShaderProgram(vertexShader, fragmentShader);
		if(!shader2.isCompiled()) {
			Gdx.app.log("err", shader2.getLog());
		}
		shader2.begin();
		shader2.setUniformf("d1", 0.02f);
		shader2.setUniformf("d2", 0.03f);
		shader2.setUniformf("smoothing", 1.0f / 120.0f);
		shader2.setUniformf("radius", 0.01f);//0.08
		shader2.end();
		batch2 = new SpriteBatch(1000, shader2);
		buffer.bind();
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch2.disableBlending();
		batch2.begin();
		batch2.draw(textureTmp, 0, 0);
		batch2.end();
		buffer.end();
		bigButtonTex = buffer.getColorBufferTexture();
		batch2.dispose();
		skin.add("btn_big", bigButtonTex);
		pixmap2.dispose();
		*/

		bigButtonTex = new Texture(Gdx.files.internal("images/btn.png"));
		skin.add("btn_big", bigButtonTex);

		//backTex = new Texture(Gdx.files.internal("images/background.jpg"));
		//Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

		btnStyle = new TextButtonStyle(skin.newDrawable("btn_big", colYellow), null, null, midiFont);

        if(language == 0)
	        helpText = "  This is a game where your objective is to capture more territory than your opponent.\n"
			+ "  You start at the bottom left corner and your opponent starts at the top right corner."
			+ " Each player takes turn to select the color they want to absorb. This is done by pressing"
			+ " a button with the corresponding color located at the bottom of the screen."
			+ " When a button is pressed, the player's area changes to the corresponding color and absorbs"
			+ " adjacent hexagons of the same color. Colors currently"
			+ " in use by any of the players cannot be selected.";
//			+ "\n  Match consists of four games. The winner is the one who won a lot of games.";
        else
        	helpText = "  В этой игре ваша цель захватить больше территории, чем ваш противник.\n" +
					"  Вы начинаете игру в нижнем левом углу, а ваш оппонент в правом верхнем.\n" +
					" На каждом ходе игроки выбирают цвет, который они хотят захватить.\n" +
					" При этом меняется цвет уже захваченной области и к ней добавляются клетки выбранного цвета.\n" +
					" Нельзя выбирать уже используемые игроками цвета.\n";
	//				"\n  Матч состоит из четырех игр. Побеждает тот кто выиграл в большем количестве игр.\n" +
	//				" Если все выиграли одинаковое количество раз, то побеждает игрок с большим количеством очков.";
        	 
        //helpText = getResources().getString(R.string.);
        
        createDialogs();
        mainScreen = new MainScreen(this);
		settingsScreen = new GameSetting(this);
    	gameScreen = new GameScreen(this);
    	scoreScreen = new ScoreScreen(this);
    	Gdx.graphics.setContinuousRendering(false);
    	setScreen(mainScreen);
    }
    
	public String GetDifficultyStr(int dif) {
		switch(dif) {
		case 0: return "Easy";
		case 1: return "Normal";
		}
		
		return "";
	}
    
    private void createDialogs() {
		WindowStyle ws = new WindowStyle();
		ws.titleFont = font;

    	dlgHelp = new Dialog("", ws) {
		    protected void result (Object object) {
		    	hide();
		    }
		    
/*	        @Override
	        public float getPrefWidth() {
	            return 450 * scale;
	        }
	        
	        @Override
	        public float getPrefHeight() {
	            return 400 * scale;
	        }*/	
	    };
        
	    MyButton but = new MyButton("OK", font, btnStyle);
    	dlgHelp.button(but, this);
	    Cell<MyButton> cell = dlgHelp.getButtonTable().getCell(but);
	    but.SetBtnScale(cell, btnScale);
    	
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = midiFont;
    	Label lb = new Label(helpText, labelStyle);
    	lb.setAlignment(Align.left);
    	lb.setColor(colWhite);
    	lb.setWrap(true);
    	
    	dlgHelp.getContentTable().add(lb).prefHeight(400 * scale).prefWidth(500 * scale).pad(16*scale).center();
    	dlgHelp.getButtonTable().padTop(32*scale).padBottom(16*scale);
		dlgHelp.setModal(true);
		dlgHelp.setMovable(false);
		dlgHelp.setResizable(false);
		dlgHelp.setBackground(skin.getTiledDrawable("bgTex"));
		dlgHelp.key(Keys.ENTER, true).key(Keys.ESCAPE, false);
		dlgHelp.center();

    	String str;
    	if(language == 0)
    		str = "Are you sure to quit the game?";
    	else
    		str = "Вы уверены что хотите прервать игру?";
		dlgYesNo = new YesNoDialog(this, btnStyle, str, ws);

		if(language == 0)
			str = "There is an unfinished game. You will lose it if you start a new one. Continue anyway?";
		else
			str = "Есть не завершенная игра. Вы ее проиграете, если начнете новую. Все равно продолжить?";
		dlgWarnNewGame = new YesNoDialog(this, btnStyle, str, ws);
		//TODO для новой игры
	}
	
	@Override
	public void render () {
		super.render();
	}
}

//TODO изменить цвет фона справки
//TODO в хэлпе добавить информацию по расчету рейтинга

//TODO добавить сохранение игры при потере фокуса