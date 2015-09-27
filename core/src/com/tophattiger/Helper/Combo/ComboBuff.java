package com.tophattiger.Helper.Combo;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.tophattiger.GameWorld.GameRenderer;
import com.tophattiger.Helper.Data.AssetLoader;
import com.tophattiger.Helper.Data.DataManagement;
import com.tophattiger.Helper.Data.Gold;

/**
 * Created by Collin on 7/15/2015.
 */
public class ComboBuff {

    public enum TYPE{
        HERODAMAGE,ALLHELPERDAMAGE,GOLD
    }

    String description;
    double amount,cost,originalAmount,currentAmount;
    int reqCombo, level;
    TYPE type;
    boolean active, leveled;
    Image picture;
    GameRenderer game;


    public ComboBuff(double _amount,int _reqCombo, TYPE _type, GameRenderer _game){
        originalAmount = _amount;
        type = _type;
        reqCombo = _reqCombo;
        game = _game;
        currentAmount = 0;
        level = 0;
        leveled = false;
        active = false;
        picture = new Image(AssetLoader.textureAtlas.findRegion(Integer.toString(reqCombo)));
        picture.setSize(128, 128);
        cost = reqCombo;
        amount = originalAmount;
        setDescription();
    }

    public void check(int combo){
        if(level != 0 && combo==reqCombo){
            activate();
        }
        else if(leveled && combo >= reqCombo){
            undo();
            activate();
            leveled = false;
        }
    }
    public void activate(){
        if(type == TYPE.ALLHELPERDAMAGE){
            game.getHelpers().comboDamage(currentAmount);
        }
        else if(type == TYPE.HERODAMAGE){
            game.getHero().comboDamage(currentAmount);
        }
        else if(type == TYPE.GOLD){
            game.comboGold(currentAmount);
        }
        active = true;
    }

    public void undo(){
        if(active){
            if(type == TYPE.ALLHELPERDAMAGE){
                game.getHelpers().undoComboDamage();
            }
            else if(type == TYPE.HERODAMAGE){
                game.getHero().undoComboDamage(amount);
            }
            else if(type == TYPE.GOLD){
                game.undoComboGold(amount);
            }
            active = false;
        }
    }

    public void level(){
        level ++;
        currentAmount = amount;
        amount *= 1.2;
        setDescription();
        setCost();
        leveled = true;
        game.getHero().levelAnimation();
    }

    public void setDescription(){
        description = "Achieve a " + Integer.toString(reqCombo) + "x combo to multiply ";
        if(type == TYPE.ALLHELPERDAMAGE){
            description += "all helper damage ";
        }
        else if(type == TYPE.HERODAMAGE){
            description += "the hero's damage ";
        }
        else if(type == TYPE.GOLD){
            description += "gold dropped by enemies ";
        }
        description += "by " + Gold.getNumberWithSuffix(amount);
    }

    public String getDescription(){
        return description;
    }

    public void setCost(){
        cost = (level+2)*(level + 1) * reqCombo;
    }

    public double getCost(){return cost;}

    public int getLevel(){return level;}

    public Image getPicture(){return picture;}

    public double getCurrentAmount(){return currentAmount;}

    public void reset(){
        amount = originalAmount;
        currentAmount = 0;
        level = 0;
        setDescription();
        setCost();
    }

    public void load(int i){
        if(DataManagement.JsonData.comboLevels != null) {
            level = DataManagement.JsonData.comboLevels.get(i);
            amount = 1.5*(level+1);
            currentAmount = 1.5*level;
            setDescription();
            setCost();
        }
    }

    public void save(){
        DataManagement.JsonData.comboLevels.add(level);
    }

}