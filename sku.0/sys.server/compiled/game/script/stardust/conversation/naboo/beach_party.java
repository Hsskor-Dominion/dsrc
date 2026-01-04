package script.stardust.conversation.naboo;

import script.*;
import script.library.*;

public class beach_party extends base_script
{
    public beach_party()
    {
    }
    public static String c_stringFile = "conversation/beach_party";
    public boolean beach_party_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean beach_partyComplete_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, "stardust_beach_party");
    }
    public boolean beach_partyFriend_condition(obj_id player, obj_id npc) throws InterruptedException
    {
        float townFaction = factions.getFactionStanding(player, "townsperson");
        if (townFaction >= -5000)
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public void beach_party_action_vendor(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }
    public void beach_party_action_signalReward(obj_id player, obj_id npc) throws InterruptedException
    {
        groundquests.sendSignal(player, "npe_ent2_reward");
    }
    public void beach_party_action_grantQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/npe_entertainer_jabba");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int beach_party_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("seek_trade"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_trade");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "trade");

            utils.setScriptVar(player, "conversation.beach_party_conversation.branchId", 2);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        else if (response.equals("seek_party"))
        {

            final string_id message = new string_id(c_stringFile, "npc_consider_party");
            final int numberOfResponses = 1;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "become_party");

            utils.setScriptVar(player, "conversation.beach_party_conversation.branchId", 3);

            npcSpeak(player, message);
            npcSetConversationResponses(player, responses);

            return SCRIPT_CONTINUE;
        }
        return SCRIPT_DEFAULT;
    }
    public int beach_party_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("trade"))
        {
            if (beach_partyFriend_condition(player, npc))
            {
                final string_id message = new string_id(c_stringFile, "fence");
                beach_party_action_vendor(player, npc);

                utils.removeScriptVar(player, "conversation.beach_party_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
            else
            {
                final string_id message = new string_id(c_stringFile, "npc_you_need_townsperson_rep");

                utils.removeScriptVar(player, "conversation.beach_party_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int beach_party_handleBranch3(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("become_party"))
        {
            // Default condition always true, keeping your structure
            if (beach_party_condition__defaultCondition(player, npc))
            {
                // Attempt to reward the player if they earned it
                beach_party_action_rewardPlayer(player, npc);

                final string_id message = new string_id(c_stringFile, "lets_party");

                utils.removeScriptVar(player, "conversation.beach_party_conversation.branchId");
                npcEndConversationWithMessage(player, message);

                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public void beach_party_action_rewardPlayer(obj_id self, obj_id player) throws InterruptedException
    {
        doAnimationAction(self, "celebrate");
        doAnimationAction(player, "celebrate");
//        int now = getGameTime(); // seconds since server start
//        int cooldown = 24 * 60 * 60; // 24 hours
//
//        // --- Cooldown check ---
//        if (hasObjVar(player, "chimaeraPartyzoneCooldown"))
//        {
//            int lastUsed = getIntObjVar(player, "chimaeraPartyzoneCooldown");
//            if (now - lastUsed < cooldown)
//            {
//                sendSystemMessage(player, "You may only access the Chimaera Partyzone once every 24 hours.", "");
//                beach_party_action_signalReward(player, self);
//                beach_party_action_grantQuest(player, self);
//                return;
//            }
//        }
//
//        // --- Grant reward item ---//maybe later I make rand 1-3 for levels?
//        obj_id reward = createObjectInInventoryAllowOverload(
//                "object/tangible/item/rare_loot_chest_3.iff",
//                player
//        );
//
//        if (!isIdValid(reward))
//        {
//            sendSystemMessage(player, "Error: Could not grant reward item.", "");
//            return;
//        }
//
//        // Attach script to reward if needed
//        attachScript(reward, "systems.loot.rare_loot_chest");
//        setName(reward, "Legendary Loot Crate");
//
//        // Save cooldown timestamp
//        setObjVar(player, "chimaeraPartyzoneCooldown", now);
//
//        // Warp player (external modular function)
//        warpPlayerToPartyzone(player);
//
//        sendSystemMessage(player, "You are being transported to the current Galactic Partyzone!", "");
//    }
//    public void warpPlayerToPartyzone(obj_id player) throws InterruptedException
//    {
//        // CURRENT PARTY - "Boonta Eve Classic"
//        String planet = "tatooine";
//        float x = 3467.0f;
//        float y = 2.0f;
//        float z = 5076.0f;
//
//        // Actual warp
//        warpPlayer(player, planet, x, y, z, null, 0.0f, 0.0f, 0.0f);
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
        ai_lib.setDefaultCalmBehavior(self, ai_lib.BEHAVIOR_SENTINEL);
        setName(self, "Zed (Party Organizer)");

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

        if (beach_party_condition__defaultCondition(npc, player))
        {
            final string_id message = new string_id(c_stringFile, "npc_intro");
            final int numberOfResponses = 3;

            final string_id[] responses = new string_id[numberOfResponses];
            int responseIndex = 0;

            responses[responseIndex++] = new string_id(c_stringFile, "seek_trade");
            responses[responseIndex++] = new string_id(c_stringFile, "seek_party");

            utils.setScriptVar(player, "conversation.beach_party_conversation.branchId", 1);

            npcStartConversation(player, npc, "beach_party_conversation", message, responses);
            return SCRIPT_CONTINUE;
        }

        chat.chat(npc, "*Speaks curiously*");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id npc, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("beach_party_conversation"))
        {
            return SCRIPT_CONTINUE;
        }

        final int branchId = utils.getIntScriptVar(player, "conversation.beach_party_conversation.branchId");

        if (branchId == 1 && beach_party_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 2 && beach_party_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        else if (branchId == 3 && beach_party_handleBranch3(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.beach_party_conversation.branchId");
        return SCRIPT_CONTINUE;
    }

}
