package script.conversation;

import script.library.*;
import script.*;

public class gcw_rebel_space_destroy extends script.base_script
{
    public gcw_rebel_space_destroy()
    {
    }
    public static String c_stringFile = "conversation/gcw_rebel_space_destroy";
    public boolean gcw_rebel_space_destroy_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean gcw_rebel_space_destroy_condition_completedKill1(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "gcw_rebel_space", "returnGrollo");
    }
    public boolean gcw_rebel_space_destroy_condition_killActive1(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "gcw_rebel_space");
    }
    public boolean gcw_rebel_space_destroy_condition_inPhase1(obj_id player, obj_id npc) throws InterruptedException
    {
        if (factions.isRebel(player))
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public boolean gcw_rebel_space_destroy_condition_ifFailedOne(obj_id player, obj_id npc) throws InterruptedException
    {
        if (space_quest.hasFailedQuestRecursive(player, "destroy", "master_rebel_1") || space_quest.hasAbortedQuestRecursive(player, "destroy", "master_rebel_1"))
        {
            return true;
        }
        return false;
    }
    public boolean gcw_rebel_space_destroy_condition_isImperialPlayer(obj_id player, obj_id npc) throws InterruptedException
    {
        return factions.isImperial(player);
    }
    public void gcw_rebel_space_destroy_action_givekill1(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.clearQuest(player, "gcw_rebel_space");
        groundquests.grantQuest(player, "gcw_rebel_space");
    }
    public void gcw_rebel_space_destroy_action_signalDone(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "returnedGrollo");
    }
    public int gcw_rebel_space_destroy_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_26"))
        {
            doAnimationAction(player, "salute2");
            gcw_rebel_space_destroy_action_givekill1(player, npc);
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                string_id message = new string_id(c_stringFile, "s_29");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_35"))
        {
            doAnimationAction(player, "shake_head_no");
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                doAnimationAction(player, "salute2");
                string_id message = new string_id(c_stringFile, "s_43");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_rebel_space_destroy_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_30"))
        {
            doAnimationAction(player, "salute2");
            gcw_rebel_space_destroy_action_givekill1(player, npc);
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                string_id message = new string_id(c_stringFile, "s_32");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_34"))
        {
            doAnimationAction(player, "shake_head_no");
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                doAnimationAction(player, "salute2");
                string_id message = new string_id(c_stringFile, "s_36");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_rebel_space_destroy_handleBranch8(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_38"))
        {
            doAnimationAction(player, "salute2");
            gcw_rebel_space_destroy_action_givekill1(player, npc);
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                string_id message = new string_id(c_stringFile, "s_40");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_42"))
        {
            doAnimationAction(player, "shake_head_no");
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                doAnimationAction(player, "salute2");
                string_id message = new string_id(c_stringFile, "s_44");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_rebel_space_destroy_handleBranch11(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_30"))
        {
            doAnimationAction(player, "salute2");
            gcw_rebel_space_destroy_action_givekill1(player, npc);
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                string_id message = new string_id(c_stringFile, "s_32");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_34"))
        {
            doAnimationAction(player, "shake_head_no");
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                doAnimationAction(player, "salute2");
                string_id message = new string_id(c_stringFile, "s_36");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_rebel_space_destroy_handleBranch14(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_26"))
        {
            doAnimationAction(player, "salute2");
            gcw_rebel_space_destroy_action_givekill1(player, npc);
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                string_id message = new string_id(c_stringFile, "s_29");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_35"))
        {
            doAnimationAction(player, "shake_head_no");
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                doAnimationAction(player, "salute2");
                string_id message = new string_id(c_stringFile, "s_43");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if ((!isTangible(self)) || (isPlayer(self)))
        {
            detachScript(self, "conversation.gcw_rebel_space_destroy");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setName(self, "Shara Bey (Green Squadron)");
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        menuInfoData.setServerNotify(false);
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnIncapacitated(obj_id self, obj_id killer) throws InterruptedException
    {
        clearCondition(self, CONDITION_CONVERSABLE);
        detachScript(self, "conversation.gcw_rebel_space_destroy");
        return SCRIPT_CONTINUE;
    }
    public boolean npcStartConversation(obj_id player, obj_id npc, String convoName, string_id greetingId, prose_package greetingProse, string_id[] responses) throws InterruptedException
    {
        Object[] objects = new Object[responses.length];
        System.arraycopy(responses, 0, objects, 0, responses.length);
        return npcStartConversation(player, npc, convoName, greetingId, greetingProse, objects);
    }
    public int OnStartNpcConversation(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id npc = self;
        if (ai_lib.isInCombat(npc) || ai_lib.isInCombat(player))
        {
            return SCRIPT_OVERRIDE;
        }
        if (gcw_rebel_space_destroy_condition_isImperialPlayer(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_47");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_destroy_condition_completedKill1(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            gcw_rebel_space_destroy_action_signalDone(player, npc);
            string_id message = new string_id(c_stringFile, "s_6");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_destroy_condition_ifFailedOne(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_39");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse1 = true;
            }
            if (hasResponse)
            {
                int responseIndex = 0;
                string_id responses[] = new string_id[numberOfResponses];
                if (hasResponse0)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_26");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_35");
                }
                utils.setScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId", 14);
                npcStartConversation(player, npc, "gcw_rebel_space_destroy", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_destroy_condition_killActive1(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_10");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_destroy_condition_inPhase1(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_24");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse1 = true;
            }
            if (hasResponse)
            {
                int responseIndex = 0;
                string_id responses[] = new string_id[numberOfResponses];
                if (hasResponse0)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_26");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_35");
                }
                utils.setScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId", 14);
                npcStartConversation(player, npc, "gcw_rebel_space_destroy", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_destroy_condition__defaultCondition(player, npc))
        {
            doAnimationAction(npc, "dismiss");
            string_id message = new string_id(c_stringFile, "s_46");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  All conditions for OnStartNpcConversation were false.");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("gcw_rebel_space_destroy"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
        if (branchId == 4 && gcw_rebel_space_destroy_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 5 && gcw_rebel_space_destroy_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 8 && gcw_rebel_space_destroy_handleBranch8(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 11 && gcw_rebel_space_destroy_handleBranch11(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 14 && gcw_rebel_space_destroy_handleBranch14(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.gcw_rebel_space_destroy.branchId");
        return SCRIPT_CONTINUE;
    }
}
