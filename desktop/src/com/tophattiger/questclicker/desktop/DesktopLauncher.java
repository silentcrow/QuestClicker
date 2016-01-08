package com.tophattiger.questclicker.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tophattiger.Helper.Controllers.AdsController;
import com.tophattiger.questclicker.QuestClicker;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 990; config.height = 540;
		new LwjglApplication(new QuestClicker(new AdsController() {
			@Override
			public void showBannerAd() {

			}

			@Override
			public void hideBannerAd() {

			}
		}), config);
	}
}
