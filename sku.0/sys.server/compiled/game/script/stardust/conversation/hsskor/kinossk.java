package script.stardust.conversation.hsskor;

import script.library.factions;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.*;
import script.*;

public class kinossk extends script.base_script
{
    public kinossk()
    {
    }
    public static final String c_stringFile = "conversation/kinossk";
    public static final String OBJ_VAR_BASE = "kinossk.";
    public static final String SCOREKEEPER_HERALD = OBJ_VAR_BASE + "herald";
    public boolean kinossk_defaultCondition()
    {
        return true;
    }
    public boolean kinossk_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public boolean kinossk_trandoshanFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float hsskorFaction = factions.getFactionStanding(player, "hsskor");
        return hsskorFaction >= 2500;
    }
    public boolean kinossk_medic_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_medic_phase1_novice");
    }
    public boolean kinossk_crafter_condition(obj_id npc, obj_id player)
    {
        return (hasSkill(player,"class_trader"));
    }
    public boolean kinossk_credits_condition(obj_id player, obj_id npc) throws InterruptedException
    {
            return (money.hasFunds(player, money.MT_TOTAL, smuggler.TIER_4_GENERIC_PVP_FRONT_COST));
    }
    public boolean kinossk_hasObjVar_condition(obj_id npc, obj_id player)
    {
        return (hasObjVar(player, SCOREKEEPER_HERALD));
    }
    public void kinossk_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        final dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void kinossk_entertainer_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_kinossk_craft");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void kinossk_medic_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_kinossk_heal");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void kinossk_prison_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        money.requestPayment(player, npc, smuggler.TIER_4_GENERIC_PVP_FRONT_COST, "none", null, true);
        groundquests.requestGrantQuest(player, "quest/stardust_kinossk_prison_break", true);
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
    public int kinossk_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("kinossk_intro_offer_hunter"))
        {

            final string_id message = new string_id(c_stringFile, "kinossk_judge");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "kinossk_hire_shaman");
            responses[responseIndex++] = new string_id(c_stringFile, "[persuasion]kinossk_craft");

            utils.setScriptVar(player, "conversation.kinossk_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("kinossk_intro_offer_prey"))
        {

            final string_id message = new string_id(c_stringFile, "kinossk_seeks_herald_of_scorekeeper");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "kinossk_fence");
            responses[responseIndex++] = new string_id(c_stringFile, "kinossk_sign_up_for_prison");

            utils.setScriptVar(player, "conversation.kinossk_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int kinossk_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("kinossk_hire_shaman"))
        {
            if (kinossk_medic_condition(npc, player))
            {
                groundquests.grantQuest(player, "kinossk_heal");
                final string_id message = new string_id(c_stringFile, "kinossk_offer_contract");

                utils.removeScriptVar(player, "conversation.kinossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);
                //setObjVar(player, "herald");
                //should this be SCOREKPEER_HERALD? Not sure about how to do this one.

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "kinossk_seeks_medic");

                utils.removeScriptVar(player, "conversation.kinossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("[persuasion]kinossk_craft"))
        {
            if (kinossk_crafter_condition(npc, player))
            {
                groundquests.grantQuest(player, "kinossk_craft");
                final string_id message = new string_id(c_stringFile, "kinossk_requests_crafting");

                utils.setScriptVar(player, "conversation.kinossk_conversation.branchId", 4);
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "kinossk_finds_you_unworthy");

                utils.removeScriptVar(player, "conversation.kinossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int kinossk_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("kinossk_fence"))
        {
        if (kinossk_trandoshanFriend_condition(player, npc))
        {
            final string_id message = new string_id(c_stringFile, "kinossk_finds_you_worthy");

            kinossk_action_vendor(player, npc);

            utils.removeScriptVar(player, "conversation.kinossk_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else
        {
            final string_id message = new string_id(c_stringFile, "kinossk_finds_you_lack_jagganath");

            utils.removeScriptVar(player, "conversation.kinossk_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
    }
        else if (response.equals("kinossk_sign_up_for_prison"))
        {
            if (kinossk_credits_condition(player, npc))
            {
                kinossk_prison_quest(player, npc);
                final string_id message = new string_id(c_stringFile, "kinossk_places_your_bounty");

                utils.removeScriptVar(player, "conversation.kinossk_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "kinossk_finds_you_lack_credits");

                utils.removeScriptVar(player, "conversation.kinossk_conversation.branchId");
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

        setName(self, "Kinossk (Trade Union Manager)");

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);

        setName(self, "Kinossk (Trade Union Manager)");

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

        if (kinossk_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "kinossk_intro");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "kinossk_intro_offer_hunter");
            responses[responseIndex++] = new string_id(c_stringFile, "kinossk_intro_offer_prey");

            utils.setScriptVar(player, "conversation.kinossk_conversation.branchId", 1);

            npcStartConversation(player, npc, "kinossk_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "SEy sasssass, sssaklkssssktskyyslk");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("kinossk_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.kinossk_conversation.branchId");

        if (branchId == 1 && kinossk_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && kinossk_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && kinossk_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.kinossk_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}