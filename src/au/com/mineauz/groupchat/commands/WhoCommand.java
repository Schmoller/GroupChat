package au.com.mineauz.groupchat.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.groupchat.Group;
import au.com.mineauz.groupchat.GroupChat;
import au.com.mineauz.groupchat.GroupManager;

public class WhoCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "who";
	}

	@Override
	public String[] getAliases()
	{
		return new String[] {"list"};
	}

	@Override
	public String getPermission()
	{
		return "groupchat.who";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] {label};
	}

	@Override
	public String getDescription()
	{
		return "Checks who is in chat with you";
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
	
	private String makePlayerList(Group group)
	{
		String list = "";
		boolean first = true;
		for(Player player : group.getMembers())
		{
			if(!list.isEmpty())
				list += ", ";
			
			if(first)
				list += ChatColor.WHITE;
			else
				list += ChatColor.GRAY;
			
			first = !first;
			
			list += player.getName();
		}
		
		return list;
	}

	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length != 0)
			return false;
		
		GroupManager manager = GroupChat.instance.getManager();
		Group group = manager.getGroup((Player)sender);

		if(!sender.hasPermission("groupchat.who.others"))
		{
			if(group == null)
				sender.sendMessage(ChatColor.RED + "You are not in group chat.");
			else
			{
				sender.sendMessage(ChatColor.GRAY + "There are " + ChatColor.YELLOW + group.getMembers().size() + ChatColor.GRAY + " players in this chat.");
				sender.sendMessage(makePlayerList(group));
			}
		}
		else
		{
			if(group != null)
			{
				sender.sendMessage(ChatColor.GRAY + "There are " + ChatColor.YELLOW + group.getMembers().size() + ChatColor.GRAY + " players in this chat.");
				sender.sendMessage(makePlayerList(group));
			}
			else if(manager.getAllGroups().isEmpty())
			{
				sender.sendMessage(ChatColor.GRAY + "Nobody is in group chat.");
				return true;
			}
			
			
			for(Group other : manager.getAllGroups())
			{
				if(other == group)
					continue;
				
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GRAY + "Group: " + ChatColor.YELLOW + other.getOwner().getName() + ChatColor.GRAY + "(" + other.getMembers().size() + ")");
				sender.sendMessage(makePlayerList(other));
			}
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label,
			String[] args )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
