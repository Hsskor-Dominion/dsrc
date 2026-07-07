package script.npc.skillteacher;

import script.*;
import script.library.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import static script.library.badge.BADGE_BOOK;
import static script.library.buff.*;
import static script.library.jedi_trials.SID_CLOSE_BUTTON;
import static script.library.jedi_trials.oneButtonMsgBox;
import static script.library.skill.*;

public class skillteacher extends script.base_script
{
    public skillteacher()
    {
    }
    public static final String VAR_SKILL_TEMPLATE = "npcSkillTemplate";
    public static final String CONVONAME = "skill_teacher";
    public static final String CONVOFILE = "skill_teacher";
    public static final String JEDI_TRAINER = "jedi_trainer";
    public static final String JEDI_TRAINER_LIGHT = "jedi_trainer_light";
    public static final String JEDI_TRAINER_DARK = "jedi_trainer_dark";
    public static final String SKILL_N = "skl_n";
    public static final String SKILL_D = "skl_d";
    public static final String SKILL_T = "skl_t";
    public static final String SCRIPT_NPC_CONVERSE = "npc.converse.npc_converse_menu";
    public static final String FACETO_VOLUME_NAME = "faceToTriggerVolume";
    public static final string_id[] OPT_DEFAULT =
    {
        new string_id(CONVOFILE, "opt1_1"),
        new string_id(CONVOFILE, "opt1_2"),
            new string_id(CONVOFILE, "opt1_3")
    };
    public static final string_id[] OPT_YES_BACK = 
    {
        new string_id(CONVOFILE, "yes"),
        new string_id(CONVOFILE, "back")
    };
    public static final string_id[] OPT_YES_NO = 
    {
        new string_id(CONVOFILE, "yes"),
        new string_id(CONVOFILE, "no")
    };
    public static final String TBL = "datatables/skill/skills.iff";
    public static final string_id PROSE_NSF = new string_id(CONVOFILE, "prose_nsf");
    public static final string_id PROSE_PAY = new string_id(CONVOFILE, "prose_pay");
    public static final string_id PROSE_SKILL_LEARNED = new string_id(CONVOFILE, "prose_skill_learned");
    public static final string_id PROSE_TRAIN_FAILED = new string_id(CONVOFILE, "prose_train_failed");
    public static final string_id SID_TRAINING_COST_REFUNDED = new string_id(CONVOFILE, "training_cost_refunded");
    public static final string_id SID_ALREADY_HAVE_THIS_SKILL = new string_id(CONVOFILE, "already_have_this_skill");
    public static final string_id SID_DO_NOT_HAVE_SKILL = new string_id(CONVOFILE, "do_not_have_skill");
    public static final int STATUS_UNKNOWN = -1;
    public static final int STATUS_NONE = 0;
    public static final int STATUS_LEARN = 1;
    public static final int STATUS_INFO = 2;
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        String teacherType = getStringObjVar(self, "trainer");
        if (teacherType != null)
        {
            if (teacherType.equals("trainer_shipwright"))
            {
                setCondition(self, CONDITION_CONVERSABLE);
                setCondition(self, CONDITION_SPACE_INTERESTING);
            }
            else 
            {
                setCondition(self, CONDITION_CONVERSABLE);
            }
        }
        createTriggerVolume(FACETO_VOLUME_NAME, 8.0f, true);
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        int mnu = mi.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data mdata = mi.getMenuItemById(mnu);
        mdata.setServerNotify(false);
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }
    public int OnIncapacitated(obj_id self, obj_id killer) throws InterruptedException
    {
        clearCondition(self, CONDITION_CONVERSABLE);
        clearCondition(self, CONDITION_SPACE_INTERESTING);
        detachScript(self, "npc.skillteacher.skillteacher");
        return SCRIPT_CONTINUE;
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        if (!utils.hasScriptVar(self, skill.SCRIPTVAR_SKILLS))
        {
            String tbl_trainer_skills = "datatables/npc_customization/skill_table.iff";
            String teacherType = getStringObjVar(self, "trainer");
            String[] skillList = dataTableGetStringColumnNoDefaults(tbl_trainer_skills, teacherType);

            String planetName = getLocation(self).area;
            if (planetName == null) return -1;
            planetName = planetName.toLowerCase();

            // list of planets for reference
            int planetId = -1;
            switch (planetName) {
                case "corellia": planetId = 1; break;
                case "naboo": planetId = 2; break;
                case "tatooine": planetId = 3; break;
                case "rori": planetId = 4; break;
                case "lok": planetId = 5; break;
                case "endor": planetId = 6; break;
                case "talus": planetId = 7; break;
                case "mustafar": planetId = 8; break;
                case "dantooine": planetId = 9; break;
                case "yavin4": planetId = 10; break;
                case "dathomir": planetId = 11; break;
                case "kashyyyk_main": planetId = 12; break;
            }

            // handle special trainer type
            if (teacherType != null && teacherType.equals("trainer_creaturehandler"))
            {
                if (!hasScript(self, "systems.pet_tradein.pet_tradein"))
                {
                    attachScript(self, "systems.pet_tradein.pet_tradein");
                }
            }

            // if no skills found, detach
            if (skillList == null || skillList.length == 0)
            {
                detachScript(self, "npc.skillteacher.skillteacher");
                return SCRIPT_OVERRIDE;
            }

            // set conditions and name
            if (teacherType != null)
            {
                setCondition(self, CONDITION_CONVERSABLE); // default condition

                if (teacherType.equals("trainer_shipwright"))
                {
                    setCondition(self, CONDITION_SPACE_INTERESTING);
                }

                // give "Desann" or other name if trainer_fs and NOT on Dathomir (planetId 11)
                if (teacherType.equals("trainer_fs") && planetId != 11)
                {
                    setName(self, "Desann");
                }
                if (teacherType.equals("trainer_fs") && planetId == 11)
                {
                    setName(self, "Noldan");
                }
            }

            // save skills
            utils.setBatchScriptVar(self, skill.SCRIPTVAR_SKILLS, skillList);

            // optionally set Jedi skills
            skillList = dataTableGetStringColumnNoDefaults(tbl_trainer_skills, "trainer_jedi");
            if (skillList != null && skillList.length > 0)
            {
                utils.setBatchScriptVar(self, skill.SCRIPTVAR_JEDI_SKILLS, skillList);
            }
        }

        createTriggerVolume(FACETO_VOLUME_NAME, 8.0f, true);
        return SCRIPT_CONTINUE;
    }
    public int OnStartNpcConversation(obj_id self, obj_id speaker) throws InterruptedException
    {
        if (hasObjVar(speaker, "jedi.usingSui"))
        {
            string_id strSpam = new string_id("jedi_spam", "cant_train_while_converting");
            chat.chat(self, speaker, strSpam, chat.ChatFlag_targetOnly);
            return SCRIPT_CONTINUE;
        }
        int city_id = getCityAtLocation(getLocation(self), 0);
        if (cityExists(city_id) && city.isCityBanned(speaker, city_id))
        {
            sendSystemMessage(speaker, new string_id("city/city", "city_banned"));
            return SCRIPT_CONTINUE;
        }
        String trainerType = "trainer_unknown";
        if (hasObjVar(self, "trainer"))
        {
            trainerType = getStringObjVar(self, "trainer");
        }
        if (trainerType.equals("trainer_shipwright") && !features.isSpaceEdition(speaker))
        {
            doAnimationAction(self, "thumbs_down");
            sendSystemMessage(speaker, new string_id("skill_teacher", "requires_jtl"));
            chat.publicChat(self, speaker, new string_id("skill_teacher", "too_complicated"));
            return SCRIPT_CONTINUE;
        }
        faceTo(self, speaker);
        if (!checkSkillStatus(self, speaker))
        {
            return SCRIPT_CONTINUE;
        }
        if (jedi.isJediTrainerForPlayer(speaker, self))
        {
            if (hasObjVar(speaker, "jedi.intFindNewTrainer"))
            {
                messageTo(speaker, "findNewTrainer", null, 0, false);
                string_id strSpam = new string_id("jedi_spam", "not_your_trainer");
                chat.chat(self, speaker, strSpam, chat.ChatFlag_targetOnly);
                return SCRIPT_CONTINUE;
            }
        }
        string_id msg = new string_id(CONVOFILE, trainerType);
        if (isJedi(speaker) && jedi.isJediTrainerForPlayer(speaker, self))
        {
            String jedi_convo = getJediConvoFile(speaker);
            if (jedi_convo != null && !jedi_convo.equals(""))
            {
                msg = new string_id(jedi_convo, "greeting");
            }
            else 
            {
                msg = new string_id(JEDI_TRAINER, "greeting");
            }
        }
        npcStartConversation(speaker, self, CONVONAME, msg, OPT_DEFAULT);
        return SCRIPT_CONTINUE;
    }
    public int OnEndNpcConversation(obj_id self, obj_id speaker) throws InterruptedException
    {
        utils.removeScriptVar(speaker, self.toString());
        removeObjVar(self, "confirmTeach." + speaker);
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id self, String convoName, obj_id speaker, string_id sid_response) throws InterruptedException
    {
        String trainerType = getStringObjVar(self, "trainer");
        if (trainerType != null)
        {
            if (trainerType.equals("trainer_shipwright") && !features.isSpaceEdition(speaker))
            {
                doAnimationAction(self, "thumbs_down");
                sendSystemMessage(speaker, new string_id("skill_teacher", "requires_jtl"));
                chat.publicChat(self, speaker, new string_id("skill_teacher", "too_complicated"));
                return SCRIPT_CONTINUE;
            }
        }
        if (!convoName.equals(CONVONAME) && !convoName.equals(JEDI_TRAINER))
        {
            return SCRIPT_CONTINUE;
        }
        if (!checkSkillStatus(self, speaker))
        {
            return SCRIPT_CONTINUE;
        }
        String tbl = sid_response.getTable();
        String response = sid_response.getAsciiId();
        int status = utils.getIntScriptVar(speaker, self.toString());
        boolean checkArray = true;
        String convo = CONVOFILE;
        if (isJedi(speaker) && jedi.isJediTrainerForPlayer(speaker, self))
        {
            String jedi_convo = getJediConvoFile(speaker);
            if (jedi_convo != null && !jedi_convo.equals(""))
            {
                convo = jedi_convo;
            }
        }
        if (tbl.equals(SKILL_N))
        {
            string_id msg = new string_id(CONVOFILE, "msg3_1");
            boolean skillGranted = false;
            switch (status)
            {
                case STATUS_LEARN:
                String[] qualifiedSkills = skill.getQualifiedTeachableSkills(speaker, self);
                if (qualifiedSkills == null)
                {
                }
                else 
                {
                    if (utils.getElementPositionInArray(qualifiedSkills, response) > -1)
                    {
                        string_id sid_skillName = new string_id(SKILL_N, response);
                        int cost = 1000;//base cost?
                        float skillMod = getEnhancedSkillStatisticModifier(speaker, "force_persuade");
                        skillMod = skillMod * 0.01f;
                        float discount = cost * skillMod;
                        cost = cost - (int)discount;
                        boolean newbieTraining = hasObjVar(speaker, "newbie.hasSkill");
                        if (hasObjVar(speaker, "newbie.trained"))
                        {
                            newbieTraining = false;
                        }
                        if (cost > 0 && (!newbieTraining))
                        {
                            int totalMoney = getTotalMoney(speaker);
                            if (totalMoney < cost)
                            {
                                prose_package pp = prose.getPackage(PROSE_NSF, sid_skillName, cost);
                                sendSystemMessageProse(speaker, pp);
                            }
                            else 
                            {
                                String ovPath = "confirmTeach." + speaker;
                                setObjVar(self, ovPath + ".sid_skillname", sid_skillName);
                                setObjVar(self, ovPath + ".cost", cost);
                                string_id PROSE_COST = new string_id(convo, "prose_cost");
                                prose_package ppConfirm = prose.getPackage(PROSE_COST, sid_skillName, cost);
                                npcSpeak(speaker, ppConfirm);
                                npcSetConversationResponses(speaker, OPT_YES_NO);
                                return SCRIPT_CONTINUE;
                            }
                            npcSpeak(speaker, new string_id(convo, "msg1_1"));
                            npcSetConversationResponses(speaker, OPT_DEFAULT);
                            return SCRIPT_CONTINUE;
                        }
                        else 
                        {
                            if (completeSkillPurchase(speaker, response))
                            {
                                if (response.equals("jedi_light_side_journeyman_novice") || response.equals("jedi_dark_side_journeyman_novice"))
                                {
                                    npcSpeak(speaker, new string_id(JEDI_TRAINER, "chosen_path"));
                                    npcEndConversation(speaker);
                                    setObjVar(speaker, "jedi.intFindNewTrainer", 1);
                                    return SCRIPT_CONTINUE;
                                }
                                msg = new string_id(convo, "msg3_2");
                                doAnimationAction(self, anims.PLAYER_FC_WINK);
                                if (response.equals("combat_bountyhunter_novice"))
                                {
                                    dictionary dctParams = new dictionary();
                                    dctParams.put("eventName", "BountyHunterNoviceGranted");
                                    messageTo(speaker, "handleHolocronEvent", dctParams, 0, false);
                                }
                                skillGranted = true;
                                if (hasObjVar(speaker, "newbie.hasSkill"))
                                {
                                    setObjVar(speaker, "newbie.trained", true);
                                }
                                if (hasSurpassedTrainer(self, speaker))
                                {
                                    prose_package ppFarewell = prose.getPackage(new string_id(convo, "surpass_trainer"), speaker);
                                    String chatType = chat.getChatType(self);
                                    String moodType = chat.getChatMood(self);
                                    npcSpeak(speaker, ppFarewell);
                                    npcEndConversation(speaker);
                                    return SCRIPT_CONTINUE;
                                }
                            }
                            else 
                            {
                                msg = new string_id(convo, "error_grant_skill");
                                doAnimationAction(self, anims.PLAYER_SHRUG_HANDS);
                            }
                        }
                    }
                }
                break;
                case STATUS_INFO:
                msg = new string_id(CONVOFILE, "msg3_3");
                String[] skillData = getSkillData(response, speaker);
                if ((skillData != null) && (skillData.length > 0))
                {
                    if (utils.getElementPositionInArray(getSkillListingForPlayer(speaker), response) > -1)
                    {
                        sui.listbox(speaker, utils.packStringId(SID_ALREADY_HAVE_THIS_SKILL), "@" + tbl + ":" + response, skillData);
                    }
                    else 
                    {
                        prose_package ppDoNotHaveSkill = prose.getPackage(SID_DO_NOT_HAVE_SKILL);
                        prose.setTO(ppDoNotHaveSkill, new string_id("skl_d", response));
                        String prompt = " \0" + packOutOfBandProsePackage(null, ppDoNotHaveSkill);
                        sui.listbox(speaker, prompt, "@" + tbl + ":" + response, skillData);
                    }
                }
                break;
                default:
                break;
            }
            npcSpeak(speaker, msg);
            npcSetConversationResponses(speaker, OPT_DEFAULT);
            return SCRIPT_CONTINUE;
        }
        else 
        {
            Vector opt = utils.concatArrays(null, OPT_DEFAULT);
            String[] skills = null;
            string_id msg = new string_id(convo, "msg2_1");
            switch (response) {
                case "opt1_1":
                    msg = new string_id(convo, "msg2_1");
                    skills = skill.getQualifiedTeachableSkills(speaker, self);
                    utils.setScriptVar(speaker, self.toString(), STATUS_LEARN);
                    break;
                case "opt1_2":
                    msg = new string_id(convoName, "msg2_2");
                    skills = skill.getTeachableSkills(speaker, self);//this works great! it pulls up all the skills the trainer has, and then I ask about each ones requirements. We need to learn from this to get getQualifiedTeachableSkills fixed
                    utils.setScriptVar(speaker, self.toString(), STATUS_INFO);
                    break;
                case "opt1_3":
                {
                    msg = new string_id(convoName, "msg3_4");
                    showPlayerSkills(speaker);

                    String[] trainerSkills = skill.getTeachableSkills(speaker, self);
                    String[] playerSkills = getSkillListingForPlayer(speaker);

                    if (trainerSkills != null && playerSkills != null)
                    {
                        Vector owned = new Vector();

                        for (int i = 0; i < trainerSkills.length; i++)
                        {
                            String s = trainerSkills[i];
                            if (utils.getElementPositionInArray(playerSkills, s) > -1)
                            {
                                owned.addElement(s);
                            }
                        }

                        // manual Vector → String[] conversion
                        skills = new String[owned.size()];
                        for (int i = 0; i < owned.size(); i++)
                        {
                            skills[i] = (String)owned.elementAt(i);
                        }
                    }

                    utils.setScriptVar(speaker, self.toString(), STATUS_INFO);
                    // --- NEW: Show skill points used ---
                    int pointsUsed = getSkillPointsForPlayer(speaker); // total of POINTS_REQUIRED
                    int maxPoints = 250; // hard cap
                    prose_package ppPoints = prose.getPackage(new string_id(convo, "skill_points_used"), pointsUsed, maxPoints);
                    sendSystemMessageProse(speaker, ppPoints);
                    break;
                }
                case "yes":
                    String ovPath = "confirmTeach." + speaker;
                    if (hasObjVar(self, ovPath)) {
                        string_id sid_skillName = getStringIdObjVar(self, ovPath + ".sid_skillname");
                        int cost = getIntObjVar(self, ovPath + ".cost");
                        if (sid_skillName != null && cost > 0) {
                            prose_package pp = prose.getPackage(PROSE_PAY, sid_skillName, cost);
                            sendSystemMessageProse(speaker, pp);
                            dictionary d = new dictionary();
                            d.put("skillName", sid_skillName.getAsciiId());
                            money.requestPayment(speaker, self, cost, "attemptedPayment", d, true);
                        }
                        removeObjVar(self, ovPath);
                    }
                    msg = new string_id(convo, "msg_yes");
                    checkArray = false;
                    utils.removeScriptVar(speaker, self.toString());
                    break;
                case "no":
                    msg = new string_id(convo, "msg_no");
                    checkArray = false;
                    utils.removeScriptVar(speaker, self.toString());
                    break;
                default:
                    checkArray = false;
                    utils.removeScriptVar(speaker, self.toString());
                    break;
            }
            if ((checkArray) && ((skills == null) || (skills.length == 0)))
            {
                msg = new string_id(convo, "error_empty_category");//this is the error I'm getting when I ask a trainer to tell me my skills
            }
            else if (!checkArray)
            {
            }
            else 
            {
                opt.clear();
                for (String skill : skills) {
                    opt = utils.addElement(opt, new string_id(SKILL_N, skill));
                }
                opt = utils.addElement(opt, new string_id(CONVOFILE, "back"));
            }
            npcSpeak(speaker, msg);
            npcSetConversationResponses(speaker, opt);
            return SCRIPT_CONTINUE;
        }
    }
    public void showPlayerSkills(obj_id player)
            throws InterruptedException
    {
        String[] playerSkills = getSkillListingForPlayer(player);

        if (playerSkills == null)
        {
            return;
        }

        String message = "";

        for (int p = 0; p < PROFESSION_ROOTS.length; p++)
        {
            String professionName = PROFESSION_ROOTS[p][0];
            String professionRoot = PROFESSION_ROOTS[p][1];

            boolean foundAny = false;
            String section = professionName + "\n";

            for (int i = 0; i < playerSkills.length; i++)
            {
                String skill = playerSkills[i];

                if (skill.startsWith(professionRoot))
                {
                    foundAny = true;

                    section += "   " + skill + "\n";
                }
            }

            if (foundAny)
            {
                message += section + "\n";
            }
        }

        sui.msgbox(
                player,
                player,
                message,
                sui.OK_ONLY,
                "Known Skills");
    }
    private static final String[][] PROFESSION_ROOTS =
    {
                    {"Artisan", "crafting_artisan"},
                    {"Brawler", "combat_brawler"},
                    {"Marksman", "combat_marksman"},
                    {"Medic", "science_medic"},
                    {"Entertainer", "social_entertainer"},
                    {"Scout", "outdoors_scout"},

                    {"Merchant", "crafting_merchant"},
                    {"Armorsmith", "crafting_armorsmith"},
                    {"Architect", "crafting_architect"},
                    {"Weaponsmith", "crafting_weaponsmith"},
                    {"Chef", "crafting_chef"},
                    {"Tailor", "crafting_tailor"},
                    {"Droid Engineer", "crafting_droidengineer"},
                    {"Shipwright", "crafting_shipwright"},

                    {"Teras Kasi", "combat_unarmed"},
                    {"Fencer", "combat_1hsword"},
                    {"Swordsman", "combat_2hsword"},
                    {"Pikeman", "combat_polearm"},
                    {"Smuggler", "combat_smuggler"},
                    {"Commando", "combat_commando"},
                    {"Pistoleer", "combat_pistol"},
                    {"Carbineer", "combat_carbine"},
                    {"Rifleman", "combat_rifle"},
                    {"Bounty Hunter", "combat_bountyhunter"},

                    {"Doctor", "science_doctor"},
                    {"Combat Medic", "science_combatmedic"},

                    {"Dancer", "social_dancer"},
                    {"Image Designer", "social_imagedesigner"},
                    {"Musician", "social_musician"},

                    {"Ranger", "outdoors_ranger"},
                    {"Creature Handler", "outdoors_creaturehandler"},
                    {"Bio Engineer", "outdoors_bioengineer"},
                    {"Squad Leader", "outdoors_squadleader"},

                    {"Lightsaber", "force_discipline_light_saber"},
                    {"Force Powers", "force_discipline_powers"},
                    {"Force Healing", "force_discipline_healing"},
                    {"Force Enhancements", "force_discipline_enhancements"},
                    {"Force Defender", "force_discipline_defender"}
    };
    public int attemptedPayment(obj_id self, dictionary params) throws InterruptedException
    {
        if ((params == null) || (params.isEmpty()))
        {
            return SCRIPT_CONTINUE;
        }
        int retCode = money.getReturnCode(params);
        if (retCode != money.RET_SUCCESS)
        {
            return SCRIPT_CONTINUE;
        }
        obj_id player = params.getObjId(money.DICT_PLAYER_ID);
        if (!isIdValid(player) || (!isPlayer(player)))
        {
            return SCRIPT_CONTINUE;
        }
        String skillName = params.getString("skillName");
        if ((skillName == null) || (skillName.equals("")))
        {
            return SCRIPT_CONTINUE;
        }
        int cost = params.getInt(money.DICT_TOTAL);
        if (completeSkillPurchase(player, skillName))
        {
            money.bankTo(self, money.ACCT_SKILL_TRAINING, cost);
            grantSkill(player, skillName);
            deductXpCostForSkillPurchase(player, skillName);
        }
        else
        {
            prose_package ppCostRefunded = prose.getPackage(SID_TRAINING_COST_REFUNDED);
            prose.setDI(ppCostRefunded, cost);
            sendSystemMessageProse(player, ppCostRefunded);
            money.bankTo(self, player, cost);
        }
        return SCRIPT_CONTINUE;
    }
    public boolean completeSkillPurchase(obj_id player, String skillName) throws InterruptedException
    {
        if (!isIdValid(player) || (!isPlayer(player)))
        {
            return false;
        }
        if ((skillName == null) || (skillName.equals("")))
        {
            return false;
        }
        boolean learned = true;
        prose_package pp;
        if (skill.purchaseSkill(player, skillName))
        {
            pp = prose.getPackage(PROSE_SKILL_LEARNED, new string_id(SKILL_N, skillName));
            if (fs_quests.isVillageEligible(player))//looks like legacy nonsese code?
            {
                if (!hasObjVar(player, fs_quests.VAR_VILLAGE_COMPLETE))
                {
                    if (skillName.contains("force_sensitive_"))
                    {
                        if (fs_quests.getBranchesLearned(player) >= 6)
                        {
                            setObjVar(player, fs_quests.VAR_VILLAGE_COMPLETE, 1);
                            CustomerServiceLog("fs_quests", "%TU has completed the village by attaining six FS skill branches.", player, null);
                        }
                    }
                }
            }
        }
        else
        {
            pp = prose.getPackage(PROSE_TRAIN_FAILED, new string_id(SKILL_N, skillName));
            learned = false;
        }
        sendSystemMessageProse(player, pp);
        return learned;
    }
    public String[] getSkillData(String skillName, obj_id player) throws InterruptedException
    {
        if (skillName.equals(""))
        {
            return null;
        }

        Vector ret = new Vector();
        ret.setSize(0);

        // --- Required skills ---
        ret = utils.addElement(ret, "REQUIRED SKILLS");
        String[] skillReqs = getSkillPrerequisiteSkills(skillName);
        if (skillReqs == null)
        {
            ret = utils.addElement(ret, " none");
        }
        else
        {
            for (String skillReq : skillReqs)
            {
                String sName = getString(new string_id("skl_n", skillReq));
                ret = utils.addElement(ret, " " + sName);
            }
        }

        // --- XP costs ---
        ret = utils.addElement(ret, "XP COSTS");
        dictionary xpReqs = getSkillPrerequisiteExperience(skillName);

        if ((xpReqs == null) || (xpReqs.isEmpty()))
        {
            ret = utils.addElement(ret, " none");
        }
        else
        {
            java.util.Enumeration xp = xpReqs.keys();
            while (xp.hasMoreElements())
            {
                String xpType = (String)xp.nextElement();
                int requiredXp = xpReqs.getInt(xpType);

                // Localized XP name - replace this probably?
                String sXp = getString(new string_id("exp_n", xpType));

                // Player's current XP (we need this working)
                int playerXp = getExperiencePoints(player, xpType);

                // Display as current / required
                ret = utils.addElement(
                        ret,
                        " " + sXp + ": " + playerXp + " / " + requiredXp
                );
            }
        }

        String[] _ret = new String[0];
        if (ret != null)
        {
            _ret = new String[ret.size()];
            ret.toArray(_ret);
        }
        return _ret;
    }
    public boolean checkSkillStatus(obj_id trainer, obj_id player) throws InterruptedException
    {
        if (!isIdValid(trainer) || !isIdValid(player))
        {
            return false; // Invalid trainer or player
        }

        // Check if the player is Jedi and has the correct trainer
        if (isJedi(player) && jedi.isJediTrainerForPlayer(player, trainer) && ((isInFocus(player) || isInStance(player))))
        {
            return true;
        }

        String[] pSkills = getSkillListingForPlayer(player); // Player's current skills
        String[] tSkills = skill.getTeacherSkills(trainer, player); // Skills trainer can teach

        // If trainer has no teachable skills, return false
        if (tSkills == null || tSkills.length == 0)
        {
            return false;
        }

        String[] lowSkills = getSkillPrerequisiteSkills(tSkills[0]); // Prerequisite skills for the first teachable skill

        // Ignore expertise points check entirely
        if (lowSkills != null && !utils.isSubset(pSkills, lowSkills))
        {
            // Notify player they don't qualify due to missing prerequisites
            string_id msg = new string_id(CONVOFILE, "no_qualify");
            chat.chat(trainer, player, msg, chat.ChatFlag_targetOnly);
            npcEndConversation(player);

            // Show list of missing skills, if any
            Vector entries = new Vector();
            for (String lowSkill : lowSkills) {
                entries.add("@skl_n:" + lowSkill);
            }
            if (!entries.isEmpty())
            {
                String title = "@skill_teacher:no_qualify_title";
                String prompt = "@skill_teacher:no_qualify_prompt";
                sui.listbox(trainer, player, prompt, sui.OK_ONLY, title, entries, "noHandler");
            }
            return false;
        }

        // If player already has the skills the trainer offers
        if (utils.isSubset(pSkills, tSkills))
        {
            string_id msg = new string_id(CONVOFILE, "topped_out");
            chat.chat(trainer, player, msg, chat.ChatFlag_targetOnly);
            npcEndConversation(player);
            return false;
        }

        // Skip "no_skill_pts" message entirely
        return true;
    }
    public boolean hasSurpassedTrainer(obj_id trainer, obj_id player) throws InterruptedException
    {
        String[] pSkills = getSkillListingForPlayer(player);
        String[] tSkills = skill.getTeacherSkills(trainer, player);
        if (tSkills == null || tSkills.length == 0)
        {
            return false;
        }
        return utils.isSubset(pSkills, tSkills);
    }
    public int OnTriggerVolumeEntered(obj_id self, String volumeName, obj_id breacher) throws InterruptedException
    {
        if (!isPlayer(breacher))
        {
            return SCRIPT_CONTINUE;
        }
        if (!volumeName.equals(FACETO_VOLUME_NAME))
        {
            return SCRIPT_CONTINUE;
        }
        if (isInNpcConversation(self))
        {
            return SCRIPT_CONTINUE;
        }
        if (canSee(self, breacher))
        {
            faceTo(self, breacher);
        }
        return SCRIPT_CONTINUE;
    }
    public String getJediConvoFile(obj_id player) throws InterruptedException
    {
        if (!isIdValid(player) || !isJedi(player))
        {
            return null;
        }
        if (hasBuff(player, "fs_buff_def_1_1"))//modernized for CU/NGE - SWG Chimaera
        {
            return JEDI_TRAINER_LIGHT;
        }
        if (hasBuff(player, "fs_buff_ca_1")) //was jedi_dark_side_journeyman_novice
        {
            return JEDI_TRAINER_DARK;
        }
        return JEDI_TRAINER;
    }
}
