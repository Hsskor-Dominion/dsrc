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
            if (utils.hasScriptVar(player, "jedi.holcron_used"))
            {
            	sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish"));
		setSkillTemplate(player, "force_sensitive_1a");
		grantSkill(player, "force_sensitive");
		grantSkill(player, "class_forcesensitive_phase1");
		grantSkill(player, "class_forcesensitive_phase1_novice");
		grantSkill(player, "class_forcesensitive_phase1_02");
		grantSkill(player, "class_forcesensitive_phase1_03");
		grantSkill(player, "class_forcesensitive_phase1_04");
		grantSkill(player, "class_forcesensitive_phase1_05");
		grantSkill(player, "class_forcesensitive_phase1_master");
		grantSkill(player, "class_forcesensitive_phase2_02");
		grantSkill(player, "class_forcesensitive_phase2_03");
		grantSkill(player, "class_forcesensitive_phase2_04");
		grantSkill(player, "class_forcesensitive_phase2_05");
		grantSkill(player, "class_forcesensitive_phase2_master");
		xp.grant(player, "jedi", 5000);
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
                return SCRIPT_CONTINUE;
            }
            int max_force = getMaxForcePower(player);
            int current_force = getForcePower(player);
            if (max_force < 1)
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish"));
		setSkillTemplate(player, "force_sensitive_1a");
		grantSkill(player, "force_sensitive");
		grantSkill(player, "class_forcesensitive_phase1");
		grantSkill(player, "class_forcesensitive_phase1_novice");
		grantSkill(player, "class_forcesensitive_phase1_02");
		grantSkill(player, "class_forcesensitive_phase1_03");
		grantSkill(player, "class_forcesensitive_phase1_04");
		grantSkill(player, "class_forcesensitive_phase1_05");
		grantSkill(player, "class_forcesensitive_phase1_master");
		grantSkill(player, "class_forcesensitive_phase2_02");
		grantSkill(player, "class_forcesensitive_phase2_03");
		grantSkill(player, "class_forcesensitive_phase2_04");
		grantSkill(player, "class_forcesensitive_phase2_05");
		grantSkill(player, "class_forcesensitive_phase2_master");
		xp.grant(player, "jedi", 5000);
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
            if (current_force >= max_force)
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish"));
		setSkillTemplate(player, "force_sensitive_1a");
		grantSkill(player, "force_sensitive");
		grantSkill(player, "class_forcesensitive_phase1");
		grantSkill(player, "class_forcesensitive_phase1_novice");
		grantSkill(player, "class_forcesensitive_phase1_02");
		grantSkill(player, "class_forcesensitive_phase1_03");
		grantSkill(player, "class_forcesensitive_phase1_04");
		grantSkill(player, "class_forcesensitive_phase1_05");
		grantSkill(player, "class_forcesensitive_phase1_master");
		grantSkill(player, "class_forcesensitive_phase2_02");
		grantSkill(player, "class_forcesensitive_phase2_03");
		grantSkill(player, "class_forcesensitive_phase2_04");
		grantSkill(player, "class_forcesensitive_phase2_05");
		grantSkill(player, "class_forcesensitive_phase2_master");
		xp.grant(player, "jedi", 5000);
		setJediBountyValue(player, 20000);
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
	
                return SCRIPT_CONTINUE;
            }
            sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish"));
            location loc = getLocation(player);
            playClientEffectLoc(player, "clienteffect/pl_force_heal_self.cef", loc, 0);
            utils.setScriptVar(player, "jedi.holcron_used", 1);
            setForcePower(player, max_force);
	    xp.grant(player, "jedi", 5000);
            setObjVar(self, "intUsed", 1);
            destroyObject(self);
        }
        return SCRIPT_CONTINUE;
    }
}
