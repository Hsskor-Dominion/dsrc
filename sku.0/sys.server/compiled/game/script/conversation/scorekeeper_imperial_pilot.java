package script.conversation;

import script.library.*;
import script.*;

public class scorekeeper_imperial_pilot extends script.base_script
{
    public scorekeeper_imperial_pilot()
    {
    }
    public static String c_stringFile = "conversation/scorekeeper_imperial_pilot";
    public boolean scorekeeper_imperial_pilot_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public void scorekeeper_imperial_pilot_action_sendToScorekeeper(obj_id player, obj_id npc) throws InterruptedException
    {
        instance.requestInstanceMovement(player, "heroic_star_destroyer");
        return;
    }
    public void scorekeeper_imperial_pilot_action_facePlayer(obj_id player, obj_id npc) throws InterruptedException
    {
        faceTo(npc, player);
        return;
    }
    public int scorekeeper_imperial_pilot_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_shuttle"))
        {
            if (scorekeeper_imperial_pilot_condition__defaultCondition(player, npc))
            {
                scorekeeper_imperial_pilot_action_sendToScorekeeper(player, npc);
                string_id message = new string_id(c_stringFile, "s_lets_go");
                utils.removeScriptVar(player, "conversation.scorekeeper_imperial_pilot.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_outpost"))
        {
            if (scorekeeper_imperial_pilot_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_move_along");
                utils.removeScriptVar(player, "conversation.scorekeeper_imperial_pilot.branchId");
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
            detachScript(self, "conversation.scorekeeper_imperial_pilot");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setName(self, "DS-547 (Imperial Shuttle Pilot)");
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
        detachScript(self, "conversation.scorekeeper_imperial_pilot");
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
        if (scorekeeper_imperial_pilot_condition__defaultCondition(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_at_your_command");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (scorekeeper_imperial_pilot_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (scorekeeper_imperial_pilot_condition__defaultCondition(player, npc))
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
                    responses[responseIndex++] = new string_id(c_stringFile, "s_shuttle");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_outpost");
                }
                utils.setScriptVar(player, "conversation.scorekeeper_imperial_pilot.branchId", 1);
                npcStartConversation(player, npc, "scorekeeper_imperial_pilot", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (scorekeeper_imperial_pilot_condition__defaultCondition(player, npc))
        {
            doAnimationAction(npc, "dismiss");
            string_id message = new string_id(c_stringFile, "s_go_away");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  All conditions for OnStartNpcConversation were false.");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("scorekeeper_imperial_pilot"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.scorekeeper_imperial_pilot.branchId");
        if (branchId == 1 && scorekeeper_imperial_pilot_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.scorekeeper_imperial_pilot.branchId");
        return SCRIPT_CONTINUE;
    }
}
