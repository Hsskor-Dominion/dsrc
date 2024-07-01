package script.systems.npc_lair;

import script.*;
import script.library.ai_lib;
import script.library.buff;
import script.library.incubator;
import script.library.resource;
import script.library.utils;
import script.dictionary;
import script.obj_id;
import script.menu_info;
import script.string_id;

public class lair_interactivity extends script.base_script {
    public lair_interactivity() {
    }

    public static final boolean LOGGING_ON = false;
    public static final String LOGGING_CATEGORY = "lair_interactivity";
    public static final String LAIR_SEARCHED = "lair.searched";
    public static final int LAIR_BUFF_INCREASE = 2;
    public static final string_id SID_SEARCH_LAIR = new string_id("lair_n", "search_lair");
    public static final string_id SID_FOUND_NOTHING = new string_id("lair_n", "found_nothing");
    public static final string_id SID_INVENTORY_FULL = new string_id("lair_n", "inventory_full");
    public static final string_id SID_FOUND_EGGS = new string_id("lair_n", "found_eggs");
    public static final string_id SID_FOUND_BUGS = new string_id("lair_n", "found_bugs");
    public static final String[] BUG_SAMPLE_OBJECTS = {
            "object/tangible/bug_jar/sample_bats.iff",
            "object/tangible/bug_jar/sample_bees.iff",
            "object/tangible/bug_jar/sample_butterflies.iff",
            "object/tangible/bug_jar/sample_flies.iff",
            "object/tangible/bug_jar/sample_glowzees.iff",
            "object/tangible/bug_jar/sample_moths.iff"
    };
    public static final String[] RARE_BUG_SAMPLE_OBJECTS = {
            "object/tangible/fishing/bait/bait_grub.iff",
            "object/tangible/fishing/bait/bait_worm.iff",
            "object/tangible/fishing/bait/bait_insect.iff"
    };
    public static final String[] BEAST_OPTIONS_FOR_LAIRS = {
            "acklay",
            "angler",
            "bageraset",
            "bantha",
            "bark_mite",
            "baz_nitch",
            "bearded_jax",
            "blistmok",
            "blurrg",
            "boar_wolf",
            "bocatt",
            "bol",
            "bolle_bol",
            "bolma",
            "bolotaur",
            "bordok",
            "borgle",
            "brackaset",
            "capper_spineflap",
            "carrion_spat",
            "choku",
            "chuba",
            "condor_dragon",
            "corellian_butterfly",
            "corellian_sand_panther",
            "corellian_slice_hound",
            "crystal_snake",
            "cu_pa",
            "dalyrake",
            "dewback",
            "dune_lizard",
            "durni",
            "dwarf_nuna",
            "eopie",
            "falumpaset",
            "fambaa",
            "fanned_rawl",
            "flewt",
            "flit",
            "fynock",
            "gackle_bat",
            "gaping_spider",
            "gnort",
            "graul",
            "gronda",
            "gualama",
            "gubbur",
            "guf_drolg",
            "gulginaw",
            "gurk",
            "gurnaset",
            "gurreck",
            "hanadak",
            "hermit_spider",
            "horned_krevol",
            "horned_rasp",
            "huf_dun",
            "huurton",
            "ikopi",
            "jundak",
            "kaadu",
            "kai_tok",
            "kashyyyk_bantha",
            "kima",
            "kimogila",
            "kittle",
            "kliknik",
            "krahbu",
            "kubaza_beetle",
            "kusak",
            "kwi",
            "langlatch",
            "lantern_bird",
            "lava_flea",
            "malkloc",
            "mamien",
            "mawgax",
            "merek",
            "minstyngar",
            "mott",
            "mouf",
            "murra",
            "mutated_acklay",
            "mutated_boar",
            "mutated_borgax",
            "mutated_cat",
            "mutated_chuba_fly",
            "mutated_cu_pa",
            "mutated_dewback",
            "mutated_griffon",
            "mutated_jax",
            "mutated_quenker",
            "mutated_rancor",
            "mutated_slice_hound",
            "mutated_varasquactyl",
            "mynock",
            "narglatch",
            "nerf",
            "nuna",
            "peko_peko",
            "perlek",
            "pharple",
            "piket",
            "plumed_rasp",
            "pugoriss",
            "purbole",
            "quenker",
            "rancor",
            "remmer",
            "reptilian_flier",
            "roba",
            "rock_mite",
            "ronto",
            "salt_mynock",
            "sharnaff",
            "shaupaut",
            "shear_mite",
            "skreeg",
            "snorbal",
            "spined_puc",
            "spined_snake",
            "squall",
            "squill",
            "stintaril",
            "swirl_prong",
            "tanc_mite",
            "tanray",
            "tauntaun",
            "thune",
            "torton",
            "tulrus",
            "tusk_cat",
            "tybis",
            "uller",
            "varactyl",
            "veermok",
            "verne",
            "vesp",
            "vir_vur",
            "voritor_lizard",
            "vynock",
            "walluga",
            "wampa",
            "webweaver",
            "whisper_bird",
            "womp_rat",
            "woolamander",
            "worrt",
            "xandank",
            "zucca_boar",
            "reek",
            "nexu"
    };

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException {
        if (!hasObjVar(self, "npc_lair.isCreatureLair")) {
            detachScript(self, "systems.npc_lair.lair_interactivity");
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException {
        return SCRIPT_CONTINUE;
    }

    public int searchLair(obj_id self, dictionary params) throws InterruptedException {
        if (params == null) {
            return SCRIPT_CONTINUE;
        }
        obj_id player = params.getObjId("player");
        searchLair(self, player);
        return SCRIPT_CONTINUE;
    }

    public void searchLair(obj_id self, obj_id player) throws InterruptedException {
        if (utils.hasScriptVar(self, LAIR_SEARCHED)) {
            return;
        }

        int searchagain = rand(0, 2);
        if (searchagain == 0) {
            utils.setScriptVar(self, LAIR_SEARCHED, 1);
        }

        obj_id pInv = utils.getInventoryContainer(player);
        if (!isValidId(pInv) || !exists(pInv)) {
            return;
        }

        if (getVolumeFree(pInv) <= 0) {
            sendSystemMessage(player, SID_INVENTORY_FULL);
            return;
        }

        int forageBonus = (int) getSkillStatisticModifier(player, "foraging");
        int searchRoll = rand(1, 100) + forageBonus;

        if (searchRoll < 20) { // increased chance of failure, but no chance for players invested in survival
            sendSystemMessage(player, SID_FOUND_NOTHING);
        } else if (searchRoll < 30) {
            sendSystemMessage(player, SID_FOUND_BUGS);
            if (rand(1, 100) < 50) {
                createObject(RARE_BUG_SAMPLE_OBJECTS[rand(0, RARE_BUG_SAMPLE_OBJECTS.length - 1)], pInv, "");
            } else {
                createObject(BUG_SAMPLE_OBJECTS[rand(0, BUG_SAMPLE_OBJECTS.length - 1)], pInv, "");
            }
        } else if (searchRoll < 200) {
            sendSystemMessage(player, SID_FOUND_EGGS);

            // Check if the player has the required skills
            if (buff.hasBuff(player, "bm_creature_knowledge")) {
                findBeastEgg(self, player);
            } else {
                sendSystemMessageTestingOnly(player, "You must use creature knowledge ability to find beast eggs.");
            }

            int amt = rand(10, 50); // increased lair_egg_buff for harvesting for QoL
            if (buff.hasBuff(player, "lair_egg_buff")) {
                amt = amt * LAIR_BUFF_INCREASE;
            }

            obj_id[] resourceList = resource.createRandom("meat_egg_" + getCurrentSceneName(), amt, getLocation(self), pInv, player, 2);
            if (resourceList == null) {
                blog("ai.handleMilking: cannot get resource data from resource.createRandom function");
                CustomerServiceLog("milking_and_lair_search", "handleMilking: Player: " + getName(player) + " OID: " + player + " attempted to milk but could not resource data from resource.createRandom function while milking " + self + " " + getName(self));
                return;
            }

            location curloc = getLocation(player);
            if (curloc == null) {
                blog("ai.handleMilking: cannot get resource data from getLocation cpp");
                CustomerServiceLog("milking_and_lair_search", "handleMilking: Player: " + getName(player) + " OID: " + player + " attempted to milk but could not retrieve location data while milking " + self + " " + getName(self));
                return;
            }

            for (obj_id obj_id : resourceList) {
                blog("" + obj_id);
                setLocation(obj_id, curloc);
                putIn(obj_id, pInv, player);
            }
        } else {
            sendSystemMessage(player, SID_FOUND_BUGS);
            if (rand(1, 100) < 50) {
                createObject(RARE_BUG_SAMPLE_OBJECTS[rand(0, RARE_BUG_SAMPLE_OBJECTS.length - 1)], pInv, "");
            } else {
                createObject(BUG_SAMPLE_OBJECTS[rand(0, BUG_SAMPLE_OBJECTS.length - 1)], pInv, "");
            }
        }
    }

    public int findBeastEgg(obj_id self, obj_id player) throws InterruptedException {
        obj_id[] creaturesInRange = getCreaturesInRange(getLocation(self), 32.0f);
        String creatureType = "";

        for (obj_id creature : creaturesInRange) {
            String creatureName = ai_lib.getCreatureName(creature);
            if (creatureName == null) {
                continue;
            }

            // Handle specific naming discrepancy
            if (creatureName.contains("womprat")) {
                creatureName = creatureName.replace("womprat", "womp_rat"); // Correct if it contains "womprat"
            }

            for (String beast : BEAST_OPTIONS_FOR_LAIRS) {
                if (creatureName.contains(beast)) {
                    creatureType = beast; // Assign the matching creature type
                    break;
                }
            }
            if (!creatureType.isEmpty()) {
                break;
            }
        }

        if (creatureType.isEmpty()) {
            sendSystemMessageTestingOnly(player, "No suitable baby creatures found in range.");
            return SCRIPT_CONTINUE;
        }

        // Create the egg object
        String creatureName = "bm_" + creatureType;
        obj_id inv = utils.getInventoryContainer(player);
        obj_id egg = createObject("object/tangible/item/beast/bm_egg.iff", inv, "");
        if (egg == null || !isIdValid(egg)) {
            sendSystemMessageTestingOnly(player, "Failed to create egg for creature type: " + creatureType);
            return SCRIPT_CONTINUE;
        }

        // Set up the egg with dummy data
        int hashCreatureType = incubator.getHashType(creatureName);
        incubator.setUpEggWithDummyData(player, egg, hashCreatureType);

        // Inform the player about the found egg
        sendSystemMessageTestingOnly(player, "Beast egg created: " + creatureType);

        return SCRIPT_CONTINUE;
    }

    public boolean blog(String msg) throws InterruptedException {
        if (LOGGING_ON && !msg.equals("")) {
            LOG(LOGGING_CATEGORY, msg);
        }
        return true;
    }
}