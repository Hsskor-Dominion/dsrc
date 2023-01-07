package script.conversation;

import script.library.*;
import script.library.factions;
import script.*;

public class jabba_raid extends script.base_script
{
    public jabba_raid()
    {
    }
    public static String c_stringFile = "conversation/jabba_raid";
    public boolean jabba_raid_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean jabba_raid_condition_checkQ1(obj_id player, obj_id npc) throws InterruptedException
    {
        int quest1 = questGetQuestId("quest/jabba_raid");
        return (questIsQuestActive(quest1, player));
    }
    public boolean jabba_raid_condition_hasAnyQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        int quest1 = questGetQuestId("quest/jabba_raid");
        return (questIsQuestActive(quest1, player));
    }
    public boolean jabba_raid_condition_playerStartedQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "jabba_raid");
    }
    public boolean jabba_raid_condition_playerFinishedMainTask(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "jabba_raid", "returntofennec");
    }
    public boolean jabba_raid_condition_isjabba(obj_id player, obj_id npc) throws InterruptedException
    {
        float jabbaFaction = factions.getFactionStanding(player, "jabba");
        if (jabbaFaction >= 2500)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public void jabba_raid_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void jabba_raid_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "fennec_reward");
    }
    public void jabba_raid_action_grantQ1(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/jabba_raid");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int jabba_raid_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_165"))
        {
            if (jabba_raid_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "thumb_up");
                string_id message = new string_id(c_stringFile, "s_167");
                utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                chat.chat(npc, player, message);
                npcEndConversation(player);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_169"))
        {
            if (jabba_raid_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "standing_raise_fist");
                jabba_raid_action_signalReward(player, npc);
                string_id message = new string_id(c_stringFile, "s_171");
                utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                chat.chat(npc, player, message);
                npcEndConversation(player);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int jabba_raid_handleBranch26(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_255"))
        {
            if (jabba_raid_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "shakefist");
                string_id message = new string_id(c_stringFile, "s_257");
                int numberOfResponses = 0;
                boolean hasResponse = false;
                boolean hasResponse0 = false;
                if (jabba_raid_condition__defaultCondition(player, npc))
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
                        responses[responseIndex++] = new string_id(c_stringFile, "s_259");
                    }
                    utils.setScriptVar(player, "conversation.jabba_raid.branchId", 27);
                    npcSpeak(player, message);
                    npcSetConversationResponses(player, responses);
                }
                else 
                {
                    utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                    chat.chat(npc, player, message);
                    npcEndConversation(player);
                }
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_271"))
        {
            if (jabba_raid_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "threaten");
                string_id message = new string_id(c_stringFile, "s_273");
                utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                chat.chat(npc, player, message);
                npcEndConversation(player);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int jabba_raid_handleBranch27(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_259"))
        {
            if (jabba_raid_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_261");
                int numberOfResponses = 0;
                boolean hasResponse = false;
                boolean hasResponse0 = false;
                if (jabba_raid_condition__defaultCondition(player, npc))
                {
                    ++numberOfResponses;
                    hasResponse = true;
                    hasResponse0 = true;
                }
                boolean hasResponse1 = false;
                if (jabba_raid_condition__defaultCondition(player, npc))
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
                        responses[responseIndex++] = new string_id(c_stringFile, "s_263");
                    }
                    if (hasResponse1)
                    {
                        responses[responseIndex++] = new string_id(c_stringFile, "s_267");
                    }
                    utils.setScriptVar(player, "conversation.jabba_raid.branchId", 28);
                    npcSpeak(player, message);
                    npcSetConversationResponses(player, responses);
                }
                else 
                {
                    utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                    chat.chat(npc, player, message);
                    npcEndConversation(player);
                }
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int jabba_raid_handleBranch28(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_263"))
        {

            if (jabba_raid_condition_isjabba(player, npc))
            {
                doAnimationAction(npc, "thumb_up");
                groundquests.grantQuest(player, "jabba_raid");
                string_id message = new string_id(c_stringFile, "s_265");
                utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                chat.chat(npc, player, message);
                npcEndConversation(player);
                return SCRIPT_CONTINUE;
            }
	    else
            {
                doAnimationAction(npc, "laugh");
                string_id message = new string_id(c_stringFile, "s_269");
                utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                chat.chat(npc, player, message);
                npcEndConversation(player);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_267"))
        {
	    if (jabba_raid_condition__defaultCondition(player, npc))
            {
		jabba_raid_action_vendor(player, npc);
                doAnimationAction(npc, "thumb_up");
                string_id message = new string_id(c_stringFile, "fence_fennec");
                utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                chat.chat(npc, player, message);
                npcEndConversation(player);
                return SCRIPT_CONTINUE;
            }
            else
            {
                doAnimationAction(npc, "waft");
                string_id message = new string_id(c_stringFile, "s_269");
                utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
                chat.chat(npc, player, message);
                npcEndConversation(player);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if ((!isMob(self)) || (isPlayer(self)))
        {
            detachScript(self, "conversation.jabba_raid");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
	setName(self, "Fennec Shand (Jabba's Assassin)");
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
        detachScript(self, "conversation.jabba_raid");
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
        if (jabba_raid_condition_hasAnyQuest(player, npc))
        {
            doAnimationAction(npc, "point_forward");
            string_id message = new string_id(c_stringFile, "s_163");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
     	if (jabba_raid_condition_checkQ1(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (jabba_raid_condition_playerFinishedMainTask(player, npc))
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
                    responses[responseIndex++] = new string_id(c_stringFile, "s_165");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_169");
                }
                utils.setScriptVar(player, "conversation.jabba_raid.branchId", 1);
                npcStartConversation(player, npc, "jabba_raid", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (jabba_raid_condition__defaultCondition(player, npc))
        {
            doAnimationAction(npc, "scare");
            string_id message = new string_id(c_stringFile, "s_253");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (jabba_raid_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (jabba_raid_condition__defaultCondition(player, npc))
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
                    responses[responseIndex++] = new string_id(c_stringFile, "s_255");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_271");
                }
                utils.setScriptVar(player, "conversation.jabba_raid.branchId", 26);
                npcStartConversation(player, npc, "jabba_raid", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  All conditions for OnStartNpcConversation were false.");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("jabba_raid"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.jabba_raid.branchId");
	if (branchId == 1 && jabba_raid_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 26 && jabba_raid_handleBranch26(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 27 && jabba_raid_handleBranch27(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 28 && jabba_raid_handleBranch28(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.jabba_raid.branchId");
        return SCRIPT_CONTINUE;
    }
}
