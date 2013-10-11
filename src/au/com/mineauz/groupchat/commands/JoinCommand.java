package au.com.mineauz.groupchat.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.groupchat.Group;
import au.com.mineauz.groupchat.GroupChat;
import au.com.mineauz.groupchat.GroupManager;

public class JoinCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "join";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "groupchat.join";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] {label + ChatColor.GREEN + " [player]"};
	}

	@Override
	public String getDescription()
	{
		return "Starts a group chat, or if a player is specified requests to join their chat";
	}

	@Override
	public boolean canBeConsole()
	{
		return false;
	}

	@Override
	public boolean canBeCommandBlock()
	{
		return false;
	}

	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length > 1)
			return false;
		
		GroupManager manager = GroupChat.instance.getManager();
		
		if(args.length == 1)
		{
			Player other = Bukkit.getPlayer(args[0]);
			if(other == null)
			{
				sender.sendMessage(ChatColor.RED + "[GroupChat] Cannot find player " + ChatColor.YELLOW + args[0]);
				return true;
			}
			
			if(manager.isInGroupChat(other))
			{
				if(sender.hasPermission("groupchat.join.force"))
					manager.getGroup(other).addMember((Player)sender);
				else
				{
					Group group = manager.getGroup(other);
					manager.requestToJoin(group, (Player)sender);
					sender.sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.WHITE + "You have sent a request to join " + other.getName() + " in their group chat.");
				}
			}
			else
				sender.sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.YELLOW + args[0] + ChatColor.RED + " is not in a group chat.");
		}
		else
		{
			if(manager.isInGroupChat((Player)sender))
			{
				sender.sendMessage(ChatColor.RED + "[GroupChat] You are already in a group chat. Use /groupchat leave to exit first");
				return true;
			}
			
			manager.getOrMakeGroup((Player)sender);
			
			sender.sendMessage(ChatColor.RED + "[GroupChat]" + ChatColor.WHITE + " You have started a group chat.");
			sender.sendMessage("Use " + ChatColor.YELLOW + "/groupchat invite <player>" + ChatColor.WHITE + " to invite other players");
		}
			
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		ArrayList<String> players = new ArrayList<String>();
		if(args.length == 1)
		{
			for(Player player : Bukkit.matchPlayer(args[0]))
				players.add(player.getName());
			
			return players;
		}
		return null;
	}

}
