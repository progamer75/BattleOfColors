package Screens;

import com.BattleOfColors.game.BattleOfColorsGame;
import com.BattleOfColors.game.MyButton;
import com.BattleOfColors.game.PlayStage;
import com.BattleOfColors.game.PlayStage.OnHardKeyListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainScreen implements Screen {
    private BattleOfColorsGame game;
	private PlayStage stage;
    private Table table;
    private Texture nameTxt;
    private float nameHeight;
	private Image btnLeaderboard;
	private Label lbVersion;
    private final int nameTxtOffset = 32;
    private float podsvetkaX = 0, podsvetkaY = 0;
    private float podsvetkaV = 1.0f;
    private float podsvetkaA;

    public MainScreen(final BattleOfColorsGame floodGame) {
        game = floodGame;

        /*camera = new OrthographicCamera();
        camera.setToOrtho(false, game.screenWidth, game.screenHeight);

        camera2 = new OrthographicCamera();
        camera2.setToOrtho(false, 600, game.screenHeight * game.scale);
*/
        stage = new PlayStage(new ScreenViewport());

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.left().bottom();
        //mainTable.debug();
        
        nameTxt = new Texture(Gdx.files.internal("images/name.png"));
        nameTxt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        nameHeight = nameTxt.getHeight() * (game.screenWidth - 2 * nameTxtOffset * game.scale) / nameTxt.getWidth();

        podsvetkaA = (float)Math.random() * 2  * (float)Math.PI;

        table = new Table();
//        table.debug();
        table.padLeft(16 * game.scale);

        ButtonStyle btStRed = new ButtonStyle(game.skin.newDrawable("btn_big", game.colRed), null, null);
        ButtonStyle btStGreen = new ButtonStyle(game.skin.newDrawable("btn_big", game.colGreen), null, null);
        ButtonStyle btStBlue = new ButtonStyle(game.skin.newDrawable("btn_big", game.colBlue), null, null);
        ButtonStyle btStYellow = new ButtonStyle(game.skin.newDrawable("btn_big", game.colYellow), null, null);

        game.font.setColor(game.colDark);

        String str0, str1, str2, str3, str4;
        if(game.language == 0) {
            str0 = "Continue";
            str1 = "Help";
        	str2 = "Start";
        	str3 = "Exit";
        	str4 = "Score";
        } else {
            str0 = "Продолжить";
        	str1 = "Помощь";
        	str2 = "Старт";
        	str3 = "Выход";
        	str4 = "Рекорды";
        };

        ClickListener clContinue = new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
            };
        };
        AddButton(str0, btStYellow, clContinue);

        ClickListener clStart = new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
            	game.setScreen(game.settingsScreen);
            };
        };
        AddButton(str2, btStYellow, clStart);

        ClickListener clScore = new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(game.scoreScreen);
            };
        };
        AddButton(str4, btStGreen, clScore);

        ClickListener clHelp = new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                game.dlgHelp.show(stage, null); //TODO: повторно не то показывает или медленно показывается
                game.dlgHelp.setPosition(Math.round((stage.getWidth() - game.dlgHelp.getWidth()) / 2), Math.round((stage.getHeight() - game.dlgHelp.getHeight()) / 2));
            };
        };
        AddButton(str1, btStBlue, clHelp);

        ClickListener clExit = new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
            	Gdx.app.exit();
            };
        };
        AddButton(str3, btStRed, clExit);

        table.padBottom(64 * game.scale);
        mainTable.add(table);

        stage.addActor(mainTable);

        btnLeaderboard = new Image(game.skin.getDrawable("leaderboard"));
        btnLeaderboard.setScale(game.scale/8);
        btnLeaderboard.setX(16*game.scale);
        btnLeaderboard.setY(table.getPrefHeight() + 32*game.scale);
//        stage.addActor(btnLeaderboard);

        LabelStyle versionStyle = new LabelStyle();
        versionStyle.font = game.miniFont;
        versionStyle.fontColor = Color.WHITE;
        lbVersion = new Label("ver." + game.versionName, versionStyle);
        lbVersion.setX(16*game.scale);
        lbVersion.setY(8*game.scale);
        stage.addActor(lbVersion);
        
        stage.setHardKeyListener(new OnHardKeyListener() {          
            @Override
            public void onHardKey(int keyCode, int state) {
                if (keyCode == Keys.BACK && state == 1){
                	Gdx.app.exit();
                }       
            }
        });
        
//Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("market://details?id=ru.alexanderklimov.crib"));
//        startActivity(intent);

        // TODO: добавить поставить оценку
        //Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.hoodrij.followTheLight");
    }
	
	public void SetLeaderboard(boolean a) {
		if (a)
			btnLeaderboard.setDrawable(game.skin.getDrawable("leaderboard_green"));
		else
			btnLeaderboard.setDrawable(game.skin.getDrawable("leaderboard"));
	}
	
	private MyButton AddButton(final String val, ButtonStyle btSt, ClickListener cl) {
		MyButton but = new MyButton(val, game.font, btSt);
        //but.setScale(game.scale);
    	but.addListener(cl);
    	Cell<MyButton> cell;
    	cell = table.add(but).padBottom(16 * game.scale);
        but.SetBtnScale(cell, game.btnScale);
        table.row();

        return but;
    	/*if(pad)
    		but.SetBtnScale(cell, game.scale).padRight(but.ShiftRight());
    	else
    		but.SetBtnScale(cell, game.scale).padTop(but.ShiftDown()).padRight(but.ShiftRight());
    	*/
	}

    @Override
    public void render(float delta) {
        //camera.update();
        Gdx.gl.glClearColor(game.colDark.r, game.colDark.g, game.colDark.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        podsvetkaX += podsvetkaV * Math.cos(podsvetkaA);
        podsvetkaY += podsvetkaV * Math.sin(podsvetkaA);

        Batch batch = stage.getBatch();
        batch.begin();

        if(podsvetkaX < 0) {
            podsvetkaX = 0;
/*            if(podsvetkaA > Math.PI)
                podsvetkaA = (float)Math.PI - podsvetkaA;
            else
                podsvetkaA = podsvetkaA - (float)Math.PI / 2;*/
            podsvetkaA += 1.0;
        }
        if(podsvetkaX > game.screenWidth) {
            podsvetkaX = game.screenWidth;
            podsvetkaA += 1.0;
        }

        if(podsvetkaY < 0) {
            podsvetkaY = 0;
            podsvetkaA += 1.0;
        }
        if(podsvetkaY > game.screenHeight) {
            podsvetkaY = game.screenHeight;
            podsvetkaA += 1.0;
        }

        podsvetkaA += Math.random() * 0.1 - 0.05;

        batch.draw(game.podsvetkaTex, podsvetkaX - game.podsvetkaTex.getWidth() / 2, podsvetkaY - game.podsvetkaTex.getHeight() / 2);
        game.skin.getTiledDrawable("background").draw(batch, 0.0f, 0.0f, game.screenWidth, game.screenHeight);

        batch.draw(nameTxt, nameTxtOffset * game.scale, game.screenHeight - nameHeight - 16 * game.scale, game.screenWidth - nameTxtOffset * game.scale * 2, nameHeight);
        batch.end();

       // Gdx.app.log("GameScreen FPS", (1/delta) + "");
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {
    	Gdx.graphics.setContinuousRendering(true);
    	Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose()
    {
    	stage.dispose();
    }	
}
// TODO для четырех игроков оставить только вариант с 7 цветами, также убрать из статистики