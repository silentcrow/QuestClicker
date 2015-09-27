package com.tophattiger.Helper.Data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.tophattiger.GameWorld.GameRenderer;

import java.util.Calendar;

/**
 * Created by Collin on 5/25/2015.
 */
public  class DataManagement {

    /**Called to save the game state to the JSON file
     * @param  fileName Name of the file to save to
     * @param  game Renderer class being used
     */
    public static void saveGame(String fileName, GameRenderer game){
        JsonData jData = new JsonData();

        jData.currentVersion = game.getVersion();
        jData.gold = Gold.getGold();
        game.getHero().save(jData);
        jData.closeTime = com.tophattiger.Helper.Data.DataHolder.currentTime;
        jData.helperAmount = com.tophattiger.Helper.Data.DataHolder.helperAmount;
        jData.combosUnlocked = game.getComboList().getCombosUnlocked();
        jData.abilitiesUnlocked = game.getAbilityList().getAbilitiesUnlocked();
        JsonData.helpers.clear();
        JsonData.comboLevels.clear();
        JsonData.abilityLevels.clear();
        game.getHelpers().saveGame();
        game.getComboList().save();
        game.getAbilityList().save();

        jData.comboLevel = JsonData.comboLevels;
        jData.helper = JsonData.helpers;
        jData.abilityLevel = JsonData.abilityLevels;

        Json json = new Json();
        writeFiles(fileName,json.toJson(jData));

    }

    /**Called to load the game with a JSON file
     *
     * @param fileName Name of the file to save to
     * @param game  Renderer class being used
     */
    public static void loadGame(String fileName, GameRenderer game) {
        String save = readFile(fileName);
        if (!save.isEmpty()) {
            Json json = new Json();
            JsonData jData = json.fromJson(JsonData.class, save);

            Gold.setGold(jData.gold);
            game.getHero().load(jData);
            game.getEnemy().set();
            //Check for version recorded to make sure version conflicts don't arise when updating
            if(jData.currentVersion >= 1) {
                com.tophattiger.Helper.Data.DataHolder.pastTime = jData.closeTime;
                com.tophattiger.Helper.Data.DataHolder.helperAmount = jData.helperAmount;
                com.tophattiger.Helper.Data.DataHolder.timeDif = (com.tophattiger.Helper.Data.DataHolder.currentTime.getTimeInMillis() - com.tophattiger.Helper.Data.DataHolder.pastTime.getTimeInMillis())/1000;
                game.autoGold();
                game.getComboList().setCombosUnlocked(jData.combosUnlocked);
                JsonData.comboLevels = jData.comboLevel;
                JsonData.helpers = jData.helper;
                game.getHelpers().loadGame();
                game.getComboList().load();
                if(jData.currentVersion >= 5){
                    JsonData.abilityLevels = jData.abilityLevel;
                    game.getAbilityList().setAbilitiesUnlocked(jData.abilitiesUnlocked);
                    game.getAbilityList().load();
                }
            }
            else {
                com.tophattiger.Helper.Data.DataHolder.pastTime = com.tophattiger.Helper.Data.DataHolder.currentTime;
                com.tophattiger.Helper.Data.DataHolder.helperAmount = 1;
            }
        }
        if(save.isEmpty() || game.getHero().getName().equals("Hero's Name"))
            game.getNameScreen().show();
    }

    /** Reset the game to beginning state
     *
     * @param game GameRenderer to reset
     */
    public static void reset(GameRenderer game){
        Gold.reset();
        game.getHero().reset();
        game.getHelpers().reset();
        game.getBackground().reset();
        game.getComboList().reset();
        game.getAbilityList().reset();
        com.tophattiger.Helper.Data.DataHolder.helperAmount = 1;
        game.getTable().resetButton();
        game.getMenu().close();
        game.getNameScreen().show();
    }

    public static void writeFiles(String fileName, String s){
        FileHandle file = Gdx.files.local(fileName);
        file.writeString(com.badlogic.gdx.utils.Base64Coder.encodeString(s), false);
    }

    public static String readFile(String fileName) {
        FileHandle file = Gdx.files.local(fileName);
        if (file != null && file.exists()) {
            String s = file.readString();
            if (!s.isEmpty()) {
                return com.badlogic.gdx.utils.Base64Coder.decodeString(s);
            }
        }
        return "";
    }

    /** Class to hold data for saving and loading*/
    public static class JsonData{
        public int questCompleted,questRequired,helperAmount,touchLevel,combosUnlocked,abilitiesUnlocked,currentVersion;
        public static IntArray abilityLevels = new IntArray();
        public static IntArray helpers = new IntArray();
        public static IntArray comboLevels = new IntArray();
        IntArray comboLevel;
        IntArray helper;
        IntArray abilityLevel;
        public double clickPower,questProgress,gold,touchCost;
        public Calendar closeTime;
        public String name;
    }
}