package script.stardust.conversation.tatooine;

import script.*;
import script.library.*;

public class boonta_eve extends base_script
{
    public boonta_eve()
    {
    }
    public static String c_stringFile = "conversation/boonta_eve";
    public boolean boonta_eve_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean boonta_eveComplete_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_boonta_eve");
    }
    public boolean boonta_eveFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float fettFaction = factions.getFactionStanding(player, "jabba");
        if (fettFaction >= 0)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public void boonta_eve_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void boonta_eve_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "boonta_eve_reward");
    }
    public void boonta_eve_action_grantQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_boonta_eve");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int boonta_eve_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "trade");

            utils.setScriptVar(player, "conversation.boonta_eve_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_race"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_race");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "become_boonta_eve");

            utils.setScriptVar(player, "conversation.boonta_eve_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int boonta_eve_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("trade"))
        {
            if (boonta_eveFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "fence");
                boonta_eve_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.boonta_eve_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_need_fett_rep");

                utils.removeScriptVar(player, "conversation.boonta_eve_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int boonta_eve_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("become_boonta_eve"))
        {
            // Default condition always true, keeping your structure
            if (boonta_eve_condition__defaultCondition(player, npc))
            {
                // Attempt to reward the player if they earned it
                boonta_eve_action_rewardPlayer(player, npc);

                final string_id message = new string_id(c_stringFile, "get_it_done");

                utils.removeScriptVar(player, "conversation.boonta_eve_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public void boonta_eve_action_rewardPlayer(obj_id player, obj_id npc) throws InterruptedException
    {
        // Reward only if they have the completion objVar
        if (hasObjVar(player, "completed_boonta"))
        {
            // Create the reward token
            obj_id playerInv = utils.getInventoryContainer(player);
            static_item.createNewItemFunction("item_pgc_token_03", playerInv);
            sendSystemMessage(player, "You receive a Boonta Eve Champion Gold Peggat Coin!", "");
            // Remove the completion flag so they can't repeat reward infinitely
            removeObjVar(player, "completed_boonta");
        }
        else
        {
            sendSystemMessage(player, "You must first complete your Boonta Eve task before claiming a reward!", "");
        }
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
        setName(self, "Geezer (Racing Gigs)");

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

        if (boonta_eve_condition__defaultCondition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_race");

            utils.setScriptVar(player, "conversation.boonta_eve_conversation.branchId", 1);

            npcStartConversation(player, npc, "boonta_eve_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks curiously*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("boonta_eve_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.boonta_eve_conversation.branchId");

        if (branchId == 1 && boonta_eve_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && boonta_eve_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && boonta_eve_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.boonta_eve_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
