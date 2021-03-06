package com.tophattiger.UI;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.tophattiger.GameObjects.Characters.Enemy;
import com.tophattiger.GameWorld.GameRenderer;
import com.tophattiger.Helper.Data.AssetLoader;
import com.tophattiger.Helper.Data.DataHolder;
import com.tophattiger.Helper.Data.DataManagement;
import com.tophattiger.Helper.Data.Gold;
import com.tophattiger.UI.Menu.NameScreen;



public class Background extends Actor {
    float abilityGoldTime;
    boolean touch;
    double abilityGoldAmount;
    ComboText combo;
    GameRenderer game;
    Stage stage;
    Sprite sprite;

    /**
     * Background of the game. User can tap on it to attack enemy
     * @param _game Game to add this
     */
    public Background(GameRenderer _game){
        game = _game;
        stage = game.getStage();
        sprite = AssetLoader.regBackground;
        sprite.setSize(DataHolder.width, DataHolder.height);
        touch = false;
        abilityGoldAmount = 0;
        abilityGoldTime = 0;

        setBounds(0, 0, stage.getWidth(), stage.getHeight());
        setTouchable(Touchable.enabled);

        combo = _game.getCombo();

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!touch && !NameScreen.SettingName) {
                    Gold.add(20000);
                    if(abilityGoldTime > 0){    //Drop coins if the gold ability is activated and tapped
                        game.dropCoins((int)abilityGoldAmount,1);
                    }
                    game.getTap().tap((int)x,(int)y);
                    touch = true;
                    DataManagement.JsonData.clicks++;
                    if(game.getAdsController().getSignedInGPGS())
                        game.getAdsController().incrementAchievementGPGS(DataHolder.thousandTaps,1);
                    combo.tap();    //Increase combo
                    game.getHero().attack();
                    Enemy enemy = game.getEnemy();
                    if (!enemy.isDead()) {
                        if (!enemy.isHit())
                            enemy.setEnemyTime(0);
                        enemy.getHit(game.getHero().getTouchPower());
                        DataManagement.JsonData.damageDoneByHero += game.getHero().getTouchPower();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Decrement gold ability and buffer for touching
     * @param delta Time in between calls
     */
    @Override
    public void act(float delta) {
        if(touch){
                touch = false;
        }
        if(abilityGoldTime >= 0){
            abilityGoldTime -= delta;
        }
    }

    /**
     * Draw the background
     * @param batch Batch to draw to
     * @param parentAlpha Parent alpha
     */
    @Override
         public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }

    /**
     * Start the ability to drop gold each tap
     * @param amount Amount of gold to drop
     * @param time Duration
     */
    public void abilityGold(double amount,int time){
        abilityGoldAmount = amount;
        abilityGoldTime = time;
    }

    /**
     * Reset gold ability time
     */
    public void reset(){
        abilityGoldTime = 0;
    }


}
