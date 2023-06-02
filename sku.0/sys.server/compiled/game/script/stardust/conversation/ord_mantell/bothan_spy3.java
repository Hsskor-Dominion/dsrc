package script.stardust.conversation.ord_mantell;

import script.library.*;
import script.library.factions;
import script.*;

public class bothan_spy3 extends script.base_script
{
    public bothan_spy3()
    {
    }
    public static String c_stringFile = "conversation/bothan_spy3";
    public boolean bothan_spy3_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean bothan_spy3_condition_isSpyProf(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasSkill(player,"class_spy_phase1_novice");
    }
    public boolean bothan_spy3_condition_isSpynet(obj_id player, obj_id npc) throws InterruptedException
    {
        float spyFaction = factions.getFactionStanding(player, "underworld");
        if (spyFaction >= -1)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public void bothan_spy3_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public int bothan_spy3_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_underworld"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_seek_underworld");

            utils.setScriptVar(player, "conversation.bothan_spy3_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("player_join_spies"))
        {

            final string_id message = new string_id(c_stringFile, "npc_explain");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_confirm_join_spies");

            utils.setScriptVar(player, "conversation.bothan_spy3_conversation.branchId", 3);

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

            utils.setScriptVar(player, "conversation.bothan_spy3_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int bothan_spy3_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_seek_underworld"))
        {
            if (bothan_spy3_condition_isSpyProf(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "fence_items");
                bothan_spy3_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.bothan_spy3_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_professional");

                utils.removeScriptVar(player, "conversation.bothan_spy3_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int bothan_spy3_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_confirm_join_spies"))
        {
            if (bothan_spy3_condition_isSpynet(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_affirm");
                grantSkill(player, "stardust_spy");
                grantSkill(player, "social_language_hutt_speak");
                grantSkill(player, "social_language_hutt_comprehend");

                utils.removeScriptVar(player, "conversation.bothan_spy3_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (bothan_spy3_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_deny");

                utils.removeScriptVar(player, "conversation.bothan_spy3_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int bothan_spy3_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("confirm_leave_enclave"))
        {
            final string_id message = new string_id(c_stringFile, "npc_remove_player");
            revokeSkill(player, "stardust_spy");

            utils.removeScriptVar(player, "conversation.bothan_spy3_conversation.branchId");
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
        setName(self, "Sus (a Spynet Operative)");

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

        if (bothan_spy3_condition__defaultCondition(npc, player))
        {
            doAnimationAction(npc, "pose_proudly");
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "player_underworld");
            responses[responseIndex++] = new string_id(c_stringFile, "player_join_spies");
            responses[responseIndex++] = new string_id(c_stringFile, "player_leave");

            utils.setScriptVar(player, "conversation.bothan_spy3_conversation.branchId", 1);

            npcStartConversation(player, npc, "bothan_spy3_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Stares*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("bothan_spy3_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.bothan_spy3_conversation.branchId");

        if (branchId == 1 && bothan_spy3_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && bothan_spy3_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && bothan_spy3_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && bothan_spy3_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.bothan_spy3_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
