package au.com.mineauz.groupchat;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GroupManager implements Listener
{
	private HashMap<Player, Group> mGroupMapping;
	private HashSet<Group> mAllGroups;
	
	private HashMap<Player, Invitation> mInvitations;
	
	private HashMap<Player, Boolean> mIsPlayerModal;
	
	private int cInvitationExpiryTime = 20000;
	
	public GroupManager()
	{
		mAllGroups = new HashSet<Group>();
		mGroupMapping = new HashMap<Player, Group>();
		mIsPlayerModal = new HashMap<Player, Boolean>();
		
		mInvitations = new HashMap<Player, Invitation>();
	}
	
	public synchronized Group getGroup(Player player)
	{
		return mGroupMapping.get(player);
	}
	
	public synchronized boolean isInGroupChat(Player player)
	{
		return mGroupMapping.containsKey(player);
	}
	
	public synchronized Group getOrMakeGroup(Player player)
	{
		Group group = mGroupMapping.get(player);
		
		if(group == null)
		{
			group = new Group(this, player);
			mGroupMapping.put(player, group);
			mAllGroups.add(group);
		}
		
		return group;
	}
	
	/**
	 * Sends an invite to the specified player to join the specified group
	 */
	public synchronized void inviteToGroup(Group group, Player player)
	{
		Invitation invitation = new Invitation();
		invitation.group = group;
		invitation.player = player;
		invitation.sentTime = System.currentTimeMillis();
		invitation.invite = true;
		
		mInvitations.put(player, invitation);
		
		player.sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.WHITE + "You have been invited to a group chat with " + group.getOwner().getName() + (group.getMembers().size() > 1 ? " and others" : "") + ". Use " + ChatColor.YELLOW + "/groupchat accept" + ChatColor.WHITE + " to join. Use " + ChatColor.YELLOW + "/groupchat deny" + ChatColor.WHITE + " to deny their request.");
	}

	/**
	 * Sends a request to the owner of the group to allow the specified player to join 
	 */
	public synchronized void requestToJoin(Group group, Player player)
	{
		Invitation invitation = new Invitation();
		invitation.group = group;
		invitation.player = player;
		invitation.sentTime = System.currentTimeMillis();
		invitation.invite = false;
		
		mInvitations.put(group.getOwner(), invitation);
		
		group.getOwner().sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.WHITE + player.getName() + " has requested to join your group chat. Use " + ChatColor.YELLOW + "/groupchat accept" + ChatColor.WHITE + " to allow them to join. Use " + ChatColor.YELLOW + "/groupchat deny" + ChatColor.WHITE + " to deny their request.");
	}

	public synchronized void acceptRequest(Player player)
	{
		Invitation invitation = mInvitations.remove(player);
		
		if(invitation == null)
			return;
		
		if(System.currentTimeMillis() - invitation.sentTime > cInvitationExpiryTime || invitation.group.getMembers().isEmpty())
		{
			if(invitation.invite)
				player.sendMessage(ChatColor.RED + "Your invitation has expired.");
			else
				player.sendMessage(ChatColor.RED + "Their request has expired.");
			
			return;
		}
		
		invitation.group.addMember(invitation.player);
	}
	
	public synchronized void denyRequest(Player player)
	{
		Invitation invitation = mInvitations.remove(player);
		
		if(invitation == null)
			return;
		
		if(System.currentTimeMillis() - invitation.sentTime > cInvitationExpiryTime || invitation.group.getMembers().isEmpty())
		{
			if(invitation.invite)
				player.sendMessage(ChatColor.RED + "Your invitation has expired.");
			else
				player.sendMessage(ChatColor.RED + "Their request has expired.");
			
			return;
		}
		
		if(invitation.invite)
			invitation.group.getOwner().sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.GOLD + player.getName() + " rejected your invitation.");
		else
			invitation.player.sendMessage(ChatColor.RED + "[GroupChat] " + ChatColor.GOLD + "Your request was rejected.");
	}
	
	public boolean isPlayerModal(Player player)
	{
		Boolean val = mIsPlayerModal.get(player);
		if(val == null)
			return true;
		
		return val;
	}
	
	public void setPlayerModal(Player player, boolean modal)
	{
		mIsPlayerModal.put(player, modal);
	}
	
	public Set<Group> getAllGroups()
	{
		return Collections.unmodifiableSet(mAllGroups);
	}
	
	/**
	 * Internal function. Group must be empty when you call this
	 */
	synchronized void removeGroup(Group group)
	{
		mAllGroups.remove(group);
	}
	
	/**
	 * Internal function.
	 */
	synchronized void addMapping(Player player, Group group)
	{
		mGroupMapping.put(player, group);
	}
	
	/**
	 * Internal function.
	 */
	synchronized void removeMapping(Player player)
	{
		mGroupMapping.remove(player);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private synchronized void onChatMessage(AsyncPlayerChatEvent event)
	{
		Group group = getGroup(event.getPlayer());
		
		if(group != null)
		{
			if(event.getMessage().startsWith("!")) // Normal chat
			{
				event.setMessage(event.getMessage().substring(1).trim());
				if(event.getMessage().isEmpty())
					event.setCancelled(true);
				return;
			}
			
			Set<Player> recipients = event.getRecipients();
			recipients.clear();
			recipients.addAll(group.getMembers());
			
			AsyncPlayerGroupChatEvent groupEvent = new AsyncPlayerGroupChatEvent(event.getPlayer(), event.getMessage(), event.getFormat(), group);
			Bukkit.getPluginManager().callEvent(groupEvent);
			if(groupEvent.isCancelled())
				event.setCancelled(true);
		}
		else
		{
			Iterator<Player> it = event.getRecipients().iterator();
			while(it.hasNext())
			{
				Player player = it.next();
				
				if(isInGroupChat(player) && isPlayerModal(player))
					it.remove();
			}
		}
	}
	
	private void handleLeave(Player player)
	{
		Group group = getGroup(player);
		if(group != null)
			group.removeMember(player);
		
		mInvitations.remove(player);
		mIsPlayerModal.remove(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	private synchronized void onPlayerLeave(PlayerQuitEvent event)
	{
		handleLeave(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private synchronized void onPlayerLeave(PlayerKickEvent event)
	{
		handleLeave(event.getPlayer());
	}
}
