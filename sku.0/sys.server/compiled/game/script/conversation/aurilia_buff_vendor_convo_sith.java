package script.conversation;

import script.*;
import script.library.*;


public class aurilia_buff_vendor_convo_sith extends script.conversation.base.conversation_base
{
    public String conversation = "conversation.aurilia_buff_vendor_convo";
    public String c_stringFile = "conversation/aurilia_buff_vendor_convo";

    private void aurilia_buff_vendor_convo_action_showTokenVendorUI(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public boolean aurilia_buff_vendor_convo_language_condition(obj_id npc, obj_id player) throws InterruptedException
    {
        return hasSkill(player, "social_language_basic_comprehend");
    }

    public int aurilia_buff_vendor_convo_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade")) {
            handleBranch(player, "npc_consider_trade", new String[] { "force_trade" }, 2);
            return SCRIPT_CONTINUE;
        }

        else if (response.equals("seek_jedi")) {
            // Offer both political and space options
            handleBranch(player, "npc_you_seek_jedi",
                    new String[] { "seek_jedi_meditation", "seek_jedi_defend_village", "seek_jedi_political", "seek_jedi_space", "seek_jedi_combat" },
                    3
            );
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

    private void handleBranch(obj_id player, String messageKey, String[] responseKeys, int branchId) throws InterruptedException {
        final string_id message = new string_id(c_stringFile, messageKey);

        string_id[] responses = new string_id[responseKeys.length];
        for (int i = 0; i < responseKeys.length; i++) {
            responses[i] = new string_id(c_stringFile, responseKeys[i]);
        }

        utils.setScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId", branchId);

        npcSpeak(player, message);
        npcSetConversationResponses(player, responses);
    }
    public int aurilia_buff_vendor_convo_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("force_trade"))
        {
                final string_id message = new string_id(c_stringFile, "npc_offer_trade");
                aurilia_buff_vendor_convo_action_showTokenVendorUI(player, npc);

                utils.removeScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int aurilia_buff_vendor_convo_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        int required = 10000;


        // MEDITATE
        if (response.equals("seek_jedi_meditation"))
        {
            int jedi1 = xp.getExperiencePoints(player, "fs_combat");
            int jedi2 = xp.getExperiencePoints(player, "fs_reflex");
            int jedi3 = xp.getExperiencePoints(player, "fs_crafting");
            int jedi4 = xp.getExperiencePoints(player, "fs_senses");
            int jedi5 = xp.getExperiencePoints(player, "jedi");
            sendSystemMessageTestingOnly(player, "FS Combat Experience " + jedi1);
            sendSystemMessageTestingOnly(player, "FS Reflex Experience " + jedi2);
            sendSystemMessageTestingOnly(player, "FS Crafting Experience " + jedi3);
            sendSystemMessageTestingOnly(player, "FS Senses Experience " + jedi4);
            sendSystemMessageTestingOnly(player, "Jedi XP: " + jedi5);

            final string_id message = new string_id(c_stringFile, "meditation_training_offered");
            groundquests.grantQuest(player, "stardust_jedi_keeper");

            utils.removeScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }

        // DEFEND
        if (response.equals("seek_jedi_defend_village"))
        {
            final string_id message = new string_id(c_stringFile, "help_defend_village");
            groundquests.grantQuest(player, "stardust_holocron_aurillia");

            utils.removeScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");
            npcEndConversationWithMessage(player, message);

            return SCRIPT_CONTINUE;
        }

        if (response.equals("seek_jedi_political"))
        {
            int politicalXp = xp.getExperiencePoints(player, "political");
            sendSystemMessageTestingOnly(player, "Political Experience " + politicalXp);

            if (politicalXp < required)
            {
                npcEndConversationWithMessage(player,
                        new string_id(c_stringFile, "npc_you_lack_experience"));
                utils.removeScriptVarTree(player,
                        "conversation.aurilia_buff_vendor_convo_conversation");
                return SCRIPT_CONTINUE;
            }

            utils.setScriptVar(player, "jedi_xp_exchange_type", "political");
            utils.setScriptVar(player, "jedi_xp_exchange_amount", politicalXp);

            handleBranch(player,
                    "npc_confirm_exchange",
                    new String[] { "confirm_yes", "confirm_no" },
                    4);
            return SCRIPT_CONTINUE;
        }

        if (response.equals("seek_jedi_combat"))
        {
            int combatXp = xp.getExperiencePoints(player, "combat_general");
            sendSystemMessageTestingOnly(player, "Combat Experience " + combatXp);

            if (combatXp < required)
            {
                npcEndConversationWithMessage(player,
                        new string_id(c_stringFile, "npc_you_lack_experience"));
                utils.removeScriptVarTree(player,
                        "conversation.aurilia_buff_vendor_convo_conversation");
                return SCRIPT_CONTINUE;
            }

            utils.setScriptVar(player, "jedi_xp_exchange_type", "combat_general");
            utils.setScriptVar(player, "jedi_xp_exchange_amount", combatXp);

            handleBranch(player,
                    "npc_confirm_exchange",
                    new String[] { "confirm_yes", "confirm_no" },
                    4);
            return SCRIPT_CONTINUE;
        }

        if (response.equals("seek_jedi_space"))
        {
            int spaceXp = xp.getExperiencePoints(player, "space_combat_general");
            sendSystemMessageTestingOnly(player, "Space Combat Experience " + spaceXp);

            if (spaceXp < required)
            {
                npcEndConversationWithMessage(player,
                        new string_id(c_stringFile, "npc_you_lack_experience"));
                utils.removeScriptVarTree(player,
                        "conversation.aurilia_buff_vendor_convo_conversation");
                return SCRIPT_CONTINUE;
            }

            utils.setScriptVar(player, "jedi_xp_exchange_type", "space");
            utils.setScriptVar(player, "jedi_xp_exchange_amount", spaceXp);

            handleBranch(player,
                    "npc_confirm_exchange",
                    new String[] { "confirm_yes", "confirm_no" },
                    4);
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

    public int aurilia_buff_vendor_convo_handleBranch4(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        String type = utils.getStringScriptVar(player, "jedi_xp_exchange_type");
        int required = 10000;

        if (response.equals("confirm_no"))
        {
            final string_id message = new string_id(c_stringFile, "npc_exchange_cancel");
            utils.removeScriptVarTree(player, "conversation.aurilia_buff_vendor_convo_conversation");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }

        if (response.equals("confirm_yes"))
        {
            // Map of input type -> experience pool to deduct
            java.util.Map<String, String> xpPools = new java.util.HashMap<String, String>();
            xpPools.put("political", "political");
            xpPools.put("combat_general", "combat_general");
            xpPools.put("space_combat", "space_combat_xp");
            // Add more here:
            xpPools.put("piloting", "piloting_xp");
            xpPools.put("stealth", "stealth_xp");
            xpPools.put("crafting", "crafting_xp");
            xpPools.put("social", "social_xp");
            // etc… up to 20+

            // Map of messages per type
            java.util.Map<String, string_id> messages = new java.util.HashMap<String, string_id>();
            messages.put("political", new string_id(c_stringFile, "npc_feel_the_force_experience_political"));
            messages.put("combat_general", new string_id(c_stringFile, "npc_feel_the_force_experience_combat"));
            messages.put("space_combat", new string_id(c_stringFile, "npc_feel_the_force_experience_space"));
            // Add more messages for each type if desired

            if (xpPools.containsKey(type))
            {
                String pool = xpPools.get(type);
                int current = getExperiencePoints(player, pool);

                if (current >= required)
                {
                    // Grant force experience
                    grantExperiencePoints(player, "fs_combat", 1000);
                    grantExperiencePoints(player, "fs_reflex", 1000);
                    grantExperiencePoints(player, "fs_crafting", 1000);
                    grantExperiencePoints(player, "fs_senses", 1000);

                    // Deduct from chosen pool
                    grantExperiencePoints(player, pool, -required);
                    sendSystemMessageTestingOnly(player, "You have unlearned what you have learned, and experience the force");

                    string_id msg = messages.containsKey(type)
                            ? messages.get(type)
                            : new string_id(c_stringFile, "npc_feel_the_force_experience_generic");

                    npcEndConversationWithMessage(player, msg);
                }
                else
                {
                    npcEndConversationWithMessage(player, new string_id(c_stringFile, "npc_you_lack_experience"));
                }
            }

            utils.removeScriptVarTree(player, "conversation.aurilia_buff_vendor_convo_conversation");
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
        // give "Old Man" name if on endor
        String planetName = getLocation(self).area;
        if (planetName == null) return -1;
        planetName = planetName.toLowerCase();

        // list of planets for reference
        int planetId = -1;
        switch (planetName) {
            case "corellia":
                planetId = 1;
                break;
            case "naboo":
                planetId = 2;
                break;
            case "tatooine":
                planetId = 3;
                break;
            case "rori":
                planetId = 4;
                break;
            case "lok":
                planetId = 5;
                break;
            case "endor":
                planetId = 6;
                break;
            case "talus":
                planetId = 7;
                break;
            case "mustafar":
                planetId = 8;
                break;
            case "dantooine":
                planetId = 9;
                break;
            case "yavin4":
                planetId = 10;
                break;
            case "dathomir":
                planetId = 11;
                break;
            case "kashyyyk_main":
                planetId = 12;
                break;
        }
            if (planetId == 11) {
                setName(self, "Paemos (Village Elder)");
            }
            if (planetId == 6) {
                setName(self, "an old man");
            }
            else {
                setName(self, "Baylen Skoll (Sith Enforcer)");
            }
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

        if (aurilia_buff_vendor_convo_language_condition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 2;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_jedi");

            utils.setScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId", 1);

            npcStartConversation(player, npc, "aurilia_buff_vendor_convo_conversation", message, responses);

            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks in riddles*");
        return SCRIPT_CONTINUE;
    }


    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("aurilia_buff_vendor_convo_conversation"))
        {
            return SCRIPT_CONTINUE;
        }
        final int branchId = utils.getIntScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");

        if (branchId == 1 && aurilia_buff_vendor_convo_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && aurilia_buff_vendor_convo_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && aurilia_buff_vendor_convo_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 4 && aurilia_buff_vendor_convo_handleBranch4(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.aurilia_buff_vendor_convo_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
