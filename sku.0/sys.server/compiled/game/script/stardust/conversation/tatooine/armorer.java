package script.stardust.conversation.tatooine;

import script.library.*;
import script.library.factions;
import script.*;

public class armorer extends script.base_script
{
    public armorer()
    {
    }
    public static String c_stringFile = "conversation/armorer";
    public boolean armorer_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean armorer_condition_playerCompletedCreed(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_mando_creed");
    }
    public boolean armorer_condition_playerCompletedCrest(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_mando_crest");
    }
    public boolean armorerFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float bhFaction = factions.getFactionStanding(player, "death_watch");
        if (bhFaction >= 2500)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public boolean armorer_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public void armorer_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void armorer_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "armorer_reward");
    }
    public void armorer_action_grantQuest1(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_mando_creed");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void armorer_action_grantQuest2(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_mando_crest");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void armorer_action_grantQuest3(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_mando_craft");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int armorer_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "trade");

            utils.setScriptVar(player, "conversation.armorer_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_mandalore"))
        {

            final string_id message = new string_id(c_stringFile, "s_273");

            utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_the_way"))
        {

            final string_id message = new string_id(c_stringFile, "s_257");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "s_259");
            responses[responseIndex++] = new string_id(c_stringFile, "mando_forge");

            utils.setScriptVar(player, "conversation.armorer_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_to_leave_enclave"))
        {

            final string_id message = new string_id(c_stringFile, "npc_are_you_sure");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "confirm_leave_enclave");

            utils.setScriptVar(player, "conversation.armorer_conversation.branchId", 5);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int armorer_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("trade"))
        {
            if (armorerFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "fence");
                armorer_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_friend");

                utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int armorer_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_mandalore"))
        {
            if (armorer_condition_playerCompletedCreed(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "for_mandalore");

                utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (armorer_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "s_273");

                utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int armorer_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_259"))
        {
            if (armorer_condition_playerCompletedCreed(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "earn_crest");
                armorer_action_grantQuest2(player, npc);

                utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (armorer_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "s_261");
                armorer_action_grantQuest1(player, npc);

                utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("mando_forge"))
        {
            if (armorer_condition_playerCompletedCreed(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "earn_forge");
                armorer_action_grantQuest3(player, npc);

                utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (armorer_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "s_261");

                utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int armorer_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("confirm_leave_enclave"))
        {
            final string_id message = new string_id(c_stringFile, "npc_very_well");

            utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
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
        setName(self, "Death Watch Armorer");

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

        if (armorer_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 5;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_mandalore");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_the_way");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_to_leave_enclave");

            utils.setScriptVar(player, "conversation.armorer_conversation.branchId", 1);

            npcStartConversation(player, npc, "armorer_conversation", message, responses);
            armorer_action_signalReward(player, npc);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in Mando'a*");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("armorer_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.armorer_conversation.branchId");

        if (branchId == 1 && armorer_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && armorer_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && armorer_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && armorer_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 5 && armorer_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.armorer_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
