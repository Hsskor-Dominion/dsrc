package script.stardust.conversation.tatooine;

import script.library.*;
import script.library.factions;
import script.*;

public class bhguild_leader extends script.base_script
{
    public bhguild_leader()
    {
    }
    public static String c_stringFile = "conversation/bhguild_leader";
    public boolean bhguild_leader_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean bhguild_leader_condition_checkQ1(obj_id player, obj_id npc) throws InterruptedException
    {
        int quest1 = questGetQuestId("quest/bhguild_leader");
        return (questIsQuestActive(quest1, player));
    }
    public boolean bhguild_leader_condition_hasAnyQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        int quest1 = questGetQuestId("quest/bhguild_leader");
        return (questIsQuestActive(quest1, player));
    }
    public boolean bhguild_leader_condition_playerStartedQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "bhguild_leader");
    }
    public boolean bhguild_leader_condition_playerFinishedMainTask(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "bhguild_leader", "returntobhguild");
    }
    public boolean bhguild_leader_condition_isBHprof(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasSkill(player,"class_bountyhunter_phase1_novice");
    }
    public boolean bhguild_leader_condition_isBHguild(obj_id player, obj_id npc) throws InterruptedException
    {
        float bhFaction = factions.getFactionStanding(player, "underworld");
        if (bhFaction <= 1)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public void bhguild_leader_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void bhguild_leader_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "bh_reward");
    }
    public void bhguild_leader_action_grantQ1(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/bhguild_leader");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int bhguild_leader_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_underworld"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_seek_underworld");

            utils.setScriptVar(player, "conversation.bhguild_leader_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("player_join_guild"))
        {

            final string_id message = new string_id(c_stringFile, "npc_explain");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_confirm_join_guild");

            utils.setScriptVar(player, "conversation.bhguild_leader_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("player_leave"))
        {

            final string_id message = new string_id(c_stringFile, "npc_are_you_sure");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "confirm_leave_enclave");

            utils.setScriptVar(player, "conversation.bhguild_leader_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int bhguild_leader_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_seek_underworld"))
        {
            if (bhguild_leader_condition_isBHguild(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "fence_items");
                bhguild_leader_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.bhguild_leader_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_friend");

                utils.removeScriptVar(player, "conversation.bhguild_leader_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int bhguild_leader_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_confirm_join_guild"))
        {
            if (bhguild_leader_condition_isBHguild(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_affirm");
                grantSkill(player, "stardust_pvp");
                grantSkill(player, "social_language_hutt_speak");
                grantSkill(player, "social_language_hutt_comprehend");

                utils.removeScriptVar(player, "conversation.bhguild_leader_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (bhguild_leader_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_deny");

                utils.removeScriptVar(player, "conversation.bhguild_leader_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int bhguild_leader_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("confirm_leave_enclave"))
        {
            final string_id message = new string_id(c_stringFile, "npc_remove_player");
            revokeSkill(player, "stardust_pvp");

            utils.removeScriptVar(player, "conversation.bhguild_leader_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setName(self, "Grissk Karrga (Bounty Hunter's Guild Leader)");

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

        if (bhguild_leader_condition__defaultCondition(npc, player))
        {
            doAnimationAction(npc, "pose_proudly");
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_underworld");
            responses[responseIndex++] = new string_id(c_stringFile, "player_join_guild");
            responses[responseIndex++] = new string_id(c_stringFile, "player_leave");

            utils.setScriptVar(player, "conversation.bhguild_leader_conversation.branchId", 1);

            npcStartConversation(player, npc, "bhguild_leader_conversation", message, responses);
            bhguild_leader_action_signalReward(player, npc);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in Mando'a*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("bhguild_leader_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.bhguild_leader_conversation.branchId");

        if (branchId == 1 && bhguild_leader_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && bhguild_leader_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && bhguild_leader_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && bhguild_leader_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.bhguild_leader_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
