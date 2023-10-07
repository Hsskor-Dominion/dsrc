package script.stardust.conversation.corellia;

import script.library.factions;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.*;
import script.*;

public class reclamations extends script.base_script
{
    public reclamations()
    {
    }
    public static final String c_stringFile = "conversation/reclamations";
    public static final String STF_FILE = "stardust/quest";
    public boolean reclamations_defaultCondition()
    {
        return true;
    }
    public boolean reclamations_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public void reclamations_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        final dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public int reclamations_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "force_trade");

            utils.setScriptVar(player, "conversation.reclamations_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            setState(npc, STATE_GLOWING_JEDI, false);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_reclamations_info"))
        {

            final string_id message = new string_id(c_stringFile, "npc_explain");

            utils.removeScriptVar(player, "conversation.reclamations_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_reclamations_list"))
        {

            final string_id message = new string_id(c_stringFile, "npc_list_reclamations");
            final int numberOfResponses = 5;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_corellia");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_tatooine");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_naboo");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_lok");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_ord_mantell");

            utils.setScriptVar(player, "conversation.reclamations_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int reclamations_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("force_trade"))
        {
                final string_id message = new string_id(c_stringFile, "npc_offer_trade");
                reclamations_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.reclamations_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int reclamations_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_corellia"))
        {
                final string_id message = new string_id(c_stringFile, "npc_point_corellia");

                utils.removeScriptVar(player, "conversation.reclamations_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
        }
        if (response.equals("seek_tatooine"))
        {
            final string_id message = new string_id(c_stringFile, "npc_point_tatooine");

            utils.removeScriptVar(player, "conversation.reclamations_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("seek_naboo"))
        {
            final string_id message = new string_id(c_stringFile, "npc_point_naboo");

            utils.removeScriptVar(player, "conversation.reclamations_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("seek_lok"))
        {
            final string_id message = new string_id(c_stringFile, "npc_point_lok");

            utils.removeScriptVar(player, "conversation.reclamations_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("seek_ord_mantell"))
        {
            final string_id message = new string_id(c_stringFile, "npc_point_ord_mantell");

            utils.removeScriptVar(player, "conversation.reclamations_conversation.branchId");
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
        setName(self, "Aph (Republic Reclamations)");

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

        if (reclamations_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_reclamations_info");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_reclamations_list");

            utils.setScriptVar(player, "conversation.reclamations_conversation.branchId", 1);

            npcStartConversation(player, npc, "reclamations_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in riddles*");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("reclamations_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.reclamations_conversation.branchId");

        if (branchId == 1 && reclamations_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && reclamations_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && reclamations_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.reclamations_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}