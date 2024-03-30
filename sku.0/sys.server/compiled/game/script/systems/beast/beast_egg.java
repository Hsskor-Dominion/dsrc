package script.systems.beast;

import script.*;
import script.library.*;

public class beast_egg extends script.base_script
{
    public beast_egg()
    {
    }
    public static final string_id SID_WHILE_DEAD = new string_id("beast", "no_hatch_while_dead");
    public static final string_id SID_EGG_HATCH = new string_id("beast", "hatch_egg");
    public static final string_id SID_GOD_EGG_HATCH = new string_id("beast", "god_hatch_egg");
    public static final string_id SID_MAKE_MOUNT = new string_id("beast", "egg_make_mount");
    public static final string_id SID_HATCH_MOUNT = new string_id("beast", "egg_hatch_mount");
    public static final string_id SID_MOUNT_CONVERT_PROMPT = new string_id("beast", "egg_make_mount_prompt");
    public static final string_id SID_MOUNT_CONVERT_TITLE = new string_id("beast", "egg_make_mount_title");
    public static final string_id SID_MAKE_HOLOPET = new string_id("beast", "egg_make_holopet");
    public static final string_id SID_HOLOBEAST_CONVERT_PROMPT = new string_id("beast", "egg_make_holobeast_prompt");
    public static final string_id SID_HOLOBEAST_CONVERT_TITLE = new string_id("beast", "egg_make_holobeast_title");
    public int OnAttach(obj_id self) throws InterruptedException
    {
        messageTo(self, "handleInitializeValues", null, 1, false);
        return SCRIPT_CONTINUE;
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        incubator.setEggHue(self);
        messageTo(self, "rename_egg", null, 1.0f, false);
        return SCRIPT_CONTINUE;
    }
    public int rename_egg(obj_id self, dictionary params) throws InterruptedException
    {
        String beastType = beast_lib.getBCDBeastType(self);
        beastType = "egg_" + beast_lib.stripBmFromType(beastType);
        setName(self, new string_id("beast", beastType));
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        obj_id egg = self;
        if (isDead(player) || isIncapacitated(player))
        {
            sendSystemMessage(player, SID_WHILE_DEAD);
            return SCRIPT_CONTINUE;
        }
        if (utils.isNestedWithinAPlayer(egg, false))
        {
            if (beast_lib.isBeastMaster(player) && hasObjVar(egg, beast_lib.OBJVAR_BEAST_TYPE) && !incubator.isEggMountFlagged(egg))
            {
                mi.addRootMenu(menu_info_types.ITEM_USE, SID_EGG_HATCH);
            }
            if (incubator.isEggMountType(egg) && hasSkill(player, "expertise_bm_attack_1") && !incubator.isEggMountFlagged(egg))
            {
                mi.addRootMenu(menu_info_types.SERVER_MENU1, SID_MAKE_MOUNT);
            }
            else if (incubator.isEggMountType(egg) && incubator.isEggMountFlagged(egg))
            {
                mi.addRootMenu(menu_info_types.SERVER_MENU1, SID_HATCH_MOUNT);
            }
            if (beast_lib.isBeastMaster(player) && hasObjVar(egg, beast_lib.OBJVAR_BEAST_TYPE))
            {
                mi.addRootMenu(menu_info_types.SERVER_MENU2, SID_MAKE_HOLOPET);
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        sendDirtyObjectMenuNotification(self);
        if (isDead(player) || isIncapacitated(player))
        {
            sendSystemMessage(player, SID_WHILE_DEAD);
            return SCRIPT_CONTINUE;
        }
        obj_id egg = self;
        if (utils.isNestedWithinAPlayer(egg))
        {
            if (item == menu_info_types.ITEM_USE && !incubator.isEggMountFlagged(egg))
            {
                if (!beast_lib.isBeastMaster(player))
                {
                    return SCRIPT_OVERRIDE;
                }
                obj_id bcd = beast_lib.createBCDFromEgg(player, egg);
                if (beast_lib.isValidBCD(bcd))
                {
                    playClientEffectObj(player, "appearance/pt_egg_crack.prt", player, "");
                    destroyObject(self);
                    // Grant collection based on the creature
                    String creatureName = beast_lib.getBCDBeastType(bcd);
                    grantCreatureCollection(player, creatureName);
                    return SCRIPT_CONTINUE;
                }
            }
            if (item == menu_info_types.SERVER_MENU1 && hasSkill(player, "expertise_bm_attack_1") && !incubator.isEggMountFlagged(egg))
            {
                if (!incubator.isEggMountType(egg))
                {
                    return SCRIPT_CONTINUE;
                }
                int pid = sui.msgbox(self, player, "@" + SID_MOUNT_CONVERT_PROMPT, sui.YES_NO, "@" + SID_MOUNT_CONVERT_TITLE, "handleStampEggAsMount");
                return SCRIPT_CONTINUE;
            }
            else if (item == menu_info_types.SERVER_MENU1 && incubator.isEggMountType(egg) && incubator.isEggMountFlagged(egg))
            {
                if (!incubator.isEggMountType(egg))
                {
                    return SCRIPT_CONTINUE;
                }
                obj_id pcd = incubator.convertEggToMount(egg, player);
                if (isValidId(pcd) && exists(pcd))
                {
                    CustomerServiceLog("BeastEggToMountConversion: ", "Player (" + player + ") has converted Egg (" + egg + ")" + " to PCD (" + pcd + ") we are now going to destroy the egg");
                    destroyObject(egg);
                    return SCRIPT_CONTINUE;
                }
                else 
                {
                    CustomerServiceLog("BeastEggToMountConversion: ", "Player (" + player + ") tried to convert incubated egg (" + egg + "). Conversion was not a success. New PCD was NOT created and egg was NOT destroyed.");
                    return SCRIPT_CONTINUE;
                }
            }
            if (item == menu_info_types.SERVER_MENU2 && hasObjVar(egg, beast_lib.OBJVAR_BEAST_TYPE))
            {
                if (!beast_lib.isBeastMaster(player))
                {
                    return SCRIPT_CONTINUE;
                }
                int pid = sui.msgbox(self, player, "@" + SID_HOLOBEAST_CONVERT_PROMPT, sui.YES_NO, "@" + SID_HOLOBEAST_CONVERT_TITLE, "handleConvertEggToHolobeast");
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnGetAttributes(obj_id self, obj_id player, String[] names, String[] attribs) throws InterruptedException
    {
        int idx = utils.getValidAttributeIndex(names);
        if (idx == -1)
        {
            return SCRIPT_CONTINUE;
        }
        if (exists(self))
        {
            for (int i = 0; i < beast_lib.ARRAY_BEAST_INCUBATION_STATS.length; ++i)
            {
                if (hasObjVar(self, beast_lib.ARRAY_BEAST_INCUBATION_STATS[i]) && !incubator.isEggMountFlagged(self))
                {
                    String name = beast_lib.DISPLAY_NAMES[i];
                    int stat = getIntObjVar(self, beast_lib.ARRAY_BEAST_INCUBATION_STATS[i]);
                    if (!name.contains("_skill"))
                    {
                        if (!name.equals("block_value_bonus"))
                        {
                            names[idx] = beast_lib.DISPLAY_NAMES[i];
                            attribs[idx] = "" + utils.roundFloatByDecimal(stat * beast_lib.DISPLAY_OBJVAR_CONVERSION_RATES[i]) + "%";
                            idx++;
                        }
                        else 
                        {
                            names[idx] = beast_lib.DISPLAY_NAMES[i];
                            attribs[idx] = "" + stat;
                            idx++;
                        }
                        continue;
                    }
                    else 
                    {
                        names[idx] = beast_lib.DISPLAY_NAMES[i];
                        attribs[idx] = "" + stat;
                        idx++;
                        continue;
                    }
                }
            }
            if (hasObjVar(self, beast_lib.OBJVAR_BEAST_PARENT))
            {
                names[idx] = "bm_template";
                int hashType = getIntObjVar(self, beast_lib.OBJVAR_BEAST_TYPE);
                String template = getStringObjVar(self, beast_lib.OBJVAR_BEAST_PARENT);
                string_id templateId = new string_id("mob/creature_names", template);
                if (localize(templateId) == null)
                {
                    template = incubator.convertHashTypeToString(hashType);
                    template = beast_lib.stripBmFromType(template);
                    templateId = new string_id("monster_name", template);
                    if (localize(templateId) == null)
                    {
                        templateId = new string_id("mob/creature_names", template);
                    }
                }
                attribs[idx] = "" + localize(templateId);
                idx++;
                if (idx >= names.length)
                {
                    return SCRIPT_CONTINUE;
                }
            }
            if (hasObjVar(self, beast_lib.OBJVAR_BEAST_TYPE))
            {
                names[idx] = "beast_type";
                int hashType = getIntObjVar(self, beast_lib.OBJVAR_BEAST_TYPE);
                String template = incubator.convertHashTypeToString(hashType);
                template = beast_lib.stripBmFromType(template);
                string_id templateId = new string_id("monster_name", template);
                if (localize(templateId) == null)
                {
                    templateId = new string_id("mob/creature_names", template);
                }
                attribs[idx] = "" + localize(templateId);
                idx++;
                if (idx >= names.length)
                {
                    return SCRIPT_CONTINUE;
                }
            }
            if (incubator.isEggMountFlagged(self))
            {
                names[idx] = "is_mount";
                attribs[idx] = "true";
                idx++;
                if (idx >= names.length)
                {
                    return SCRIPT_CONTINUE;
                }
            }
            if (hasObjVar(self, beast_lib.OBJVAR_BEAST_ENGINEER))
            {
                String creatorName = getStringObjVar(self, beast_lib.OBJVAR_BEAST_ENGINEER);
                names[idx] = "bm_creator";
                attribs[idx] = creatorName;
                idx++;
                if (idx >= names.length)
                {
                    return SCRIPT_CONTINUE;
                }
            }
            if (hasObjVar(self, beast_lib.OBJVAR_BEAST_HUE))
            {
                color eggColor = getPalcolorCustomVarSelectedColor(self, hue.INDEX_1);
                String attrib = "";
                attrib += "" + eggColor.getR() + ", " + eggColor.getG() + ", " + eggColor.getB();
                names[idx] = "bm_rgb_values";
                attribs[idx] = attrib;
                idx++;
                if (idx >= names.length)
                {
                    return SCRIPT_CONTINUE;
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int handleInitializeValues(obj_id self, dictionary params) throws InterruptedException
    {
        LOG("beast", "beast_egg: handleInitializeValues()");
        incubator.initializeEgg(self);
        return SCRIPT_CONTINUE;
    }
    public int handleStampEggAsMount(obj_id self, dictionary params) throws InterruptedException
    {
        int bp = sui.getIntButtonPressed(params);
        if (bp == sui.BP_CANCEL)
        {
            return SCRIPT_CONTINUE;
        }
        obj_id player = sui.getPlayerId(params);
        obj_id egg = self;
        incubator.stampEggAsMount(egg, player);
        return SCRIPT_CONTINUE;
    }
    public int handleConvertEggToHolobeast(obj_id self, dictionary params) throws InterruptedException
    {
        int bp = sui.getIntButtonPressed(params);
        if (bp == sui.BP_CANCEL)
        {
            return SCRIPT_CONTINUE;
        }
        obj_id player = sui.getPlayerId(params);
        if (isIdValid(player))
        {
            obj_id holopetCube = beast_lib.createHolopetCubeFromEgg(player, self);
            if (isIdValid(holopetCube))
            {
                destroyObject(self);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_CONTINUE;
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
        creatureCollectionMap.put("bagersat", "corellia_bagersat");
        creatureCollectionMap.put("bark_mite", "corellia_bark_mite");
        creatureCollectionMap.put("boar_wolf", "corellia_boar_wolf");
        creatureCollectionMap.put("carrion_spat", "corellia_carrion_spat");
        creatureCollectionMap.put("butterfly", "corellia_butterfly");
        creatureCollectionMap.put("sand_panther", "corellia_sand_panther");
        creatureCollectionMap.put("slice_hound", "corellia_slice_hound");
        creatureCollectionMap.put("dalyrake", "corellia_dalyrake");
        creatureCollectionMap.put("durni", "corellia_durni");
        creatureCollectionMap.put("falumpaset", "corellia_falumpaset");
        creatureCollectionMap.put("gronda", "corellia_gronda");
        creatureCollectionMap.put("gubbur", "corellia_gubbur");
        creatureCollectionMap.put("gulginaw", "corellia_gulginaw");
        creatureCollectionMap.put("gurreck", "corellia_gurreck");
        creatureCollectionMap.put("horned_rasp", "corellia_horned_rasp");
        creatureCollectionMap.put("klicknik", "corellia_klicknik");
        creatureCollectionMap.put("krabhu", "corellia_krabhu");
        creatureCollectionMap.put("langlatch", "corellia_langlatch");
        creatureCollectionMap.put("murra", "corellia_murra");
        creatureCollectionMap.put("narglatch", "corellia_narglatch");
        creatureCollectionMap.put("plumed_rasp", "corellia_plumed_rasp");
        creatureCollectionMap.put("sharnaff", "corellia_sharnaff");
        creatureCollectionMap.put("stintaril", "corellia_stintaril");
        creatureCollectionMap.put("swirl_prong", "corellia_swirl_prong");
        creatureCollectionMap.put("tanc_mite", "corellia_tanc_mite");
        creatureCollectionMap.put("tusk_cat", "corellia_tusk_cat");
        creatureCollectionMap.put("vynock", "corellia_vynock");
        creatureCollectionMap.put("womp_rat", "corellia_womp_rat");
        creatureCollectionMap.put("worrt", "corellia_worrt");
        //Dantooine
        creatureCollectionMap.put("bol", "dantooine_bol");
        creatureCollectionMap.put("dune_lizard", "dantooine_dune_lizard");
        creatureCollectionMap.put("graul", "dantooine_graul");
        creatureCollectionMap.put("huurton", "dantooine_huurton");
        creatureCollectionMap.put("piket", "dantooine_piket");
        creatureCollectionMap.put("quenkar", "dantooine_quenkar");
        creatureCollectionMap.put("thune", "dantooine_thune");
        creatureCollectionMap.put("voritor_lizard", "dantooine_voritor_lizard");
        creatureCollectionMap.put("hawk_bat", "dantooine_hawk_bat");
        //Dathomir
        creatureCollectionMap.put("bane_back_spider", "dathomir_bane_back_spider");
        creatureCollectionMap.put("baz_nitch", "dathomir_baz_nitch");
        creatureCollectionMap.put("blackwing_rancor", "dathomir_blackwing_rancor");
        creatureCollectionMap.put("bolma", "dathomir_bolma");
        creatureCollectionMap.put("brackaset", "dathomir_brackaset");
        creatureCollectionMap.put("gaping_spider", "dathomir_gaping_spider");
        creatureCollectionMap.put("kwi", "dathomir_kwi");
        creatureCollectionMap.put("malkloc", "dathomir_malkloc");
        creatureCollectionMap.put("purbole", "dathomir_purbole");
        creatureCollectionMap.put("rancor", "dathomir_rancor");
        creatureCollectionMap.put("reptilian_flyer", "dathomir_reptilian_flyer");
        creatureCollectionMap.put("shear_mite", "dathomir_shear_mite");
        creatureCollectionMap.put("spiderclan_consort", "dathomir_spiderclan_consort");
        creatureCollectionMap.put("spiderclan_queen", "dathomir_spiderclan_queen");
        creatureCollectionMap.put("verne", "dathomir_verne");
        creatureCollectionMap.put("voritor_lizard", "dathomir_voritor_lizard");
        // Endor
        creatureCollectionMap.put("angler", "endor_angler");
        creatureCollectionMap.put("barbed_quenkar", "endor_barbed_quenkar");
        creatureCollectionMap.put("bark_mite", "endor_bark_mite");
        creatureCollectionMap.put("bearded_jax", "endor_bearded_jax");
        creatureCollectionMap.put("blurrg", "endor_blurrg");
        creatureCollectionMap.put("boar_wolf", "endor_boar_wolf");
        creatureCollectionMap.put("bolle_bol", "endor_bolle_bol");
        creatureCollectionMap.put("bordok", "endor_bordok");
        creatureCollectionMap.put("borgle", "endor_borgle");
        creatureCollectionMap.put("condor_dragon", "endor_condor_dragon");
        creatureCollectionMap.put("gackle_bat", "endor_gackle_bat");
        creatureCollectionMap.put("gurrck", "endor_gurrck");
        creatureCollectionMap.put("hanadak", "endor_hanadak");
        creatureCollectionMap.put("lantern_bird", "endor_lantern_bird");
        creatureCollectionMap.put("merek", "endor_merek");
        creatureCollectionMap.put("remmer", "endor_remmer");
        creatureCollectionMap.put("roba", "endor_roba");
        creatureCollectionMap.put("squall", "endor_squall");
        // Kashyyyk
        creatureCollectionMap.put("bolotaur", "kashyyyk_bolotaur");
        creatureCollectionMap.put("bantha", "kashyyyk_bantha");
        creatureCollectionMap.put("minstyngar", "kashyyyk_minstyngar");
        creatureCollectionMap.put("mouf", "kashyyyk_mouf");
        creatureCollectionMap.put("purbole", "kashyyyk_purbole");
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
        creatureCollectionMap.put("pharple", "lok_pharple");
        creatureCollectionMap.put("salt_mynock", "lok_salt_mynock");
        creatureCollectionMap.put("snorbal", "lok_snorbal");
        creatureCollectionMap.put("spined_snake", "lok_spined_snake");
        creatureCollectionMap.put("vesp", "lok_vesp");
        // Mustafar
        creatureCollectionMap.put("blistmok", "mustafar_blistmok");
        creatureCollectionMap.put("jundak", "mustafar_jundak");
        creatureCollectionMap.put("kabaza_beetle", "mustafar_kabaza_beetle");
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
        creatureCollectionMap.put("capper_spineflap", "rori_capper_spineflap");
        creatureCollectionMap.put("huf_dun", "rori_huf_dun");
        creatureCollectionMap.put("kai_tok", "rori_kai_tok");
        creatureCollectionMap.put("pugoriss", "rori_pugoriss");
        creatureCollectionMap.put("squall", "rori_squall");
        creatureCollectionMap.put("torton", "rori_torton");
        creatureCollectionMap.put("vir_vur", "rori_vir_vur");
        // Talus
        creatureCollectionMap.put("bark_mite", "talus_bark_mite");
        creatureCollectionMap.put("dalyrake", "talus_dalyrake");
        creatureCollectionMap.put("falumpaset", "talus_falumpaset");
        creatureCollectionMap.put("frynock", "talus_frynock");
        creatureCollectionMap.put("gaping_spider", "talus_gaping_spider");
        creatureCollectionMap.put("guf_drolg", "talus_guf_drolg");
        creatureCollectionMap.put("gulignaw", "talus_gulignaw");
        creatureCollectionMap.put("gurreck", "talus_gurreck");
        creatureCollectionMap.put("horned_rasp", "talus_horned_rasp");
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
        creatureCollectionMap.put("finned_blaggart", "yavin4_finned_blaggart");
        creatureCollectionMap.put("kliknik", "yavin4_kliknik");
        creatureCollectionMap.put("mamien", "yavin4_mamien");
        creatureCollectionMap.put("mawgax", "yavin4_mawgax");
        creatureCollectionMap.put("nexu", "yavin4_nexu");
        creatureCollectionMap.put("reek", "yavin4_reek");
        creatureCollectionMap.put("skreeg", "yavin4_skreeg");
        creatureCollectionMap.put("spined_puc", "yavin4_spined_puc");
        creatureCollectionMap.put("stintaril", "yavin4_stintaril");
        creatureCollectionMap.put("tanc_mite", "yavin4_tanc_mite");
        creatureCollectionMap.put("tybis", "yavin4_tybis");
        creatureCollectionMap.put("whisper_bird", "yavin4_whisper_bird");
        creatureCollectionMap.put("woolamander", "yavin4_woolamander");

        // Lowercase the creatureName for case-insensitive comparison
        creatureName = creatureName.toLowerCase();

        // Check if creatureName exists in the dictionary
        if (creatureCollectionMap.containsKey(creatureName))
        {
            String collectionName = creatureCollectionMap.getString(creatureName);
            modifyCollectionSlotValue(player, collectionName, 1);
        }
    }
}
