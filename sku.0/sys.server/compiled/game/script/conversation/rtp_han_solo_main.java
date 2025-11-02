package script.conversation;

import script.library.*;
import script.library.utils;
import script.library.npe;
import script.dictionary;
import script.*;

public class rtp_han_solo_main extends script.base_script
{
    public rtp_han_solo_main()
    {
    }
    public static String c_stringFile = "conversation/rtp_han_solo_main";
    public boolean rtp_han_solo_main_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean rtp_han_solo_main_condition_rtp_han_solo_01_active(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "rtp_han_solo_01");
    }
    public boolean rtp_han_solo_main_condition_rtp_han_solo_01_complete(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "rtp_han_solo_01", "rtp_han_solo_01_03") || groundquests.hasCompletedQuest(player, "rtp_han_solo_01");
    }
    public boolean rtp_han_solo_main_condition_rtp_han_solo_02_active(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "rtp_han_solo_02");
    }
    public boolean rtp_han_solo_main_condition_rtp_han_solo_02_completed(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "rtp_han_solo_02", "rtp_han_solo_02_02") || groundquests.hasCompletedQuest(player, "rtp_han_solo_02");
    }
    public boolean rtp_han_solo_main_condition_completedNienNunb(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "rtp_nien_nunb_01");
    }
    public boolean rtp_han_solo_main_condition_notRebel(obj_id player, obj_id npc) throws InterruptedException
    {
        String playerFaction = factions.getFaction(player);
        if (playerFaction == null || !playerFaction.equals("Rebel"))
        {
            return true;
        }
        return false;
    }
    public boolean rtp_han_solo_main_condition_rebel_isOnLeave(obj_id player, obj_id npc) throws InterruptedException
    {
        return factions.isOnLeave(player);
    }
    public void rtp_han_solo_main_action_rtp_han_solo_01_granted(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.grantQuest(player, "rtp_han_solo_01");
    }
    public void rtp_han_solo_main_action_rtp_han_solo_01_signal(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "rtp_han_solo_01_03");
    }
    public void rtp_han_solo_main_action_rtp_han_solo_02_signal(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "rtp_han_solo_02_02");
    }
    public void rtp_han_solo_main_action_rtp_han_solo_02_granted(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.grantQuest(player, "rtp_han_solo_02");
    }
    public int rtp_han_solo_main_handleBranch6(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_22"))
        {
            if (rtp_han_solo_main_condition__defaultCondition(player, npc))
            {
                rtp_han_solo_main_action_rtp_han_solo_02_granted(player, npc);
                string_id message = new string_id(c_stringFile, "s_25");
                utils.removeScriptVar(player, "conversation.rtp_han_solo_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_26"))
        {
            if (rtp_han_solo_main_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_30");
                utils.removeScriptVar(player, "conversation.rtp_han_solo_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int rtp_han_solo_main_handleBranch10(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_36"))
        {
            if (rtp_han_solo_main_condition__defaultCondition(player, npc))
            {
                rtp_han_solo_main_action_rtp_han_solo_01_granted(player, npc);
                string_id message = new string_id(c_stringFile, "s_38");
                utils.removeScriptVar(player, "conversation.rtp_han_solo_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_40"))
        {
            if (rtp_han_solo_main_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_42");
                utils.removeScriptVar(player, "conversation.rtp_han_solo_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    // --- Player chooses smuggle option ---
    public int rtp_han_solo_main_handleBranch12(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        string_id smuggleChoiceSid = new string_id(c_stringFile, "s_smuggle_choice");

        if (response.equals(smuggleChoiceSid))
        {
            final int SMUGGLE_COST = 18000;

            // --- Deduct funds regardless of balance ---
            long playerCredits = getTotalMoney(player);
            if (playerCredits < SMUGGLE_COST)
            {
                sendSystemMessageTestingOnly(player, "Han Solo takes the credits anyway...");
            }
            money.requestPayment(player, npc, SMUGGLE_COST, null, null); // just deduct, no callback

            // --- Flavor feedback ---
            sendSystemMessageTestingOnly(player, "Han Solo winks and signals Chewie to prep the Falcon...");
            sendSystemMessageTestingOnly(player, "You're being smuggled to Tansaari Point Station...");

            // --- Warp player directly ---
            warpPlayerToDestination(player);

            // --- Clean up conversation vars ---
            utils.removeScriptVar(player, "conversation.rtp_han_solo_main.branchId");

            return SCRIPT_CONTINUE;
        }

        utils.removeScriptVar(player, "conversation.rtp_han_solo_main.branchId");
        return SCRIPT_CONTINUE;
    }

    // --- Helper function: warp player only ---
    public void warpPlayerToDestination(obj_id player) throws InterruptedException
    {
        // --- Flavor message ---
        sendSystemMessageTestingOnly(player, "Han Solo flies the Falcon through hyperspace...");

        // --- Warp player using NPE shared station function ---
        // This function handles the cluster-wide data and warp internally
        npe.movePlayerFromOrdMantellSpaceToSharedStation(player);

        // --- Optionally, you can add a follow-up system message ---
        sendSystemMessageTestingOnly(player, "You arrive safely at Tansarii Point Station.");
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if ((!isMob(self)) || (isPlayer(self)))
        {
            detachScript(self, "conversation.rtp_han_solo_main");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        menuInfoData.setServerNotify(false);
        faceTo(self, player);
        return SCRIPT_CONTINUE;
    }
    public int OnIncapacitated(obj_id self, obj_id killer) throws InterruptedException
    {
        clearCondition(self, CONDITION_CONVERSABLE);
        detachScript(self, "conversation.rtp_han_solo_main");
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

        // --- Condition: Not a Rebel
        if (rtp_han_solo_main_condition_notRebel(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_44");
            string_id[] responses = { new string_id(c_stringFile, "s_smuggle_choice") };
            npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
            utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 12);
            return SCRIPT_CONTINUE;
        }

        // --- Condition: Prior mission not complete
        if (!rtp_han_solo_main_condition_completedNienNunb(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_24");
            string_id[] responses = { new string_id(c_stringFile, "s_smuggle_choice") };
            npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
            utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 12);
            return SCRIPT_CONTINUE;
        }

        // --- Condition: On leave
        if (rtp_han_solo_main_condition_rebel_isOnLeave(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_43");
            string_id[] responses = { new string_id(c_stringFile, "s_smuggle_choice") };
            npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
            utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 12);
            return SCRIPT_CONTINUE;
        }

        // --- Condition: Final mission complete
        if (rtp_han_solo_main_condition_rtp_han_solo_02_completed(player, npc))
        {
            rtp_han_solo_main_action_rtp_han_solo_02_signal(player, npc);
            string_id message = new string_id(c_stringFile, "s_9");
            string_id[] responses = { new string_id(c_stringFile, "s_smuggle_choice") };
            npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
            utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 12);
            return SCRIPT_CONTINUE;
        }

        // --- Condition: Mission 02 active
        if (rtp_han_solo_main_condition_rtp_han_solo_02_active(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_16");
            string_id[] responses = { new string_id(c_stringFile, "s_smuggle_choice") };
            npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
            utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 12);
            return SCRIPT_CONTINUE;
        }

        // --- Condition: Mission 01 complete
        if (rtp_han_solo_main_condition_rtp_han_solo_01_complete(player, npc))
        {
            rtp_han_solo_main_action_rtp_han_solo_01_signal(player, npc);
            string_id message = new string_id(c_stringFile, "s_20");

            int numberOfResponses = 2;
            string_id[] responses = new string_id[numberOfResponses];
            responses[0] = new string_id(c_stringFile, "s_22");
            responses[1] = new string_id(c_stringFile, "s_smuggle_choice");

            npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
            utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 6);
            return SCRIPT_CONTINUE;
        }

        // --- Condition: Mission 01 active
        if (rtp_han_solo_main_condition_rtp_han_solo_01_active(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_32");
            string_id[] responses = { new string_id(c_stringFile, "s_smuggle_choice") };
            npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
            utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 12);
            return SCRIPT_CONTINUE;
        }

        // --- Default branch
        if (rtp_han_solo_main_condition__defaultCondition(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_34");
            string_id[] responses = {
                    new string_id(c_stringFile, "s_36"),
                    new string_id(c_stringFile, "s_smuggle_choice")
            };

            npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
            utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 10);
            return SCRIPT_CONTINUE;
        }

        // --- Fallback only if no other conditions met
        string_id message = new string_id(c_stringFile, "s_smuggle_offer");
        string_id[] responses = { new string_id(c_stringFile, "s_smuggle_choice") };
        npcStartConversation(player, npc, "rtp_han_solo_main", message, responses);
        utils.setScriptVar(player, "conversation.rtp_han_solo_main.branchId", 12);
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("rtp_han_solo_main"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.rtp_han_solo_main.branchId");

        if (branchId == 6 && rtp_han_solo_main_handleBranch6(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 10 && rtp_han_solo_main_handleBranch10(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 12 && rtp_han_solo_main_handleBranch12(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.rtp_han_solo_main.branchId");
        return SCRIPT_CONTINUE;
    }
}
