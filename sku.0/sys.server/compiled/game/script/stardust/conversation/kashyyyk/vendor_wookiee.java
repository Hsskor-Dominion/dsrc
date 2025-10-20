package script.stardust.conversation.kashyyyk;

import script.*;
import script.library.ai_lib;
import script.library.chat;
import script.library.factions;
import script.library.utils;
import script.library.groundquests;

public class vendor_wookiee extends script.base_script
{
    public static final String c_stringFile = "conversation/vendor_wookiee";
    public static final String OBJ_VAR_BASE = "vendor_wookiee.";

    public vendor_wookiee() { }

    // --- Conditions ---
    public boolean vendor_wookiee_defaultCondition(obj_id player, obj_id npc)
    {
        return true;
    }

    public boolean wookieeFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float wookieeFaction = factions.getFactionStanding(player, "kashyyyk_resistance");
        int pSpecies = getSpecies(player);
        return (pSpecies == SPECIES_WOOKIEE || wookieeFaction >= 1);
    }

    // --- Actions ---
    public void vendor_wookiee_action_showVendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }

    public void vendor_wookiee_action_offerMission(obj_id player, obj_id npc) throws InterruptedException
    {
        //grant mission or start quest
        groundquests.grantQuest(player, "stardust_trandoshan_hunt");
    }

    // --- Conversation branches ---
    public int vendor_wookiee_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("vendor_wookiee_trade"))
        {
            if (wookieeFriend_condition(player, npc))
            {
                //Player is trusted — open vendor
                vendor_wookiee_action_showVendor(player, npc);
                string_id message = new string_id(c_stringFile, "offer_trade");
                utils.removeScriptVar(player, "conversation.vendor_wookiee.branchId");
                npcEndConversationWithMessage(player, message);
            }
            else
            {
                //Player not trusted — growl and end conversation
                string_id message = new string_id(c_stringFile, "deny_trade");
                utils.removeScriptVar(player, "conversation.vendor_wookiee.branchId");
                npcEndConversationWithMessage(player, message);
            }
            return SCRIPT_CONTINUE;
        }

        if (response.equals("vendor_wookiee_mission"))
        {
            vendor_wookiee_action_offerMission(player, npc);
            string_id message = new string_id(c_stringFile, "offer_mission");
            utils.removeScriptVar(player, "conversation.vendor_wookiee.branchId");
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
            responses[0] = new string_id(c_stringFile, "vendor_wookiee_trade");
            responses[1] = new string_id(c_stringFile, "vendor_wookiee_mission");

            utils.setScriptVar(player, "conversation.vendor_wookiee.branchId", 1);

            npcStartConversation(player, npc, "vendor_wookiee", greeting, responses);
            return SCRIPT_CONTINUE;
    }

    // --- Object setup ---
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Rorwaa (Wookiee Trade Guild Representative)");
        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Rorwaa (Wookiee Trade Guild Representative)");
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
        if (!conversationId.equals("vendor_wookiee"))
        {
            return SCRIPT_CONTINUE;
        }

        int branchId = utils.getIntScriptVar(player, "conversation.vendor_wookiee.branchId");

        if (branchId == 1 && vendor_wookiee_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error: fell through conversation branches.");
        utils.removeScriptVar(player, "conversation.vendor_wookiee.branchId");
        return SCRIPT_CONTINUE;
    }

}