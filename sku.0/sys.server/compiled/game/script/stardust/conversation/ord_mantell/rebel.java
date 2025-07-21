package script.stardust.conversation.ord_mantell;

import script.*;
import script.library.*;

public class rebel extends base_script
{
    public rebel()
    {
    }
    public static String c_stringFile = "conversation/freelance";
    public boolean freelance_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public void pilot_quest_rebel(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "newbie_gendra_rebel_pilot");
    }
    public void freelance_action_leaveStation1(obj_id player, obj_id npc) throws InterruptedException
    {
        string_id stfPrompt = new string_id("npe", "exit_station_prompt");
        string_id stfTitle = new string_id("npe", "exit_station");
        String prompt = utils.packStringId(stfPrompt);
        String title = utils.packStringId(stfTitle);
        int pid = sui.msgbox(player, player, prompt, sui.OK_CANCEL, title, 0, "handTransfer");
    }
    public int freelance_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_planet"))
        {
            final string_id message = new string_id(c_stringFile, "npc_choose_planet");
            final string_id[] responses = new string_id[3];
            responses[0] = new string_id(c_stringFile, "choose_tatooine");
            responses[1] = new string_id(c_stringFile, "choose_naboo");
            responses[2] = new string_id(c_stringFile, "choose_corellia");

            utils.setScriptVar(player, "conversation.freelance_conversation.branchId", 2);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int freelance_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        string_id message = new string_id(c_stringFile, "travel");
        if (response.equals("choose_tatooine"))
        {
            setObjVar(player, "stardust_espa", 1);
        }
        else if (response.equals("choose_naboo"))
        {
            setObjVar(player, "stardust_moenia", 1);
        }
        else if (response.equals("choose_corellia"))
        {
            setObjVar(player, "stardust_tyrena", 1);
        }
        else
        {
            return SCRIPT_DEFAULT;
        }

        pilot_quest_rebel(player, npc);
        freelance_action_leaveStation1(player, npc);
        utils.removeScriptVar(player, "conversation.freelance_conversation.branchId");
        npcEndConversationWithMessage(player, message);
        return SCRIPT_CONTINUE;
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
        setName(self, "Imanuel 'The Bull' Doza (a New Republic Navy Recruiter)");

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

        if (freelance_condition__defaultCondition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final string_id[] responses = new string_id[1];
            responses[0] = new string_id(c_stringFile, "seek_planet");

            utils.setScriptVar(player, "conversation.freelance_conversation.branchId", 1);
            npcStartConversation(player, npc, "freelance_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks curiously*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("freelance_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.freelance_conversation.branchId");

        if (branchId == 1 && freelance_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && freelance_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.freelance_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
