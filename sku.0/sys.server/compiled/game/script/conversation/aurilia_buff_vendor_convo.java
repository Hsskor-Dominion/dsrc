package script.conversation;

import script.*;
import script.library.*;


public class aurilia_buff_vendor_convo extends script.conversation.base.conversation_base
{
    public String conversation = "conversation.aurilia_buff_vendor_convo";
    public String c_stringFile = "conversation/aurilia_buff_vendor_convo";

    private void aurilia_buff_vendor_convo_action_showTokenVendorUI(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public boolean aurilia_buff_vendor_convo_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }


    public int aurilia_buff_vendor_convo_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade")) {
            handleBranch(player, "npc_consider_trade", new String[] { "force_trade" }, 2);
            return SCRIPT_CONTINUE;
        }

        else if (response.equals("seek_jedi")) {
            // Offer both political and space options
            handleBranch(player, "npc_you_seek_jedi",
                    new String[] { "seek_jedi_meditation", "seek_jedi_political", "seek_jedi_space" },
                    3
            );
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

    private void handleBranch(obj_id player, String messageKey, String[] responseKeys, int branchId) throws InterruptedException {
        final string_id message = new string_id(c_stringFile, messageKey);

        string_id[] responses = new string_id[responseKeys.length];
        for (int i = 0; i < responseKeys.length; i++) {
            responses[i] = new string_id(c_stringFile, responseKeys[i]);
        }

        utils.setScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId", branchId);

        npcSpeak(player, message);
        npcSetConversationResponses(player, responses);
    }
    public int aurilia_buff_vendor_convo_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("force_trade"))
        {
                final string_id message = new string_id(c_stringFile, "npc_offer_trade");
                aurilia_buff_vendor_convo_action_showTokenVendorUI(player, npc);

                utils.removeScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int aurilia_buff_vendor_convo_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        int required = 20000;


        // MEDITATE
        if (response.equals("seek_jedi_meditation"))
        {
            int current = xp.getExperiencePoints(player, "jedi");
            sendSystemMessageTestingOnly(player, "(jedi xp) = " + current);

            final string_id message = new string_id(c_stringFile, "meditation_training_offered");
            groundquests.grantQuest(player, "stardust_jedi_keeper");

            utils.removeScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }

        // POLITICAL → are you sure?
        if (response.equals("seek_jedi_political"))
        {
            int current = xp.getExperiencePoints(player, "political");
            sendSystemMessageTestingOnly(player, "(political xp) = " + current);

            if (current < required)
            {
                npcEndConversationWithMessage(player, new string_id(c_stringFile, "npc_you_are_not_ready"));
                utils.removeScriptVarTree(player, "conversation.aurilia_buff_vendor_convo_conversation");
                return SCRIPT_CONTINUE;
            }

            utils.setScriptVar(player, "jedi_xp_exchange_type", "political");
            handleBranch(player, "npc_confirm_exchange", new String[] { "confirm_yes", "confirm_no" }, 4);
            return SCRIPT_CONTINUE;
        }

        // SPACE → are you sure?
        if (response.equals("seek_jedi_space"))
        {
            int current = xp.getExperiencePoints(player, "space_xp");
            sendSystemMessageTestingOnly(player, "(space xp) = " + current);

            if (current < required)
            {
                npcEndConversationWithMessage(player, new string_id(c_stringFile, "npc_you_are_not_ready"));
                utils.removeScriptVarTree(player, "conversation.aurilia_buff_vendor_convo_conversation");
                return SCRIPT_CONTINUE;
            }

            utils.setScriptVar(player, "jedi_xp_exchange_type", "space");
            handleBranch(player, "npc_confirm_exchange", new String[] { "confirm_yes", "confirm_no" }, 4);
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

    public int aurilia_buff_vendor_convo_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        String type = utils.getStringScriptVar(player, "jedi_xp_exchange_type");
        int required = 20000;

        if (response.equals("confirm_no"))
        {
            final string_id message = new string_id(c_stringFile, "npc_exchange_cancel");
            utils.removeScriptVarTree(player, "conversation.aurilia_buff_vendor_convo_conversation");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }

        if (response.equals("confirm_yes"))
        {
            if (type.equals("political"))
            {
                int current = getExperiencePoints(player, "political");
                if (current >= required)
                {
                    grantExperiencePoints(player, "jedi", 1000);
                    grantExperiencePoints(player, "political", -required);

                    final string_id message = new string_id(c_stringFile, "npc_feel_the_force_experience_political");
                    npcEndConversationWithMessage(player, message);
                }
            }
            else if (type.equals("space"))
            {
                int current = getExperiencePoints(player, "space_xp");
                if (current >= required)
                {
                    grantExperiencePoints(player, "jedi", 1000);
                    grantExperiencePoints(player, "space_xp", -required);

                    final string_id message = new string_id(c_stringFile, "npc_feel_the_force_experience_space");
                    npcEndConversationWithMessage(player, message);
                }
            }

            utils.removeScriptVarTree(player, "conversation.aurilia_buff_vendor_convo_conversation");
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
        setName(self, "Paemos (Village Elder)");

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

        if (aurilia_buff_vendor_convo_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi");

            utils.setScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId", 1);

            npcStartConversation(player, npc, "aurilia_buff_vendor_convo_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in riddles*");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("aurilia_buff_vendor_convo_conversation"))
        {
            return SCRIPT_CONTINUE;
        }
        final int branchId = utils.getIntScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");

        if (branchId == 1 && aurilia_buff_vendor_convo_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && aurilia_buff_vendor_convo_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && aurilia_buff_vendor_convo_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && aurilia_buff_vendor_convo_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
