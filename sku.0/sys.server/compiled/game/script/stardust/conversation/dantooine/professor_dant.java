package script.stardust.conversation.dantooine;

import script.base_script;
import script.dictionary;
import script.library.*;
import script.obj_id;
import script.string_id;

import static script.library.groundquests.grantQuest;

public class professor_dant extends base_script
{
    public professor_dant() {}

    public static final String c_stringFile = "conversation/professor_dant";
    public static final String QUEST_DANTOOINE_BM = "stardust_bm_dantooine";
    public static final String SKILL_DANTOOINE_INDEX = "stardust_bm_dantooine";

    // --------------------------------------------------
    // CONDITIONS
    // --------------------------------------------------
    public boolean professor_dant_condition_hasActiveBeastPet(obj_id player, obj_id npc) throws InterruptedException
    {
        obj_id beast = beast_lib.getBeastOnPlayer(player);

        if (!isIdValid(beast))
        {
            return false;
        }

        if (!exists(beast) || isDead(beast))
        {
            return false;
        }

        // Optional distance check (keeps cheesing away)
        if (getDistance(player, beast) > 32.0f)
        {
            return false;
        }

        return true;
    }

    public boolean professor_dant_condition_hasCompletedBattleQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        return groundquests.hasCompletedQuest(player, QUEST_DANTOOINE_BM);

    }

    public boolean professor_dant_condition_hasdantooineIndex(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasCompletedCollection(player, "dantooine_creature_index");
    }

    public boolean professor_dant_condition_hasdantooineTitle(obj_id player, obj_id npc) throws InterruptedException
    {
        return hasSkill(player, SKILL_DANTOOINE_INDEX);
    }

    // --------------------------------------------------
    // ACTIONS
    // --------------------------------------------------

    public void professor_dant_action_facePlayer(obj_id player, obj_id npc) throws InterruptedException
    {
        faceTo(npc, player);
    }

    public void professor_dant_action_grantBattleQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        grantQuest(player, QUEST_DANTOOINE_BM);
    }

    public void professor_dant_action_grantdantooineTitle(obj_id player, obj_id npc) throws InterruptedException
    {
        if (!hasSkill(player, SKILL_DANTOOINE_INDEX))
        {
            grantSkill(player, SKILL_DANTOOINE_INDEX);
        }
    }

    public void vendor_dant_action_showTokenVendorUI(obj_id player, obj_id npc) throws InterruptedException
    {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }

    // --------------------------------------------------
    // BRANCH HANDLERS
    // --------------------------------------------------

    // Branch 1: Accept the fight
    public int professor_dant_handleBranch1(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        // Player chooses to fight
        if (response.equals("player_choose_to_fight"))
        {
            if (!professor_dant_condition_hasActiveBeastPet(player, npc))
            {
                string_id message = new string_id(c_stringFile, "dant_need_beast_pet");
                npcEndConversationWithMessage(player, message);
                utils.removeScriptVar(player, "conversation.professor_dant.branchId");
                return SCRIPT_CONTINUE;
            }

            string_id message = new string_id(c_stringFile, "dant_let_us_fight");
            professor_dant_action_grantBattleQuest(player, npc);
            buff.applyBuff(player, "bm_attention_penalty_stardust");
            buff.applyBuff(player, "stasis");
            utils.removeScriptVar(player, "conversation.professor_dant.branchId");
            npcEndConversationWithMessage(player, message);
            return SCRIPT_CONTINUE;
        }

        // Player chooses to end the fight
        if (response.equals("player_choose_to_end_fight"))
        {
            // Remove any debuffs
            buff.removeBuff(player, "bm_attention_penalty_stardust");
            buff.removeBuff(player, "stasis");
            //need to remove the quest too
            int questId1 = questGetQuestId("quest/stardust_bm_dantooine");
            questClearQuest(questId1, player);

            // Optional pet cleanup
            obj_id pet = beast_lib.getBeastOnPlayer(player);
            if (isIdValid(pet) && exists(pet) && isDead(pet))
            {
                string_id message = new string_id(c_stringFile, "dant_sorry_pet_dead");
                npcEndConversationWithMessage(player, message);
            }
            else
            {
                string_id message = new string_id(c_stringFile, "dant_fight_ended");
                npcEndConversationWithMessage(player, message);
            }

            utils.removeScriptVar(player, "conversation.professor_dant.branchId");
            return SCRIPT_CONTINUE;
        }

        return SCRIPT_DEFAULT;
    }

    // Branch 2: dantooine Index check
    public int professor_dant_handleBranch2(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {
        if (response.equals("player_knows_its_their_destiny"))
        {
            if (professor_dant_condition_hasdantooineIndex(player, npc))
            {
                string_id message = new string_id(c_stringFile, "dant_congratulations_completed_dantooine");
                professor_dant_action_grantdantooineTitle(player, npc);
                vendor_dant_action_showTokenVendorUI(player, npc);
                buff.removeBuff(player, "bm_attention_penalty_stardust");//this way the player is not stuck with debuff after fight
                buff.removeBuff(player, "stasis");
                utils.removeScriptVar(player, "conversation.professor_dant.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
            else
            {
                string_id message = new string_id(c_stringFile, "dant_gotta_tame_them_all");
                buff.removeBuff(player, "bm_attention_penalty_stardust");
                buff.removeBuff(player, "stasis");
                utils.removeScriptVar(player, "conversation.professor_dant.branchId");
                npcEndConversationWithMessage(player, message);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_DEFAULT;
    }

    // --------------------------------------------------
    // NPC SETUP
    // --------------------------------------------------

    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if ((!isMob(self)) || (isPlayer(self)))
        {
            detachScript(self, "conversation.professor_dant");
        }
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        setName(self, "Professor dant (Beast Researcher)");
        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setInvulnerable(self, true);
        ai_lib.setDefaultCalmBehavior(self, ai_lib.BEHAVIOR_SENTINEL);
        setName(self, "Professor dant (Beast Researcher)");
        return SCRIPT_CONTINUE;
    }

    // --------------------------------------------------
    // CONVERSATION START
    // --------------------------------------------------

    public int OnStartNpcConversation(obj_id self, obj_id player) throws InterruptedException
    {
        obj_id npc = self;

        professor_dant_action_facePlayer(player, npc);

        // PHASE 1 — Offer battle quest
        if (!professor_dant_condition_hasCompletedBattleQuest(player, npc))
        {
            string_id message = new string_id(c_stringFile, "dant_seeks_creature_trainers_and_researchers");
            string_id responses[] =
                    {
                            new string_id(c_stringFile, "player_choose_to_fight"),
                            new string_id(c_stringFile, "player_choose_to_end_fight")
                    };
            utils.setScriptVar(player, "conversation.professor_dant.branchId", 1);
            npcStartConversation(player, npc, "professor_dant", message, responses);
            return SCRIPT_CONTINUE;
        }

        // PHASE 2 — dantooine Index discussion
        if (!professor_dant_condition_hasdantooineTitle(player, npc))
        {
            string_id message = new string_id(c_stringFile, "dant_talks_creature_index");
            string_id responses[] =
                    {
                            new string_id(c_stringFile, "player_knows_its_their_destiny")
                    };
            utils.setScriptVar(player, "conversation.professor_dant.branchId", 2);
            npcStartConversation(player, npc, "professor_dant", message, responses);
            return SCRIPT_CONTINUE;
        }

        // PHASE 3 — Vendor unlocked
        vendor_dant_action_showTokenVendorUI(player, npc);
        chat.chat(npc, player, new string_id(c_stringFile, "dant_master_researcher_greeting"));
        return SCRIPT_CONTINUE;
    }

    // --------------------------------------------------
    // RESPONSE HANDLER
    // --------------------------------------------------

    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals("professor_dant"))
        {
            return SCRIPT_CONTINUE;
        }

        obj_id npc = self;
        int branchId = utils.getIntScriptVar(player, "conversation.professor_dant.branchId");

        if (branchId == 1 && professor_dant_handleBranch1(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        if (branchId == 2 && professor_dant_handleBranch2(player, npc, response) == SCRIPT_CONTINUE)
        {
            return SCRIPT_CONTINUE;
        }

        utils.removeScriptVar(player, "conversation.professor_dant.branchId");
        chat.chat(npc, "Error: Conversation branch fell through.");
        return SCRIPT_CONTINUE;
    }
}