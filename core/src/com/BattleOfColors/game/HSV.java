package com.BattleOfColors.game;

import com.badlogic.gdx.graphics.Color;

public class HSV {
	public int h; //0..360
	public float s, v; //0..1
	public float r, g, b; //0..1
	
	HSV() {};
	HSV(int h, float s, float v) {
		this.h = h;
		this.s = s;
		this.v = v;
	}
	
	public Color GetColor() {
		return new Color(r, g, b, 1f);
	}
	
	public void RGB2HSV() {
		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));
		float mm = 1/(max - min)/6;
		
		h = 0;
		s = 1 - min / max;
		v = max;
		if((r > g) && (r > b)) {
			if(g < b)
				h = 360 + (int) ((g-b)*mm); 
			else
				h = (int) ((g-b)*mm);
		} else if ((g > r) && (g > b))
			h = 120 + (int) ((b-r)*mm); 
		else
			h = 240 + (int) ((r-g)*mm);
		
	}
	
	public void HSV2RGB() {
		int hi = (h / 60) % 6;
		float vmin = (1 - s) * v;
		float a = (v - vmin) * (h % 60) / 60;
		float vinc = vmin + a;
		float vdec = v - a;
		
		switch(hi) {
		case 0: r = v; g = vinc; b = vmin; break;
		case 1: r = vdec; g = v; b = vmin; break;
		case 2: r = vmin; g = v; b = vinc; break;
		case 3: r = vmin; g = vdec; b = v; break;
		case 4: r = vinc; g = vmin; b = v; break;
		case 5: r = v; g = vmin; b = vdec; break;
		}
	}
	public void CopyFrom(HSV hsv) {
		r = hsv.r;
		g = hsv.g;
		b = hsv.b;
		h = hsv.h;
		s = hsv.s;
		v = hsv.v;
	}
}
