package script.conversation;

import script.library.*;
import script.*;

public class rtp_luke_main extends script.base_script
{
    public rtp_luke_main()
    {
    }
    public static String c_stringFile = "conversation/rtp_luke_main";
    public boolean rtp_luke_main_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean rtp_luke_main_condition_rtp_luke_01_active(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "rtp_luke_01");
    }
    public boolean rtp_luke_main_condition_rtp_luke_01_complete(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "rtp_luke_01", "rtp_luke_01_02") || groundquests.hasCompletedQuest(player, "rtp_luke_01");
    }
    public boolean rtp_luke_main_condition_rtp_luke_02_active(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "rtp_luke_02");
    }
    public boolean rtp_luke_main_condition_rtp_luke_03_complete(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "rtp_luke_03", "rtp_luke_03_02") || groundquests.hasCompletedQuest(player, "rtp_luke_03");
    }
    public boolean rtp_luke_main_condition_gifts_complete(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "jedi_gifts_1")
                || groundquests.hasCompletedQuest(player, "jedi_gifts_2");
    }
    public boolean rtp_luke_main_condition_holocron_power_complete(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_holocron_power");
    }
    public boolean rtp_luke_main_condition_rtp_luke_academy_01_complete(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_jedi_academy_luke1");
    }
    public boolean rtp_luke_main_condition_rtp_luke_academy_02_complete(obj_id player, obj_id npc) throws InterruptedException
    {
        return skill.hasSkill(player, "stardust_jedi_elder");
    }
    public boolean rtp_luke_main_condition_rtp_luke_03_active(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "rtp_luke_03");
    }
    public boolean rtp_luke_main_condition_rtp_luke_02_complete(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "rtp_luke_02", "rtp_luke_02_02") || groundquests.hasCompletedQuest(player, "rtp_luke_02");
    }
    public boolean rtp_luke_main_condition_completedDodonna(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "rtp_dodonna_03")
                || hasSkill(player, "stardust_jedi_elder");
    }
    public boolean rtp_luke_main_condition_notRebel(obj_id player, obj_id npc) throws InterruptedException
    {
        String playerFaction = factions.getFaction(player);
        // Block (return true) only if NOT Rebel and NOT blueGlowie
        return !("Rebel".equals(playerFaction) || hasSkill(player, "stardust_jedi_elder"));
    }
    public boolean rtp_luke_main_condition_isJedi(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasSkill(player,"class_forcesensitive_phase1_novice");
    }
    public void rtp_luke_main_action_rtp_luke_01_granted(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.grantQuest(player, "rtp_luke_01");
    }
    public void rtp_luke_main_action_rtp_luke_01_signal(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "rtp_luke_01_02");
    }
    public void rtp_luke_main_action_rtp_luke_03_signal(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "rtp_luke_03_02");
    }
    public void rtp_luke_main_action_rtp_luke_02_signal(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "rtp_luke_02_02");
    }
    public void rtp_luke_main_action_sidious_grant(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.grantQuest(player, "stardust_holocron_sidious");
    }
    public void rtp_luke_main_action_rtp_luke_02_granted(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.grantQuest(player, "rtp_luke_02");
    }
    public void rtp_luke_main_action_rtp_luke_03_granted(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.grantQuest(player, "rtp_luke_03");
    }
    public void rtp_luke_main_action_academy_luke_01_granted(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.grantQuest(player, "stardust_jedi_academy_luke1");
    }
    public int rtp_luke_main_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_31"))
        {
            if (rtp_luke_main_condition__defaultCondition(player, npc))
            {
                rtp_luke_main_action_rtp_luke_03_granted(player, npc);
                string_id message = new string_id(c_stringFile, "s_33");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_35"))
        {
            if (rtp_luke_main_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_37");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int rtp_luke_main_handleBranch9(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_22"))
        {
            if (rtp_luke_main_condition__defaultCondition(player, npc))
            {
                rtp_luke_main_action_rtp_luke_02_granted(player, npc);
                string_id message = new string_id(c_stringFile, "s_25");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_26"))
        {
            if (rtp_luke_main_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_30");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int rtp_luke_main_handleBranch13(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_36"))
        {
            if (rtp_luke_main_condition__defaultCondition(player, npc))
            {
                rtp_luke_main_action_rtp_luke_01_granted(player, npc);
                string_id message = new string_id(c_stringFile, "s_39");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_41"))
        {
            if (rtp_luke_main_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "s_45");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int rtp_luke_main_handleBranch20(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("luke_academy1"))
        {
            if (rtp_luke_main_condition_rtp_luke_academy_02_complete(player, npc))
            {
                string_id message = new string_id(c_stringFile, "luke_lesson3");
                groundquests.grantQuest(player, "jedi_gifts_1");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("luke_academy1"))
        {
            if (rtp_luke_main_condition_rtp_luke_academy_01_complete(player, npc))
            {
                string_id message = new string_id(c_stringFile, "luke_lesson2");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("luke_academy1"))
        {
            if (!rtp_luke_main_condition_rtp_luke_academy_01_complete(player, npc))
            {
                rtp_luke_main_action_academy_luke_01_granted(player, npc);
                string_id message = new string_id(c_stringFile, "luke_lesson1");
                utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
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
            detachScript(self, "conversation.rtp_luke_main");
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
        detachScript(self, "conversation.rtp_luke_main");
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

        // ---- SAFETY CHECK: in combat ----
        if (ai_lib.isInCombat(npc) || ai_lib.isInCombat(player)) {
            return SCRIPT_OVERRIDE;
        }

        // ---- 1. Imperial or non-Rebel blocker ----
        else if (rtp_luke_main_condition_notRebel(player, npc)) {
            chat.chat(npc, player, new string_id(c_stringFile, "s_44"));
            return SCRIPT_CONTINUE;
        }

        // ---- 2. Must have completed Dodonna ----
        else if (!rtp_luke_main_condition_completedDodonna(player, npc)) {
            chat.chat(npc, player, new string_id(c_stringFile, "s_46"));
            return SCRIPT_CONTINUE;
        }

        // ---- 3. Sith Wayfinder grant path (Luke1 + Power complete) ----
        else if (rtp_luke_main_condition_gifts_complete(player, npc)
                && rtp_luke_main_condition_holocron_power_complete(player, npc))
        {
            rtp_luke_main_action_sidious_grant(player, npc);
            chat.chat(npc, player, new string_id(c_stringFile, "s_sith_wayfinder"));
            return SCRIPT_CONTINUE;
        }

        // ---- 4. Luke 03 complete → branching Jedi path ----
        else if (rtp_luke_main_condition_rtp_luke_03_complete(player, npc)
                && !rtp_luke_main_condition_gifts_complete(player, npc))
        {
            rtp_luke_main_action_rtp_luke_03_signal(player, npc);
            string_id message = new string_id(c_stringFile, "s_9");

            boolean isJedi = rtp_luke_main_condition_isJedi(player, npc);

            if (isJedi) {
                string_id[] responses = { new string_id(c_stringFile, "luke_academy1") };
                utils.setScriptVar(player, "conversation.rtp_luke_main.branchId", 20);
                npcStartConversation(player, npc, "rtp_luke_main", message, responses);
            } else {
                chat.chat(npc, player, message);
                npcEndConversationWithMessage(player, message);
            }
            return SCRIPT_CONTINUE;
        }

        // ---- 5. Luke 03 active ----
        else if (rtp_luke_main_condition_rtp_luke_03_active(player, npc)) {
            chat.chat(npc, player, new string_id(c_stringFile, "s_29"));
            return SCRIPT_CONTINUE;
        }

        // ---- 6. Luke 02 complete → branching ----
        else if (rtp_luke_main_condition_rtp_luke_02_complete(player, npc)
                && !rtp_luke_main_condition_rtp_luke_03_complete(player, npc))
        {
            rtp_luke_main_action_rtp_luke_02_signal(player, npc);

            string_id message = new string_id(c_stringFile, "s_28");
            string_id[] responses = {
                    new string_id(c_stringFile, "s_31"),
                    new string_id(c_stringFile, "s_35")
            };

            utils.setScriptVar(player, "conversation.rtp_luke_main.branchId", 5);
            npcStartConversation(player, npc, "rtp_luke_main", message, responses);
            return SCRIPT_CONTINUE;
        }

        // ---- 7. Luke 02 active ----
        else if (rtp_luke_main_condition_rtp_luke_02_active(player, npc)) {
            chat.chat(npc, player, new string_id(c_stringFile, "s_16"));
            return SCRIPT_CONTINUE;
        }

        // ---- 8. Luke 01 complete → branching ----
        else if (rtp_luke_main_condition_rtp_luke_01_complete(player, npc)
                && !rtp_luke_main_condition_rtp_luke_03_complete(player, npc))
        {
            rtp_luke_main_action_rtp_luke_01_signal(player, npc);

            string_id message = new string_id(c_stringFile, "s_20");
            string_id[] responses = {
                    new string_id(c_stringFile, "s_22"),
                    new string_id(c_stringFile, "s_26")
            };

            utils.setScriptVar(player, "conversation.rtp_luke_main.branchId", 9);
            npcStartConversation(player, npc, "rtp_luke_main", message, responses);
            return SCRIPT_CONTINUE;
        }

        // ---- 9. Luke 01 active ----
        else if (rtp_luke_main_condition_rtp_luke_01_active(player, npc)) {
            chat.chat(npc, player, new string_id(c_stringFile, "s_32"));
            return SCRIPT_CONTINUE;
        }

        // ---- 10. Default conversation root ----
        else if (rtp_luke_main_condition__defaultCondition(player, npc)
                && !rtp_luke_main_condition_rtp_luke_03_complete(player, npc))
        {

            string_id message = new string_id(c_stringFile, "s_34");
            string_id[] responses = {
                    new string_id(c_stringFile, "s_36"),
                    new string_id(c_stringFile, "s_41")
            };

            utils.setScriptVar(player, "conversation.rtp_luke_main.branchId", 13);
            npcStartConversation(player, npc, "rtp_luke_main", message, responses);
            return SCRIPT_CONTINUE;
        }

        // ---- 11. Fail-safe ----
        else {
            chat.chat(npc, "May the force be with you.");
            return SCRIPT_CONTINUE;
        }
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("rtp_luke_main"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.rtp_luke_main.branchId");
        if (branchId == 5 && rtp_luke_main_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 9 && rtp_luke_main_handleBranch9(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 13 && rtp_luke_main_handleBranch13(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 20 && rtp_luke_main_handleBranch20(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.rtp_luke_main.branchId");
        return SCRIPT_CONTINUE;
    }
}
