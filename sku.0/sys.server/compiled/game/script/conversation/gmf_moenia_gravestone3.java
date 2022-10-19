package script.conversation;


import script.*;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.create;


public class gmf_moenia_gravestone3 extends script.base_script
{
    public gmf_moenia_gravestone3()
    {
    }
    public static final String c_stringFile = "conversation/gmf_moenia_gravetone3";

    private static final String REQUIRED_SKILL = "social_language_basic_comprehend";
    private static final String GMF_QUEST = "the_ghoul";

    public boolean grave3_defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
        //The player has completed the scavenger hunt
    public boolean grave3_taskCondition(obj_id player, obj_id npc) throws InterruptedException
      {
        if (!hasObjVar(player, "the_ghoul.quest_state"))
        {
            return false;
        }
        return getIntObjVar(player, "the_ghoul.quest_state") == 2;
    }
    public boolean grave3_haunted(obj_id player, obj_id npc) throws InterruptedException
      {
        if (!hasObjVar(player, "the_ghoul.quest_state"))
        {
            return false;
        }
        return getIntObjVar(player, "the_ghoul.quest_state") == 3;
    }
    public boolean grave3_completeCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!hasObjVar(player, "the_ghoul.quest_state"))
        {
            return false;
        }
        return getIntObjVar(player, "the_ghoul.quest_state") == 5;
    }
    public boolean grave3_onQuestCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        //Are they on the quest at all?
        return hasObjVar(player, "the_ghoul.quest_state");
    }
    /*
        This branch is our intro conversation.
        So this is the first dialog shown to the player
    */
    public int grave3_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_investigate"))
        {
            string_id message = new string_id(c_stringFile, "npc_the_dead_speak");
            npcEndConversationWithMessage(player, message);

            //we assign them the quest for now -this should be granted by the scroll purchased from the GMF vendor
            setObjVar(player, "the_ghoul.quest_state", 1);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int grave3_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_why"))
        {
            //Npc message
            string_id message = new string_id(c_stringFile, "npc_why_speak");

            //How many responses are there?
            int numberOfResponses = 1;

            //Don't update this manually
            int responseIndex = 0;

            //Prepare the responses
            string_id responses[] = new string_id[numberOfResponses];

            //Ensure numberOfResponses equals the amount of responses below
            responses[responseIndex++] = new string_id(c_stringFile, "player_good_question");

            //Set the branch that we want to go to, to check for the above responses
            utils.setScriptVar(player, "conversation.grave3.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        if (response.equals("player_trick_or_treat"))
        {
            string_id message = new string_id(c_stringFile, "npc_quit_wasting_my_time");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int grave3_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_good_question"))
        {
            string_id message = new string_id(c_stringFile, "npc_the_dead_speak");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int grave3_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_offer_tribute"))
        {
            string_id message = new string_id(c_stringFile, "npc_released");
 	    create.object("vendor_bm_2", getLocation(player));
            npcEndConversationWithMessage(player, message);
            setObjVar(player, "the_ghoul.quest_state", 3);
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Cursed Gravestone");
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
	if (grave3_completeCondition(player, npc)) 
        {
            string_id message = new string_id(c_stringFile, "npc_gravestone");
        }
	else if (grave3_haunted(player, npc)) 
        {
            //Npc message
            string_id message = new string_id(c_stringFile, "npc_released");

            //How many responses are there?
            int numberOfResponses = 1;

            //Don't update this manually
            int responseIndex = 0;

            //Prepare the responses
            string_id responses[] = new string_id[numberOfResponses];

            //Ensure numberOfResponses equals the amount of responses below
            responses[responseIndex++] = new string_id(c_stringFile, "player_trick_or_treat");

            //Set the branch that we want to go to, to check for the above responses
            utils.setScriptVar(player, "conversation.grave3.branchId", 2);

            npcStartConversation(player, npc, "grave3", message, responses);
        }
	else if (grave3_taskCondition(player, npc)) 
        {
            string_id message = new string_id(c_stringFile, "npc_released");
            int numberOfResponses = 1;
            int responseIndex = 0;
            string_id responses[] = new string_id[numberOfResponses];
            responses[responseIndex++] = new string_id(c_stringFile, "player_offer_tribute");
            utils.setScriptVar(player, "conversation.grave3.branchId", 4);
            npcStartConversation(player, npc, "grave3", message, responses);
        }
        else if (grave3_onQuestCondition(player, npc))
        {
            string_id message = new string_id(c_stringFile, "npc_i_told_you_to_get_the_stuff");
            int numberOfResponses = 2;
            int responseIndex = 0;
            string_id responses[] = new string_id[numberOfResponses];
            responses[responseIndex++] = new string_id(c_stringFile, "player_why");
            responses[responseIndex++] = new string_id(c_stringFile, "player_trick_or_treat");
            utils.setScriptVar(player, "conversation.grave3.branchId", 2);
            npcStartConversation(player, npc, "grave3", message, responses);
        }
        else if (grave3_defaultCondition(player, npc))
        {
            string_id message = new string_id(c_stringFile, "npc_the_gravestone");
            int numberOfResponses = 1;
            int responseIndex = 0;
            string_id responses[] = new string_id[numberOfResponses];
            responses[responseIndex++] = new string_id(c_stringFile, "player_investigate");
            utils.setScriptVar(player, "conversation.grave3.branchId", 1);

            npcStartConversation(player, npc, "grave3", message, responses);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("grave3"))
        {
            return SCRIPT_CONTINUE;
        }
        int branchId = utils.getIntScriptVar(player, "conversation.grave3.branchId");
        if (branchId == 1 && grave3_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 2 && grave3_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 3 && grave3_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 4 && grave3_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.grave3.branchId");
        return SCRIPT_CONTINUE;
    }
}
