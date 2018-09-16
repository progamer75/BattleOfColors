package com.BattleOfColors.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;

public class YesNoDialog extends Dialog {
	private String question;
	private MyButton butYes, butNo;
	private YesAction yesAction;
	
	public YesNoDialog (BattleOfColorsGame game, TextButtonStyle btStyle, String question, WindowStyle windowStyle) {
		super("", windowStyle);
		this.question = question;
		String str1, str2;
		if(game.language == 0) {
			str1 = "Yes";
			str2 = "No";
		} else {
			str1 = "Да";
			str2 = "Нет";			
		}
		
	    butYes = new MyButton(str1, game.midiFont, btStyle);
    	button(butYes, true);
	    Cell<MyButton> cell = getButtonTable().getCell(butYes);
	    butYes.SetBtnScale(cell, game.btnScale * 0.5f);
    	
	    butNo = new MyButton(str2, game.midiFont, btStyle);
    	button(butNo, false);
	    cell = getButtonTable().getCell(butNo);
	    butNo.SetBtnScale(cell, game.btnScale * 0.5f);
    	
        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = game.midiFont;
    	Label lb = new Label(question, labelStyle);
    	lb.setAlignment(Align.left);
    	lb.setColor(game.colWhite);
    	//lb.setWrap(true);
    	
    	getContentTable().add(lb).pad(16*game.scale).center();//.prefHeight(150 * game.scale).prefWidth(250 * game.scale);
    	getButtonTable().padTop(32*game.scale).padBottom(16*game.scale);
		setModal(true);
		setMovable(false);
		setResizable(false);
		setBackground(game.skin.getTiledDrawable("bgTex"));
		key(Keys.ENTER, true).key(Keys.ESCAPE, false);
	}
	
    protected void result (Object object) {
   		if((Boolean) object) {
   			hide();
   			yesAction.Action();
   		}
    }

	public void SetAction(YesAction action) {
		yesAction = action;
	}
}
