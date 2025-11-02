package script.stardust.conversation.tatooine;

import script.*;
import script.library.*;

public class prison_guard extends base_script
{
    public static String c_stringFile = "conversation/prison_guard";

    // --- default condition always true
    public boolean prison_guard_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }

    // --- Actions ---
    // --- Persuade success/failure roll (hardcoded luck with Rancor chance) ---
    public void prison_guard_action_persuade(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!isIdValid(player))
            return;

        // Hardcoded player luck (100–600 range)
        int luck = rand(100, 600);

        // Calculate bonus: 10–60
        int luckBonus = luck / 10;

        // Random roll 1–150
        int roll = rand(1, 150);
        int rollWithBonus = roll + luckBonus;

        // Debug message
        sendSystemMessageTestingOnly(player, "Persuade roll: " + roll + " + luck bonus: " + luckBonus + " = " + rollWithBonus);

        // Success threshold
        if (rollWithBonus >= 150)//extreme threshold during testing
        {
            // Success: warp player to guard (free)
            prison_guard_action_free_player(player, npc);
        }
        else
        {
            // Failure: % chance to spawn in Rancor pit instead of Sarlaac
            int badRoll = rand(1, 100);
            if (badRoll <= 50)
            {
                // Spawn in Rancor cell
                obj_id palaceBuilding = obj_id.getObjId(1177465L); // Fett's Palace
                obj_id rancorCell = getCellId(palaceBuilding, "rancorpit");

                if (isIdValid(rancorCell))
                {
                    // Spawn static Rancor
                    warpPlayer(player, "tatooine", 0f, 1f, 0f, rancorCell, 0, 0, 0f, "", false);
                    sendSystemMessageTestingOnly(player, "Oh no! You stumble into the Rancor pit!");
                    return;
                }
            }

            // Normal failure: warp to Sarlaac
            prison_guard_action_send_to_sarlaac(player, npc);
        }
    }

    // Warp to Sarlaac Pit
    public void prison_guard_action_send_to_sarlaac(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!isIdValid(player)) return;

        warpPlayer(player, "tatooine", -6168f, 19f, -3365f, null, 0, 0, 0f, "", false);
        sendSystemMessageTestingOnly(player, "You have been swallowed by the Sarlacc!");
    }

    // Warp player to guard (free) inside cell
    public void prison_guard_action_free_player(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!isIdValid(player)) return;

        // Actual building where guard is
        obj_id prisonBuilding = obj_id.getObjId(1177465L); // Fett's Palace example
        obj_id freedomCell = getCellId(prisonBuilding, "hall7"); // guard's cell

        if (!isIdValid(freedomCell))
        {
            sendSystemMessageTestingOnly(player, "Error: could not find hall7 cell!");
            return;
        }

        // Warp the player inside the guard cell
        warpPlayer(player, "tatooine", 1f, 0.2f, 83f, freedomCell, 0, 0, 0f, "", false);
        sendSystemMessageTestingOnly(player, "The prison guard frees you and escorts you outside.");
    }

    // Bribe 50/50 chance
    public void prison_guard_action_bribe(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!isIdValid(player)) return;

        int roll = rand(0, 1); // 0 or 1
        if (roll == 1)
        {
            sendSystemMessageTestingOnly(player, "Bribe successful!");
            prison_guard_action_free_player(player, npc);
        }
        else
        {
            sendSystemMessageTestingOnly(player, "Bribe failed!");
            prison_guard_action_send_to_sarlaac(player, npc);
        }
    }

    // --- Branch 1: player chooses Persuade or Bribe ---
    public int prison_guard_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("persuade"))
        {
            // NPC reacts and performs luck roll
            prison_guard_action_persuade(player, npc);

            // End conversation
            utils.removeScriptVar(player, "conversation.prison_guard_conversation.branchId");
            final string_id message = new string_id(c_stringFile, "npc_react_persuade");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        else if (response.equals("bribe_18k"))
        {
            final int BRIBE_COST = 18000;
            money.requestPayment(player, npc, BRIBE_COST, null, null); // deduct cost

            // Perform 50/50 chance
            prison_guard_action_bribe(player, npc);

            utils.removeScriptVar(player, "conversation.prison_guard_conversation.branchId");
            final string_id message = new string_id(c_stringFile, "npc_accept_bribe");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

    // --- Standard SWG NPC hooks ---
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
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        final int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        menuInfoData.setServerNotify(false);
        return SCRIPT_CONTINUE;
    }

    public int OnStartNpcConversation(obj_id npc, obj_id player) throws InterruptedException
    {
        if (ai_lib.isInCombat(npc) || ai_lib.isInCombat(player))
            return SCRIPT_OVERRIDE;

        faceTo(npc, player);

        final string_id message = new string_id(c_stringFile, "npc_intro");
        final string_id[] responses = new string_id[]
                {
                        new string_id(c_stringFile, "persuade"),
                        new string_id(c_stringFile, "bribe_18k")
                };

        utils.setScriptVar(player, "conversation.prison_guard_conversation.branchId", 1);
        npcStartConversation(player, npc, "prison_guard_conversation", message, responses);

        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("prison_guard_conversation"))
            return SCRIPT_CONTINUE;

        final int branchId = utils.getIntScriptVar(player, "conversation.prison_guard_conversation.branchId");
        if (branchId == 1)
            return prison_guard_handleBranch1(player, npc, response);

        return SCRIPT_CONTINUE;
    }
}
