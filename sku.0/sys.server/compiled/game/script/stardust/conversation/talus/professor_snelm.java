package script.stardust.conversation.talus;

import script.*;
import script.library.*;

public class professor_snelm extends base_script
{
    public professor_snelm()
    {
    }
    public static String c_stringFile = "conversation/professor_snelm";
    public boolean professor_snelm_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean professor_snelm_condition_hasTalusIndex(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasCompletedCollection(player, "talus_creature_index");
    }

    public void professor_snelm_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void professor_snelm_action_grantQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_professor_snelm");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int professor_snelm_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "trade");

            utils.setScriptVar(player, "conversation.professor_snelm_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_battle"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_battle");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "creature_battle");

            utils.setScriptVar(player, "conversation.professor_snelm_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_creature_index"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_index");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "creature_index");

            utils.setScriptVar(player, "conversation.professor_snelm_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int professor_snelm_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("trade"))
        {
            if (professor_snelm_condition_hasTalusIndex (player, npc))
            {
                final string_id message = new string_id(c_stringFile, "fence");
                professor_snelm_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.professor_snelm_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_need_to_complete_talus_index");

                utils.removeScriptVar(player, "conversation.professor_snelm_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int professor_snelm_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("creature_battle"))
        {
            if (professor_snelm_condition_hasTalusIndex (player, npc))
            {
                final string_id message = new string_id(c_stringFile, "lets_battle");
                //professor_snelm_action_grantQuest(player, npc);

                utils.removeScriptVar(player, "conversation.professor_snelm_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_need_to_complete_talus_index");

                utils.removeScriptVar(player, "conversation.professor_snelm_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int professor_snelm_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("creature_index"))
        {
            if (professor_snelm_condition_hasTalusIndex (player, npc))
            {
                final string_id message = new string_id(c_stringFile, "congratulations_completed_talus_index");
                grantSkill(player, "stardust_bm_talus");

                utils.removeScriptVar(player, "conversation.professor_snelm_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_need_to_complete_talus_index_or_snoak");

                utils.removeScriptVar(player, "conversation.professor_snelm_conversation.branchId");
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
        setName(self, "Professor Sn'Elm (Beast Researcher)");

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

        if (professor_snelm_condition__defaultCondition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 4;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_battle");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_creature_index");

            utils.setScriptVar(player, "conversation.professor_snelm_conversation.branchId", 1);

            npcStartConversation(player, npc, "professor_snelm_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks curiously*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("professor_snelm_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.professor_snelm_conversation.branchId");

        if (branchId == 1 && professor_snelm_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && professor_snelm_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && professor_snelm_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && professor_snelm_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.professor_snelm_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
