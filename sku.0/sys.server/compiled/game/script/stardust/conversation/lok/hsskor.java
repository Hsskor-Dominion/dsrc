package script.stardust.conversation.lok;

import script.*;
import script.library.*;

public class hsskor extends base_script
{
    public static final String c_stringFile = "conversation/hsskor";
    public static final String OBJ_VAR_BASE = "hsskor.";

    public hsskor() { }

    // --- Conditions ---
    public boolean hsskor_defaultCondition(obj_id player, obj_id npc)
    {
        return true;
    }

    // --- Actions ---
    public void hsskor_action_showVendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }

    public void hsskor_action_offerMissionNewbie(obj_id player, obj_id npc) throws InterruptedException
    {
        //grant mission or start quest
        //groundquests.grantQuest(player, "stardust_wookiee_hunt");
    }
    public void hsskor_action_offerMission(obj_id player, obj_id npc) throws InterruptedException
    {
        //grant mission or start quest
        //groundquests.grantQuest(player, "stardust_wookiee_hunt");
    }
    public boolean admiral_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasSkill(player, "stardust_admiral_imperial");
    }

    // --- Conversation branches ---
    public int hsskor_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("hsskor_report_to_duty"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_rank");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "hsskor_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "hsskor_mission");
            responses[responseIndex++] = new string_id(c_stringFile, "hsskor_info");

            utils.setScriptVar(player, "conversation.hsskor.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int hsskor_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        int playerLevel = getLevel(player);
        if (response.equals("hsskor_trade"))
        {
            hsskor_action_showVendor(player, npc);

            string_id message = new string_id(c_stringFile, "offer_trade");
            utils.removeScriptVar(player, "conversation.hsskor.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }

        if (response.equals("hsskor_mission"))
        {
            if (playerLevel >= 10)
            {
                hsskor_action_offerMission(player, npc);

                string_id message = new string_id(c_stringFile, "offer_mission");
                utils.removeScriptVar(player, "conversation.hsskor.branchId");
                npcEndConversationWithMessage(player, message);
            }
            else
            {
                hsskor_action_offerMissionNewbie(player, npc);

                string_id message = new string_id(c_stringFile, "offer_mission_newbie");
                utils.removeScriptVar(player, "conversation.hsskor.branchId");
                npcEndConversationWithMessage(player, message);
            }

            return SCRIPT_CONTINUE;
        }

        if (response.equals("hsskor_info"))
        {
            if (admiral_condition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "hsskor_info_point_to_thrawn");
                utils.removeScriptVar(player, "conversation.hsskor.branchId");
                npcEndConversationWithMessage(player, message);
            }
            else
            {
                string_id message = new string_id(c_stringFile, "hsskor_info_claw_squadron_not_admiral");
                utils.removeScriptVar(player, "conversation.hsskor.branchId");
                npcEndConversationWithMessage(player, message);
            }
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

        string_id greeting = new string_id(c_stringFile, "greet_recruit");

        string_id[] responses = new string_id[1];
        responses[0] = new string_id(c_stringFile, "hsskor_report_to_duty");

        utils.setScriptVar(player, "conversation.hsskor.branchId", 1);

        npcStartConversation(player, npc, "hsskor", greeting, responses);
        return SCRIPT_CONTINUE;
    }

    // --- Object setup ---
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Hsskor (96th Imperial Task Force Commander)");
        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Hsskor (96th Imperial Task Force Commander)");
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
        if (!conversationId.equals("hsskor"))
        {
            return SCRIPT_CONTINUE;
        }

        int branchId = utils.getIntScriptVar(player, "conversation.hsskor.branchId");

        if (branchId == 1 && hsskor_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 2 && hsskor_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error: fell through conversation branches.");
        utils.removeScriptVar(player, "conversation.hsskor.branchId");
        return SCRIPT_CONTINUE;
    }

}