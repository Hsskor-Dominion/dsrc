package script.conversation;


import script.*;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.create;

public class gmf_the_ghoul extends script.base_script
{
    public gmf_the_ghoul()
    {
    }
    public static final String c_stringFile = "conversation/gmf_the_ghoul";

    private static final String REQUIRED_SKILL = "social_language_basic_comprehend";
    private static final String GMF_QUEST = "the_ghoul";

    public boolean ghoul_defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean grave2_haunted(obj_id player, obj_id npc) throws InterruptedException
      {
        if (!hasObjVar(player, "the_ghoul.quest_state"))
        {
            return false;
        }
        return getIntObjVar(player, "the_ghoul.quest_state") == 3;
    }
    public boolean ghoul_haunted(obj_id player, obj_id npc) throws InterruptedException
      {
        if (!hasObjVar(player, "the_ghoul.quest_state"))
        {
            return false;
        }
        return getIntObjVar(player, "the_ghoul.quest_state") == 4;
    }
    public boolean grave2_completeCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!hasObjVar(player, "the_ghoul.quest_state"))
        {
            return false;
        }
        return getIntObjVar(player, "the_ghoul.quest_state") == 5;
    }
    /*
        This branch is our intro conversation.
        So this is the first dialog shown to the player
    */
    public int ghoul_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_why"))
        {
            string_id message = new string_id(c_stringFile, "npc_sacrifice_for_the_cult");
 	    int numberOfResponses = 3;
	    int responseIndex = 0;
	    string_id responses[] = new string_id[numberOfResponses];
	    responses[responseIndex++] = new string_id(c_stringFile, "player_why_sacrifice");
	    responses[responseIndex++] = new string_id(c_stringFile, "player_self_sacrifice");
	    responses[responseIndex++] = new string_id(c_stringFile, "player_organic_offering");
            utils.setScriptVar(player, "conversation.ghoul.branchId", 2);
            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            return SCRIPT_CONTINUE;
        }
	if (response.equals("player_peace"))
        {
            string_id message = new string_id(c_stringFile, "npc_aggro");
            npcEndConversationWithMessage(player, message);
	    setObjVar(player, "the_ghoul.quest_state", 4);
            //need to add script to make NPC engage aggro
            return SCRIPT_CONTINUE;
        }
	if (response.equals("player_destroy"))
        {
            string_id message = new string_id(c_stringFile, "npc_aggro2");
            npcEndConversationWithMessage(player, message);
	    setObjVar(player, "the_ghoul.quest_state", 4);
            //need to add script to make NPC engage aggro
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int ghoul_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_why_sacrifice"))
        {
            string_id message = new string_id(c_stringFile, "npc_release_me");

            int numberOfResponses = 3;

            int responseIndex = 0;

            string_id responses[] = new string_id[numberOfResponses];

	    responses[responseIndex++] = new string_id(c_stringFile, "player_why_sacrifice");
	    responses[responseIndex++] = new string_id(c_stringFile, "player_self_sacrifice");
	    responses[responseIndex++] = new string_id(c_stringFile, "player_organic_offering");
            utils.setScriptVar(player, "conversation.ghoul.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
	if (response.equals("player_self_sacrifice"))
        {
            string_id message = new string_id(c_stringFile, "npc_aggro2");
            npcEndConversationWithMessage(player, message);
	    setObjVar(player, "the_ghoul.quest_state", 5);
            //need to add script to make NPC engage aggro
            return SCRIPT_CONTINUE;
        }
	if (response.equals("player_organic_offering"))
                {
            string_id message = new string_id(c_stringFile, "npc_good");
            npcEndConversationWithMessage(player, message);
	    setObjVar(player, "the_ghoul.quest_state", 5);
            //need to add script to kill npc, cost resources
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "The ghoul");
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
        if (grave2_completeCondition(player, npc))
        {
            string_id message = new string_id(c_stringFile, "npc_peace");
        }
	else if (ghoul_haunted(player, npc)) 
        {
            //Npc message
            string_id message = new string_id(c_stringFile, "npc_aggro");

            //How many responses are there?
            int numberOfResponses = 3;

            //Don't update this manually
            int responseIndex = 0;

            //Prepare the responses
            string_id responses[] = new string_id[numberOfResponses];

            //Ensure numberOfResponses equals the amount of responses below
	    responses[responseIndex++] = new string_id(c_stringFile, "player_why");
	    responses[responseIndex++] = new string_id(c_stringFile, "player_peace");
	    responses[responseIndex++] = new string_id(c_stringFile, "player_destroy");

            //Set the branch that we want to go to, to check for the above responses
            utils.setScriptVar(player, "conversation.ghoul.branchId", 1);

            npcStartConversation(player, npc, "ghoul", message, responses);
        }
        else if (grave2_haunted(player, npc))
        {
            string_id message = new string_id(c_stringFile, "npc_i_have_risen");
            int numberOfResponses = 3;
            int responseIndex = 0;
            string_id responses[] = new string_id[numberOfResponses];
            responses[responseIndex++] = new string_id(c_stringFile, "player_why");
	    responses[responseIndex++] = new string_id(c_stringFile, "player_peace");
	    responses[responseIndex++] = new string_id(c_stringFile, "player_destroy");
            utils.setScriptVar(player, "conversation.ghoul.branchId", 1);
            npcStartConversation(player, npc, "ghoul", message, responses);
        }
        else if (ghoul_defaultCondition(player, npc))
        {
            string_id message = new string_id(c_stringFile, "npc_ignore");
            int numberOfResponses = 1;
            int responseIndex = 0;
            string_id responses[] = new string_id[numberOfResponses];
            utils.setScriptVar(player, "conversation.ghoul.branchId", 1);

            npcStartConversation(player, npc, "ghoul", message, responses);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("ghoul"))
        {
            return SCRIPT_CONTINUE;
        }
        int branchId = utils.getIntScriptVar(player, "conversation.ghoul.branchId");
        if (branchId == 1 && ghoul_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 2 && ghoul_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.ghoul.branchId");
        return SCRIPT_CONTINUE;
    }
}
