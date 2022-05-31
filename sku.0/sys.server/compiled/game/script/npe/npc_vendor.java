package script.npe;

import script.dictionary;
import script.library.*;
import script.obj_id;
import script.string_id;

public class npc_vendor extends script.base_script
{
    public npc_vendor()
    {
    }
    public static final String STF = "npe";
    public static final String[] BEAST_OPTIONS_FOR_PLAYERS = {
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
    public static final String[] BEAST_OPTIONS = {
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
        "zucca_boar"
    };
    public int npeHandleBuy(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id player = sui.getPlayerId(params);
        int price = 1000;
        if ((params == null) || (params.isEmpty()))
        {
            sendSystemMessageTestingOnly(player, "Failing, params were empty!");
            return SCRIPT_CONTINUE;
        }
        int btn = sui.getIntButtonPressed(params);
        int idx = sui.getListboxSelectedRow(params);
        int totalMoney = getTotalMoney(player);
        dictionary d = new dictionary();
        d.put("price", price);
        obj_id pInv = utils.getInventoryContainer(player);
        if (btn == sui.BP_CANCEL)
        {
            return SCRIPT_CONTINUE;
        }
        switch (idx)
        {
            case 0:
            obj_id stimpack = static_item.createNewItemFunction("item_stimpack_a_02_01", player);
            if (isIdValid(stimpack))
            {
                d.put("player", player);
                d.put("item", stimpack);
                d.put("npc", self);
                money.requestPayment(player, "Tyrral", price, "handleTransaction", d, true);
                break;
            }
            else 
            {
                CustomerServiceLog("NPE_VENDOR: ", "tried to create a item_stimpack_a_02_01 that had an invalid id.");
                break;
            }
            case 1:
            boolean playerHasItem = utils.playerHasStaticItemInBankOrInventory(player, "item_npe_tatooine_bug_juice_01_01");
            if (!playerHasItem)
            {
                obj_id bugJuice = static_item.createNewItemFunction("item_npe_tatooine_bug_juice_01_01", player);
                if (isIdValid(bugJuice))
                {
                    d.put("player", player);
                    d.put("item", bugJuice);
                    d.put("npc", self);
                    money.requestPayment(player, "Tyrral", price, "handleTransaction", d, true);
                    break;
                }
                else 
                {
                    CustomerServiceLog("NPE_VENDOR: ", "tried to create a item_npe_tatooine_bug_juice_01_01 that had an invalid id.");
                    break;
                }
            }
            string_id msgHasItem = new string_id(STF, "has_item");
            chat.publicChat(self, player, msgHasItem);
            break;
            default:
            break;
        }
        return SCRIPT_CONTINUE;
    }
    public int handleTransaction(obj_id self, dictionary params) throws InterruptedException
    {
        if (params == null || params.isEmpty())
        {
            debugSpeakMsg(self, "null?");
            return SCRIPT_CONTINUE;
        }
        obj_id player = params.getObjId("player");
        obj_id item = params.getObjId("item");
        int price = params.getInt("price");
        if (!isIdValid(player) || !isIdValid(item) || price < 1)
        {
            return SCRIPT_CONTINUE;
        }
        if (params.getInt(money.DICT_CODE) == money.RET_FAIL)
        {
            chat.publicChat(self, player, new string_id(STF, "no_money"));
            destroyObject(item);
            return SCRIPT_CONTINUE;
        }
        string_id msgBoughtItem = new string_id(STF, "bought_item");
        chat.publicChat(self, player, msgBoughtItem);
        playMusic(player, "sound/music_acq_academic.snd");
        obj_id pInv = utils.getInventoryContainer(player);
        String creatureName = "bm_blistmok";
        obj_id inv = utils.getInventoryContainer(player);
        obj_id egg = createObject("object/tangible/item/beast/bm_egg.iff", inv, "");
        int hashCreatureType = incubator.getHashType(creatureName);
        incubator.setUpEggWithDummyData(player, egg, hashCreatureType);
        return SCRIPT_CONTINUE;
    }
}
