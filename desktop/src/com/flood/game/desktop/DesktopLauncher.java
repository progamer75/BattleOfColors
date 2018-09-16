package com.flood.game.desktop;

import com.BattleOfColors.game.BattleOfColorsGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {

		System.setProperty("user.name","EnglishWords");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 480;
		config.height = 640;
		//config.samples = 2;
		new LwjglApplication(new BattleOfColorsGame(new ActionResolverDesktop()), config);
	}
}

/*	TODO надо проверить:

		AssetManager
		Bitmap
		BitmapFont
		BitmapFontCache
		CameraGroupStrategy
		DecalBatch
		ETC1Data
		FrameBuffer
		Mesh
		ParticleEffect
		Pixmap
		PixmapPacker
		ShaderProgram
		Shape
		Skin
		SpriteBatch
		SpriteCache
		Stage
		Texture
		TextureAtlas
		TileAtlas
		TileMapRenderer
		World
*/