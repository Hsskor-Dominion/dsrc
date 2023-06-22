package script.systems.jedi;

import script.library.*;
import script.library.utils;
import script.library.xp;
import script.*;

import java.util.Enumeration;

public class sith_holocron3 extends script.base_script
{
    public sith_holocron3()
    {
    }
    public boolean isMandoReady(obj_id player, obj_id npc) throws InterruptedException
    {
        return (getLevel(player) >= 90);
    }
    public boolean isMandoExplore(obj_id player, obj_id npc) throws InterruptedException
    {
        return ((badge.hasBadge(player, "bdg_deathtrooper_undead_rancor") || badge.hasBadge(player, "bdg_kash_wookiee_rage") || badge.hasBadge(player, "bdg_must_victory_army") || badge.hasBadge(player, "bdg_kill_deathwatch_overlord") || hasCompletedCollectionSlot(player, "col_ig88_factory_01") || hasCompletedCollectionSlot(player, "col_tusken_king_01")) && badge.hasBadge(player, "count_50"));
    }
    public void holocron_action_grantQuestRevan(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_revan");
        groundquests.grantQuest(questId, player, npc, true);
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
            if (!isMandoReady(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_level_mando"));
                return SCRIPT_OVERRIDE;
            }
            if (!isMandoExplore(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_explore_mando"));
                factions.goOvertWithDelay(player, 0.0f);
            }
            if (isMandoExplore(player, self))
            {
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_force_replenish_mando"));
                setSkillTemplate(player, "force_sensitive_1a");
                grantSkill(player, "force_sensitive");
                grantSkill(player, "class_forcesensitive_phase1");
                grantSkill(player, "class_forcesensitive_phase1_novice");
                grantSkill(player, "force_sensitive_heightened_senses_persuasion_04");
                xp.grant(player, "jedi", 100);
                factions.addFactionStanding(player, "sith_shadow", 50.0f);
                factions.goOvertWithDelay(player, 0.0f);
                holocron_action_grantQuestRevan(player, self);
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
                sendSystemMessage(player, new string_id("jedi_spam", "holocron_no_effect"));
            }
        }
        return SCRIPT_CONTINUE;
    }
}
