package script.stardust.conversation.tatooine;

import script.*;
import script.library.*;

import static script.library.factions.addUnmodifiedFactionStanding;
import static script.library.factions.getFaction;

public class personel_mando extends base_script
{
    public personel_mando()
    {
    }
    public static String c_stringFile = "conversation/personel_mando";
    public boolean personel_mando_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean personel_mandoFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float townFaction = factions.getFactionStanding(player, "death_watch");
        if (townFaction >= 50)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }

    private String hirelingSelection;

    public void personel_mando_action_grantHiring1(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!pet_lib.hasMaxPets(player, pet_lib.PET_TYPE_NPC) && !pet_lib.hasMaxStoredPetsOfType(player, pet_lib.PET_TYPE_NPC))
        {
            location loc = getLocation(player);
            obj_id hireling = create.createCreature("mando_reinforcement_1", loc, true);
            if (!isIdValid(hireling))
            {
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
        return;
    }

    public void personel_mando_action_grantHiring2(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!pet_lib.hasMaxPets(player, pet_lib.PET_TYPE_NPC) && !pet_lib.hasMaxStoredPetsOfType(player, pet_lib.PET_TYPE_NPC))
        {
            location loc = getLocation(player);
            obj_id hireling = create.createCreature("mando_reinforcement_2", loc, true);
            if (!isIdValid(hireling))
            {
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
        return;
    }

    public void personel_mando_action_grantHiring3(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!pet_lib.hasMaxPets(player, pet_lib.PET_TYPE_NPC) && !pet_lib.hasMaxStoredPetsOfType(player, pet_lib.PET_TYPE_NPC))
        {
            location loc = getLocation(player);
            obj_id hireling = create.createCreature("mando_reinforcement_3", loc, true);
            if (!isIdValid(hireling))
            {
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
        return;
    }

    public void personel_mando_action_grantHiring4(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!pet_lib.hasMaxPets(player, pet_lib.PET_TYPE_NPC) && !pet_lib.hasMaxStoredPetsOfType(player, pet_lib.PET_TYPE_NPC))
        {
            location loc = getLocation(player);
            obj_id hireling = create.createCreature("mando_reinforcement_4", loc, true);
            if (!isIdValid(hireling))
            {
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
        return;
    }

    public void personel_mando_action_grantHiring5(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!pet_lib.hasMaxPets(player, pet_lib.PET_TYPE_NPC) && !pet_lib.hasMaxStoredPetsOfType(player, pet_lib.PET_TYPE_NPC))
        {
            location loc = getLocation(player);
            obj_id hireling = create.createCreature("mando_reinforcement_5", loc, true);
            if (!isIdValid(hireling))
            {
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
        return;
    }

    public int personel_mando_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_hireling"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_hireling");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "hireling_selection");

            utils.setScriptVar(player, "conversation.personel_mando_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_info"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_info");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_quest");

            utils.setScriptVar(player, "conversation.personel_mando_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int personel_mando_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("hireling_selection"))
        {
            if (personel_mandoFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "npc_shares_hireling_list");
                final int numberOfResponses = 5;

                final string_id[] responses = new string_id[numberOfResponses];
                int responseIndex = 0;

                responses[responseIndex++] = new string_id(c_stringFile, "hireling_selection_01");
                responses[responseIndex++] = new string_id(c_stringFile, "hireling_selection_02");
                responses[responseIndex++] = new string_id(c_stringFile, "hireling_selection_03");
                responses[responseIndex++] = new string_id(c_stringFile, "hireling_selection_04");
                responses[responseIndex++] = new string_id(c_stringFile, "hireling_selection_05"); // Added missing response

                utils.setScriptVar(player, "conversation.personel_mando_conversation.branchId", 4);

                npcSpeak(player, message);
                npcSetConversationResponses(player, responses);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_need_rep");

                utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int personel_mando_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_quest"))
        {
            if (personel_mando_condition__defaultCondition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "seek_this");
                //personel_mando_action_grantQuest(player, npc);

                utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int personel_mando_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        // Check player's faction points
        String hirelingFaction = getFaction(npc);
        float factionPoints = factions.getFactionStanding(player, hirelingFaction);
        if (factionPoints <= 200)
        {
            final string_id denialMessage = new string_id(c_stringFile, "insufficient_faction");
            utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
            npcEndConversationWithMessage(player, denialMessage);
            return SCRIPT_CONTINUE;
        }

        // Check if player already has a pet out
        if (callable.hasAnyCallable(player))
        {
            final string_id petActiveMessage = new string_id(c_stringFile, "pet_vehicle_already_active");
            utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
            npcEndConversationWithMessage(player, petActiveMessage);
            return SCRIPT_CONTINUE;
        }

        // Handle hireling selections if faction check passes
        if (response.equals("hireling_selection_01"))
        {
            final string_id message = new string_id(c_stringFile, "hireling_commissioned");
            addUnmodifiedFactionStanding(player, hirelingFaction, -1000);
            personel_mando_action_grantHiring1(player, npc);

            utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("hireling_selection_02"))
        {
            final string_id message = new string_id(c_stringFile, "hireling_commissioned");
            addUnmodifiedFactionStanding(player, hirelingFaction, -1000);
            personel_mando_action_grantHiring2(player, npc);

            utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("hireling_selection_03"))
        {
            final string_id message = new string_id(c_stringFile, "hireling_commissioned");
            addUnmodifiedFactionStanding(player, hirelingFaction, -1000);
            personel_mando_action_grantHiring3(player, npc);

            utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("hireling_selection_04"))
        {
            final string_id message = new string_id(c_stringFile, "hireling_commissioned");
            addUnmodifiedFactionStanding(player, hirelingFaction, -10000);
            personel_mando_action_grantHiring4(player, npc);

            utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("hireling_selection_05"))
        {
            final string_id message = new string_id(c_stringFile, "hireling_commissioned");
            addUnmodifiedFactionStanding(player, hirelingFaction, -18000);
            personel_mando_action_grantHiring5(player, npc);

            utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
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
        setName(self, "Woves (a mercenary vendor)");

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

        if (personel_mando_condition__defaultCondition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_hireling");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_info");

            utils.setScriptVar(player, "conversation.personel_mando_conversation.branchId", 1);

            npcStartConversation(player, npc, "personel_mando_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks curiously*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("personel_mando_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.personel_mando_conversation.branchId");

        if (branchId == 1 && personel_mando_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && personel_mando_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && personel_mando_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && personel_mando_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.personel_mando_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
