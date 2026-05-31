package script.stardust.conversation.ossus;

import script.*;
import script.library.*;

import static script.library.force_rank.SCRIPT_FRS_PLAYER;

public class minch extends base_script
{
    public minch()
    {
    }
    public static final String c_stringFile = "conversation/yoda";
    public static final String OBJ_VAR_BASE = "minch.";
    public static final String JEDI_APPRENTICE = OBJ_VAR_BASE + "youngling";
    public boolean minch_defaultCondition()
    {
        return true;
    }
    public boolean minch_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public boolean minch_chronicler_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "class_chronicles_master");
    }
    public boolean minch_jediFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float jediFaction = factions.getFactionStanding(player, "fs_villager");
        return jediFaction >= 1000;
    }
    public boolean minch_phase1_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase1_novice");
    }
    public boolean minch_phase2_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase2_novice");
    }
    public boolean minch_phase3_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase3_novice");
    }
    public boolean minch_phase4_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"class_forcesensitive_phase4_novice");
    }
    public boolean minch_wanderer_condition(obj_id npc, obj_id player)
    {
        return hasSkill(player,"force_rank");
    }
    public boolean minch_credits_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return (money.hasFunds(player, money.MT_TOTAL, smuggler.TIER_4_GENERIC_PVP_FRONT_COST));
    }
    public boolean minch_hasObjVar_condition(obj_id npc, obj_id player)
    {
        return hasObjVar(player, JEDI_APPRENTICE);
    }
    public boolean minch_quest_condition_on_diplomacy(obj_id npc, obj_id player) throws InterruptedException
    {
        // Check if the player has any diplomacy quests or is on "jedi_gift_exchange"
        return (groundquests.isQuestActive(player, "stardust_jedi_diplomacy1") ||
                groundquests.isQuestActive(player, "stardust_jedi_diplomacy2") ||
                groundquests.isQuestActive(player, "stardust_jedi_diplomacy3") ||
                groundquests.isQuestActive(player, "stardust_jedi_diplomacy4"));
    }
    public void minch_diplomacy_mission(obj_id player, obj_id npc) throws InterruptedException
    {
        int diplomacy_mission = rand(1, 4);
        String mission = "";
        switch (diplomacy_mission)
        {
            case 1:
                mission = "stardust_jedi_diplomacy1";
                break;
            case 2:
                mission = "stardust_jedi_diplomacy2";
                break;
            case 3:
                mission = "stardust_jedi_diplomacy3";
                break;
            case 4:
                mission = "stardust_jedi_diplomacy4";
                break;
        }
        groundquests.grantQuest(player, mission);
    }
    public void minch_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        final dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void minch_jedi_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_jedi_yoda");
    }
    public void minch_bounty_quest(obj_id player, obj_id npc) throws InterruptedException
    {
        money.requestPayment(player, npc, smuggler.TIER_5_GENERIC_PVP_FRONT_COST, "none", null, true);
        int mission_bounty = 10000;
        int current_bounty = 0;
        mission_bounty += rand(1, 2000);
        if (hasObjVar(player, "bounty.amount"))
        {
            current_bounty = getIntObjVar(player, "bounty.amount");
        }
        current_bounty += mission_bounty;
        setObjVar(player, "bounty.amount", current_bounty);
        setObjVar(player, "jedi.bounty", mission_bounty);
        setJediBountyValue(player, current_bounty);
        updateJediScriptData(player, "jedi", 1);
    }
    public int handleDestroyTempSpawn(obj_id self, dictionary params) throws InterruptedException
    {
        // If this was called without a delay, schedule one
        if (!params.containsKey("delayed"))
        {
            dictionary d = new dictionary();
            d.put("delayed", 1);
            messageTo(self, "handleDestroyTempSpawn", d, 60.0f, false);
            return SCRIPT_CONTINUE;
        }

        // If we reach here, 30 seconds have already passed
        destroyObject(self);
        return SCRIPT_CONTINUE;
    }
    public int minch_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "force_trade");

            utils.setScriptVar(player, "conversation.minch_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);
            setState(npc, STATE_GLOWING_JEDI, false);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_sith"))
        {

            final string_id message = new string_id(c_stringFile, "npc_sith_whispers");

            utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_balance"))
        {

            final string_id message = new string_id(c_stringFile, "npc_explain");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_balance2");

            utils.setScriptVar(player, "conversation.minch_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_jedi"))
        {

            final string_id message = new string_id(c_stringFile, "npc_you_seek_yoda");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi2");

            utils.setScriptVar(player, "conversation.minch_conversation.branchId", 4);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_serve"))
        {

            final string_id message = new string_id(c_stringFile, "npc_considers");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "confirm_serve");

            utils.setScriptVar(player, "conversation.minch_conversation.branchId", 5);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_to_leave_order"))
        {

            final string_id message = new string_id(c_stringFile, "npc_are_you_sure");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "confirm_leave_order");

            utils.setScriptVar(player, "conversation.minch_conversation.branchId", 6);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int minch_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("force_trade"))
        {
            if (minch_jediFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_trade");
                minch_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_friend");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int minch_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_balance2"))
        {
            if (minch_phase1_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_mission");
                setState(npc, STATE_GLOWING_JEDI, true);


                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_deny_mission");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int minch_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_jedi2"))
        {
            if (minch_wanderer_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_you_belong_to_an_order");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (minch_phase2_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_jedi_training");
                jedi_trials.initializeKnightTrials(player);
                force_rank.addToForceRankSystem(player, force_rank.LIGHT_COUNCIL);
                grantSkill(player, "force_rank");
                grantSkill(player, "force_rank_light");
                grantSkill(player, "force_rank_light_novice");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_ready");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int minch_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("confirm_serve"))
        {
            if (!minch_chronicler_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_you_must_master_chronicles");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            if (minch_quest_condition_on_diplomacy(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_already_on_diplomacy");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (minch_phase2_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_jedi_diplomacy");
                //experimental diplomacy missions
                minch_diplomacy_mission(player, npc);
                minch_bounty_quest(player, npc);

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else if (minch_phase1_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_jedi_meditation");
                groundquests.grantQuest(player, "stardust_jedi_keeper");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_jedi");

                utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int minch_handleBranch6(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("confirm_leave_order"))
        {
            final string_id message = new string_id(c_stringFile, "npc_very_well");
            force_rank.removeFromForceRankSystem(player, true);
            revokeSkill(player, "force_rank");
            revokeSkill(player, "force_rank_light");
            revokeSkill(player, "force_rank_light_novice");
            detachScript(player, SCRIPT_FRS_PLAYER);
            setObjVar(player, force_rank.VAR_RANK, 0);
            setJediState(player, JEDI_STATE_NONE);//this removes the mind bar bug


            utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, false);

        setName(self, "Minch (a Force Ghost)");

        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, false);
        float maxHealth = getMaxHealth(self);
        setHealth(self, (int) maxHealth);
        setLevel(self, 95);

        setName(self, "Minch (a Force Ghost)");
        setState(self, STATE_GLOWING_JEDI, true);

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

        if (minch_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 6;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_balance");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_sith");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_serve");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_to_leave_order");

            utils.setScriptVar(player, "conversation.minch_conversation.branchId", 1);

            npcStartConversation(player, npc, "minch_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in riddles*");
        return SCRIPT_CONTINUE;
    }

    public int aiCorpsePrepared(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id corpseInventory = utils.getInventoryContainer(self);
        if (corpseInventory == null)
        {
            return SCRIPT_CONTINUE;
        }
        if (!isIdValid(self))
        {
            return SCRIPT_CONTINUE;
        }
        createMyLoot(self);
        return SCRIPT_CONTINUE;
    }

    public void createMyLoot(obj_id self) throws InterruptedException
    {
        // --- get all objects within 64 meters ---
        obj_id[] nearbyObjects = getObjectsInRange(self, 64.0f); // returns all objects

        if (nearbyObjects != null)
        {
            for (obj_id obj : nearbyObjects)
            {
                if (isPlayer(obj))
                {
                    // --- quest signal + completion ---
                    groundquests.completeQuest(obj, "stardust_mando_crest");
                }
            }
        }

        // --- loot container ---
        obj_id corpseInventory = utils.getInventoryContainer(self);
        if (corpseInventory == null)
        {
            return;
        }

        String mobType = ai_lib.getCreatureName(self);
        if (mobType == null)
        {
            return;
        }

        int x = rand(1, 100);  // random number 1–100

        // --- LOOT TABLE ---
        if (x <= 1)
        {
            static_item.createNewItemFunction("jedi_holocron", corpseInventory);
        }
        else if (x <= 10)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_01", corpseInventory);
        }
        else if (x <= 15)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_02", corpseInventory);
        }
        else if (x <= 20)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_03", corpseInventory);
        }
        else if (x <= 25)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_04", corpseInventory);
        }
        else if (x <= 30)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_01_05", corpseInventory);
        }
        else if (x <= 35)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_01", corpseInventory);
        }
        else if (x <= 40)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_02", corpseInventory);
        }
        else if (x <= 45)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_03", corpseInventory);
        }
        else if (x <= 50)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_04", corpseInventory);
        }
        else if (x <= 55)
        {
            static_item.createNewItemFunction("item_collection_jedi_holocron_02_05", corpseInventory);
        }
        else if (x <= 65)
        {
            static_item.createNewItemFunction("jedi_holocron", corpseInventory);
        }
        else if (x <= 95)
        {
            static_item.createNewItemFunction("item_pgc_token_03", corpseInventory);
        }
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("minch_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.minch_conversation.branchId");

        if (branchId == 1 && minch_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && minch_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && minch_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && minch_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 5 && minch_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 6 && minch_handleBranch6(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.minch_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
