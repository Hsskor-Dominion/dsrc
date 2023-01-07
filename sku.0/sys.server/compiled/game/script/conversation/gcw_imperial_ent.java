package script.conversation;

import script.library.*;
import script.*;

public class gcw_imperial_ent extends script.base_script
{
    public gcw_imperial_ent()
    {
    }
    public static String c_stringFile = "conversation/gcw_imperial_ent";
    public boolean gcw_imperial_ent_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean gcw_imperial_ent_condition_playerCompletedQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "gcw_imperial_ent");
    }
    public boolean gcw_imperial_ent_condition_playerStartedQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "gcw_imperial_ent");
    }
    public boolean gcw_imperial_ent_condition_playerFinishedMainTask(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "gcw_imperial_ent", "talktogcw");
    }
    public boolean gcw_imperial_ent_condition_notEntertainer(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        if (pTemplate.contains("entertainer"))
        {
            return false;
        }
        else 
        {
            return true;
        }
    }
    public boolean gcw_imperial_ent_condition_notImperial(obj_id player, obj_id npc) throws InterruptedException
    {
        if (factions.isImperial(player))
        {
            return false;
        }
        else 
        {
            return true;
        }
    }
    public boolean gcw_imperial_ent_condition_startingconversation(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        if (pTemplate.contains("entertainer"))
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public void gcw_imperial_ent_action_facePlayer(obj_id player, obj_id npc) throws InterruptedException
    {
        faceTo(npc, player);
    }
    public void gcw_imperial_ent_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "gcw_ent_reward");
    }
    public void gcw_imperial_ent_action_giveQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "gcw_imperial_ent");
    }
    public int gcw_imperial_ent_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_32"))
        {
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "celebrate1");
                gcw_imperial_ent_action_signalReward(player, npc);
                string_id message = new string_id(c_stringFile, "s_34");
                utils.removeScriptVar(player, "conversation.gcw_imperial_ent.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_36"))
        {
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "wave_finger_warning");
                string_id message = new string_id(c_stringFile, "s_38");
                utils.removeScriptVar(player, "conversation.gcw_imperial_ent.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_imperial_ent_handleBranch6(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_16"))
        {
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "nod");
                string_id message = new string_id(c_stringFile, "s_18");
                int numberOfResponses = 0;
                boolean hasResponse = false;
                boolean hasResponse0 = false;
                if (gcw_imperial_ent_condition__defaultCondition(player, npc))
                {
                    ++numberOfResponses;
                    hasResponse = true;
                    hasResponse0 = true;
                }
                if (hasResponse)
                {
                    int responseIndex = 0;
                    string_id responses[] = new string_id[numberOfResponses];
                    if (hasResponse0)
                    {
                        responses[responseIndex++] = new string_id(c_stringFile, "s_20");
                    }
                    utils.setScriptVar(player, "conversation.gcw_imperial_ent.branchId", 7);
                    npcSpeak(player, message);
                    npcSetConversationResponses(player, responses);
                }
                else 
                {
                    utils.removeScriptVar(player, "conversation.gcw_imperial_ent.branchId");
                    npcEndConversationWithMessage(player, message);
                }
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_41"))
        {
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "sigh_deeply");
                string_id message = new string_id(c_stringFile, "s_44");
                utils.removeScriptVar(player, "conversation.gcw_imperial_ent.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_imperial_ent_handleBranch7(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_20"))
        {
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "shrug_shoulders");
                string_id message = new string_id(c_stringFile, "s_22");
                int numberOfResponses = 0;
                boolean hasResponse = false;
                boolean hasResponse0 = false;
                if (gcw_imperial_ent_condition__defaultCondition(player, npc))
                {
                    ++numberOfResponses;
                    hasResponse = true;
                    hasResponse0 = true;
                }
                if (hasResponse)
                {
                    int responseIndex = 0;
                    string_id responses[] = new string_id[numberOfResponses];
                    if (hasResponse0)
                    {
                        responses[responseIndex++] = new string_id(c_stringFile, "s_24");
                    }
                    utils.setScriptVar(player, "conversation.gcw_imperial_ent.branchId", 8);
                    npcSpeak(player, message);
                    npcSetConversationResponses(player, responses);
                }
                else 
                {
                    utils.removeScriptVar(player, "conversation.gcw_imperial_ent.branchId");
                    npcEndConversationWithMessage(player, message);
                }
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_imperial_ent_handleBranch8(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_24"))
        {
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "nod");
                gcw_imperial_ent_action_giveQuest(player, npc);
                string_id message = new string_id(c_stringFile, "s_26");
                utils.removeScriptVar(player, "conversation.gcw_imperial_ent.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if ((!isMob(self)) || (isPlayer(self)))
        {
            detachScript(self, "conversation.gcw_imperial_ent");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        ai_lib.setDefaultCalmBehavior(self, ai_lib.BEHAVIOR_SENTINEL);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Dolores Karre (Imperial Morale Corps)");
        ai_lib.setDefaultCalmBehavior(self, ai_lib.BEHAVIOR_SENTINEL);
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
        detachScript(self, "conversation.gcw_imperial_ent");
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
        if (gcw_imperial_ent_condition_playerCompletedQuest(player, npc))
        {
            groundquests.clearQuest(player, "gcw_imperial_ent");
        }
        if (gcw_imperial_ent_condition_playerStartedQuest(player, npc))
        {
            gcw_imperial_ent_action_facePlayer(player, npc);
            string_id message = new string_id(c_stringFile, "s_30");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (gcw_imperial_ent_condition_playerFinishedMainTask(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
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
                    responses[responseIndex++] = new string_id(c_stringFile, "s_32");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_36");
                }
                utils.setScriptVar(player, "conversation.gcw_imperial_ent.branchId", 2);
                npcStartConversation(player, npc, "gcw_imperial_ent", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (gcw_imperial_ent_condition_notEntertainer(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_43");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_imperial_ent_condition_notImperial(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_43");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_imperial_ent_condition_startingconversation(player, npc))
        {
            doAnimationAction(npc, "greet");
            gcw_imperial_ent_action_facePlayer(player, npc);
            string_id message = new string_id(c_stringFile, "s_14");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (gcw_imperial_ent_condition__defaultCondition(player, npc))
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
                    responses[responseIndex++] = new string_id(c_stringFile, "s_16");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_41");
                }
                utils.setScriptVar(player, "conversation.gcw_imperial_ent.branchId", 6);
                npcStartConversation(player, npc, "gcw_imperial_ent", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (gcw_imperial_ent_condition__defaultCondition(player, npc))
        {
            doAnimationAction(npc, "greet");
            string_id message = new string_id(c_stringFile, "s_46");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  All conditions for OnStartNpcConversation were false.");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("gcw_imperial_ent"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.gcw_imperial_ent.branchId");
        if (branchId == 2 && gcw_imperial_ent_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 6 && gcw_imperial_ent_handleBranch6(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 7 && gcw_imperial_ent_handleBranch7(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 8 && gcw_imperial_ent_handleBranch8(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.gcw_imperial_ent.branchId");
        return SCRIPT_CONTINUE;
    }
}
