package Screens;

import com.BattleOfColors.game.BattleOfColorsGame;
import com.BattleOfColors.game.MyButton;
import com.BattleOfColors.game.PlayStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameSetting implements Screen {
    private BattleOfColorsGame game;
    private PlayStage stage;
    private Table table;
    private Label.LabelStyle labelStyle;
    private Label lbOrder;
    private String strOrder, str5, str6;

    public GameSetting(final BattleOfColorsGame floodGame) {
        game = floodGame;
        stage = new PlayStage(new ScreenViewport());

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        table = new Table();
        //table.setDebug(true);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = game.midiFont;
        labelStyle.fontColor = game.colYellow;

        game.midiFont.setColor(game.colDark);
        game.miniFont.setColor(game.colDark);
        Button.ButtonStyle btSt = new Button.ButtonStyle(game.skin.newDrawable("btn_1", Color.GRAY), null, game.skin.newDrawable("btn_1", game.colYellow));

        if(game.language == 0)
            mainTable.add(new Label("Game options", labelStyle)).top();
        else
            mainTable.add(new Label("Настройка игры", labelStyle)).top();
        mainTable.row();
        mainTable.row();

        String str1, str2, str3, str4;
        String[] strArr, strArr2;
        if(game.language == 0) {
            str1 = "Number of\nplayers...";
            str2 = "Number of\ncolors...";
            str3 = "Difficulty";
            str4 = "Hexagon size";
            strArr = new String[]{"easy", "normal"};
            strArr2 = new String[]{"small", "normal", "big"};
            strOrder = "In this game you will go ";
            str5 = "first.";
            str6 = "second.";
        }
        else {
            str1 = "Число\nигроков...";
            str2 = "Кол-во\nцветов...";
            str3 = "Сложность\nигры...";
            str4 = "Размер\nклеток...";
            strArr = new String[]{"легко", "норм."};
            strArr2 = new String[]{"малый", "норм.", "больш."};
            strOrder = "В этой игре Вы будете ходить ";
            str5 = "первым.";
            str6 = "вторым.";
        }
        int val = game.pref.GetColors();
        if(val > -1)
            game.maxColors = val;
        val = game.pref.GetDif();
        if(val > -1) {
            game.difficult = BattleOfColorsGame.Difficulties.Easy;
            if(val == 1)
                game.difficult = BattleOfColorsGame.Difficulties.Normal;
        }

        val = game.pref.GetSize();
        if(val > -1)
            game.boardSize = val;
        game.order = game.pref.GetOrder();

        AddSelector(str1, new String[]{"2", "4"}, game.numPlayer == 2 ? 0 : 1, game.midiFont, btSt, "pla", true, 0); // 50
        table.row();
        AddSelector(str2, new String[]{"5", "6", "7"}, game.maxColors - 5, game.midiFont, btSt, "col", false, 0); // 20
        table.row();
        AddSelector(str3, strArr, game.difficult.GetValue(), game.miniFont, btSt, "dif", true, 0); // 40
        table.row();
        AddSelector(str4, strArr2, game.boardSize, game.miniFont, btSt, "siz", false, 0); // 30
        mainTable.add(table);
        mainTable.row();

        String str = strOrder;
        if (game.order == 0)
            str = str + str5;
        else
            str = str + str6;
        lbOrder = new Label(str, labelStyle);
        mainTable.add(lbOrder).pad(16 * game.scale);
        mainTable.row();

        MyButton but = new MyButton("Start", game.font, game.btnStyle);
        but.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                game.setScreen(game.gameScreen);
            }
        });
        Cell<MyButton> cell = mainTable.add(but).spaceTop(36 * game.scale).padBottom(16 * game.scale);
        but.SetBtnScale(cell, game.scale / 1.5f);
        stage.addActor(mainTable);

        stage.setHardKeyListener(new PlayStage.OnHardKeyListener() {
            @Override
            public void onHardKey(int keyCode, int state) {
                if (keyCode == Input.Keys.BACK && state == 1){
                    game.setScreen(game.mainScreen);
                }
            }
        });
    }

    private void AddSelector(/*Texture*/ String label, final String[] val, int defIndx, BitmapFont font, Button.ButtonStyle btSt1, String name, boolean shift, int pad) {
        Label lb = new Label(label, labelStyle);
//		Image lb = new Image(label);
//		lb.setScale(game.scale);
        table.add(lb).left();//.expandX().left().padRight(8 * game.scale)/*padLeft(-16*game.scale)*/.padTop(-pad*game.scale);

        final MyButton[] but = new MyButton[val.length];
        int i = 0;
        for(String v: val) {
            but[i] = new MyButton(v, font, btSt1);
            but[i].setName(name + i);
            i++;
        }
        but[defIndx].setChecked(true);
        ClickListener clD = new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                MyButton btn = (MyButton) event.getListenerActor();

                for(MyButton b: but)
                    b.setChecked(false);
                btn.setChecked(true);
                String valName = btn.getName().substring(3);
                String btnName = btn.getName().substring(0, 3);
                int val = Integer.valueOf(valName);
                if(btnName.contains("col")) {
                    game.maxColors = val + 5;
                    game.pref.SetColors(game.maxColors);
                }
                if(btnName.contains("pla")) {
                    game.numPlayer = val == 0 ? 2 : 4;
                    MyButton colBtn = table.findActor("col0");
                    if(game.numPlayer == 4) {
                        colBtn.setVisible(false);
                        colBtn.setChecked(false);
                        game.maxColors = 7;
                        colBtn = table.findActor("col1");
                        colBtn.setChecked(false);
                        colBtn.setVisible(false);
                        colBtn = table.findActor("col2");
                        colBtn.setChecked(true);
                    }
                    else {
                        colBtn.setVisible(true);
                        colBtn = table.findActor("col1");
                        colBtn.setVisible(true);
                    }
                }
                if(btnName.contains("dif")) {
                    game.difficult = BattleOfColorsGame.Difficulties.Easy;
                    if(val == 1)
                        game.difficult = BattleOfColorsGame.Difficulties.Normal;
                    game.pref.SetDif(game.difficult.GetValue());
                }

                if(btnName.contains("siz")) {
                    game.boardSize = val;
                    game.pref.SetSize(game.boardSize);
                }
            }
        };
        Cell<MyButton> cell;
/*        if(shift)
            table.add();*/
        for(MyButton b: but) {
            cell = table.add(b);
            b.addListener(clD);
//            if(shift)
//                b.SetBtnScale(cell, game.scale).padRight(b.ShiftRight());
//            else
//                b.SetBtnScale(cell, game.scale);///*.padTop(b.ShiftDown())*/.padRight(b.ShiftRight());
//            table.add();
        }
    }


    @Override
    public void show() {
        String str = strOrder;
        if (game.order == 0)
            str = str + str5;
        else
            str = str + str6;
        lbOrder.setText(str);

        Gdx.graphics.setContinuousRendering(true);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(game.colDark.r, game.colDark.g, game.colDark.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Batch batch = stage.getBatch();
        batch.begin();
        game.skin.getTiledDrawable("background").draw(batch, 0.0f, 0.0f, game.screenWidth, game.screenHeight);
        batch.end();
        // Gdx.app.log("GameScreen FPS", (1/delta) + "");
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

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
