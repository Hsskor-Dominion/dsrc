package script.stardust.conversation.korriban;

import script.*;
import script.library.*;

import static script.library.factions.addUnmodifiedFactionStanding;
import static script.library.factions.getFaction;

public class shin_hati extends base_script
{
    public shin_hati()
    {
    }
    public static final String c_stringFile = "conversation/shin_hati";
    public static final String STF_FILE = "stardust/quest";
    public static final String OBJ_VAR_BASE = "shin_hati.";

    public boolean shin_hati_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }
    public boolean shin_hati_chronicler_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "class_chronicles_master");
    }
    public boolean shin_hati_jediFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float jediFaction = factions.getFactionStanding(player, "sith_shadow");
        return jediFaction >= 50;
    }

    public void shin_hati_jedi_quest1(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_sith_academy_shin1");
    }
    public void shin_hati_jedi_quest2(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_sith_academy_shin2");
    }
    public void shin_hati_jedi_quest3(obj_id player, obj_id npc) throws InterruptedException
    {
        String pTemplate = getSkillTemplate(player);
        groundquests.grantQuest(player, "stardust_sith_academy_shin3");
    }

    public void shin_hati_action_grantHiring(obj_id player, obj_id npc, String reinforcementType) throws InterruptedException {
        if (!pet_lib.hasMaxPets(player, pet_lib.PET_TYPE_NPC) && !pet_lib.hasMaxStoredPetsOfType(player, pet_lib.PET_TYPE_NPC)) {
            location loc = getLocation(player);
            obj_id hireling = create.createCreature(reinforcementType, loc, true);
            if (!isIdValid(hireling)) {
                return;
            }
            setObjVar(hireling, "pet.petRestriction", 1);
            obj_id petControlDevice = pet_lib.makeControlDevice(player, hireling);
            callable.setCallableCD(hireling, petControlDevice);
            pet_lib.makePet(hireling, player);
            pet_lib.setupOfficerPetCommands(hireling);
            ai_lib.setDefaultCalmBehavior(hireling, ai_lib.BEHAVIOR_STOP);
            callable.setCallableLinks(player, petControlDevice, hireling);
            dictionary params = new dictionary();
            params.put("pet", hireling);
            params.put("master", player);
            params.put("controlDevice", petControlDevice);
            messageTo(hireling, "handleAddMaster", params, 0, false);
        }
    }

    public int shin_hati_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException {
        if (response.equals("seek_trade")) {
            handleBranch(player, "npc_consider_trade", new String[] { "force_trade" }, 2);
            return SCRIPT_CONTINUE;
        }

        if (response.equals("seek_sith")) {
            if (shin_hati_jediFriend_condition(npc, player)) {
                handleBranch(player, "npc_sith_whispers", new String[] {
                        "hireling_selection_01", "hireling_selection_02", "hireling_selection_03",
                        "hireling_selection_04", "hireling_selection_05"
                }, 3);
            }
        } else if (response.equals("seek_balance")) {
            handleBranch(player, "npc_explain", new String[] { "seek_balance2" }, 4);
        } else if (response.equals("seek_jedi")) {
            handleBranch(player, "npc_you_seek_jedi", new String[] { "seek_jedi2" }, 5);
        }

        return SCRIPT_CONTINUE;
    }

    private void handleBranch(obj_id player, String messageKey, String[] responseKeys, int branchId) throws InterruptedException {
        final string_id message = new string_id(c_stringFile, messageKey);

        string_id[] responses = new string_id[responseKeys.length];
        for (int i = 0; i < responseKeys.length; i++) {
            responses[i] = new string_id(c_stringFile, responseKeys[i]);
        }

        utils.setScriptVar(player, "conversation.shin_hati_conversation.branchId", branchId);

        npcSpeak(player, message);
        npcSetConversationResponses(player, responses);
    }
    public int shin_hati_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("force_trade"))
        {
            if (shin_hati_jediFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_trade");

                utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_a_friend");

                utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int shin_hati_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException {
        // Check player's faction points
        String hirelingFaction = getFaction(npc);
        float factionPoints = factions.getFactionStanding(player, hirelingFaction);
        if (factionPoints <= 200) {
            final string_id denialMessage = new string_id(c_stringFile, "insufficient_faction");
            utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
            npcEndConversationWithMessage(player, denialMessage);
            return SCRIPT_CONTINUE;
        }

        // Check if player already has a pet out
        if (callable.hasAnyCallable(player)) {
            final string_id petActiveMessage = new string_id(c_stringFile, "pet_vehicle_already_active");
            utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
            npcEndConversationWithMessage(player, petActiveMessage);
            return SCRIPT_CONTINUE;
        }

        // Define hireling type based on response
        String reinforcementType = null;
        int factionCost = 0;

        if (response.equals("hireling_selection_01")) {
            reinforcementType = "sith_reinforcement_1";
            factionCost = 1000;
        } else if (response.equals("hireling_selection_02")) {
            reinforcementType = "sith_reinforcement_2";
            factionCost = 1000;
        } else if (response.equals("hireling_selection_03")) {
            reinforcementType = "sith_reinforcement_3";
            factionCost = 1000;
        } else if (response.equals("hireling_selection_04")) {
            reinforcementType = "sith_reinforcement_4";
            factionCost = 10000;
        } else if (response.equals("hireling_selection_05")) {
            reinforcementType = "sith_reinforcement_5";
            factionCost = 18000;
        }

        // Grant hireling if a valid selection is made
        if (reinforcementType != null) {
            final string_id message = new string_id(c_stringFile, "hireling_commissioned");
            addUnmodifiedFactionStanding(player, hirelingFaction, -factionCost);
            shin_hati_action_grantHiring(player, npc, reinforcementType);

            utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }
    public int shin_hati_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_balance2"))
        {
            if (shin_hati_chronicler_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_mission");

                utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_deny_mission");

                utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int shin_hati_handleBranch5(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_jedi2"))
        {
            if (shin_hati_jediFriend_condition(npc, player))
            {
                final string_id message = new string_id(c_stringFile, "npc_offer_jedi_training");
                final int numberOfResponses = 3;

                final string_id[] responses = new string_id[numberOfResponses];
                int responseIndex = 0;

                responses[responseIndex++] = new string_id(c_stringFile, "seek_tat1");
                responses[responseIndex++] = new string_id(c_stringFile, "seek_cor1");
                responses[responseIndex++] = new string_id(c_stringFile, "seek_cor2");

                utils.setScriptVar(player, "conversation.shin_hati_conversation.branchId", 6);

                npcSpeak(player, message);
                npcSetConversationResponses(player, responses);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_are_not_ready");

                utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int shin_hati_handleBranch6(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_tat1"))
        {

            final string_id message = new string_id(c_stringFile, "quest_tat1");
            shin_hati_jedi_quest1(player, npc);

            utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_cor1"))
        {

            final string_id message = new string_id(c_stringFile, "quest_cor1");
            shin_hati_jedi_quest2(player, npc);

            utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_cor2"))
        {

            final string_id message = new string_id(c_stringFile, "quest_cor2");
            shin_hati_jedi_quest3(player, npc);

            utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
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
        setName(self, "Shin Hati (Sith Battlemaster)");

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

        if (shin_hati_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 4;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_sith");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_balance");

            utils.setScriptVar(player, "conversation.shin_hati_conversation.branchId", 1);

            npcStartConversation(player, npc, "shin_hati_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in riddles*");
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("shin_hati_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.shin_hati_conversation.branchId");

        if (branchId == 1 && shin_hati_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && shin_hati_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && shin_hati_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && shin_hati_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 5 && shin_hati_handleBranch5(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 6 && shin_hati_handleBranch6(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.shin_hati_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
