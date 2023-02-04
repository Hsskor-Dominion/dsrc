package script.stardust.conversation.hsskor;

import script.library.factions;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.*;
import script.*;

public class cradossk extends script.base_script
{
    public cradossk()
    {
    }
    public static final String c_stringFile = "conversation/cradossk";
    public static final String OBJ_VAR_BASE = "cradossk.";
    public static final String SCOREKEEPER_HERALD = OBJ_VAR_BASE + "herald";
    public boolean cradossk_defaultCondition()
    {
        return true;
    }
    public boolean cradossk_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_trandoshan_comprehend");
    }
    public boolean cradossk_trandoshanFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float hsskorFaction = factions.getFactionStanding(player, "hsskor");
        return hsskorFaction >= 2500;
    }
    public boolean cradossk_hunter_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_bountyhunter_phase1_novice");
    }
    public boolean cradossk_scorekeeper_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_entertainer_phase1_novice");
    }
    public boolean cradossk_credits_condition(obj_id player, obj_id npc) throws InterruptedException
    {
            return (money.hasFunds(player, money.MT_TOTAL, smuggler.TIER_4_GENERIC_PVP_FRONT_COST));
    }
    public boolean cradossk_hasObjVar_condition(obj_id npc, obj_id player)
    {
        return hasObjVar(player, SCOREKEEPER_HERALD);
    }
    public void cradossk_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        final dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void cradossk_entertainer_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/cradossk_entertain");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void cradossk_bounty_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/cradossk_bounty");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void cradossk_arena_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        money.requestPayment(player, npc, smuggler.TIER_4_GENERIC_PVP_FRONT_COST, "none", null, true);
        groundquests.requestGrantQuest(player, "quest/cradossk_arena", true);
        int mission_bounty = 10000;
        int current_bounty = 0;
        mission_bounty += rand(1, 2000);
        if (hasObjVar(player, "bounty.amount"))
        {
            current_bounty = getIntObjVar(player, "bounty.amount");
        }
        current_bounty += mission_bounty;
        setObjVar(player, "bounty.amount", current_bounty);
        setObjVar(player, "smuggler.bounty", mission_bounty);
        setJediBountyValue(player, current_bounty);
        updateJediScriptData(player, "smuggler", 1);
    }
    public int cradossk_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("cradossk_intro_offer_hunter"))
        {

            final string_id message = new string_id(c_stringFile, "cradossk_judge");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "cradossk_hire_for_hunt");
            responses[responseIndex++] = new string_id(c_stringFile, "cradossk_fence");

            utils.setScriptVar(player, "conversation.cradossk_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("cradossk_intro_offer_prey"))
        {

            final string_id message = new string_id(c_stringFile, "cradossk_seeks_herald_of_scorekeeper");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "[persuasion]cradossk_entertain");
            responses[responseIndex++] = new string_id(c_stringFile, "cradossk_sign_up_for_arena");

            utils.setScriptVar(player, "conversation.cradossk_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int cradossk_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("cradossk_hire_for_hunt"))
        {
            if (cradossk_hunter_condition(npc, player))
            {
                groundquests.grantQuest(player, "cradossk_bounty");
                final string_id message = new string_id(c_stringFile, "cradossk_offer_hunt");

                utils.removeScriptVar(player, "conversation.cradossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);
                //setObjVar(player, "herald");
                //should this be SCOREKPEER_HERALD? Not sure about how to do this one.

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cradossk_seeks_bounty_hunter");

                utils.removeScriptVar(player, "conversation.cradossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("cradossk_fence"))
        {
            if (cradossk_trandoshanFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "cradossk_finds_you_worthy");

                cradossk_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.cradossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cradossk_finds_you_lack_jagganath");

                utils.removeScriptVar(player, "conversation.cradossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int cradossk_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("[persuasion]cradossk_entertain"))
        {
            if (cradossk_scorekeeper_condition(npc, player))
            {
                groundquests.grantQuest(player, "cradossk_entertain");
                final string_id message = new string_id(c_stringFile, "cradossk_requests_dance");

                utils.setScriptVar(player, "conversation.cradossk_conversation.branchId", 4);
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cradossk_finds_you_unworthy");

                utils.removeScriptVar(player, "conversation.cradossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("cradossk_sign_up_for_arena"))
        {
            if (cradossk_credits_condition(player, npc))
            {
                cradossk_arena_quest(player, npc);
                final string_id message = new string_id(c_stringFile, "cradossk_places_your_bounty");

                utils.removeScriptVar(player, "conversation.cradossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cradossk_finds_you_lack_credits");

                utils.removeScriptVar(player, "conversation.cradossk_conversation.branchId");
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

        setName(self, "Cradossk the Lesser (Trandoshan Clan Leader)");

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);

        setName(self, "Cradossk the Lesser (Bounty Hunter's Guild Leader)");

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

        // Since we can talk to the player, might as well face them.
        faceTo(npc, player);

        if (cradossk_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "cradossk_intro");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "cradossk_intro_offer_hunter");
            responses[responseIndex++] = new string_id(c_stringFile, "cradossk_intro_offer_prey");

            utils.setScriptVar(player, "conversation.cradossk_conversation.branchId", 1);

            npcStartConversation(player, npc, "cradossk_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "SEy sasssass, sssaklkssssktskyyslk");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("cradossk_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.cradossk_conversation.branchId");

        if (branchId == 1 && cradossk_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && cradossk_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && cradossk_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.cradossk_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
