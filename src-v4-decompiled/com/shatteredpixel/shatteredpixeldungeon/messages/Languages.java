/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.messages;

import java.util.Locale;

public enum Languages {
    ENGLISH("english", "en", Status.O_COMPLETE, null, null),
    CHI_SMPL("\u7b80\u4f53\u4e2d\u6587", "zh", Status.O_COMPLETE, new String[]{"Chronie_Lynn_Iwa", "Jinkeloid(zdx00793)", "endlesssolitude"}, new String[]{"931451545", "Budding", "DM_216", "Fatir", "Fishbone", "Hcat", "HoofBumpBlurryface", "Horr_lski", "Lery", "Lyn_0401", "Lyx0527", "Ooooscar", "RainSlide", "ShatteredFlameBlast", "SpaceAnchor", "SunsetGlowTheDOGE", "Teller", "hmdzl001", "leo", "tempest102", "tequilasunset", "\u6237\u65b9\u72f8\u5974"}),
    KOREAN("\ud55c\uad6d\uc5b4", "ko", Status.X_UNFINISH, new String[]{"Cocoa", "Flameblast12", "GameConqueror", "Korean2017"}, new String[]{"AFS", "N8fall", "WondarRabb1t", "benepaper", "chlrhwnstkd", "ddojin0115", "eeeei", "enjuxx", "hancyel", "linterpreteur", "lemonam", "lsiebnie", "sora0430"}),
    RUSSIAN("\u0440\u0443\u0441\u0441\u043a\u0438\u0439", "ru", Status.__UNREVIEW, new String[]{"AprilRain(Vadzim Navuma\u00fb)", "ConsideredHamster", "Dominowood371", "Inevielle", "apxwn", "yarikonline"}, new String[]{"5r", "Alternative15", "AngryPotato", "AttHawk46", "BlueberryShortcake", "CatGirlSasha", "Enwviun", "HerrGotlieb", "HoloTheWise", "Ilbko", "JleHuBbluKoT", "KirStaLong", "MrXantar", "Nikets", "OneDuo", "Originalej0name", "Provitia", "Raymundo", "Roycce", "Shamahan", "Thomasg63", "XAutumn", "Ya6lo4ko", "chelikchelik", "dasfan123", "ifritdiezel", "katyp2005", "kirusyaga", "kptmx", "leondorus", "long_live_the_9", "pancreper1", "perefrazz", "ponfertato", "roman.yagodin", "tibby", "un_logic", "vivatimperia", "wntrau", "xenrun", "\u0412o\u0432a"}),
    SPANISH("espa\u00f1ol", "es", Status.__UNREVIEW, new String[]{"KeyKai", "Kiroto", "Kohru", "airman12", "grayscales"}, new String[]{"2001sergiobr", "AdventurerKilly", "Alesxanderk", "Bryan092", "CorvosUtopy", "D0n.Kak0", "Dewstend", "Dyrran", "Enddox", "Fervoreking", "Illyatwo2", "Fuwn", "JPCHZ", "LastCry", "Marquezo_577_284", "NAVI1237", "No_se145", "STKmonoqui", "Sh4rkill3r", "Uri2523", "alfongad", "alquimistamyl", "anauta", "benzarr410", "chepe567.jc", "ctrijueque", "damc0616", "desen90", "dhg121", "javifs", "jonismack1", "magmax", "rechebeltran", "saadhabibi077", "tres.14159"}),
    PORTUGUESE("portugu\u00eas", "pt", Status.__UNREVIEW, new String[]{"NicholasPainek", "TDF2001", "matheus208"}, new String[]{"14NGiestas", "Aetheryll", "Andrew_px1", "Arthur_Mastriaga", "Bigode935", "Bionic64", "Chacal.Ex", "ChainedFreaK", "DAVICCOSTA", "DRACOnicus", "Derik", "DredgenVale", "ElefanteFome", "Helen0903", "JST", "Kotaroo05", "MadHorus", "Maria_Jo\u00e3o", "MarkusCoisa", "Matie", "Ninguem.EXE", "Ot\u00e1vioMoraes", "PingasOwner", "Piraldo", "Sr.BaconDelicioso", "Tete_Teli", "Tio_P_(Krampus)", "Zukkine", "ancientorange", "danypr23", "denis.gnl", "efverick", "gBiazon", "ismael.henriques12", "juniorsilve33", "mfcord", "nattlegal", "owenreilly", "phobos445", "rafazago", "renan408", "try31"}),
    FRENCH("fran\u00e7ais", "fr", Status.O_COMPLETE, new String[]{"Emether", "TheKappaDuWeb", "Weende_Bellet", "Xalofar", "canc42", "kultissim", "minikrob", "Lucasgstar"}, new String[]{"3raven", "Alsydis", "Anonyme48", "Axce", "Az_zahr", "Bastien72", "Basttee", "Coco_EC", "Dekadisk", "Draal", "Eragem", "Karnot", "L.E.V.", "Lama", "Le_Valla", "Leandre", "Louson", "Martin.Bellet", "Neopolitan", "NoGi", "Nyrnx", "Opidox", "Pandaman516", "Petit_Chat", "RomTheMareep", "RunningColours", "STPayoube", "Soeiz", "SpeagleZNT", "Teddywestside", "Tronche2Cake", "VRad", "Ygdrazil", "_nim_", "adamch", "adeb", "antoine9298", "clexanis", "eloiseflo", "fricht", "gdavid2", "go11um", "hydrasho", "jan.", "jazzzz", "levilbatard", "linterpreteur", "luffah", "maeltur70", "marmous", "mcbaba29000", "mluzarreta", "panopano", "solthaar", "speagle", "tkf_", "typhr80", "vavavoum", "whereisfelix", "willi3725", "zM_"}),
    GERMAN("deutsch", "de", Status.X_UNFINISH, new String[]{"Dallukas", "KrystalCroft", "Wuzzy", "Zap0", "apxwn", "bernhardreiter", "davedude"}, new String[]{"2711chrissi", "Abracadabra", "Anaklysmos", "Ceeee", "DarkPixel", "David.transifex", "EmilKevinManuel", "ErichME", "Faquarl", "JorahEtLabora", "LenzB", "MacMoff", "Micksha", "Niseko", "Ordoviz", "Sarius", "Shtynow", "SirEddi", "Sorpl3x", "SurmanPP", "SwissQ", "ThunfischGott", "Timo_S", "Topicranger", "azrdev", "carrageen", "dome.scheidler", "galactictrans", "gekko303", "jeinzi", "johannes.schobel", "karoshi42", "koryphea", "luciocarreras", "lukasghesse", "mklr", "niemand", "oragothen", "razzifazzi0", "spixi", "tanjay", "unbekannterTyp", "wunst"}),
    CHI_TRAD("\u7e41\u9ad4\u4e2d\u6587", "zh-hant", Status.__UNREVIEW, new String[]{"JZR", "Yichm", "p2635"}, new String[]{"DT227", "Fishbone", "Ken4Ro", "Lstron", "Relrin167", "Sotis425", "Zoe096423", "arnolam", "jackymaxj", "redbrow", "shiba", "\u5510\u5ef6\u8aed"}),
    JAPANESE("\u65e5\u672c\u8a9e", "ja", Status.O_COMPLETE, new String[]{"daingewuvzeevisiddfddd", "oz51199"}, new String[]{"Gosamaru", "NickZhrbin", "Otogiri", "Siraore_Rou", "amama", "grassedge", "kiyofumimanabe", "librada", "mocklike", "tomofumikitano"}),
    POLISH("polski", "pl", Status.X_UNFINISH, new String[]{"Daniel Wita\u0144ski", "Deksippos", "MrKukurykpl", "chronon", "kuadziw", "szymex73"}, new String[]{"Akmetari", "AntiTime", "Boguc", "Chasseur", "Ciechu", "Darden", "DarkKnightComes", "DogeseleQ", "GRan0000", "Hammil", "I256I", "KarixDaii", "Krn\u0105brnyOlaf", "Lufix", "MJedi", "MrCommander", "Odiihinia", "Ostsee0912", "Peperos", "RolsoN", "Scharnvirk", "Serpens13", "Tangens", "VasteelXolotl", "Voyteq", "Wiiiiiii", "bobas10", "bogumilg", "bvader95", "dusakus", "elchudy", "jajkoswinka", "michaub", "mikolka9144", "ozziezombie", "szczoteczka22", "taki1", "transportowiec96"}),
    ITALIAN("italiano", "it", Status.O_COMPLETE, new String[]{"MottledElm", "NeoAugustus", "bizzolino", "funnydwarf", "inkubo87"}, new String[]{"4est", "Danelix", "DaniMare", "Danzl", "Dj1234", "Eriliken", "Esse78", "Guiller124", "Hydr46605", "IoannesMaria", "LN_90", "Mat323", "Mister64", "Noostale", "PicchiSeba", "Tugamer89", "Tysal", "andrea049ita", "andreafaffo", "andrearubbino00", "angelica.caruso", "cantarini", "carinellialessandro31", "dmytro.tokayev", "lorenzofrosi05", "mamon68596", "mattiuw", "max1234ita", "maxifire32", "nessunluogo", "righi.a", "umby000", "unknown888", "valerio.bozzolan"}),
    TURKISH("t\u00fcrk\u00e7e", "tr", Status.__UNREVIEW, new String[]{"LokiofMillenium", "Mustafa.10", "T3kin5iZ", "emrebnk", "gorkem_y\u0131lmaz"}, new String[]{"AGORAAA", "AchernarPrime", "AcuriousPotato", "BurningDaylight", "ErenayDev", "Helgon", "Koga", "Mehmet_Emin_21", "MuratEfeYilmaz", "OzanAlkan", "TR_Muhittin", "Talha_0_0", "TheMBDsvs", "Yllcare", "YORGANSIZMTAV", "ahmetbakicakir", "akkaya.mustafa", "alikeremozfidan", "alpekin98", "barankrky", "denizakalin", "eraysall402", "erdemozdemir98", "hasantahsin160", "immortalsamuraicn", "kayikyaki", "kempilbey", "melezorus34", "mitux", "mustafadoslu", "ryuga", "superDpermn", "utkanozer13", "yasirckr85", "yukete"}),
    VIETNAMESE("ti\u1ebfng vi\u1ec7t", "vi", Status.O_COMPLETE, new String[]{"Chuseko", "The_Hood", "nguyenanhkhoapythus"}, new String[]{"BlueSheepAlgodoo", "Phuc2401", "SpaceMetropolis", "Teh_boi", "Threyja", "Toluu", "bruhwut", "buicongminh_t63", "deadlevel13", "duongfg250", "h4ndy_c4ndy", "hniV", "khangxyz3g", "ngolamaz3", "nkhhu", "vdgiapp", "vtvinh24"}),
    UKRANIAN("\u0443\u043a\u0440\u0430\u0457\u043d\u0441\u044c\u043a\u0430", "uk", Status.O_COMPLETE, new String[]{"Oster", "Snikewin", "zhushman00"}, new String[]{"AlexFenixUA", "Buster54", "Doodlinka", "Dotsent", "Lyttym", "MaxQuiet", "Mops", "Sadsaltan1", "TarasUA", "TheGuyBill", "Tomfire", "Volkov", "ZverWolf", "_bor_", "alexfenixva", "ddmaster3463", "filalex77", "holuydadko", "ingvarfed", "iu0v1", "jesternotricks", "lezzen", "myshokoleksander05", "oliolioxinfree", "qweez", "romanokurg", "so1der", "sterenkevicsasa", "vlisivka", "xojltoh", "yukete", "zhawty", "\u041c\u0430\u043b\u044c\u0432\u043e\u0447\u043a\u0430"}),
    INDONESIAN("indonesia", "in", Status.__UNREVIEW, new String[]{"RF_4R4F1_03", "rakapratama"}, new String[]{"An_Ironstone", "Flasherx", "INDRA_SYAHPUTRA", "Izulhaaq", "Karanh", "M.Bintang.K", "PineFirebloom", "QiuQiuQi", "Ruzz_Axleod", "Taka31", "ZakyM313", "ZangieF347", "aachunemiku", "anagakenny24", "aryasatya_arifien", "atmorojo", "di9526985", "esprogarap", "hatsunnimiku", "icebearwand", "kirimaja", "lupar21", "luthfidzaky_ldzy", "mkakhsan301", "nicoalvito", "noeldycreator", "oolek", "wisnugafur"}),
    CZECH("\u010de\u0161tina", "cs", Status.__UNREVIEW, new String[]{"16cnovotny", "ObisMike", "novotnyvaclav"}, new String[]{"AshenShugar", "Autony", "Block_Vader", "Buba237", "JStrange", "Nerdiniel", "Patrik123", "RealBrofessor", "Thorn_123", "chuckjirka", "emteckos2", "kristanka", "luhan.lukas"}),
    DUTCH("nederlands", "nl", Status.O_COMPLETE, new String[]{"AlbertBrand", "Mvharen"}, new String[]{"AvanLieshout", "Blokheck011", "Frankwert", "Gehenna", "Valco", "ZephyrZodiac", "link200023", "ojppe", "rmw", "th3f4llenh0rr0r"}),
    SWEDISH("svenska", "sv", Status.__UNREVIEW, new String[]{"leowitchhh", "yeager"}, new String[]{"KeyB", "Moistmemesneverlie", "antonaut", "dotMavriQ"}),
    HUNGARIAN("magyar", "hu", Status.O_COMPLETE, new String[]{"dorheim", "summoner001", "szalaik"}, new String[]{"Csanevox", "Navetelen", "acszoltan111", "balazsszalab", "clarovani", "dhialub", "nanometer", "nardomaa", "savarall", "szemetvodor"}),
    GREEK("\u03b5\u03bb\u03bb\u03b7\u03bd\u03b9\u03ba\u03ac", "el", Status.X_UNFINISH, new String[]{"Aeonius", "Saxy"}, new String[]{"DU_Clouds", "VasKyr", "YiorgosH", "fr3sh", "nikolaoskelirakis", "stefboi", "toumbo", "val.exe"}),
    BELARUSIAN("\u0431\u0435\u043b\u0430\u0440\u0443\u0441\u043a\u0430\u044f", "be", Status.X_UNFINISH, new String[]{"AprilRain(Vadzim Navuma\u00fb)"}, new String[]{"4ebotar", "Loentrin"}),
    ESPERANTO("esperanto", "eo", Status.X_UNFINISH, new String[]{"Verdulo"}, new String[]{"Raizin", "Rwelean", "kameluloj"});

    private String name;
    private String code;
    private Status status;
    private String[] reviewers;
    private String[] translators;

    private Languages(String name, String code, Status status, String[] reviewers, String[] translators) {
        this.name = name;
        this.code = code;
        this.status = status;
        this.reviewers = reviewers;
        this.translators = translators;
    }

    public String nativeName() {
        return this.name;
    }

    public String code() {
        return this.code;
    }

    public Status status() {
        return this.status;
    }

    public String[] reviewers() {
        if (this.reviewers == null) {
            return new String[0];
        }
        return (String[])this.reviewers.clone();
    }

    public String[] translators() {
        if (this.translators == null) {
            return new String[0];
        }
        return (String[])this.translators.clone();
    }

    public static Languages matchLocale(Locale locale) {
        if (locale.getLanguage().equals("zh") && locale.toString().contains("Hant")) {
            return CHI_TRAD;
        }
        return Languages.matchCode(locale.getLanguage());
    }

    public static Languages matchCode(String code) {
        for (Languages lang : Languages.values()) {
            if (!lang.code().equals(code)) continue;
            return lang;
        }
        return ENGLISH;
    }

    public static enum Status {
        X_UNFINISH,
        __UNREVIEW,
        O_COMPLETE;

    }
}

