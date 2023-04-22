package script.systems.jedi;

import script.library.*;
import script.library.utils;
import script.library.xp;
import script.*;

import java.util.Enumeration;

public class jedi_holocron extends script.base_script
{
    public jedi_holocron()
    {
    }
    public boolean isJediReady(obj_id player, obj_id npc) throws InterruptedException
    {
        return (getLevel(player) >= 90);
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (hasObjVar(self, "intUsed"))
        {
            return SCRIPT_CONTINUE;
        }
        menu_info_data mid = mi.getMenuItemByType(menu_info_types.EXAMINE);
        if (mid != null)
        {
            mid.setServerNotify(true);
        }
        mid = mi.getMenuItemByType(menu_info_types.ITEM_USE);
        if (mid != null)
        {
            mid.setServerNotify(true);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.ITEM_USE)
        {
            if (hasObjVar(self, "intUsed"))
            {
                return SCRIPT_CONTINUE;
            }
            int max_force = getMaxForcePower(player);
            int current_force = getForcePower(player);
            if (isJediReady(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish"));
		        setSkillTemplate(player, "force_sensitive_1a");
		        grantSkill(player, "force_sensitive");
		        grantSkill(player, "class_forcesensitive_phase1");
		        grantSkill(player, "class_forcesensitive_phase1_novice");
		        xp.grant(player, "jedi", 5000);
                jedi_trials.initializePadawanTrials(player);
                destroyObject(self);
            int mission_bounty = 25000;
            int current_bounty = 0;
            mission_bounty += rand(1, 2000);
            if (hasObjVar(player, "bounty.amount"))
            {
                current_bounty = getIntObjVar(player, "bounty.amount");
            }
            current_bounty += mission_bounty;
            setObjVar(player, "bounty.amount", current_bounty);
            setObjVar(player, "jedi.bounty", mission_bounty);
            setJediBountyValue(player, current_bounty);
            updateJediScriptData(player, "jedi", 1);
            }
        }
        return SCRIPT_CONTINUE;
    }
}
