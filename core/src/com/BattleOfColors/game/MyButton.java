package com.BattleOfColors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;

public class MyButton extends Button {
	public BitmapFont font;
	public String text;
	private float privateScale = 0;
	
	public MyButton(String str, BitmapFont font, ButtonStyle style) {
		super(style);
		//font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.font = font;
		this.text = str;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		//Gdx.app.log("" + getX(), "" + getWidth());
		font.draw(batch, text, getX(), getY() +
				getHeight() / 2 + font.getCapHeight() / 2, getWidth(), Align.center, false);

	}
	
	public Cell<MyButton> SetBtnScale(Cell<MyButton> cell, float scale) {
		privateScale = scale;
		ButtonStyle btSt = this.getStyle();
		return cell.size(btSt.up.getMinWidth() * scale, btSt.up.getMinHeight() * scale);
	}

	public float ShiftDown() {
		ButtonStyle btSt = this.getStyle();
		return -btSt.up.getMinHeight() * privateScale / 2;
	}

	public float ShiftRight() {
		ButtonStyle btSt = this.getStyle();
		return -btSt.up.getMinWidth() * privateScale / 2;
	}
	
/*	public void setScale (float scaleXY) {
		super.setScale(scaleXY);
		ButtonStyle style = getStyle();
		if (style.up != null) setWidth(style.up.getMinWidth() * getScaleX());
	}

	public float getPrefWidth () {
		float width = 0;
		ButtonStyle style = getStyle();
		if (style.up != null) width = Math.max(width, style.up.getMinWidth() * getScaleX());
		if (style.down != null) width = Math.max(width, style.down.getMinWidth() * getScaleX());
		if (style.checked != null) width = Math.max(width, style.checked.getMinWidth() * getScaleX());
		return width;
	}
	
	public float getPrefHeight () {
		float height = 0;
		ButtonStyle style = getStyle();
		if (style.up != null) height = Math.max(height, style.up.getMinHeight() * getScaleY());
		if (style.down != null) height = Math.max(height, style.down.getMinHeight() * getScaleY());
		if (style.checked != null) height = Math.max(height, style.checked.getMinHeight() * getScaleY());
		return height;
	}*/
}
