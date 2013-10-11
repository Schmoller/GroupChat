package au.com.mineauz.groupchat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Group
{
	private Player mOwner;
	private HashSet<Player> mMembers;
	private GroupManager mManager;
	private String mName;
	
	public Group(GroupManager manager, Player owner)
	{
		mMembers = new HashSet<Player>();
		mManager = manager;
		mOwner = owner;
		mMembers.add(owner);
	}
	public void addMember(Player player)
	{
		mMembers.add(player);
		mManager.addMapping(player, this);
	
		player.sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.WHITE + "You have joined group chat. Use " + ChatColor.YELLOW + "/groupchat leave " + ChatColor.WHITE + " to leave group chat.");
		sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.WHITE + player.getName() + " has joined group chat.", player);
	}
	
	public void removeMember(Player player)
	{
		mMembers.remove(player);
		mManager.removeMapping(player);
		
		if(mOwner.equals(player))
		{
			for(Player member : mMembers)
			{
				mOwner = member;
				mOwner.sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.WHITE + "You are now the owner of this chat group. You can invite and kick people.");
				break;
			}
		}
		
		sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.WHITE + player.getName() + " has left group chat.", player);
		
		if(mMembers.isEmpty())
			mManager.removeGroup(this);
	}
	
	public Player getOwner()
	{
		return mOwner;
	}
	
	public void setName(String name)
	{
		mName = name;
	}
	
	public String getName()
	{
		return mName;
	}

	public Set<Player> getMembers()
	{
		return Collections.unmodifiableSet(mMembers);
	}
	
	public void sendMessage(String message)
	{
		for(Player member : mMembers)
			member.sendMessage(message);
	}
	
	public void sendMessage(String message, Player except)
	{
		for(Player member : mMembers)
		{
			if(member.equals(except))
				continue;
			
			member.sendMessage(message);
		}
	}
}
