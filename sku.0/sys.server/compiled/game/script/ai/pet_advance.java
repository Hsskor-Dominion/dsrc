package script.ai;

import script.*;
import script.library.*;

import static script.ai.ai_combat.isInCombat;
import static script.systems.npc_lair.lair_interactivity.BEAST_OPTIONS_FOR_LAIRS;

public class pet_advance extends base_script {
    private static final int MENU_ATTEMPT_TAME = menu_info_types.ITEM_USE + 50;
    private static final int MENU_ATTEMPT_FEED = menu_info_types.ITEM_USE + 51;
    private static final int BABY_LIFETIME_SECONDS = 600; // 10 minutes
    private static final String MSG_SELF_DESTRUCT = "pet_baby_self_destruct";

    public int OnAttach(obj_id self) throws InterruptedException
    {
        if (!isIdValid(self))
            return SCRIPT_CONTINUE;

        // Only apply to baby lair creatures
        if (!utils.hasScriptVar(self, "npc_lair.isBaby"))
            return SCRIPT_CONTINUE;

        // Prevent double-scheduling
        if (!hasObjVar(self, "pet.selfDestructScheduled"))
        {
            setObjVar(self, "pet.selfDestructScheduled", true);
            messageTo(self, MSG_SELF_DESTRUCT, null, BABY_LIFETIME_SECONDS, false);
        }

        return SCRIPT_CONTINUE;
    }

    public int pet_baby_self_destruct(obj_id self, dictionary params) throws InterruptedException
    {
        if (!isIdValid(self))
            return SCRIPT_CONTINUE;

        // If it somehow stopped being a baby, abort
        if (!utils.hasScriptVar(self, "npc_lair.isBaby"))
            return SCRIPT_CONTINUE;

        // Optional: if engaged in combat, delay a bit
        if (ai_lib.isInCombat(self))
        {
            messageTo(self, MSG_SELF_DESTRUCT, null, 60, false); // retry in 1 min
            return SCRIPT_CONTINUE;
        }

        // Poof quietly
        playClientEffectObj(self, "appearance/despawn_effect.prt", self, "");
        destroyObject(self);

        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException {
        if (!isIdValid(self) || !isIdValid(player))
            return SCRIPT_CONTINUE;

        if (!utils.hasScriptVar(self, "npc_lair.isBaby"))
            return SCRIPT_CONTINUE;

        mi.addRootMenu(MENU_ATTEMPT_TAME, new string_id("pet/pet_menu", "attempt_tame"));
        mi.addRootMenu(MENU_ATTEMPT_FEED, new string_id("pet/pet_menu", "attempt_feed"));
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException {
        if (item == MENU_ATTEMPT_TAME)
            attemptTame(self, player);
        else if (item == MENU_ATTEMPT_FEED)
            attemptFeed(self, player);

        return SCRIPT_CONTINUE;
    }

    private void attemptFeed(obj_id baby, obj_id player) throws InterruptedException {
        // Preconditions
        if (!isIdValid(player) || !isIdValid(baby)) return;

        // Check happiness objVar
        if (!hasObjVar(baby, "pet.happiness")) {
            setObjVar(baby, "pet.happiness", 10);
        }

        // Look in player's inventory for pet food
        obj_id inv = utils.getInventoryContainer(player);
        if (!isIdValid(inv)) {
            sendSystemMessage(player, new string_id("pet/pet_menu", "no_inventory"));
            return;
        }

        obj_id[] items = getContents(inv);
        obj_id foodItem = null;

        // Find first valid pet food by template
        for (obj_id item : items) {
            if (!isIdValid(item)) continue;

            String template = getTemplateName(item);
            if (template != null && template.toLowerCase().contains("beastfood")) {
                foodItem = item;
                break;
            }
        }

        if (foodItem == null) {
            sendSystemMessage(player, new string_id("pet/pet_menu", "no_pet_food"));
            return;
        }

        // ------------------------
        // Apply effect
        // ------------------------
        int happiness = getIntObjVar(baby, "pet.happiness");
        happiness += 10; // +10 happiness
        setObjVar(baby, "pet.happiness", happiness);

        // Play eat animation and effect
        doAnimationAction(baby, "eat");
        doAnimationAction(player, "manipulate_low");

        // ------------------------
        // Decrement food
        // ------------------------
        static_item.decrementStaticItem(foodItem);

        // Notify player
        sendSystemMessage(player, new string_id("pet/pet_menu", "fed_pet_success"));
        messageTo(baby, MSG_SELF_DESTRUCT, null, BABY_LIFETIME_SECONDS, true);
    }

    // -------------------------------------------------------
    // TAME FLOW
    // -------------------------------------------------------

    private void attemptTame(obj_id baby, obj_id player) throws InterruptedException {
        // ------------------------
        // Preconditions: combat and happiness
        // ------------------------
        if (isInCombat(player)) {
            sendSystemMessage(player, new string_id("pet/pet_menu", "cannot_tame_in_combat"));
            return;
        }

        // Initialize happiness if it doesn't exist
        if (!hasObjVar(baby, "pet.happiness")) {
            setObjVar(baby, "pet.happiness", 10); // default happiness
        }

        int happiness = getIntObjVar(baby, "pet.happiness");

        if (happiness <= 0) {
            sendSystemMessage(player, new string_id("pet/pet_menu", "beast_too_unhappy"));
            return;
        }

        // ------------------------
        // Player must have creature knowledge
        // ------------------------
        if (!buff.hasBuff(player, "bm_creature_knowledge")) {
            sendSystemMessage(player, new string_id("pet/pet_menu", "need_creature_knowledge"));
            return;
        }

        // ------------------------
        // Skill Check Mini-Game
        // ------------------------
        int skillLevel = getSkillStatisticModifier(player, "creature_knowledge");
        int roll = rand(1, 100);

        if (roll > skillLevel) { // failed skill check
            sendSystemMessage(player, new string_id("pet/pet_menu", "tame_failed_skill"));

            // Reduce happiness
            happiness -= 10;
            setObjVar(baby, "pet.happiness", happiness);

            // Check for low happiness threshold
            if (happiness <= 0) {
                sendSystemMessage(player, new string_id("pet/pet_menu", "beast_angry"));
                startCombat(baby, player);
            }

            return; // taming failed
        }

        // ------------------------
        // Skill Check Passed → increase happiness
        // ------------------------
        happiness += 1;
        setObjVar(baby, "pet.happiness", happiness);

        // ------------------------
        // Create the egg
        // ------------------------
        boolean success = createEggFromBaby(baby, player);
        if (!success) {
            return;
        }

        sendSystemMessage(player, new string_id("pet/pet_menu", "tame_success"));
    }

    // -------------------------------------------------------
    // EGG CREATION
    // -------------------------------------------------------

    public boolean createEggFromBaby(obj_id self, obj_id player) throws InterruptedException {

        // Read the beast type directly from the creature
        if (!hasObjVar(self, "beast.beastType")) {
            sendSystemMessageTestingOnly(player, "Creature does not have a beastType.");
            return false;
        }

        String creatureType = getStringObjVar(self, "beast.beastType");

        if (creatureType == null || creatureType.isEmpty()) {
            sendSystemMessageTestingOnly(player, "beastType is empty.");
            return false;
        }

// Normalize specific naming discrepancies//can we make this call to a separate function for name normalizing?
        if (creatureType.contains("womprat")) creatureType = "womp_rat";
        if (creatureType.contains("kamurith")) creatureType = "voritor";
        if (creatureType.contains("dragonet")) creatureType = "dune_lizard";
        if (creatureType.contains("razorback")) creatureType = "zucca_boar";
        if (creatureType.contains("mantigrue")) creatureType = "condor_dragon";
        if (creatureType.contains("bull_bantha")) creatureType = "bantha";
        if (creatureType.contains("feral_bantha")) creatureType = "bantha";
        if (creatureType.contains("dwarf_bantha")) creatureType = "bantha";
        if (creatureType.contains("rogue_bantha")) creatureType = "bantha";
        if (creatureType.contains("matriarch_bantha")) creatureType = "matriarch_bantha";
        if (creatureType.contains("rockmite")) creatureType = "rock_mite";

// Validate against allowed beasts
        boolean validBeast = false;
        for (String beast : BEAST_OPTIONS_FOR_LAIRS) {
            if (creatureType.equals(beast)) {
                validBeast = true;
                break;
            }
        }

        if (!validBeast) {
            sendSystemMessageTestingOnly(player, "Creature beastType not valid for taming: " + creatureType);
            return false;
        }

        // Create the egg in the player inventory
        obj_id inv = utils.getInventoryContainer(player);
        if (!isIdValid(inv)) {
            sendSystemMessageTestingOnly(player, "Player has no valid inventory.");
            return false;
        }

        obj_id egg = createObject("object/tangible/item/beast/bm_egg.iff", inv, "");
        if (!isIdValid(egg)) {
            sendSystemMessageTestingOnly(player, "Failed to create egg.");
            return false;
        }

        // Initialize the egg with the correct hash
        String bmcreatureType = "bm_" + creatureType;
        int hashCreatureType = incubator.getHashType(bmcreatureType);
        incubator.setUpEggWithDummyData(player, egg, hashCreatureType);
        //eureka! It's working! Next steps... now we need to auto-hatch the egg, probably call another function
        obj_id bcd = beast_lib.createBCDFromEgg(player, egg);
        if (beast_lib.isValidBCD(bcd))
        {
            playClientEffectObj(player, "appearance/pt_egg_crack.prt", player, "");
            destroyObject(self);
            destroyObject(egg);
            //trying to also "call" the newly created egg?
            callable.storeCallable(player, bcd);//this isn't working, but sufficient for now as a placeholder
            // Grant collection based on the creature
            String creatureName = beast_lib.getBCDBeastType(bcd);
            grantCreatureCollection(player, creatureName);
            return true;
        }

        sendSystemMessageTestingOnly(player, "Beast egg created: " + creatureType);
        return true;
    }
    private void grantCreatureCollection(obj_id player, String creatureName) throws InterruptedException
    {
        // Check if creatureName starts with "bm_" and remove it
        if (creatureName.startsWith("bm_"))
        {
            creatureName = creatureName.substring(3);
        }

        // Mapping between creature names and corresponding collection names
        dictionary creatureCollectionMap = new dictionary();
        // Corellia
        creatureCollectionMap.put("bageraset", "corellia_bageraset");
        //creatureCollectionMap.put("bark_mite", "corellia_bark_mite");
        creatureCollectionMap.put("boar_wolf", "corellia_boar_wolf");
        creatureCollectionMap.put("carrion_spat", "corellia_carrion_spat");
        creatureCollectionMap.put("corellian_butterfly", "corellia_butterfly");
        creatureCollectionMap.put("corellian_sand_panther", "corellia_sand_panther");
        creatureCollectionMap.put("corellian_slice_hound", "corellia_slice_hound");
        //creatureCollectionMap.put("dalyrake", "corellia_dalyrake");
        creatureCollectionMap.put("durni", "corellia_durni");
        //creatureCollectionMap.put("falumpaset", "corellia_falumpaset");
        creatureCollectionMap.put("gronda", "corellia_gronda");
        creatureCollectionMap.put("gubbur", "corellia_gubbur");
        creatureCollectionMap.put("gulginaw", "corellia_gulginaw");
        //creatureCollectionMap.put("gurreck", "corellia_gurreck");
        //creatureCollectionMap.put("horned_rasp", "corellia_horned_rasp");
        creatureCollectionMap.put("krahbu", "corellia_krahbu");
        creatureCollectionMap.put("nerf", "corellia_nerf");
        //creatureCollectionMap.put("langlatch", "corellia_langlatch");
        //creatureCollectionMap.put("murra", "corellia_murra");
        //creatureCollectionMap.put("narglatch", "corellia_narglatch");
        creatureCollectionMap.put("plumed_rasp", "corellia_plumed_rasp");
        creatureCollectionMap.put("sharnaff", "corellia_sharnaff");
        //creatureCollectionMap.put("stintaril", "corellia_stintaril");
        creatureCollectionMap.put("swirl_prong", "corellia_swirl_prong");
        //creatureCollectionMap.put("tanc_mite", "corellia_tanc_mite");
        //creatureCollectionMap.put("tusk_cat", "corellia_tusk_cat");
        creatureCollectionMap.put("vynock", "corellia_vynock");
        //creatureCollectionMap.put("womp_rat", "corellia_womp_rat");
        //creatureCollectionMap.put("worrt", "corellia_worrt");
        //Dantooine
        //creatureCollectionMap.put("bol", "dantooine_bol"); //broken due to Bolle Bol and Bol being to similar
        //creatureCollectionMap.put("dune_lizard", "dantooine_dune_lizard");
        creatureCollectionMap.put("graul", "dantooine_graul");
        creatureCollectionMap.put("huurton", "dantooine_huurton");
        creatureCollectionMap.put("piket", "dantooine_piket");
        creatureCollectionMap.put("quenker", "dantooine_quenker");
        creatureCollectionMap.put("thune", "dantooine_thune");
        creatureCollectionMap.put("voritor_lizard", "dantooine_voritor_lizard");
        //creatureCollectionMap.put("hawk_bat", "dantooine_hawk_bat");
        //Dathomir
        //creatureCollectionMap.put("bane_back_spider", "dathomir_bane_back_spider");
        creatureCollectionMap.put("baz_nitch", "dathomir_baz_nitch");
        creatureCollectionMap.put("blackwing_rancor", "dathomir_blackwing_rancor");
        creatureCollectionMap.put("bolma", "dathomir_bolma");
        creatureCollectionMap.put("brackaset", "dathomir_brackaset");
        creatureCollectionMap.put("gaping_spider", "dathomir_gaping_spider");
        creatureCollectionMap.put("kwi", "dathomir_kwi");
        creatureCollectionMap.put("malkloc", "dathomir_malkloc");
        creatureCollectionMap.put("purbole", "dathomir_purbole");
        creatureCollectionMap.put("rancor", "dathomir_rancor");
        creatureCollectionMap.put("reptilian_flier", "dathomir_reptilian_flyer");
        creatureCollectionMap.put("shear_mite", "dathomir_shear_mite");
        creatureCollectionMap.put("spiderclan_consort", "dathomir_spiderclan_consort");
        creatureCollectionMap.put("spiderclan_queen", "dathomir_spiderclan_queen");
        creatureCollectionMap.put("verne", "dathomir_verne");
        //creatureCollectionMap.put("voritor_lizard", "dathomir_voritor_lizard");
        // Endor
        creatureCollectionMap.put("angler", "endor_angler");
        //creatureCollectionMap.put("barbed_quenkar", "endor_barbed_quenkar");
        //creatureCollectionMap.put("bark_mite", "endor_bark_mite");
        //creatureCollectionMap.put("bearded_jax", "endor_bearded_jax");
        creatureCollectionMap.put("blurrg", "endor_blurrg");
        //creatureCollectionMap.put("boar_wolf", "endor_boar_wolf");
        //creatureCollectionMap.put("bolle", "endor_bolle_bol"); Broken due to Bolle Bol and Bol too similar
        creatureCollectionMap.put("bordok", "endor_bordok");
        //creatureCollectionMap.put("borgle", "endor_borgle");
        creatureCollectionMap.put("condor_dragon", "endor_condor_dragon");
        creatureCollectionMap.put("gackle_bat", "endor_gackle_bat");
        //creatureCollectionMap.put("gurreck", "endor_gurreck");
        creatureCollectionMap.put("hanadak", "endor_hanadak");
        creatureCollectionMap.put("lantern_bird", "endor_lantern_bird");
        creatureCollectionMap.put("merek", "endor_merek");
        creatureCollectionMap.put("remmer", "endor_remmer");
        creatureCollectionMap.put("roba", "endor_roba");
        //creatureCollectionMap.put("squall", "endor_squall");
        // Kashyyyk
        creatureCollectionMap.put("bolotaur", "kashyyyk_bolotaur");
        creatureCollectionMap.put("kashyyyk_bantha", "kashyyyk_bantha");
        creatureCollectionMap.put("minstyngar", "kashyyyk_minstyngar");
        creatureCollectionMap.put("mouf", "kashyyyk_mouf");
        //creatureCollectionMap.put("purbole", "kashyyyk_purbole");
        creatureCollectionMap.put("skreeg", "kashyyyk_skreeg");
        creatureCollectionMap.put("spined_puc", "kashyyyk_spined_puc");
        creatureCollectionMap.put("uller", "kashyyyk_uller");
        creatureCollectionMap.put("urnsoris", "kashyyyk_urnsoris");
        creatureCollectionMap.put("uwari_beetle", "kashyyyk_uwari_beetle");
        creatureCollectionMap.put("varactyl", "kashyyyk_varactyl");
        creatureCollectionMap.put("walluga", "kashyyyk_walluga");
        creatureCollectionMap.put("webweaver", "kashyyyk_webweaver");
        // Lok
        creatureCollectionMap.put("flit", "lok_flit");
        creatureCollectionMap.put("gurk", "lok_gurk");
        creatureCollectionMap.put("gurnaset", "lok_gurnaset");
        creatureCollectionMap.put("kimogila", "lok_kimogila");
        creatureCollectionMap.put("kusak", "lok_kusak");
        creatureCollectionMap.put("langlatch", "lok_langlatch");
        creatureCollectionMap.put("pharple", "lok_perlek");
        creatureCollectionMap.put("pharple", "lok_pharple");
        creatureCollectionMap.put("salt_mynock", "lok_salt_mynock");
        creatureCollectionMap.put("snorbal", "lok_snorbal");
        creatureCollectionMap.put("spined_snake", "lok_spined_snake");
        creatureCollectionMap.put("vesp", "lok_vesp");
        // Mustafar
        creatureCollectionMap.put("blistmok", "mustafar_blistmok");
        creatureCollectionMap.put("jundak", "mustafar_jundak");
        creatureCollectionMap.put("kubaza_beetle", "mustafar_kubaza_beetle");
        creatureCollectionMap.put("lava_flea", "mustafar_lava_flea");
        creatureCollectionMap.put("tanray", "mustafar_tanray");
        creatureCollectionMap.put("tulrus", "mustafar_tulrus");
        creatureCollectionMap.put("xandank", "mustafar_xandank");
        // Naboo
        creatureCollectionMap.put("capper_spineflap", "naboo_capper_spineflap");
        creatureCollectionMap.put("chuba", "naboo_chuba");
        creatureCollectionMap.put("dwarf_nuna", "naboo_dwarf_nuna");
        creatureCollectionMap.put("falumpaset", "naboo_falumpaset");
        creatureCollectionMap.put("fambaa", "naboo_fambaa");
        creatureCollectionMap.put("fanned_rawl", "naboo_fanned_rawl");
        creatureCollectionMap.put("flewt", "naboo_flewt");
        creatureCollectionMap.put("gnort", "naboo_gnort");
        creatureCollectionMap.put("gualama", "naboo_gualama");
        creatureCollectionMap.put("hermit_spider", "naboo_hermit_spider");
        creatureCollectionMap.put("horned_krevol", "naboo_horned_krevol");
        creatureCollectionMap.put("ikopi", "naboo_ikopi");
        creatureCollectionMap.put("kaadu", "naboo_kaadu");
        creatureCollectionMap.put("mott", "naboo_mott");
        creatureCollectionMap.put("narglatch", "naboo_narglatch");
        creatureCollectionMap.put("nuna", "naboo_nuna");
        creatureCollectionMap.put("peko_peko", "naboo_peko_peko");
        creatureCollectionMap.put("shaupaut", "naboo_shaupaut");
        creatureCollectionMap.put("tusk_cat", "naboo_tusk_cat");
        creatureCollectionMap.put("veermok", "naboo_veermok");
        // Rori
        creatureCollectionMap.put("bark_mite", "rori_bark_mite");
        creatureCollectionMap.put("bearded_jax", "rori_bearded_jax");
        creatureCollectionMap.put("borgle", "rori_borgle");
        //creatureCollectionMap.put("capper_spineflap", "rori_capper_spineflap");
        creatureCollectionMap.put("huf_dun", "rori_huf_dun");
        creatureCollectionMap.put("kai_tok", "rori_kai_tok");
        creatureCollectionMap.put("pugoriss", "rori_pugoriss");
        creatureCollectionMap.put("squall", "rori_squall");
        creatureCollectionMap.put("torton", "rori_torton");
        creatureCollectionMap.put("vir_vur", "rori_vir_vur");
        // Talus
        //creatureCollectionMap.put("bark_mite", "talus_bark_mite");
        creatureCollectionMap.put("dalyrake", "talus_dalyrake");
        //creatureCollectionMap.put("falumpaset", "talus_falumpaset");
        creatureCollectionMap.put("fynock", "talus_fynock");
        //creatureCollectionMap.put("gaping_spider", "talus_gaping_spider");
        creatureCollectionMap.put("guf_drolg", "talus_guf_drolg");
        //creatureCollectionMap.put("gulginaw", "talus_gulginaw");
        creatureCollectionMap.put("gurreck", "talus_gurreck");
        creatureCollectionMap.put("horned_rasp", "talus_horned_rasp");
        creatureCollectionMap.put("kittle", "talus_kittle");
        creatureCollectionMap.put("kima", "talus_kima");
        creatureCollectionMap.put("murra", "talus_murra");
        // Tatooine
        creatureCollectionMap.put("bantha", "tatooine_bantha");
        creatureCollectionMap.put("bocatt", "tatooine_bocatt");
        creatureCollectionMap.put("cu_pa", "tatooine_cu_pa");
        creatureCollectionMap.put("dewback", "tatooine_dewback");
        creatureCollectionMap.put("dune_lizard", "tatooine_dune_lizard");
        creatureCollectionMap.put("dwarf_nuna", "tatooine_dwarf_nuna");
        creatureCollectionMap.put("eopie", "tatooine_eopie");
        creatureCollectionMap.put("mynock", "tatooine_mynock");
        creatureCollectionMap.put("rock_mite", "tatooine_rock_mite");
        creatureCollectionMap.put("ronto", "tatooine_ronto");
        creatureCollectionMap.put("squill", "tatooine_squill");
        creatureCollectionMap.put("tanc_mite", "tatooine_tanc_mite");
        creatureCollectionMap.put("womp_rat", "tatooine_womp_rat");
        creatureCollectionMap.put("worrt", "tatooine_worrt");
        creatureCollectionMap.put("zucca_boar", "tatooine_zucca_boar");
        // Yavin4
        creatureCollectionMap.put("acklay", "yavin4_acklay");
        creatureCollectionMap.put("choku", "yavin4_choku");
        creatureCollectionMap.put("crystal_snake", "yavin4_crystal_snake");
        //creatureCollectionMap.put("finned_blaggart", "yavin4_finned_blaggart");
        creatureCollectionMap.put("kliknik", "yavin4_kliknik");
        creatureCollectionMap.put("mamien", "yavin4_mamien");
        creatureCollectionMap.put("mawgax", "yavin4_mawgax");
        creatureCollectionMap.put("nexu", "yavin4_nexu");
        creatureCollectionMap.put("reek", "yavin4_reek");
        creatureCollectionMap.put("skreeg", "yavin4_skreeg");
        creatureCollectionMap.put("spined_puc", "yavin4_spined_puc");
        creatureCollectionMap.put("stintaril", "yavin4_stintaril");
        //creatureCollectionMap.put("tanc_mite", "yavin4_tanc_mite");
        creatureCollectionMap.put("tybis", "yavin4_tybis");
        creatureCollectionMap.put("whisper_bird", "yavin4_whisper_bird");
        creatureCollectionMap.put("woolamander", "yavin4_woolamander");
        // Hoth
        creatureCollectionMap.put("taun", "hoth_tauntaun");
        creatureCollectionMap.put("wampa", "hoth_wampa");
        // Mutated
        creatureCollectionMap.put("mutated_acklay", "mutated_acklay");
        creatureCollectionMap.put("mutated_borgax", "mutated_borgax");
        creatureCollectionMap.put("mutated_cat", "mutated_cat");
        creatureCollectionMap.put("mutated_boar", "mutated_boar");
        creatureCollectionMap.put("mutated_chuba_fly", "mutated_chuba_fly");
        creatureCollectionMap.put("mutated_cu_pa", "mutated_cu_pa");
        creatureCollectionMap.put("mutated_dewback", "mutated_dewback");
        creatureCollectionMap.put("mutated_griffon", "mutated_griffon");
        creatureCollectionMap.put("kittle", "mutated_kittle");
        creatureCollectionMap.put("mutated_jax", "mutated_jax");
        creatureCollectionMap.put("mutated_quenker", "mutated_quenker");
        creatureCollectionMap.put("mutated_rancor", "mutated_rancor");
        creatureCollectionMap.put("mutated_slice_hound", "mutated_slice_hound");
        creatureCollectionMap.put("mutated_varasquactyl", "mutated_varasquactyl");
        creatureCollectionMap.put("monkey_lizard", "monkey_lizard");

        // Lowercase the creatureName for case-insensitive comparison
        creatureName = creatureName.toLowerCase();

        // Check if creatureName exists in the dictionary
        if (creatureCollectionMap.containsKey(creatureName))
        {
            String collectionName = creatureCollectionMap.getString(creatureName);
            modifyCollectionSlotValue(player, collectionName, 1);
            xp.grant(player, "creaturehandler", 1000);//hatching eggs rewards player with experience
        }
    }
}