package script.stardust.conversation.ord_mantell;

import script.library.factions;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.*;
import script.*;

public class bothan_spy2 extends script.base_script
{
    public bothan_spy2()
    {
    }
    public static final String c_stringFile = "conversation/bothan_spy2";
    public static final String OBJ_VAR_BASE = "deathstar.";
    public static final String DEATHSTAR_PLANS = OBJ_VAR_BASE + "plans";
    public boolean bothan_spy2_defaultCondition()
    {
        return true;
    }
    public boolean bothan_spy2_hasObjVar_condition(obj_id npc, obj_id player)
    {
        return hasObjVar(player, DEATHSTAR_PLANS);
    }

    public int bothan_spy2_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("bothan_spy2_intro_deathstar_plans"))
        {

            final string_id message = new string_id(c_stringFile, "bothan_spy2_conspires");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "[gossip]bothan_spy2_deathstar_plans");

            utils.setScriptVar(player, "conversation.bothan_spy2_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int bothan_spy2_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("[gossip]bothan_spy2_deathstar_plans"))
        {
            if (bothan_spy2_hasObjVar_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy2_talks_stardust");

                utils.removeScriptVar(player, "conversation.bothan_spy2_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                removeObjVar(player, DEATHSTAR_PLANS);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "bothan_spy2_changes_subject");

                utils.removeScriptVar(player, "conversation.bothan_spy2_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);

        setName(self, "an especially mysterious figure");

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);

        setName(self, "an especially mysterious figure");

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

        if (bothan_spy2_hasObjVar_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "bothan_spy2_intro");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "bothan_spy2_intro_deathstar_plans");

            utils.setScriptVar(player, "conversation.bothan_spy2_conversation.branchId", 1);

            npcStartConversation(player, npc, "bothan_spy2_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Go away");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("bothan_spy2_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.bothan_spy2_conversation.branchId");

        if (branchId == 1 && bothan_spy2_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && bothan_spy2_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.bothan_spy2_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}