package script.stardust.conversation.hsskor;

import script.library.ai_lib;
import script.library.chat;
import script.library.npe;
import script.library.utils;
import script.library.*;
import script.*;

import java.util.HashSet;

public class professor_snoak extends script.base_script
{
    public professor_snoak()
    {
    }
    public static String c_stringFile = "conversation/professor_snoak";
    public boolean professor_snoak_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean professor_snoak_condition_hasBeenBefore(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasObjVar(player, "npe.snoak");
    }
    public static void snoakNpcVendor(obj_id player, obj_id npc) throws InterruptedException
    {
        String[] options = new String[2];
        string_id[] items = new string_id[2];
        items[0] = new string_id("npe", "store_item3");
        items[1] = new string_id("npe", "store_item1");
        for (int i = 0; i < items.length; i++)
        {
            options[i] = utils.packStringId(items[i]);
        }
        sui.showSUIPage(sui.listbox(
                npc,
                player,
                utils.packStringId(new string_id("npe", "choose_a_beast_starter")),
                sui.OK_CANCEL,
                utils.packStringId(new string_id("npe", "beast_research")),
                options,
                "npeHandleBuy",
                false,
                false
        ));
    }
    public void professor_snoak_action_startShop(obj_id player, obj_id npc) throws InterruptedException
    {
        snoakNpcVendor(player, npc);
        if (!hasObjVar(player, "npe.snoak"))
        {
            setObjVar(player, "npe.snoak", 1);
        }
    }
    public void professor_snoak_action_facePlayer(obj_id player, obj_id npc) throws InterruptedException
    {
        faceTo(npc, player);
    }
    public int professor_snoak_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_want_to_be_very_best"))
        {
            if (professor_snoak_condition__defaultCondition(player, npc))
            {
                string_id message = new string_id(c_stringFile, "snoak_like_no_one_ever_was");
                int numberOfResponses = 0;
                boolean hasResponse = false;
                boolean hasResponse0 = false;
                if (professor_snoak_condition__defaultCondition(player, npc))
                {
                    ++numberOfResponses;
                    hasResponse = true;
                    hasResponse0 = true;
                }
                if (hasResponse)
                {
                    int responseIndex = 0;
                    string_id responses[] = new string_id[numberOfResponses];
                    if (hasResponse0)
                    {
                        responses[responseIndex++] = new string_id(c_stringFile, "player_choose_to_tame_creatures_as_their_cause");
                    }
                    utils.setScriptVar(player, "conversation.professor_snoak.branchId", 2);
                    npcSpeak(player, message);
                    npcSetConversationResponses(player, responses);
                }
                else 
                {
                    utils.removeScriptVar(player, "conversation.professor_snoak.branchId");
                    npcEndConversationWithMessage(player, message);
                }
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int professor_snoak_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_choose_to_tame_creatures_as_their_cause"))
        {
            if (professor_snoak_condition__defaultCondition(player, npc))
            {
                professor_snoak_action_startShop(player, npc);//ideally, this would offer 3 starter creatures instead
                string_id message = new string_id(c_stringFile, "snoak_they_choose_you");
                utils.removeScriptVar(player, "conversation.professor_snoak.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int professor_snoak_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_knows_its_their_destiny"))
        {
            if (professor_snoak_condition__defaultCondition(player, npc))
            {
                professor_snoak_action_startShop(player, npc);
                string_id message = new string_id(c_stringFile, "snoak_gotta_tame_them_all");
                utils.removeScriptVar(player, "conversation.professor_snoak.branchId");
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
            detachScript(self, "conversation.professor_snoak");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Professor Sn'Oak (Beast Researcher)");
        attachScript(self, "npe.npc_vendor");
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Professor Sn'Oak (Beast Researcher)");
        attachScript(self, "npe.npc_vendor");
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
        detachScript(self, "conversation.professor_snoak");
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
        if (!professor_snoak_condition_hasBeenBefore(player, npc))
        {
            professor_snoak_action_facePlayer(player, npc);
            string_id message = new string_id(c_stringFile, "snoak_seeks_creature_trainers_and_researchers");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (professor_snoak_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            if (hasResponse)
            {
                int responseIndex = 0;
                string_id responses[] = new string_id[numberOfResponses];
                if (hasResponse0)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "player_want_to_be_very_best");
                }
                utils.setScriptVar(player, "conversation.professor_snoak.branchId", 1);
                npcStartConversation(player, npc, "professor_snoak", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (professor_snoak_condition_hasBeenBefore(player, npc))
        {
            professor_snoak_action_facePlayer(player, npc);
            string_id message = new string_id(c_stringFile, "snoak_talks_creature_index");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (professor_snoak_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            if (hasResponse)
            {
                int responseIndex = 0;
                string_id responses[] = new string_id[numberOfResponses];
                if (hasResponse0)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "player_knows_its_their_destiny");
                }
                utils.setScriptVar(player, "conversation.professor_snoak.branchId", 4);
                prose_package pp = new prose_package();
                pp.stringId = message;
                pp.actor.set(player);
                pp.target.set(npc);
                npcStartConversation(player, npc, "professor_snoak", null, pp, responses);
            }
            else 
            {
                prose_package pp = new prose_package();
                pp.stringId = message;
                pp.actor.set(player);
                pp.target.set(npc);
                chat.chat(npc, player, null, null, pp);
            }
            return SCRIPT_CONTINUE;
        }
        if (professor_snoak_condition__defaultCondition(player, npc))
        {
            string_id message = new string_id(c_stringFile, "snoak_seeks_research_assistant");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  All conditions for OnStartNpcConversation were false.");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("professor_snoak"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.professor_snoak.branchId");
        if (branchId == 1 && professor_snoak_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 2 && professor_snoak_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 4 && professor_snoak_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.professor_snoak.branchId");
        return SCRIPT_CONTINUE;
    }
}
