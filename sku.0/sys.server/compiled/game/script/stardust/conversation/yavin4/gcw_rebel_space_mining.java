package script.stardust.conversation.yavin4;

import script.library.*;
import script.*;

import java.util.Vector;

public class gcw_rebel_space_mining extends script.base_script
{
    public gcw_rebel_space_mining()
    {
    }
    public static String c_stringFile = "conversation/gcw_rebel_space_mining";
    public boolean gcw_rebel_space_mining_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    public boolean gcw_rebel_space_mining_condition_completedSpaceMine1(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id[] boxes = utils.getAllItemsPlayerHasByTemplate(player, "object/resource_container/space/metal.iff");
        if (boxes == null || boxes.length < 1)
        {
            return false;
        }
        obj_id currentBox = null;
        obj_id currentBoxResource = null;
        String currentBoxResourceName = "";
        int currentBoxCount = 0;
        for (obj_id box : boxes) {
            currentBox = box;
            currentBoxResource = getResourceContainerResourceType(currentBox);
            currentBoxResourceName = getResourceName(currentBoxResource);
            if (currentBoxResourceName.contains("space_metal_obsidian") || currentBoxResourceName.contains("Obsidian Asteroid")) {
                currentBoxCount = getResourceContainerQuantity(currentBox);
                if (currentBoxCount >= 500) {
                    if (groundquests.isTaskActive(player, "stardust_gcw_rebel_mining", "returnHera")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean gcw_rebel_space_mining_condition_spaceMineActive1(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "stardust_gcw_rebel_mining");
    }
    public boolean gcw_rebel_space_mining_condition_completedSpaceMine2(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id[] boxes = utils.getAllItemsPlayerHasByTemplate(player, "object/resource_container/space/gas.iff");
        if (boxes == null || boxes.length < 1)
        {
            return false;
        }
        obj_id currentBox = null;
        obj_id currentBoxResource = null;
        String currentBoxResourceName = "";
        int currentBoxCount = 0;
        for (obj_id box : boxes) {
            currentBox = box;
            currentBoxResource = getResourceContainerResourceType(currentBox);
            currentBoxResourceName = getResourceName(currentBoxResource);
            if (currentBoxResourceName.contains("space_gas_organometallic") || currentBoxResourceName.contains("Organometallic Asteroid")) {
                currentBoxCount = getResourceContainerQuantity(currentBox);
                if (currentBoxCount >= 250) {
                    if (groundquests.isTaskActive(player, "gcw_rebel_space_mining", "returnHera2")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean gcw_rebel_space_mining_condition_spaceMineActive2(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.isQuestActive(player, "stardust_gcw_rebel_space_mining");
    }
    public boolean gcw_rebel_space_mining_condition_inPhase2(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id object = trial.getParent(npc);
        if (factions.isRebel(player))
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public boolean gcw_rebel_space_mining_condition_inPhase1(obj_id player, obj_id npc) throws InterruptedException
    {
        if (factions.isRebel(player))
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public boolean gcw_rebel_space_mining_condition_enoughOre(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id object = trial.getParent(npc);
        if (factions.isRebel(player))
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    public boolean gcw_rebel_space_mining_condition_isImperialPlayer(obj_id player, obj_id npc) throws InterruptedException
    {
        return factions.isImperial(player);
    }
    public void gcw_rebel_space_mining_action_giveSpaceMine1(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id mobj = space_quest._getQuest(player, "space_mining_destroy", "restuss_rebel_mining_1");
        if (mobj != null) space_quest.setSilentQuestAborted(player, mobj);
        groundquests.clearQuest(player, "stardust_gcw_rebel_mining");
        groundquests.grantQuest(player, "stardust_gcw_rebel_mining");
    }
    public void gcw_rebel_space_mining_action_signalDone(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id[] boxes = utils.getAllItemsPlayerHasByTemplate(player, "object/resource_container/space/metal.iff");
        obj_id currentBox = null;
        obj_id currentBoxResource = null;
        String currentBoxResourceName = "";
        int currentBoxCount = 0;
        for (obj_id box : boxes) {
            currentBox = box;
            currentBoxResource = getResourceContainerResourceType(currentBox);
            currentBoxResourceName = getResourceName(currentBoxResource);
            if (currentBoxResourceName.contains("space_metal_obsidian") || currentBoxResourceName.contains("Obsidian Asteroid")) {
                currentBoxCount = getResourceContainerQuantity(currentBox);
                if (currentBoxCount >= 500) {
                    removeResourceFromContainer(currentBox, currentBoxResource, 500);
                    break;
                }
            }
        }
        groundquests.sendSignal(player, "returnedHera");
        if (group.isGrouped(player))
        {
            Vector members = group.getPCMembersInRange(player, 35.0f);
            if (members != null && members.size() > 0)
            {
                int numInGroup = members.size();
                for (Object member : members) {
                    obj_id thisMember = ((obj_id) member);
                    groundquests.sendSignal(thisMember, "returnedPekt");
                }
            }
        }
    }
    public void gcw_rebel_space_mining_action_giveSpaceMine2(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id mobj = space_quest._getQuest(player, "space_mining_destroy", "gcw_rebel_mining_1");
        if (mobj != null) space_quest.setSilentQuestAborted(player, mobj);
        groundquests.clearQuest(player, "stardust_gcw_rebel_mining");
        groundquests.grantQuest(player, "stardust_gcw_rebel_mining");
    }
    public void gcw_rebel_space_mining_action_signalDone2(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id[] boxes = utils.getAllItemsPlayerHasByTemplate(player, "object/resource_container/space/gas.iff");
        obj_id currentBox = null;
        obj_id currentBoxResource = null;
        String currentBoxResourceName = "";
        int currentBoxCount = 0;
        for (obj_id box : boxes) {
            currentBox = box;
            currentBoxResource = getResourceContainerResourceType(currentBox);
            currentBoxResourceName = getResourceName(currentBoxResource);
            if (currentBoxResourceName.contains("space_gas_organometallic") || currentBoxResourceName.contains("Organometallic Asteroid")) {
                currentBoxCount = getResourceContainerQuantity(currentBox);
                if (currentBoxCount >= 250) {
                    removeResourceFromContainer(currentBox, currentBoxResource, 250);
                    break;
                }
            }
        }
        groundquests.sendSignal(player, "returnedHera2");
        if (group.isGrouped(player))
        {
            Vector members = group.getPCMembersInRange(player, 35.0f);
            if (members != null && members.size() > 0)
            {
                int numInGroup = members.size();
                for (Object member : members) {
                    obj_id thisMember = ((obj_id) member);
                    groundquests.sendSignal(thisMember, "returnedPekt2");
                }
            }
        }
    }
    public int gcw_rebel_space_mining_handleBranch6(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_38"))
        {
            doAnimationAction(player, "salute2");
            gcw_rebel_space_mining_action_giveSpaceMine2(player, npc);
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                string_id message = new string_id(c_stringFile, "s_40");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_mining.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_42"))
        {
            doAnimationAction(player, "shake_head_no");
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                doAnimationAction(player, "salute2");
                string_id message = new string_id(c_stringFile, "s_44");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_mining.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_rebel_space_mining_handleBranch9(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_30"))
        {
            doAnimationAction(player, "salute2");
            gcw_rebel_space_mining_action_giveSpaceMine2(player, npc);
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                string_id message = new string_id(c_stringFile, "s_32");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_mining.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_34"))
        {
            doAnimationAction(player, "shake_head_no");
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                doAnimationAction(player, "salute2");
                string_id message = new string_id(c_stringFile, "s_36");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_mining.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int gcw_rebel_space_mining_handleBranch12(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("s_24"))
        {
            doAnimationAction(player, "salute2");
            gcw_rebel_space_mining_action_giveSpaceMine1(player, npc);
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                string_id message = new string_id(c_stringFile, "s_26");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_mining.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        if (response.equals("s_29"))
        {
            doAnimationAction(player, "shake_head_no");
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                doAnimationAction(npc, "salute2");
                doAnimationAction(player, "salute2");
                string_id message = new string_id(c_stringFile, "s_35");
                utils.removeScriptVar(player, "conversation.gcw_rebel_space_mining.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if ((!isTangible(self)) || (isPlayer(self)))
        {
            detachScript(self, "conversation.gcw_rebel_space_mining");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setName(self, "Freyta Smyth (Starlight Mining Operations)");
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
        detachScript(self, "conversation.gcw_rebel_space_mining");
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
        if (gcw_rebel_space_mining_condition_isImperialPlayer(player, npc))
        {
            string_id message = new string_id(c_stringFile, "s_41");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_mining_condition_completedSpaceMine2(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            gcw_rebel_space_mining_action_signalDone2(player, npc);
            string_id message = new string_id(c_stringFile, "s_13");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_mining_condition_completedSpaceMine1(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            gcw_rebel_space_mining_action_signalDone(player, npc);
            string_id message = new string_id(c_stringFile, "s_6");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_mining_condition_spaceMineActive2(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_11");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_mining_condition_spaceMineActive1(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_9");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_mining_condition_enoughOre(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_33");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse1 = true;
            }
            if (hasResponse)
            {
                int responseIndex = 0;
                string_id responses[] = new string_id[numberOfResponses];
                if (hasResponse0)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_38");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_42");
                }
                utils.setScriptVar(player, "conversation.gcw_rebel_space_mining.branchId", 6);
                npcStartConversation(player, npc, "gcw_rebel_space_mining", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_mining_condition_inPhase2(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_28");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse1 = true;
            }
            if (hasResponse)
            {
                int responseIndex = 0;
                string_id responses[] = new string_id[numberOfResponses];
                if (hasResponse0)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_30");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_34");
                }
                utils.setScriptVar(player, "conversation.gcw_rebel_space_mining.branchId", 9);
                npcStartConversation(player, npc, "gcw_rebel_space_mining", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_mining_condition_inPhase1(player, npc))
        {
            doAnimationAction(npc, "salute2");
            doAnimationAction(player, "salute2");
            string_id message = new string_id(c_stringFile, "s_22");
            int numberOfResponses = 0;
            boolean hasResponse = false;
            boolean hasResponse0 = false;
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse0 = true;
            }
            boolean hasResponse1 = false;
            if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
            {
                ++numberOfResponses;
                hasResponse = true;
                hasResponse1 = true;
            }
            if (hasResponse)
            {
                int responseIndex = 0;
                string_id responses[] = new string_id[numberOfResponses];
                if (hasResponse0)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_24");
                }
                if (hasResponse1)
                {
                    responses[responseIndex++] = new string_id(c_stringFile, "s_29");
                }
                utils.setScriptVar(player, "conversation.gcw_rebel_space_mining.branchId", 12);
                npcStartConversation(player, npc, "gcw_rebel_space_mining", message, responses);
            }
            else 
            {
                chat.chat(npc, player, message);
            }
            return SCRIPT_CONTINUE;
        }
        if (gcw_rebel_space_mining_condition__defaultCondition(player, npc))
        {
            doAnimationAction(npc, "dismiss");
            string_id message = new string_id(c_stringFile, "s_39");
            chat.chat(npc, player, message);
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  All conditions for OnStartNpcConversation were false.");
        return SCRIPT_CONTINUE;
    }
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("gcw_rebel_space_mining"))
        {
            return SCRIPT_CONTINUE;
        }
        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.gcw_rebel_space_mining.branchId");
        if (branchId == 6 && gcw_rebel_space_mining_handleBranch6(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 9 && gcw_rebel_space_mining_handleBranch9(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        if (branchId == 12 && gcw_rebel_space_mining_handleBranch12(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }
        chat.chat(npc, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.gcw_rebel_space_mining.branchId");
        return SCRIPT_CONTINUE;
    }
}
