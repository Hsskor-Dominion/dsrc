package script.stardust.conversation.ossus;

import script.library.factions;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.*;
import script.*;

public class keeper extends script.base_script
{
    public keeper()
    {
    }
    public static final String c_stringFile = "conversation/keeper";
    public static final String STF_FILE = "stardust/quest";
    public static final String OBJ_VAR_BASE = "keeper.";
    public boolean keeper_defaultCondition()
    {
        return true;
    }
    public boolean keeper_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public boolean keeper_chronicler_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "class_chronicles_master");
    }
    public boolean keeper_jediFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float jediFaction = factions.getFactionStanding(player, "fs_villager");
        return jediFaction >= 1000;
    }
    public boolean keeper_phase1_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase1_novice");
    }
    public boolean keeper_phase2_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase2_novice");
    }
    public boolean keeper_phase3_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase3_novice");
    }
    public boolean keeper_phase4_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase4_novice");
    }
    public boolean keeper_wanderer_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"force_rank");
    }
    public boolean keeper_jedi_quest_condition_playerFinishedMainTask(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "stardust_keeper", "talktokeeper");
    }
    public void keeper_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        final dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void keeper_jedi_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_jedi_keeper");
    }
    public int keeper_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "force_trade");

            utils.setScriptVar(player, "conversation.keeper_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            setState(npc, STATE_GLOWING_JEDI, false);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_sith"))
        {

            final string_id message = new string_id(c_stringFile, "npc_sith_whispers");

            utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
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

            utils.setScriptVar(player, "conversation.keeper_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_jedi"))
        {

            final string_id message = new string_id(c_stringFile, "npc_you_seek_jedi");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi2");

            utils.setScriptVar(player, "conversation.keeper_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_to_leave_order"))
        {

            final string_id message = new string_id(c_stringFile, "npc_are_you_sure");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "confirm_leave_order");

            utils.setScriptVar(player, "conversation.keeper_conversation.branchId", 5);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int keeper_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("force_trade"))
        {
            if (keeper_jediFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_trade");
                keeper_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_friend");

                utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int keeper_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_balance2"))
        {
            if (keeper_chronicler_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_mission");
                keeper_jedi_quest(player, npc);
                factions.goOvertWithDelay(player, 0.0f);
                obj_id[] objPlayers = getPlayerCreaturesInRange(player, 256.0f);
                if (objPlayers != null && objPlayers.length > 0)
                {
                    for (obj_id objPlayer : objPlayers) {
                        sendSystemMessage(objPlayer, new string_id(STF_FILE, "jedi_awakening"));
                    }
                }


                utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_deny_mission");

                utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int keeper_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_jedi2"))
        {
            if (keeper_wanderer_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_you_belong_to_an_order");

                utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (keeper_phase2_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_jedi_training");
                jedi_trials.initializeKnightTrials(player);

                utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_ready");

                utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int keeper_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("confirm_leave_order"))
        {
            final string_id message = new string_id(c_stringFile, "npc_very_well");
            force_rank.removeFromForceRankSystem(player, true);
            revokeSkill(player, "force_rank");
            revokeSkill(player, "force_rank_light");
            revokeSkill(player, "force_rank_light_novice");

            utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);

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

        if (keeper_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 5;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_balance");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_sith");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_to_leave_order");

            utils.setScriptVar(player, "conversation.keeper_conversation.branchId", 1);

            npcStartConversation(player, npc, "keeper_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in riddles*");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("keeper_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.keeper_conversation.branchId");

        if (branchId == 1 && keeper_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && keeper_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && keeper_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && keeper_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 5 && keeper_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.keeper_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
