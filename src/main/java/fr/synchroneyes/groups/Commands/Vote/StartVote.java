package fr.synchroneyes.groups.Commands.Vote;

import fr.synchroneyes.groups.Commands.CommandTemplate;
import fr.synchroneyes.groups.Core.Groupe;
import fr.synchroneyes.mineral.Core.Game.Game;
import fr.synchroneyes.mineral.Translation.Lang;
import fr.synchroneyes.mineral.mineralcontest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartVote extends CommandTemplate {

    public StartVote() {
        this.accessCommande.add(PLAYER_COMMAND);
        this.accessCommande.add(GROUP_REQUIRED);
        this.accessCommande.add(GROUP_ADMIN);
    }

    @Override
    public boolean performCommand(CommandSender commandSender, String command, String[] args) {
        Player joueur = (Player) commandSender;
        Groupe playerGroup = mineralcontest.getPlayerGroupe(joueur);

        Game groupGame = playerGroup.getGame();
        if (groupGame.isGameStarted() || groupGame.isGamePaused() || groupGame.isPreGame()) {
            commandSender.sendMessage(mineralcontest.prefixErreur + Lang.game_already_started.toString());
            return false;
        }


        playerGroup.initVoteMap();


        if (!joueur.getWorld().equals(mineralcontest.plugin.pluginWorld)) {
            commandSender.sendMessage(mineralcontest.prefixErreur + Lang.error_command_can_only_be_used_hub_world.toString());
            return false;
        }


        return false;
    }

    @Override
    public String getCommand() {
        return "startvote";
    }


    @Override
    public String getDescription() {
        return "Permet de lancer le vote";
    }

    @Override
    public String getPermissionRequise() {
        return "";
    }
}
