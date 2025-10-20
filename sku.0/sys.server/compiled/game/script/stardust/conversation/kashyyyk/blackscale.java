package script.stardust.conversation.kashyyyk;

import script.*;
import script.library.ai_lib;
import script.library.chat;
import script.library.factions;
import script.library.utils;
import script.library.groundquests;

public class blackscale extends script.base_script
{
    public static final String c_stringFile = "conversation/blackscale";
    public static final String OBJ_VAR_BASE = "blackscale.";

    public blackscale() { }

    // --- Conditions ---
    public boolean blackscale_defaultCondition(obj_id player, obj_id npc)
    {
        return true;
    }

    public boolean trandoshanFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float trandoshanFaction = factions.getFactionStanding(player, "hsskor");
        int pSpecies = getSpecies(player);
        return (pSpecies == SPECIES_TRANDOSHAN || trandoshanFaction >= 1);
    }

    // --- Actions ---
    public void blackscale_action_showVendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }

    public void blackscale_action_offerMission(obj_id player, obj_id npc) throws InterruptedException
    {
        //grant mission or start quest
        groundquests.grantQuest(player, "stardust_wookiee_hunt");
    }

    // --- Conversation branches ---
    public int blackscale_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("blackscale_trade"))
        {
            if (trandoshanFriend_condition(player, npc))
            {
                //Player is trusted — open vendor
                blackscale_action_showVendor(player, npc);
                string_id message = new string_id(c_stringFile, "offer_trade");
                utils.removeScriptVar(player, "conversation.blackscale.branchId");
                npcEndConversationWithMessage(player, message);
            }
            else
            {
                //Player not trusted — growl and end conversation
                string_id message = new string_id(c_stringFile, "deny_trade");
                utils.removeScriptVar(player, "conversation.blackscale.branchId");
                npcEndConversationWithMessage(player, message);
            }
            return SCRIPT_CONTINUE;
        }

        if (response.equals("blackscale_mission"))
        {
            blackscale_action_offerMission(player, npc);
            string_id message = new string_id(c_stringFile, "offer_mission");
            utils.removeScriptVar(player, "conversation.blackscale.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

    // --- Conversation start ---
    public int OnStartNpcConversation(obj_id npc, obj_id player) throws InterruptedException
    {
        if (ai_lib.isInCombat(npc) || ai_lib.isInCombat(player))
        {
            return SCRIPT_OVERRIDE;
        }

        faceTo(npc, player);

        string_id greeting = new string_id(c_stringFile, "greet_friend");

        string_id[] responses = new string_id[2];
        responses[0] = new string_id(c_stringFile, "blackscale_trade");
        responses[1] = new string_id(c_stringFile, "blackscale_mission");

        utils.setScriptVar(player, "conversation.blackscale.branchId", 1);

        npcStartConversation(player, npc, "blackscale", greeting, responses);
        return SCRIPT_CONTINUE;
    }

    // --- Object setup ---
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Skrisst (Hsskor Dominion Representative)");
        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Skrisst (Hsskor Dominion Representative)");
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        menuInfoData.setServerNotify(false);
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("blackscale"))
        {
            return SCRIPT_CONTINUE;
        }

        int branchId = utils.getIntScriptVar(player, "conversation.blackscale.branchId");

        if (branchId == 1 && blackscale_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error: fell through conversation branches.");
        utils.removeScriptVar(player, "conversation.blackscale.branchId");
        return SCRIPT_CONTINUE;
    }

}