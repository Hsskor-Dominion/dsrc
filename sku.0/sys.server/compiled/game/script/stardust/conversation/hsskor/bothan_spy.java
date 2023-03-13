package script.stardust.conversation.hsskor;

import script.library.factions;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.*;
import script.*;

public class bothan_spy extends script.base_script
{
    public bothan_spy()
    {
    }
    public static final String c_stringFile = "conversation/bothan_spy";
    public static final String OBJ_VAR_BASE = "deathstar.";
    public static final String DEATHSTAR_PLANS = OBJ_VAR_BASE + "plans";
    public boolean bothan_spy_defaultCondition()
    {
        return true;
    }
    public boolean bothan_spy_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public boolean bothan_spy_underworld_smuggler_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float underworldSmugglerFaction = factions.getFactionStanding(player, "underworld");
        return underworldSmugglerFaction >= 1000;
    }
    public boolean bothan_spy_underworld_hunter_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float underworldSmugglerFaction = factions.getFactionStanding(player, "underworld");
        return underworldSmugglerFaction <= -1000;
    }
    public boolean bothan_spy_hunter_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_bountyhunter_phase1_novice");
    }
    public boolean bothan_spy_smuggler_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_smuggler_phase1_novice");
    }
    public boolean bothan_spy_spy_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_spy_phase1_novice");
    }
    public boolean bothan_spy_credits_condition(obj_id player, obj_id npc) throws InterruptedException
    {
            return (money.hasFunds(player, money.MT_TOTAL, smuggler.TIER_4_GENERIC_PVP_FRONT_COST));
    }
    public boolean bothan_spy_hasObjVar_condition(obj_id npc, obj_id player)
    {
        return hasObjVar(player, DEATHSTAR_PLANS);
    }

    public boolean bothan_spy_steal_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        float sleightOfHand = getEnhancedSkillStatisticModifierUncapped(player, "precision");
        return sleightOfHand >= 100;
    }
    public void bothan_spy_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        final dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void bothan_spy_smuggler_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        money.requestPayment(player, npc, smuggler.TIER_4_GENERIC_PVP_FRONT_COST, "none", null, true);
        groundquests.requestGrantQuest(player, "quest/bothan_spy_smuggler", true);
        int mission_bounty = 10000;
        int current_bounty = 0;
        mission_bounty += rand(1, 2000);
        if (hasObjVar(player, "bounty.amount"))
        {
            current_bounty = getIntObjVar(player, "bounty.amount");
        }
        current_bounty += mission_bounty;
        setObjVar(player, "bounty.amount", current_bounty);
        setObjVar(player, "smuggler.bounty", mission_bounty);
        setJediBountyValue(player, current_bounty);
        updateJediScriptData(player, "smuggler", 1);
    }
    public int bothan_spy_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("bothan_spy_intro_spy"))
        {

            final string_id message = new string_id(c_stringFile, "bothan_spy_sus");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "bothan_spy_vs_spy");
            responses[responseIndex++] = new string_id(c_stringFile, "bothan_spy_fence");

            utils.setScriptVar(player, "conversation.bothan_spy_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("bothan_spy_intro_smuggler"))
        {

            final string_id message = new string_id(c_stringFile, "bothan_spy_is_curious");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "[steal]bothan_spy_smuggle");
            responses[responseIndex++] = new string_id(c_stringFile, "[persuasion]bothan_spy_smuggle");

            utils.setScriptVar(player, "conversation.bothan_spy_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("bothan_spy_intro_deathstar_plans"))
        {

            final string_id message = new string_id(c_stringFile, "bothan_spy_conspires");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "[bounty]bothan_spy_smuggle");
            responses[responseIndex++] = new string_id(c_stringFile, "[gossip]bothan_spy_deathstar_plans");

            utils.setScriptVar(player, "conversation.bothan_spy_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int bothan_spy_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("bothan_spy_vs_spy"))
        {
            if (bothan_spy_spy_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_aggro");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_you_are_no_spy");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("bothan_spy_fence"))
        {
            if (bothan_spy_underworld_smuggler_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_has_wares");

                bothan_spy_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_no_deal");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int bothan_spy_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("[steal]bothan_spy_smuggle"))
        {
            if (bothan_spy_steal_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_where_did_my_plans_go?");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_aggro2");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("[persuasion]bothan_spy_smuggle"))
        {
            if (bothan_spy_smuggler_condition(player, npc))
            {
                bothan_spy_smuggler_quest(player, npc);
                final string_id message = new string_id(c_stringFile, "bothan_spy_offers_smuggler_mission");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_finds_you_sus");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int bothan_spy_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("[bounty]bothan_spy_smuggle"))
        {
            if (bothan_spy_underworld_hunter_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_no_trouble");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (bothan_spy_hunter_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_get_lost_hunter");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_thinks_you_are_lost");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("[gossip]bothan_spy_deathstar_plans"))
        {
            if (bothan_spy_hasObjVar_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_talks_stardust");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy_changes_subject");

                utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);

        setName(self, "a mysterious figure");

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);

        setName(self, "a mysterious figure");

        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        final int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        menuInfoData.setServerNotify(false);

        return SCRIPT_CONTINUE;
    }

    public boolean npcStartConversation(obj_id player, obj_id npc, String convoName, string_id greetingId, prose_package greetingProse, string_id[] responses) throws InterruptedException
    {
        Object[] objects = new Object[responses.length];
        System.arraycopy(responses, 0, objects, 0, responses.length);
        return npcStartConversation(player, npc, convoName, greetingId, greetingProse, objects);
    }
    public int OnStartNpcConversation(obj_id npc, obj_id player) throws InterruptedException
    {
        if (ai_lib.isInCombat(npc) || ai_lib.isInCombat(player))
        {
            return SCRIPT_OVERRIDE;
        }

        // Since we can talk to the player, might as well face them.
        faceTo(npc, player);

        if (bothan_spy_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "bothan_spy_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "bothan_spy_intro_spy");
            responses[responseIndex++] = new string_id(c_stringFile, "bothan_spy_intro_smuggler");
            responses[responseIndex++] = new string_id(c_stringFile, "bothan_spy_intro_deathstar_plans");

            utils.setScriptVar(player, "conversation.bothan_spy_conversation.branchId", 1);

            npcStartConversation(player, npc, "bothan_spy_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Go away");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("bothan_spy_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.bothan_spy_conversation.branchId");

        if (branchId == 1 && bothan_spy_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && bothan_spy_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && bothan_spy_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && bothan_spy_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.bothan_spy_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}