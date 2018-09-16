package com.BattleOfColors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Arrays;

public class GameBoard {
	public int x_offset = 0;
	public int y_offset = 0;
	public int width;
	public int height;
	BattleOfColorsGame game;
	public GameCell[][] cells;
	public boolean forAI;
	
	private IntVector coord = new IntVector(0, 0);
	private IntVector coord2 = new IntVector(0, 0);
	private int[] destX, destY;
	private int tmpColor;
	private Vector2D[] cache_fill; // просмотренные ячейки для fillHidden
	private int cache_fill_num;

	// для рендеринга
	public int cell_size;
	int cell_size2; // cell_size/2
	private int cell_size60; // cell_size * sin(60)
	boolean horiz_orientation;
	//private Texture[][] pixmaptex, pixmaptex_prev;
	//private	Pixmap[][] pixmap_cur, pixmap_prev;
	private	Pixmap pixmap, pixmapBig;
	private Texture pixmaptex;
//	private Vector2 pnt1, pnt2, pnt3, pnt4, pnt5, pnt6;
	private HSV hsv;
	private int pixmapsizeX, pixmapsizeY;
	private FrameBuffer buffer;
	public boolean blink;
	private boolean withCorrect;
	private int color;
	private double corr_koeff;
	private ShaderProgram shader;
	private Batch batch1;
	private static int x_getx, y_gety;
	private static Vector2D vec2d;

	private class Vector2D {
		public int x, y;

		public Vector2D() {
			this.x = 0;
			this.y = 0;
		}

		public Vector2D(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public GameBoard(int width, int height, BattleOfColorsGame game, int cell_size) {
		cache_fill = new Vector2D[1000];
		for(int i = 0; i < 1000; i++)
			cache_fill[i] = new Vector2D();
		cache_fill_num = -1;
		forAI = false;
		this.game = game;
		this.width = width;
		this.height = height;
		cells = new GameCell[width][height];
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				cells[x][y] = new GameCell(0);
			}

		SetCellSize(cell_size);
		Randomize();

		destX = new int[4];
		destY = new int[4];
		
		for(int destPl = 0; destPl < game.numPlayer; destPl ++) {
			GetPlayerCoord(coord2, destPl);
			destX[destPl] = coord2.x;
			destY[destPl] = coord2.y;
		}
		
		/*textures = new Texture[6][2][2];
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 2; j++)
				for(int k = 0; k < 2; k++) {
					if((j == 0) && (k == 0))
						continue;
					textures[i][j][k] = new Texture(Gdx.files.internal("images/" + i + "" + j + "" + k + "-.png"));
					//textures[i][j][k].setFilter(TextureFilter.Linear, TextureFilter.Linear);
				}
		}*/
		
        //buffer = new FrameBuffer(Format.RGBA8888, (int)size, (int)size, false);
        //buffer = new FrameBuffer(Format.RGBA8888, (int)game.screenWidth, (int)game.screenHeight, false);
		GLFrameBuffer.FrameBufferBuilder frameBufferBuilder = new GLFrameBuffer.FrameBufferBuilder((int)game.screenWidth,
				(int)game.screenHeight);
		frameBufferBuilder.addBasicColorTextureAttachment(Format.RGBA8888);
		buffer = frameBufferBuilder.build();
        //buffer = new FrameBuffer(Format.RGBA8888, (int)game.screenWidth, (int)game.screenHeight/*30 * 3 * width, 26 * height*2*/, false);
        batch1 = new SpriteBatch();
        
/*        IntBuffer buf = BufferUtils.newIntBuffer(16);
        Gdx.gl20.glGetIntegerv(GL20.GL_MAX_RENDERBUFFER_SIZE, buf);
        Gdx.app.log("" + buf.get(), "");*/

		shader = game.shader;
//		vertexShader = Gdx.files.internal("shaders/vertex.txt").readString();
//		fragmentShader =  Gdx.files.internal("shaders/cell.txt").readString();
//		shader = new ShaderProgram(vertexShader, fragmentShader);
//		ShaderProgram.pedantic = false;
//		if(!shader.isCompiled()) {
//			Gdx.app.log("err", shader.getLog());
//		}
//		shader.begin();
//		shader.setUniformf("d1", 0.05f);//0.08
//		shader.setUniformf("d2", 0.1f);//0.14
//		shader.setUniformf("smoothing", 0.5f / cell_size);//3
//		shader.setUniformf("radius", 0.08f);
	}
	
	public void dispose() {
		/*for(int i = 0; i < 6; i++)
			for(int j = 0; j < 2; j++)
				for(int k = 0; k < 2; k++)
					if(textures[i][j][k] != null)
						textures[i][j][k].dispose();
*/
		/*for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				pixmaptex[x][y].dispose();
				pixmaptex_prev[x][y].dispose();
			}
		pixmap.dispose();*/
		shader.end();
		//shader.dispose();
		//skin.dispose();
		cells = null;
	}
	
	public void SetAI(boolean forAI) {
		this.forAI = forAI;
	}
	
	private void SetCellSize(int cell_size) {
		this.cell_size = cell_size;
		cell_size2 = cell_size / 2;
		cell_size60 = (int) (cell_size * Math.sin(60 * Math.PI / 180));
		pixmapsizeX = 2 * cell_size;
		pixmapsizeY = 2 * cell_size60;
		pixmap = new Pixmap(pixmapsizeX, pixmapsizeX, Format.RGBA8888);
		pixmaptex = new Texture(pixmap);
}

	public void Randomize() {
		int[] plColors = new int[game.numPlayer];
		for(int i = 0; i < plColors.length; i++)
			plColors[i] = -1;
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				cells[x][y].colorID = (int) (Math.random() * game.maxColors);
			}
		
		IntVector coord = new IntVector(0, 0);
		for(int pl = 0; pl < game.numPlayer; pl++) {
			GetPlayerCoord(coord, pl);
			boolean a = true;
			int newColor = 0;
			while(a) {
				newColor = (int) (Math.random() * game.maxColors);
				a = false;
				for(int i = 0; i < game.numPlayer; i++) {
					if(i == pl)
						continue;
					if(GetPlayerColor(i) == newColor) {
						a = true;
						break;
					}
				}
			}
			cells[coord.x][coord.y].colorID = newColor;
		}
	}
	
	public void DrawShader(Batch batch) {
		long startTime = TimeUtils.millis();

		int colors[] = new int[6];
		int color;
		float dx, xx, yy;

		//batch.setShader(shader);

		shader.begin();
		shader.setUniformf("d1", 0.05f);//0.08
		shader.setUniformf("d2", 0.1f);//0.14
		shader.setUniformf("smoothing", 0.5f / cell_size);//3
		shader.setUniformf("radius", 0.08f);

		batch.begin();
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				if(!blink)
					color = cells[x][y].colorID;
				else
					color = cells[x][y].prevColor;

				batch.setShader(shader);
				for(int i = 0; i < 6; i++) {
					colors[i] = GetNeighborColor(x, y, i);
					shader.setUniformi("neighbors" + i, color == colors[i] ? 1 : 0);
				}

				dx = x * (cell_size * 3 - 1);
				if(y % 2 != 0)
					dx += cell_size + cell_size2;
				xx = Math.round(dx);
				yy = Math.round(y * cell_size60);

				batch.setColor(game.cellColors[color].r, game.cellColors[color].g, game.cellColors[color].b, 1);
				batch.draw(pixmaptex, x_offset + xx, y_offset + yy);

//				if(y == height - 1)
//					Gdx.app.log("" + height + " / " + xx, "" + yy);
			}

		batch.flush();
		//shader.end();
		batch.end();

		// рисуем указатели игроков
		float arrow_w = game.texUp.getWidth() * game.scale;
		float arrow_h = game.texUp.getHeight() * game.scale;
		float pad_up = 4 * game.scale;
		float pad_down = 8 * game.scale;
		SpriteBatch batch2 = new SpriteBatch();
		batch2.begin();

		GetPlayerCoord(coord, 0);
		color = GetPlayerColor(0);
		batch2.setColor(game.cellColors[color].r, game.cellColors[color].g, game.cellColors[color].b, 1);
		batch2.draw(game.texUp, x_offset + cell_size - arrow_w / 2, y_offset - arrow_h - pad_up, arrow_w, arrow_h);

		GetPlayerCoord(coord, 1);
		color = GetPlayerColor(1);
		dx = coord.x * (cell_size * 3 - 1);
		if(coord.y % 2 != 0)
			dx += cell_size + cell_size2;
		xx = Math.round(dx);
		yy = Math.round(coord.y * cell_size60);
		batch2.setColor(game.cellColors[color].r, game.cellColors[color].g, game.cellColors[color].b, 1);
		batch2.draw(game.texDown, x_offset + xx + cell_size - arrow_w / 2, y_offset + yy + cell_size60 * 2 + pad_down, arrow_w, arrow_h);

		if(game.numPlayer == 4) {
			GetPlayerCoord(coord, 2);
			color = GetPlayerColor(2);
			dx = coord.x * (cell_size * 3 - 1);
			if(coord.y % 2 != 0)
				dx += cell_size + cell_size2;
			xx = Math.round(dx);
			yy = Math.round(coord.y * cell_size60);
			batch2.setColor(game.cellColors[color].r, game.cellColors[color].g, game.cellColors[color].b, 1);
			batch2.draw(game.texDown, x_offset + xx + cell_size - arrow_w / 2, y_offset + yy + cell_size60 * 2 + pad_down, arrow_w, arrow_h);

			GetPlayerCoord(coord, 3);
			color = GetPlayerColor(3);
			dx = coord.x * (cell_size * 3 - 1);
			if(coord.y % 2 != 0)
				dx += cell_size + cell_size2;
			xx = Math.round(dx);
			batch2.setColor(game.cellColors[color].r, game.cellColors[color].g, game.cellColors[color].b, 1);
			batch2.draw(game.texUp, x_offset + xx + cell_size - arrow_w / 2, y_offset - arrow_h - pad_up, arrow_w, arrow_h);
		}

		batch2.end();
		batch2.dispose();

		//	Gdx.app.log("GB draw", "" + (TimeUtils.millis()-startTime));
	}
	
	public void Draw(Batch batch) {
		long startTime = TimeUtils.millis();
//		Gdx.gl20.glBlendFuncSeparate(Gdx.gl20.GL_SRC_ALPHA,
//				Gdx.gl20.GL_ONE_MINUS_SRC_ALPHA, Gdx.gl20.GL_ONE, Gdx.gl20.GL_ONE);
		//buffer.bind();
		
/*		Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setBlendFunction(-1, -1);*/

		int dimX = (int)game.screenWidth;
		int dimY = (int)game.screenHeight;
		int xxx = dimX / 90; 
		int yyy = dimY / 26;
		float maxBufX = width / xxx + 1;
		float maxBufY = height / yyy + 1;
		for(int bufX = 0; bufX < maxBufX; bufX++)
			for(int bufY = 0; bufY < maxBufY; bufY++) {
				buffer.bind();
				Gdx.gl.glClearColor(game.bgR, game.bgG, game.bgB, 1f);
		        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		        batch.setShader(shader);
		        batch.begin();
				for(int x = bufX == 0 ? bufX * xxx : bufX * xxx - 1; (x < (bufX + 1) * xxx) && (x < width); x++)
					for(int y = bufY == 0 ? bufY * yyy : bufY * yyy - 2; (y < (bufY + 1) * yyy) && (y < height); y++) {
						DrawCell(batch, x, y, bufX * xxx * 90, bufY * yyy * 26);
					}
				batch.end();
				buffer.end();
				
				//shader.begin();
				//shader.setUniformf("blurSize", (float)(1f/buffer.getHeight()));
				batch1.disableBlending();
				batch1.begin();
				//batch1.setShader(shader);
				
				//Gdx.app.log("" + width, "" + height);
				//Gdx.app.log("123", "" + shader.isCompiled());
				//Gdx.app.log("123", "" + shader.getLog());
				
				//batch1.draw(buffer.getColorBufferTexture(), xx, yy, size, size, 0, 0, (int)size, (int)size, false, true);
				Texture tex = buffer.getColorBufferTexture();
				tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				float scale = (float)cell_size / 30f;
				float dddX = (dimX - xxx * 90) * scale * bufX;
				float dddY = (dimY - yyy * 26) * scale * bufY;
				batch1.draw(tex, x_offset + dimX * bufX * scale - dddX, y_offset + dimY * bufY * scale - dddY,
					dimX * scale, dimY * scale, 0, 0, dimX, dimY, false, true);
			//	Gdx.app.log("" + buffer.getWidth(), "" + width);
				batch1.end();
			}

		//batch1.begin();
				
		//batch1.end();
		//shader.end();		

/*		batch.begin();
		float off = 8f * game.scale;
		for(int i = 0; i < game.numPlayer; i++) {
			int color = GetPlayerColor(i);
			batch.setColor(game.cellColors[color].r, game.cellColors[color].g, game.cellColors[color].b, 1);
			GetPlayerCoord(coord, i);
			float dx = coord.x * cell_size * 3f;
			if(coord.y % 2 != 0) {
				dx += cell_size + cell_size2;
			}
			//dx = Math.round(dx);

			float xx, yy;
			switch(i) {
			case 0:
				xx = x_offset + dx - off;
				yy = y_offset - off*1.2f + coord.y * cell_size60;
				//batch.draw(texMetka, xx, yy, size, size);
				break;
			case 1:
				xx = x_offset + dx + off;
				yy = y_offset + off*1.2f + coord.y * cell_size60;
				//batch.draw(texMetka, xx, yy, size, size, 0, 0, texMetka.getWidth(), texMetka.getHeight(), true, true);
				break;
			case 2:
				xx = x_offset + dx - off;
				yy = y_offset + off*1.2f + (coord.y * cell_size60);
				//batch.draw(texMetka, xx, yy, size, size, 0, 0, texMetka.getWidth(), texMetka.getHeight(), false, true);
				break;
			case 3:
				xx = x_offset + dx + off;
				yy = y_offset - off*1.2f + (coord.y * cell_size60);
				//batch.draw(texMetka, xx, yy, size, size, 0, 0, texMetka.getWidth(), texMetka.getHeight(), true, false);
				break;
			}
		}
		batch.end();
*/
		//Gdx.app.log("GB draw", "" + (TimeUtils.millis()-startTime));
	}

	private void DrawCell(Batch batch, int x, int y, int x_off, int y_off) {
		int color;
		if(!blink)
			color = cells[x][y].colorID;
		else
			color = cells[x][y].prevColor;

		batch.setColor(game.cellColors[color].r, game.cellColors[color].g, game.cellColors[color].b, 1);
		return;

/*		int _size = 64;
		int _cell_size = 30;
		int _cell_size2 = 15;
		int _cell_size60 = 26;

		float dx = x * _cell_size * 3f;
		if(y % 2 != 0) {
			dx += _cell_size + _cell_size2;
		}
		//dx = Math.round(dx);

		float xx = Math.round(dx - x_off); //x_offset +
		float yy = Math.round( y * _cell_size60 - y_off); //y_offset +
		
		batch.setColor(game.cellColors[color].r, game.cellColors[color].g, game.cellColors[color].b, 1);
		//batch.draw(texSolid, xx, yy, _size, _size);
		for(int i = 0; i < 6; i++) {
			int indx2 = i - 1;
			if(indx2 < 0)
				indx2 = 5;
			int j = 0;
			if(GetNeighborColor(x, y, indx2) == color)
				j = 1;
			int k = 0;
			if(GetNeighborColor(x, y, i) == color)
				k = 1;
			if((j == 0) && (k == 0))
				continue;
			//tex = textures[i][j][k];
			
			//skin.getDrawable("" + i + "" + j + "" + k).draw(batch, xx, yy, _size, _size);
		}*/
		
/*		float dist = (float) Math.abs((width * y - height * x) / Math.sqrt(width * width + height * height)) + 1;
		float dist2 = (float) Math.hypot(x*1.5f, y/2);
		String str = "" + Math.floor(1 + 5*5*5 / dist/dist/dist + dist2*dist2);
		game.miniFont.draw(batch, str.substring(0, 3), xx, yy + size/2);*/
		
	}
	
/*	private void DrawCell(Batch batch, int x, int y, boolean draw) {
		pixmap_prev[x][y] = pixmap_cur[x][y];
		pixmaptex_prev[x][y].draw(pixmap_prev[x][y], 0, 0);
		
		pixmap = pixmap_cur[x][y];
		
		int color = cells[x][y].colorID;
		
		for(int i = 0; i < 6; i++)
			colors[i] = GetNeighborColor(x, y, i);
		
		for(int xp = 0; xp < pixmapsizeX; xp++)
			for(int yp = 0; yp < pixmapsizeY; yp++) {
				while (true) { // ���� ���� ���� 60-70%
					float dst, dst2;
					dst = Destantion(pnt1, pnt2, xp, yp, dx1_2, dy1_2, hypot1_2);
					if(dst < 0) {
						pixmap.setColor(0,0,0,0);
						break;
					}
					if(colors[5] == color)
						dst = 1000;
					
					dst2 = Destantion(pnt3, pnt1, xp, yp, dx3_1, dy3_1, hypot3_1);
					if(dst2 < 0) {
						pixmap.setColor(0,0,0,0);
						break;
					}
					if((colors[0] != color) && (dst > dst2))
						dst = dst2;
					
					dst2 = Destantion(pnt4, pnt6, xp, yp, dx4_6, dy4_6, hypot4_6);
					if(dst2 < 0) {
						pixmap.setColor(0,0,0,0);
						break;
					}
					if((colors[3] != color) && (dst > dst2))
						dst = dst2;
					
					dst2 = Destantion(pnt6, pnt5, xp, yp, dx6_5, dy6_5, hypot6_5);
					if(dst2 < 0) {
						pixmap.setColor(0,0,0,0);
						break;
					}
					if((colors[2] != color) && (dst > dst2))
						dst = dst2;							
					
					if(colors[4] == color)
						dst2 = 1000;
					else
						dst2 = Destantion(pnt2, pnt4, xp, yp, dx2_4, dy2_4, hypot2_4);
					if(Math.abs(dst) > Math.abs(dst2))
						dst = dst2;
					
					if(colors[1] == color)
						dst2 = 1000;
					else
						dst2 = Destantion(pnt5, pnt3, xp, yp, dx5_3, dy5_3, hypot5_3);
					if(Math.abs(dst) > Math.abs(dst2))
						dst = dst2;
					
					if(dst > 0) { // ����� 10-20% ����.�������
						hsv.CopyFrom(game.cellColors[color]);
						float offset = 0.3f;
						float amp = 1f;
						if(hsv.r > 0)
							hsv.r = offset + amp*dst/cell_size;
						
						if(hsv.g > 0)
							hsv.g = offset + dst*amp/cell_size;
						
						if(hsv.b > 0) 
							hsv.b = offset + dst*amp/cell_size;
						
						float add = 1.2f * (1f - (dst - pixmapsizeY * 0.1f) / (pixmapsizeY * 0.1f)) *
								(1f - (float)yp / (float)(pixmapsizeY) * 2f);
						if ((yp < pixmapsizeY * 0.5f) && (dst < pixmapsizeY * 0.2f) && (dst > pixmapsizeY * 0.1f)) {
							hsv.r += add;
							hsv.g += add;
							hsv.b += add;
						}
						
//						if ((yp < pixmapsizeY * 0.5f) && (dst < pixmapsizeY * 0.5f) && (dst > pixmapsizeY * 0.3f)) {
//							hsv.r *= 0.8f;
//							hsv.g *= 0.8f;
//							hsv.b *= 0.8f;
//						}
						
						if(hsv.r > 1)
							hsv.r = 1;
						if(hsv.g > 1)
							hsv.g = 1;
						if(hsv.b > 1)
							hsv.b = 1;
						
						if(hsv.r < 0)
							hsv.r = 0;
						if(hsv.g < 0)
							hsv.g = 0;
						if(hsv.b < 0)
							hsv.b = 0;
						
//						hsv.v = dst/cell_size + 0.4f;
//						if(hsv.v > 1)
//							hsv.v = 1;
//						hsv.HSV2RGB();
						pixmap.setColor(hsv.r, hsv.g, hsv.b, 1);
					}
					else {
						pixmap.setColor(0,0,0,0);
					}
					break;
				}
				pixmap.drawPixel(xp, yp); // ��� ������ ���� �� 25%
			};
			
		pixmaptex[x][y].draw(pixmap, 0, 0); // ��� ����� ����������� �� �������� ����.�������
		float dx = Math.round(x * (cell_size - 0.5f) * 3);
		if(y % 2 != 0)
			dx += cell_size + cell_size2;
		if(draw)
			batch.draw(pixmaptex[x][y], x_offset + dx, y_offset + y * (cell_size60));
	}
	*/

	private static void GetNeighbor(int x, int y, int i) {
		int xx = x;
		if(y % 2 != 0)
			xx++;
		switch (i) {
			case 0: { x_getx = xx-1; y_gety = y-1; return; }
			case 1: { x_getx = x; y_gety = y-2; return; }
			case 2: { x_getx = xx; y_gety = y-1; return; }
			case 3: { x_getx = xx; y_gety = y+1; return; }
			case 4: { x_getx = x; y_gety = y+2; return; }
			case 5: { x_getx = xx-1; y_gety = y+1; return; }
		}
	}

	private static int GetNeighborX(int x, int y, int i) {
		int xx = x;
		if(y % 2 != 0)
			xx++;
		switch (i) {
		case 0: return xx-1;
		case 1: return x;
		case 2: return xx;
		case 3: return xx;
		case 4: return x;
		case 5: return xx-1;
		}
		
		return -1;		
	}

	private static int GetNeighborY(int y, int i) {
		switch (i) {
		case 0: return y-1;
		case 1: return y-2;
		case 2: return y-1;
		case 3: return y+1;
		case 4: return y+2;
		case 5: return y+1;
		}
		
		return -1;		
	}
	
	private final int GetNeighborColor(int x, int y, int i) {
		return GetCellColor(GetNeighborX(x, y, i), GetNeighborY(y, i));
	}

	public int GetCellColor(int x, int y) {
		if(y < 0)
			return -1;
		if(x < 0)
			return -1;
		if(y > height - 1)
			return -1;
		if(x > width - 1)
			return -1;
		
		if(forAI)
			return cells[x][y].tmpColor;
		else
			if(blink)
				return cells[x][y].prevColor;
			else
				return cells[x][y].colorID;
	}

	private boolean GetCellTmp(int x, int y) {
		if(y < 0)
			return true;
		if(x < 0)
			return true;
		if(y > height - 1)
			return true;
		if(x > width - 1)
			return true;
		
		return cells[x][y].tmp;
	}

	private boolean GetCellTmp_() {
		if(y_gety < 0)
			return true;
		if(x_getx < 0)
			return true;
		if(y_gety > height - 1)
			return true;
		if(x_getx > width - 1)
			return true;

		return cells[x_getx][y_gety].tmp;
	}
	
	private float Destantion(Vector2 pnt1, Vector2 pnt2, int xp, int yp, float dx, float dy, float hypot) {
		return (dy * xp + dx * yp + (pnt1.x * pnt2.y - pnt2.x * pnt1.y)) / hypot;
	}

	public void FillColor(int x, int y, int _color, int old_color) {
		ClearFillColor();
		color = _color;
		FillColorPrivate(x, y, old_color);
	}
	
	private void FillColorPrivate(int x, int y, int old_color) {
		int colorXY = GetCellColor(x, y);
		if((colorXY != old_color)&&(colorXY != color))
			return;

		if((x < 0) || (y < 0) || (x > width - 1) || (y > height - 1))
			return;

		if(cells[x][y].tmp) {
			//Gdx.app.log("tmp:", "" + x + " / " + y);
			return;
		}
		
//		if(!forAI)
//			Gdx.app.log("" + colorXY + " // " + cells[x][y].colorID, "" + x + " / " + y);
		
		if(forAI)
			cells[x][y].tmpColor = color;
		else {
			//cells[x][y].prevColor = cells[x][y].colorID;
			cells[x][y].colorID = color;
		}
		cells[x][y].tmp = true;
		for(int i = 0; i < 6; i++) {
			int neighborX = GetNeighborX(x, y, i);
			int neighborY = GetNeighborY(y, i);
			if(colorXY == color)
				FillColorPrivate(neighborX, neighborY, color);
			else
				FillColorPrivate(neighborX, neighborY, old_color);
		}
	}

	private void SetTmpColor(GameCell cell) {
		if (forAI)
			cell.tmpColor = tmpColor;
		else
			cell.colorID = tmpColor;
		cell.tmp = true;
	}

	private void LOGG(int player, String s1, String s2) {
		if(player == 0)
			Gdx.app.log(s1, s2);
	}

	// а теперь закрасим пойманные клетки
	public boolean FillHiddenCells(int player) {
		long startTime = TimeUtils.millis();

		tmpColor = GetPlayerColor(player);
		boolean res = false;
		boolean rez, repeat;
		GameCell cell;

		//LOGG(player,"START", "");

		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				cells[x][y].cache_fill = Cache_fill.nil;

		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				//LOGG(player, "****************** X=" + x, "Y=" + y);
				if(cells[x][y].tmp) { // если tmp, то ячейка занята
					//LOGG(player, "zanyto: x=" + x, "y=" + y);
					continue;
				}

				cell = cells[x][y];
/*				if(cell.cache_fill == Cache_fill.yes) {
					SetTmpColor(cell);
                    if(player == 0)
					    Gdx.app.log("yes: x=" + x, "y=" + y);
					continue;
				}

				if(cell.cache_fill == Cache_fill.no) {
                    if(player == 0)
				        Gdx.app.log("no: x=" + x, "y=" + y);
					continue;
				}
*/
				if(cell.cache_fill != Cache_fill.nil)
					continue;

				int xx = x;
				int yy = y;
				int x0 = -1, y0 = -1;
				repeat = true;
				// будем идти вдоль препятствия поворачивая против часовой стрелки,
				// при пересечении начальной точки 2 раза - мы в замкнутой области
				//
				int dir; // выбираем направление
				boolean vihod = false;
				boolean nashli = false;
				for(dir = 0; dir < 6; dir++) {
					GetNeighbor(x, y, dir);
					if(GetCellTmp_()) { // ищем закрашенную
						nashli = true;
						//LOGG(player,"nashli: x=" + x_getx, "y=" + y_gety);
						for(int i = 0; i < 6; i++) {
							dir++;
							if(dir == 6)
								dir = 0;
							GetNeighbor(x, y, dir);
							if(!GetCellTmp_()) { // ищем незакрашенную
								//LOGG(player,"vihod: x=" + x_getx, "y=" + y_gety);
								vihod = true;
								break;
							}
						}
						break;
					}
				}

				if((!vihod)&&(nashli)) { //если вся ячейка окружена, то закрашиваем ее
					SetTmpColor(cells[x][y]);
					res = true;
					//LOGG(player, "vsa okrujena: x=" + x, "y=" + y);
					continue;
				}

				if(dir == 6)
					dir = 0;
				if(nashli) {
					x0 = x;
					y0 = y;
				}
				
				cache_fill_num = -1;
				int vernulis = 0;
				while(repeat) {
					xx = GetNeighborX(xx, yy, dir);
					yy = GetNeighborY(yy, dir);

					if(cells[xx][yy].cache_fill == Cache_fill.yes) {
					    //LOGG(player, "cache_fill.yes: x=" + xx, "y=" + yy);
						for(int i = 0; i < cache_fill_num; i++) {
							cell = cells[cache_fill[i].x][cache_fill[i].y];
							cell.cache_fill = Cache_fill.yes;
							SetTmpColor(cell); // закрашиваем
							//LOGG(player, "закрашиваем: x=" + cache_fill[i].x, "y=" + cache_fill[i].y);
						}
						cache_fill_num = -1;
						repeat = false;
						res = true;
						break;
					}

					if(cells[xx][yy].cache_fill == Cache_fill.no) {
						for(int i = 0; i < cache_fill_num; i++) {
							cell = cells[cache_fill[i].x][cache_fill[i].y];
							cell.cache_fill = Cache_fill.no;
						}
						cache_fill_num = -1;
						repeat = false;
						break;
					}

					cache_fill_num++;
					cache_fill[cache_fill_num].x = xx;
					cache_fill[cache_fill_num].y = yy;

					if((xx == x0)&&(yy == y0)) {
						vernulis++;
						if(vernulis == 2) { // если вернулись в начальную ячейку, то закрашиваем
							//LOGG(player, "vernulis x=" + x, "y=" + y + " / " + x0 + " / " + y0);
							SetTmpColor(cells[x][y]);
							repeat = false;
							res = true;

							for(int i = 0; i < cache_fill_num; i++) {
								cell = cells[cache_fill[i].x][cache_fill[i].y];
								cell.cache_fill = Cache_fill.yes;
								SetTmpColor(cell);
								//LOGG(player, "Красим x=" + cache_fill[i].x, "y=" + cache_fill[i].y);
							}
							cache_fill_num = -1;

							break;
						}
					}

					rez = false;
					for(int destPl = 0; destPl < game.numPlayer; destPl ++) {
						if(player == destPl)
							continue;
						rez = rez || ((xx == destX[destPl]) && (yy == destY[destPl]));
					}
					if(rez) {
						for(int i = 0; i < cache_fill_num; i++) {
							cell = cells[cache_fill[i].x][cache_fill[i].y];
							cell.cache_fill = Cache_fill.no;
						}
						cache_fill_num = -1;

						repeat = false;
						break;
					}

					if(!nashli) {
						for(int i = 0; i < 6; i++) {
							int dd = dir + i;
							if(dd > 5)
								dd -= 6;
							GetNeighbor(xx, yy, dd);
							if(GetCellTmp_()) {
								nashli = true;
								x0 = xx;
								y0 = yy;
								//LOGG(player, "nashli2222 x=" + x, "y=" + y + " / " + x0 + " / " + y0);
								for(int j = 0; j < 6; j++) {
									dd++;
									if(dd == 6)
										dd = 0;
									GetNeighbor(xx, yy, dd);
									if(!GetCellTmp_()) {
										dir = dd;
										break;
									}
								}
								break;
							}
						}
						continue;
					}
					
					int dir_1 = dir - 1; // меняем направление по часовой стрелке
					if(dir_1 < 0)
						dir_1 = 5;
					GetNeighbor(xx, yy, dir_1);
					if(!GetCellTmp_()) {
						dir = dir_1; // если справа появилась лазейка, поворачиваем туда
						continue;
					}

					boolean cont = false;
					for(int j = 0; j < 6; j++) {
						int dd = dir + j;
						if(dd > 5)
							dd -= 6;
						if(dd < 0)
							dd = 5;
						GetNeighbor(xx, yy, dd);
						if(!GetCellTmp_()) {
							dir = dd;
							cont = true;
							break;
						}
					}
					if(!cont)
						Gdx.app.log("!!!!!!! ОШИБКА !!!!!!!!!!", "");
				}
//				if(cache_fill_num > 0)
//					Gdx.app.log("==========!!!!!!!========", "");
//				cache_fill_num = -1;
			}
		//Gdx.app.log("Obr: ", "" + obr);
		//Gdx.app.log("FillHidden", "" + (TimeUtils.millis()-startTime));
		return res;
	}
	
/*	public boolean FillHiddenCells(int player) {
		long startTime = TimeUtils.millis();
		tmpColor = GetPlayerColor(player);
		boolean res = false;
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				if(cells[x][y].tmp)
					continue;
				
				for(int xx = 0; xx < width; xx++)
					for(int yy = 0; yy < height; yy++)
						cells[xx][yy].rez = false;

				if(!FillHiddenCellsPrivate(x, y, player)) {
					res = true;
					//cells[x][y].prevColor = cells[x][y].colorID; 
					if(forAI)
						cells[x][y].tmpColor = tmpColor;
					else
						cells[x][y].colorID = tmpColor;
				}
			}
		Gdx.app.log("FillHidden", "" + (TimeUtils.millis()-startTime));
		return res;
	}*/
	
	private boolean FillHiddenCellsPrivate(int x, int y, int player) {
		if((x < 0) || (y < 0) || (x > width - 1) || (y > height - 1))
			return false;

		if(cells[x][y].rez)
			return false;
		
		boolean rez = false;
		for(int destPl = 0; destPl < game.numPlayer; destPl ++) {
			if(player == destPl)
				continue;
			rez = rez || ((x == destX[destPl]) && (y == destY[destPl]));
		}
		if(rez)
			return true;
		
		if(cells[x][y].tmp)
			return false;

		//Gdx.app.log("-" + zzz, "");
		cells[x][y].rez = true;
		for(int i = 0; i < 6; i++) {
			GetNeighborX(x, y, i);
			if(FillHiddenCellsPrivate(x_getx, y_gety, player))
				return true;
		}

		return false;
	}

	public void ClearFillColor() {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				cells[x][y].tmp = false;
			}
	}

	public void Copy2tmpColor() {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				cells[x][y].tmpColor = cells[x][y].colorID;
	}
	
	public void Copytmp2Board(GameCell[][] tmpBoard) {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				tmpBoard[x][y].tmpColor = cells[x][y].tmpColor;
	}
	
	public void CopyBoard2tmp(GameCell[][] tmpBoard) {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				cells[x][y].tmpColor = tmpBoard[x][y].tmpColor;
			}
	}
	
	public int GetPlayerScore(int player, boolean _withCorrect, float koeff) {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				cells[x][y].tmpScore = false;
		GetPlayerCoord(coord, player);
		color = GetPlayerColor(player);
		withCorrect = _withCorrect;
		corr_koeff = koeff;
		return (int)ScoreCycle(coord.x, coord.y);
	}

	private float ScoreCycle(int x, int y) {
		if(GetCellColor(x, y) != color)
			return 0;
		
		if(cells[x][y].tmpScore)
			return 0;
		
		cells[x][y].tmpScore = true;
		float result = 1;
		if(withCorrect) {
			float dist = (float) Math.abs((width * y - height * x) / Math.sqrt(width * width + height * height)) + 1;
			float dist2 = (float) Math.hypot((coord.x-x)*1.5f, (coord.y-y)/2);
			result = (float) (1 + corr_koeff*corr_koeff*corr_koeff / dist/dist/dist + (dist2*dist2*dist2*corr_koeff/5));//(1 + Math.hypot(x*1.5f-coord.x, (y/2-coord.y)) * corr_koeff);
		}
			
		for(int i = 0; i < 6; i++) {
			int neighborX = GetNeighborX(x, y, i);
			int neighborY = GetNeighborY(y, i);
			result += ScoreCycle(neighborX, neighborY);
		}
		
		return result;
	}

	public boolean IsBorder(int player, int borderColor) {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				cells[x][y].tmpScore = false;
		GetPlayerCoord(coord, player);
		int color = GetPlayerColor(player);
		return IsBorderCycle(coord.x, coord.y, color, borderColor);
	}
	
	private boolean IsBorderCycle(int x, int y, int color, int borderColor) {
		if(GetCellColor(x, y) != color) {
			if(GetCellColor(x, y) == borderColor)
				return true;
			else
				return false;
		}
		
		if(cells[x][y].tmpScore)
			return false;
		
		cells[x][y].tmpScore = true;
		for(int i = 0; i < 6; i++)
			if(IsBorderCycle(GetNeighborX(x, y, i), GetNeighborY(y, i), color, borderColor))
				return true;
		
		return false;
	}
	
	public int GetPlayerColor(int player) {
		GetPlayerCoord(coord, player);
		return GetCellColor(coord.x, coord.y);
	}
	
	public void GetPlayerCoord(IntVector coord, int activePlayer2) {
		switch (activePlayer2) {
		case 0:
			coord.x = 0;
			coord.y = 0;
			break;
		case 1:
			coord.x = width - 1;
			coord.y = height - 1;
			break;
		case 2:
			coord.x = 0;
			coord.y = height - 1;
			break;
		case 3:
			coord.x = width - 1;
			coord.y = 0;
			break;
		};
	}
/*	public float getPrefWidth () {
		return 10;//width * cell_size;
	}

	public float getPrefHeight () {
		return height * cell_size60;
	}*/

	public void CopyColor() {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				cells[x][y].prevColor = cells[x][y].colorID;
			}
	}
}
