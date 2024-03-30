package script.stardust.conversation.ord_mantell;

import script.library.*;
import script.library.factions;
import script.*;

public class slaver extends script.base_script
{
    public slaver()
    {
    }
    public static String c_stringFile = "conversation/slaver";
    public boolean slaver_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean slaverComplete_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_slaver");
    }
    public void slaver_action_grantQuest1(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_slaver1");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void slaver_action_grantQuest2(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_slaver2");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void slaver_action_leaveStation1(obj_id player, obj_id npc) throws InterruptedException
    {
        string_id stfPrompt = new string_id("npe", "exit_station_prompt");
        string_id stfTitle = new string_id("npe", "exit_station");
        String prompt = utils.packStringId(stfPrompt);
        String title = utils.packStringId(stfTitle);
        int pid = sui.msgbox(player, player, prompt, sui.OK_CANCEL, title, 0, "handTransfer");
    }
    public void slaver_action_leaveStation2(obj_id player, obj_id npc) throws InterruptedException
    {
        string_id stfPrompt = new string_id("npe", "exit_station_prompt");
        string_id stfTitle = new string_id("npe", "exit_station");
        String prompt = utils.packStringId(stfPrompt);
        String title = utils.packStringId(stfTitle);
        int pid = sui.msgbox(player, player, prompt, sui.OK_CANCEL, title, 0, "handTransfer");
    }
    public int slaver_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_tatooine_ent"))
        {

            final string_id message = new string_id(c_stringFile, "npc_explain_tatooine");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_tatooine");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("seek_tatooine_farmer"))
        {

            final string_id message = new string_id(c_stringFile, "npc_explain_tatooine2");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_tatooine2");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int slaver_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_tatooine"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel_tatooine");
                setObjVar(player, "stardust_ent", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int slaver_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_tatooine2"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel_tatooine2");
                setObjVar(player, "stardust_farmer", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
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
        setName(self, "Nautto (a Trade Union Representative)");

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

        if (slaver_condition__defaultCondition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_tatooine_ent");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_tatooine_farmer");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 1);

            npcStartConversation(player, npc, "slaver_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks curiously*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("slaver_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.slaver_conversation.branchId");

        if (branchId == 1 && slaver_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && slaver_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && slaver_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
