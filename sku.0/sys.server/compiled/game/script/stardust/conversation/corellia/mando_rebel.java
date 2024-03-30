package script.stardust.conversation.corellia;

import script.library.*;
import script.library.factions;
import script.*;

public class mando_rebel extends script.base_script
{
    public mando_rebel()
    {
    }
    public static String c_stringFile = "conversation/mando_rebel";
    public boolean mando_rebel_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean mando_rebel_condition_playerCompletedCreed(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_mando_creed");
    }
    public boolean mando_rebel_condition_playerCompletedCrest(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_mando_crest");
    }
    public boolean mando_rebel_check_playerHasHelm(obj_id player) throws InterruptedException//not yet working
    {
        if (utils.playerHasItemByTemplate(player, "object/tangible/wearables/armor/bounty_hunter/armor_bounty_hunter_helmet.iff"))
        {
                return true;
        }
        return false;
    }
    public boolean mando_rebelFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float bhFaction = factions.getFactionStanding(player, "death_watch");
        if (bhFaction >= 1000)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public boolean mando_rebelAlly_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float bhFaction = factions.getFactionStanding(player, "death_watch");
        if (bhFaction >= 2500)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean mando_rebelMandalore_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float bhFaction = factions.getFactionStanding(player, "death_watch");
        if ((hasSkill(player,"faction_rank_mando_novice")) & (bhFaction >= 5000))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public boolean mando_rebel_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public void mando_rebel_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void mando_rebel_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "mando_rebel_reward");
    }
    public void mando_rebel_action_grantQuest1(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_mando_creed");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void mando_rebel_action_grantQuest2(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_mando_crest");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public void mando_rebel_action_grantQuest3(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/stardust_mando_craft");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int mando_rebel_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "trade");

            utils.setScriptVar(player, "conversation.mando_rebel_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_mandalore"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_training");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "become_mandalore");

            utils.setScriptVar(player, "conversation.mando_rebel_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_the_way"))
        {

            final string_id message = new string_id(c_stringFile, "s_257");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "s_259");
            responses[responseIndex++] = new string_id(c_stringFile, "mando_forge");

            utils.setScriptVar(player, "conversation.mando_rebel_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_to_leave_enclave"))
        {

            final string_id message = new string_id(c_stringFile, "npc_are_you_sure");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "confirm_leave_enclave");

            utils.setScriptVar(player, "conversation.mando_rebel_conversation.branchId", 5);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int mando_rebel_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("trade"))
        {
            if (mando_rebelFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "fence");
                mando_rebel_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_friend");

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int mando_rebel_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("become_mandalore"))
        {
            if (mando_rebelMandalore_condition(player, npc) && mando_rebel_condition_playerCompletedCreed(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "for_new_mandalore");
                sendSystemMessage(player, new string_id("stardust/mando_rank", "mando_master"));
                grantSkill(player, "faction_rank_mando_master");

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (mando_rebel_condition_playerCompletedCreed(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "for_mandalore");
                sendSystemMessage(player, new string_id("stardust/mando_rank", "mando_novice"));
                sendSystemMessage(player, new string_id("stardust/mando_rank", "mando_lang"));
                grantSkill(player, "faction_rank_mando");
                grantSkill(player, "faction_rank_mando_novice");
                grantSkill(player, "social_language_mando_speak");
                grantSkill(player, "social_language_mando_comprehend");

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (mando_rebelAlly_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "for_mandalore_foundling");
                sendSystemMessage(player, new string_id("stardust/mando_rank", "mando_lang"));
                grantSkill(player, "faction_rank_mando");
                grantSkill(player, "social_language_mando_speak");
                grantSkill(player, "social_language_mando_comprehend");

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (mando_rebel_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "s_273");

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int mando_rebel_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_259"))
        {
            if (mando_rebel_condition_playerCompletedCreed(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "earn_crest");
                mando_rebel_action_grantQuest2(player, npc);

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (mando_rebel_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "s_261");
                mando_rebel_action_grantQuest1(player, npc);
                revokeSkill(player, "faction_rank_mando_novice");

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("mando_forge"))
        {
            if (mando_rebel_condition_playerCompletedCreed(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "earn_forge");
                mando_rebel_action_grantQuest3(player, npc);

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (mando_rebel_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "s_261");

                utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int mando_rebel_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("confirm_leave_enclave"))
        {
            final string_id message = new string_id(c_stringFile, "npc_very_well");
            revokeSkill(player, "faction_rank_mando_novice");

            utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
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
        setName(self, "a Mandalorian mercencary");

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

        if (mando_rebel_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 5;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_mandalore");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_the_way");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_to_leave_enclave");

            utils.setScriptVar(player, "conversation.mando_rebel_conversation.branchId", 1);

            npcStartConversation(player, npc, "mando_rebel_conversation", message, responses);
            mando_rebel_action_signalReward(player, npc);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in Mando'a*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("mando_rebel_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.mando_rebel_conversation.branchId");

        if (branchId == 1 && mando_rebel_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && mando_rebel_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && mando_rebel_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && mando_rebel_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 5 && mando_rebel_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.mando_rebel_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
