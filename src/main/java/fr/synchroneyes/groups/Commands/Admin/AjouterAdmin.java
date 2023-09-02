package fr.synchroneyes.groups.Commands.Admin;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AjouterAdmin extends CommandTemplate {

    public AjouterAdmin() {
        super();

        addArgument("Nom du joueur", true);

        this.accessCommande.add(PLAYER_COMMAND);
        this.accessCommande.add(GROUP_REQUIRED);
        this.accessCommande.add(GROUP_CREATOR);

        constructArguments();

    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player) commandSender;

        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);

        Player joueurAMettreAdmin = Bukkit.getPlayer(args[0]);
        if (joueurAMettreAdmin == null) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_no_player_with_this_name.toString());
            return false;
        }

        if (mineralcontest.getPlayerGroupe(joueurAMettreAdmin) == null || !playerGroup.containsPlayer(joueurAMettreAdmin)) {
            joueur.sendMessage(mineralcontest.prefixErreur + Lang.error_player_not_in_our_group.toString());
            return false;
        }

        playerGroup.addAdmin(joueurAMettreAdmin);
        return false;
    }

    @Override
    public String getCommand() {
        return "ajouteradmin";
    }


    @Override
    public String getDescription() {
        return "Mets un joueur du groupe en admin";
    }

    @Override
    public String getPermissionRequise() {
        return null;
    }
}
