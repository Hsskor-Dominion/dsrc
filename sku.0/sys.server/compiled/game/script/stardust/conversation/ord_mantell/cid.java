package script.stardust.conversation.ord_mantell;

import script.library.factions;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;
import script.library.*;
import script.*;

public class cid extends script.base_script
{
    public cid()
    {
    }
    public static final String c_stringFile = "conversation/cid";
    public static final String OBJ_VAR_BASE = "deathstar.";
    public static final String DEATHSTAR_PLANS = OBJ_VAR_BASE + "plans";
    public boolean cid_defaultCondition()
    {
        return true;
    }
    public boolean cid_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public boolean cid_intimidated_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        float intimidation = getEnhancedSkillStatisticModifierUncapped(player, "strength");
        return intimidation >= 10;
    }
    public boolean cid_commando_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_commando_phase1_novice");
    }
    public boolean cid_bountyhunter_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_bountyhunter_phase1_novice");
    }
    public boolean cid_scorekeeper_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_entertainer_phase1_novice");
    }
    public boolean cid_credits_condition(obj_id player, obj_id npc) throws InterruptedException
    {
            return (money.hasFunds(player, money.MT_TOTAL, smuggler.TIER_4_GENERIC_PVP_FRONT_COST));
    }
    public boolean cid_entertainer_quest_condition_playerFinishedMainTask(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isTaskActive(player, "stardust_entertain_cid", "talktocid");
    }
    public void cid_entertainer_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_entertain_cid");
    }
    public void cid_commando_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("stardust_commando_batch");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void cid_sells_info(obj_id player, obj_id npc) throws InterruptedException
    {
        money.requestPayment(player, npc, smuggler.TIER_4_GENERIC_PVP_FRONT_COST, "none", null, true);
    }
    public void cid_entertainer_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "cid_reward");
    }
    public int cid_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("cid_intro_offer_jobs"))
        {

            final string_id message = new string_id(c_stringFile, "cid_interviews");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "cid_apply_for_commando_mission");
            responses[responseIndex++] = new string_id(c_stringFile, "cid_apply_for_bounty_hunter_mission");
            responses[responseIndex++] = new string_id(c_stringFile, "cid_apply_for_entertainer_gig");

            utils.setScriptVar(player, "conversation.cid_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("cid_intro_offer_information_ent"))
        {

            final string_id message = new string_id(c_stringFile, "cid_ent_info");

            utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("cid_intro_offer_information"))
        {

            final string_id message = new string_id(c_stringFile, "cid_looks_for_insight");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "[intimidate]cid_gives_information");
            responses[responseIndex++] = new string_id(c_stringFile, "[persuasion]cid_sell_information");

            utils.setScriptVar(player, "conversation.cid_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("cid_intro_finished_work"))
        {

            final string_id message = new string_id(c_stringFile, "cid_i_knew_you_could_do_it");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "you_are_welcome");
            responses[responseIndex++] = new string_id(c_stringFile, "well_actually");

            utils.setScriptVar(player, "conversation.cid_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int cid_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("cid_apply_for_commando_mission"))
        {
            if (cid_commando_condition(npc, player))
            {
                cid_commando_quest(player, npc);
                final string_id message = new string_id(c_stringFile, "cid_offer_commando_mission");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cid_seeks_commando");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("cid_apply_for_bounty_hunter_mission"))
        {
            if (cid_bountyhunter_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "cid_go_see_cradossk");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cid_finds_you_lack_credibility");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("cid_apply_for_entertainer_gig"))
        {
            if (cid_scorekeeper_condition(npc, player))
            {
                cid_entertainer_quest(player, npc);
                final string_id message = new string_id(c_stringFile, "cid_hires_you_to_entertain");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cid_finds_you_lack_stage_presense");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int cid_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("[intimidate]cid_gives_information"))
        {
            if (cid_intimidated_condition(npc, player))
            {
                setObjVar(player, DEATHSTAR_PLANS, true);
                groundquests.sendSignal(player, "jasper_to_cid");
                final string_id message = new string_id(c_stringFile, "cid_talks_about_deathstar_plans");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cid_tires_of_you");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("[persuasion]cid_sell_information"))
        {
            if (cid_credits_condition(player, npc))
            {
                cid_sells_info(player, npc);
                setObjVar(player, DEATHSTAR_PLANS, true);
                groundquests.sendSignal(player, "jasper_to_cid");
                final string_id message = new string_id(c_stringFile, "cid_talks_about_deathstar_plans");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cid_finds_you_lack_credits");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }

    public int cid_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("you_are_welcome"))
        {
            if (cid_entertainer_quest_condition_playerFinishedMainTask(player, npc))
            {
                cid_entertainer_signalReward(player, npc);
                final string_id message = new string_id(c_stringFile, "cid_takes_a_cut");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "cid_increases_her_share");

                utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        else if (response.equals("well_actually"))
        {
            final string_id message = new string_id(c_stringFile, "cid_well_what");

            utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);

        setName(self, "cid");

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);

        setName(self, "Cid (Cantina Manager)");

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

        if (cid_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "cid_intro");
            final int numberOfResponses = 4;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "cid_intro_offer_jobs");
            responses[responseIndex++] = new string_id(c_stringFile, "cid_intro_offer_information_ent");
            responses[responseIndex++] = new string_id(c_stringFile, "cid_intro_offer_information");
            responses[responseIndex++] = new string_id(c_stringFile, "cid_intro_finished_work");

            utils.setScriptVar(player, "conversation.cid_conversation.branchId", 1);

            npcStartConversation(player, npc, "cid_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Want a job? Learn to speak basic.");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("cid_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.cid_conversation.branchId");

        if (branchId == 1 && cid_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && cid_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && cid_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && cid_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.cid_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}