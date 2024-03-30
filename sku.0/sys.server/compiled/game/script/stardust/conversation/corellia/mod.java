package script.stardust.conversation.corellia;

import script.library.*;
import script.library.factions;
import script.*;

public class mod extends script.base_script
{
    public mod()
    {
    }
    public static String c_stringFile = "conversation/mod";
    public boolean mod_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean mod_condition_playerCompletedQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_mod");
    }
    public boolean modFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float swoopFaction = factions.getFactionStanding(player, "swoop");
        if (swoopFaction >= 50)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public boolean mod_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public void mod_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void mod_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "mod_reward");
    }
    public void mod_action_grantQuest1(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_mod");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int mod_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "trade");

            utils.setScriptVar(player, "conversation.mod_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_information"))
        {

            final string_id message = new string_id(c_stringFile, "npc_info");

            utils.removeScriptVar(player, "conversation.mod_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_quest"))
        {

            final string_id message = new string_id(c_stringFile, "s_257");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "s_259");

            utils.setScriptVar(player, "conversation.mod_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int mod_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("trade"))
        {
            if (modFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "fence");
                mod_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.mod_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_friend");

                utils.removeScriptVar(player, "conversation.mod_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int mod_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_259"))
        {
            if (mod_condition_playerCompletedQuest(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_swoop_pointer");

                utils.removeScriptVar(player, "conversation.mod_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (mod_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_quest");
                mod_action_grantQuest1(player, npc);

                utils.removeScriptVar(player, "conversation.mod_conversation.branchId");
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

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setName(self, "Skadossk (a Mod Swooper)");

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

        faceTo(npc, player);

        if (mod_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_information");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_quest");

            utils.setScriptVar(player, "conversation.mod_conversation.branchId", 1);

            npcStartConversation(player, npc, "mod_conversation", message, responses);
            mod_action_signalReward(player, npc);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Stares*");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("mod_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.mod_conversation.branchId");

        if (branchId == 1 && mod_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && mod_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && mod_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.mod_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
