package script.item.gcw_buff_banner;

import script.dictionary;
import script.library.buff;
import script.library.factions;
import script.library.trial;
import script.library.utils;
import script.obj_id;

import java.util.Vector;

public class banner_buff_manager extends script.base_script
{
    public banner_buff_manager()
    {
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        trial.cleanupObject(self);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        messageTo(self, "buffPlayers", null, 1.0f, false);
        messageTo(self, "handleDeleteSelf", null, 180.0f, false);
        return SCRIPT_CONTINUE;
    }
    public String getBannerBuff(obj_id self) throws InterruptedException
    {
        obj_id player = trial.getParent(self);
        if (!isIdValid(player))
        {
            return null;
        }
        if (factions.isImperial(player))//I replaced this section with a faction check
        {
            return "pvp_aura_buff_target";
        }
        if (factions.isRebel(player))
        {
            return "pvp_aura_buff_rebel_target";
        }
        return null;
    }
    public int buffPlayers(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id[] players = trial.getValidPlayersInRadius(self, 30.0f);
        Vector filteredPlayers = new Vector();
        filteredPlayers.setSize(0);
        boolean applyBuff = true;
        int faction = -1;
        if (!hasObjVar(self, "parent.faction"))
        {
            applyBuff = false;
        }
        else 
        {
            faction = getIntObjVar(self, "parent.faction");
        }
        if (players == null || players.length == 0)
        {
            applyBuff = false;
        }
        else 
        {
            for (obj_id player : players) {
                switch (faction) {
                    case 0:
                        if (factions.isRebel(player)) {
                            utils.addElement(filteredPlayers, player);
                        }
                        break;
                    case 1:
                        if (factions.isImperial(player)) {
                            utils.addElement(filteredPlayers, player);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        if (filteredPlayers == null || filteredPlayers.size() == 0)
        {
            applyBuff = false;
        }
        if (filteredPlayers != null)
        {
            players = new obj_id[filteredPlayers.size()];
            filteredPlayers.toArray(players);

        }
        String buffToApply = getBannerBuff(self);
        if (buffToApply == null)
        {
            applyBuff = false;
        }
        if (applyBuff)
        {
            buff.applyBuff(players, buffToApply);
        }
        messageTo(self, "buffPlayers", null, 8.0f, false);
        return SCRIPT_CONTINUE;
    }
    public int handleDeleteSelf(obj_id self, dictionary params) throws InterruptedException
    {
        trial.cleanupObject(self);
        return SCRIPT_CONTINUE;
    }
}
