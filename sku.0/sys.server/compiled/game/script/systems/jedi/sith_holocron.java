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

    public boolean isSithReady(obj_id player, obj_id self) throws InterruptedException
    {
        return (getLevel(player) >= 90);
    }
    public boolean phase3_condition(obj_id player, obj_id self)
    {
        return hasSkill(player,"class_forcesensitive_phase3_novice");
    }
    public void grantPhase3Quest(obj_id player, obj_id self) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_jedi_kill");
        groundquests.grantQuest(questId, player, self, true);
    }
    public boolean isSithExplore(obj_id player, obj_id self) throws InterruptedException
    {
        int explore_requirement = rand(1, 10);
        String explore = "";
        switch (explore_requirement)
        {
            case 1:
                explore = "warren_hero";
                break;
            case 2:
                explore = "bdg_col_jedi_npc_kill";
                break;
            case 3:
                explore = "bdg_must_obiwan_story_bad";
                break;
            case 4:
                explore = "bdg_ground_dwartii_statue_crafting";
                break;
            case 5:
                explore = "col_bdg_hero_tatooine";
                break;
            case 6:
                explore = "bdg_kash_arena_champ";
                break;
            case 7:
                explore = "bdg_kash_avatar_zssik";
                break;
            case 8:
                explore = "bdg_thm_park_jabba_badge";
                break;
            case 9:
                explore = "bdg_thm_park_imperial_badge";
                break;
            case 10:
                explore = "bdg_library_trivia";
                break;
        }
        return badge.hasBadge(player, explore) && badge.hasBadge(player, "count_50");
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
            int damageAmount = rand(5, 10);
            damageItem(self, damageAmount);

            if (!isSithExplore(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_explore_sith"));
                factions.goOvertWithDelay(player, 0.0f);
                return SCRIPT_OVERRIDE;
            }
            if (phase3_condition(player, self))
            {
                grantPhase3Quest(player, self);
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish_sith"));
                xp.grant(player, "jedi", 7500);
                factions.addFactionStanding(player, "sith_shadow", 50.0f);
                factions.goOvertWithDelay(player, 0.0f);
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
                return SCRIPT_OVERRIDE;
            }
            if (isSithExplore(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish_sith"));
                setSkillTemplate(player, "force_sensitive_1a");
                grantSkill(player, "force_sensitive");
                grantSkill(player, "class_forcesensitive_phase1");
                grantSkill(player, "class_forcesensitive_phase1_novice");
                grantSkill(player, "force_sensitive_heightened_senses_persuasion_04");
                xp.grant(player, "jedi", 5000);
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
                factions.goOvertWithDelay(player, 0.0f);
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