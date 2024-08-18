package script.terminal;

import script.*;
import script.library.ai_lib;
import script.library.chat;
import script.library.groundquests;
import script.library.utils;
import script.library.callable;

public class stardust extends script.base_script
{
    public stardust()
    {
    }

    public static String c_stringFile = "conversation/gcw_terminal";

    public boolean gcw_terminal_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }

    public void gcw_terminal_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }

    public void gcw_terminal_action_grantQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_gcw_terminal");
        groundquests.grantQuest(questId, player, npc, true);
    }

    public void gcw_terminal_action_attachBattlefieldScript(obj_id player, obj_id npc) throws InterruptedException
    {
        // Define the range and volume
        float range = 64.0f;
        location loc = getLocation(player); // or getLocation(npc) if using NPC's location
        obj_id vehicle = getFirstObjectWithScript(loc, range, "systems.vehicle_system.vehicle_base");

        debugServerConsoleMsg(player, "Vehicle ID found: " + vehicle);

        if (isIdValid(vehicle))
        {
            if (!hasScript(vehicle, "systems.vehicle_system.battlefield_vehicle"))
            {
                attachScript(vehicle, "systems.vehicle_system.battlefield_vehicle");
                sendSystemMessage(player, new string_id(c_stringFile, "battlefield_script_attached"));
            }
            else
            {
                sendSystemMessage(player, new string_id(c_stringFile, "battlefield_script_already_attached"));
            }
        }
        else
        {
            sendSystemMessage(player, new string_id(c_stringFile, "no_vehicle_found"));
        }
    }

    public int gcw_terminal_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_exchange"))
        {
            string_id message = new string_id(c_stringFile, "npc_consider_trade");
            int numberOfResponses = 1;

            string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "exchange");

            utils.setScriptVar(player, "conversation.gcw_terminal_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_siege_vehicles"))
        {
            string_id message = new string_id(c_stringFile, "npc_consider_siege");
            int numberOfResponses = 1;

            string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "siege_vehicle");

            utils.setScriptVar(player, "conversation.gcw_terminal_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_gcw_craft"))
        {
            string_id message = new string_id(c_stringFile, "npc_consider_gcw_craft");
            int numberOfResponses = 1;

            string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "gcw_craft");

            utils.setScriptVar(player, "conversation.gcw_terminal_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    public int gcw_terminal_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("exchange"))
        {
            if (gcw_terminal_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "fence");
                gcw_terminal_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.gcw_terminal_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                string_id message = new string_id(c_stringFile, "npc_something_went_wrong");

                utils.removeScriptVar(player, "conversation.gcw_terminal_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }

    public int gcw_terminal_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("siege_vehicle"))
        {
            if (gcw_terminal_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "we_must_build_it");

                //gcw_terminal_action_grantQuest(player, npc); // Grant the crafter quest
                gcw_terminal_action_attachBattlefieldScript(player, npc); // Attach battlefield_vehicle script

                utils.removeScriptVar(player, "conversation.gcw_terminal_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }

    public int gcw_terminal_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("gcw_craft"))
        {
            string_id message = new string_id(c_stringFile, "choose_option");
            int numberOfResponses = 4;

            string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "option1");
            responses[responseIndex++] = new string_id(c_stringFile, "option2");
            responses[responseIndex++] = new string_id(c_stringFile, "option3");
            responses[responseIndex++] = new string_id(c_stringFile, "option4");

            utils.setScriptVar(player, "conversation.gcw_terminal_conversation.branchId", 5);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }

    public int gcw_terminal_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("option1"))
        {
            string_id message = new string_id(c_stringFile, "option1_response");
            // Add functionality here if needed
            utils.removeScriptVar(player, "conversation.gcw_terminal_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        else if (response.equals("option2"))
        {
            string_id message = new string_id(c_stringFile, "option2_response");
            // Add functionality here if needed
            utils.removeScriptVar(player, "conversation.gcw_terminal_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        else if (response.equals("option3"))
        {
            string_id message = new string_id(c_stringFile, "option3_response");
            // Add functionality here if needed
            utils.removeScriptVar(player, "conversation.gcw_terminal_conversation.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        else if (response.equals("option4"))
        {
            string_id message = new string_id(c_stringFile, "option4_response");
            // Add functionality here if needed
            utils.removeScriptVar(player, "conversation.gcw_terminal_conversation.branchId");
            npcEndConversationWithMessage(player, message);
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
        setName(self, "CLL-M3 Construction Droid (Siege Vehicles)");
        ai_lib.setDefaultCalmBehavior(self, ai_lib.BEHAVIOR_SENTINEL);

        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
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

        if (gcw_terminal_condition__defaultCondition(npc, player))
        {
            string_id message = new string_id(c_stringFile, "npc_intro");
            int numberOfResponses = 3;

            string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_exchange");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_siege_vehicles");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_gcw_craft");

            utils.setScriptVar(player, "conversation.gcw_terminal_conversation.branchId", 1);

            npcStartConversation(player, npc, "gcw_terminal_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Terminal Error*");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("gcw_terminal_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        int branchId = utils.getIntScriptVar(player, "conversation.gcw_terminal_conversation.branchId");

        if (branchId == 1 && gcw_terminal_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && gcw_terminal_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && gcw_terminal_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && gcw_terminal_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 5 && gcw_terminal_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.gcw_terminal_conversation.branchId");
        return SCRIPT_CONTINUE;
    }
}