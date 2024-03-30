package script.stardust.conversation.corellia;

import script.library.*;
import script.library.factions;
import script.*;

public class politician extends script.base_script
{
    public politician()
    {
    }
    public static String c_stringFile = "conversation/politician";
    public boolean politician_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean politician_condition_playerReadyPolitics(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_politics");
    }
    public boolean politicianMayor_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasSkill(player, "social_politician_master");
    }
    public boolean politicianAdmiral_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return (hasSkill(player, "pvp_imperial_airstrike_ability") || hasSkill(player, "pvp_rebel_airstrike_ability")) && hasSkill(player, "stardust_ace_of_aces");
    }
    public boolean politicianCitizen_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasSkill(player, "social_politician_novice");
    }
    public boolean politician_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public void politician_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void politician_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "politician_reward");
    }
    public void politician_action_grantQuest1(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_politics");
        groundquests.grantQuest(questId, player, npc, true);
    }

    public int politician_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_info"))
        {

            final string_id message = new string_id(c_stringFile, "npc_info_gcw");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "join_gcw");
            responses[responseIndex++] = new string_id(c_stringFile, "become_admiral");

            utils.setScriptVar(player, "conversation.politician_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_info2"))
        {

            final string_id message = new string_id(c_stringFile, "npc_info_govern");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "become_diplomat");
            responses[responseIndex++] = new string_id(c_stringFile, "become_senator");
            responses[responseIndex++] = new string_id(c_stringFile, "become_govern");

            utils.setScriptVar(player, "conversation.politician_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_info3"))
        {

            final string_id message = new string_id(c_stringFile, "npc_info_underworld");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "become_underworld");
            responses[responseIndex++] = new string_id(c_stringFile, "report_to_authorities");

            utils.setScriptVar(player, "conversation.politician_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_leave"))
        {

            final string_id message = new string_id(c_stringFile, "npc_are_you_sure");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "confirm_leave_politics");

            utils.setScriptVar(player, "conversation.politician_conversation.branchId", 5);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int politician_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("join_gcw"))
        {
            if (politicianMayor_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_point_moff");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_point_gcw");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("become_admiral"))
        {
            if (politicianAdmiral_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_grant_admiral");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);
                grantSkill(player, "stardust_admiral_imperial");
                grantSkill(player, "stardust_admiral_republic");
                grantSkill(player, "stardust_pvp");
                sendSystemMessage(player, new string_id("stardust/politics_rank", "stardust_admiral"));

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_point_gcw");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int politician_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("become_diplomat"))
        {
            if (politicianCitizen_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "diplomatic_mission");
                politician_action_grantQuest1(player, npc);

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (politician_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "not_citizen");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("become_senator"))
        {
            if (politicianMayor_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "new_senator_candidate");
                sendSystemMessage(player, new string_id("stardust/politics_rank", "stardust_senator_candidate"));
                grantSkill(player, "stardust_senator_candidate");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (politician_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "not_qualified2");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("become_govern"))
        {
            if (politicianMayor_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "new_gov_candidate");
                sendSystemMessage(player, new string_id("stardust/politics_rank", "stardust_gov"));
                grantSkill(player, "stardust_gov_candidate");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (politician_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "not_qualified");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int politician_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("become_underworld"))
        {
            if (politician_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_point_underworld");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("report_to_authorities"))
        {
            if (politician_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_is_authority");

                utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int politician_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("confirm_leave_politics"))
        {
            final string_id message = new string_id(c_stringFile, "npc_very_well");
            revokeSkill(player, "stardust_pvp");
            revokeSkill(player, "stardust_spy");
            revokeSkill(player, "faction_rank_mando_novice");
            revokeSkill(player, "stardust_admiral_imperial");
            revokeSkill(player, "stardust_admiral_republic");
            factions.addFactionStanding(player, "underworld", 10000.0f);
            factions.addFactionStanding(player, "underworld", -5000.0f);

            utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
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
        setName(self, "Cal Omas (a senate politician trainer)");

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

        if (politician_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 5;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_info");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_info2");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_info3");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_leave");

            utils.setScriptVar(player, "conversation.politician_conversation.branchId", 1);

            npcStartConversation(player, npc, "politician_conversation", message, responses);
            politician_action_signalReward(player, npc);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in doublespeak*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("politician_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.politician_conversation.branchId");

        if (branchId == 1 && politician_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && politician_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && politician_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && politician_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 5 && politician_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.politician_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
