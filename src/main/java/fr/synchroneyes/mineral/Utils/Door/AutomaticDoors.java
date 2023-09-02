package fr.synchroneyes.mineral.Utils.Door;

import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Teams.Equipe;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class AutomaticDoors {

    // Le bloc du centre


    private Groupe groupe;

    // On a une Liste de block
    private LinkedList<DisplayBlock> porte;

    LinkedList<Player> playerNearDoor;

    private boolean estOuvert = false;


    // Prend un bloc, et un rayon
    public AutomaticDoors(Equipe equipe, Groupe g) {
        this.porte = new LinkedList<DisplayBlock>();
        this.playerNearDoor = new LinkedList<Player>();
        this.groupe = g;
    }

    public void clear() {
        this.forceCloseDoor();
        this.porte.clear();
        this.playerNearDoor.clear();
    }

    // On initialise la pile de blocs
    public boolean addToDoor(Block b) {
        // Pour chaque bloc de la porte
        boolean ajouter = false;
        if (porte.size() == 0) {
            porte.add(new DisplayBlock(b));
            if (mineralcontest.debug)
                mineralcontest.broadcastMessage(ChatColor.GREEN + "+ Le bloc selectionné a été ajouté", groupe);
        } else {
            for (DisplayBlock db : porte) {
                if (db.getBlock().equals(b)) {
                    porte.remove(db);
                    if (mineralcontest.debug)
                        mineralcontest.broadcastMessage(ChatColor.YELLOW + "- Le bloc selectionné a été supprimé", groupe);
                } else {
                    ajouter = true;
                }
            }
            if (ajouter) {
                porte.add(new DisplayBlock(b));
                if (mineralcontest.debug)
                    mineralcontest.broadcastMessage(ChatColor.GREEN + "+ Le bloc selectionné a été ajouté à la porte", groupe);
                return true;
            }

            return true;
        }

        return true;


    }

    // On initialise la pile de blocs
    public void openDoor() {
        // Pour chaque bloc de la porte
        if (playerNearDoor.size() > 0) {
            // Pour chaque bloc de la porte

            for (DisplayBlock db : porte) {
                db.hide();
            }
            this.estOuvert = true;
        }
    }


    public void forceCloseDoor() {
        playerNearDoor.clear();
        closeDoor();
    }

    public void closeDoor() {

        // Si on a personne pres de la porte
        if (playerNearDoor.size() == 0) {
            // Pour chaque bloc de la porte

            for (DisplayBlock db : porte) {
                db.display();
            }
            this.estOuvert = false;
        }

    }

    public LinkedList<DisplayBlock> getPorte() {
        return porte;
    }


    public void playerIsNearDoor(Player joueur) {
        if (!this.playerNearDoor.contains(joueur) && !mineralcontest.getPlayerGame(joueur).getArene().getDeathZone().isPlayerDead(joueur)) {
            this.playerNearDoor.add(joueur);
            openDoor();
        }
    }

    public void playerIsNotNearDoor(Player joueur) {
        if (this.playerNearDoor.contains(joueur)) {
            this.playerNearDoor.remove(joueur);
            closeDoor();
        }
    }
}
