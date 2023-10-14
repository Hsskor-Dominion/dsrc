package script.systems.jedi;

import script.library.*;
import script.library.utils;
import script.library.xp;
import script.*;

import java.util.Enumeration;

public class sith_holocron extends script.base_script
{
    public sith_holocron()
    {
    }

    public boolean isSithReady(obj_id player, obj_id npc) throws InterruptedException
    {
        return (getLevel(player) >= 90);
    }

    public boolean isSithExplore(obj_id player, obj_id npc) throws InterruptedException
    {
        return ((badge.hasBadge(player, "bdg_col_jedi_npc_kill") || badge.hasBadge(player, "bdg_must_obiwan_story_bad") || badge.hasBadge(player, "bdg_ground_dwartii_statue_crafting") ||badge.hasBadge(player, "bdg_kill_axkva_min")) && badge.hasBadge(player, "count_50"));
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

            if (!isSithReady(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_level_sith"));
                return SCRIPT_OVERRIDE;
            }

            // Damage the holocron by X hitpoints
            int damageAmount = rand(10, 50);
            damageItem(self, damageAmount);

            if (!isSithExplore(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_explore_sith"));
                factions.goOvertWithDelay(player, 0.0f);
            }

            if (isSithExplore(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish_sith"));
                setSkillTemplate(player, "force_sensitive_1a");
                grantSkill(player, "force_sensitive");
                grantSkill(player, "class_forcesensitive_phase1");
                grantSkill(player, "class_forcesensitive_phase1_novice");
                grantSkill(player, "force_sensitive_heightened_senses_persuasion_04");
                xp.grant(player, "jedi", 100);
                factions.addFactionStanding(player, "sith_shadow", 50.0f);
                factions.goOvertWithDelay(player, 0.0f);
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
            else
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_explore_sith"));
            }
        }
        return SCRIPT_CONTINUE;
    }

    private void damageItem(obj_id item, int amount) throws InterruptedException
    {
        int curHp = getHitpoints(item);
        int newHp = curHp - amount;

        if (newHp <= 0)
        {
            // Item is destroyed
            destroyObject(item);
        }
        else
        {
            // Update item hitpoints
            setMaxHitpoints(item, 1); // Set max hitpoints to 1 temporarily
            setHitpoints(item, newHp);
            setMaxHitpoints(item, newHp + 1); // Set max hitpoints to new value
        }
    }
}