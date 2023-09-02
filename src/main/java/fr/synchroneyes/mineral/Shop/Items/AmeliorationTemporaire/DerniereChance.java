package fr.synchroneyes.mineral.Shop.Items.AmeliorationTemporaire;

import fr.synchroneyes.mineral.Shop.Items.Abstract.ConsumableItem;
import fr.synchroneyes.mineral.Shop.ShopManager;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;

public class DerniereChance extends ConsumableItem {
    @Override
    public String getNomItem() {
        return Lang.shopitem_martyr_title.toString();
    }

    @Override
    public String[] getDescriptionItem() {
        return new String[]{Lang.shopitem_martyr_desc1.toString(), Lang.shopitem_martyr_desc2.toString()};
    }

    @Override
    public Material getItemMaterial() {
        return Material.TNT;
    }

    @Override
    public boolean isEnabledOnRespawn() {
        return false;
    }

    @Override
    public boolean isEnabledOnPurchase() {
        return false;
    }

    @Override
    public int getNombreUtilisations() {
        return 1;
    }

    @Override
    public void onItemUse() {
        Location playerDeathLocation = this.joueur.getLocation();

        int rayon_block_tnt = 1;
        int temps_avant_explosion = 3;

        int defaultX, defaultY, defaultZ;
        defaultX = playerDeathLocation.getBlockX();
        defaultY = playerDeathLocation.getBlockY();
        defaultZ = playerDeathLocation.getBlockZ();


        for (int x = defaultX - rayon_block_tnt; x < defaultX + rayon_block_tnt; ++x)
            for (int z = defaultZ - rayon_block_tnt; z < defaultZ + rayon_block_tnt; ++z) {
                TNTPrimed primed = playerDeathLocation.getWorld().spawn(new Location(playerDeathLocation.getWorld(), x, defaultY, z), TNTPrimed.class);
                primed.setFuseTicks(20 * temps_avant_explosion);
            }


        joueur.sendMessage(mineralcontest.prefixPrive + Lang.shopitem_martyr_onitemuse.toString());

    }

    @Override
    public int getPrice() {
        return ShopManager.getBonusPriceFromName("self_martyr");

    }


    @Override
    public boolean isEnabledOnDeathByAnotherPlayer() {
        return true;
    }
}
