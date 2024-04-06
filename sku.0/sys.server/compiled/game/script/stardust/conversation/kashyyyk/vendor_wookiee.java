package script.stardust.conversation.kashyyyk;

import script.*;
import script.library.ai_lib;
import script.library.chat;
import script.library.factions;
import script.library.utils;

import java.util.HashSet;

public class vendor_wookiee extends base_script
{
    public vendor_wookiee()
    {
    }
    public static String c_stringFile = "conversation/vendor_wookiee";
    public boolean vendor_wookiee_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean wookieeFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float wookieeFaction = factions.getFactionStanding(player, "kashyyyk_resistance");
        int pSpecies = getSpecies(player);

        if (pSpecies == SPECIES_WOOKIEE)
        {
            return true;
        }
        else if (wookieeFaction >= 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void vendor_wookiee_action_showTokenVendorUI(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
        return;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if ((!isTangible(self)) || (isPlayer(self)))
        {
            detachScript(self, "conversation.vendor_wookiee");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setName(self, "Wawaatt (Wookiee Trade Guild Representative)");
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
        detachScript(self, "conversation.vendor_wookiee");
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
        if (wookieeFriend_condition(player, npc))
        {
            vendor_wookiee_action_showTokenVendorUI(player, npc);
            string_id message = new string_id(c_stringFile, "speaks_shyriiwook");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        else if (vendor_wookiee_condition__defaultCondition(player, npc))
        {
            string_id message = new string_id(c_stringFile, "speaks_shyriiwook");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "*Speaks Shyriiwook*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("vendor_wookiee"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.vendor_wookiee.branchId");
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.vendor_wookiee.branchId");
        return SCRIPT_CONTINUE;
    }
}
