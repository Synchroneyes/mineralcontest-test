package fr.synchroneyes.mineral.Core.Arena.ArenaChestContent;

import fr.synchroneyes.file_manager.FileList;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Settings.GameSettings;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.Utils.ErrorReporting.Error;
import fr.synchroneyes.mineral.Utils.Range;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Cette classe gère les objets présents dans un coffre d'arène
 * Il est possible de préciser un fichier contenant les items, si aucun n'est précisé, alors on prendra celui par défault
 */
public class ArenaChestContentGenerator {
    private ArrayList<ArenaChestItem> items;
    private boolean initialized = false;
    private Groupe groupe;

    public ArenaChestContentGenerator(Groupe groupe) {
        this.items = new ArrayList<>();
        this.groupe = groupe;
    }

    /**
     * Fonction appelée pour initialiser le coffre d'arène
     *
     * @param fichier
     */
    public void initialize(File fichier) throws Exception {
        if (fichier == null) initializeFromDefaultFile();
        else initializeFromFile(fichier);
    }

    /**
     * Retourne un inventaire contenant les objets initialisés
     *
     * @return Inventory
     */
    public Inventory generateInventory() throws Exception {
        if (!initialized) initializeFromDefaultFile();

        // Initialisation des variables
        GameSettings settings = groupe.getParametresPartie();
        int minItem = 1;
        int maxItem = 10;
        try {
            minItem = settings.getCVAR("min_item_in_chest").getValeurNumerique();
            maxItem = settings.getCVAR("max_item_in_chest").getValeurNumerique();
        } catch (Exception e) {
            Error.Report(e, groupe.getGame());
        }

        // On génère un tableau de "range"
        int tailleTableau = items.size();
        int minProba = 0;
        Range[] tableauProba = new Range[tailleTableau];

        // On parcours notre liste d'item pour instancié chaque valeur du tableau "tableauProba"
        for (int i = 0; i < tailleTableau; ++i) {
            ArenaChestItem item = items.get(i);
            tableauProba[i] = new Range(item.getItemMaterial(), minProba, minProba + item.getItemProbability());
            minProba = minProba + item.getItemProbability();
        }

        // On instancie l'inventaire dans lequel stocker les items
        Inventory inventaire = Bukkit.createInventory(null, 27, Lang.arena_chest_title.toString());

        Random random = new Random();
        // On génère un nombre entre maxItem et minItem
        int numeroGenere = random.nextInt((maxItem - minItem) - 1) + minItem;

        // Maintenant, on génère "numeroGenere" items
        for (int i = 0; i < numeroGenere; ++i) {
            // On génère un nombre entre 0 et la proba du dernier item instancié
            int probabiliteGenere = random.nextInt(minProba - 1);
            // On récupère un item correspondant à la proba générée
            Material itemMaterial = Range.getInsideRange(tableauProba, probabiliteGenere);

            // On crée l'item
            ItemStack item = new ItemStack(itemMaterial, 1);

            // On l'ajoute à l'inventaire
            inventaire.addItem(item);
        }

        // et on retourne cet inventaire
        return inventaire;
    }


    /**
     * Retourne l'inventaire d'items généré pour les largages
     *
     * @param minItem Le nombre d'items minimum
     * @param maxItem Le nombre d'item max
     * @return
     * @throws Exception
     */
    public Inventory generateAirDropInventory(int minItem, int maxItem) throws Exception {
        if (!initialized) initializeFromDefaultFile();


        // On génère un tableau de "range"
        int tailleTableau = items.size();
        int minProba = 0;
        Range[] tableauProba = new Range[tailleTableau];

        // On parcours notre liste d'item pour instancié chaque valeur du tableau "tableauProba"
        for (int i = 0; i < tailleTableau; ++i) {
            ArenaChestItem item = items.get(i);
            tableauProba[i] = new Range(item.getItemMaterial(), minProba, minProba + item.getItemProbability());
            minProba = minProba + item.getItemProbability();
        }

        // On instancie l'inventaire dans lequel stocker les items
        Inventory inventaire = Bukkit.createInventory(null, 27, Lang.arena_chest_title.toString());

        Random random = new Random();
        // On génère un nombre entre maxItem et minItem
        int numeroGenere = random.nextInt((maxItem - minItem) - 1) + minItem;

        // Maintenant, on génère "numeroGenere" items
        for (int i = 0; i < numeroGenere; ++i) {
            // On génère un nombre entre 0 et la proba du dernier item instancié
            int probabiliteGenere = random.nextInt(minProba - 1);
            // On récupère un item correspondant à la proba générée
            Material itemMaterial = Range.getInsideRange(tableauProba, probabiliteGenere);

            // On crée l'item
            ItemStack item = new ItemStack(itemMaterial, 1);

            // On l'ajoute à l'inventaire
            inventaire.addItem(item);
        }

        // et on retourne cet inventaire
        return inventaire;
    }

    /**
     * Initialisation des items d'un coffre à partir d'un fichier passé en paramètre
     *
     * @param fichier
     */
    private void initializeFromFile(File fichier) throws Exception {
        if (fichier == null || !fichier.exists()) {
            initializeFromDefaultFile();
            return;
        }

        // On lit le fichier
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(fichier);
        // Et on applique la fonction permettant de lire le contenu
        initializeFromSection(yamlConfiguration.getConfigurationSection("chest_content"));
    }


    /**
     * Cette fonction lit le fichier à partir d'une section donnée, ici: chest_content
     *
     * @param section
     * @throws Exception
     */
    private void initializeFromSection(ConfigurationSection section) throws Exception {
        if (section == null)
            throw new Exception("La section \"chest_content\" n'existe pas dans le fichier de configuration donné");
        for (String item_id : section.getKeys(false)) {
            ArenaChestItem item = new ArenaChestItem();
            item.setItemMaterial((String) section.get(item_id + ".name"));
            item.setItemProbability(Integer.parseInt(section.get(item_id + ".probability").toString()));

            items.add(item);
            if (mineralcontest.debug)
                Bukkit.getLogger().severe("ArenaChestContentGenerator: Added " + item.getItemMaterial().toString() + " with=> " + item.getItemProbability());
        }

        initialized = true;
    }

    /**
     * Initialisation des items d'un coffre à partir du fichier présent par défaut
     */
    private void initializeFromDefaultFile() throws Exception {
        // On charge le fichier
        File fichier = new File(mineralcontest.plugin.getDataFolder(), FileList.Config_default_arena_chest.toString());


        if (!fichier.exists()) {
            throw new Exception("Impossible de charger le fichier de contenu du coffre d'arène par défaut");
        }

        // On lit le fichier
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(fichier);
        // Et on applique la fonction permettant de lire le contenu
        initializeFromSection(yamlConfiguration.getConfigurationSection("chest_content"));
    }
}
