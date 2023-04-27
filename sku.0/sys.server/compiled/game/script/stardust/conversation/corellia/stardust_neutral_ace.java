package script.stardust.conversation.naboo;

import script.library.*;
import script.*;

public class stardust_neutral_ace extends script.base_script
{
    public stardust_neutral_ace()
    {
    }
    public static String c_stringFile = "conversation/stardust_neutral_ace";
    public boolean stardust_neutral_ace_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public void stardust_neutral_ace_action_facePlayer(obj_id player, obj_id npc) throws InterruptedException
    {
        faceTo(npc, player);
        return;
    }
    public boolean stardust_imperial_condition_ace_of_aces(obj_id player, obj_id npc) throws InterruptedException
    {
        return ((badge.hasBadge(player, "pilot_imperial_navy_corellia") && badge.hasBadge(player, "pilot_imperial_navy_naboo") && badge.hasBadge(player, "pilot_imperial_navy_tatooine")));
    }
    public boolean stardust_neutral_condition_ace_of_aces(obj_id player, obj_id npc) throws InterruptedException
    {
        return ((badge.hasBadge(player, "pilot_neutral_corellia") && badge.hasBadge(player, "pilot_neutral_naboo") && badge.hasBadge(player, "pilot_neutral_tatooine")));
    }
    public boolean stardust_rebel_condition_ace_of_aces(obj_id player, obj_id npc) throws InterruptedException
    {
        return ((badge.hasBadge(player, "pilot_rebel_navy_corellia") && badge.hasBadge(player, "pilot_rebel_navy_naboo") && badge.hasBadge(player, "pilot_rebel_navy_tatooine")));
    }
    public int stardust_neutral_ace_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_star_destroyer"))
        {
            if (stardust_neutral_ace_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_lets_go");
                utils.removeScriptVar(player, "conversation.stardust_neutral_ace.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_ace_of_aces"))
        {
            if (stardust_imperial_condition_ace_of_aces(player, npc))
            {
                grantSkill(player, "stardust_ace_of_aces");
                string_id message = new string_id(c_stringFile, "grant_ace_of_aces");
                utils.removeScriptVar(player, "conversation.stardust_neutral_ace.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
            if (stardust_neutral_condition_ace_of_aces(player, npc))
            {
                grantSkill(player, "stardust_ace_of_aces");
                string_id message = new string_id(c_stringFile, "grant_ace_of_aces");
                utils.removeScriptVar(player, "conversation.stardust_neutral_ace.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
            if (stardust_rebel_condition_ace_of_aces(player, npc))
            {
                grantSkill(player, "stardust_ace_of_aces");
                string_id message = new string_id(c_stringFile, "grant_ace_of_aces");
                utils.removeScriptVar(player, "conversation.stardust_neutral_ace.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
            if (stardust_neutral_ace_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_move_along");
                utils.removeScriptVar(player, "conversation.stardust_neutral_ace.branchId");
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
            detachScript(self, "conversation.stardust_neutral_ace");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setName(self, "Commander Venisa Doza (Jade Squadron Ace of Aces)");
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
        detachScript(self, "conversation.stardust_neutral_ace");
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
        if (stardust_neutral_ace_condition__defaultCondition(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_at_your_command");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (stardust_neutral_ace_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (stardust_neutral_ace_condition__defaultCondition(player, npc))
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
                    responses[responseIndex++] = new string_id(c_stringFile, "s_star_destroyer");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_ace_of_aces");
                }
                utils.setScriptVar(player, "conversation.stardust_neutral_ace.branchId", 1);
                npcStartConversation(player, npc, "stardust_neutral_ace", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (stardust_neutral_ace_condition__defaultCondition(player, npc))
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
        if (!conversationId.equals("stardust_neutral_ace"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.stardust_neutral_ace.branchId");
        if (branchId == 1 && stardust_neutral_ace_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.stardust_neutral_ace.branchId");
        return SCRIPT_CONTINUE;
    }
}
