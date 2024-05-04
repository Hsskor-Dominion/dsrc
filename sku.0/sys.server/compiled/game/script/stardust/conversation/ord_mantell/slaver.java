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
    public void slaver_action_leaveStation1(obj_id player, obj_id npc) throws InterruptedException
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
        if (response.equals("seek_else"))
        {

            final string_id message = new string_id(c_stringFile, "npc_explain_else");
            final int numberOfResponses = 6;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_tatooine_pilot");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_naboo");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_corellia");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_lok");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_dathomir");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_kashyyyk");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 4);

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
    public int slaver_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_tatooine_pilot"))
        {

            final string_id message = new string_id(c_stringFile, "npc_offer_tatooine_pilot");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_tatooine_bestine");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_tatooine_espa");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 5);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("player_accept_naboo"))
        {

            final string_id message = new string_id(c_stringFile, "npc_offer_naboo");
            final int numberOfResponses = 4;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_naboo_space_rsf");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_naboo_ground_rsf");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_naboo_space_imperial");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_naboo_space_rebel");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 6);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("player_accept_corellia"))
        {

            final string_id message = new string_id(c_stringFile, "npc_offer_corellia");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_corellia_ground_academy");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_corellia_coronet_fee");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 7);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("player_accept_lok"))
        {

            final string_id message = new string_id(c_stringFile, "npc_offer_lok");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_lok2");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 10);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("player_accept_dathomir"))
        {

            final string_id message = new string_id(c_stringFile, "npc_offer_dathomir");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_dathomir2");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 8);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("player_accept_kashyyyk"))
        {

            final string_id message = new string_id(c_stringFile, "npc_offer_kashyyyk");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_wookiee");
            responses[responseIndex++] = new string_id(c_stringFile, "player_accept_trandoshan");

            utils.setScriptVar(player, "conversation.slaver_conversation.branchId", 9);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int slaver_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_tatooine_bestine"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_bestine", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("player_accept_tatooine_espa"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_espa", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int slaver_handleBranch6(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_naboo_ground_rsf"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_kaadara", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("player_accept_naboo_space_rsf"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_theed", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("player_accept_naboo_space_imperial"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_naboo_imperial", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("player_accept_naboo_space_rebel"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_moenia", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int slaver_handleBranch7(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_corellia_ground_academy"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_republic_academy", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("player_accept_corellia_coronet_fee"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_coronet", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int slaver_handleBranch8(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_dathomir2"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_dathomir", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int slaver_handleBranch9(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_wookiee"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_wookiee", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("player_accept_trandoshan"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_trandoshan", 1);
                slaver_action_leaveStation1(player, npc);

                utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int slaver_handleBranch10(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_accept_lok2"))
        {
            if (slaver_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "travel");
                setObjVar(player, "stardust_lok", 1);
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
            final int numberOfResponses = 4;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_tatooine_ent");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_tatooine_farmer");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_else");

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
        else if (branchId == 4 && slaver_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 5 && slaver_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 6 && slaver_handleBranch6(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 7 && slaver_handleBranch7(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 8 && slaver_handleBranch8(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 9 && slaver_handleBranch9(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 10 && slaver_handleBranch10(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.slaver_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
