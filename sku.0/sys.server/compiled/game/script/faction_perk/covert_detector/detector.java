package script.faction_perk.covert_detector;

import script.dictionary;
import script.library.factions;
import script.library.trial;
import script.library.utils;
import script.obj_id;
import script.location;

import java.util.Vector;

public class detector extends script.base_script
{
    private static final float DETECTION_RADIUS = 32.0f;
    private static final int SMOLDER_HP = 2000;
    private static final int FIRE_HP = 1000;

    public detector()
    {
    }

    public int OnInitialize(obj_id self) throws InterruptedException
    {
        messageTo(self, "flagPlayers", null, 15.0f, false);
        setInvulnerable(self, false);
        pvpSetAttackableOverride(self, true);
        return SCRIPT_CONTINUE;
    }

    public int flagPlayers(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id[] players = trial.getValidPlayersInRadius(self, DETECTION_RADIUS);
        Vector<obj_id> filteredPlayers = new Vector<>();
        boolean applyFlag = true;

        if (players == null || players.length == 0)
        {
            applyFlag = false;
        }

        if (applyFlag)
        {
            for (obj_id player : players)
            {
                if (isIdValid(player) && exists(player) && pvpGetType(player) == PVPTYPE_COVERT)
                {
                    factions.goOvertWithDelay(player, 0.0f);
                    sendSystemMessage(player, "You have been flagged for PvP by a covert faction scanner.", null);
                }
            }
        }

        handleDetectorDamage(self);
        return SCRIPT_CONTINUE;
    }

    public void handleDetectorDamage(obj_id self) throws InterruptedException
    {
        int curHP = getHitpoints(self);
        if (curHP < 1)
        {
            explodeDetector(self);
        }
        else if (!utils.hasScriptVar(self, "playingEffect") && curHP < SMOLDER_HP)
        {
            location death = getLocation(self);
            utils.setScriptVar(self, "playingEffect", 1);
            String effect = (curHP < FIRE_HP) ? "clienteffect/lair_hvy_damage_fire.cef" : "clienteffect/lair_med_damage_smoke.cef";
            playClientEffectLoc(self, effect, death, 0);
            messageTo(self, "effectManager", null, 15, true);
        }
    }

    public void explodeDetector(obj_id self) throws InterruptedException
    {
        destroyObject(self);
    }
}

