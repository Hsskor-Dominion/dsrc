package script.stardust.conversation.korriban;

import script.library.factions;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.*;
import script.*;

public class darth_sidious extends script.base_script
{
    public darth_sidious()
    {
    }
    public static final String c_stringFile = "conversation/darth_sidious";
    public static final String OBJ_VAR_BASE = "darth_sidious.";
    public static final String SITH_APPRENTICE = OBJ_VAR_BASE + "sith";
    public boolean darth_sidious_defaultCondition()
    {
        return true;
    }
    public boolean darth_sidious_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public boolean darth_sidious_sithFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float sithFaction = factions.getFactionStanding(player, "sith_shadow");
        return sithFaction >= 2500;
    }
    public boolean darth_sidious_phase1_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase1_novice");
    }
    public boolean darth_sidious_phase2_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase2_novice");
    }
    public boolean darth_sidious_phase3_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase3_novice");
    }
    public boolean darth_sidious_phase4_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase4_novice");
    }
    public boolean darth_sidious_credits_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return (money.hasFunds(player, money.MT_TOTAL, smuggler.TIER_4_GENERIC_PVP_FRONT_COST));
    }
    public boolean darth_sidious_hasObjVar_condition(obj_id npc, obj_id player)
    {
        return hasObjVar(player, SITH_APPRENTICE);
    }
    public boolean darth_sidious_sith_quest_condition_playerFinishedMainTask(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "stardust_darth_sidious", "talktodarth_sidious");
    }
    public void darth_sidious_sith_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "stardust_entertainer_darth_sidious_reward");
    }
    public void darth_sidious_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        final dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void darth_sidious_sith_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_sith_darth_sidious");
    }
    public void darth_sidious_bounty_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        money.requestPayment(player, npc, smuggler.TIER_4_GENERIC_PVP_FRONT_COST, "none", null, true);
        groundquests.requestGrantQuest(player, "quest/stardust_darth_sidious_arena", true);
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
    public int darth_sidious_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "force_trade");

            utils.setScriptVar(player, "conversation.darth_sidious_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_jedi"))
        {

            final string_id message = new string_id(c_stringFile, "npc_aggro");
            jedi.jediDeathExperienceLoss(player);
            force_rank.removeFromForceRankSystem(player, true);

            utils.removeScriptVar(player, "conversation.darth_sidious_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_balance"))
        {

            final string_id message = new string_id(c_stringFile, "npc_explain");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_balance2");

            utils.setScriptVar(player, "conversation.darth_sidious_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_sith"))
        {

            final string_id message = new string_id(c_stringFile, "npc_sith_whispers");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_sith2");

            utils.setScriptVar(player, "conversation.darth_sidious_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int darth_sidious_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("force_trade"))
        {
            if (darth_sidious_sithFriend_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_trade");
                darth_sidious_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.darth_sidious_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_friend");

                utils.removeScriptVar(player, "conversation.darth_sidious_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int darth_sidious_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_balance2"))
        {
            if (darth_sidious_phase1_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_mission");
                force_rank.addToForceRankSystem(player, force_rank.DARK_COUNCIL);


                utils.removeScriptVar(player, "conversation.darth_sidious_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_deny_mission");

                utils.removeScriptVar(player, "conversation.darth_sidious_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int darth_sidious_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_sith2"))
        {
            if (darth_sidious_phase1_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_sith_training");
                jedi_trials.initializeKnightTrials(player);

                utils.removeScriptVar(player, "conversation.darth_sidious_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_sith");

                utils.removeScriptVar(player, "conversation.darth_sidious_conversation.branchId");
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

        setName(self, "Spirit of Darth Sidious (Clone Experiment)");

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);

        setName(self, "Spirit of Darth Sidious (Clone Experiment)");

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

        if (darth_sidious_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 4;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_balance");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_sith");

            utils.setScriptVar(player, "conversation.darth_sidious_conversation.branchId", 1);

            npcStartConversation(player, npc, "darth_sidious_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in Sith*");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("darth_sidious_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.darth_sidious_conversation.branchId");

        if (branchId == 1 && darth_sidious_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && darth_sidious_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && darth_sidious_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && darth_sidious_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.darth_sidious_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
